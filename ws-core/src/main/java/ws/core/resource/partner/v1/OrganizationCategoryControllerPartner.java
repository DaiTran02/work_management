package ws.core.resource.partner.v1;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ws.core.model.OrganizationCategory;
import ws.core.model.filter.OrderByFilter;
import ws.core.model.filter.OrderByFilter.Direction;
import ws.core.model.filter.OrganizationCategoryFilter;
import ws.core.model.filter.SkipLimitFilter;
import ws.core.model.response.ResponseAPI;
import ws.core.model.response.util.OrganizationCategoryUtil;
import ws.core.services.OrganizationCategoryService;

@RestController
@RequestMapping("/api/partner/v1")
public class OrganizationCategoryControllerPartner {
	
	@Autowired
	private OrganizationCategoryService organizationCategoryService;
	
	@Autowired
	private OrganizationCategoryUtil organizationCategoryUtil;
	
	@GetMapping("/organization-categories")
	public Object list(@RequestParam(name = "keyword", required = false) String keyword) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		OrganizationCategoryFilter organizationCategoryFilter=new OrganizationCategoryFilter();
		organizationCategoryFilter.setKeySearch(keyword);
		organizationCategoryFilter.setActive(true);
		organizationCategoryFilter.setSkipLimitFilter(new SkipLimitFilter(0, 0));

		OrderByFilter orderByFilter=new OrderByFilter();
		orderByFilter.add("order", Direction.ASC);
		organizationCategoryFilter.setOrderByFilter(orderByFilter);
		
		long total=organizationCategoryService.countAll(organizationCategoryFilter);
		List<OrganizationCategory> leaderApproveTasks=organizationCategoryService.findAll(organizationCategoryFilter);
		List<Document> results=new ArrayList<Document>();
		for (OrganizationCategory item : leaderApproveTasks) {
			results.add(organizationCategoryUtil.toSiteResponse(item));
		}
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		responseAPI.setResult(results);
		return responseAPI.build();
	}
	
	@GetMapping("/organization-categories/{id}")
	public Object get(@PathVariable(name = "id", required = true) String id) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		OrganizationCategory leaderApproveTask=organizationCategoryService.getById(id);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationCategoryUtil.toSiteResponse(leaderApproveTask));
		return responseAPI.build();
	}
}
