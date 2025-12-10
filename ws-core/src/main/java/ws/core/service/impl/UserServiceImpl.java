package ws.core.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import ws.core.advice.DuplicateKeyExceptionAdvice;
import ws.core.advice.NotAcceptableExceptionAdvice;
import ws.core.advice.NotFoundElementExceptionAdvice;
import ws.core.advice.ObjectIdExceptionAdvance;
import ws.core.advice.UnauthorizedExceptionAdvice;
import ws.core.enums.UserProvider;
import ws.core.model.Organization;
import ws.core.model.User;
import ws.core.model.User.BelongOrganization;
import ws.core.model.embeded.LdapUser;
import ws.core.model.embeded.UserOrganizationExpand.AddFromSource;
import ws.core.model.filter.OrganizationFilter;
import ws.core.model.filter.UserFilter;
import ws.core.model.request.ReqUserChangePassword;
import ws.core.model.request.ReqUserCreate;
import ws.core.model.request.ReqUserImportFromLdap;
import ws.core.model.request.ReqUserLogin;
import ws.core.model.request.ReqUserResetPassword;
import ws.core.model.request.ReqUserUpdate;
import ws.core.model.request.site.ReqUserChangePasswordSite;
import ws.core.model.request.site.ReqUserUpdateSite;
import ws.core.respository.UserRepository;
import ws.core.respository.UserRepositoryCustom;
import ws.core.security.CustomUserDetails;
import ws.core.services.LogAccessService;
import ws.core.services.OrganizationService;
import ws.core.services.OtpService;
import ws.core.services.UserService;

@Service
public class UserServiceImpl implements UserService{
	@Autowired 
	protected AuthenticationProvider authenticationProvider;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserRepositoryCustom userRepositoryCustom;
	
	@Autowired
    protected PasswordEncoder passwordEncoder;
	
	@Autowired
	private LogAccessService logAccessService;
	
	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private OtpService otpService;
	
	@Override
	public Optional<User> findUserByUserName(String userName) {
		return userRepository.findByUsername(userName);
	}

	public User getUserByUserName(String userName) {
		Optional<User> user = findUserByUserName(userName);
		if(user.isPresent()) {
			return user.get();
		}
		throw new NotFoundElementExceptionAdvice("Không tìm thấy tài khoản");
	}
	
	@Override
	public User createUser(ReqUserCreate reqUserCreate, User creator){
		User userCreate=new User();
		userCreate.setCreatedTime(new Date());
		userCreate.setUpdatedTime(new Date());
		userCreate.setUsername(reqUserCreate.getUsername());
		userCreate.setEmail(reqUserCreate.getEmail());
		userCreate.setFullName(reqUserCreate.getFullName());
		userCreate.setPhone(reqUserCreate.getPhone());
		userCreate.setPassword(passwordEncoder.encode(reqUserCreate.getPassword()));
		userCreate.setActive(reqUserCreate.isActive());
		userCreate.setActiveCode(reqUserCreate.getActiveCode());
		if(creator!=null) {
			userCreate.setCreatorId(creator.getId());
			userCreate.setCreatorName(creator.getFullName());
		}
		
		try {
			return userRepository.save(userCreate);
		} catch (Exception e) {
			if(e instanceof DuplicateKeyException) {
				throw new DuplicateKeyExceptionAdvice("Dữ liệu bị trùng khóa chính, vui lòng thử lại");
			}
			throw e;
		}
	}

	@Override
	public User updateUserById(String userId, ReqUserUpdate reqUserUpdate) {
		User userUpdate=getUserById(userId);
		userUpdate.setEmail(reqUserUpdate.getEmail());
		userUpdate.setFullName(reqUserUpdate.getFullName());
		userUpdate.setJobTitle(reqUserUpdate.getJobTitle());
		userUpdate.setPhone(reqUserUpdate.getPhone());
		userUpdate.setActive(reqUserUpdate.isActive());
		userUpdate.setActiveCode(reqUserUpdate.getActiveCode());
		return userRepository.save(userUpdate);
	}
	
