package com.ngn.views;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.lineawesome.LineAwesomeIcon;

import com.ngn.api.organization.ApiOrganizationServiceCustom;
import com.ngn.captcha.CaptchaGenerator;
import com.ngn.captcha.CaptchaUtils;
import com.ngn.interfaces.FormInterface;
import com.ngn.secutity.AuthenticatedUser;
import com.ngn.setting.ChooseOrgDialog;
import com.ngn.setting.step_setup_user.SetupUserForm;
import com.ngn.tdnv.dashboard.DashboardView;
import com.ngn.utils.PropsUtil;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.ConfirmDialogTemplate;
import com.ngn.utils.components.NotificationTemplate;
import com.ngn.utils.otp.AuthenByOTPForm;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginI18n.ErrorMessage;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import cn.apiclub.captcha.Captcha;



// If you want to find login by code you can find AuthenByToken in package Utils
@Route("login_default")
@PageTitle("Đăng nhập")
public class LoginView extends VerticalLayout implements FormInterface,BeforeEnterObserver,BeforeLeaveObserver{
	private static final long serialVersionUID = 1L;

	private boolean isAllowSelectOrgWhenIsLoginFirst = PropsUtil.isAllowChooseOrgWhenIsFirstLogin();

	@Autowired
	private ApiOrganizationServiceCustom apiOrganizationServiceCustom;

	private final AuthenticatedUser authenticatedUser;
	private boolean isOtpAuthen = PropsUtil.isAllowAuthenByOTP();

	private LoginOverlay formLogin = new LoginOverlay();
	private VerticalLayout vTitle = new VerticalLayout();
	private Image imgLogo;
	private Span lblMainTitle = new Span();

	private FlexLayout flexGaptcha = new FlexLayout();
	private TextField txtCaptcha = new TextField("Nhập mã bên trên");
	private Captcha captcha;
	private CaptchaGenerator captchaGenerator ;
	private Button btnRefreshCaptcha = new Button(LineAwesomeIcon.SYNC_SOLID.create());
	private Image imageCaptcha = new Image();

	private LoginI18n i18n = LoginI18n.createDefault();

	public LoginView(AuthenticatedUser authenticatedUser) {
		this.authenticatedUser = authenticatedUser;

	}

	@Override
	public void buildLayout() {
		languageSetting();

		imgLogo = new Image(PropsUtil.getUrlLogoInLogin(),"Thành phố Hà Nội logo.");
		imgLogo.setWidth("80px");
		vTitle.removeAll();
		vTitle.getStyle().setMarginLeft("5px");

		this.add(formLogin);
		this.add(flexGaptcha);
		this.add(txtCaptcha);

		buidLayoutCaptcha();

		lblMainTitle.getElement().setProperty("innerHTML", "<span style='text-align:center;color:var(--lumo-primary-color);font-size:15px;font-weight: bold;padding-top: 10px; display: block;'>"
				+ PropsUtil.getMainTitleForLogin()
				+ "</span>");

		vTitle.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
		vTitle.add(imgLogo,lblMainTitle);
		formLogin.setTitle(vTitle);
		formLogin.setOpened(true); 
		formLogin.setI18n(i18n);
		formLogin.setId("my-form-login");

		this.setSizeFull();
		this.setId("login-view");

		loadCaptcha();

	}

	@Override
	public void configComponent() {
		formLogin.addLoginListener(e->{
			String userName = e.getUsername();
			String password = e.getPassword();

			authenticate(userName,password);
		});
		formLogin.addForgotPasswordListener(e->{
			NotificationTemplate.warning("Vui lòng liên hệ quản trị viên để cấp lại mật khẩu");
		});
		txtCaptcha.addKeyDownListener(Key.ENTER, e->{
			UI.getCurrent().getPage().executeJs("document.getElementById(\"vaadinLoginForm\").submit();");
		});
		btnRefreshCaptcha.addClickListener(e->{
			loadCaptcha();
		});
	}

	private void buidLayoutCaptcha() {
		flexGaptcha.add(imageCaptcha,btnRefreshCaptcha);

		txtCaptcha.setRequiredIndicatorVisible(true);
		txtCaptcha.setId("txtCaptcha");

		imageCaptcha.setWidth("80%");
		btnRefreshCaptcha.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnRefreshCaptcha.setWidth("20%");

		flexGaptcha.setFlexWrap(FlexWrap.WRAP);
		flexGaptcha.setId("imgCaptcha");

		UI.getCurrent().getPage().executeJs("const combo = document.getElementById(\"imgCaptcha\");\nconst password = document.getElementById(\"vaadinLoginPassword\");\npassword.after(combo);");
		UI.getCurrent().getPage().executeJs("const combo = document.getElementById(\"txtCaptcha\");\nconst password = document.getElementById(\"imgCaptcha\");\npassword.after(combo);");


	}

