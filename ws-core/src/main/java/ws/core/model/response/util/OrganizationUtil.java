package ws.core.model.response.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ws.core.enums.UserAccessStatus;
import ws.core.model.Organization;
import ws.core.model.embeded.GroupOrganizationExpand;
import ws.core.model.embeded.RoleOrganizationExpand;
import ws.core.model.embeded.UserOrganizationExpand;
import ws.core.model.filter.OrganizationFilter;
import ws.core.services.OrganizationService;

@Component
public class OrganizationUtil{
	
	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private UserOrganizationExpandUtil userOrganizationExpandUtil;
	
	@Autowired
	private RoleOrganizationExpandUtil roleOrganizationExpandUtil;
	
	@Autowired
	private GroupOrganizationExpandUtil groupOrganizationExpandUtil;
	
	private Document toCommon(Organization organization) {
		Document document=new Document();
		document.put("id", organization.getId());
		document.put("createdTime", organization.getCreatedTime());
		document.put("updatedTime", organization.getUpdatedTime());
		document.put("name", organization.getName());
		document.put("description", organization.getDescription());
		document.put("creatorId", organization.getCreatorId());
		document.put("creatorName", organization.getCreatorName());
		document.put("path", organization.getPath());
		document.put("parentId", organization.getParentId());
		document.put("parentIdSeconds", organization.getParentIdSeconds());
		document.put("active", organization.isActive());
		document.put("archive", organization.isArchive());
		document.put("order", organization.getOrder());
		document.put("unitCode", organization.getUnitCode());
		document.put("level", organization.getLevel());
		
		document.put("organizationCategoryId", organization.getOrganizationCategoryId());
		document.put("countSubOrganization", organizationService.countSubOrganization(organization));
		document.put("userOrganizationExpands", getListUserOrganizationExpandToResponse(organization, false));
		document.put("groupOrganizationExpands", organization.getGroupOrganizationExpands().stream().map(e->groupOrganizationExpandUtil.convertGroupOrganizationExpand(e)).toList());
		document.put("roleOrganizationExpands", organization.getRoleOrganizationExpands().stream().map(e->roleOrganizationExpandUtil.convertRoleOrganizationExpand(e)).toList());
		return document;
	}
	
	public Document toPartnerResponse(Organization organization) {
		Document document=new Document();
		document.put("id", organization.getId());
		document.put("unitCode", organization.getUnitCode());
		document.put("createdTime", organization.getCreatedTime());
		document.put("updatedTime", organization.getUpdatedTime());
		document.put("name", organization.getName());
		document.put("description", organization.getDescription());
		document.put("creatorId", organization.getCreatorId());
		document.put("creatorName", organization.getCreatorName());
		document.put("path", organization.getPath());
		document.put("parentId", organization.getParentId());
		document.put("parentIdSeconds", organization.getParentIdSeconds());
		document.put("active", organization.isActive());
		document.put("archive", organization.isArchive());
		document.put("order", organization.getOrder());
		document.put("organizationCategoryId", organization.getOrganizationCategoryId());
		document.put("countSubOrganization", organizationService.countSubOrganization(organization));
		document.put("syncExternal", organization.isSyncExternal());
		return document;
	}
	
	private Document toCommon(Organization organization, boolean user, boolean group, boolean role) {
		Document document=new Document();
		document.put("id", organization.getId());
		document.put("createdTime", organization.getCreatedTime());
		document.put("updatedTime", organization.getUpdatedTime());
		document.put("name", organization.getName());
		document.put("description", organization.getDescription());
		document.put("creatorId", organization.getCreatorId());
		document.put("creatorName", organization.getCreatorName());
		document.put("path", organization.getPath());
		document.put("parentId", organization.getParentId());
		document.put("parentIdSeconds", organization.getParentIdSeconds());
		document.put("active", organization.isActive());
		document.put("archive", organization.isArchive());
		document.put("order", organization.getOrder());
		document.put("unitCode", organization.getUnitCode());
		document.put("level", organization.getLevel());
		
		document.put("organizationCategoryId", organization.getOrganizationCategoryId());
		document.put("countSubOrganization", organizationService.countSubOrganization(organization));
		if(user) {
			document.put("userOrganizationExpands", getListUserOrganizationExpandToResponse(organization, false));
		}
		
		if(group) {
			document.put("groupOrganizationExpands", organization.getGroupOrganizationExpands());
		}
		
		if(role) {
			document.put("roleOrganizationExpands", organization.getRoleOrganizationExpands());
		}
		return document;
	}
	
