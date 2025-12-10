package ws.core.resource.admin;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ws.core.model.embeded.LdapUser;
import ws.core.model.response.ResponseAPI;
import ws.core.model.response.util.LdapUserUtil;
import ws.core.services.redis.LdapDataUserServiceRD;

@RestController
@RequestMapping("/api/admin")
public class LdapUserControllerAdmin {

	@Autowired
	private LdapDataUserServiceRD ldapDataUserServiceRD;
	
	@Autowired
	private LdapUserUtil ldapUserUtil;
	
	@GetMapping("/ldap/search-users")
	public Object list(
			@RequestParam(name = "skip", required = true, defaultValue = "0") long skip, 
			@RequestParam(name = "limit", required = true, defaultValue = "10") long limit, 
			@RequestParam(name = "username", required = false) String username) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		long total=ldapDataUserServiceRD.countSearchUsers(username);
		List<LdapUser> ldapUsers=ldapDataUserServiceRD.searchUsers(username, skip, limit);
		List<Document> results=new ArrayList<Document>();
		for (LdapUser item : ldapUsers) {
			results.add(ldapUserUtil.toAdminResponse(item));
		}
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		responseAPI.setResult(results);
		return responseAPI.build();
	}
	
}
