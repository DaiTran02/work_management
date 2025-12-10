package ws.core.resource.admin;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import ws.core.advice.NotAcceptableExceptionAdvice;
import ws.core.enums.UserProvider;
import ws.core.model.Organization;
import ws.core.model.User;
import ws.core.model.data.UserCodePublic;
import ws.core.model.embeded.GroupOrganizationExpand;
import ws.core.model.embeded.RoleOrganizationExpand;
import ws.core.model.filter.FirstReviewFilter;
import ws.core.model.filter.SkipLimitFilter;
import ws.core.model.filter.UserFilter;
import ws.core.model.request.ReqUserChangePassword;
import ws.core.model.request.ReqUserCodeAuth;
import ws.core.model.request.ReqUserCreate;
import ws.core.model.request.ReqUserImportFromLdap;
import ws.core.model.request.ReqUserLogin;
import ws.core.model.request.ReqUserResetPassword;
import ws.core.model.request.ReqUserUpdate;
import ws.core.model.response.ResponseAPI;
import ws.core.model.response.util.OrganizationUtil;
import ws.core.model.response.util.UserUtil;
import ws.core.security.CustomUserDetails;
import ws.core.services.OrganizationService;
import ws.core.services.PropsService;
import ws.core.services.UserService;
import ws.core.services.redis.UserCodePublicServiceRD;

@RestController
@RequestMapping("/api/admin")
public class UserControllerAdmin {
	
	@Autowired 
	protected AuthenticationProvider authenticationProvider;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserUtil userUtil;
	
	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private OrganizationUtil organizationUtil;
	
	@Autowired
	private UserCodePublicServiceRD userTokenPublicServiceRD;
	
	@Autowired
	private PropsService propsService;
	
	/**
	 * Đăng nhập cho admin
	 * @param request
	 * @param reqUserLogin
	 * @return
	 */
	@PostMapping("/users/login")
	public Object login(HttpServletRequest request, @RequestBody @Valid ReqUserLogin reqUserLogin){
		ResponseAPI responseAPI=new ResponseAPI();
		
		User user=userService.loginUser(request, reqUserLogin);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(userUtil.toAdminLoginResponse(user));
		return responseAPI.build();
	}
	
	@PostMapping("/users/login-by-code")
	public Object loginByCode(
			HttpServletRequest request, 
			@RequestBody @Valid ReqUserCodeAuth reqUserTokenAuth){
		ResponseAPI responseAPI=new ResponseAPI();
		
		if(propsService.isSecurityAuthCodeEnable()) {
			UserCodePublic userTokenPublic=userTokenPublicServiceRD.pull(reqUserTokenAuth.getCode());
			User user = userService.getUserByUserName(userTokenPublic.getUsername());
			/*if(user.getUsername().equalsIgnoreCase("administrator")) {
				throw new NotAcceptableExceptionAdvice("Tài khoản không được phép truy cập");
			}*/
			
			if(!user.isActive()) {
				throw new NotAcceptableExceptionAdvice("Tài khoản đã bị vô hiệu hóa, vui lòng liên hệ quản trị");
			}
			
			/* 2024-11-11 Bỏ qua để thiết lập chế độ cho chọn Đơn vị nếu chưa có ở front-end
			if(user.getBelongOrganizations().size()==0) {
				throw new NotAcceptableExceptionAdvice("Tài khoản không nằm trong tổ chức nào");
			}*/
			
			responseAPI.setStatus(HttpStatus.OK);
			responseAPI.setMessage("Thành công");
			responseAPI.setResult(userUtil.toSiteLoginResponse(user));
			return responseAPI.build();
		}else {
			responseAPI.setStatus(HttpStatus.NOT_ACCEPTABLE);
			responseAPI.setMessage("Dịch vụ xác thực qua code không hỗ trợ");
			return responseAPI.build();
		}
	}
	
