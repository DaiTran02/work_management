package com.ngn.setting;

import java.util.Timer;
import java.util.TimerTask;

import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.user.ApiUpdateUserModel;
import com.ngn.api.user.ApiUpdateUserService;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.ConfirmDialogTemplate;
import com.ngn.utils.components.NotificationTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class InfomationView extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;

	private Avatar avatar = new Avatar();
	private TextField txtUserName = new TextField("Tên tài khoản");
	private TextField txtEmail = new TextField("Địa chỉ email");
	private TextField txtFullName = new TextField("Họ và Tên");
	private TextField txtPhone = new TextField("Số điện thoại");
	private Timer timer = new Timer();

	private Runnable run;
	public InfomationView(Runnable run) {
		this.run = run;
		buildLayout();
		configComponent();
		loadData();
	}
	
	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(createLayoutInfo());
		
	}

	@Override
	public void configComponent() {
		
	}
	
	public void loadData() {
		UserAuthenticationModel userAuthenticationModel = SessionUtil.getUser();
		avatar.setName(userAuthenticationModel.getFullName());
		txtUserName.setValue(userAuthenticationModel.getUsername());
		txtEmail.setValue(userAuthenticationModel.getEmail());
		txtFullName.setValue(userAuthenticationModel.getFullName());
		txtPhone.setValue(userAuthenticationModel.getPhone());
	}
	
	private Component createLayoutInfo() {
		VerticalLayout vLayout = new VerticalLayout();
		
		avatar.setWidth("100px");
		avatar.setHeight("100px");
		avatar.getStyle().setMargin("auto");
		
		txtUserName.setReadOnly(true);
		txtUserName.setWidthFull();
		txtEmail.setWidthFull();
		txtFullName.setWidthFull();
		txtPhone.setWidthFull();
		
		vLayout.setWidthFull();
		vLayout.add(avatar,txtUserName,txtEmail,txtFullName,txtPhone);
		
		return vLayout;
	}
	
	public void updateInfo() {
		if(!invalid()) {
			return;
		}
		
		ApiUpdateUserModel apiUpdateUserModel = new ApiUpdateUserModel();
		apiUpdateUserModel.setEmail(txtEmail.getValue());
		apiUpdateUserModel.setFullName(txtFullName.getValue());
		apiUpdateUserModel.setPhone(txtPhone.getValue());
		
		ConfirmDialogTemplate confirmDialogTemplate = new ConfirmDialogTemplate("Thay đổi thông tin");
		confirmDialogTemplate.setText("Xác nhận thay đổi thông tin");
		confirmDialogTemplate.getBtnConfirm().addClickListener(e->{
			doUpdate(apiUpdateUserModel);
		});
		confirmDialogTemplate.open();
	}
	
	private void doUpdate(ApiUpdateUserModel apiUpdateUserModel) {
		ApiResultResponse<Object> update = ApiUpdateUserService.updateUser(SessionUtil.getIdUser(), apiUpdateUserModel);
		if(update.isSuccess()) {
			NotificationTemplate.success("Cập nhật thành công");
			startCount();
		}
	}
	
	private boolean firstRun = true;
	
	private void startCount() {
	
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(firstRun) {
					firstRun = false;
					return;
				}else {
					stopCountDown();
					run.run();
				}
			}
		}, 0,5000);
	}
	
	private void stopCountDown() {
		if(timer != null) {
			timer.cancel();
		}
	}
	
	private boolean invalid() {
		
		
		return true;
	}

}
