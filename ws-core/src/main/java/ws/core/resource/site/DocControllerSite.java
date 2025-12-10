package ws.core.resource.site;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import ws.core.enums.DataScopeType;
import ws.core.enums.DocCategory;
import ws.core.enums.DocSecurity;
import ws.core.enums.DocStatus;
import ws.core.model.Doc;
import ws.core.model.Organization;
import ws.core.model.filter.DocFilter;
import ws.core.model.filter.OrderByFilter;
import ws.core.model.filter.OrderByFilter.Direction;
import ws.core.model.filter.OrganizationFilter;
import ws.core.model.filter.SkipLimitFilter;
import ws.core.model.filter.embeded.DocOwnerFilter;
import ws.core.model.request.ReqDocConfirmComplete;
import ws.core.model.request.ReqDocCreate;
import ws.core.model.request.ReqDocUpdate;
import ws.core.model.response.ResponseAPI;
import ws.core.model.response.data.TaskCountByStatusModel;
import ws.core.model.response.util.DocUtil;
import ws.core.services.DocService;
import ws.core.services.OrganizationService;
import ws.core.services.PersonalRecordService;
import ws.core.services.TagService;

@RestController
@RequestMapping("/api/site")
public class DocControllerSite {
	
	@Autowired
	private DocService docService;
	
	@Autowired
	private DocUtil docUtil;
	
	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private TagService tagService;
	
	@Autowired
	private PersonalRecordService personalRecordService;
	
