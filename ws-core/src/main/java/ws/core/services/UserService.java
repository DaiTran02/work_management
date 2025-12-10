package ws.core.services;

import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import ws.core.model.Organization;
import ws.core.model.User;
import ws.core.model.filter.UserFilter;
import ws.core.model.request.ReqUserChangePassword;
import ws.core.model.request.ReqUserCreate;
import ws.core.model.request.ReqUserImportFromLdap;
import ws.core.model.request.ReqUserLogin;
import ws.core.model.request.ReqUserResetPassword;
import ws.core.model.request.ReqUserUpdate;
import ws.core.model.request.site.ReqUserChangePasswordSite;
import ws.core.model.request.site.ReqUserUpdateSite;

public interface UserService {
	/**
	 * Tìm user bằng userName
	 * @param userName
	 * @return
	 */
	public Optional<User> findUserByUserName(String userName);
	
	/**
	 * Lấy user bằng userName
	 * @param userName
	 * @return
	 */
	public User getUserByUserName(String userName);
	
	/**
	 * Tạo user mới
	 * @param reqUserCreate
	 * @return
	 */
	public User createUser(ReqUserCreate reqUserCreate, User creator);
	
	/**
	 * Lấy danh sách người dùng
	 * @return
	 */
	public List<User> findUserAll();
	
	/**
	 * Lấy danh sách người dùng
	 * @param userFilter
	 * @return
	 */
	public List<User> findUserAll(UserFilter userFilter);
	
	/**
	 * Tổng số lượng người dùng
	 * @return
	 */
	public long countUserAll();
	
	/**
	 * Tổng số lượng người dùng
	 * @param userFilter
	 * @return
	 */
	public long countUserAll(UserFilter userFilter);
	
	/**
	 * Tìm người dùng theo userId
	 * @param userId
	 * @return
	 */
	public Optional<User> findUserById(String userId);
	
	/**
	 * Lấy khoản theo userId
	 * @param id
	 * @return
	 */
	public User getUserById(String userId);
	
	/**
	 * Xóa tài khoản theo userId
	 * @param id
	 * @return
	 */
	public User deleteUserById(String userId);
	
	/**
	 * Cập nhật tài khoản theo userId
	 * @param userId
	 * @param reqUserUpdate
	 * @return
	 */
	public User updateUserById(String userId, ReqUserUpdate reqUserUpdate);
	
	/**
	 * Cập nhật tài khoản theo userId
	 * @param userId
	 * @param reqUserUpdate
	 * @return
	 */
	public User updateUserById(String userId, ReqUserUpdateSite reqUserUpdateSite);
	
	/**
	 * Xác thực thông tin người dùng
	 * @param reqUserLogin
	 * @return
	 */
	public User loginUser(HttpServletRequest request, ReqUserLogin reqUserLogin);
	
	/**
	 * Thay đổi mật khẩu
	 * @param userId
	 * @param reqUserChangePassword
	 * @return
	 */
	public User changePasswordUser(String userId, ReqUserChangePassword reqUserChangePassword);
	
	/**
	 * Thay đổi mật khẩu
	 * @param userId
	 * @param reqUserChangePassword
	 * @return
	 */
	public User changePasswordUser(String userId, ReqUserChangePasswordSite reqUserChangePasswordSite);
	
	/**
	 * Đặt lại mật khẩu
	 * @param userId
	 * @param reqUserResetPassword
	 * @return
	 */
	public User resetPasswordUser(String userId, ReqUserResetPassword reqUserResetPassword);
	
	/**
	 * Cập nhật thông tin tổ chức liên quan cho mỗi user
	 * Chức năng sẽ chạy ngầm mặc định bằng thread
	 */
	public void updateOrganizationEachUser();
	
	/**
	 * Cập nhật thông tin tổ chức liên quan cho user
	 * Chức năng này dùng khi có thay đổi người dùng trong đơn vị
	 * @param userId
	 */
	public void updateOrganizationOfUser(String userId);
	
	/**
	 * Lưu user model
	 * @param user
	 * @return
	 */
	public User saveUser(User user);
	
	/**
	 * Gán đã xem qua lần đầu với login tự chọn đơn vị
	 * @param userId
	 * @return
	 */
	public User setReviewedOfFirstReview(String userId);
	
	/**
	 * Gán đã hướng dẫn sử dụng trên WebUI
	 * @param userId
	 * @return
	 */
	public User setGuidedWebUI(String userId);
	
	/**
	 * Lấy toàn bộ permission của userId
	 * @param userId
	 * @return
	 */
	public List<String> getAllPermission(String userId);

	/**
	 * Thêm người dùng vào hệ thống với username
	 * @param username
	 * @param organization
	 * @return
	 */
	public Optional<User> addUserByUsernameLdap(String username);
	
	/**
	 * Thêm người dùng vào tổ chức với username, dùng qua trường hợp thêm văn bản từ VNPT
	 * @param username
	 * @param organization
	 * @return
	 */
	public Optional<User> addUserToOrganizationByUsernameLdap(String username, Organization organization);

	/**
	 * Thêm người dùng từ ldap vào hệ thống
	 * @param reqUsersImportFromLdap
	 * @param user
	 * @return
	 */
	public List<User> createUsersImportFromLdap(@Valid ReqUserImportFromLdap reqUserImportFromLdap, User user);

	
}
