package com.ngn.setting;

import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.user.ApiChangePasswordUserModel;
import com.ngn.api.user.ApiUpdateUserService;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.NotificationTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;

public class ChangePasswordView extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;

	private UserAuthenticationModel userAuthenticationModel = SessionUtil.getUser();
	private PasswordField oldPass = new PasswordField("Mật khẩu cũ");
	private PasswordField newPass = new PasswordField("Mật khẩu mới");
	private PasswordField configNewPass = new PasswordField("Xác nhận mật khẩu mới");

	public ChangePasswordView() {
		buildLayout();
		configComponent();

	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(createLayout());
	}

	@Override
	public void configComponent() {

	}


	private Component createLayout() {
		VerticalLayout layout = new VerticalLayout();

		oldPass.setWidthFull();
		newPass.setWidthFull();
		configNewPass.setWidthFull();

		layout.add(oldPass,newPass,configNewPass);
		layout.setWidthFull();

		return layout;
	}


	public void changePass() {
		if(!invalib()) {
			return;
		}
		ApiChangePasswordUserModel apiChangePasswordUserModel = new ApiChangePasswordUserModel();
		apiChangePasswordUserModel.setPasswordOld(oldPass.getValue());
		apiChangePasswordUserModel.setPasswordNew(newPass.getValue());
		doChangePass(apiChangePasswordUserModel);
	}

	private void doChangePass(ApiChangePasswordUserModel apiChangePasswordUserModel) {
		ApiResultResponse<Object> changePass = ApiUpdateUserService.changePassword(userAuthenticationModel.getId(), apiChangePasswordUserModel);
		if(changePass.isSuccess()) {
			NotificationTemplate.success("Đổi mật khẩu thành công");
		}
	}

	private boolean invalib() {

		if(oldPass.getValue().isBlank()) {
			oldPass.setErrorMessage("Vui lòng nhập mật khẩu cũ");
			oldPass.setInvalid(true);
			oldPass.focus();
			return false;
		}

		if(newPass.getValue().isBlank()) {
			newPass.setErrorMessage("Vui lòng nhập mật khẩu mới");
			newPass.setInvalid(true);
			newPass.focus();
			return false;
		}

		if(configNewPass.getValue().isBlank()) {
			configNewPass.setErrorMessage("Vui lòng xác nhận mật khẩu mới");
			configNewPass.setInvalid(true);
			configNewPass.focus();
			return false;
		}

		if(!newPass.getValue().equals(configNewPass.getValue())) {
			configNewPass.setErrorMessage("Mật khẩu không trùng khớp");
			configNewPass.setInvalid(true);
			configNewPass.focus();
			return false;
		}




		return true;
	}

}