	@GetMapping("/docs")
	public Object list(
			@RequestParam(name = "skip", required = true) int skip, 
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "organizationId", required = false) String organizationId, 
			@RequestParam(name = "organizationGroupId", required = false) String organizationGroupId,
			@RequestParam(name = "organizationUserId", required = false) String organizationUserId, 
			@RequestParam(name = "dataScopeType", required = false) DataScopeType dataScopeType, 
			@RequestParam(name = "number", required = false) String number,
			@RequestParam(name = "symbol", required = false) String symbol,
			@RequestParam(name = "category", required = false) String category, 
			@RequestParam(name = "status", required = false) String status, 
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "active", required = false) Boolean active,
			@RequestParam(name = "leaderApproveTaskId", required = false) String leaderApproveTaskId,
			@RequestParam(name = "classifyTaskId", required = false) String classifyTaskId,
			@RequestParam(name = "tagIds", required = false) List<String> tagIds,
			@RequestParam(name = "personalId", required = false) String personalId
			) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		DocFilter docFilter=new DocFilter();
		docFilter.setFromRegDate(fromDate);
		docFilter.setToRegDate(toDate);
		
		DocOwnerFilter docOwnerFilter=new DocOwnerFilter();
		docOwnerFilter.setOrganizationId(organizationId);
		if(organizationId!=null && dataScopeType!=null) {
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.setParentId(organizationId);
			
			List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
			docOwnerFilter.setOrganizationIds(organizations.stream().map(e->e.getId()).collect(Collectors.toList()));
			
			if(dataScopeType.equals(DataScopeType.incChildOrgs)) {
				docOwnerFilter.getOrganizationIds().add(organizationId);
			}
		}
		docOwnerFilter.setOrganizationGroupId(organizationGroupId);
		docOwnerFilter.setOrganizationUserId(organizationUserId);
		docFilter.setOwnerFilter(docOwnerFilter);
		
		docFilter.setNumber(number);
		docFilter.setSymbol(symbol);
		docFilter.setCategory(EnumUtils.getEnumIgnoreCase(DocCategory.class, category));
		docFilter.setStatus(EnumUtils.getEnumIgnoreCase(DocStatus.class, status));
		docFilter.setKeySearch(keyword);
		docFilter.setActive(active);
		docFilter.setLeaderApproveTaskId(leaderApproveTaskId);
		docFilter.setClassifyTaskId(classifyTaskId);
		
		if(tagIds!=null && tagIds.size()>0) {
			List<String> ids=new ArrayList<>();
			ids.addAll(tagService.getListObjectIdsByTagIds(tagIds));
			docFilter.setIds(ids);
		}
		
		if(personalId!=null && !personalId.isEmpty()) {
			List<String> ids = new ArrayList<String>();
			ids.addAll(personalRecordService.getListObjectIdsByPersonalIds(Pair.of("Doc",personalId)));
			docFilter.setIds(ids);
		}
		
		OrderByFilter orderByFilter = new OrderByFilter();
		orderByFilter.add("regDate", Direction.DESC);
		docFilter.setOrderByFilter(orderByFilter);
		docFilter.setSkipLimitFilter(new SkipLimitFilter(skip, limit));
		
		long total=docService.countDocAll(docFilter);
		List<Doc> docs=docService.findDocAll(docFilter);
		List<Document> results=new ArrayList<Document>();
		for (Doc item : docs) {
			results.add(docUtil.toSiteResponse(item));
		}
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		responseAPI.setResult(results);
		return responseAPI.build();
	}
	
	@GetMapping("/docs/summary")
	public Object summary(
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "organizationId", required = false) String organizationId, 
			@RequestParam(name = "organizationGroupId", required = false) String organizationGroupId,
			@RequestParam(name = "organizationUserId", required = false) String organizationUserId, 
			@RequestParam(name = "dataScopeType", required = false) DataScopeType dataScopeType, 
			@RequestParam(name = "number", required = false) String number,
			@RequestParam(name = "symbol", required = false) String symbol,
			@RequestParam(name = "category", required = false) String category, 
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "active", required = false) Boolean active,
			@RequestParam(name = "leaderApproveTaskId", required = false) String leaderApproveTaskId,
			@RequestParam(name = "classifyTaskId", required = false) String classifyTaskId,
			@RequestParam(name = "tagIds", required = false) List<String> tagIds,
			@RequestParam(name = "personalId", required = false) String personalId) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		DocFilter docFilter=new DocFilter();
		docFilter.setFromRegDate(fromDate);
		docFilter.setToRegDate(toDate);
		
		DocOwnerFilter docOwnerFilter=new DocOwnerFilter();
		docOwnerFilter.setOrganizationId(organizationId);
		if(organizationId!=null && dataScopeType!=null) {
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.setParentId(organizationId);
			
			List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
			docOwnerFilter.setOrganizationIds(organizations.stream().map(e->e.getId()).collect(Collectors.toList()));
			
			if(dataScopeType.equals(DataScopeType.incChildOrgs)) {
				docOwnerFilter.getOrganizationIds().add(organizationId);
			}
		}
		docOwnerFilter.setOrganizationGroupId(organizationGroupId);
		docOwnerFilter.setOrganizationUserId(organizationUserId);
		docFilter.setOwnerFilter(docOwnerFilter);
		
		docFilter.setNumber(number);
		docFilter.setSymbol(symbol);
		docFilter.setCategory(EnumUtils.getEnumIgnoreCase(DocCategory.class, category));
		docFilter.setKeySearch(keyword);
		docFilter.setActive(active);
		docFilter.setLeaderApproveTaskId(leaderApproveTaskId);
		docFilter.setClassifyTaskId(classifyTaskId);
		
		if(tagIds!=null && tagIds.size()>0) {
			List<String> ids=new ArrayList<>();
			ids.addAll(tagService.getListObjectIdsByTagIds(tagIds));
			docFilter.setIds(ids);
		}
		
		if(personalId!=null && !personalId.isEmpty()) {
			List<String> ids = new ArrayList<String>();
			ids.addAll(personalRecordService.getListObjectIdsByPersonalIds(Pair.of("Doc",personalId)));
			docFilter.setIds(ids);
		}
		
		docFilter.setStatus(DocStatus.chuagiaonhiemvu);
		long statusTaskTotalChuaGiao=docService.countDocAll(docFilter);
		
		docFilter.setStatus(DocStatus.dangthuchien);
		long statusTaskTotalDaGiao=docService.countDocAll(docFilter);
		
		docFilter.setStatus(DocStatus.vanbandahoanthanh);
		long statusTaskTotalDaHoanThanh=docService.countDocAll(docFilter);
		
		long statusTaskTotal=statusTaskTotalChuaGiao+statusTaskTotalDaGiao+statusTaskTotalDaHoanThanh;
		
		List<TaskCountByStatusModel> statusTaskChild=new ArrayList<>();
		
		TaskCountByStatusModel statusTaskChuaGiao=new TaskCountByStatusModel();
		statusTaskChuaGiao.setKey("chuagiaonhiemvu");
		statusTaskChuaGiao.setName("Chưa giao nhiệm vụ");
		statusTaskChuaGiao.setShortName("Chưa giao");
		statusTaskChuaGiao.setCount(statusTaskTotalChuaGiao);
		statusTaskChild.add(statusTaskChuaGiao);
		
		TaskCountByStatusModel statusTaskDaGiao=new TaskCountByStatusModel();
		statusTaskDaGiao.setKey("dangthuchien");
		statusTaskDaGiao.setName("Đang thực hiện");
		statusTaskDaGiao.setShortName("Đang thực hiện");
		statusTaskDaGiao.setCount(statusTaskTotalDaGiao);
		statusTaskChild.add(statusTaskDaGiao);
		
		TaskCountByStatusModel statusTaskDaHoanThanh=new TaskCountByStatusModel();
		statusTaskDaHoanThanh.setKey("vanbandahoanthanh");
		statusTaskDaHoanThanh.setName("Văn bản đã hoàn thành");
		statusTaskDaHoanThanh.setShortName("Đã hoàn thành");
		statusTaskDaHoanThanh.setCount(statusTaskTotalDaHoanThanh);
		statusTaskChild.add(statusTaskDaHoanThanh);
		
		TaskCountByStatusModel statusTask=new TaskCountByStatusModel();
		statusTask.setKey("trangthai");
		statusTask.setName("Trạng thái văn bản");
		statusTask.setShortName("Trạng thái");
		statusTask.setCount(statusTaskTotal);
		statusTask.setChild(statusTaskChild);
			
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(statusTaskChild.size());
		responseAPI.setResult(statusTask);
		return responseAPI.build();
	}

	@GetMapping("/docs/count")
	public Object count(
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "organizationId", required = false) String organizationId, 
			@RequestParam(name = "organizationGroupId", required = false) String organizationGroupId,
			@RequestParam(name = "organizationUserId", required = false) String organizationUserId, 
			@RequestParam(name = "dataScopeType", required = false) DataScopeType dataScopeType,
			@RequestParam(name = "number", required = false) String number,
			@RequestParam(name = "symbol", required = false) String symbol,
			@RequestParam(name = "category", required = false) String category, 
			@RequestParam(name = "status", required = false) String status, 
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "active", required = false) Boolean active,
			@RequestParam(name = "leaderApproveTaskId", required = false) String leaderApproveTaskId,
			@RequestParam(name = "classifyTaskId", required = false) String classifyTaskId,
			@RequestParam(name = "tagIds", required = false) List<String> tagIds) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		DocFilter docFilter=new DocFilter();
		docFilter.setFromRegDate(fromDate);
		docFilter.setToRegDate(toDate);
		
		DocOwnerFilter docOwnerFilter=new DocOwnerFilter();
		docOwnerFilter.setOrganizationId(organizationId);
		if(organizationId!=null && dataScopeType!=null) {
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.setParentId(organizationId);
			
			List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
			docOwnerFilter.setOrganizationIds(organizations.stream().map(e->e.getId()).collect(Collectors.toList()));
			
			if(dataScopeType.equals(DataScopeType.incChildOrgs)) {
				docOwnerFilter.getOrganizationIds().add(organizationId);
			}
		}
		docOwnerFilter.setOrganizationGroupId(organizationGroupId);
		docOwnerFilter.setOrganizationUserId(organizationUserId);
		docFilter.setOwnerFilter(docOwnerFilter);
		
		docFilter.setNumber(number);
		docFilter.setSymbol(symbol);
		docFilter.setCategory(EnumUtils.getEnumIgnoreCase(DocCategory.class, category));
		docFilter.setStatus(EnumUtils.getEnumIgnoreCase(DocStatus.class, status));
		docFilter.setKeySearch(keyword);
		docFilter.setActive(active);
		docFilter.setLeaderApproveTaskId(leaderApproveTaskId);
		docFilter.setClassifyTaskId(classifyTaskId);
		
		if(tagIds!=null && tagIds.size()>0) {
			List<String> ids=new ArrayList<>();
			ids.addAll(tagService.getListObjectIdsByTagIds(tagIds));
			docFilter.setIds(ids);
		}
		
		long total=docService.countDocAll(docFilter);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		return responseAPI.build();
	}
	
	@GetMapping("/docs/{id}")
	public Object get(@PathVariable(name = "id", required = true) String id) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		Doc docGet=docService.getDocById(id);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(docUtil.toSiteResponse(docGet));
		return responseAPI.buildToCache();
	}
	
	@GetMapping("/docs/get-by-iofficeid/{iOfficeId}")
	public Object getByIOfficeId(@PathVariable(name = "iOfficeId", required = true) String iOfficeId) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		Doc docGet=docService.getDocByIOfficeId(iOfficeId);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(docUtil.toSiteResponse(docGet));
		return responseAPI.buildToCache();
	}
	
	@PostMapping("/docs")
	public Object create(@RequestBody @Valid ReqDocCreate reqDocCreate){
		ResponseAPI responseAPI=new ResponseAPI();
		
		Doc docCreate=docService.createDoc(reqDocCreate);
		responseAPI.setStatus(HttpStatus.CREATED);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(docUtil.toSiteResponse(docCreate));
		return responseAPI.build();
	}
	
	@PutMapping("/docs/{id}")
	public Object update(
			@PathVariable(name = "id", required = true) String id,
			@RequestBody @Valid ReqDocUpdate reqDocUpdate){
		ResponseAPI responseAPI=new ResponseAPI();
		
		Doc docUpdate=docService.updateDoc(id, reqDocUpdate);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(docUtil.toSiteResponse(docUpdate));
		return responseAPI.build();
	}
	
	@PutMapping("/docs/{id}/confirm-complete")
	public Object confirmComplete(
			@PathVariable(name = "id", required = true) String id,
			@RequestBody @Valid ReqDocConfirmComplete reqDocConfirmComplete){
		ResponseAPI responseAPI=new ResponseAPI();
		
		Doc docUpdate=docService.confirmComplete(id, reqDocConfirmComplete);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(docUtil.toSiteResponse(docUpdate));
		return responseAPI.build();
	}
	
	@DeleteMapping("/docs/{id}")
	public Object delete(@PathVariable(name = "id", required = true) String id) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		docService.deleteDocById(id);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Đã xóa thành công");
		return responseAPI.build();
	}
	
	@GetMapping("/docs/category")
	public Object getCategory() {
		ResponseAPI responseAPI=new ResponseAPI();
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(DocCategory.values());
		return responseAPI.build();
	}
	
	@GetMapping("/docs/status")
	public Object getStatus() {
		ResponseAPI responseAPI=new ResponseAPI();
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(DocStatus.values());
		return responseAPI.build();
	}
	
	@GetMapping("/docs/security")
	public Object getSecurity() {
		ResponseAPI responseAPI=new ResponseAPI();
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(DocSecurity.values());
		return responseAPI.build();
	}
	
	@GetMapping("/docs/data-scope-type")
	public Object getDataScopeType() {
		ResponseAPI responseAPI=new ResponseAPI();
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(DataScopeType.values());
		return responseAPI.build();
	}
	
	@GetMapping("/docs/{id}/get-tree-tasks")
	public Object getTreeTasks(@PathVariable(name = "id", required = true) String id) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		Doc doc=docService.getDocById(id);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(docUtil.toSiteResponseTreeTasks(doc));
		return responseAPI.buildToCache();
	}
}