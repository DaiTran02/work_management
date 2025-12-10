package ws.core.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.EnumUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import ws.core.advice.DuplicateKeyExceptionAdvice;
import ws.core.advice.NotAcceptableExceptionAdvice;
import ws.core.advice.NotFoundElementExceptionAdvice;
import ws.core.enums.DataAction;
import ws.core.enums.EventCalendarAttandUserOrganizationType;
import ws.core.enums.EventCalendarType;
import ws.core.enums.EventCalendarUserStatus;
import ws.core.enums.EventCalendarUserType;
import ws.core.enums.NotificationAction;
import ws.core.enums.NotificationObject;
import ws.core.enums.NotificationScope;
import ws.core.enums.NotificationType;
import ws.core.enums.PermissionKey;
import ws.core.model.EventCalendar;
import ws.core.model.EventHistory;
import ws.core.model.User;
import ws.core.model.embeded.EventCalendarUserAttend;
import ws.core.model.filter.EventCalendarFilter;
import ws.core.model.filter.UserFilter;
import ws.core.model.request.ReqEventCalendarCreate;
import ws.core.model.request.ReqEventCalendarUpdate;
import ws.core.model.request.ReqEventCalendarUserDoConfirmed;
import ws.core.model.request.ReqEventCalendarUserDoDelegacy;
import ws.core.model.request.ReqNotificationCreate;
import ws.core.model.request.embeded.ReqEventCalendarUserAttand;
import ws.core.respository.EventCalendarRepository;
import ws.core.respository.EventCalendarRepositoryCustom;
import ws.core.respository.EventHistoryRepository;
import ws.core.services.EventCalendarService;
import ws.core.services.NotificationService;
import ws.core.services.PermissionService;
import ws.core.services.UserService;
import ws.core.util.CommonUtil;
import ws.core.util.DateTimeUtil;

@Service
public class EventCalendarServiceImpl implements EventCalendarService{

	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	protected EventCalendarRepository eventCalendarRepository;
	
	@Autowired
	protected EventCalendarRepositoryCustom eventCalendarRepositoryCustom;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PermissionService permissionService;
	
	@Autowired
	private EventHistoryRepository eventHistoryRepository;
	
	@Override
	public long countAll(EventCalendarFilter eventCalendarFilter) {
		return eventCalendarRepositoryCustom.countAll(eventCalendarFilter);
	}

	@Override
	public List<EventCalendar> findAll(EventCalendarFilter eventCalendarFilter) {
		return eventCalendarRepositoryCustom.findAll(eventCalendarFilter);
	}

	@Override
	public Optional<EventCalendar> findOne(EventCalendarFilter eventCalendarFilter) {
		return eventCalendarRepositoryCustom.findOne(eventCalendarFilter);
	}

	@Override
	public EventCalendar getOne(EventCalendarFilter eventCalendarFilter) {
		Optional<EventCalendar> findOne = findOne(eventCalendarFilter);
		if(findOne.isPresent()) {
			return findOne.get();
		}
		throw new NotFoundElementExceptionAdvice("eventCalendar không tồn tại trong hệ thống");
	}

	@Override
	public Optional<EventCalendar> findById(String id) {
		return eventCalendarRepository.findById(new ObjectId(id));
	}

	@Override
	public EventCalendar getById(String id) {
		Optional<EventCalendar> findById = findById(id);
		if(findById.isPresent()) {
			return findById.get();
		}
		throw new NotFoundElementExceptionAdvice("eventCalendar ["+id+"] không tồn tại trong hệ thống");
	}

