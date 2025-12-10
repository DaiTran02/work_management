package ws.core.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import ws.core.advice.DuplicateKeyExceptionAdvice;
import ws.core.advice.NotFoundElementExceptionAdvice;
import ws.core.model.Organization;
import ws.core.model.RoleTemplate;
import ws.core.model.User;
import ws.core.model.filter.OrganizationFilter;
import ws.core.model.filter.RoleTemplateFilter;
import ws.core.model.filter.embeded.RoleOrganizationExpandFilter;
import ws.core.model.request.ReqRoleTemplateCreate;
import ws.core.model.request.ReqRoleTemplateUpdate;
import ws.core.respository.RoleTemplateRepository;
import ws.core.respository.RoleTemplateRepositoryCustom;
import ws.core.services.OrganizationService;
import ws.core.services.RoleTemplateService;

@Service
public class RoleTemplateServiceImpl implements RoleTemplateService{

	@Autowired
	private RoleTemplateRepository roleTemplateRepository;
	
	@Autowired
	private RoleTemplateRepositoryCustom roleTemplateRepositoryCustom;
	
	@Autowired
	private OrganizationService organizationService;
	
	@Override
	public long countRoleTemplateAll(RoleTemplateFilter roleTemplateFilter) {
		return roleTemplateRepositoryCustom.countAll(roleTemplateFilter);
	}

	@Override
	public List<RoleTemplate> findRoleTemplateAll(RoleTemplateFilter roleTemplateFilter) {
		return roleTemplateRepositoryCustom.findAll(roleTemplateFilter);
	}

	@Override
	public RoleTemplate findRoleTemplateById(String id) {
		Optional<RoleTemplate> roleTemplate = roleTemplateRepository.findById(new ObjectId(id));
		if(roleTemplate.isPresent()) {
			return roleTemplate.get();
		}
		throw new NotFoundElementExceptionAdvice("Không tìm thấy kết quả");
	}

	@Override
	public RoleTemplate deleteRoleTemplateById(String id) {
		RoleTemplate roleTemplate=findRoleTemplateById(id);
		roleTemplateRepository.delete(roleTemplate);
		return roleTemplate;
	}

	@Override
	public RoleTemplate createRoleTemplate(ReqRoleTemplateCreate reqRoleTemplateCreate, User creator) {
		RoleTemplate roleTemplate=new RoleTemplate();
		roleTemplate.setId(ObjectId.get());
		roleTemplate.setCreatedTime(new Date());
		roleTemplate.setUpdatedTime(new Date());
		roleTemplate.setName(reqRoleTemplateCreate.getName());
		roleTemplate.setDescription(reqRoleTemplateCreate.getDescription());
		if(creator!=null) {
			roleTemplate.setCreatorId(creator.getId());
			roleTemplate.setCreatorName(creator.getFullName());
		}
		roleTemplate.setPermissionKeys(reqRoleTemplateCreate.getPermissionKeys());
		roleTemplate.setActive(reqRoleTemplateCreate.isActive());
		
		try {
			return roleTemplateRepository.save(roleTemplate);
		} catch (Exception e) {
			if(e instanceof DuplicateKeyException) {
				throw new DuplicateKeyExceptionAdvice("Dữ liệu bị trùng khóa chính, vui lòng thử lại");
			}
			throw e;
		}
	}

	@Override
	public RoleTemplate updateRoleTemplate(String roleTemplateId, ReqRoleTemplateUpdate reqRoleTemplateUpdate) {
		RoleTemplate roleTemplate=findRoleTemplateById(roleTemplateId);
		roleTemplate.setUpdatedTime(new Date());
		roleTemplate.setName(reqRoleTemplateUpdate.getName());
		roleTemplate.setDescription(reqRoleTemplateUpdate.getDescription());
		roleTemplate.setPermissionKeys(reqRoleTemplateUpdate.getPermissionKeys());
		roleTemplate.setActive(reqRoleTemplateUpdate.isActive());
		
		/* Cập nhật cho các vai trò của các đơn vị có sử dụng vai trò mẫu làm tham chiếu*/
		OrganizationFilter organizationFilter=new OrganizationFilter();
		organizationFilter.setRoleOrganizationExpandFilter(RoleOrganizationExpandFilter.builder().roleTemplateId(roleTemplateId).build());
		List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
		for (Organization organization : organizations) {
			try {
				organizationService.updateRoleOrganizationExpandByRoleTemplate(organization, roleTemplate);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/* End cập nhật cho các vai trò của các đơn vị */
		
		return roleTemplateRepository.save(roleTemplate);
	}
}
