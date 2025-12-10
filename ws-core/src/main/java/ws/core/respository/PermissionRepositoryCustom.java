package ws.core.respository;

import java.util.List;
import java.util.Optional;

import ws.core.model.Permission;
import ws.core.model.filter.PermissionFilter;

public interface PermissionRepositoryCustom {
	List<Permission> findAll(PermissionFilter permissionFilter);
	long countAll(PermissionFilter permissionFilter);
	Optional<Permission> findOne(PermissionFilter permissionFilter);
}
