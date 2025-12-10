package vn.com.ngn.page.user.form.details;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.user.ApiUserModel;
import vn.com.ngn.api.user.ApiUserService;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.user.form.ChangeAndResetPasswordForm;
import vn.com.ngn.page.user.form.EditUserForm;
import vn.com.ngn.page.user.model.UserModel;
import vn.com.ngn.utils.CheckPermissionUtil;
import vn.com.ngn.utils.components.ButtonTemplate;
import vn.com.ngn.utils.components.DialogTemplate;
import vn.com.ngn.utils.components.NotificationTemplate;
import vn.com.ngn.utils.components.VerticalLayoutTemplate;

public class InfoOfUserForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private VerticalLayout vLayout = new VerticalLayout();
	private UserModel userModel = new UserModel();
	private Avatar avatar = new Avatar();
	private CheckPermissionUtil checkPermissionUtil = new CheckPermissionUtil();
	
	private String idUser;
	public InfoOfUserForm(String idUser) {
		this.idUser = idUser;
		buildLayout();
		configComponent();
		loadData();
	}
	
	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(vLayout);
	}

	@Override
	public void configComponent() {
		
	}
	
	private void loadData() {
		try {
			ApiResultResponse<ApiUserModel> data = ApiUserService.getaUser(idUser);
			if(data.isSuscces()) {
				userModel = new UserModel(data.getResult());
			}
			
			
		} catch (Exception e) {
		}
		createLayout(userModel);
	}
	
	private void createLayout(UserModel userModel) {
		vLayout.removeAll();
		HorizontalLayout hLayoutAvatar = new HorizontalLayout();
		
		VerticalLayout vLayoutName = new VerticalLayout();
		Span spName = new Span(userModel.getFullName());
		spName.getStyle().set("font-weight", "600");
		
		Span spUserName = new Span(userModel.getUsername());
		spUserName.getStyle().set("font-style", "italic");
		
		vLayoutName.setSpacing(false);
		vLayoutName.setPadding(false);
		vLayoutName.add(spName,spUserName);
		
		
		HorizontalLayout hLayoutButton = new HorizontalLayout();
		
		ButtonTemplate btnUpdate = new ButtonTemplate("Cập nhật",FontAwesome.Solid.EDIT.create());
		btnUpdate.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnUpdate.setEnabled(false);
		btnUpdate.addClickListener(e->openDialogUpdateUser(idUser));
		
		ButtonTemplate btnResetPassword = new ButtonTemplate("Đổi mật khẩu",FontAwesome.Solid.TASKS_ALT.create());
		btnResetPassword.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnResetPassword.setEnabled(false);
		btnResetPassword.addClickListener(e->openDialogResetPassword(idUser));
		
		if(checkPermissionUtil.checkCreator(userModel.getCreatorName())) {
			btnUpdate.setEnabled(true);
			btnResetPassword.setEnabled(true);
		}
		
		if(userModel.getProvider().equals("local")) {
			btnResetPassword.setEnabled(true);
		}
		
		hLayoutButton.add(btnUpdate,btnResetPassword);
		hLayoutButton.getStyle().set("margin-left", "auto");
		
		avatar.setName(userModel.getFullName());
		hLayoutAvatar.setWidthFull();
		hLayoutAvatar.add(avatar,vLayoutName,hLayoutButton);
		
		VerticalLayout vLayoutProperty = new VerticalLayout();
		
		
		String styleActive = userModel.isActive() ? "#018d00" : "#8d0000";
		
		vLayoutProperty.add(createLayoutKeyValue("Email:", userModel.getEmailText(),null),new Hr(),
				createLayoutKeyValue("Số điện thoại:", userModel.getPhoneText(),null),new Hr(),
				createLayoutKeyValue("Mã kích hoạt:", userModel.getActiveCodeText(),null),new Hr(),
				createLayoutKeyValue("Ngày tạo tài khoản:", userModel.getCreateTimeText(),null),new Hr(),
				createLayoutKeyValue("Đăng nhập:", userModel.getLastDateLoginText(),null),new Hr(),
				createLayoutKeyValue("Ngày đổi mật khẩu:", userModel.getLastChangePassText(),null),new Hr(),
				 createLayoutKeyValue("Tình trạng:", userModel.getActiveText(), styleActive));
		
		
		vLayout.add(hLayoutAvatar,new Hr(),vLayoutProperty);
		vLayout.setSizeFull();
		vLayout.getStyle().set("border-radius", "10px").set("box-shadow", "rgba(99, 99, 99, 0.2) 0px 2px 8px 0px").setMargin("0  auto");
	}
	
	private void openDialogUpdateUser(String id) {
		DialogTemplate dialog = new DialogTemplate("CHỈNH SỬA TÀI KHOẢN NGƯỜI DÙNG",()->{

		});

		EditUserForm editUserForm = new EditUserForm(id,false,null, ()->{
			loadData();
			NotificationTemplate.success("Chỉnh sửa thành công");
			dialog.close();
		});


		dialog.getBtnSave().addClickListener(e->{
			editUserForm.doSave();
		});


		dialog.add(editUserForm);
		dialog.setWidth("60%");

		dialog.open();
	}
	
	private void openDialogResetPassword(String id) {
		DialogTemplate dialog = new DialogTemplate("ĐỔI MẬT KHẨU",()->{

		});

		ChangeAndResetPasswordForm chanPasswordForm = new ChangeAndResetPasswordForm(id, ()->{
			loadData();
			dialog.close();
		});


		dialog.getBtnSave().addClickListener(e->{
			chanPasswordForm.resetPassword();
		});


		dialog.add(chanPasswordForm);
		dialog.setWidth("40%");

		dialog.open();
	}
	
	private Component createLayoutKeyValue(String name,String value,String style) {
		HorizontalLayout hLayoutKeyValue = new HorizontalLayout();
		Span spanName = new Span(name);
		
		spanName.getStyle().set("font-weight", "600").setWidth("135px");
		
		Span spanValue = new Span(value);
		if(style != null) {
			spanValue.getStyle().setColor(style);
		}
		hLayoutKeyValue.add(spanName,spanValue);
		return hLayoutKeyValue;
	}

}