	public Document toAdminResponse(Organization organization) {
		Document document=toCommon(organization);
		return document;
	}
	
	public Document toSiteResponse(Organization organization) {
		Document document=toCommon(organization);
		return document;
	}
	
	public Document toSiteResponseOnlyOrganization(Organization organization) {
		Document document=toCommon(organization, false, false, false);
		return document;
	}
	
	/**
	 * Danh sách Người dùng trong Đơn vị
	 * @param organization
	 * @param userDetail
	 * @return
	 */
	public List<Document> getListUserOrganizationExpandToResponsePartner(Organization organization, boolean userDetail) {
		List<Document> list=new ArrayList<Document>();
		for(UserOrganizationExpand userOrganizationExpand:organization.getUserOrganizationExpands()) {
			list.add(userOrganizationExpandUtil.toPartnerConvertUserOrganizationExpand(userOrganizationExpand, userDetail));
		}
		return list;
	}
	
	/**
	 * Danh sách Người dùng trong Đơn vị
	 * @param organization
	 * @param userDetail
	 * @return
	 */
	public List<Document> getListUserOrganizationExpandToResponse(Organization organization, boolean userDetail) {
		List<Document> list=new ArrayList<Document>();
		for(UserOrganizationExpand userOrganizationExpand:organization.getUserOrganizationExpands()) {
			list.add(userOrganizationExpandUtil.convertUserOrganizationExpand(userOrganizationExpand, userDetail, null));
		}
		return list;
	}
	
	/**
	 * Danh sách Người dùng trong Đơn vị
	 * @param organization
	 * @param userDetail
	 * @param userIds
	 * @param include
	 * @return
	 */
	public List<Document> getListUserOrganizationExpandToResponse(Organization organization, boolean userDetail, List<String> userIds, String include) {
		List<Document> list=new ArrayList<Document>();
		for(UserOrganizationExpand userOrganizationExpand:organization.getUserOrganizationExpands()) {
			if(include!=null) {
				if(Boolean.parseBoolean(include)) {
					if(userIds.contains(userOrganizationExpand.getUserId())) {
						list.add(userOrganizationExpandUtil.convertUserOrganizationExpand(userOrganizationExpand, userDetail, "logged"));
					}
				}else {
					if(!userIds.contains(userOrganizationExpand.getUserId())) {
						list.add(userOrganizationExpandUtil.convertUserOrganizationExpand(userOrganizationExpand, userDetail, "notlogged"));
					}
				}
			}else {
				if(userIds.contains(userOrganizationExpand.getUserId())) {
					list.add(userOrganizationExpandUtil.convertUserOrganizationExpand(userOrganizationExpand, userDetail, "logged"));
				}else {
					list.add(userOrganizationExpandUtil.convertUserOrganizationExpand(userOrganizationExpand, userDetail, "notlogged"));
				}
			}
		}
		return list;
	}
	
	/**
	 * Chi tiết Người dùng trong Đơn vị
	 * @param organization
	 * @param userId
	 * @param userDetail
	 * @return
	 */
	public Document getUserOrganizationExpandToResponsePartner(Organization organization, String userId, boolean userDetail) {
		for(UserOrganizationExpand userOrganizationExpand:organization.getUserOrganizationExpands()) {
			if(userOrganizationExpand.getUserId().equalsIgnoreCase(userId)) {
				return userOrganizationExpandUtil.toPartnerConvertUserOrganizationExpand(userOrganizationExpand, userDetail);
			}
		}
		return null;
	}
	
