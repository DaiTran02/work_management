package ws.core.services;

import java.util.List;
import java.util.Optional;

import ws.core.enums.OrganizationLevel;
import ws.core.model.Organization;
import ws.core.model.RoleTemplate;
import ws.core.model.User;
import ws.core.model.embeded.UserOrganizationExpand.AddFromSource;
import ws.core.model.filter.OrganizationFilter;
import ws.core.model.request.ReqGroupOrganizationCreate;
import ws.core.model.request.ReqGroupOrganizationUpdate;
import ws.core.model.request.ReqOrganizationCreate;
import ws.core.model.request.ReqOrganizationCreatePartner;
import ws.core.model.request.ReqOrganizationUpdate;
import ws.core.model.request.ReqOrganizationUpdatePartner;
import ws.core.model.request.ReqRoleOrganizationCreate;
import ws.core.model.request.ReqRoleOrganizationUpdate;
import ws.core.model.request.ReqUserLoginAddToOrganization;
import ws.core.model.request.ReqUserOrganizationAdds;
import ws.core.model.request.ReqUserOrganizationAddsPartner;
import ws.core.model.request.ReqUserOrganizationRemoves;
import ws.core.model.request.ReqUserOrganizationRemovesPartner;
import ws.core.model.request.ReqUserOrganizationUpdate;
import ws.core.model.response.ResUserOrganizationAddsPartner;
import ws.core.model.response.ResUserOrganizationRemovesPartner;

/**
 * The Interface OrganizationService.
 */
public interface OrganizationService {

	/**
	 * Tìm tổ chức theo id.
	 *
	 * @param organizationId the organization id
	 * @return the optional
	 */
	public Optional<Organization> findOrganizationById(String organizationId);
	
	/**
	 * Lấy tổ chức theo id.
	 *
	 * @param organizationId the organization id
	 * @return the organization by id
	 */
	public Organization getOrganizationById(String organizationId);
	
	/**
	 * Xóa tổ chức theo id.
	 *
	 * @param organizationId the organization id
	 * @return the organization
	 */
	public Organization deleteOrganizationById(String organizationId);
	
	/**
	 * Xóa tổ chức theo unitCode
	 * @param unitCode
	 * @return
	 */
	public Organization deleteOrganizationByUnitCode(String unitCode);
	
	/**
	 * Thêm tổ chức.
	 *
	 * @param reqOrganizationCreate the req organization create
	 * @param creator the creator
	 * @return the organization
	 */
	public Organization createOrganization(ReqOrganizationCreate reqOrganizationCreate, User creator);
	
	/**
	 * Thêm tổ chức
	 * @param reqOrganizationCreatePartner
	 * @param creator
	 * @return
	 */
	public Organization createOrganization(ReqOrganizationCreatePartner reqOrganizationCreatePartner, User creator);
	
	
	/**
	 * Cập nhật tổ chức.
	 *
	 * @param organizationId the organization id
	 * @param reqOrganizationUpdate the req organization update
	 * @return the organization
	 */
	public Organization updateOrganization(String organizationId, ReqOrganizationUpdate reqOrganizationUpdate);
	
	/**
	 * Cập nhật tổ chức
	 * @param unitCode
	 * @param reqOrganizationUpdatePartner
	 * @return
	 */
	public Organization updateOrganization(String unitCode, ReqOrganizationUpdatePartner reqOrganizationUpdatePartner);
	
	
	/**
	 * Lấy path ids.
	 *
	 * @param organization the organization
	 * @return the path organization
	 */
	public String getPathOrganization(Organization organization);
	
	/**
	 * Số lượng tổ chức.
	 *
	 * @param organizationFilter the organization filter
	 * @return the long
	 */
	public long countOrganizationAll(OrganizationFilter organizationFilter);
	
	/**
	 * Danh sách tổ chức.
	 *
	 * @return the list
	 */
	public List<Organization> findOrganizationAll();
	
	/**
	 * Danh sách tổ chức.
	 *
	 * @param organizationFilter the organization filter
	 * @return the list
	 */
	public List<Organization> findOrganizationAll(OrganizationFilter organizationFilter);
	
