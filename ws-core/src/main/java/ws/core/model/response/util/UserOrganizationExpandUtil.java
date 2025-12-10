package ws.core.model.response.util;

import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ws.core.model.User;
import ws.core.model.embeded.UserOrganizationExpand;
import ws.core.services.UserService;

@Component
public class UserOrganizationExpandUtil {
	
	@Autowired
	private UserUtil userUtil;
	
	@Autowired
	private UserService userService;
	
	public Document convertUserOrganizationExpand(UserOrganizationExpand userOrganizationExpand, boolean userDetail, String status) {
		Document document=new Document();
		document.put("userId", userOrganizationExpand.getUserId());
		document.put("createdTime", userOrganizationExpand.getCreatedTimeLong());
		document.put("updatedTime", userOrganizationExpand.getUpdatedTimeLong());
		document.put("userName", userOrganizationExpand.getUserName());
		document.put("fullName", userOrganizationExpand.getFullName());
		document.put("positionName", userOrganizationExpand.getPositionName());
		document.put("accountIOffice", userOrganizationExpand.getAccountIOffice());
		document.put("active", userOrganizationExpand.isActive());
		document.put("archive", userOrganizationExpand.isArchive());
		
		if(userDetail) {
			Optional<User> findUser=userService.findUserById(userOrganizationExpand.getUserId());
			if(findUser.isPresent()) {
				document.put("moreInfo", userUtil.toAdminResponse(findUser.get()));
			}else {
				document.put("moreInfo", null);
			}
		}
		document.put("status", status);
		return document;
	}
	
	public Document toPartnerConvertUserOrganizationExpand(UserOrganizationExpand userOrganizationExpand, boolean userDetail) {
		Document document=new Document();
		document.put("userId", userOrganizationExpand.getUserId());
		document.put("createdTime", userOrganizationExpand.getCreatedTimeLong());
		document.put("updatedTime", userOrganizationExpand.getUpdatedTimeLong());
		document.put("userName", userOrganizationExpand.getUserName());
		document.put("fullName", userOrganizationExpand.getFullName());
		document.put("positionName", userOrganizationExpand.getPositionName());
		document.put("accountIOffice", userOrganizationExpand.getAccountIOffice());
		document.put("active", userOrganizationExpand.isActive());
		document.put("archive", userOrganizationExpand.isArchive());
		
		if(userDetail) {
			Optional<User> findUser=userService.findUserById(userOrganizationExpand.getUserId());
			if(findUser.isPresent()) {
				document.put("moreInfo", userUtil.toAdminResponse(findUser.get()));
			}else {
				document.put("moreInfo", null);
			}
		}
		return document;
	}
}
