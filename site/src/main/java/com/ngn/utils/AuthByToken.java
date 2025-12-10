package com.ngn.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import com.ngn.api.authentication.ApiAuthenticationModel;
import com.ngn.api.authentication.ApiAuthenticationService;
import com.ngn.api.organization.ApiOrganizationServiceCustom;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.sign_in_org.ApiSignInOrgModel;
import com.ngn.api.sign_in_org.ApiSignInOrgService;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.sign_in_org.SignInOrgModel;
import com.ngn.secutity.AuthenticatedUser;
import com.ngn.setting.ChooseOrgDialog;
import com.ngn.setting.step_setup_user.SetupUserForm;
import com.ngn.utils.components.ConfirmDialogTemplate;
import com.ngn.utils.components.NotificationTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@AnonymousAllowed
public class AuthByToken extends VerticalLayoutTemplate implements  BeforeEnterObserver,BeforeLeaveObserver{
	private static final long serialVersionUID = 1L;

	private final AuthenticatedUser authenticatedUser;
	
	@Autowired
	private ApiOrganizationServiceCustom apiOrganizationServiceCustom;


	public AuthByToken(AuthenticatedUser authenticatedUser) {
		this.authenticatedUser = authenticatedUser;
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {

		Location location = event.getLocation();
		QueryParameters queryParameters = location.getQueryParameters();
		if(queryParameters.getParameters().containsKey("token")) {
			handleFunction(queryParameters.getParameters());
		}else {
			UI.getCurrent().getPage().setLocation("login_default");
		}
	}

	@Override
	public void beforeLeave(BeforeLeaveEvent event) {

	}

	private void handleFunction(Map<String, List<String>> parameters) {
		String token = parameters.get("token").get(0);

		if(authenWithShortTermToken(token)) {
			//			doHandleSession(parameters);
			doIgnoreCheckOrg(parameters);
		}else {
			loadLayoutWhenAuthenFail();
		}
	}

	private void doIgnoreCheckOrg(Map<String, List<String>> parameters) {
		CheckParametersUtil checkParametersUtil = new CheckParametersUtil(parameters);
		if(SessionUtil.getOrgId() != null) {
			if(parameters.containsKey("org")) {
				String idOrg = parameters.get("org").get(0);
				List<BelongOrganizationModel> listWillSaveToSession = new ArrayList<BelongOrganizationModel>();
				SignInOrgModel getOrgByUnitCode = null;
				if(!ObjectId.isValid(idOrg)) {
					getOrgByUnitCode = getOrg(idOrg);
				}

				if(getOrgByUnitCode != null) {
					BelongOrganizationModel belongOrganizationModel = new BelongOrganizationModel();
					belongOrganizationModel.setOrganizationId(getOrgByUnitCode.getId());
					belongOrganizationModel.setOrganizationName(getOrgByUnitCode.getName());
					listWillSaveToSession.add(belongOrganizationModel);
				}else {
					SignInOrgModel signInOrgModel = getOrg(idOrg);
					if(signInOrgModel != null) {
						BelongOrganizationModel belongOrganizationModel = new BelongOrganizationModel();
						belongOrganizationModel.setOrganizationId(signInOrgModel.getId());
						belongOrganizationModel.setOrganizationName(signInOrgModel.getName());
						listWillSaveToSession.add(belongOrganizationModel);
					}
				}
				@SuppressWarnings("unused")
				ChooseOrgDialog chooseOrgDialog = new ChooseOrgDialog(listWillSaveToSession,false,parameters,false);
				checkParametersUtil.handleParam();
			}else {
				@SuppressWarnings("unused")
				ChooseOrgDialog chooseOrgDialog = new ChooseOrgDialog(SessionUtil.getBelongOrg(),false,parameters,false);
				checkParametersUtil.handleParam();
			}
		}else {
			if(parameters.containsKey("org")) {
				String idOrg = parameters.get("org").get(0);

				List<BelongOrganizationModel> listBelongOrganizationModels = SessionUtil.getBelongOrg();
				List<BelongOrganizationModel> listWillSaveToSession = new ArrayList<BelongOrganizationModel>();
				
				SignInOrgModel getOrgByUnitCode = null;
				if(!ObjectId.isValid(idOrg)) {
					getOrgByUnitCode = getOrg(idOrg);
				}

				if(getOrgByUnitCode != null) {
					BelongOrganizationModel belongOrganizationModel = new BelongOrganizationModel();
					belongOrganizationModel.setOrganizationId(getOrgByUnitCode.getId());
					belongOrganizationModel.setOrganizationName(getOrgByUnitCode.getName());
					listWillSaveToSession.add(belongOrganizationModel);
				}else {
					SignInOrgModel signInOrgModel = getOrg(idOrg);
					if(signInOrgModel != null) {
						BelongOrganizationModel belongOrganizationModel = new BelongOrganizationModel();
						belongOrganizationModel.setOrganizationId(signInOrgModel.getId());
						belongOrganizationModel.setOrganizationName(signInOrgModel.getName());
						listWillSaveToSession.add(belongOrganizationModel);
					}else {
						listBelongOrganizationModels.forEach(model->{
							listWillSaveToSession.add(model);
						});
					}
				}
				ChooseOrgDialog chooseOrgDialog = new ChooseOrgDialog(listWillSaveToSession,false,parameters,false);
				if(listWillSaveToSession.size() > 1) {
					chooseOrgDialog.getFooter().removeAll();
					chooseOrgDialog.open();
				}else {
					if(listWillSaveToSession.isEmpty()) {
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
						checkParametersUtil.handleParam();
					}
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private void doHandleSession(Map<String, List<String>> parameters) {
		CheckParametersUtil checkParametersUtil = new CheckParametersUtil(parameters);
		if(SessionUtil.getOrgId()!=null) {
			if(parameters.containsKey("org")) {
				String idOrg = parameters.get("org").get(0);
				List<BelongOrganizationModel> listBelongOrganizationModels = SessionUtil.getBelongOrg();
				List<BelongOrganizationModel> listWillSaveToSession = new ArrayList<BelongOrganizationModel>();
				SignInOrgModel getOrgByUnitCode = null;
				if(!ObjectId.isValid(idOrg)) {
					getOrgByUnitCode = getOrg(idOrg);
				}
				for(BelongOrganizationModel belongOrganizationModel : listBelongOrganizationModels) {
					if(belongOrganizationModel.getOrganizationId().equals(idOrg)) {
						listWillSaveToSession.add(belongOrganizationModel);
					}

					if(getOrgByUnitCode != null && belongOrganizationModel.getOrganizationId().equals(getOrgByUnitCode.getId())) {
						listWillSaveToSession.add(belongOrganizationModel);
					}

				}

				if(!listWillSaveToSession.isEmpty()) {
					ChooseOrgDialog chooseOrgDialog = new ChooseOrgDialog(listWillSaveToSession,false,parameters,false);
					checkParametersUtil.handleParam();
				}else {
					NotificationTemplate.error("Không tìm thấy đơn vị chỉ định");
					ChooseOrgDialog chooseOrgDialog = new ChooseOrgDialog(listBelongOrganizationModels, false, parameters,true);
					chooseOrgDialog.getBtnSave().setText("Xác nhận");
					chooseOrgDialog.getBtnSave().addClickListener(e->{
						checkParametersUtil.handleParam();
					});
					chooseOrgDialog.open();
				}
			}else {
				ChooseOrgDialog chooseOrgDialog = new ChooseOrgDialog(SessionUtil.getBelongOrg(),false,parameters,false);
				checkParametersUtil.handleParam();
			}
		}
		else {
			if(parameters.containsKey("org")) {
				String idOrg = parameters.get("org").get(0);
				List<BelongOrganizationModel> listBelongOrganizationModels = SessionUtil.getBelongOrg();
				List<BelongOrganizationModel> listWillSaveToSession = new ArrayList<BelongOrganizationModel>();
				SignInOrgModel getOrgByUnitCode = null;
				if(!ObjectId.isValid(idOrg)) {
					getOrgByUnitCode = getOrg(idOrg);
				}
				for(BelongOrganizationModel belongOrganizationModel : listBelongOrganizationModels) {
					if(belongOrganizationModel.getOrganizationId().equals(idOrg)) {
						listWillSaveToSession.add(belongOrganizationModel);
					}

					if(getOrgByUnitCode != null && belongOrganizationModel.getOrganizationId().equals(getOrgByUnitCode.getId())) {
						listWillSaveToSession.add(belongOrganizationModel);
					}

				}
				if(!listWillSaveToSession.isEmpty()) {
					ChooseOrgDialog chooseOrgDialog = new ChooseOrgDialog(listWillSaveToSession,false,parameters,false);
					checkParametersUtil.handleParam();
				}else {
					NotificationTemplate.error("Không tìm thấy đơn vị chỉ định");
					ChooseOrgDialog chooseOrgDialog = new ChooseOrgDialog(listBelongOrganizationModels, false, parameters,true);
					chooseOrgDialog.getBtnSave().setText("Xác nhận");
					chooseOrgDialog.getBtnSave().addClickListener(e->{
						checkParametersUtil.handleParam();
					});
					chooseOrgDialog.open();
				}
			}else {
				ChooseOrgDialog chooseOrgDialog = new ChooseOrgDialog(SessionUtil.getBelongOrg(),false,parameters,false);
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

	private void loadLayoutWhenAuthenFail() {
		// Send to Overview When is authen fail in package utils-> components -> OverviewWhenIsAuthenFailLayout.java
		UI.getCurrent().getPage().setLocation("authen_fail");
	}

	private boolean authenWithShortTermToken(String token) {

		if(SessionUtil.getUser() != null) {
			SessionUtil.cleanAllSession();
			String idUser = SessionUtil.getIdUser();
			ApiAuthenticationModel apiAuthenticationModel = checkShortTermTokenSeccond(token);
			if(apiAuthenticationModel != null && idUser.equals(apiAuthenticationModel.getId())) {
				return true;
			}
		}

		if(authenticatedUser.authenticateByCode(token)) {
			System.out.println("Xac thuc thanh cong");
			return true;
		}
		System.out.println("Khong the xac thuc");
		return false;
	}

	private SignInOrgModel getOrg(String id) {
		try {
			ApiResultResponse<ApiSignInOrgModel> getData = ApiSignInOrgService.getDetailOrg(id);
			SignInOrgModel signInOrgModel = new SignInOrgModel(getData.getResult());
			return signInOrgModel;
		} catch (Exception e) {
			return null;
		}
	}

	private ApiAuthenticationModel checkShortTermTokenSeccond(String token) {
		ApiResultResponse<ApiAuthenticationModel> apiAuthenticationModel = null;
		try {
			apiAuthenticationModel = ApiAuthenticationService.loginByCode(token);
			if(apiAuthenticationModel.isSuccess()) {
				return apiAuthenticationModel.getResult();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