	/**
	 * Chi tiết Người dùng trong Đơn vị
	 * @param organization
	 * @param userId
	 * @param userDetail
	 * @return
	 */
	public Document getUserOrganizationExpandToResponse(Organization organization, String userId, boolean userDetail) {
		for(UserOrganizationExpand userOrganizationExpand:organization.getUserOrganizationExpands()) {
			if(userOrganizationExpand.getUserId().equalsIgnoreCase(userId)) {
				return userOrganizationExpandUtil.convertUserOrganizationExpand(userOrganizationExpand, userDetail, null);
			}
		}
		return null;
	}
	
	
	/**
	 * Danh sách Nhóm tổ trong Đơn vị
	 * @param organization
	 * @return
	 */
	public List<Document> getListGroupInOrganizationToResponse(Organization organization) {
		List<Document> list=new ArrayList<Document>();
		for(GroupOrganizationExpand groupOrganizationExpand:organization.getGroupOrganizationExpands()) {
			list.add(groupOrganizationExpandUtil.convertGroupOrganizationExpand(groupOrganizationExpand));
		}
		return list;
	}
	
	
	/**
	 * Tìm danh sách Nhóm tổ trong Đơn vị
	 * @param organization
	 * @param keyword
	 * @return
	 */
	public List<Document> searchListGroupInOrganizationToResponse(Organization organization, String keyword) {
		List<Document> list=new ArrayList<Document>();
		for(GroupOrganizationExpand groupOrganizationExpand:organization.getGroupOrganizationExpands()) {
			if(groupOrganizationExpand.isCondition(keyword)) {
				list.add(groupOrganizationExpandUtil.convertGroupOrganizationExpand(groupOrganizationExpand));
			}
		}
		return list;
	}
	
	/**
	 * Chi tiết Nhóm tổ trong Đơn vị
	 * @param organization
	 * @param groupId
	 * @return
	 */
	public Document getGroupInOrganizationToResponse(Organization organization, String groupId) {
		Optional<GroupOrganizationExpand> findGroupOrganizationExpand=organization.getGroupOrganizationExpand(groupId);
		if(findGroupOrganizationExpand.isPresent()) {
			return groupOrganizationExpandUtil.convertGroupOrganizationExpand(findGroupOrganizationExpand.get());
		}
		return null;
	}
	
	/**
	 * Chi tiết Nhóm Tổ trong Đơn vị của Người dùng đang trực thuộc
	 * @param organization
	 * @param userId
	 * @return
	 */
	public Document getGroupInOrganizationThatUserBelongToResponse(Organization organization, String userId) {
		for(GroupOrganizationExpand groupOrganizationExpand:organization.getGroupOrganizationExpands()) {
			if(groupOrganizationExpand.getUserIds().contains(userId)) {
				return groupOrganizationExpandUtil.convertGroupOrganizationExpand(groupOrganizationExpand);
			}
		}
		return null;
	}
	
	/**
	 * Danh sách Người dùng của Nhóm Tổ trong Đơn vị
	 * @param organization
	 * @param groupId
	 * @return
	 */
	public List<Document> getListUsersInGroupOrganizationToResponse(Organization organization, String groupId) {
		List<Document> results=new ArrayList<>();
		for(GroupOrganizationExpand groupOrganizationExpand:organization.getGroupOrganizationExpands()) {
			if(groupOrganizationExpand.getGroupId().equalsIgnoreCase(groupId)) {
				List<UserOrganizationExpand> userOrganizationExpands = organization.getUserOrganizationExpands().stream().filter(e->groupOrganizationExpand.getUserIds().contains(e.getUserId())).collect(Collectors.toList());
				for (UserOrganizationExpand userOrganizationExpand : userOrganizationExpands) {
					results.add(userOrganizationExpandUtil.convertUserOrganizationExpand(userOrganizationExpand, true, null));
				}
				break;
			}
		}
		return results;
	}	
	
