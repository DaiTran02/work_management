package ws.core.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import ws.core.advice.DuplicateKeyExceptionAdvice;
import ws.core.advice.NotAcceptableExceptionAdvice;
import ws.core.advice.NotFoundElementExceptionAdvice;
import ws.core.advice.ObjectIdExceptionAdvance;
import ws.core.enums.OrganizationLevel;
import ws.core.model.Organization;
import ws.core.model.RoleTemplate;
import ws.core.model.User;
import ws.core.model.embeded.FirstReview;
import ws.core.model.embeded.GroupOrganizationExpand;
import ws.core.model.embeded.RoleOrganizationExpand;
import ws.core.model.embeded.UserOrganizationExpand;
import ws.core.model.embeded.UserOrganizationExpand.AddFromSource;
import ws.core.model.filter.OrganizationFilter;
import ws.core.model.filter.UserFilter;
import ws.core.model.request.ReqGroupOrganizationCreate;
import ws.core.model.request.ReqGroupOrganizationUpdate;
import ws.core.model.request.ReqOrganizationCreate;
import ws.core.model.request.ReqOrganizationCreatePartner;
import ws.core.model.request.ReqOrganizationUpdate;
import ws.core.model.request.ReqOrganizationUpdatePartner;
import ws.core.model.request.ReqRoleOrganizationCreate;
import ws.core.model.request.ReqRoleOrganizationUpdate;
import ws.core.model.request.ReqUserLoginAddToOrganization;
import ws.core.model.request.ReqUserOrganizationAdds;
import ws.core.model.request.ReqUserOrganizationAddsPartner;
import ws.core.model.request.ReqUserOrganizationRemoves;
import ws.core.model.request.ReqUserOrganizationRemovesPartner;
import ws.core.model.request.ReqUserOrganizationUpdate;
import ws.core.model.response.ResUserOrganizationAddsPartner;
import ws.core.model.response.ResUserOrganizationRemovesPartner;
import ws.core.respository.OrganizationRepository;
import ws.core.respository.OrganizationRepositoryCustom;
import ws.core.services.CasbinAuthService;
import ws.core.services.DocService;
import ws.core.services.OrganizationService;
import ws.core.services.OtpService;
import ws.core.services.TaskService;
import ws.core.services.UserService;

@Service
public class OrganizationServiceImpl implements OrganizationService{

	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Autowired
	private OrganizationRepositoryCustom organizationRepositoryCustom;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CasbinAuthService casbinAuthService;
	
	@Autowired
	private OtpService otpService;
	
	@Autowired
	private DocService docService;
	
	@Autowired
	private TaskService taskService;
	
	@Override
	public OrganizationLevel autoDetectDefaultByUnitCode(String unitCode) {
		if(unitCode!=null && unitCode.split("\\.").length<=2) {
			return OrganizationLevel.organization;
		}
		return OrganizationLevel.room;
	}
	
	@Override
	public Optional<Organization> findOrganizationById(String organizationId) {
		if(ObjectId.isValid(organizationId))
			return organizationRepository.findById(new ObjectId(organizationId));
		throw new ObjectIdExceptionAdvance("organizationId ["+organizationId+"] không hợp lệ");
	}

	@Override
	public Organization getOrganizationById(String organizationId) {
		Optional<Organization> organization = findOrganizationById(organizationId);
		if(organization.isPresent()) {
			return organization.get();
		}
		throw new NotFoundElementExceptionAdvice("Không tìm thấy tổ chức");
	}
	
	@Override
	public Organization deleteOrganizationById(String organizationId) {
		Organization organization = getOrganizationById(organizationId);
		if(organization.getUserOrganizationExpands().size()>0) {
			throw new NotAcceptableExceptionAdvice("Không thể xóa tổ chức này, vì đang có người dùng");
		}
		
		organizationRepository.delete(organization);
		
		organization.getAllUserIds().stream().forEach(userId->{
			userService.updateOrganizationOfUser(userId);
		});
		return organization;
	}

	@Override
	public Organization deleteOrganizationByUnitCode(String unitCode) {
		Organization organization = getOrganizationByUnitCode(unitCode);
		if(organization.getUserOrganizationExpands().size()>0) {
			throw new NotAcceptableExceptionAdvice("Không thể xóa tổ chức này, vì đang có người dùng");
		}
		
		organizationRepository.delete(organization);
		organization.getAllUserIds().stream().forEach(userId->{
			userService.updateOrganizationOfUser(userId);
		});
		return organization;
	}

