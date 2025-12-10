package ws.core.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.validation.Valid;
import ws.core.advice.DuplicateKeyExceptionAdvice;
import ws.core.advice.NotAcceptableExceptionAdvice;
import ws.core.advice.NotFoundElementExceptionAdvice;
import ws.core.advice.ObjectIdExceptionAdvance;
import ws.core.enums.DocAction;
import ws.core.enums.DocSource;
import ws.core.enums.DocStatus;
import ws.core.enums.TaskStatus;
import ws.core.model.Doc;
import ws.core.model.Organization;
import ws.core.model.User;
import ws.core.model.embeded.Creator;
import ws.core.model.embeded.DocOwner;
import ws.core.model.embeded.DocReceiver;
import ws.core.model.embeded.DocResultConfirm;
import ws.core.model.filter.DocFilter;
import ws.core.model.filter.OrganizationFilter;
import ws.core.model.filter.TaskFilter;
import ws.core.model.filter.embeded.DocUserRefFilter;
import ws.core.model.request.ReqDocConfirmComplete;
import ws.core.model.request.ReqDocCreate;
import ws.core.model.request.ReqDocCreatePartner;
import ws.core.model.request.ReqDocUpdate;
import ws.core.model.request.ReqDocUpdatePartner;
import ws.core.respository.DocRepository;
import ws.core.respository.DocRepositoryCustom;
import ws.core.services.DocHistoryService;
import ws.core.services.DocService;
import ws.core.services.OrganizationService;
import ws.core.services.TaskService;
import ws.core.services.UserService;
import ws.core.util.DateTimeUtil;

@Service
public class DocServiceImpl implements DocService{

	@Autowired
	private DocRepository docRepository;
	
	@Autowired
	private DocRepositoryCustom docRepositoryCustom;
	
	@Autowired
	private DocHistoryService docHistoryService;
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private UserService userService; 
	
	@Override
	public long countDocAll(DocFilter docFilter) {
		return docRepositoryCustom.countAll(docFilter);
	}

	@Override
	public List<Doc> findDocAll(DocFilter docFilter) {
		return docRepositoryCustom.findAll(docFilter);
	}

	@Override
	public Optional<Doc> findDocById(String id) {
		if(ObjectId.isValid(id))
			return docRepository.findById(new ObjectId(id));
		throw new ObjectIdExceptionAdvance("docId ["+id+"] không hợp lệ");
	}

	@Override
	public Optional<Doc> findDocByIOfficeId(String iofficeId) {
		return docRepository.findByiOfficeId(iofficeId);
	}

	@Override
	public Doc getDocById(String id) {
		Optional<Doc> findDoc = findDocById(id);
		if(findDoc.isPresent()) {
			return findDoc.get();
		}
		throw new NotFoundElementExceptionAdvice("docId ["+id+"] không tồn tại trong hệ thống");
	}

	@Override
	public Doc getDocByIOfficeId(String iofficeId) {
		Optional<Doc> findDoc = findDocByIOfficeId(iofficeId);
		if(findDoc.isPresent()) {
			return findDoc.get();
		}
		throw new NotFoundElementExceptionAdvice("iofficeId ["+iofficeId+"] không tồn tại trong hệ thống");
	}

	@Override
	public Doc deleteDocById(String id) {
		Doc doc=getDocById(id);
		docRepository.delete(doc);
		return doc;
	}

