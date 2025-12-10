package ws.core.resource.partner.v1;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ws.core.model.Organization;
import ws.core.model.User;
import ws.core.model.embeded.GroupOrganizationExpand;
import ws.core.model.filter.OrganizationFilter;
import ws.core.model.response.ResponseAPI;
import ws.core.services.OrganizationService;
import ws.core.services.UserService;

@RestController
@RequestMapping("/api/partner/v1")
public class UserControllerPartner {

	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/users/belong-to-group-organizations")
	public Object getListOrganizationsUserBelongTo(
			@RequestParam(name = "username", required = true) String username){
		ResponseAPI responseAPI=new ResponseAPI();
		
		User user= userService.getUserByUserName(username);
		
		OrganizationFilter organizationFilter=new OrganizationFilter();
		organizationFilter.setIncludeUserId(user.getId());
		
		List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
		List<Document> resulst=new ArrayList<>();
		for(Organization organization:organizations) {
			List<GroupOrganizationExpand> groupOrganizationExpands=new ArrayList<>();
			for(GroupOrganizationExpand groupOrganizationExpand:organization.getGroupOrganizationExpands()) {
				if(groupOrganizationExpand.getUserIds().contains(user.getId())) {
					groupOrganizationExpands.add(groupOrganizationExpand);
				}
			}

			if(groupOrganizationExpands.size()>0) {
				for (GroupOrganizationExpand groupOrganizationExpand : groupOrganizationExpands) {
					Document belongOrganization = new Document();
					belongOrganization.put("organizationId", organization.getId());
					belongOrganization.put("organizationName", organization.getName());
					belongOrganization.put("organizationGroupId", groupOrganizationExpand.getGroupId());
					belongOrganization.put("organizationGroupName", groupOrganizationExpand.getName());
					belongOrganization.put("organizationUserId", user.getId());
					belongOrganization.put("organizationUserName", user.getFullName());
					
					resulst.add(belongOrganization);
				}
			}else {
				Document belongOrganization = new Document();
				belongOrganization.put("organizationId", organization.getId());
				belongOrganization.put("organizationName", organization.getName());
				belongOrganization.put("organizationGroupId", null);
				belongOrganization.put("organizationGroupName", null);
				belongOrganization.put("organizationUserId", user.getId());
				belongOrganization.put("organizationUserName", user.getFullName());
				
				resulst.add(belongOrganization);
			}
		}
		
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Danh sách đơn vị trực thuộc tài khoản ["+username+"]");
		responseAPI.setTotal(resulst.size());
		responseAPI.setResult(resulst);
		return responseAPI.build();
	}
}