	@Override
	public Organization createOrganization(ReqOrganizationCreate reqOrganizationCreate, User creator) {
		Organization organizationCreate=new Organization();
		organizationCreate.setId(new ObjectId());
		organizationCreate.setCreatedTime(new Date());
		organizationCreate.setUpdatedTime(new Date());
		organizationCreate.setName(reqOrganizationCreate.getName());
		organizationCreate.setDescription(reqOrganizationCreate.getDescription());
		organizationCreate.setParentId(reqOrganizationCreate.getParentId());
		organizationCreate.setActive(reqOrganizationCreate.isActive());
		if(creator!=null) {
			organizationCreate.setCreatorId(creator.getId());
			organizationCreate.setCreatorName(creator.getFullName());
		}
		organizationCreate.setUnitCode(reqOrganizationCreate.getUnitCode());
		organizationCreate.setOrganizationCategoryId(reqOrganizationCreate.getOrganizationCategoryId());
		organizationCreate.setOrder(reqOrganizationCreate.getOrder());
		organizationCreate.setPath(getPathOrganization(organizationCreate));
		
		organizationCreate.setLevel(reqOrganizationCreate.getLevel());
		if(reqOrganizationCreate.getUnitCode()!=null && reqOrganizationCreate.getLevel()==null) {
			organizationCreate.setLevel(autoDetectDefaultByUnitCode(reqOrganizationCreate.getUnitCode()).getKey());
		}
		
		try {
			return organizationRepository.save(organizationCreate);
		} catch (Exception e) {
			if(e instanceof DuplicateKeyException) {
				throw new DuplicateKeyExceptionAdvice("Dữ liệu bị trùng khóa chính, vui lòng thử lại");
			}
			throw e;
		}
	}

	@Override
	public Organization createOrganization(ReqOrganizationCreatePartner reqOrganizationCreatePartner, User creator) {
		Organization parentOrganization=null;
		if(reqOrganizationCreatePartner.getParentUnitCode()!=null) {
			parentOrganization=getOrganizationByUnitCode(reqOrganizationCreatePartner.getParentUnitCode());
		}
		
		Organization organizationCreate=new Organization();
		organizationCreate.setId(new ObjectId());
		organizationCreate.setCreatedTime(new Date());
		organizationCreate.setUpdatedTime(new Date());
		organizationCreate.setName(reqOrganizationCreatePartner.getName());
		organizationCreate.setDescription(reqOrganizationCreatePartner.getDescription());
		if(parentOrganization!=null) {
			organizationCreate.setParentId(parentOrganization.getId());
		}
		organizationCreate.setActive(reqOrganizationCreatePartner.isActive());
		if(creator!=null) {
			organizationCreate.setCreatorId(creator.getId());
			organizationCreate.setCreatorName(creator.getFullName());
		}
		organizationCreate.setUnitCode(reqOrganizationCreatePartner.getUnitCode());
		organizationCreate.setOrganizationCategoryId(null);
		organizationCreate.setOrder(reqOrganizationCreatePartner.getOrder());
		organizationCreate.setSyncExternal(true);
		organizationCreate.setPath(getPathOrganization(organizationCreate));
		try {
			return organizationRepository.save(organizationCreate);
		} catch (Exception e) {
			if(e instanceof DuplicateKeyException) {
				throw new DuplicateKeyExceptionAdvice("Dữ liệu bị trùng khóa chính, vui lòng thử lại");
			}
			throw e;
		}
	}

	@Override
	public Organization updateOrganization(String organizationId, ReqOrganizationUpdate reqOrganizationUpdate) {
		Organization organizationUpdate=getOrganizationById(organizationId);
		organizationUpdate.setName(reqOrganizationUpdate.getName());
		organizationUpdate.setDescription(reqOrganizationUpdate.getDescription());
		organizationUpdate.setParentId(reqOrganizationUpdate.getParentId());
		organizationUpdate.setOrder(reqOrganizationUpdate.getOrder());
		organizationUpdate.setActive(reqOrganizationUpdate.isActive());
		organizationUpdate.setUnitCode(reqOrganizationUpdate.getUnitCode());
		organizationUpdate.setOrganizationCategoryId(reqOrganizationUpdate.getOrganizationCategoryId());
		
		organizationUpdate.setLevel(reqOrganizationUpdate.getLevel());
		if(reqOrganizationUpdate.getUnitCode()!=null && reqOrganizationUpdate.getLevel()==null) {
			organizationUpdate.setLevel(autoDetectDefaultByUnitCode(organizationUpdate.getUnitCode()).getKey());
		}
		
		organizationUpdate = organizationRepository.save(organizationUpdate);
		organizationUpdate.getAllUserIds().stream().forEach(userId->{
			userService.updateOrganizationOfUser(userId);
		});
		return organizationUpdate;
	}

	@Override
	public Organization updateOrganization(String unitCode, ReqOrganizationUpdatePartner reqOrganizationUpdatePartner) {
		Organization parentOrganization=null;
		if(reqOrganizationUpdatePartner.getParentUnitCode()!=null) {
			parentOrganization=getOrganizationByUnitCode(reqOrganizationUpdatePartner.getParentUnitCode());
		}
		
		Organization organizationUpdate=getOrganizationByUnitCode(unitCode);
		organizationUpdate.setName(reqOrganizationUpdatePartner.getName());
		organizationUpdate.setDescription(reqOrganizationUpdatePartner.getDescription());
		if(parentOrganization!=null) {
			organizationUpdate.setParentId(parentOrganization.getId());
		}else {
			organizationUpdate.setParentId(null);
		}
		organizationUpdate.setOrder(reqOrganizationUpdatePartner.getOrder());
		organizationUpdate.setActive(reqOrganizationUpdatePartner.isActive());
		organizationUpdate.setOrganizationCategoryId(null);
		organizationUpdate.setSyncExternal(true);
		
		organizationUpdate = organizationRepository.save(organizationUpdate);
		organizationUpdate.getAllUserIds().stream().forEach(userId->{
			userService.updateOrganizationOfUser(userId);
		});
		
		return organizationUpdate;
	}

