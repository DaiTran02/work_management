package vn.com.ngn.page.user.form;

import java.nio.charset.Charset;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.organization.ApiOrganizationService;
import vn.com.ngn.api.user.ApiUserModel;
import vn.com.ngn.api.user.ApiUserService;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.organization.models.ListUserIDsModel;
import vn.com.ngn.page.user.model.UserModel;
import vn.com.ngn.utils.components.NotificationTemplate;

public class EditUserForm extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;
	private Avatar avatar = new Avatar();
	private TextField txtName = new TextField("Họ và Tên");
	private TextField txtUserName = new TextField("Tên đăng nhập");
	private TextField txtEmail = new TextField("Email");
	private TextField txtPhoneNumber = new TextField("Số điện thoại");
	private TextField txtActiveCode = new TextField("Mã kích hoạt");
	private Checkbox cbActive = new Checkbox("Tình trạng hoạt động");
	private PasswordField passwordField = new PasswordField("Mật khẩu*");
	
	private Button btnRefreshCode = new Button(FontAwesome.Solid.REPEAT.create());
	private String id;
	private boolean checkId = false;
	
	private UserModel userModel;
	
	private Runnable run;
	private String orgId;
	private boolean checkAddUserInOrg;
	public EditUserForm(String id,boolean checkAddUserInOrg,String orgId,Runnable run) {
		this.run = run;
		this.checkAddUserInOrg = checkAddUserInOrg;
		cbActive.setValue(true);
		if(orgId != null) {
			this.orgId = orgId;
		}
		if(id!=null) {
			this.id = id;
			checkId = true;
			loadData();
		}else {
			randomActive();
		}
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		this.setSizeFull();
		
		txtName.focus();
		
		this.add(createLayout());
		
	}

	@Override
	public void configComponent() {
		btnRefreshCode.addClickListener(e->{
			randomActive();
		});
		
		txtName.addValueChangeListener(e->{
			avatar.setName(e.getValue());
		});
	}
	
	public void loadData() {
		try {
			ApiResultResponse<ApiUserModel> data = ApiUserService.getaUser(id);
			userModel = new UserModel(data.getResult());
			if(userModel.getActiveCode()!=null) {
				txtActiveCode.setValue(userModel.getActiveCode());
			}
			txtEmail.setValue(userModel.getEmail());
			txtName.setValue(userModel.getFullName());
			if(userModel.getPhone()!=null) {
				txtPhoneNumber.setValue(userModel.getPhone().toString());
			}
			txtUserName.setValue(userModel.getUsername());
			cbActive.setValue(userModel.isActive());
			avatar.setName(userModel.getFullName());
			txtUserName.setReadOnly(true);
			txtUserName.setTooltipText("Không thể sửa tên đăng nhập");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Component createLayout() {
		VerticalLayout vLayout = new VerticalLayout();
		
		VerticalLayout layoutAvatar = new VerticalLayout();
		HorizontalLayout hLayoutAvatar = new HorizontalLayout();
		
		HorizontalLayout hLayout1 = new HorizontalLayout();
		HorizontalLayout hLayout2 = new HorizontalLayout();
		
		avatar.setWidth("100px");
		avatar.setHeight("100px");
		avatar.getStyle().setMargin("auto");
		
		txtName.setSizeFull();
		txtPhoneNumber.setSizeFull();
		
		txtEmail.setWidthFull();
		txtUserName.setWidthFull();
		txtUserName.setHelperText("Tên đăng nhập chỉ bao gồm chữ và số");
		txtActiveCode.setWidthFull();
		
		cbActive.setWidthFull();
		
		
		passwordField.setSizeFull();
		
		btnRefreshCode.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnRefreshCode.getStyle().set("margin-top", "30px").setCursor("pointer");
		btnRefreshCode.setTooltipText("Làm mới mã kích hoạt");
		
		hLayoutAvatar.add(txtName,txtPhoneNumber);
		hLayoutAvatar.setSizeFull();
		
		layoutAvatar.getStyle().set("display","flex");
		layoutAvatar.add(avatar,hLayoutAvatar);
		layoutAvatar.setSizeFull();
		layoutAvatar.setPadding(false);
		
		hLayout1.add(txtEmail,txtUserName);
		hLayout2.add(txtActiveCode,btnRefreshCode);
		
		
		hLayout1.setSizeFull();
		hLayout2.setSizeFull();
		
		if(checkId) {
			vLayout.add(layoutAvatar,hLayout1,hLayout2,cbActive);
		}else {
			vLayout.add(layoutAvatar,hLayout1,hLayout2,passwordField,cbActive);
		}
		vLayout.setSizeFull();
		
		return vLayout;
	}
	
	private void randomActive() {
		byte[] array = new byte[256];
		new Random().nextBytes(array);
		int n = 8;
		String randomString = new String(array,Charset.forName("UTF-8"));
		StringBuffer ra = new StringBuffer();
		
		for(int i = 0; i < randomString.length();i++) {
			char ch = randomString.charAt(i);
			if(((ch>='A'&&ch<='Z')||(ch>='0'&&ch<='9'))&&(n>0)) {
				ra.append(ch);
				n--;
			}
		}
		txtActiveCode.setValue(ra.toString());
	}
	
	public void doSave() {
		if(invalidForm()==false) {
			return;
		}
		UserModel userModel = new UserModel();
		userModel.setUsername(txtUserName.getValue());
		userModel.setEmail(txtEmail.getValue());
		userModel.setPhone(txtPhoneNumber.getValue());
		userModel.setFullName(txtName.getValue());
		userModel.setActive(cbActive.getValue());
		userModel.setActiveCode(txtActiveCode.getValue());
		userModel.setPassword(passwordField.getValue());
		if(checkId) {
			doUpdateUser(userModel);
		}else {
			doCreateUser(userModel);
		}
	}
	
	private void doCreateUser(UserModel userModel) {
		ApiResultResponse<ApiUserModel> createUser = null;
		try {
			 createUser = ApiUserService.createUser(userModel);
			if(createUser.getStatus()==201) {
				if(checkAddUserInOrg) {
					moveUserToOrg(orgId, createUser.getResult().getId());
				}else {
					run.run();
				}
				
			}else {
				NotificationTemplate.error(createUser.getMessage());
			}
		} catch (Exception e) {
			NotificationTemplate.error("Vui lòng điền đúng thông tin");
			e.printStackTrace();
		}
		
	}
	
	private void doUpdateUser(UserModel userModel) {
		try {
			ApiResultResponse<Object> updateUser = ApiUserService.updateUser(id, userModel);
			if(updateUser.getStatus()==200) {
				run.run();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void moveUserToOrg(String orgId,String userId) {
		try {
			ListUserIDsModel listUserIDsModel = new ListUserIDsModel();
			listUserIDsModel.getUserIds().add(userId);
			
			ApiResultResponse<Object> moveUser = ApiOrganizationService.moveUsersToOrg(orgId, listUserIDsModel);
			if(moveUser.getStatus()==200) {
				run.run();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean invalidForm() {
		if(txtName.getValue().isEmpty()) {
			txtName.setErrorMessage("Không được để trống");
			txtName.setInvalid(true);
			txtName.focus();
			return false;
		}
		if(txtUserName.getValue().isEmpty()) {
			txtUserName.setErrorMessage("Không được để trống");
			txtUserName.setInvalid(true);
			txtUserName.focus();
			return false;
		}
		
		if(txtEmail.getValue().isEmpty()) {
			txtEmail.setErrorMessage("Không được để trống");
			txtEmail.setInvalid(true);
			txtEmail.focus();
			return false;
		}
		
		String regexEmail = "^(.+)@(\\S+)$";
		
		Pattern pattern = Pattern.compile(regexEmail);
		Matcher matcher = pattern.matcher(txtEmail.getValue());
		if(matcher.matches()==false) {
			txtEmail.setErrorMessage("Email không đúng định dạng");
			txtEmail.setInvalid(true);
			txtEmail.focus();
			return false;
		}
		
		
		if(checkId==false) {
			if(passwordField.getValue().isEmpty()) {
				passwordField.setErrorMessage("Không được để trống");
				passwordField.setInvalid(true);
				passwordField.focus();
				return false;
			}
			
			if(passwordField.getValue().length()<8) {
				passwordField.setErrorMessage("Mật khẩu phải trên 8 ký tự và ít nhất 1 chữ cái viết hoa và có ký tự đặc biệt");
				passwordField.setInvalid(true);
				passwordField.focus();
				return false;
			}
			
			String stringPassword = passwordField.getValue();
			int countCharacter = 0;
			int countUppercase = 0;
			for(int i=0;i<passwordField.getValue().length();i++) {
				if(!Character.isDigit(stringPassword.charAt(i))&&!Character.isLetter(stringPassword.charAt(i))&&!Character.isWhitespace(stringPassword.charAt(i))) {
					countCharacter++;
				}
				
				if(Character.isUpperCase(stringPassword.charAt(i))) {
					countUppercase++;
				}
			}
			
			if(countCharacter==0) {
				passwordField.setErrorMessage("Mật khẩu phải có ký tự đặc biệt");
				passwordField.setInvalid(true);
				passwordField.focus();
				return false;
			}
			if(countUppercase==0) {
				passwordField.setErrorMessage("Mật khẩu phải có ít nhất 1 chữ cái viết hoa");
				passwordField.setInvalid(true);
				passwordField.focus();
				return false;
			}
			 if (!txtPhoneNumber.getValue().matches("\\d*")) {
				 txtPhoneNumber.setErrorMessage("Số điện thoại phải là số");
				 txtPhoneNumber.setInvalid(true);
				 txtPhoneNumber.focus();
				 return false;
	            }
		}
		
		return true;
	}
}