	/**
	 * Thêm người dùng vào tổ chức.
	 *
	 * @param organizationId the organization id
	 * @param reqUserOrganizationAdds the req user organization adds
	 * @return the organization
	 */
	public Organization addUsersToOrganization(String organizationId, ReqUserOrganizationAdds reqUserOrganizationAdds);
	
	/**
	 * Thêm người dùng vào tổ chức.
	 *
	 * @param organizationId the organization id
	 * @param reqUserOrganizationAdds the req user organization adds
	 * @return the organization
	 */
	public ResUserOrganizationAddsPartner addUsersToOrganizationPartner(String organizationCode, ReqUserOrganizationAddsPartner reqUserOrganizationAddsPartner);
	
	
	/**
	 * Thêm người dùng vào tổ chức
	 * @param organizationId
	 * @param userIds
	 * @return
	 */
	public Organization addUsersToOrganization(String organizationId, List<String> userIds, AddFromSource addFromSource);
	
	/**
	 * Bỏ người dùng ra khỏi tổ chức.
	 *
	 * @param organizationId the organization id
	 * @param reqUserOrganizationRemoves the req user organization removes
	 * @return the organization
	 */
	public Organization removeUsersToOrganization(String organizationId, ReqUserOrganizationRemoves reqUserOrganizationRemoves);
	
	/**
	 * Bỏ người dùng ra khỏi tổ chức.
	 *
	 * @param organizationId the organization id
	 * @param reqUserOrganizationRemoves the req user organization removes
	 * @return the organization
	 */
	public ResUserOrganizationRemovesPartner removeUsersToOrganizationPartner(String organizationCode, ReqUserOrganizationRemovesPartner reqUserOrganizationRemovesPartner);
	
	/**
	 * Bỏ người dùng ra khỏi tổ chức
	 * @param organizationId
	 * @param userIds
	 * @return
	 */
	public Organization removeUsersToOrganization(String organizationId, List<String> userIds);
	
	/**
	 * Cập nhật thông tin người dùng trong tổ chức sau khi thêm.
	 *
	 * @param organizationId the organization id
	 * @param userId the user id
	 * @param reqUserOrganizationUpdate the req user organization update
	 * @return the organization
	 */
	public Organization updateUserToOrganization(String organizationId, String userId, ReqUserOrganizationUpdate reqUserOrganizationUpdate);
	
	
	
	
	/**
	 * Tạo nhóm giúp việc cho Đơn vị.
	 *
	 * @param organizationId the organization id
	 * @param reqGroupOrganizationCreate the req group organization create
	 * @param creator the creator
	 * @return the organization
	 */
	public Organization createGroupToOrganization(String organizationId, ReqGroupOrganizationCreate reqGroupOrganizationCreate, User creator);
	
	/**
	 * Cập nhật nhóm giúp việc cho Đơn vị.
	 *
	 * @param organizationId the organization id
	 * @param groupId the group id
	 * @param reqGroupOrganizationUpdate the req group organization update
	 * @return the organization
	 */
	public Organization updateGroupFromOrganization(String organizationId, String groupId, ReqGroupOrganizationUpdate reqGroupOrganizationUpdate);
	
	/**
	 * Xóa nhóm giúp việc cho Đơn vị.
	 *
	 * @param organizationId the organization id
	 * @param groupId the group id
	 * @return the organization
	 */
	public Organization deleteGroupFromOrganization(String organizationId, String groupId);
	
	/**
	 * Tìm nhóm giúp việc của Đơn vị.
	 *
	 * @param organizationId the organization id
	 * @param groupId the group id
	 * @return the organization
	 */
	public Organization findOrganizationGroupById(String organizationId, String groupId);
	
	/**
	 * Tao vai trò của Đơn vị.
	 *
	 * @param organizationId the organization id
	 * @param reqRoleOrganizationCreate the req role organization create
	 * @param creator the creator
	 * @return the organization
	 */
	public Organization createRoleOrganization(String organizationId, ReqRoleOrganizationCreate reqRoleOrganizationCreate, User creator);
	
