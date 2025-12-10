package vn.com.ngn.views;

import org.vaadin.lineawesome.LineAwesomeIcon;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import cn.apiclub.captcha.Captcha;
import vn.com.ngn.api.auth.ApiAuthService;
import vn.com.ngn.api.auth.ApiSignInOrgModel;
import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.capcha.CapchaUtils;
import vn.com.ngn.capcha.CaptchaGenerator;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.home.HomeView;
import vn.com.ngn.page.setting.forms.ChangeOrgDialog;
import vn.com.ngn.page.setting.forms.ShowErrorPermissionDialog;
import vn.com.ngn.page.user.UserView;
import vn.com.ngn.securitys.AuthenticatedUser;
import vn.com.ngn.utils.CheckPermissionUtil;
import vn.com.ngn.utils.PropUtils;
import vn.com.ngn.utils.SessionUtil;

@Route(value = "/login")
@PageTitle(value = "Đăng nhập")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver,FormInterface{
	private static final long serialVersionUID = 1L;
	private final AuthenticatedUser authenticatedUser;

	private CheckPermissionUtil checkPermissionUtil = new CheckPermissionUtil();
	private final LoginOverlay loginForm = new LoginOverlay();
	private Captcha captcha;
	private CaptchaGenerator captchaGenerator;
	private Button btnRefreshCaptcha = new Button(LineAwesomeIcon.SYNC_SOLID.create());
	private TextField txtCaptcha = new TextField("Nhập mã bên trên");
	private FlexLayout flexGaptcha = new FlexLayout();
	private Image imageCaptcha = new Image();
	LoginI18n loginI18n = LoginI18n.createDefault();

	public LoginView(AuthenticatedUser authenticatedUser) {
		this.authenticatedUser = authenticatedUser;
		//		loginForm.setAction("login");
		buildLayout();
		configComponent();
		loginForm.setOpened(true);
	}

	@Override
	public void buildLayout() {
		// This class in file login_view.css
		loginForm.addClassName("layout--login");
		this.setSizeFull();
		this.add(loginForm);
		this.add(flexGaptcha);
		this.add(txtCaptcha);
		changeLanguage();
		builLayoutCaptcha();
		builCaptcha();
	}

	@Override
	public void configComponent() {

		loginForm.addLoginListener(e->{
			String username = e.getUsername();
			String password = e.getPassword();

			authenticate(username, password);
		});

		txtCaptcha.addKeyDownListener(Key.ENTER, e->{
			UI.getCurrent().getPage().executeJs("document.getElementById(\"vaadinLoginForm\").submit();");
		});

		btnRefreshCaptcha.addClickListener(e->{
			builCaptcha();
		});
	}


	private void builLayoutCaptcha() {
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

	private void builCaptcha() {

		captchaGenerator = new CaptchaGenerator();
		captcha = captchaGenerator.createCaptcha(300,50);
		imageCaptcha.setSrc(String.format("data:image/png;base64,%s", CapchaUtils.encodeBase64(captcha)));
	}

	private void authenticate(String username,String password) {
		try {
			if(authenticatedUser.isCaptchaRequired()) {
				if(txtCaptcha.isEmpty()) {
					txtCaptcha.setErrorMessage("Vui lòng nhập mã capcha");
					txtCaptcha.setInvalid(true);
					loginForm.setEnabled(true);
					return;
				}else if(checkCaptcha()==false) {
					txtCaptcha.setValue("");
					txtCaptcha.setErrorMessage("Mã nhập sai");
					txtCaptcha.setInvalid(true);
					loginForm.setEnabled(true);
					return;
				}
				txtCaptcha.setInvalid(false);
			}

			boolean authentication = authenticatedUser.authenticate(username, password);
			if(authentication) {
				if(SessionUtil.getOrgId() != null) {
					if(checkPermissionUtil.checkAdmin()) {
						UI.getCurrent().navigate(HomeView.class);
					}else {
						SessionUtil.setSignInOrg(getSignInOrgModel(SessionUtil.getOrgId()));
						if(checkPermissionUtil.checkPermissionManagerOrg()) {
							UI.getCurrent().navigate(UserView.class);
						}else {
							ShowErrorPermissionDialog showErrorPermissionDialog = new ShowErrorPermissionDialog();
							showErrorPermissionDialog.open();
							showErrorPermissionDialog.getBtnClose().addClickListener(ev->{
								authenticatedUser.logout();
							});
						}
						
					}
				}else {
					ChangeOrgDialog changeOrgDialog = new ChangeOrgDialog(SessionUtil.getListOrg(),false);
					changeOrgDialog.getFooter().removeAll();
					changeOrgDialog.open();
					changeOrgDialog.getBtnClose().addClickListener(e->{
						authenticatedUser.logout();
						changeOrgDialog.close();
					});
					changeOrgDialog.setCloseOnOutsideClick(false);
				}
			}else {
				loginForm.setError(true);
				System.out.println("Fail");
			}
		} catch (Exception e) {
			e.printStackTrace();
			loginForm.setError(true);
		}
	}

	private boolean checkCaptcha() {
		if(captcha.getAnswer().equals(txtCaptcha.getValue())) {
			return true;
		}
		builCaptcha();
		return false;
	}

	private void changeLanguage() {
		Image logoLogin = new Image(PropUtils.getUrlLogoForLogin(),"LOGO");
		logoLogin.setWidth("50px");
		logoLogin.setHeight("50px");
		logoLogin.getStyle().set("margin-top", "20px");

		H1 drcLogo = new H1("Hà Nội");
		drcLogo.getStyle().set("margin-top", "10px").setColor("yellow");

		Span spanHeader = new Span(PropUtils.getMainTitlteForLogin());


		VerticalLayout layoutHeader = new VerticalLayout();
		layoutHeader.add(drcLogo,spanHeader);
		layoutHeader.setSpacing(false);

		HorizontalLayout layoutLogo = new HorizontalLayout();
		layoutLogo.add(logoLogin,layoutHeader);
		layoutLogo.getStyle().setMargin("auto").set("padding-left", "10px");

		loginForm.setTitle(layoutLogo);
		//		loginForm.setTitle("Hà Nội");
		loginForm.setDescription("");


		LoginI18n.Form i18nForm = loginI18n.getForm();
		i18nForm.setTitle("Đăng nhập");
		i18nForm.setUsername("Tên người dùng");
		i18nForm.setPassword("Mật khẩu");
		i18nForm.setSubmit("Đăng nhập");
		i18nForm.setForgotPassword("Quên mật khẩu");
		loginI18n.setForm(i18nForm);

		LoginI18n.ErrorMessage i18nErrorMessage = loginI18n.getErrorMessage();
		i18nErrorMessage.setTitle("Tài khoản hoặc mật khẩu không chính xác");
		i18nErrorMessage.setMessage("Vui lòng kiểm tra lại tên người dùng và mật khẩu");
		i18nErrorMessage.setUsername("Tên người dùng không được để trống");
		i18nErrorMessage.setPassword("Mật khẩu không được để trống");
		loginI18n.setErrorMessage(i18nErrorMessage);
		
		loginI18n.setAdditionalInformation("Engineered by NGN");

		loginForm.setI18n(loginI18n);
	}
	
	private ApiSignInOrgModel getSignInOrgModel(String idOrg) {
		try {
			ApiResultResponse<ApiSignInOrgModel> signOrg = ApiAuthService.signInOrg(idOrg);
			if(signOrg.isSuscces()) {
				return signOrg.getResult();
			}
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		if(event.getLocation()
				.getQueryParameters()
				.getParameters()
				.containsKey("error")) {
			loginForm.setError(true);
		}

	}
}