	@Override
	public String getPathOrganization(Organization organization) {
		try {
			return generalPath(new LinkedList<String>(), organization);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private String generalPath(LinkedList<String> paths, Organization childOrganization) throws Exception{
		paths.add(childOrganization.getId());
		
		/* Nếu là ROOT */
		if(childOrganization.isRoot()) {
			String result="";
			for (int i=paths.size()-1;i>=0;i--) {
				if(i==0) {
					result+=paths.get(i);
				}else {
					result+=paths.get(i)+"/";
				}
			}
			return result;
		}
		
		/* Ngược lại thì tiếp tục truy vấn parentId */
		try {
			Optional<Organization> findparentOrganization=findOrganizationById(childOrganization.getParentId());
			if(findparentOrganization.isPresent()) {
				return generalPath(paths, findparentOrganization.get());
			}else {
				String result="";
				for (int i=paths.size()-1;i>=0;i--) {
					if(i==0) {
						result+=paths.get(i);
					}else {
						result+=paths.get(i)+"/";
					}
				}
				return result;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			String result="";
			for (int i=paths.size()-1;i>=0;i--) {
				if(i==0) {
					result+=paths.get(i);
				}else {
					result+=paths.get(i)+"/";
				}
			}
			return result;
		}
	}

	@Override
	public List<Organization> findOrganizationAll() {
		return organizationRepository.findAll();
	}

	@Override
	public long countOrganizationAll(OrganizationFilter organizationFilter) {
		return organizationRepositoryCustom.countAll(organizationFilter);
	}

	@Override
	public List<Organization> findOrganizationAll(OrganizationFilter organizationFilter) {
		return organizationRepositoryCustom.findAll(organizationFilter);
	}

	@Override
	public Optional<Organization> findOne(OrganizationFilter organizationFilter) {
		return organizationRepositoryCustom.findOne(organizationFilter);
	}

	
	@Override
	public Organization addUsersToOrganization(String organizationId, ReqUserOrganizationAdds reqUserOrganizationAdds) {
		return addUsersToOrganization(organizationId, reqUserOrganizationAdds.getUserIds(), AddFromSource.admin);
	}

	@Override
	public ResUserOrganizationAddsPartner addUsersToOrganizationPartner(String organizationCode, ReqUserOrganizationAddsPartner reqUserOrganizationAddsPartner) {
		/* Lấy đơn vị bởi unitCode */
		Organization organizationUpdate=getOrganizationByUnitCode(organizationCode);
		String organizationId=organizationUpdate.getId();
		
		/* Tự thêm user mới từ LDAP nếu không tồn tại */
		if(otpService.isEnable()) {
			/* Khởi tạo threads */
			ExecutorService executor = Executors.newFixedThreadPool(reqUserOrganizationAddsPartner.getUserNames().size());
			List<Future<Optional<User>>> listFuture = new ArrayList<Future<Optional<User>>>();
			
			for(String userName: reqUserOrganizationAddsPartner.getUserNames()) {
				/* Kiểm tra trong DB trước */
				Optional<User> findUserLocal=userService.findUserByUserName(userName);
				/* Nếu chưa tồn tại thì thêm vào hệ thống */
				if(!findUserLocal.isPresent()) {
					/* Đưa vào thread pool cho thực thi */
					listFuture.add(executor.submit(new Callable<Optional<User>>() {
						@Override
						public Optional<User> call() throws Exception {
							return userService.addUserByUsernameLdap(userName);
						}
					}));
				}
			}
			
			/* Thu hoạch từ pool threads */
			for (Future<Optional<User>> future : listFuture) {
				try {
					future.get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
			
			/* Tắt pool */
			executor.shutdown();
		}
		
		/* Chuyển userName thành userId để tái sử dụng lại hàm */
		UserFilter userFilter=new UserFilter();
		userFilter.setUserNames(reqUserOrganizationAddsPartner.getUserNames());
		List<User> users=userService.findUserAll(userFilter);
		List<String> userIds=users.stream().map(e->e.getId()).collect(Collectors.toList());
		
		/* Sử dụng lại hàm local */
		organizationUpdate = addUsersToOrganization(organizationId, userIds, AddFromSource.partner);
		
		/* Tổng hợp lại kết quả */
		ResUserOrganizationAddsPartner result=new ResUserOrganizationAddsPartner();
		/* Đơn vị sau khi đã cập nhật */
		result.setOrganization(organizationUpdate);
		/* Giao của 2 mảng, những phần tử có ở cả 2 mang -> thêm vào thành công*/
		result.setSuccesses((List<String>) CollectionUtils.intersection(reqUserOrganizationAddsPartner.getUserNames(), organizationUpdate.getAllUserNames()));
		/* Loại bỏ những phần tử trong mảng A, những phần tử mà A có B không có */
		result.setFailes((List<String>) CollectionUtils.subtract(reqUserOrganizationAddsPartner.getUserNames(), organizationUpdate.getAllUserNames()));
		return result;
	}

	@Override
	public Organization addUsersToOrganization(String organizationId, List<String> userIds, AddFromSource addFromSource) {
		Organization organizationUpdate=getOrganizationById(organizationId);
		LinkedList<UserOrganizationExpand> userOrganizationExpands = organizationUpdate.getUserOrganizationExpands();
		for(String userId:userIds) {
			if(userOrganizationExpands.stream().filter(e->e.getUserId().equals(userId)).count()==0) {
				Optional<User> findUser=userService.findUserById(userId);
				if(findUser.isPresent()) {
					User user=findUser.get();
					
					UserOrganizationExpand userOrganizationExpand=new UserOrganizationExpand();
					userOrganizationExpand.setUserId(user.getId());
					userOrganizationExpand.setCreatedTime(new Date());
					userOrganizationExpand.setUpdatedTime(new Date());
					userOrganizationExpand.setUserName(user.getUsername());
					userOrganizationExpand.setFullName(user.getFullName());
					userOrganizationExpand.setPositionName(user.getJobTitle());
					userOrganizationExpand.setActive(true);
					userOrganizationExpand.setAddFromSource(addFromSource);
					
					userOrganizationExpands.add(userOrganizationExpand);
				}
			}
		}
		organizationUpdate = organizationRepository.save(organizationUpdate);
		for(String userId:userIds) {
			userService.updateOrganizationOfUser(userId);
		}
		return organizationUpdate;
	}

	@Override
	public Organization removeUsersToOrganization(String organizationId, ReqUserOrganizationRemoves reqUserOrganizationRemoves) {
		return removeUsersToOrganization(organizationId, reqUserOrganizationRemoves.getUserIds());
	}

	@Override
	public ResUserOrganizationRemovesPartner removeUsersToOrganizationPartner(String organizationCode, ReqUserOrganizationRemovesPartner reqUserOrganizationRemovesPartner) {
		/* Lấy đơn vị bởi unitCode */
		Organization organizationUpdate=getOrganizationByUnitCode(organizationCode);
		String organizationId=organizationUpdate.getId();
		
		/* Chuyển userName thành userId để tái sử dụng lại hàm */
		UserFilter userFilter=new UserFilter();
		userFilter.setUserNames(reqUserOrganizationRemovesPartner.getUserNames());
		List<User> users=userService.findUserAll(userFilter);
		List<String> userIds=users.stream().map(e->e.getId()).collect(Collectors.toList());
		
		/* Sử dụng lại hàm local */
		organizationUpdate = removeUsersToOrganization(organizationId, userIds);
		
		/* Tổng hợp lại kết quả */
		ResUserOrganizationRemovesPartner result=new ResUserOrganizationRemovesPartner();
		/* Kết quả của Đơn vị sau khi cập nhật */
		
		result.setOrganization(organizationUpdate);
		/* Loại bỏ những phần tử trong mảng A, những phần tử mà A có B không có */
		result.setSuccesses((List<String>) CollectionUtils.subtract(reqUserOrganizationRemovesPartner.getUserNames(), organizationUpdate.getAllUserNames()));
		
		/* Giao của 2 mảng, những phần tử có ở cả 2 mang -> thêm vào thành công*/
		result.setFailes((List<String>) CollectionUtils.intersection(reqUserOrganizationRemovesPartner.getUserNames(), organizationUpdate.getAllUserNames()));
		return result;
	}

	@Override
	public Organization removeUsersToOrganization(String organizationId, List<String> userIdsRemove) {
		Organization organizationUpdate=getOrganizationById(organizationId);
		LinkedList<UserOrganizationExpand> userOrganizationExpands = organizationUpdate.getUserOrganizationExpands();
		
		/*
		 * Kiểm tra từng userId của Đơn vị có liên quan đến Docs hoặc Tasks bất kỳ nào
		 * không?
		 */
		List<String> userIdsCanRemove=new ArrayList<>();
		for (String userId : userIdsRemove) {
			boolean isCanRemove=true;
			if(isCanRemove && docService.isReferenceAnyDoc(organizationId, userId)) {
				isCanRemove=false;
			}
			
			if(isCanRemove && taskService.isReferenceAnyTask(organizationId, userId)) {
				isCanRemove=false;
			}
			
			if(isCanRemove) {
				userIdsCanRemove.add(userId);
			}
		}
		
		/* Danh sách đối tượng có thể xóa trong danh sách Người dùng hiện tại của Đơn vị */
		List<UserOrganizationExpand> userOrganizationExpandsRemove = userOrganizationExpands.stream().filter(e->userIdsCanRemove.contains(e.getUserId())).toList();
		
		/* Xóa các đối tượng cần xóa ra khỏi danh sách Người dùng của Đơn vị */
		organizationUpdate.getUserOrganizationExpands().removeAll(userOrganizationExpandsRemove);
		
		/* Cập nhật lại danh sách users trong Tổ và các Vai trò */
		List<String> userIdsCurrent=organizationUpdate.getUserOrganizationExpands().stream().map(e->e.getUserId()).toList();
		
		/* Cập nhật lại Người dùng trong Tổ */
		for(GroupOrganizationExpand groupOrganizationExpand:organizationUpdate.getGroupOrganizationExpands()) {
			/*Lọc ra danh sách Người dùng trong Tổ thuộc Người dùng trong Đơn vị*/
			LinkedList<String> userIdsGroup = groupOrganizationExpand.getUserIds().stream().filter(e->userIdsCurrent.contains(e)).collect(Collectors.toCollection(LinkedList::new));
			
			/* Cập nhật lại danh sách Người dùng cho Tổ */
			groupOrganizationExpand.setUserIds(userIdsGroup);
		}
		
		/* Cập nhật lại Người dùng trong Vai trò */
		for(RoleOrganizationExpand roleOrganizationExpand:organizationUpdate.getRoleOrganizationExpands()) {
			/* Lọc ra danh sách Người dùng trong Đơn vị thuộc Người dùng trong Đơn vị */
			LinkedList<String> userIdsRole = roleOrganizationExpand.getUserIds().stream().filter(e->userIdsCurrent.contains(e)).collect(Collectors.toCollection(LinkedList::new));
			
			/* Cập nhật lại danh sách Người dùng cho Vai trò */
			roleOrganizationExpand.setUserIds(userIdsRole);
		}
		
		/* Cập nhật đơn vị */
		organizationUpdate = organizationRepository.save(organizationUpdate);
		for (String userId : userIdsRemove) {
			userService.updateOrganizationOfUser(userId);
		}
		
		return organizationUpdate;
	}

	@Override
	public Organization updateUserToOrganization(String organizationId, String userId, ReqUserOrganizationUpdate reqUserOrganizationUpdate) {
		Organization organizationUpdate=getOrganizationById(organizationId);
		organizationUpdate.getUserOrganizationExpands().stream().filter(e->e.getUserId().equals(userId)).forEach(item->{
			item.setPositionName(reqUserOrganizationUpdate.getPositionName());
			item.setAccountIOffice(reqUserOrganizationUpdate.getAccountIOffice());
			item.setActive(reqUserOrganizationUpdate.isActive());
		});
		return save(organizationUpdate);
	}

	
	
	
	
	
	
	
	
	
	
	
	@Override
	public Organization createGroupToOrganization(String organizationId, ReqGroupOrganizationCreate reqGroupOrganizationCreate, User creator) {
		Organization organizationUpdate=getOrganizationById(organizationId);
		GroupOrganizationExpand groupOrganizationExpand=new GroupOrganizationExpand();
		groupOrganizationExpand.setGroupId(new ObjectId().toHexString());
		groupOrganizationExpand.setCreatedTime(new Date());
		groupOrganizationExpand.setUpdatedTime(new Date());
		groupOrganizationExpand.setName(reqGroupOrganizationCreate.getName());
		groupOrganizationExpand.setDescription(reqGroupOrganizationCreate.getDescription());
		if(creator!=null) {
			groupOrganizationExpand.setCreatorId(creator.getId());
			groupOrganizationExpand.setCreatorName(creator.getFullName());
		}
		groupOrganizationExpand.setUserIds(validUsersInGroup(organizationUpdate, groupOrganizationExpand.getGroupId(), reqGroupOrganizationCreate.getUserIds()));
		
		organizationUpdate.getGroupOrganizationExpands().add(groupOrganizationExpand);
		return organizationRepository.save(organizationUpdate);
	}

	@Override
	public Organization updateGroupFromOrganization(String organizationId, String groupId, ReqGroupOrganizationUpdate reqGroupOrganizationUpdate) {
		Organization organizationUpdate=getOrganizationById(organizationId);
		for(GroupOrganizationExpand groupOrganizationExpand:organizationUpdate.getGroupOrganizationExpands()) {
			if(groupOrganizationExpand.getGroupId().equalsIgnoreCase(groupId)) {
				/* Cập nhật thông tin */
				groupOrganizationExpand.setName(reqGroupOrganizationUpdate.getName());
				groupOrganizationExpand.setDescription(reqGroupOrganizationUpdate.getDescription());
				
				groupOrganizationExpand.setUserIds(validUsersInGroup(organizationUpdate, groupId, reqGroupOrganizationUpdate.getUserIds()));
				return organizationRepository.save(organizationUpdate);
			}
		}
		throw new NotFoundElementExceptionAdvice("Không tìm thấy nhóm");
	}

	@Override
	public Organization deleteGroupFromOrganization(String organizationId, String groupId) {
		Organization organizationUpdate=getOrganizationById(organizationId);
		for(GroupOrganizationExpand groupOrganizationExpand:organizationUpdate.getGroupOrganizationExpands()) {
			if(groupOrganizationExpand.getGroupId().equalsIgnoreCase(groupId)) {
				if(groupOrganizationExpand.getUserIds().size()==0) {
					organizationUpdate.getGroupOrganizationExpands().remove(groupOrganizationExpand);
					return organizationRepository.save(organizationUpdate);
				}
				throw new NotAcceptableExceptionAdvice("Không thể xóa tổ giúp việc này, vì đang có người dùng");
			}
		}  
		throw new NotFoundElementExceptionAdvice("Không tìm thấy nhóm");
	}
	
	@Override
	public Organization findOrganizationGroupById(String organizationId, String groupId) {
		Organization organization=getOrganizationById(organizationId);
		for(GroupOrganizationExpand groupOrganizationExpand:organization.getGroupOrganizationExpands()) {
			if(groupOrganizationExpand.getGroupId().equalsIgnoreCase(groupId)) {
				return organization;
			}
		}
		throw new NotFoundElementExceptionAdvice("Không tìm thấy nhóm");
	}

	private LinkedList<String> validUsersInGroup(Organization organization, String groupId, LinkedList<String> userIds){
		LinkedList<String> validUserIds=new LinkedList<String>();
		for(String userId:userIds) {
			boolean valid=false;
			/* Phải là trong tổ chức */
			for(UserOrganizationExpand userOrganizationExpand:organization.getUserOrganizationExpands()) {
				if(userOrganizationExpand.getUserId().equalsIgnoreCase(userId)) {
					valid=true;
					break;
				}
			}
			
			/* Không tồn tại trong các phòng khác */
			for(GroupOrganizationExpand groupOrganizationExpand:organization.getGroupOrganizationExpands()) {
				if(!groupOrganizationExpand.getGroupId().equalsIgnoreCase(groupId) && groupOrganizationExpand.getUserIds().contains(userId)) {
					valid=false;
					break;
				}
			}
			
			if(valid) {
				validUserIds.add(userId);
			}
		}
		return validUserIds;
	}
	
	
	
	
	
	
	
	
	
	
	@Override
	public Organization createRoleOrganization(String organizationId, ReqRoleOrganizationCreate reqRoleOrganizationCreate, User creator) {
		Organization organizationUpdate=getOrganizationById(organizationId);
		RoleOrganizationExpand roleOrganizationExpand=new RoleOrganizationExpand();
		roleOrganizationExpand.setRoleId(new ObjectId().toHexString());
		roleOrganizationExpand.setCreatedTime(new Date());
		roleOrganizationExpand.setUpdatedTime(new Date());
		roleOrganizationExpand.setName(reqRoleOrganizationCreate.getName());
		roleOrganizationExpand.setDescription(reqRoleOrganizationCreate.getDescription());
		if(creator!=null) {
			roleOrganizationExpand.setCreatorId(creator.getId());
			roleOrganizationExpand.setCreatorName(creator.getFullName());
		}
		roleOrganizationExpand.setPermissionKeys(reqRoleOrganizationCreate.getPermissionKeys());
		roleOrganizationExpand.setUserIds(reqRoleOrganizationCreate.getUserIds());
		roleOrganizationExpand.setRoleTemplateId(reqRoleOrganizationCreate.getRoleTemplateId());
		
		organizationUpdate.getRoleOrganizationExpands().add(roleOrganizationExpand);
		return organizationRepository.save(organizationUpdate);
	}

	@Override
	public Organization updateRoleOrganization(String organizationId, String roleId, ReqRoleOrganizationUpdate reqRoleOrganizationUpdate) {
		Organization organizationUpdate=getOrganizationById(organizationId);
		for(RoleOrganizationExpand roleOrganizationExpand:organizationUpdate.getRoleOrganizationExpands()) {
			if(roleOrganizationExpand.getRoleId().equalsIgnoreCase(roleId)) {
				/* Cập nhật thông tin */
				roleOrganizationExpand.setName(reqRoleOrganizationUpdate.getName());
				roleOrganizationExpand.setDescription(reqRoleOrganizationUpdate.getDescription());
				roleOrganizationExpand.setPermissionKeys(reqRoleOrganizationUpdate.getPermissionKeys());
				roleOrganizationExpand.setUserIds(reqRoleOrganizationUpdate.getUserIds());
				roleOrganizationExpand.setRoleTemplateId(reqRoleOrganizationUpdate.getRoleTemplateId());
				
				return organizationRepository.save(organizationUpdate);
			}
		}
		throw new NotFoundElementExceptionAdvice("Không tìm thấy vai trò");
	}

	@Override
	public Organization deleteRoleOrganization(String organizationId, String roleId) {
		Organization organizationUpdate=getOrganizationById(organizationId);
		for(RoleOrganizationExpand roleOrganizationExpand:organizationUpdate.getRoleOrganizationExpands()) {
			if(roleOrganizationExpand.getRoleId().equalsIgnoreCase(roleId)) {
				if(roleOrganizationExpand.getUserIds().size()==0) {
					organizationUpdate.getRoleOrganizationExpands().remove(roleOrganizationExpand);
					return organizationRepository.save(organizationUpdate);
				}
				throw new NotAcceptableExceptionAdvice("Không thể xóa vai trò này, vì đang có người dùng");
			}
		}
		throw new NotFoundElementExceptionAdvice("Không tìm thấy vai trò");
	}

	@Override
	public Organization findOrganizationRoleById(String organizationId, String roleId) {
		Organization organization=getOrganizationById(organizationId);
		for(RoleOrganizationExpand roleOrganizationExpand:organization.getRoleOrganizationExpands()) {
			if(roleOrganizationExpand.getRoleId().equalsIgnoreCase(roleId)) {
				return organization;
			}
		}
		throw new NotFoundElementExceptionAdvice("Không tìm thấy vai trò");
	}

	@Override
	public Organization save(Organization organization) {
		return organizationRepository.save(organization);
	}

	@Override
	public void updateReference() {
		List<Organization> organizations=organizationRepository.findAll();
		for (Organization organization : organizations) {
			List<String> usersOrganization=organization.getUserOrganizationExpands().stream().map(e->e.getUserId()).toList();
			
			/* Cập nhật Groups */
			for(GroupOrganizationExpand groupOrganizationExpand:organization.getGroupOrganizationExpands()) {
				groupOrganizationExpand.setUserIds(groupOrganizationExpand.getUserIds().stream().filter(e->usersOrganization.contains(e)).collect(Collectors.toCollection(LinkedList::new)));
			}
			
			/* Cập nhật Roles */
			for(RoleOrganizationExpand roleOrganizationExpand:organization.getRoleOrganizationExpands()) {
				roleOrganizationExpand.setUserIds(roleOrganizationExpand.getUserIds().stream().filter(e->usersOrganization.contains(e)).collect(Collectors.toCollection(LinkedList::new)));
			}
			
			save(organization);
		}
	}

	@Override
	public Optional<Organization> findOrganizationByUnitCode(String unitCode) {
		return organizationRepository.findByUnitCode(unitCode);
	}

	@Override
	public Organization getOrganizationByUnitCode(String unitCode) {
		Assert.notNull(unitCode, "unitCode không được null");
		Optional<Organization> organization = findOrganizationByUnitCode(unitCode);
		if(organization.isPresent()) {
			return organization.get();
		}
		throw new NotFoundElementExceptionAdvice("Không tìm thấy tổ chức");
	}

	@Override
	public Organization updateRoleOrganizationExpandByRoleTemplate(Organization organization, RoleTemplate roleTemplate) {
		try {
			for(RoleOrganizationExpand roleOrganizationExpand:organization.getRoleOrganizationExpands()) {
				if(roleOrganizationExpand.getRoleTemplateId()!=null && roleOrganizationExpand.getRoleTemplateId().equals(roleTemplate.getId())) {
					roleOrganizationExpand.setPermissionKeys(roleTemplate.getPermissionKeys());
					roleOrganizationExpand.setUpdatedTime(new Date());
				}
			}
			return save(organization);
		} catch (Exception e) {
			e.printStackTrace();
			throw new NotAcceptableExceptionAdvice("Không cập nhật vai trò mẫu cho Đơn vị được");
		}
	}

	@Override
	public long countSubOrganization(Organization organization) {
		OrganizationFilter organizationFilter=new OrganizationFilter();
		organizationFilter.setParentId(organization.getId());
		return countOrganizationAll(organizationFilter);
	}

	@Override
	public boolean addUserToGroupOrganization(String organizationId, String groupId, String userId) {
		Organization organizationUpdate=getOrganizationById(organizationId);
		
		/* Kiểm tra nếu userId chưa có trong đơn vị thì phải thêm vào trong đơn vị */
		if(organizationUpdate.getUserOrganizationExpands().stream().filter(e->e.getUserId().equals(userId)).count()==0) {
			organizationUpdate=addUsersToOrganization(organizationId, Arrays.asList(userId), AddFromSource.admin);
		}
		
		for(GroupOrganizationExpand groupOrganizationExpand:organizationUpdate.getGroupOrganizationExpands()) {
			if(groupOrganizationExpand.getGroupId().equals(groupId)) {
				if(!groupOrganizationExpand.getUserIds().contains(userId)) {
					groupOrganizationExpand.getUserIds().add(userId);
					organizationRepository.save(organizationUpdate);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean removeUserToGroupOrganization(String organizationId, String groupId, String userId) {
		Organization organizationUpdate=getOrganizationById(organizationId);
		for(GroupOrganizationExpand groupOrganizationExpand:organizationUpdate.getGroupOrganizationExpands()) {
			if(groupOrganizationExpand.getGroupId().equals(groupId)) {
				if(groupOrganizationExpand.getUserIds().contains(userId)) {
					groupOrganizationExpand.getUserIds().remove(userId);
					
					organizationRepository.save(organizationUpdate);
					userService.updateOrganizationOfUser(userId);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean addUserToRoleOrganization(String organizationId, String roleId, String userId) {
		Organization organizationUpdate=getOrganizationById(organizationId);
		User user=userService.getUserById(userId);
		
		/* Kiểm tra nếu userId chưa có trong đơn vị thì phải thêm vào trong đơn vị */
		if(organizationUpdate.getUserOrganizationExpands().stream().filter(e->e.getUserId().equals(userId)).count()==0) {
			organizationUpdate=addUsersToOrganization(organizationId, Arrays.asList(userId), AddFromSource.admin);
		}
		
		for(RoleOrganizationExpand roleOrganizationExpand:organizationUpdate.getRoleOrganizationExpands()) {
			if(roleOrganizationExpand.getRoleId().equals(roleId)) {
				if(!roleOrganizationExpand.getUserIds().contains(userId)) {
					roleOrganizationExpand.getUserIds().add(userId);
					
					/* Gán người dùng vào vai trò trong casbin */
					for(String permissionKey:roleOrganizationExpand.getPermissionKeys()) {
						casbinAuthService.addRoleForUser(user.getUsername(), permissionKey);
					}
					
					organizationRepository.save(organizationUpdate);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean removeUserToRoleOrganization(String organizationId, String roleId, String userId) {
		Organization organization=getOrganizationById(organizationId);
		User user=userService.getUserById(userId);
		
		for(RoleOrganizationExpand roleOrganizationExpand:organization.getRoleOrganizationExpands()) {
			if(roleOrganizationExpand.getRoleId().equals(roleId)) {
				if(roleOrganizationExpand.getUserIds().contains(userId)) {
					roleOrganizationExpand.getUserIds().remove(userId);
					
					/* Bỏ gán người dùng vào vai trò trong casbin */
					for(String permissionKey:roleOrganizationExpand.getPermissionKeys()) {
						casbinAuthService.removeRoleForUser(user.getUsername(), permissionKey);
					}
					
					organizationRepository.save(organization);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean addUserToOrganizationFirstLogin(ReqUserLoginAddToOrganization reqUserLoginAddToOrganization, String userId) {
		Organization organization=getOrganizationById(reqUserLoginAddToOrganization.getOrganizationId());
		User user=userService.getUserById(userId);
		
		FirstReview firstReview=new FirstReview();
		firstReview.setChoiceOrganizationId(organization.getId());
		
		/* Kiểm tra nếu userId chưa có trong đơn vị thì phải thêm vào trong đơn vị */
		if(organization.getUserOrganizationExpands().stream().filter(e->e.getUserId().equals(userId)).count()==0) {
			organization=addUsersToOrganization(reqUserLoginAddToOrganization.getOrganizationId(), Arrays.asList(userId), AddFromSource.self);
		}
		
		/* Thêm vào group nếu có set */
		if(reqUserLoginAddToOrganization.getGroupId()!=null) {
			addUserToGroupOrganization(organization.getId(), reqUserLoginAddToOrganization.getGroupId(), user.getId());
			firstReview.setChoiceOrganizationGroupId(reqUserLoginAddToOrganization.getGroupId());
		}
		
		/* Thêm vào vai trò nếu có set */
		if(reqUserLoginAddToOrganization.getRoleId()!=null) {
			addUserToRoleOrganization(organization.getId(), reqUserLoginAddToOrganization.getRoleId(), user.getId());
			firstReview.setChoiceOrganizationRoleId(reqUserLoginAddToOrganization.getRoleId());
		}
		
		/* Lưu organizationUpdate lại */
		organization = organizationRepository.save(organization);
		
		/* Lưu lại firstReview cho tài khoản đó, đồng thời update lại belongOrganizations */
		user.setFirstReview(firstReview);
		userService.saveUser(user);
		
		return true;
	}

	@Override
	public List<Organization> getListOrganizationOfUser(String userId) {
		OrganizationFilter organizationFilter=new OrganizationFilter();
		organizationFilter.setActive(true);
		organizationFilter.setIncludeUserId(userId);
		return findOrganizationAll(organizationFilter);
	}

	@Override
	public List<String> getChildOrganizationsAllLevel(Organization organization) {
		List<String> ids=new ArrayList<>();
		ids.add(organization.getId());
		
		OrganizationFilter organizationFilter=new OrganizationFilter();
		organizationFilter.setParentId(organization.getId());
		List<Organization> organizations=findOrganizationAll(organizationFilter);
		if(organizations.size()>0) {
			List<String> childTasksDocument=new ArrayList<String>();
			
			/* Khởi tạo threads */
			ExecutorService executor = Executors.newFixedThreadPool(organizations.size());
			List<Future<List<String>>> listFuture = new ArrayList<Future<List<String>>>();

			for (Organization child : organizations) {
				listFuture.add(executor.submit(new Callable<List<String>>() {
					@Override
					public List<String> call() throws Exception {
						return getChildOrganizationsAllLevel(child);
					}
				}));
			}
			
			for (Future<List<String>> future : listFuture) {
				try {
					childTasksDocument.addAll(future.get());
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
			
			executor.shutdown();
			ids.addAll(childTasksDocument);
		}
		return ids;
	}
	
}