	/**
	 * Cập nhật vai trò của Đơn vị.
	 *
	 * @param organizationId the organization id
	 * @param roleId the role id
	 * @param reqRoleOrganizationUpdate the req role organization update
	 * @return the organization
	 */
	public Organization updateRoleOrganization(String organizationId, String roleId, ReqRoleOrganizationUpdate reqRoleOrganizationUpdate);
	
	/**
	 * Xóa vai trò của Đơn vị.
	 *
	 * @param organizationId the organization id
	 * @param roleId the role id
	 * @return the organization
	 */
	public Organization deleteRoleOrganization(String organizationId, String roleId);
	
	
	
	/**
	 * Tìm vai trò của Đơn vị .
	 *
	 * @param organizationId the organization id
	 * @param roleId the role id
	 * @return the organization
	 */
	public Organization findOrganizationRoleById(String organizationId, String roleId);
	
	/**
	 * Lưu tổ chức.
	 *
	 * @param organization the organization
	 * @return the organization
	 */
	public Organization save(Organization organization);
	
	/**
	 * Thực hiện cập nhật bảo toàn dữ liệu chuẩn
	 * Chức năng chạy ngầm bằng thread.
	 */
	public void updateReference();
	
	/**
	 * Tìm đơn vị bởi mã Đơn vị Quốc gia
	 * @param unitCode
	 * @return
	 */
	public Optional<Organization> findOrganizationByUnitCode(String unitCode);
	
	/**
	 * Lấy đơn vị bởi mã Đơn vị Quốc gia
	 * @param unitCode
	 * @return
	 */
	public Organization getOrganizationByUnitCode(String unitCode);
	
	/**
	 * Cập nhật vai trò cho đơn vị, nếu có tham chiếu đến vai trò mẫu
	 * @param organization
	 * @param roleTemplate
	 * @return
	 */
	public Organization updateRoleOrganizationExpandByRoleTemplate(Organization organization, RoleTemplate roleTemplate);
	
	/**
	 * Đếm số đơn vị con trực thuộc
	 * @param organization
	 * @return
	 */
	public long countSubOrganization(Organization organization);
	
	/**
	 * Thêm người dùng vào vai trò trong đơn vị
	 * @param organizationId
	 * @param roleId
	 * @param userId
	 * @return
	 */
	public boolean addUserToRoleOrganization(String organizationId, String roleId, String userId);
	
	/**
	 * Bỏ người dùng ra khỏi vai trò trong đơn vị
	 * @param organization
	 * @param roleId
	 * @param userId
	 * @return
	 */
	public boolean removeUserToRoleOrganization(String organizationId, String roleId, String userId);
	
	/**
	 * Thêm người dùng vào vai trò trong đơn vị
	 * @param organizationId
	 * @param groupId
	 * @param userId
	 * @return
	 */
	public boolean addUserToGroupOrganization(String organizationId, String groupId, String userId);
	
	/**
	 * Bỏ người dùng ra khỏi vai trò trong đơn vị
	 * @param organization
	 * @param groupId
	 * @param userId
	 * @return
	 */
	public boolean removeUserToGroupOrganization(String organizationId, String groupId, String userId);
	
	/**
	 * Thêm người dùng vào đơn vị cho lần đầu đăng nhập, với lựa chọn nhóm và vai trò kèm theo
	 * @param reqUserLoginAddToOrganization
	 * @return
	 */
	public boolean addUserToOrganizationFirstLogin(ReqUserLoginAddToOrganization reqUserLoginAddToOrganization, String userId);
	
	/**
	 * Danh sách tổ chức của người dùng
	 * @param userId
	 * @return
	 */
	public List<Organization> getListOrganizationOfUser(String userId);
	
	/**
	 * Danh sách id tổ chức của các cấp con
	 * @param orrganizationId
	 * @return
	 */
	public List<String> getChildOrganizationsAllLevel(Organization orrganization);

	/**
	 * Auto detect cấp loại Đơn vị là Cơ quan hay Phòng ban, dựa vào unitCode
	 * @param unitCode
	 * @return
	 */
	public OrganizationLevel autoDetectDefaultByUnitCode(String unitCode);

	public Optional<Organization> findOne(OrganizationFilter organizationFilter);
	
}