	@Override
	public Doc createDoc(ReqDocCreate reqDocCreate) {
		Doc doc=new Doc();
		doc.setId(ObjectId.get());
		doc.setCreatedTime(new Date());
		doc.setUpdatedTime(new Date());
		doc.setNumber(reqDocCreate.getNumber());
		doc.setSymbol(reqDocCreate.getSymbol());
		doc.setSecurity(reqDocCreate.getSecurity());
		doc.setRegDate(new Date(reqDocCreate.getRegDate()));
		doc.setType(reqDocCreate.getType());
		doc.setSignerName(reqDocCreate.getSignerName());
		doc.setSignerPosition(reqDocCreate.getSignerPosition());
		doc.setCopies(reqDocCreate.getCopies());
		doc.setPages(reqDocCreate.getPages());
		doc.setOrgCreateName(reqDocCreate.getOrgCreateName());
		doc.setOrgReceiveName(reqDocCreate.getOrgReceiveName());
		doc.setSummary(reqDocCreate.getSummary());
		doc.setActive(reqDocCreate.isActive());
		doc.setCategory(reqDocCreate.getCategory());
		doc.setAttachments(reqDocCreate.getAttachments());
		doc.setClassifyTaskId(reqDocCreate.getClassifyTaskId());
		doc.setLeaderApproveTaskId(reqDocCreate.getLeaderApproveTaskId());
		
		/* Đơn vị chủ quản */
		DocOwner docOwner=new DocOwner();
		docOwner.setOrganizationId(reqDocCreate.getOwner().getOrganizationId());
		docOwner.setOrganizationName(reqDocCreate.getOwner().getOrganizationName());
		docOwner.setOrganizationGroupId(reqDocCreate.getOwner().getOrganizationGroupId());
		docOwner.setOrganizationGroupName(reqDocCreate.getOwner().getOrganizationGroupName());
		docOwner.setOrganizationUserId(reqDocCreate.getOwner().getOrganizationUserId());
		docOwner.setOrganizationUserName(reqDocCreate.getOwner().getOrganizationUserName());
		doc.setOwner(docOwner);
		
		/* Đơn vị nhận */
		if(reqDocCreate.getReceivers().size()>0) {
			List<DocReceiver> receivers = reqDocCreate.getReceivers().stream().map(e->{
				DocReceiver docReceiver=new DocReceiver();
				docReceiver.setOrganizationId(e.getOrganizationId());
				docReceiver.setOrganizationName(e.getOrganizationName());
				docReceiver.setOrganizationUserId(e.getOrganizationUserId());
				docReceiver.setOrganizationUserName(e.getOrganizationUserName());
				
				Optional<Organization> findOrganization=organizationService.findOrganizationById(e.getOrganizationId());
				if(findOrganization.isPresent()) {
					docReceiver.setOrganizationCode(findOrganization.get().getUnitCode());
				}
				return docReceiver;
			}).collect(Collectors.toList());
			doc.setReceivers(receivers);
		}
		
		doc.setSource(DocSource.APISelf.getKey());
		
		return saveDoc(doc, DocAction.taomoi);
	}

	@Override
	public Doc createDoc(ReqDocCreatePartner reqDocCreatePartner) {
		Optional<Doc> findDoc=findDocByIOfficeId(reqDocCreatePartner.getIOfficeId());
		if(findDoc.isPresent()) {
			throw new DuplicateKeyExceptionAdvice("iOfficeId đã tồn tại trong hệ thống");
		}
		
		Organization organization = null;
		Optional<Organization> findOrganization=organizationService.findOrganizationByUnitCode(reqDocCreatePartner.getOwner().getOrganizationCode());
		if(findOrganization.isPresent()) {
			organization = findOrganization.get();
		}else {
			throw new NotFoundElementExceptionAdvice("organizationCode không tồn tại trong hệ thống");
		}
		Assert.notNull(organization, "Không thể thực hiện, vì đơn vị không tồn tại");
		
		User user = null;
		Optional<User> findUser=userService.findUserByUserName(reqDocCreatePartner.getOwner().getUsername());
		if(findUser.isPresent()) {
			user = findUser.get();
		}else {
			Optional<User> addNewUser = userService.addUserToOrganizationByUsernameLdap(reqDocCreatePartner.getOwner().getUsername(), organization);
			if(addNewUser.isPresent()) {
				user = addNewUser.get();
				
				/* Cập nhật lại organization mới sau khi thêm user mới (nếu có) */
				organization = organizationService.getOrganizationById(organization.getId());
			}else {
				throw new NotFoundElementExceptionAdvice("username không tồn tại hoặc không tự thêm vô được trong hệ thống");
			}
		}
		Assert.notNull(user, "Không thể thực hiện, vì người dùng không tồn tại");
		
		/* Kiểm tra người dùng này có ở trong đơn vị không? */
		if(!organization.hasUser(user.getId())) {
			throw new NotAcceptableExceptionAdvice("Người dùng không tồn tại trong Đơn vị, vui lòng liên hệ quản trị viên");
		}
		
		Doc doc=new Doc();
		doc.setId(ObjectId.get());
		doc.setCreatedTime(new Date());
		doc.setUpdatedTime(new Date());
		doc.setNumber(reqDocCreatePartner.getNumber());
		doc.setSymbol(reqDocCreatePartner.getSymbol());
		doc.setSecurity(reqDocCreatePartner.getSecurity());
		doc.setRegDate(new Date(reqDocCreatePartner.getRegDate()));
		doc.setType(reqDocCreatePartner.getType());
		doc.setSignerName(reqDocCreatePartner.getSignerName());
		doc.setSignerPosition(reqDocCreatePartner.getSignerPosition());
		doc.setCopies(reqDocCreatePartner.getCopies());
		doc.setPages(reqDocCreatePartner.getPages());
		doc.setOrgCreateName(reqDocCreatePartner.getOrgCreateName());
		doc.setOrgReceiveName(reqDocCreatePartner.getOrgReceiveName());
		doc.setSummary(reqDocCreatePartner.getSummary());
		doc.setActive(reqDocCreatePartner.isActive());
		doc.setCategory(reqDocCreatePartner.getCategory());
		doc.setAttachments(reqDocCreatePartner.getAttachments());
		doc.setClassifyTaskId(reqDocCreatePartner.getClassifyTaskId());
		doc.setLeaderApproveTaskId(reqDocCreatePartner.getLeaderApproveTaskId());
		
		/* Đơn vị chủ quản */
		DocOwner docOwner=new DocOwner();
		docOwner.setOrganizationId(organization.getId());
		docOwner.setOrganizationName(organization.getName());
		docOwner.setOrganizationGroupId(null);
		docOwner.setOrganizationGroupName(null);
		docOwner.setOrganizationUserId(user.getId());
		docOwner.setOrganizationUserName(user.getFullName());
		doc.setOwner(docOwner);
		
		/* Đơn vị nhận */
		if(reqDocCreatePartner.getReceivers().size()>0) {
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.setUnitCodes(reqDocCreatePartner.getReceivers());
			
			List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
			List<DocReceiver> receivers = organizations.stream().map(e->{
				DocReceiver docReceiver=new DocReceiver();
				docReceiver.setOrganizationId(e.getId());
				docReceiver.setOrganizationCode(e.getUnitCode());
				docReceiver.setOrganizationName(e.getName());
				return docReceiver;
			}).collect(Collectors.toList());
			
			doc.setReceivers(receivers);
		}
		
		doc.setIOfficeId(reqDocCreatePartner.getIOfficeId());
		doc.setSource(DocSource.APIPartner.getKey());

		return saveDoc(doc, DocAction.taomoi);
	}