	@Override
	public User updateUserById(String userId, ReqUserUpdateSite reqUserUpdateSite) {
		User userUpdate=getUserById(userId);
		userUpdate.setEmail(reqUserUpdateSite.getEmail());
		userUpdate.setFullName(reqUserUpdateSite.getFullName());
		userUpdate.setPhone(reqUserUpdateSite.getPhone());
		return userRepository.save(userUpdate);
	}

	@Override
	public List<User> findUserAll() {
		return userRepository.findAll();
	}

	@Override
	public List<User> findUserAll(UserFilter userFilter) {
		return userRepositoryCustom.findAll(userFilter);
	}

	@Override
	public long countUserAll() {
		return userRepository.count();
	}

	@Override
	public long countUserAll(UserFilter userFilter) {
		return userRepositoryCustom.countAll(userFilter);
	}

	@Override
	public Optional<User> findUserById(String userId) {
		if(ObjectId.isValid(userId))
			return userRepository.findById(new ObjectId(userId));
		throw new ObjectIdExceptionAdvance("userId ["+userId+"] không hợp lệ"); 
	}

	@Override
	public User getUserById(String userId) {
		Optional<User> user = findUserById(userId);
		if(user.isPresent()) {
			return user.get();
		}
		throw new NotFoundElementExceptionAdvice("Không tìm thấy tài khoản");
	}

	@Override
	public User deleteUserById(String userId) {
		User user=getUserById(userId);
		if(user.getBelongOrganizations().size()>0) {
			throw new NotAcceptableExceptionAdvice("Không thể xóa tài khoản này");
		}
		userRepository.delete(user);
		return user;
	}

	@Override
	public User loginUser(HttpServletRequest request, ReqUserLogin reqUserLogin) {
		User user=null;
		Optional<User> findUser=findUserByUserName(reqUserLogin.getUsername());
		/* Nếu có rồi thì phải xem loại provider để xác thực */
		if(findUser.isPresent()) {
			User userAuth=findUser.get();
			/* Nếu provider là ldap */
			if(userAuth.getProvider()!=null && userAuth.getProvider().equals(UserProvider.ldap)) {
				if(otpService.isEnable()) {
					Optional<LdapUser> auth=otpService.authLdap(reqUserLogin.getUsername(), reqUserLogin.getPassword());
					/*Xác thực thành công*/
					if(auth.isPresent()) {
						user=findUser.get();						
					}else {
						throw new UnauthorizedExceptionAdvice("Xác thực qua OTP không thành công");
					}
				}else {
					throw new UnauthorizedExceptionAdvice("Xác thực qua OTP không được kích hoạt");
				}
			}
			/* Nếu provider là local hoặc null */
			else {
				// Xác thực từ username và password.
				Authentication authentication = null;
				try {
					authentication = authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(reqUserLogin.getUsername(), reqUserLogin.getPassword()));
				} catch (Exception e) {
					throw new UnauthorizedExceptionAdvice("Xác thực không thành công");
				}
				
				// Nếu không xảy ra exception tức là thông tin hợp lệ
				CustomUserDetails customUser = (CustomUserDetails) authentication.getPrincipal();
				
				// Set thông tin authentication vào Security Context
				SecurityContextHolder.getContext().setAuthentication(authentication);
				
				/* Lấy thông tin user đã xác thực */
				user=customUser.getUser();
			}
		}
		/* Nếu chưa có và có khai báo sử dụng otpService thì phải xác thực LDAP */
		else if(otpService.isEnable()){
			Optional<LdapUser> auth=otpService.authLdap(reqUserLogin.getUsername(), reqUserLogin.getPassword());
			/*Xác thực thành công*/
			if(auth.isPresent()) {
				LdapUser ldapUser=auth.get();
				User createUser=new User();
				createUser.setUsername(ldapUser.getUsername());
				createUser.setEmail(ldapUser.getUserPrincipalName());
				createUser.setFullName(ldapUser.getFullName());
				createUser.setPassword(passwordEncoder.encode(reqUserLogin.getPassword()));
				createUser.setActiveCode(RandomStringUtils.randomAlphanumeric(8).toUpperCase());
				createUser.setActive(true);
				createUser.setProvider(UserProvider.ldap);
				createUser.setLdapInfo(ldapUser);
				
				user=saveUser(createUser);
			}else {
				throw new UnauthorizedExceptionAdvice("Xác thực không thành công");
			}
		}
		
