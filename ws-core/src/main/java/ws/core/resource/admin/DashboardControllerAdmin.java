package ws.core.resource.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ws.core.model.Organization;
import ws.core.model.filter.FirstReviewFilter;
import ws.core.model.filter.OrganizationFilter;
import ws.core.model.filter.UserFilter;
import ws.core.model.response.ResponseAPI;
import ws.core.services.OrganizationService;
import ws.core.services.UserService;

@RestController
@RequestMapping("/api/admin")
public class DashboardControllerAdmin {

	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/dashboards/users")
	public Object users(
			@RequestParam(name = "organizationId", required = false) String organizationId) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		List<String> ids=new ArrayList<>();
		if(organizationId!=null) {
			Organization organization=organizationService.getOrganizationById(organizationId);
			ids=organizationService.getChildOrganizationsAllLevel(organization);
		}
		System.out.println("Total orgs: "+ids.size());
		
		UserFilter userFilter=new UserFilter();
		if(organizationId!=null) {
			userFilter.setIncludeOrganizationIds(ids);
		}
		
		long totalUsers = userService.countUserAll(userFilter);
		
		userFilter.setActive(true);
		long totalUsersActive = userService.countUserAll(userFilter);
		
		userFilter.setActive(false);
		long totalUsersInActive = userService.countUserAll(userFilter);
		
		FirstReviewFilter firstReviewFilter=new FirstReviewFilter();
		userFilter.setFirstReviewFilter(firstReviewFilter);
		userFilter.setActive(null);
		long totalUsersUseFirstReview = userService.countUserAll(userFilter);
		
		firstReviewFilter=new FirstReviewFilter();
		firstReviewFilter.setReviewed(true);
		userFilter.setFirstReviewFilter(firstReviewFilter);
		userFilter.setActive(null);
		long totalUsersFirstReviewAccepted = userService.countUserAll(userFilter);
		
		Document data1=new Document();
		data1.put("key", "totalUsersActive");
		data1.put("title", "Tổng tài khoản đang hoạt động");
		data1.put("value", totalUsersActive);
		data1.put("display", totalUsersActive+"/"+totalUsers+" tài khoản");
		
		Document data2=new Document();
		data2.put("key", "totalUsersInActive");
		data2.put("title", "Tổng tài khoản không hoạt động");
		data2.put("value", totalUsersInActive);
		data2.put("display", totalUsersInActive+"/"+totalUsers+" tài khoản");
		
		Document data3=new Document();
		data3.put("key", "totalUsersUseFirstReview");
		data3.put("title", "Những tài khoản tự chọn đơn vị");
		data3.put("value", totalUsersUseFirstReview);
		data3.put("display", totalUsersUseFirstReview+"/"+totalUsers+" tài khoản");
		
		Document data4=new Document();
		data4.put("key", "totalUsersFirstReviewAccepted");
		data4.put("title", "Những tài khoản đã duyệt khi chọn đơn vị lần đầu");
		data4.put("value", totalUsersFirstReviewAccepted);
		data4.put("display", totalUsersFirstReviewAccepted+"/"+totalUsersUseFirstReview+" tài khoản");
		
		List<Document> results=new ArrayList<Document>();
		results.add(data1);
		results.add(data2);
		results.add(data3);
		results.add(data4);
		
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(results);
		return responseAPI.build();
	}
	
	@GetMapping("/dashboards/organizations")
	public Object organizations(@RequestParam(name = "organizationId", required = false) String organizationId) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		List<String> ids=new ArrayList<>();
		if(organizationId!=null) {
			Organization organization=organizationService.getOrganizationById(organizationId);
			ids=organizationService.getChildOrganizationsAllLevel(organization);
		}else {
			ids=organizationService.findOrganizationAll().stream().map(e->e.getId()).collect(Collectors.toList());
		}
		System.out.println("Total orgs: "+ids.size());
		
		OrganizationFilter organizationFilter=new OrganizationFilter();
		organizationFilter.setIds(ids);
		
		long totalOrganizationsChild=ids.size();
		
		organizationFilter.setActive(true);
		long totalOrganizationsChildActive=organizationService.countOrganizationAll(organizationFilter);
		
		organizationFilter.setActive(false);
		long totalOrganizationsChildInActive=organizationService.countOrganizationAll(organizationFilter);
		
		organizationFilter.setActive(null);
		organizationFilter.setHasContainUsers(true);
		long totalOrganizationsChildUsersEmpty=organizationService.countOrganizationAll(organizationFilter);
		
		Document data1=new Document();
		data1.put("key", "totalOrganizationsChild");
		data1.put("title", "Tổng số đơn vị (cả các cấp dưới)");
		data1.put("value", totalOrganizationsChild);
		data1.put("display", totalOrganizationsChild+" đơn vị");
		
		Document data2=new Document();
		data2.put("key", "totalOrganizationsChildActive");
		data2.put("title", "Số đơn vị đang hoạt động");
		data2.put("value", totalOrganizationsChildActive);
		data2.put("display", totalOrganizationsChildActive+"/"+totalOrganizationsChild+" đơn vị");
		
		Document data3=new Document();
		data3.put("key", "totalOrganizationsChildInActive");
		data3.put("title", "Số đơn vị ngưng hoạt động");
		data3.put("value", totalOrganizationsChildInActive);
		data3.put("display", totalOrganizationsChildInActive+"/"+totalOrganizationsChild+" đơn vị");
		
		Document data4=new Document();
		data4.put("key", "totalOrganizationsChildUsersEmpty");
		data4.put("title", "Số đơn vị không có người dùng");
		data4.put("value", totalOrganizationsChildUsersEmpty);
		data4.put("display", totalOrganizationsChildUsersEmpty+"/"+totalOrganizationsChild+" đơn vị");
		
		List<Document> results=new ArrayList<Document>();
		results.add(data1);
		results.add(data2);
		results.add(data3);
		results.add(data4);
		
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(results);
		return responseAPI.build();
	}
}
