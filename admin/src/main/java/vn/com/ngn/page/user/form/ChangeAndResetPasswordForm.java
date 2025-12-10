package vn.com.ngn.page.user.form;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.user.ApiPasswordModel;
import vn.com.ngn.api.user.ApiUserService;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.user.model.PasswordModel;
import vn.com.ngn.utils.components.NotificationTemplate;

public class ChangeAndResetPasswordForm extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;

	private PasswordField txtNewPassword = new PasswordField("Mật khẩu mới");
	private PasswordField txtConfirmNewPassword = new PasswordField("Xác nhận mật khẩu mới");
	
	private String id;
	private Runnable onRun;
	public ChangeAndResetPasswordForm(String id,Runnable onRun) {
		this.onRun = onRun;
		this.id = id;
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		this.setSizeFull();
		Span label = new Span("*Mật khẩu phải có ít nhất 1 ký tự viết hoa và 1 ký tự đặc biệt, tối thiểu là 6 ký tự.");
		label.getStyle().set("font-weight", "600");
		this.add(label,createLayout());
	}

	@Override
	public void configComponent() {
		
	}
	
	private Component createLayout() {
		VerticalLayout vLayout = new VerticalLayout();
		
		txtNewPassword.setSizeFull();
		txtConfirmNewPassword.setSizeFull();
		
		vLayout.add(txtNewPassword,txtConfirmNewPassword);
		
		vLayout.setSizeFull();
		
		return vLayout;
	}
	
	public void resetPassword() {
		if(isValid() == false) {
			return;
		}
		PasswordModel passwordModel = new PasswordModel();
		passwordModel.setPasswordNew(txtConfirmNewPassword.getValue());
		
		doReset(passwordModel);
	}
	
	private void doReset(PasswordModel passwordModel) {
		ApiResultResponse<Object> reset = null;
		try {
			ApiPasswordModel apiPasswordModel = new ApiPasswordModel(passwordModel);
			reset = ApiUserService.resetPassword(id, apiPasswordModel);
			if(reset.getStatus() == 200) {
				onRun.run();
				NotificationTemplate.success(reset.getMessage());
			}else {
				NotificationTemplate.error(reset.getMessage());
			}
		} catch (Exception e) {
			NotificationTemplate.error("Vui lòng kiểm tra lại");
			e.printStackTrace();
		}
	}
	
	private boolean isValid() {
		if(!txtNewPassword.getValue().equals(txtConfirmNewPassword.getValue())) {
			txtConfirmNewPassword.setErrorMessage("Mật khẩu không trùng khớp với nhau");
			txtConfirmNewPassword.setInvalid(true);
			txtConfirmNewPassword.focus();
			return false;
		}
		
		
		return true;
	}

}
