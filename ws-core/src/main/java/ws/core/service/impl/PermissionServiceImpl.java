package ws.core.service.impl;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ws.core.advice.NotFoundElementExceptionAdvice;
import ws.core.model.Organization;
import ws.core.model.Permission;
import ws.core.model.filter.PermissionFilter;
import ws.core.respository.PermissionRepository;
import ws.core.respository.PermissionRepositoryCustom;
import ws.core.services.OrganizationService;
import ws.core.services.PermissionService;

@Service
public class PermissionServiceImpl implements PermissionService{
	
	@Autowired
	private PermissionRepository permissionRepository;
	
	@Autowired
	private PermissionRepositoryCustom permissionRepositoryCustom;
	
	@Autowired
	private OrganizationService organizationService;
	
	@Override
	public long countPermissioniAll(PermissionFilter permissionFilter) {
		return permissionRepositoryCustom.countAll(permissionFilter);
	}

	@Override
	public List<Permission> findAppMobiAll(PermissionFilter permissionFilter) {
		return permissionRepositoryCustom.findAll(permissionFilter);
	}

	@Override
	public Permission findPermissionById(String id) {
		Optional<Permission> permission = permissionRepository.findById(new ObjectId(id));
		if(permission.isPresent()) {
			return permission.get();
		}
		throw new NotFoundElementExceptionAdvice("Không tìm thấy quyền hạn");
	}

	@Override
	public Permission findPermissionByKey(String key) {
		Optional<Permission> permission = permissionRepository.findByKey(key);
		if(permission.isPresent()) {
			return permission.get();
		}
		throw new NotFoundElementExceptionAdvice("Không tìm thấy quyền hạn");
	}

	@Override
	public boolean hasPermission(String organizationId, String userId, String permissionKey) {
		Optional<Organization> findOrganization=organizationService.findOrganizationById(organizationId);
		if(findOrganization.isPresent()) {
			Organization organization=findOrganization.get();
			List<String> permissionOfUser = organization.getAllPermissionOfUser(userId);
			if(permissionOfUser.contains(permissionKey)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Boolean checkUserHasPermission(String userId, String permissionKey) {
		List<Organization> userOrganizations = organizationService.getListOrganizationOfUser(userId);
		for (Organization organization : userOrganizations) {
			if(organization.hasPermissionUserRoles(userId, permissionKey)) {
				return true;
			}
		}
		return false;
	}

}
