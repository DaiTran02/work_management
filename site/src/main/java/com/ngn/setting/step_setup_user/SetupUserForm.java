package com.ngn.setting.step_setup_user;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.organization.ApiAddToOrgModel;
import com.ngn.api.organization.ApiGroupExpandModel;
import com.ngn.api.organization.ApiOrganizationModel;
import com.ngn.api.organization.ApiOrganizationService;
import com.ngn.api.organization.ApiOrganizationServiceCustom;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.sign_in_org.ApiSignInOrgModel;
import com.ngn.api.sign_in_org.ApiSignInOrgService;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.sign_in_org.SignInOrgModel;
import com.ngn.tdnv.dashboard.DashboardView;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.DialogTemplate;
import com.ngn.utils.components.NotificationTemplate;
import com.ngn.utils.components.StepComponentSecondVersion;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class SetupUserForm extends DialogTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private final ApiOrganizationServiceCustom apiOrganizationServiceCustom;
	private String idUser = SessionUtil.getIdUser();

	private int step = 1;
	private StepComponentSecondVersion stepComponentSecondVersion = new StepComponentSecondVersion();
	private FirstStepChooseOrgForm firstStepChooseOrgForm = new FirstStepChooseOrgForm();

	private ApiOrganizationModel  dataOrgIsChoose = null;
	private ApiGroupExpandModel dataGroupIsChoose = null;
	private String idRole = null;
	

	private VerticalLayout vLayout = new VerticalLayout();
	private HorizontalLayout hLayout = new HorizontalLayout();

	private ButtonTemplate btnNext = new ButtonTemplate("Tiếp tục",FontAwesome.Solid.RIGHT_LONG.create());
	private ButtonTemplate btnPrevious = new ButtonTemplate("Quay lại",FontAwesome.Solid.LEFT_LONG.create());
	private ButtonTemplate btnSuccess = new ButtonTemplate("Hoàn thành",FontAwesome.Solid.CHECK.create());

	private boolean firstStepActive = true;
	private boolean firstStepDone = false;
	private boolean secondStepActive = false;
	private boolean secondStepDone = false;
	private boolean thirdStepActive = false;
	private boolean thirdStepDone = false;

	public SetupUserForm(ApiOrganizationServiceCustom apiOrganizationServiceCustom) {
		this.apiOrganizationServiceCustom = apiOrganizationServiceCustom;
		buildLayout();
		event();

	}

	@Override
	public void buildLayout() {
		this.setWidth("90%");
		this.setHeightFull();

		this.setHeaderTitle("ĐĂNG NHẬP LẦN ĐẦU (Cài đặt thông tin đơn vị, lý do tài khoản này chưa có đơn vị sử dụng)");
		this.getFooter().removeAll();

		if(step == 1) {
			btnPrevious.setVisible(false);
		}

		if(dataOrgIsChoose == null) {
			btnNext.setEnabled(false);
		}

		btnNext.getStyle().setMarginLeft("auto").setBorder("1px solid");
		
		btnSuccess.getStyle().setMarginLeft("auto").setBorder("1px solid");
		btnSuccess.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
		
		btnPrevious.getStyle().setBorder("1px solid");

		this.getFooter().add(btnPrevious,btnNext);

		hLayout.setSizeFull();

		vLayout.setSizeFull();
		vLayout.add(stepComponentSecondVersion,hLayout);
		
		firstStepChooseOrgForm.setData(apiOrganizationServiceCustom);


		setUpStep();
		setUpContent();
		
		this.setCloseOnOutsideClick(false);

		this.add(vLayout);

	}
	
	public void configComponent() {
		
	}

	public void event() {
		if(btnNext != null)
			btnNext.addClickListener(e->{
				step++;
				
				setUpContent();
				setUpStep();
				
				if(step == 3) {
					this.getFooter().remove(btnNext);
					this.getFooter().add(btnSuccess);
				}
				hLayout.addClassName("layout-fade-in");
				// Chờ hiệu ứng hoàn tất, sau đó thay đổi bước và nội dung
	            UI.getCurrent().getPage().executeJs("setTimeout(() => { $0.classList.remove('layout-fade-in'); }, 500);", hLayout.getElement());
			});
		
		if(btnPrevious != null)
			btnPrevious.addClickListener(e->{
				step--;
				if(step == 1) {
					step = 1;
					btnPrevious.setVisible(false);
				}
				if(step != 3) {
					this.getFooter().remove(btnSuccess);
					this.getFooter().add(btnNext);
				}
				setUpContent();
				setUpStep();
				
				hLayout.addClassName("layout-fade-out");
				UI.getCurrent().getPage().executeJs("setTimeout(() => { $0.classList.remove('layout-fade-out'); }, 500);", hLayout.getElement());
			});
		
		if(btnSuccess != null)
			btnSuccess.addClickListener(e->{
				addToOrg();
			});
	}

	private void setUpStep() {
		stepComponentSecondVersion.refreshStep();
		
		if(step == 1) {
			firstStepActive = true;
			firstStepDone = false;
			secondStepActive = false;
			secondStepDone = false;
			thirdStepActive = false;
			thirdStepDone = false;
		}
		
		if(step == 2) {
			firstStepActive = false;
			firstStepDone = true;
			secondStepActive = true;
			secondStepDone = false;
			thirdStepActive = false;
			thirdStepDone = false;
		}
		
		if(step == 3) {
			firstStepActive = false;
			firstStepDone = true;
			secondStepActive = false;
			secondStepDone = true;
			thirdStepActive = true;
			thirdStepDone = false;
		}
		
		
		stepComponentSecondVersion.addStep("Bước 1: Chọn đơn vị sử dụng", firstStepActive, firstStepDone,false);
		stepComponentSecondVersion.addStep("Bước 2: Chọn nhóm sử dụng", secondStepActive, secondStepDone,false);
		stepComponentSecondVersion.addStep("Bước 3: Chọn vai trò trong đơn vị", thirdStepActive, thirdStepDone,true);
	}

	private void setUpContent() {
//			// Bắt đầu hiệu ứng fade-out
//        hLayout.addClassName("layout-fade-in");
        hLayout.removeClassName("layout-fade-in");
        hLayout.removeClassName("layout-fade-out");
        
		hLayout.removeAll();

		if(step == 1) {
			firstStepChooseOrgForm.addChangeListener(e->{
				dataOrgIsChoose = firstStepChooseOrgForm.getOrgIsChoose();
				if(dataOrgIsChoose != null) {
					btnNext.setEnabled(true);
				}else {
					btnNext.setEnabled(false);
				}
			});
			
			dataGroupIsChoose = null;
			
			firstStepChooseOrgForm.setOrg(dataOrgIsChoose);

			hLayout.add(firstStepChooseOrgForm);
		}



		if(step == 2) {
			SecondStepChooseGroupForm secondStepChooseGroupForm = new SecondStepChooseGroupForm(apiOrganizationServiceCustom,dataOrgIsChoose);
			
			secondStepChooseGroupForm.setOrgGroup(dataGroupIsChoose);
			
			secondStepChooseGroupForm.addChangeListener(e->{
				dataGroupIsChoose = secondStepChooseGroupForm.getApiGroupChoose();
			});
			
			hLayout.add(secondStepChooseGroupForm);
			btnPrevious.setVisible(true);
		}
		
		if(step == 3) {
			ThirdStepChooseRoleForm thirdStepChooseRoleForm = new ThirdStepChooseRoleForm(dataOrgIsChoose.getId(),true,apiOrganizationServiceCustom);
			
			thirdStepChooseRoleForm.addChangeListener(e->{
				idRole = thirdStepChooseRoleForm.getIdRole();
//				if(idRole != null && !idRole.isEmpty()) {
//					btnSuccess.setEnabled(true);
//				}else {
//					btnSuccess.setEnabled(false);
//				}
			});
			
			hLayout.add(thirdStepChooseRoleForm);
		}

		
	}
	
	private void addToOrg() {
		ApiAddToOrgModel apiAddToOrgModel = new ApiAddToOrgModel();
		apiAddToOrgModel.setOrganizationId(dataOrgIsChoose.getId());
		apiAddToOrgModel.setRoleId(idRole);
		if(dataGroupIsChoose != null) {
			apiAddToOrgModel.setGroupId(dataGroupIsChoose.getGroupId());
		}
		ApiResultResponse<Object> data = apiOrganizationServiceCustom.addToOrg(idUser, apiAddToOrgModel);
		if(data.isSuccess()) {
			welcomeTo(dataOrgIsChoose.getId());
			NotificationTemplate.success("Chọn đơn vị hoạt động thành công");
		}
	}
	
	private void welcomeTo(String idOrg) {
		ApiResultResponse<ApiOrganizationModel> dataOrg = ApiOrganizationService.getOneOrg(idOrg);
		BelongOrganizationModel belongOrganizationModel = new BelongOrganizationModel();
		
		belongOrganizationModel.setOrganizationId(dataOrg.getResult().getId());
		belongOrganizationModel.setOrganizationName(dataOrg.getResult().getName());
		SessionUtil.setOrgId(belongOrganizationModel);
		
		SignInOrgModel signInOrgModel = getOrg(idOrg);
		SessionUtil.setDetailOrg(signInOrgModel);
		
		UI.getCurrent().navigate(DashboardView.class);
		this.close();
	}
	
	private SignInOrgModel getOrg(String id) {
		try {
			ApiResultResponse<ApiSignInOrgModel> getData = ApiSignInOrgService.getDetailOrg(id);
			SignInOrgModel signInOrgModel = new SignInOrgModel(getData.getResult());
			return signInOrgModel;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