	/**
	 * Danh sách Người dùng không nằm trong Nhóm Tổ của Đơn vị
	 * @param organization
	 * @return
	 */
	public List<Document> getListUsersNotInAllGroupOrganizationToResponse(Organization organization) {
		List<String> userIdsAllGroup=new ArrayList<>();
		for(GroupOrganizationExpand groupOrganizationExpand:organization.getGroupOrganizationExpands()) {
			userIdsAllGroup.addAll(groupOrganizationExpand.getUserIds());
		}
		
		List<UserOrganizationExpand> userOrganizationExpands = organization.getUserOrganizationExpands().stream().filter(e->!userIdsAllGroup.contains(e.getUserId())).collect(Collectors.toList());
		List<Document> results=new ArrayList<>();
		for (UserOrganizationExpand userOrganizationExpand : userOrganizationExpands) {
			results.add(userOrganizationExpandUtil.convertUserOrganizationExpand(userOrganizationExpand, true, null));
		}
		return results;
	}	
	
	
	/**
	 * Danh sách Vai trò
	 * @param organization
	 * @return
	 */
	public List<Document> getListRoleInOrganizationToResponse(Organization organization) {
		List<Document> list=new ArrayList<Document>();
		for(RoleOrganizationExpand roleOrganizationExpand:organization.getRoleOrganizationExpands()) {
			list.add(roleOrganizationExpandUtil.convertRoleOrganizationExpand(roleOrganizationExpand));
		}
		return list;
	}
	
	/**
	 * Chi tiết Vai trò của Đơn vị
	 * @param organization
	 * @param roleId
	 * @return
	 */
	public Document getRoleInOrganizationToResponse(Organization organization, String roleId) {
		Optional<RoleOrganizationExpand> findRoleOrganizationExpand=organization.getRoleOrganizationExpand(roleId);
		if(findRoleOrganizationExpand.isPresent()) {
			return roleOrganizationExpandUtil.convertRoleOrganizationExpand(findRoleOrganizationExpand.get());
		}
		return null;
	}
	
	/**
	 * Danh sách Vai trò trong Đơn vị của Người dùng
	 * @param organization
	 * @param userId
	 * @return
	 */
	public List<Document> getListRoleInOrganizationThatUserBelongToResponse(Organization organization, String userId) {
		List<Document> list=new ArrayList<Document>();
		for(RoleOrganizationExpand roleOrganizationExpand:organization.getRoleOrganizationExpands()) {
			if(roleOrganizationExpand.getUserIds().contains(userId)) {
				list.add(roleOrganizationExpandUtil.convertRoleOrganizationExpand(roleOrganizationExpand));
			}
		}
		return list;
	}
	
	/**
	 * Danh sách Người dùng của Vai trò trong Đơn vị
	 * @param organization
	 * @param roleId
	 * @return
	 */
	public List<Document> getListUsersInRoleOrganizationToResponse(Organization organization, String roleId) {
		List<Document> results=new ArrayList<>();
		for(RoleOrganizationExpand roleOrganizationExpand:organization.getRoleOrganizationExpands()) {
			if(roleOrganizationExpand.getRoleId().equalsIgnoreCase(roleId)) {
				List<UserOrganizationExpand> userOrganizationExpands = organization.getUserOrganizationExpands().stream().filter(e->roleOrganizationExpand.getUserIds().contains(e.getUserId())).collect(Collectors.toList());
				for (UserOrganizationExpand userOrganizationExpand : userOrganizationExpands) {
					results.add(userOrganizationExpandUtil.convertUserOrganizationExpand(userOrganizationExpand, false, null));
				}
				break;
			}
		}
		return results;
	}	
	
	/**
	 * Danh sách Người dùng không nằm trong Vai trò của Đơn vị
	 * @param organization
	 * @param roleId
	 * @return
	 */
	public List<Document> getListUsersNotInRoleInOrganizationToResponse(Organization organization, String roleId) {
		List<Document> results=new ArrayList<>();
		for(RoleOrganizationExpand roleOrganizationExpand:organization.getRoleOrganizationExpands()) {
			if(roleOrganizationExpand.getRoleId().equalsIgnoreCase(roleId)) {
				List<UserOrganizationExpand> userOrganizationExpands = organization.getUserOrganizationExpands().stream().filter(e->!roleOrganizationExpand.getUserIds().contains(e.getUserId())).collect(Collectors.toList());
				for (UserOrganizationExpand userOrganizationExpand : userOrganizationExpands) {
					results.add(userOrganizationExpandUtil.convertUserOrganizationExpand(userOrganizationExpand, false, null));
				}
				break;
			}
		}
		return results;
	}	
	