	@Override
	public EventCalendar deleteById(String id, User user) {
		EventCalendar eventCalendar=getById(id);
		if((eventCalendar.getType().equals(EventCalendarType.personal.getKey()) && eventCalendar.getCreator().getOrganizationUserId().equals(user.getId()))
				|| (eventCalendar.getType().equals(EventCalendarType.organization.getKey()) && permissionService.hasPermission(eventCalendar.getCreator().getOrganizationId(), user.getId(), PermissionKey.quanlylichcongtac.getKey()))) {
			
			if(!eventCalendar.isTrash()) {
				eventCalendar.setTrash(true);
				eventCalendar.setActor(user.toActor());
				eventCalendar = save(eventCalendar, DataAction.delele, user);
				
				/* Thông báo */
				try {
					ReqNotificationCreate reqNotificationCreate=new ReqNotificationCreate();
					reqNotificationCreate.setAction(NotificationAction.lich_huy_su_kien);
					reqNotificationCreate.setTitle(NotificationAction.lich_huy_su_kien.getTitle());
					reqNotificationCreate.setContent(eventCalendar.getContent());
					reqNotificationCreate.setType(NotificationType.info);
					reqNotificationCreate.setObject(NotificationObject.eventCalendar);
					reqNotificationCreate.setObjectId(eventCalendar.getId());
					reqNotificationCreate.setActionUrl(null);
					reqNotificationCreate.setCreator(null);
	
					notifyCancelEventCalendar(reqNotificationCreate, eventCalendar);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return eventCalendar;
			}
			throw new NotAcceptableExceptionAdvice("eventCalendar ["+id+"] đã được xóa trước đó");
		}
		throw new NotAcceptableExceptionAdvice("Không được phép, vì không có quyền");
	}

	@Override
	public EventCalendar create(ReqEventCalendarCreate reqEventCalendarCreate, User user) {
		if(reqEventCalendarCreate.getType().equals(EventCalendarType.personal.getKey())  || (reqEventCalendarCreate.getType().equals(EventCalendarType.organization.getKey()) && permissionService.hasPermission(reqEventCalendarCreate.getCreator().getOrganizationId(), reqEventCalendarCreate.getCreator().getOrganizationUserId(), PermissionKey.quanlylichcongtac.getKey()))) {
			EventCalendar eventCalendar = new EventCalendar();
			eventCalendar.setType(reqEventCalendarCreate.getType());
			eventCalendar.setPeriod(reqEventCalendarCreate.getPeriod());
			eventCalendar.setFrom(new Date(reqEventCalendarCreate.getFrom()));
			eventCalendar.setTo(new Date(reqEventCalendarCreate.getTo()));
			eventCalendar.setContent(reqEventCalendarCreate.getContent());
			eventCalendar.setNotes(reqEventCalendarCreate.getNotes());
			eventCalendar.setColor(reqEventCalendarCreate.getColor());
			
			/* Host */
			if(reqEventCalendarCreate.getHosts().size()>0) {
				List<ReqEventCalendarUserAttand> hosts=remixUserOrganization(reqEventCalendarCreate.getHosts());
				if(hosts.size()>0) {
					reqEventCalendarCreate.setHosts(hosts);
					//eventCalendar.setHosts(hosts.stream().map(e->e.toUserOrganizationEventCalendar()).collect(Collectors.toList()));
				}else {
					reqEventCalendarCreate.setHosts(Arrays.asList());
				}
			}
			
			/* AttendeesRequired */
			if(reqEventCalendarCreate.getAttendeesRequired().size()>0) {
				List<ReqEventCalendarUserAttand> attendeesRequired=remixUserOrganization(reqEventCalendarCreate.getAttendeesRequired());
				if(attendeesRequired.size()>0) {
					reqEventCalendarCreate.setAttendeesRequired(attendeesRequired);
					//eventCalendar.setAttendeesRequired(attendeesRequired.stream().map(e->e.toUserOrganizationEventCalendar()).collect(Collectors.toList()));
				}else {
					reqEventCalendarCreate.setAttendeesRequired(Arrays.asList());
				}
			}
			
			/* AttendeesNoRequired */
			if(reqEventCalendarCreate.getAttendeesNoRequired().size()>0) {
				List<ReqEventCalendarUserAttand> attendeesNoRequired=remixUserOrganization(reqEventCalendarCreate.getAttendeesNoRequired());
				if(attendeesNoRequired.size()>0) {
					reqEventCalendarCreate.setAttendeesNoRequired(attendeesNoRequired);
					//eventCalendar.setAttendeesNoRequired(attendeesNoRequired.stream().map(e->e.toUserOrganizationEventCalendar()).collect(Collectors.toList()));
				}else {
					reqEventCalendarCreate.setAttendeesNoRequired(Arrays.asList());
				}
			}
			
			/* Prepareres */
			if(reqEventCalendarCreate.getPrepareres().size()>0) {
				List<ReqEventCalendarUserAttand> prepareres=remixUserOrganization(reqEventCalendarCreate.getAttendeesNoRequired());
				if(prepareres.size()>0) {
					reqEventCalendarCreate.setPrepareres(prepareres);
				}else {
					reqEventCalendarCreate.setPrepareres(Arrays.asList());
				}
			}
			
			/* Loại bỏ sự trùng nhau theo độ ưu tiên */
			if(reqEventCalendarCreate.getAttendeesNoRequired().size()>0) {
				List<ReqEventCalendarUserAttand> attendeesNoRequired=removeDuplicate(reqEventCalendarCreate.getAttendeesNoRequired(), getAllUsersAttandCreate(reqEventCalendarCreate, 3));
				reqEventCalendarCreate.setAttendeesNoRequired(attendeesNoRequired);
				eventCalendar.setAttendeesNoRequired(attendeesNoRequired.stream().map(e->e.toUserOrganizationEventCalendar()).collect(Collectors.toList()));
			}
			
			if(reqEventCalendarCreate.getAttendeesRequired().size()>0) {
				List<ReqEventCalendarUserAttand> attendeesRequired=removeDuplicate(reqEventCalendarCreate.getAttendeesRequired(), getAllUsersAttandCreate(reqEventCalendarCreate, 2));
				reqEventCalendarCreate.setAttendeesNoRequired(attendeesRequired);
				eventCalendar.setAttendeesRequired(attendeesRequired.stream().map(e->e.toUserOrganizationEventCalendar()).collect(Collectors.toList()));
			}
			
			if(reqEventCalendarCreate.getHosts().size()>0) {
				List<ReqEventCalendarUserAttand> hosts=removeDuplicate(reqEventCalendarCreate.getHosts(), getAllUsersAttandCreate(reqEventCalendarCreate, 1));
				reqEventCalendarCreate.setHosts(hosts);
				eventCalendar.setHosts(hosts.stream().map(e->e.toUserOrganizationEventCalendar()).collect(Collectors.toList()));
			}
			
			eventCalendar.setResources(reqEventCalendarCreate.getResources().stream().map(e->e.toEventCalendarResourceAttach()).collect(Collectors.toList()));
			eventCalendar.setAttachments(reqEventCalendarCreate.getAttachments());
			eventCalendar.setCreator(reqEventCalendarCreate.getCreator().toCreator());
			
			eventCalendar=save(eventCalendar, DataAction.create, user);
			
			/* Thông báo */
			try {
				ReqNotificationCreate reqNotificationCreate=new ReqNotificationCreate();
				reqNotificationCreate.setAction(NotificationAction.lich_moi_tham_gia_su_kien);
				reqNotificationCreate.setTitle(NotificationAction.lich_moi_tham_gia_su_kien.getTitle());
				reqNotificationCreate.setContent(eventCalendar.getContent());
				reqNotificationCreate.setType(NotificationType.info);
				reqNotificationCreate.setObject(NotificationObject.eventCalendar);
				reqNotificationCreate.setObjectId(eventCalendar.getId());
				reqNotificationCreate.setActionUrl(null);
				reqNotificationCreate.setCreator(null);
	
				notifyCreateEventCalendar(reqNotificationCreate, eventCalendar);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return eventCalendar;
		}
		
		throw new NotAcceptableExceptionAdvice("Không được phép, vì không có quyền");
	}

	@Override
	public EventCalendar update(String id, ReqEventCalendarUpdate reqEventCalendarUpdate, User user) {
		EventCalendar eventCalendar = getById(id);
		EventCalendar eventCalendarOld = getById(id);
		if(eventCalendar.canUpdate() && (eventCalendar.getType().equals(EventCalendarType.personal.getKey()) && eventCalendar.getCreator().getOrganizationUserId().equals(user.getId()))
				|| (eventCalendar.getType().equals(EventCalendarType.organization.getKey()) && permissionService.hasPermission(eventCalendar.getCreator().getOrganizationId(), user.getId(), PermissionKey.quanlylichcongtac.getKey()))) {
			/* Lịch sử */
			EventHistory eventHistory=new EventHistory();
			eventHistory.setEventId(eventCalendar.getId());
			eventHistory.setCreatorId(user.getId());
			eventHistory.setCreatorName(user.getFullName());
			eventHistory.setData(eventCalendar);
			eventHistoryRepository.save(eventHistory);
			
			/* Cập nhật mới */
			eventCalendar.setPeriod(reqEventCalendarUpdate.getPeriod());
			eventCalendar.setFrom(new Date(reqEventCalendarUpdate.getFrom()));
			eventCalendar.setTo(new Date(reqEventCalendarUpdate.getTo()));
			eventCalendar.setContent(reqEventCalendarUpdate.getContent());
			eventCalendar.setNotes(reqEventCalendarUpdate.getNotes());
			eventCalendar.setColor(reqEventCalendarUpdate.getColor());
			
			/* Chủ trì */
			//List<EventCalendarUserAttend> hostsNew=reqEventCalendarUpdate.getHosts().stream().map(e->e.toUserOrganizationEventCalendar()).collect(Collectors.toList());
			List<ReqEventCalendarUserAttand> remixHostsNew=remixUserOrganization(reqEventCalendarUpdate.getHosts());
			reqEventCalendarUpdate.setHosts(remixHostsNew);
			
			/* Tham dự bắt buộc */
			//List<EventCalendarUserAttend> attandeesRequiredNew=reqEventCalendarUpdate.getAttendeesRequired().stream().map(e->e.toUserOrganizationEventCalendar()).collect(Collectors.toList());
			List<ReqEventCalendarUserAttand> remixAttandeesRequiredNew=remixUserOrganization(reqEventCalendarUpdate.getAttendeesRequired());
			reqEventCalendarUpdate.setAttendeesRequired(remixAttandeesRequiredNew);
			
			/* Tham dự không bắt buộc */
			//List<EventCalendarUserAttend> attandeesNoRequiredNew=reqEventCalendarUpdate.getAttendeesNoRequired().stream().map(e->e.toUserOrganizationEventCalendar()).collect(Collectors.toList());
			List<ReqEventCalendarUserAttand> remixAttandeesNoRequiredNew=remixUserOrganization(reqEventCalendarUpdate.getAttendeesNoRequired());
			reqEventCalendarUpdate.setAttendeesNoRequired(remixAttandeesNoRequiredNew);
			
			/* Chuẩn bị hậu cần */
			reqEventCalendarUpdate.setAttendeesNoRequired(reqEventCalendarUpdate.getPrepareres());
			
			/* Loại bỏ sự trùng lắp theo độ ưu tiên*/
			if(true) {
				List<ReqEventCalendarUserAttand> attendeesNoRequired=removeDuplicate(reqEventCalendarUpdate.getAttendeesNoRequired(), getAllUsersAttandUpdate(reqEventCalendarUpdate, 3));
				reqEventCalendarUpdate.setAttendeesNoRequired(attendeesNoRequired);
				
				List<EventCalendarUserAttend> attandeesNoRequiredNew=attendeesNoRequired.stream().map(e->e.toUserOrganizationEventCalendar()).collect(Collectors.toList());
				for (EventCalendarUserAttend attendNoRequiredNew : attandeesNoRequiredNew) {
					for(EventCalendarUserAttend attendNoRequiredOld: eventCalendar.getAttendeesNoRequired()) {
						if(attendNoRequiredNew.isSimilar(attendNoRequiredOld)) {
							attendNoRequiredNew.setStatus(attendNoRequiredOld.getStatus());
							attendNoRequiredNew.setNotes(attendNoRequiredOld.getNotes());
							if(attendNoRequiredOld.getConfirmedTime()>0) {
								attendNoRequiredNew.setConfirmedTime(new Date(attendNoRequiredOld.getConfirmedTime()));
							}
						}
					}
				}
				eventCalendar.setAttendeesNoRequired(attandeesNoRequiredNew);
			}
			
			if(true) {
				List<ReqEventCalendarUserAttand> attandeesRequired=removeDuplicate(reqEventCalendarUpdate.getAttendeesRequired(), getAllUsersAttandUpdate(reqEventCalendarUpdate, 2));
				reqEventCalendarUpdate.setAttendeesRequired(attandeesRequired);
				
				List<EventCalendarUserAttend> attandeesRequiredNew=attandeesRequired.stream().map(e->e.toUserOrganizationEventCalendar()).collect(Collectors.toList());
				for (EventCalendarUserAttend attendRequiredNew : attandeesRequiredNew) {
					for(EventCalendarUserAttend attendRequiredOld: eventCalendar.getAttendeesRequired()) {
						if(attendRequiredNew.isSimilar(attendRequiredOld)) {
							attendRequiredNew.setStatus(attendRequiredOld.getStatus());
							attendRequiredNew.setNotes(attendRequiredOld.getNotes());
							if(attendRequiredOld.getConfirmedTime()>0) {
								attendRequiredNew.setConfirmedTime(new Date(attendRequiredOld.getConfirmedTime()));
							}
						}
					}
				}
				eventCalendar.setAttendeesRequired(attandeesRequiredNew);
			}
			
			if(true) {
				List<ReqEventCalendarUserAttand> hosts=removeDuplicate(reqEventCalendarUpdate.getHosts(), getAllUsersAttandUpdate(reqEventCalendarUpdate, 1));
				reqEventCalendarUpdate.setHosts(hosts);
				
				List<EventCalendarUserAttend> hostsNew=hosts.stream().map(e->e.toUserOrganizationEventCalendar()).collect(Collectors.toList());
				for (EventCalendarUserAttend hostNew : hostsNew) {
					for(EventCalendarUserAttend hostOld: eventCalendar.getHosts()) {
						if(hostNew.isSimilar(hostOld)) {
							hostNew.setStatus(hostOld.getStatus());
							hostNew.setNotes(hostOld.getNotes());
							if(hostOld.getConfirmedTime()>0) {
								hostNew.setConfirmedTime(new Date(hostOld.getConfirmedTime()));
							}
						}
					}
				}
				eventCalendar.setHosts(hostsNew);
			}
			
			eventCalendar.setResources(reqEventCalendarUpdate.getResources().stream().map(e->e.toEventCalendarResourceAttach()).collect(Collectors.toList()));
			eventCalendar.setAttachments(reqEventCalendarUpdate.getAttachments());
			eventCalendar = save(eventCalendar, DataAction.update, user);
			
			/* Thông báo */
			try {
				ReqNotificationCreate reqNotificationCreate=new ReqNotificationCreate();
				reqNotificationCreate.setAction(NotificationAction.lich_cap_nhat_su_kien);
				reqNotificationCreate.setTitle(NotificationAction.lich_cap_nhat_su_kien.getTitle());
				reqNotificationCreate.setContent(eventCalendar.getContent());
				reqNotificationCreate.setType(NotificationType.info);
				reqNotificationCreate.setObject(NotificationObject.eventCalendar);
				reqNotificationCreate.setObjectId(eventCalendar.getId());
				reqNotificationCreate.setActionUrl(null);
				reqNotificationCreate.setCreator(null);

				notifyUpdateEventCalendar(reqNotificationCreate, eventCalendar, eventCalendarOld);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return eventCalendar;
		}
		
		throw new NotAcceptableExceptionAdvice("Không được phép, vì không có quyền");
	}

	@Override
	public EventCalendar save(EventCalendar eventCalendar, DataAction dataAction, User user) {
		try {
			Assert.notNull(eventCalendar, "eventCalendar is null");
			Assert.notNull(dataAction, "dataAction is null");
			
			if(user!=null) {
				eventCalendar.setActor(user.toActor());
			}
			eventCalendar = eventCalendarRepository.save(eventCalendar);
			return eventCalendar;
		} catch (Exception e) {
			if(e instanceof DuplicateKeyException) {
				throw new DuplicateKeyExceptionAdvice("Dữ liệu bị trùng khóa chính, vui lòng thử lại");
			}
			throw e;
		}
	}
	
	@Override
	public void notifySoonExpire(Date beforeEventMinutes) {
		try {
			System.out.println("beforeEventMinutes: "+beforeEventMinutes.toString());
			EventCalendarFilter eventCalendarFilter=new EventCalendarFilter();
			eventCalendarFilter.setBeforeEventTime(beforeEventMinutes.getTime());
			List<EventCalendar> eventCalendars=eventCalendarRepositoryCustom.findAll(eventCalendarFilter);
			for (EventCalendar eventCalendar : eventCalendars) {
				System.out.println("eventCalendar: "+eventCalendar.getContent());
				eventCalendar.setNotifyBeforeEvent(true);
				eventCalendarRepository.save(eventCalendar);
				
				ReqNotificationCreate reqNotificationCreate_Support=new ReqNotificationCreate();
				reqNotificationCreate_Support.setAction(NotificationAction.lich_su_kien_sap_bat_dau);
				reqNotificationCreate_Support.setTitle(NotificationAction.lich_su_kien_sap_bat_dau.getTitle());
				reqNotificationCreate_Support.setContent(eventCalendar.getContent());
				reqNotificationCreate_Support.setType(NotificationType.info);
				reqNotificationCreate_Support.setObject(NotificationObject.eventCalendar);
				reqNotificationCreate_Support.setObjectId(eventCalendar.getId());
				reqNotificationCreate_Support.setActionUrl(null);
				reqNotificationCreate_Support.setCreator(null);
	
				notifyBeforeEventCalendar(reqNotificationCreate_Support, eventCalendar);
			}
			System.out.println("Done notifySoonExpire");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void notifyBeforeEventCalendar(ReqNotificationCreate reqNotificationCreate, EventCalendar eventCalendar) {
		Thread thread=new Thread(new Runnable() {
			@Override
			public void run() {
				/* Thông báo cho hosts */
				List<EventCalendarUserAttend> userTasks=new ArrayList<EventCalendarUserAttend>();
				userTasks.addAll(eventCalendar.getHosts());
				
				/* Thông báo */
				String title="Sự kiện bạn tham gia chủ trì sắp bắt đầu";
				reqNotificationCreate.setTitle(title);
				
				if(userTasks.size()>0) {
					for(EventCalendarUserAttend receiver:userTasks) {
						if(reqNotificationCreate.getCreator().equals(receiver.toCreator())) {
							continue;
						}
	
						reqNotificationCreate.setReceiver(receiver.toReceiver());
						if(reqNotificationCreate.getReceiver().getOrganizationUserId()!=null) {
							reqNotificationCreate.setScope(NotificationScope.user);
						}else {
							reqNotificationCreate.setScope(NotificationScope.organization);
						}
						
						notificationService.create(reqNotificationCreate);
					}
				}
				
				/* Thông báo cho attendeesRequired */
				userTasks=new ArrayList<EventCalendarUserAttend>();
				userTasks.addAll(eventCalendar.getAttendeesRequired());
				
				/* Thông báo */
				title="Sự kiện bạn tham gia (bắt buộc) sắp bắt đầu";
				reqNotificationCreate.setTitle(title);
				
				if(userTasks.size()>0) {
					for(EventCalendarUserAttend receiver:userTasks) {
						if(reqNotificationCreate.getCreator().equals(receiver.toCreator())) {
							continue;
						}
						
						reqNotificationCreate.setReceiver(receiver.toReceiver());
						if(reqNotificationCreate.getReceiver().getOrganizationUserId()!=null) {
							reqNotificationCreate.setScope(NotificationScope.user);
						}else {
							reqNotificationCreate.setScope(NotificationScope.organization);
						}
						
						notificationService.create(reqNotificationCreate);
					}
				}
				
				/* Thông báo cho attendeesNoRequired */
				userTasks=new ArrayList<EventCalendarUserAttend>();
				userTasks.addAll(eventCalendar.getAttendeesNoRequired());
				
				/* Thông báo */
				title="Sự kiện bạn tham gia sắp bắt đầu";
				reqNotificationCreate.setTitle(title);
				
				if(userTasks.size()>0) {
					for(EventCalendarUserAttend receiver:userTasks) {
						if(reqNotificationCreate.getCreator().equals(receiver.toCreator())) {
							continue;
						}
						
						reqNotificationCreate.setReceiver(receiver.toReceiver());
						if(reqNotificationCreate.getReceiver().getOrganizationUserId()!=null) {
							reqNotificationCreate.setScope(NotificationScope.user);
						}else {
							reqNotificationCreate.setScope(NotificationScope.organization);
						}
						
						notificationService.create(reqNotificationCreate);
					}
				}
			}
		});
		thread.start();
	}
	
	private void doNotificationToUserAttend(List<EventCalendarUserAttend> userAttends, ReqNotificationCreate reqNotificationCreate, EventCalendar eventCalendar) {
		for(EventCalendarUserAttend receiver:userAttends) {
			try {
				if(reqNotificationCreate.getCreator().equals(receiver.toCreator())) {
					continue;
				}

				reqNotificationCreate.setReceiver(receiver.toReceiver());
				if(reqNotificationCreate.getReceiver().getOrganizationUserId()!=null) {
					reqNotificationCreate.setScope(NotificationScope.user);
				}else {
					reqNotificationCreate.setScope(NotificationScope.organization);
				}
				
				notificationService.create(reqNotificationCreate);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void notifyCancelEventCalendar(ReqNotificationCreate reqNotificationCreate, EventCalendar eventCalendar) {
		Thread thread=new Thread(new Runnable() {
			@Override
			public void run() {
				/* Hosts -----------------*/
				/* Thông báo cho hosts */
				List<EventCalendarUserAttend> userTasks=new ArrayList<EventCalendarUserAttend>();
				userTasks.addAll(eventCalendar.getHosts());
				
				/* Thông báo */
				String title="Đã hủy sự kiện mà bạn được mời làm chủ trì";
				reqNotificationCreate.setTitle(title);
				
				/* Thông báo cho các đối tượng được chỉ định */
				doNotificationToUserAttend(userTasks, reqNotificationCreate, eventCalendar);
				
				/* AttendeesRequired-----------------*/
				/* Thông báo cho attendeesRequired */
				userTasks=new ArrayList<EventCalendarUserAttend>();
				userTasks.addAll(eventCalendar.getAttendeesRequired());
				
				/* Thông báo */
				title="Đã hủy sự kiện mà bạn được mời tham gia (bắt buộc)";
				reqNotificationCreate.setTitle(title);
				
				/* Thông báo cho các đối tượng được chỉ định */
				doNotificationToUserAttend(userTasks, reqNotificationCreate, eventCalendar);
				
				/* AttendeesNoRequired -----------------*/
				/* Thông báo cho attendeesNoRequired */
				userTasks=new ArrayList<EventCalendarUserAttend>();
				userTasks.addAll(eventCalendar.getAttendeesNoRequired());
				
				/* Thông báo */
				title="Đã hủy sự kiện mà bạn được mời tham gia";
				reqNotificationCreate.setTitle(title);
				
				/* Thông báo cho các đối tượng được chỉ định */
				doNotificationToUserAttend(userTasks, reqNotificationCreate, eventCalendar);
			}
		});
		thread.start();
	}
	
	private void notifyCreateEventCalendar(ReqNotificationCreate reqNotificationCreate, EventCalendar eventCalendar) {
		Thread thread=new Thread(new Runnable() {
			@Override
			public void run() {
				List<EventCalendarUserAttend> userTasks=new ArrayList<EventCalendarUserAttend>();
				
				/* Hosts -----------------*/
				userTasks=new ArrayList<EventCalendarUserAttend>();
				userTasks.addAll(eventCalendar.getHosts());
				reqNotificationCreate.setTitle("Đã mời bạn tham gia chủ trì sự kiện");
				doNotificationToUserAttend(userTasks, reqNotificationCreate, eventCalendar);
				
				/* AttendeesRequired-----------------*/
				userTasks=new ArrayList<EventCalendarUserAttend>();
				userTasks.addAll(eventCalendar.getAttendeesRequired());
				reqNotificationCreate.setTitle("Đã mời bạn tham gia sự kiện");
				doNotificationToUserAttend(userTasks, reqNotificationCreate, eventCalendar);
				
				/* AttendeesNoRequired -----------------*/
				userTasks=new ArrayList<EventCalendarUserAttend>();
				userTasks.addAll(eventCalendar.getAttendeesNoRequired());
				reqNotificationCreate.setTitle("Đã mời bạn tham gia sự kiện");
				doNotificationToUserAttend(userTasks, reqNotificationCreate, eventCalendar);
				
				/* Prepareres -----------------*/
				userTasks=new ArrayList<EventCalendarUserAttend>();
				userTasks.addAll(eventCalendar.getAttendeesNoRequired());
				reqNotificationCreate.setTitle("Đã mời bạn tham gia sự kiện, với vai trò là chuẩn bị hậu cần");
				doNotificationToUserAttend(userTasks, reqNotificationCreate, eventCalendar);
			}
		});
		thread.start();
	}
	
	private void notifyUpdateEventCalendar(ReqNotificationCreate reqNotificationCreate, EventCalendar eventCalendar, EventCalendar eventCalendarOld) {
		Thread thread=new Thread(new Runnable() {
			@Override
			public void run() {
				List<EventCalendarUserAttend> userTasks=new ArrayList<EventCalendarUserAttend>();
				String titleDetail="";
				if(!eventCalendarOld.getFrom().equals(eventCalendar.getFrom())) {
					titleDetail="(thay đổi thời gian từ "+DateTimeUtil.getDatetimeFormat().format(eventCalendarOld.getFrom())+" sang "+DateTimeUtil.getDatetimeFormat().format(eventCalendar.getFrom())+")";
				}
				
				/* Hosts -----------------*/
				userTasks=new ArrayList<EventCalendarUserAttend>();
				userTasks.addAll(eventCalendar.getHosts());
				reqNotificationCreate.setTitle("Đã cập nhật lịch mà bạn tham gia chủ trì sự kiện "+titleDetail);
				doNotificationToUserAttend(userTasks, reqNotificationCreate, eventCalendar);
				
				/* AttendeesRequired-----------------*/
				userTasks=new ArrayList<EventCalendarUserAttend>();
				userTasks.addAll(eventCalendar.getAttendeesRequired());
				reqNotificationCreate.setTitle("Đã cập nhật lịch mà bạn chuẩn bị hậu cần, trang thiết bị cho sự kiện "+titleDetail);
				doNotificationToUserAttend(userTasks, reqNotificationCreate, eventCalendar);
				
				/* AttendeesNoRequired -----------------*/
				userTasks=new ArrayList<EventCalendarUserAttend>();
				userTasks.addAll(eventCalendar.getAttendeesNoRequired());
				reqNotificationCreate.setTitle("Đã cập nhật lịch mà bạn tham gia sự kiện "+titleDetail);
				doNotificationToUserAttend(userTasks, reqNotificationCreate, eventCalendar);
				
				/* Prepareres -----------------*/
				userTasks=new ArrayList<EventCalendarUserAttend>();
				userTasks.addAll(eventCalendar.getPrepareres());
				reqNotificationCreate.setTitle("Đã cập nhật lịch mà bạn tham gia sự kiện "+titleDetail);
				doNotificationToUserAttend(userTasks, reqNotificationCreate, eventCalendar);
			}
		});
		thread.start();
	}
	
	private void notifyConfirmEventCalendar(ReqNotificationCreate reqNotificationCreate, EventCalendar eventCalendar, String status) {
		Thread thread=new Thread(new Runnable() {
			@Override
			public void run() {
				List<EventCalendarUserAttend> userTasks=new ArrayList<EventCalendarUserAttend>();
				
				/* Hosts -----------------*/
				userTasks=new ArrayList<EventCalendarUserAttend>();
				userTasks.addAll(eventCalendar.getHosts());
				reqNotificationCreate.setTitle("Đã xác nhận "+status+" sự kiện");
				doNotificationToUserAttend(userTasks, reqNotificationCreate, eventCalendar);
				
				/* AttendeesRequired-----------------*/
				userTasks=new ArrayList<EventCalendarUserAttend>();
				userTasks.addAll(eventCalendar.getAttendeesRequired());
				reqNotificationCreate.setTitle("Đã xác nhận "+status+" sự kiện");
				doNotificationToUserAttend(userTasks, reqNotificationCreate, eventCalendar);
				
				/* AttendeesNoRequired -----------------*/
				userTasks=new ArrayList<EventCalendarUserAttend>();
				userTasks.addAll(eventCalendar.getAttendeesNoRequired());
				reqNotificationCreate.setTitle("Đã xác nhận "+status+" sự kiện");
				doNotificationToUserAttend(userTasks, reqNotificationCreate, eventCalendar);
				
				/* Prepareres -----------------*/
				userTasks=new ArrayList<EventCalendarUserAttend>();
				userTasks.addAll(eventCalendar.getPrepareres());
				reqNotificationCreate.setTitle("Đã xác nhận "+status+" sự kiện");
				doNotificationToUserAttend(userTasks, reqNotificationCreate, eventCalendar);
			}
		});
		thread.start();
	}
	
	private void notifyDeligacyEventCalendar(ReqNotificationCreate reqNotificationCreate, EventCalendar eventCalendar, String status, EventCalendarUserAttend fromUser, EventCalendarUserAttend toUser) {
		Thread thread=new Thread(new Runnable() {
			@Override
			public void run() {
				List<EventCalendarUserAttend> userTasks=new ArrayList<EventCalendarUserAttend>();
				
				/* Thông báo cho người được ủy quyền */
				userTasks=new ArrayList<EventCalendarUserAttend>();
				userTasks.add(toUser);
				reqNotificationCreate.setTitle("Đã "+status+" sự kiện cho bạn");
				doNotificationToUserAttend(userTasks, reqNotificationCreate, eventCalendar);
				
				/* Thông báo cho người ủy quyền */
				userTasks=new ArrayList<EventCalendarUserAttend>();
				userTasks.add(fromUser);
				reqNotificationCreate.setTitle("Bạn đã "+status+" sự kiện");
				doNotificationToUserAttend(userTasks, reqNotificationCreate, eventCalendar);
				
				/* Thông báo cho người tạo sự kiện */
				userTasks=new ArrayList<EventCalendarUserAttend>();
				userTasks.add(eventCalendar.getCreator().toEventCalendarUserAttend());
				reqNotificationCreate.setTitle(fromUser.toCreator().getTextDisplay()+" đã "+status+" sự kiện lại cho "+toUser.toCreator().getTextDisplay());
				doNotificationToUserAttend(userTasks, reqNotificationCreate, eventCalendar);
			}
		});
		thread.start();
	}
	
	private List<ReqEventCalendarUserAttand> getAllUsersAttandCreate(ReqEventCalendarCreate reqEventCalendarCreate, int position){
		/* Tất cả người tham dự (user và đơn vị) */
		List<ReqEventCalendarUserAttand> allUsersAttand=new ArrayList<ReqEventCalendarUserAttand>();
		if(position==1) {
			allUsersAttand.addAll(reqEventCalendarCreate.getAttendeesRequired());
			allUsersAttand.addAll(reqEventCalendarCreate.getAttendeesNoRequired());
		}
		if(position==2) {
			allUsersAttand.addAll(reqEventCalendarCreate.getHosts());
			allUsersAttand.addAll(reqEventCalendarCreate.getAttendeesNoRequired());
		}
		if(position==3) {
			allUsersAttand.addAll(reqEventCalendarCreate.getHosts());
			allUsersAttand.addAll(reqEventCalendarCreate.getAttendeesRequired());
		}
		return allUsersAttand;
	}
	
	private List<ReqEventCalendarUserAttand> getAllUsersAttandUpdate(ReqEventCalendarUpdate reqEventCalendarUpdate, int position){
		/* Tất cả người tham dự (user và đơn vị) */
		List<ReqEventCalendarUserAttand> allUsersAttand=new ArrayList<ReqEventCalendarUserAttand>();
		if(position==1) {
			allUsersAttand.addAll(reqEventCalendarUpdate.getAttendeesRequired());
			allUsersAttand.addAll(reqEventCalendarUpdate.getAttendeesNoRequired());
		}
		if(position==2) {
			allUsersAttand.addAll(reqEventCalendarUpdate.getHosts());
			allUsersAttand.addAll(reqEventCalendarUpdate.getAttendeesNoRequired());
		}
		if(position==3) {
			allUsersAttand.addAll(reqEventCalendarUpdate.getHosts());
			allUsersAttand.addAll(reqEventCalendarUpdate.getAttendeesRequired());
		}
		return allUsersAttand;
	}
	
	/**
	 * Kiểm tra và lấy thêm user details nếu mời đơn vị
	 * @param usersAttand
	 * @return Danh sách user details
	 */
	private List<ReqEventCalendarUserAttand> remixUserOrganization(List<ReqEventCalendarUserAttand> usersAttand){
		/* Kết quả người tham dự đang cần mix*/
		List<ReqEventCalendarUserAttand> results=new ArrayList<ReqEventCalendarUserAttand>();
		for(ReqEventCalendarUserAttand userAttand:usersAttand) {
			List<ReqEventCalendarUserAttand> attands=new ArrayList<ReqEventCalendarUserAttand>();
			/* Nếu là user detail */
			if(userAttand.isUser()) {
				userAttand.setType(EventCalendarAttandUserOrganizationType.user.getKey());
				attands.add(userAttand);
			}	
			/* Nếu là đơn vị */
			else {
				/* Đi kiếm user đại diện */
				UserFilter userFilter=new UserFilter();
				userFilter.setActive(true);
				userFilter.setIncludeOrganizationIds(Arrays.asList(userAttand.getOrganizationId()));
				List<User> users = userService.findUserAll(userFilter);
				for (User user : users) {
					/* Kiểm tra user nếu có quyền lại đại diện */
					if(permissionService.hasPermission(userAttand.getOrganizationId(), user.getId(), PermissionKey.daidienlichcongtacdonvi.getKey())) {
						ReqEventCalendarUserAttand reqAttand;
						try {
							reqAttand = (ReqEventCalendarUserAttand) userAttand.clone();
							reqAttand.setOrganizationUserId(user.getId());
							reqAttand.setOrganizationUserName(user.getFullName());
							reqAttand.setJobTitle(user.getJobTitle());
							reqAttand.setType(EventCalendarAttandUserOrganizationType.organization.getKey());
							attands.add(reqAttand);
						} catch (CloneNotSupportedException e) {
							e.printStackTrace();
						}
					}
				}
			}
			
			/* Kiểm tra sự trùng lắp*/
			for(ReqEventCalendarUserAttand attand:attands) {
				boolean exists=false;
				for(ReqEventCalendarUserAttand result:results) {
					if(result.isSimilar(attand)) {
						exists=true;
						break;
					}
				}
				if(exists==false) {
					results.add(attand);
				}
			}
		}
		return results;
	}
	
	/**
	 * Xóa trùng lắp
	 * @param results
	 * @param allUsersPriority
	 * @return
	 */
	private List<ReqEventCalendarUserAttand> removeDuplicate(List<ReqEventCalendarUserAttand> results, List<ReqEventCalendarUserAttand> allUsersPriority) {
		for(ReqEventCalendarUserAttand attand:allUsersPriority) {
			if(attand.isUser()) {
				for(ReqEventCalendarUserAttand result:results) {
					if(attand.isSimilar(result)){
						results.remove(result);
						break;
					}
				}
			}
		}
		return results;
	}

	@Override
	public EventCalendar userDoConfirm(String eventCalendarId,
			ReqEventCalendarUserDoConfirmed reqEventCalendarUserDoConfirmed, User user) {
		EventCalendarFilter eventCalendarFilter=new EventCalendarFilter();
		eventCalendarFilter.setId(eventCalendarId);
		eventCalendarFilter.setTrash(false);
		
		EventCalendar eventCalendar=getOne(eventCalendarFilter);
		if(eventCalendar.canUpdate()) {
			/* Kiểm tra người xác nhận có tồn tại trong sự kiện không? */
			EventCalendarUserAttend eventCalendarUserAttand=null;
			
			/* Kiểm tra trong hosts */
			if(eventCalendarUserAttand==null && eventCalendar.getHosts().size()>0) {
				for(EventCalendarUserAttend attend:eventCalendar.getHosts()) {
					if(attend.isSimilar(reqEventCalendarUserDoConfirmed)) {
						attend.setStatus(reqEventCalendarUserDoConfirmed.getStatus());
						attend.setNotes(reqEventCalendarUserDoConfirmed.getNotes());
						attend.setConfirmedTime(new Date());
						
						eventCalendarUserAttand=attend;
					}
				}
			}
			
			/* Kiểm tra người tham dự bắt buộc */
			if(eventCalendarUserAttand==null && eventCalendar.getAttendeesRequired().size()>0) {
				for(EventCalendarUserAttend attend:eventCalendar.getAttendeesRequired()) {
					if(attend.isSimilar(reqEventCalendarUserDoConfirmed)) {
						attend.setStatus(reqEventCalendarUserDoConfirmed.getStatus());
						attend.setNotes(reqEventCalendarUserDoConfirmed.getNotes());
						attend.setConfirmedTime(new Date());
						
						eventCalendarUserAttand=attend;
					}
				}
			}
			
			/* Kiểm tra người tham dự không bắt buộc */
			if(eventCalendarUserAttand==null && eventCalendar.getAttendeesNoRequired().size()>0) {
				for(EventCalendarUserAttend attend:eventCalendar.getAttendeesNoRequired()) {
					if(attend.isSimilar(reqEventCalendarUserDoConfirmed)) {
						attend.setStatus(reqEventCalendarUserDoConfirmed.getStatus());
						attend.setNotes(reqEventCalendarUserDoConfirmed.getNotes());
						attend.setConfirmedTime(new Date());
						
						eventCalendarUserAttand=attend;
					}
				}
			}
			
			if(eventCalendarUserAttand!=null) {
				eventCalendar = save(eventCalendar, DataAction.update, user);
				
				/* Thông báo */
				try {
					ReqNotificationCreate reqNotificationCreate=new ReqNotificationCreate();
					reqNotificationCreate.setAction(NotificationAction.lich_xac_nhan_tham_gia_su_kien);
					reqNotificationCreate.setTitle(NotificationAction.lich_xac_nhan_tham_gia_su_kien.getTitle());
					reqNotificationCreate.setContent(eventCalendar.getContent());
					reqNotificationCreate.setType(NotificationType.info);
					reqNotificationCreate.setObject(NotificationObject.eventCalendar);
					reqNotificationCreate.setObjectId(eventCalendar.getId());
					reqNotificationCreate.setActionUrl(null);
					reqNotificationCreate.setCreator(null);

					String status = EnumUtils.getEnumIgnoreCase(EventCalendarUserStatus.class, reqEventCalendarUserDoConfirmed.getStatus()).getName().toLowerCase();
					notifyConfirmEventCalendar(reqNotificationCreate, eventCalendar, status);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return eventCalendar;
		}
			
		throw new NotAcceptableExceptionAdvice("Không được phép, vì không có quyền");
	}

	@Override
	public EventCalendar userDoDelegacy(String eventCalendarId,
			ReqEventCalendarUserDoDelegacy reqEventCalendarUserDoDelegacy, User user) {
		EventCalendarFilter eventCalendarFilter=new EventCalendarFilter();
		eventCalendarFilter.setId(eventCalendarId);
		eventCalendarFilter.setTrash(false);
		
		EventCalendar eventCalendar=getOne(eventCalendarFilter);
		if(eventCalendar.canUpdate()) {
			EventCalendarUserAttend fromUser=null;
			EventCalendarUserAttend toUser=null;
			
			/* Nếu type là hosts */
			if(reqEventCalendarUserDoDelegacy.getType().equalsIgnoreCase(EventCalendarUserType.hosts.getKey())) {
				/* Kiểm tra người ủy quyền phải là người trong sự kiện */
				boolean existsFromUser=false;
				for (EventCalendarUserAttend eventCalendarUserAttend : eventCalendar.getHosts()) {
					if(eventCalendarUserAttend.isSimilar(reqEventCalendarUserDoDelegacy.getFromUser().toUserOrganizationEventCalendar())) {
						existsFromUser=true;
						break;
					}
				}
				if(existsFromUser==false) {
					throw new NotAcceptableExceptionAdvice("fromUser không tồn tại trong hosts của sự kiện");
				}
					
				/* Kiểm tra người được ủy quyền phải là người không có trong sự kiện */
				boolean existsToUser=false;
				for (EventCalendarUserAttend eventCalendarUserAttend : eventCalendar.getAllUsers()) {
					if(eventCalendarUserAttend.isSimilar(reqEventCalendarUserDoDelegacy.getToUser().toUserOrganizationEventCalendar())) {
						existsToUser=true;
						break;
					}
				}
				if(existsToUser) {
					throw new NotAcceptableExceptionAdvice("toUser đã tồn tại trong sự kiện");
				}
				
				/* Cập nhật */
				for (EventCalendarUserAttend eventCalendarUserAttend : eventCalendar.getHosts()) {
					if(eventCalendarUserAttend.isSimilar(reqEventCalendarUserDoDelegacy.getFromUser().toUserOrganizationEventCalendar())) {
						fromUser=CommonUtil.copy(eventCalendarUserAttend, EventCalendarUserAttend.class);
						fromUser.setDelegacyTime(new Date());
						toUser=reqEventCalendarUserDoDelegacy.getToUser().toUserOrganizationEventCalendar();
						
						eventCalendarUserAttend.setOrganizationId(toUser.getOrganizationId());
						eventCalendarUserAttend.setOrganizationName(toUser.getOrganizationName());
						eventCalendarUserAttend.setOrganizationUserId(toUser.getOrganizationUserId());
						eventCalendarUserAttend.setOrganizationUserName(toUser.getOrganizationUserName());
						eventCalendarUserAttend.setJobTitle(toUser.getJobTitle());
						eventCalendarUserAttend.getHistoriesDelegacy().add(fromUser);
						break;
					}
				}
			}
			/* Nếu type là attandeesRequried */
			else if(reqEventCalendarUserDoDelegacy.getType().equalsIgnoreCase(EventCalendarUserType.attendeesRequired.getKey())) {
				/* Kiểm tra người ủy quyền phải là người trong sự kiện */
				boolean existsFromUser=false;
				for (EventCalendarUserAttend eventCalendarUserAttend : eventCalendar.getAttendeesRequired()) {
					if(eventCalendarUserAttend.isSimilar(reqEventCalendarUserDoDelegacy.getFromUser().toUserOrganizationEventCalendar())) {
						existsFromUser=true;
						break;
					}
				}
				if(existsFromUser==false) {
					throw new NotAcceptableExceptionAdvice("fromUser không tồn tại trong addtandeesRequired của sự kiện");
				}
					
				/* Kiểm tra người được ủy quyền phải là người không có trong sự kiện */
				boolean existsToUser=false;
				for (EventCalendarUserAttend eventCalendarUserAttend : eventCalendar.getAllUsers()) {
					if(eventCalendarUserAttend.isSimilar(reqEventCalendarUserDoDelegacy.getToUser().toUserOrganizationEventCalendar())) {
						existsToUser=true;
						break;
					}
				}
				if(existsToUser) {
					throw new NotAcceptableExceptionAdvice("toUser đã tồn tại trong sự kiện");
				}
				
				/* Cập nhật */
				for (EventCalendarUserAttend eventCalendarUserAttend : eventCalendar.getAttendeesRequired()) {
					if(eventCalendarUserAttend.isSimilar(reqEventCalendarUserDoDelegacy.getFromUser().toUserOrganizationEventCalendar())) {
						fromUser=CommonUtil.copy(eventCalendarUserAttend, EventCalendarUserAttend.class);
						fromUser.setDelegacyTime(new Date());
						toUser=reqEventCalendarUserDoDelegacy.getToUser().toUserOrganizationEventCalendar();
						
						eventCalendarUserAttend.setOrganizationId(toUser.getOrganizationId());
						eventCalendarUserAttend.setOrganizationName(toUser.getOrganizationName());
						eventCalendarUserAttend.setOrganizationUserId(toUser.getOrganizationUserId());
						eventCalendarUserAttend.setOrganizationUserName(toUser.getOrganizationUserName());
						eventCalendarUserAttend.setJobTitle(toUser.getJobTitle());
						eventCalendarUserAttend.getHistoriesDelegacy().add(fromUser);
						break;
					}
				}
			}
			/* Nếu type là attandeesNoRequried */
			else if(reqEventCalendarUserDoDelegacy.getType().equalsIgnoreCase(EventCalendarUserType.attendeesNoRequired.getKey())) {
				/* Kiểm tra người ủy quyền phải là người trong sự kiện */
				boolean existsFromUser=false;
				for (EventCalendarUserAttend eventCalendarUserAttend : eventCalendar.getAttendeesNoRequired()) {
					if(eventCalendarUserAttend.isSimilar(reqEventCalendarUserDoDelegacy.getFromUser().toUserOrganizationEventCalendar())) {
						existsFromUser=true;
						break;
					}
				}
				if(existsFromUser==false) {
					throw new NotAcceptableExceptionAdvice("fromUser không tồn tại trong addtandeesNoRequired của sự kiện");
				}
					
				/* Kiểm tra người được ủy quyền phải là người không có trong sự kiện */
				boolean existsToUser=false;
				for (EventCalendarUserAttend eventCalendarUserAttend : eventCalendar.getAllUsers()) {
					if(eventCalendarUserAttend.isSimilar(reqEventCalendarUserDoDelegacy.getToUser().toUserOrganizationEventCalendar())) {
						existsToUser=true;
						break;
					}
				}
				if(existsToUser) {
					throw new NotAcceptableExceptionAdvice("toUser đã tồn tại trong sự kiện");
				}
				
				/* Cập nhật */
				for (EventCalendarUserAttend eventCalendarUserAttend : eventCalendar.getAttendeesNoRequired()) {
					if(eventCalendarUserAttend.isSimilar(reqEventCalendarUserDoDelegacy.getFromUser().toUserOrganizationEventCalendar())) {
						fromUser=CommonUtil.copy(eventCalendarUserAttend, EventCalendarUserAttend.class);
						fromUser.setDelegacyTime(new Date());
						toUser=reqEventCalendarUserDoDelegacy.getToUser().toUserOrganizationEventCalendar();
						
						eventCalendarUserAttend.setOrganizationId(toUser.getOrganizationId());
						eventCalendarUserAttend.setOrganizationName(toUser.getOrganizationName());
						eventCalendarUserAttend.setOrganizationUserId(toUser.getOrganizationUserId());
						eventCalendarUserAttend.setOrganizationUserName(toUser.getOrganizationUserName());
						eventCalendarUserAttend.setJobTitle(toUser.getJobTitle());
						eventCalendarUserAttend.getHistoriesDelegacy().add(fromUser);
						break;
					}
				}
			}
			
			if(fromUser!=null && toUser!=null) {
				eventCalendar = save(eventCalendar, DataAction.update, user);
				
				/* Thông báo */
				try {
					ReqNotificationCreate reqNotificationCreate=new ReqNotificationCreate();
					reqNotificationCreate.setAction(NotificationAction.lich_uy_quyen_tham_gia_su_kien);
					reqNotificationCreate.setTitle(NotificationAction.lich_uy_quyen_tham_gia_su_kien.getTitle());
					reqNotificationCreate.setContent(eventCalendar.getContent());
					reqNotificationCreate.setType(NotificationType.info);
					reqNotificationCreate.setObject(NotificationObject.eventCalendar);
					reqNotificationCreate.setObjectId(eventCalendar.getId());
					reqNotificationCreate.setActionUrl(null);
					reqNotificationCreate.setCreator(null);

					String status = EventCalendarUserStatus.delegacy.getName().toLowerCase();
					notifyDeligacyEventCalendar(reqNotificationCreate, eventCalendar, status, fromUser, toUser);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return eventCalendar;
		}
		
		throw new NotAcceptableExceptionAdvice("Không được phép, vì không có quyền");
	}
}