	@Override
	public Doc updateDoc(String docId, ReqDocUpdate reqDocUpdate) {
		Doc doc=getDocById(docId);
		doc.setUpdatedTime(new Date());
		doc.setNumber(reqDocUpdate.getNumber());
		doc.setSymbol(reqDocUpdate.getSymbol());
		doc.setSecurity(reqDocUpdate.getSecurity());
		doc.setRegDate(new Date(reqDocUpdate.getRegDate()));
		doc.setType(reqDocUpdate.getType());
		doc.setSignerName(reqDocUpdate.getSignerName());
		doc.setSignerPosition(reqDocUpdate.getSignerPosition());
		doc.setCopies(reqDocUpdate.getCopies());
		doc.setPages(reqDocUpdate.getPages());
		doc.setOrgCreateName(reqDocUpdate.getOrgCreateName());
		doc.setOrgReceiveName(reqDocUpdate.getOrgReceiveName());
		doc.setSummary(reqDocUpdate.getSummary());
		doc.setActive(reqDocUpdate.isActive());
		doc.setCategory(reqDocUpdate.getCategory());
		doc.setAttachments(reqDocUpdate.getAttachments());
		doc.setClassifyTaskId(reqDocUpdate.getClassifyTaskId());
		doc.setLeaderApproveTaskId(reqDocUpdate.getLeaderApproveTaskId());
		
		/* Đơn vị nhận */
		if(reqDocUpdate.getReceivers().size()>0) {
			List<DocReceiver> receivers = reqDocUpdate.getReceivers().stream().map(e->{
				DocReceiver docReceiver=new DocReceiver();
				docReceiver.setOrganizationId(e.getOrganizationId());
				docReceiver.setOrganizationName(e.getOrganizationName());
				docReceiver.setOrganizationUserId(e.getOrganizationUserId());
				docReceiver.setOrganizationUserName(e.getOrganizationUserName());
				
				Optional<Organization> findOrganization=organizationService.findOrganizationById(e.getOrganizationId());
				if(findOrganization.isPresent()) {
					docReceiver.setOrganizationCode(findOrganization.get().getUnitCode());
				}
				return docReceiver;
			}).collect(Collectors.toList());
			doc.setReceivers(receivers);
		}else {
			doc.setReceivers(Arrays.asList());
		}
		
		return saveDoc(doc, DocAction.capnhat);
	}