	@GetMapping("/users/sign-in-organization/{organizationId}")
	public Object signInOrganization(@PathVariable(name = "organizationId", required = true) String organizationId){
		ResponseAPI responseAPI=new ResponseAPI();

		CustomUserDetails userDetails = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User user=userDetails.getUser();
		
		Organization organization=organizationService.getOrganizationById(organizationId);
		
		Document userOrganizationExpand = organizationUtil.getUserOrganizationExpandToResponse(organization, user.getId(), false);
		
		Document group=new Document();
		for(GroupOrganizationExpand groupOrganizationExpand:organization.getGroupOrganizationExpands()) {
			if(groupOrganizationExpand.getUserIds().contains(user.getId())) {
				group.append("id", groupOrganizationExpand.getGroupId());
				group.append("name", groupOrganizationExpand.getName());
				group.append("description", groupOrganizationExpand.getDescription());
				break;
			}
		}
		
		Document roles=new Document();
		List<String> rolesName=new ArrayList<String>();
		List<String> rolesPermissionKey=new ArrayList<String>();
		for(RoleOrganizationExpand roleOrganizationExpand:organization.getRoleOrganizationExpands()) {
			if(roleOrganizationExpand.getUserIds().contains(user.getId())) {
				rolesName.add(roleOrganizationExpand.getName());
				rolesPermissionKey.addAll(roleOrganizationExpand.getPermissionKeys());
			}
		}
		roles.append("name", rolesName);
		roles.append("permissionKeys", rolesPermissionKey);
		
		Document result=new Document();
		result.put("id", organization.getId());
		result.put("name", organization.getName());
		result.put("description", organization.getDescription());
		result.put("userExpand", userOrganizationExpand);
		result.put("group", group);
		result.put("roles", roles);
		
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(result);
		return responseAPI.build();
	}
	
	/**
	 * Tạo tài khoản
	 * @param reqUserCreate
	 * @return
	 */
	@PostMapping("/users")
	public Object create(@RequestBody @Valid ReqUserCreate reqUserCreate){
		ResponseAPI responseAPI=new ResponseAPI();
		CustomUserDetails userCreator = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User userCreate=userService.createUser(reqUserCreate, userCreator.getUser());
		responseAPI.setStatus(HttpStatus.CREATED);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(userUtil.toAdminResponse(userCreate));
		return responseAPI.build();
	}
	
	@PostMapping("/users/import-from-ldap")
	public Object importFromLdap(@RequestBody @Valid ReqUserImportFromLdap reqUserImportFromLdap){
		ResponseAPI responseAPI=new ResponseAPI();
		CustomUserDetails userCreator = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<User> usersCreate=userService.createUsersImportFromLdap(reqUserImportFromLdap, userCreator.getUser());
		responseAPI.setStatus(HttpStatus.CREATED);
		responseAPI.setMessage("Thành công");
		List<Document> results=new ArrayList<>();
		for (User user : usersCreate) {
			results.add(userUtil.toAdminResponse(user));
		}
		responseAPI.setTotal(results.size());
		responseAPI.setResult(results);
		return responseAPI.build();
	}
	