	public Document buildMetaOrganizations(Organization organization) throws InterruptedException, ExecutionException{
		Document result=new Document();
		result=toCommon(organization, true, true, true);
		
		OrganizationFilter organizationFilter=new OrganizationFilter();
		organizationFilter.setParentId(organization.getId());
		List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
		
		List<Document> subOrganizations=new ArrayList<Document>();
		if(organizations.size()>0) {
			ExecutorService executor = Executors.newFixedThreadPool(organizations.size());
			List<Future<Document>> listFuture = new ArrayList<Future<Document>>();
			
			for (Organization item : organizations) {
				listFuture.add(executor.submit(new Callable<Document>() {
					@Override
					public Document call() throws Exception {
						return buildMetaOrganizations(item);
					}
				}));	
			}
			
			for (Future<Document> future : listFuture) {
				subOrganizations.add(future.get());
			}
			executor.shutdown();
		}
		
		result.append("subOrganizations", subOrganizations);
		return result;
	}
	
	public Document buildMetaOrganizationUserSystems(Organization organization, boolean includeSub, String active) throws InterruptedException, ExecutionException {
		Document result=new Document();
		/* Lấy user hiện tại */
		result = toCommon(organization, true, false, false);
		
		if(includeSub) {
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.setParentId(organization.getId());
			
			List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
			List<Document> docSubOrganizations=new ArrayList<Document>();
			if(organizations.size()>0) {
				/* Khởi tạo threads */
				ExecutorService executor = Executors.newFixedThreadPool(organizations.size());
				List<Future<Document>> listFuture = new ArrayList<Future<Document>>();
				
				for (Organization itemOrg : organizations) {
					listFuture.add(executor.submit(new Callable<Document>() {
						@Override
						public Document call() throws Exception {
							return buildMetaOrganizationUserSystems(itemOrg, includeSub, active);
						}
					}));
				}
				
				for (Future<Document> future : listFuture) {
					docSubOrganizations.add(future.get());
				}
				executor.shutdown();
			}
			result.append("subOrganizations", docSubOrganizations);
		}
		return result;
	}
	
	public Document toReportUserUsings(List<String> userIdsLoged, Organization organization, boolean includeSub, UserAccessStatus accessStatus) throws InterruptedException, ExecutionException {
		Document result=new Document();
		/* Lấy user hiện tại */
		result = toCommon(organization, false, false, false);
		if(accessStatus!=null) {
			if(accessStatus.equals(UserAccessStatus.logged)) {
				result.put("userOrganizationExpands", getListUserOrganizationExpandToResponse(organization, true, userIdsLoged, "true"));
			}else {
				result.put("userOrganizationExpands", getListUserOrganizationExpandToResponse(organization, true, userIdsLoged, "false"));
			}
		}else {
			result.put("userOrganizationExpands", getListUserOrganizationExpandToResponse(organization, true, userIdsLoged, null));
		}
		
		/* Lấy thêm suborg - đệ quy */
		if(includeSub) {
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.setParentId(organization.getId());
			List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
			
			List<Document> docSubOrganizations=new ArrayList<Document>();
			if(organizations.size()>0) {
				/* Khởi tạo threads */
				ExecutorService executor = Executors.newFixedThreadPool(organizations.size());
				List<Future<Document>> listFuture = new ArrayList<Future<Document>>();
				
				for (Organization itemOrg : organizations) {
					listFuture.add(executor.submit(new Callable<Document>() {
						@Override
						public Document call() throws Exception {
							return toReportUserUsings(userIdsLoged, itemOrg, includeSub, accessStatus);
						}
					}));
				}
				
				for (Future<Document> future : listFuture) {
					docSubOrganizations.add(future.get());				
				}
				executor.shutdown();
			}
			result.append("subOrganizations", docSubOrganizations);
		}
		return result;
	}
}
