package ws.core.resource.partner.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.EnumUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
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
import ws.core.advice.NotAcceptableExceptionAdvice;
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
import ws.core.model.request.ReqDocCreatePartner;
import ws.core.model.request.ReqDocUpdatePartner;
import ws.core.model.response.ResponseAPI;
import ws.core.model.response.util.DocUtil;
import ws.core.services.DocService;
import ws.core.services.OrganizationService;

@RestController
@RequestMapping("/api/partner/v1")
public class DocControllerPartner {
	
	@Autowired
	private DocService docService;
	
	@Autowired
	private DocUtil docUtil;
	
	@Autowired
	private OrganizationService organizationService;
	
	@GetMapping("/docs")
	public Object list(
			@RequestParam(name = "skip", required = true) int skip, 
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "ownerOrganizationId", required = false) String organizationId, 
			@RequestParam(name = "ownerOrganizationGroupId", required = false) String organizationGroupId,
			@RequestParam(name = "ownerOrganizationUserId", required = false) String organizationUserId, 
			@RequestParam(name = "includeChildOrgs", required = false) Boolean includeChildOrgs, 
			@RequestParam(name = "number", required = false) String number,
			@RequestParam(name = "symbol", required = false) String symbol,
			@RequestParam(name = "category", required = false) String category, 
			@RequestParam(name = "status", required = false) String status, 
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "active", required = false) Boolean active,
			@RequestParam(name = "leaderApproveTaskId", required = false) String leaderApproveTaskId,
			@RequestParam(name = "classifyTaskId", required = false) String classifyTaskId) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		Organization organization=null;
		if(organizationId!=null) {
			if(ObjectId.isValid(organizationId)) {
				organization=organizationService.getOrganizationById(organizationId);
			}else {
				organization=organizationService.getOrganizationByUnitCode(organizationId);
			}
		}
		
		DocFilter docFilter=new DocFilter();
		docFilter.setFromRegDate(fromDate);
		docFilter.setToRegDate(toDate);
		
		DocOwnerFilter docOwnerFilter=new DocOwnerFilter();
		if(organization!=null) {
			docOwnerFilter.setOrganizationId(organizationId);
		}
		if(organizationId!=null && includeChildOrgs!=null && includeChildOrgs.booleanValue()) {
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.setParentId(organizationId);
			
			List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
			docOwnerFilter.setOrganizationIds(organizations.stream().map(e->e.getId()).collect(Collectors.toList()));
			docOwnerFilter.getOrganizationIds().add(organizationId);
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
	
	@GetMapping("/docs/count")
	public Object count(
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "ownerOrganizationId", required = false) String organizationId, 
			@RequestParam(name = "ownerOrganizationGroupId", required = false) String organizationGroupId,
			@RequestParam(name = "ownerOrganizationUserId", required = false) String organizationUserId, 
			@RequestParam(name = "includeChildOrgs", required = false) Boolean includeChildOrgs, 
			@RequestParam(name = "number", required = false) String number,
			@RequestParam(name = "symbol", required = false) String symbol,
			@RequestParam(name = "category", required = false) String category, 
			@RequestParam(name = "status", required = false) String status, 
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "active", required = false) Boolean active,
			@RequestParam(name = "leaderApproveTaskId", required = false) String leaderApproveTaskId,
			@RequestParam(name = "classifyTaskId", required = false) String classifyTaskId) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		Organization organization=null;
		if(organizationId!=null) {
			if(ObjectId.isValid(organizationId)) {
				organization=organizationService.getOrganizationById(organizationId);
			}else {
				organization=organizationService.getOrganizationByUnitCode(organizationId);
			}
		}
		
		DocFilter docFilter=new DocFilter();
		docFilter.setFromRegDate(fromDate);
		docFilter.setToRegDate(toDate);
		
		DocOwnerFilter docOwnerFilter=new DocOwnerFilter();
		if(organization!=null) {
			docOwnerFilter.setOrganizationId(organizationId);
		}
		if(organizationId!=null && includeChildOrgs!=null && includeChildOrgs.booleanValue()) {
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.setParentId(organizationId);
			
			List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
			docOwnerFilter.setOrganizationIds(organizations.stream().map(e->e.getId()).collect(Collectors.toList()));
			docOwnerFilter.getOrganizationIds().add(organizationId);
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
		
		long total=docService.countDocAll(docFilter);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		return responseAPI.build();
	}
	
	@GetMapping("/docs/get-by-iofficeId/{iOfficeId}")
	public Object getByIOfficeId(@PathVariable(name = "iOfficeId", required = true) String iOfficeId) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		Doc docGet=docService.getDocByIOfficeId(iOfficeId);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(docUtil.toSiteResponse(docGet));
		return responseAPI.buildToCache();
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
	
	@PostMapping("/docs")
	public Object create(@RequestBody @Valid ReqDocCreatePartner reqDocCreatePartner){
		ResponseAPI responseAPI=new ResponseAPI();
		
		Doc docCreate=docService.createDoc(reqDocCreatePartner);
		responseAPI.setStatus(HttpStatus.CREATED);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(docUtil.toSiteResponse(docCreate));
		
		/* Thông báo đến Người soạn, quản lý văn bản khi được đẩy về TDNV */
		Thread thread=new Thread(new Runnable() {
			@Override
			public void run() {
				docUtil.notificationSyncDocToOwner(docCreate);
			}
		});
		thread.start();
		
		return responseAPI.build();
	}
	
	@PutMapping("/docs/{id}")
	public Object update(
			@PathVariable(name = "id", required = true) String id,
			@RequestBody @Valid ReqDocUpdatePartner reqDocUpdatePartner){
		ResponseAPI responseAPI=new ResponseAPI();
		
		Doc docCheck=docService.getDocById(id);
		if(!docCheck.isAPIPartner()) {
			throw new NotAcceptableExceptionAdvice("Dữ liệu không được phép truy cập");
		}
		
		Doc docUpdate=docService.updateDoc(id, reqDocUpdatePartner);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(docUtil.toSiteResponse(docUpdate));
		return responseAPI.build();
	}
	
	@PutMapping("/docs/update-by-iofficeId/{iOffficeId}")
	public Object updateByIOfficeId(
			@PathVariable(name = "iOffficeId", required = true) String iOffficeId,
			@RequestBody @Valid ReqDocUpdatePartner reqDocUpdatePartner){
		ResponseAPI responseAPI=new ResponseAPI();
		
		Doc docCheck=docService.getDocByIOfficeId(iOffficeId);
		if(!docCheck.isAPIPartner()) {
			throw new NotAcceptableExceptionAdvice("Dữ liệu không được phép truy cập");
		}
		
		Doc docUpdate=docService.updateDoc(docCheck.getId(), reqDocUpdatePartner);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(docUtil.toSiteResponse(docUpdate));
		return responseAPI.build();
	}
	
	@DeleteMapping("/docs/{id}")
	public Object delete(@PathVariable(name = "id", required = true) String id) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		Doc docCheck=docService.getDocById(id);
		if(!docCheck.isAPIPartner()) {
			throw new NotAcceptableExceptionAdvice("Dữ liệu không được phép truy cập");
		}
		
		docService.deleteDocById(id);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Đã xóa thành công");
		return responseAPI.build();
	}
	
	@DeleteMapping("/docs/delete-by-iofficeId/{iOfficeId}")
	public Object deleteByIOfficeId(@PathVariable(name = "iOfficeId", required = true) String iOfficeId) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		Doc docCheck=docService.getDocByIOfficeId(iOfficeId);
		if(!docCheck.isAPIPartner()) {
			throw new NotAcceptableExceptionAdvice("Dữ liệu không được phép truy cập");
		}
		
		docService.deleteDocById(docCheck.getId());
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
	
	@GetMapping("/docs/data-type")
	public Object getDataType() {
		ResponseAPI responseAPI=new ResponseAPI();
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(DataScopeType.values());
		return responseAPI.build();
	}
}
