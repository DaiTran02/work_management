package ws.core.resource.admin;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ws.core.model.Permission;
import ws.core.model.filter.PermissionFilter;
import ws.core.model.response.ResponseAPI;
import ws.core.model.response.util.PermissionUtil;
import ws.core.services.PermissionService;

@RestController
@RequestMapping("/api/admin")
public class PermissionControllerAdmin {

	@Autowired
	private PermissionService permissionService;
	
	@Autowired
	private PermissionUtil permissionUtil;
	
	@GetMapping("/permissions")
	public Object list() {
		ResponseAPI responseAPI=new ResponseAPI();
		
		PermissionFilter permissionFilter=new PermissionFilter();
		
		long total=permissionService.countPermissioniAll(permissionFilter);
		List<Permission> permissions=permissionService.findAppMobiAll(permissionFilter);
		List<Document> results=new ArrayList<Document>();
		for (Permission item : permissions) {
			results.add(permissionUtil.toAdminResponse(item));
		}
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		responseAPI.setResult(results);
		return responseAPI.build();
	}
	
	@GetMapping("/permissions/{key}")
	public Object getByKey(@PathVariable(name = "key", required = true) String key) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		Permission permission=permissionService.findPermissionByKey(key);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(permissionUtil.toAdminResponse(permission));
		return responseAPI.build();
	}
}
