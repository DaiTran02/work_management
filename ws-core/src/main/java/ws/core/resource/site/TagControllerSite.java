package ws.core.resource.site;

import java.util.ArrayList;
import java.util.List;

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
import ws.core.enums.TagType;
import ws.core.model.Tag;
import ws.core.model.filter.CreatorFilter;
import ws.core.model.filter.SearchingTypeFilter;
import ws.core.model.filter.SkipLimitFilter;
import ws.core.model.filter.TagFilter;
import ws.core.model.request.ReqTagAddClass;
import ws.core.model.request.ReqTagCreate;
import ws.core.model.request.ReqTagEdit;
import ws.core.model.request.ReqTagRemoveClass;
import ws.core.model.response.ResponseAPI;
import ws.core.model.response.util.TagUtil;
import ws.core.services.TagService;

@RestController
@RequestMapping("/api/site")
public class TagControllerSite {
	
	@Autowired
	private TagService tagService;
	
	@Autowired
	private TagUtil tagUtil;
	
	@GetMapping("/tags")
	public Object list(
			@RequestParam(name = "organizationId", required = true) String organizationId,
			@RequestParam(name = "userId", required = true) String userId,
			@RequestParam(name = "skip", required = true) int skip, 
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "type", required = false) String type,
			@RequestParam(name = "classIds", required = false) List<String> classIds,
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "active", required = false) Boolean active) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		TagFilter tagFilter=new TagFilter();
		tagFilter.setType(type);
		tagFilter.setClassIds(classIds);
		tagFilter.setKeySearch(keyword);
		tagFilter.setActive(active);
		
		CreatorFilter creatorFilter=new CreatorFilter();
		creatorFilter.setOrganizationId(organizationId);
		creatorFilter.setOrganizationUserId(userId);
		tagFilter.setCreatorFilter(creatorFilter);
		
		tagFilter.setSearchingTypeFilter(SearchingTypeFilter.matchingAny);
		tagFilter.setSkipLimitFilter(new SkipLimitFilter(skip, limit));
		
		long total=tagService.countAll(tagFilter);
		List<Tag> leaderApproveTasks=tagService.findAll(tagFilter);
		List<Document> results=new ArrayList<Document>();
		for (Tag item : leaderApproveTasks) {
			results.add(tagUtil.toSiteResponse(item));
		}
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		responseAPI.setResult(results);
		return responseAPI.build();
	}
	
	@GetMapping("/tags/{id}")
	public Object get(@PathVariable(name = "id", required = true) String id) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		Tag tag=tagService.getById(id);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(tagUtil.toSiteResponse(tag));
		return responseAPI.build();
	}
	
	@PostMapping("/tags")
	public Object create(@RequestBody @Valid ReqTagCreate reqTagCreate){
		ResponseAPI responseAPI=new ResponseAPI();
		
		Tag appAccessCreate=tagService.create(reqTagCreate);
		responseAPI.setStatus(HttpStatus.CREATED);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(tagUtil.toSiteResponse(appAccessCreate));
		return responseAPI.build();
	}
	
	@PutMapping("/tags/{id}")
	public Object update(
			@PathVariable(name = "id", required = true) String id,
			@RequestBody @Valid ReqTagEdit reqTagEdit){
		ResponseAPI responseAPI=new ResponseAPI();
		
		Tag tagUpdate=tagService.update(id, reqTagEdit);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(tagUtil.toSiteResponse(tagUpdate));
		return responseAPI.build();
	}
	
	@DeleteMapping("/tags/{id}")
	public Object delete(@PathVariable(name = "id", required = true) String id) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		tagService.delele(id);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Xóa thành công");
		return responseAPI.build();
	}
	
	@GetMapping("/tags/get-types-filter")
	public Object getTypesFilter(){
		ResponseAPI responseAPI=new ResponseAPI();
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(TagType.values());
		return responseAPI.build();
	}
	
	@PutMapping("/tags/add-class")
	public Object addClass(@RequestBody @Valid ReqTagAddClass reqTagAddClass){
		ResponseAPI responseAPI=new ResponseAPI();
		
		Tag tag=tagService.addClass(reqTagAddClass);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(tagUtil.toSiteResponse(tag));
		return responseAPI.build();
	}
	
	@PutMapping("/tags/remove-class")
	public Object removeClass(@RequestBody @Valid ReqTagRemoveClass reqTagRemoveClass){
		ResponseAPI responseAPI=new ResponseAPI();
		
		Tag tag=tagService.removeClass(reqTagRemoveClass);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(tagUtil.toSiteResponse(tag));
		return responseAPI.build();
	}
}