	private void authenticate(String username, String password) {
		try {
			if(authenticatedUser.isCaptchaRequired()) {
				if(txtCaptcha.isEmpty()) {
					txtCaptcha.setErrorMessage("Vui lòng nhập mã trên");
					txtCaptcha.setInvalid(true);

					formLogin.setEnabled(true);
					return;
				} else if(checkCaptcha()==false) {
					txtCaptcha.setValue("");
					txtCaptcha.setErrorMessage("Mã nhập trên sai");
					txtCaptcha.setInvalid(true);

					formLogin.setEnabled(true);
					return;
				}
				txtCaptcha.setInvalid(false);
			}

			boolean authentication=authenticatedUser.authenticate(username, password);
			// Check org of user
			if(authentication) {
				if(isOtpAuthen) {
					AuthenByOTPForm authenByOTPForm = new AuthenByOTPForm();
					authenByOTPForm.addCancelListener(e->{
						openConfirmCancel();
						authenByOTPForm.open();
					});
					authenByOTPForm.addChangeListener(e->{
						doNavigate();
					});
					authenByOTPForm.open();
				}else {
					doNavigate();
				}
			}else {
				System.out.println("authen fail");
				formLogin.setError(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			formLogin.setError(true);
		}
	}

	private void doNavigate() {

		if( SessionUtil.getBelongOrg() != null && SessionUtil.getBelongOrg().isEmpty()) {
			if(isAllowSelectOrgWhenIsLoginFirst) {
				SetupUserForm setupUserForm = new SetupUserForm(apiOrganizationServiceCustom);
				setupUserForm.getBtnClose().addClickListener(e->{
					ConfirmDialogTemplate confirmDialogTemplate = new ConfirmDialogTemplate("Không chọn đơn vị sử dụng");
					confirmDialogTemplate.setText("Nếu không chọn đơn vị sử dụng thì không thể sử dụng tài khoản này vui lòng đăng nhập bằng tài khoản khác hoặc lên hệ quản trị viên");
					confirmDialogTemplate.getBtnConfirm().addClickListener(ev->{
						authenticatedUser.logout();
						setupUserForm.close();
					});

					confirmDialogTemplate.getBtnConfirm().addThemeVariants(ButtonVariant.LUMO_ERROR);

					confirmDialogTemplate.open();
				});

				setupUserForm.open();
			}else {
				ConfirmDialogTemplate confirmDialogTemplate = new ConfirmDialogTemplate("Tài khoản chưa có đơn vị trong hệ thống");
				confirmDialogTemplate.setText("Tài khoản này hiện chưa thuộc đơn vị nào trong hệ thống, "
						+ "vui lòng liên hệ quản trị viên để cập nhật tài khoản đúng đơn vị.");
				confirmDialogTemplate.open();
				confirmDialogTemplate.addConfirmListener(e->{
					authenticatedUser.logout();
				});
				confirmDialogTemplate.setCancelable(false);
			}
		}else {
			if(SessionUtil.getOrgId()!=null) {
				// This class in setting package
				
				if(SessionUtil.isPermissionChooseOrg() == true) {
					ChooseOrgDialog chooseOrgDialog = new ChooseOrgDialog(SessionUtil.getBelongOrg(),false,null,false);
					chooseOrgDialog.getFooter().removeAll();
					chooseOrgDialog.open();
					chooseOrgDialog.getBtnClose().addClickListener(e->{
						authenticatedUser.logout();
						chooseOrgDialog.close();
					});
					chooseOrgDialog.setCloseOnOutsideClick(false);
				}else {
					@SuppressWarnings("unused")
					ChooseOrgDialog chooseOrgDialog = new ChooseOrgDialog(SessionUtil.getBelongOrg(),false,null,false);
					UI.getCurrent().navigate(DashboardView.class);
				}
			}else {
				// This class in setting package
				ChooseOrgDialog chooseOrgDialog = new ChooseOrgDialog(SessionUtil.getBelongOrg(),false,null,false);
				chooseOrgDialog.getFooter().removeAll();
				chooseOrgDialog.open();
				chooseOrgDialog.getBtnClose().addClickListener(e->{
					authenticatedUser.logout();
					chooseOrgDialog.close();
				});
				chooseOrgDialog.setCloseOnOutsideClick(false);

			}
		}
	}

	private void loadCaptcha() {
		captchaGenerator = new CaptchaGenerator();
		captcha = captchaGenerator.createCaptcha(300,50);
		imageCaptcha.setSrc(String.format("data:image/png;base64,%s", CaptchaUtils.encodeBase64(captcha)));
	}

	private boolean checkCaptcha() {
		if(captcha.getAnswer().equals(txtCaptcha.getValue())) {
			return true;
		}
		loadCaptcha();
		return false;
	}

	private void openConfirmCancel() {
		ConfirmDialogTemplate confirmDialogTemplate = new ConfirmDialogTemplate("Không xác thực OTP");
		confirmDialogTemplate.setText("Tài khoản này không thể sử dụng khi không xác thực OTP");
		confirmDialogTemplate.addConfirmListener(e->{
			authenticatedUser.logout();
		});
		confirmDialogTemplate.open();
	}

	public void languageSetting() {
		i18n.getForm().setTitle("Đăng nhập");
		i18n.getForm().setUsername("Tài khoản");
		i18n.getForm().setPassword("Mật khẩu");
		i18n.getForm().setSubmit("ĐĂNG NHẬP");
		i18n.getForm().setForgotPassword("Quên mật khẩu");
		i18n.setAdditionalInformation("Engineered by NGN");
		ErrorMessage errorMessage=new ErrorMessage();
		errorMessage.setTitle("Không thể xác thực");
		errorMessage.setMessage("Vui lòng kiểm tra lại thông tin tài khoản hoặc mật khẩu và thử lại");
		i18n.setErrorMessage(errorMessage);
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		if(authenticatedUser.get().isPresent()) {
			if(SessionUtil.getOrgId() == null) {
				ChooseOrgDialog chooseOrgDialog = new ChooseOrgDialog(SessionUtil.getBelongOrg(),false,null,false);
				chooseOrgDialog.getFooter().removeAll();
				chooseOrgDialog.open();
				chooseOrgDialog.getBtnClose().addClickListener(e->{
					authenticatedUser.logout();
					chooseOrgDialog.close();
				});
				chooseOrgDialog.setCloseOnOutsideClick(false);
			}else {
				UI.getCurrent().navigate("");
			}
		} else {
			this.removeAll();
			buildLayout(); 
			configComponent();
		}
	}


	@Override
	public void beforeLeave(BeforeLeaveEvent event) {

	}

}
