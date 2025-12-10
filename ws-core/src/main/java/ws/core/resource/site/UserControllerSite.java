package ws.core.resource.site;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import ws.core.advice.NotAcceptableExceptionAdvice;
import ws.core.model.Organization;
import ws.core.model.RefreshToken;
import ws.core.model.User;
import ws.core.model.data.UserCodePublic;
import ws.core.model.embeded.GroupOrganizationExpand;
import ws.core.model.embeded.RoleOrganizationExpand;
import ws.core.model.embeded.UserOrganizationExpand;
import ws.core.model.request.ReqUserCodeAuth;
import ws.core.model.request.ReqUserLogin;
import ws.core.model.request.ReqUserLoginAddToOrganization;
import ws.core.model.request.ReqUserRefreshToken;
import ws.core.model.request.site.ReqUserChangePasswordSite;
import ws.core.model.request.site.ReqUserUpdateSite;
import ws.core.model.response.ResponseAPI;
import ws.core.model.response.util.OrganizationUtil;
import ws.core.model.response.util.RefreshTokenUtil;
import ws.core.model.response.util.UserUtil;
import ws.core.security.CustomUserDetails;
import ws.core.services.OrganizationService;
import ws.core.services.PropsService;
import ws.core.services.RefreshTokenService;
import ws.core.services.UserService;
import ws.core.services.redis.UserCodePublicServiceRD;

@RestController
@RequestMapping("/api/site")
public class UserControllerSite {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserUtil userUtil;
	
	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private OrganizationUtil organizationUtil;
	
	@Autowired
	private RefreshTokenUtil refreshTokenUtil;
	
	@Autowired
	private RefreshTokenService refreshTokenService;
	
	@Autowired
	private UserCodePublicServiceRD userTokenPublicServiceRD;
	
	@Autowired
	private PropsService propsService;
	