	@Override
	public Doc updateDoc(String docId, ReqDocUpdatePartner reqDocUpdatePartner) {
		Doc doc=getDocById(docId);
		doc.setUpdatedTime(new Date());
		doc.setNumber(reqDocUpdatePartner.getNumber());
		doc.setSymbol(reqDocUpdatePartner.getSymbol());
		doc.setSecurity(reqDocUpdatePartner.getSecurity());
		doc.setRegDate(new Date(reqDocUpdatePartner.getRegDate()));
		doc.setType(reqDocUpdatePartner.getType());
		doc.setSignerName(reqDocUpdatePartner.getSignerName());
		doc.setSignerPosition(reqDocUpdatePartner.getSignerPosition());
		doc.setCopies(reqDocUpdatePartner.getCopies());
		doc.setPages(reqDocUpdatePartner.getPages());
		doc.setOrgCreateName(reqDocUpdatePartner.getOrgCreateName());
		doc.setOrgReceiveName(reqDocUpdatePartner.getOrgReceiveName());
		doc.setSummary(reqDocUpdatePartner.getSummary());
		doc.setActive(reqDocUpdatePartner.isActive());
		doc.setCategory(reqDocUpdatePartner.getCategory());
		doc.setAttachments(reqDocUpdatePartner.getAttachments());
		doc.setClassifyTaskId(reqDocUpdatePartner.getClassifyTaskId());
		doc.setLeaderApproveTaskId(reqDocUpdatePartner.getLeaderApproveTaskId());
	
		/* Đơn vị nhận */
		if(reqDocUpdatePartner.getReceivers().size()>0) {
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.setUnitCodes(reqDocUpdatePartner.getReceivers());
			
			List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
			List<DocReceiver> receivers = organizations.stream().map(e->{
				DocReceiver docReceiver=new DocReceiver();
				docReceiver.setOrganizationId(e.getId());
				docReceiver.setOrganizationCode(e.getUnitCode());
				docReceiver.setOrganizationName(e.getName());
				return docReceiver;
			}).collect(Collectors.toList());
			
			doc.setReceivers(receivers);
		}else {
			doc.setReceivers(Arrays.asList());
		}
		
		return saveDoc(doc, DocAction.capnhat);
	}

	@Override
	public Doc saveDoc(Doc doc, DocAction docAction) {
		try {
			doc = docRepository.save(doc);
			
			/* Ghi lại lịch sử nếu có hành động cụ thể */
			if(docAction!=null) {
				saveHistory(doc, docAction.getKey(), null);
			}
			
			return doc;
		} catch (Exception e) {
			if(e instanceof DuplicateKeyException) {
				throw new DuplicateKeyExceptionAdvice("Dữ liệu bị trùng khóa chính, vui lòng thử lại");
			}
			throw e;
		}
	}

	@Override
	public long getCountTask(String docId) {
		return taskService.getCountTaskOfDoc(docId);
	}

	@Override
	public long syncCountTask(String docId) {
		Doc doc=getDocById(docId);
		int current=(int) getCountTask(docId);
		doc.setCountTask(current);
		saveDoc(doc, null);
		return doc.getCountTask();
	}

	private void saveHistory(Doc doc, String action, Creator creator) {
		Thread thread=new Thread(new Runnable() {
			@Override
			public void run() {
				docHistoryService.saveDocHistory(doc, action, creator);
			}
		});
		thread.start();
	}

	@Override
	public boolean isReferenceAnyDoc(String organizationId, String userId) {
		DocUserRefFilter docUserRefFilter=new DocUserRefFilter();
		docUserRefFilter.setOrganizationId(organizationId);
		docUserRefFilter.setOrganizationUserId(userId);
		
		DocFilter docFilter=new DocFilter();
		docFilter.setUserRefFilter(docUserRefFilter);
		
		return docRepositoryCustom.findOne(docFilter).isPresent();
	}

	@Override
	public Doc confirmComplete(String id, @Valid ReqDocConfirmComplete reqDocConfirmComplete) {
		Doc doc=getDocById(id);
		
		if(doc.getResultConfirm()==null && !doc.getStatus().equals(DocStatus.vanbandahoanthanh)) {
			
			TaskFilter taskFilter=new TaskFilter();
			taskFilter.setDocIds(Arrays.asList(doc.getId()));
			taskFilter.setTaskRoot(true);
			
			long totalTask=taskService.countTaskAll(taskFilter);
			
			taskFilter.setStatus(TaskStatus.dahoanthanh);
			long totalTaskComplete=taskService.countTaskAll(taskFilter);
			
			if(totalTask==totalTaskComplete) {
				DocResultConfirm docResultConfirm=new DocResultConfirm();
				docResultConfirm.setConfirmedTime(new Date());
				if(reqDocConfirmComplete.getCompletedTime()>0) {
					docResultConfirm.setCompletedTime(DateTimeUtil.createDate(reqDocConfirmComplete.getCompletedTime()));
				}else {
					docResultConfirm.setCompletedTime(new Date());
				}
				docResultConfirm.setContent(reqDocConfirmComplete.getContent());
				docResultConfirm.setAttachments(reqDocConfirmComplete.getAttachments());
				docResultConfirm.setCreator(reqDocConfirmComplete.getCreator().toCreatorInfo());
				
				doc.setResultConfirm(docResultConfirm);
				doc.setStatus(DocStatus.vanbandahoanthanh);
				
				doc=saveDoc(doc, DocAction.capnhat);
				return doc;
			}
			throw new NotAcceptableExceptionAdvice("Văn bản còn nhiệm vụ chưa hoàn thành, không thể xác nhận hoàn thành văn bản được");
		}
		throw new NotAcceptableExceptionAdvice("Văn bản đã xác nhận trước đó rồi");
	}
}