		if(user==null) {
			throw new UnauthorizedExceptionAdvice("Xác thực không thành công");
		}
		
		if(!user.isActive()) {
			throw new NotAcceptableExceptionAdvice("Tài khoản đã bị khóa");
		}

		/* Ghi log access */
		logAccessService.createLogAccess(request, user);
		
		return user;
	}

	@Override
	public User changePasswordUser(String userId, ReqUserChangePassword reqUserChangePassword) {
		User userUpdate=getUserById(userId);
		if(userRepositoryCustom.checkPassword(userId, reqUserChangePassword.getPasswordOld())) {
			userUpdate.setLastChangePassword(new Date());
			userUpdate.setPassword(passwordEncoder.encode(reqUserChangePassword.getPasswordNew()));
			return userRepository.save(userUpdate);
		}
		throw new NotAcceptableExceptionAdvice("Thay đổi mật khẩu không thành công");
	}
	
	@Override
	public User changePasswordUser(String userId, ReqUserChangePasswordSite reqUserChangePasswordSite) {
		User userUpdate=getUserById(userId);
		if(userRepositoryCustom.checkPassword(userId, reqUserChangePasswordSite.getPasswordOld())) {
			userUpdate.setLastChangePassword(new Date());
			userUpdate.setPassword(passwordEncoder.encode(reqUserChangePasswordSite.getPasswordNew()));
			return userRepository.save(userUpdate);
		}
		throw new NotAcceptableExceptionAdvice("Thay đổi mật khẩu không thành công");
	}

	@Override
	public User resetPasswordUser(String userId, ReqUserResetPassword reqUserResetPassword) {
		User userUpdate=getUserById(userId);
		userUpdate.setLastChangePassword(new Date());
		userUpdate.setPassword(passwordEncoder.encode(reqUserResetPassword.getPasswordNew()));
		return userRepository.save(userUpdate);
	}

	@Override
	public void updateOrganizationEachUser() {
		List<User> users=findUserAll();
		for (User user : users) {
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.setIncludeUserId(user.getId());
			
			List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
			List<BelongOrganization> belongOrganizations=new ArrayList<>();
			for(Organization organization:organizations) {
				BelongOrganization belongOrganization = new BelongOrganization();
				belongOrganization.setOrganizationId(organization.getId());
				belongOrganization.setOrganizationName(organization.getName());
				belongOrganizations.add(belongOrganization);
			}
			user.setBelongOrganizations(belongOrganizations);
			
			userRepository.save(user);
		}
	}

	@Override
	public void updateOrganizationOfUser(String userId) {
		User user=getUserById(userId);
		
		OrganizationFilter organizationFilter=new OrganizationFilter();
		organizationFilter.setIncludeUserId(user.getId());
		
		List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
		List<BelongOrganization> belongOrganizations=new ArrayList<>();
		for(Organization organization:organizations) {
			BelongOrganization belongOrganization = new BelongOrganization();
			belongOrganization.setOrganizationId(organization.getId());
			belongOrganization.setOrganizationName(organization.getName());
			belongOrganizations.add(belongOrganization);
		}
		user.setBelongOrganizations(belongOrganizations);
		userRepository.save(user);
	}

	@Override
	public User saveUser(User user) {
		OrganizationFilter organizationFilter=new OrganizationFilter();
		organizationFilter.setIncludeUserId(user.getId());
		
		List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
		List<BelongOrganization> belongOrganizations=new ArrayList<>();
		for(Organization organization:organizations) {
			BelongOrganization belongOrganization = new BelongOrganization();
			belongOrganization.setOrganizationId(organization.getId());
			belongOrganization.setOrganizationName(organization.getName());
			belongOrganizations.add(belongOrganization);
		}
		user.setBelongOrganizations(belongOrganizations);
		return userRepository.save(user);
	}

	@Override
	public User setReviewedOfFirstReview(String userId) {
		User user=getUserById(userId);
		if(user.getFirstReview()==null) {
			throw new NotAcceptableExceptionAdvice("Tài khoản không có sử dụng first-review khi lần đầu sử dụng tự chọn Đơn vị");
		}
		user.getFirstReview().setReviewed(true);
		user.getFirstReview().setReviewedTime(new Date());

		return saveUser(user);
	}

	@Override
	public User setGuidedWebUI(String userId) {
		User user=getUserById(userId);
		if(user.isGuideWebUI()) {
			throw new NotAcceptableExceptionAdvice("Tài khoản đã được hướng dẫn sử dụng trên WebUI");
		}
		user.setGuideWebUI(true);

		return saveUser(user);
	}

	@Override
	public List<String> getAllPermission(String userId) {
		List<Organization> organizations=organizationService.getListOrganizationOfUser(userId);
		List<String> permissions=new ArrayList<>();
		for(Organization organization:organizations) {
			permissions.addAll(organization.getAllPermissionOfUser(userId));
		}
		return permissions.stream().distinct().collect(Collectors.toList());
	}

	@Override
	public Optional<User> addUserByUsernameLdap(String username){
		/* Kiểm tra tại local nếu chưa có thật sự, loại bỏ provider */
		Optional<User> findUserByUsername=findUserByUserName(username);
		if(findUserByUsername.isPresent()) {
			return findUserByUsername;
		}
		
		/* Kiểm tra nếu Ldap có bật */
		if(otpService.isEnable()) {
			/* Tìm người dùng trong Ldap */
			Optional<LdapUser> auth=otpService.getUserByAccountName(username);
			/* Nếu người dùng tồn tại trong Ldap */
			if(auth.isPresent()) {
				LdapUser ldapUser=auth.get();
				User createUser=new User();
				createUser.setUsername(ldapUser.getUsername());
				createUser.setEmail(ldapUser.getUserPrincipalName());
				createUser.setFullName(ldapUser.getFullName());
				createUser.setPassword(passwordEncoder.encode(username));
				createUser.setActiveCode(RandomStringUtils.randomAlphanumeric(8).toUpperCase());
				createUser.setActive(true);
				createUser.setProvider(UserProvider.ldap);
				createUser.setLdapInfo(ldapUser);
				
				/* Tạo người dùng */
				createUser=saveUser(createUser);
				return Optional.ofNullable(createUser);
			}
		}
		/* Ngược lại thì thêm vào local? */
//		else {
//			User createUser=new User();
//			createUser.setUsername(username);
//			createUser.setEmail(username+"@local");
//			createUser.setFullName(username);
//			createUser.setPassword(passwordEncoder.encode(username));
//			createUser.setActiveCode(RandomStringUtils.randomAlphanumeric(8).toUpperCase());
//			createUser.setActive(true);
//			createUser.setProvider(UserProvider.local);
//			createUser.setLdapInfo(null);
//			
//			/* Tạo người dùng */
//			createUser=saveUser(createUser);
//			return Optional.ofNullable(createUser);
//		}
		return Optional.empty();
	}
	
	@Override
	public Optional<User> addUserToOrganizationByUsernameLdap(String username, Organization organization){
		Assert.notNull(organization, "Không thể tự thêm username ["+username+"] vào đơn vị, vì đơn vị không tồn tại");
		Optional<User> findAndCreateIfNotExistUserByUserName=addUserByUsernameLdap(username);
		if(findAndCreateIfNotExistUserByUserName.isPresent()) {
			User user = findAndCreateIfNotExistUserByUserName.get();
			organizationService.addUsersToOrganization(organization.getId(), Arrays.asList(user.getId()), AddFromSource.partner);
			return findUserById(user.getId());
		}
		return findAndCreateIfNotExistUserByUserName;
	}

	@Override
	public List<User> createUsersImportFromLdap(@Valid ReqUserImportFromLdap reqUserImportFromLdap, User user) {
		List<User> usersCreated=new ArrayList<>();
		for (String username : reqUserImportFromLdap.getUsernames()) {
			Optional<User> userCreated=addUserByUsernameLdap(username);
			if(userCreated.isPresent()) {
				usersCreated.add(userCreated.get());
			}
		}
		return usersCreated;
	}
	
}
