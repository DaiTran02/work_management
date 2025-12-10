package ws.core.services;

import java.util.List;

import ws.core.model.RoleTemplate;
import ws.core.model.User;
import ws.core.model.filter.RoleTemplateFilter;
import ws.core.model.request.ReqRoleTemplateCreate;
import ws.core.model.request.ReqRoleTemplateUpdate;

public interface RoleTemplateService {
	public long countRoleTemplateAll(RoleTemplateFilter roleTemplateFilter);
	
	public List<RoleTemplate> findRoleTemplateAll(RoleTemplateFilter roleTemplateFilter);
	
	public RoleTemplate findRoleTemplateById(String id);
	
	public RoleTemplate deleteRoleTemplateById(String id);
	
	public RoleTemplate createRoleTemplate(ReqRoleTemplateCreate reqRoleTemplateCreate, User creator);
	
	public RoleTemplate updateRoleTemplate(String roleTemplateId, ReqRoleTemplateUpdate reqRoleTemplateUpdate);
}