	/**
	 * Danh sách tài khoản
	 * @param skip
	 * @param limit
	 * @param excludeOrganizationId
	 * @param organizationEmpty
	 * @param keyword
	 * @param active
	 * @return
	 */
	@GetMapping("/users")
	public Object list(
			@RequestParam(name = "skip", required = true) int skip, 
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "active", required = false) Boolean active,
			@RequestParam(name = "archive", required = false) Boolean archive,
			@RequestParam(name = "includeOrganizationId", required = false) String includeOrganizationId,
			@RequestParam(name = "excludeOrganizationId", required = false) String excludeOrganizationId,
			@RequestParam(name = "hasFirstReview", required = false) Boolean hasFirstReview,
			@RequestParam(name = "firstReviewed", required = false) Boolean firstReviewed,
			@RequestParam(name = "provider", required = false) String provider,
			@RequestParam(name = "hasUsed", required = false) Boolean hasUsed) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		UserFilter userFilter=new UserFilter();
		userFilter.setKeySearch(keyword);
		userFilter.setActive(active);
		userFilter.setArchive(archive);
		if(includeOrganizationId!=null) {
			userFilter.setIncludeOrganizationIds(Arrays.asList(includeOrganizationId));
		}
		
		if(excludeOrganizationId!=null) {
			userFilter.setExcludeOrganizationIds(Arrays.asList(excludeOrganizationId));
		}
		userFilter.setProvider(provider);
		userFilter.setHasUsed(hasUsed);
		userFilter.setSkipLimitFilter(new SkipLimitFilter(skip, limit));
		
		/* Lọc những tài khoản đã chọn khi lần đầu login */
		FirstReviewFilter firstReviewFilter=null;
		if(hasFirstReview!=null) {
			if(firstReviewFilter==null) {
				firstReviewFilter=new FirstReviewFilter();
			}
		}
		
		if(firstReviewed!=null) {
			if(firstReviewFilter==null) {
				firstReviewFilter=new FirstReviewFilter();
			}
			firstReviewFilter.setReviewed(firstReviewed);
		}
		
		if(firstReviewFilter!=null) {
			userFilter.setFirstReviewFilter(firstReviewFilter);
		}
		
		long total=userService.countUserAll(userFilter);
		List<User> users=userService.findUserAll(userFilter);
		
		List<Document> results=new ArrayList<Document>();
		for (User item : users) {
			results.add(userUtil.toAdminResponse(item));
		}
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		responseAPI.setResult(results);
		return responseAPI.build();
	}
	
	/**
	 * Chi tiết tài khoản
	 * @param userId
	 * @return
	 */
	
	@GetMapping("/users/{userId}")
	public Object getUser(Principal principal, @PathVariable(name = "userId", required = true) String userId) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		User user=userService.getUserById(userId);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(userUtil.toAdminResponse(user));
		return responseAPI.build();
	}
	
	/**
	 * Cập nhật tài khoản
	 * @param userId
	 * @param reqUserUpdate
	 * @return
	 */
	@PutMapping("/users/{userId}")
	public Object update(@PathVariable(name = "userId", required = true) String userId,
			@RequestBody @Valid ReqUserUpdate reqUserUpdate){
		ResponseAPI responseAPI=new ResponseAPI();
		User user=userService.updateUserById(userId, reqUserUpdate);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(userUtil.toAdminResponse(user));
		return responseAPI.build();
	}
	
	/**
	 * Xóa tài khoản
	 * @param userId
	 * @return
	 */
	@DeleteMapping("/users/{userId}")
	public Object deleteUser(@PathVariable(name = "userId", required = true) String userId) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		User user=userService.deleteUserById(userId);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Đã xóa người dùng thành công");
		responseAPI.setResult(user);
		return responseAPI.build();
	}
	
	/**
	 * Thay đổi mật khẩu cho tài khoản
	 * @param userId
	 * @param userChangePassword
	 * @return
	 */
	@PutMapping("/users/{userId}/change-password")
	public Object changePassword(
			@PathVariable(name = "userId", required = true) String userId, 
			@RequestBody @Valid ReqUserChangePassword userChangePassword){
		ResponseAPI responseAPI=new ResponseAPI();
		
		userService.changePasswordUser(userId, userChangePassword);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Đổi mật khẩu thành công");
		return responseAPI.build();
	}
	
	/**
	 * Đặt lại mật khẩu cho tài khoản
	 * @param userId
	 * @param userResetPassword
	 * @return
	 */
	@PutMapping("/users/{userId}/reset-password")
	public Object resetPassword(
			@PathVariable(name = "userId", required = true) String userId, 
			@RequestBody @Valid ReqUserResetPassword userResetPassword){
		ResponseAPI responseAPI=new ResponseAPI();
		
		userService.resetPasswordUser(userId, userResetPassword);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Đổi mật khẩu thành công");
		return responseAPI.build();
	}
	
	@PutMapping("/users/{userId}/set-reviewed-of-first-review")
	public Object setReviewedOfFirstReview(@PathVariable(name = "userId", required = true) String userId){
		ResponseAPI responseAPI=new ResponseAPI();
		User user=userService.setReviewedOfFirstReview(userId);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(userUtil.toAdminResponse(user));
		return responseAPI.build();
	}
	
	@GetMapping("/users/provider")
	public Object getProvider() {
		ResponseAPI responseAPI=new ResponseAPI();
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(UserProvider.values());
		return responseAPI.build();
	}
}
