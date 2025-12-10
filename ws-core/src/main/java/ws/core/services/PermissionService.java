package ws.core.services;

import java.util.List;

import ws.core.model.Permission;
import ws.core.model.filter.PermissionFilter;

public interface PermissionService {
	public long countPermissioniAll(PermissionFilter permissionFilter);
	
	public List<Permission> findAppMobiAll(PermissionFilter permissionFilter);
	
	public Permission findPermissionById(String id);
	
	public Permission findPermissionByKey(String key);
	
	public boolean hasPermission(String organizationId, String userId, String permissionKey);

	public Boolean checkUserHasPermission(String userId, String permissionKey);
}