	@PostMapping("/users/login")
	public Object login(HttpServletRequest request, @RequestBody @Valid ReqUserLogin reqUserLogin){
		ResponseAPI responseAPI=new ResponseAPI();

		User user=userService.loginUser(request, reqUserLogin);
		/*if(user.getUsername().equalsIgnoreCase("administrator")) {
			throw new NotAcceptableExceptionAdvice("Tài khoản không được phép truy cập");
		}*/
		
		if(!user.isActive()) {
			throw new NotAcceptableExceptionAdvice("Tài khoản đã bị khóa");
		}
		
		/* 2024-11-11 Bỏ qua để thiết lập chế độ cho chọn Đơn vị nếu chưa có ở front-end
		 * if(user.getBelongOrganizations().size()==0) {
			throw new NotAcceptableExceptionAdvice("Tài khoản không nằm trong tổ chức nào");
		}*/
		
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(userUtil.toSiteLoginResponse(user));
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
	
	@PostMapping("/users/refresh-token")
	public Object refreshToken(HttpServletRequest request, @RequestBody @Valid ReqUserRefreshToken reqUserRefreshToken){
		ResponseAPI responseAPI=new ResponseAPI();

		RefreshToken refreshToken=refreshTokenService.findRefreshTokenByRefreshToken(reqUserRefreshToken.getRefreshToken());
		refreshToken=refreshTokenService.verifyExpiration(refreshToken);
		
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(refreshTokenUtil.toSiteResponse(refreshToken));
		return responseAPI.build();
	}
	
	@GetMapping("/users/sign-in-organization/{organizationId}")
	public Object signInOrganization(@PathVariable(name = "organizationId", required = true) String organizationId){
		ResponseAPI responseAPI=new ResponseAPI();

		CustomUserDetails userDetails = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User user=userDetails.getUser();
		
		if(!user.isActive()) {
			throw new NotAcceptableExceptionAdvice("Tài khoản đã bị khóa");
		}
		
		Organization organization=null;
		if(ObjectId.isValid(organizationId)) {
			organization=organizationService.getOrganizationById(organizationId);
		}else {
			organization=organizationService.getOrganizationByUnitCode(organizationId);
		}
		
		Assert.notNull(organization, "Không tim thấy Đơn vị");
		Optional<UserOrganizationExpand> userOrganizationExpand =  organization.getUserOrganizationExpand(user.getId());
		
//		Tạm bỏ qua kiểm tra 2025-02-20
//		if(!userOrganizationExpand.isPresent()) {
//			throw new NotAcceptableExceptionAdvice("Tài khoản không thuộc Đơn vị");
//		}else if(!userOrganizationExpand.get().isActive()) {
//			throw new NotAcceptableExceptionAdvice("Tài khoản trong Đơn vị đã bị khóa");
//		}
		
		Document userExpandDocument = null;
		if(userOrganizationExpand.isPresent()) {
			userExpandDocument = organizationUtil.getUserOrganizationExpandToResponse(organization, user.getId(), false);
		}
		
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
		result.put("userExpand", userExpandDocument);
		result.put("group", group);
		result.put("roles", roles);
		
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(result);
		return responseAPI.build();
	}
	
	@PutMapping("/users/{userId}")
	public Object update(@PathVariable(name = "userId", required = true) String userId,
			@RequestBody @Valid ReqUserUpdateSite reqUserUpdateSite){
		CustomUserDetails userDetails = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User user=userDetails.getUser();
		if(!user.getId().equals(userId)) {
			throw new NotAcceptableExceptionAdvice("Tài khoản không có quyền cập nhật thông tin của người khác");
		}
		
		ResponseAPI responseAPI=new ResponseAPI();
		user=userService.updateUserById(userId, reqUserUpdateSite);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(userUtil.toSiteResponse(user));
		return responseAPI.build();
	}
	
	@PutMapping("/users/{userId}/change-password")
	public Object changePassword(
			@PathVariable(name = "userId", required = true) String userId, 
			@RequestBody @Valid ReqUserChangePasswordSite userChangePasswordSite){
		ResponseAPI responseAPI=new ResponseAPI();
		
		userService.changePasswordUser(userId, userChangePasswordSite);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Đổi mật khẩu thành công");
		return responseAPI.build();
	}
	
	/**
	 * Dùng cho người dùng truy cập site lần đầu nếu chưa có tổ chức
	 * @param userId
	 * @param reqUserLoginAddToOrganization
	 * @return
	 */
	@PutMapping("/users/{userId}/add-to-organization")
	public Object addUserToOrganization(
			@PathVariable(name = "userId", required = true) String userId, 
			@RequestBody @Valid ReqUserLoginAddToOrganization reqUserLoginAddToOrganization){
		ResponseAPI responseAPI=new ResponseAPI();
		
		if(organizationService.addUserToOrganizationFirstLogin(reqUserLoginAddToOrganization, userId)) {
			Organization organization = organizationService.getOrganizationById(reqUserLoginAddToOrganization.getOrganizationId());
			responseAPI.setStatus(HttpStatus.OK);
			responseAPI.setResult(organizationUtil.toSiteResponse(organization));
			responseAPI.setMessage("Thành công");
			return responseAPI.build();
		}
		responseAPI.setStatus(HttpStatus.NOT_MODIFIED);
		responseAPI.setMessage("Không thành công");
		return responseAPI.build();
	}
	
	/**
	 * Gán tài khoản này đã được hướng dẫn sử dụng trên WebUI
	 * @param userId
	 * @return
	 */
	@PutMapping("/users/{userId}/set-guided-webui")
	public Object setGuidedWebUI(@PathVariable(name = "userId", required = true) String userId){
		ResponseAPI responseAPI=new ResponseAPI();
		
		User user = userService.setGuidedWebUI(userId);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setResult(userUtil.toSiteResponse(user));
		responseAPI.setMessage("Thành công");
		return responseAPI.build();
	}
	
	@GetMapping("/users/{userId}")
	public Object getUser(@PathVariable(name = "userId", required = true) String userId) {
		ResponseAPI responseAPI=new ResponseAPI();
		User user = null;
		if(ObjectId.isValid(userId)) {
			user=userService.getUserById(userId);
		}else {
			user=userService.getUserByUserName(userId);
		}
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(userUtil.toSiteResponse(user));
		return responseAPI.build();
	}
	
}
