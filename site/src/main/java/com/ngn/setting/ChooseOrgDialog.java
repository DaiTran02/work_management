package com.ngn.setting;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.sign_in_org.ApiSignInOrgModel;
import com.ngn.api.sign_in_org.ApiSignInOrgService;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.sign_in_org.SignInOrgModel;
import com.ngn.setting.components.CardOrgForm;
import com.ngn.setting.components.ViewOrgFromRootForm;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.DialogTemplate;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;

public class ChooseOrgDialog extends DialogTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private HorizontalLayout orderedList = new HorizontalLayout();

	private List<BelongOrganizationModel> listBelongOrganizationModels;
	// Kiem tra co phai thay doi don vi khong
	private boolean checkChangeOrg;

	// Kiem tra neu khong tim thay don vi chi dinh
	private boolean checkOrgFail = false;

	private boolean isPermissionChooseAllOrg = SessionUtil.isPermissionChooseOrg();

	private Map<String, List<String>> parametters;
	private int widthOfCards = 0;
	private Span spTitle = new Span("*Chọn một đơn vị để vào thực hiện các chức năng, mỗi đơn vị sẽ có một vai trò chức năng khác nhau.");
	public ChooseOrgDialog(List<BelongOrganizationModel> listBelongOrganizationModels,boolean checkChangeOrg,
			Map<String, List<String>> parametters,boolean checkOrgFail) {
		
		Set<BelongOrganizationModel> listOrg = new HashSet<BelongOrganizationModel>();
		listBelongOrganizationModels.forEach(model->{
			listOrg.add(model);
		});
		this.listBelongOrganizationModels = listOrg.stream().toList();
		
		this.checkChangeOrg = checkChangeOrg;
		if(parametters != null) {
			this.parametters = parametters;
		}
		this.checkOrgFail = checkOrgFail;
		buildLayout();
		loadData();
	}

	@Override
	public void buildLayout() {
		//The class in report.css
		addClassNames("image-gallery-view");
		addClassNames(Margin.Horizontal.AUTO, Padding.Bottom.LARGE, Padding.Horizontal.LARGE);
		this.setHeaderTitle("Chọn đơn vị sử dụng");

		if(checkOrgFail) {
			spTitle.setText("*Xác thực thành công nhưng không tim thấy đơn vị chỉ định, bạn có muốn vào đơn vị mặc định của tài khoản không");
		}


		VerticalLayout vLayout = new VerticalLayout();
		spTitle.getStyle().setFontWeight(600);
		vLayout.add(spTitle,orderedList);
		vLayout.setWidthFull();
		this.add(vLayout);

		this.setWidth("auto");

		//		orderedList.addClassNames(Gap.MEDIUM, Display.GRID, ListStyleType.NONE, Margin.AUTO, Padding.NONE);

	}

	private void loadData() {
		if(isPermissionChooseAllOrg) {
			for(BelongOrganizationModel belongOrganizationModel : listBelongOrganizationModels) {
				CardOrgForm cardOrgForm = new CardOrgForm(belongOrganizationModel,checkChangeOrg,parametters,false,false);
				cardOrgForm.addChangeListener(e->this.close());
				orderedList.add(cardOrgForm);
				widthOfCards += 250;
			}

			CardOrgForm cardOrgForm = new CardOrgForm(null,checkChangeOrg,parametters,false,true);
			cardOrgForm.addChangeListener(e->{
				openDialogChooseOrtherOrg(()->{
					this.close();
				});
			});
			orderedList.add(cardOrgForm);
			widthOfCards += 250;
		}else {
			if(listBelongOrganizationModels.size() == 1) {
				for(BelongOrganizationModel belongOrganizationModel : listBelongOrganizationModels) {
					System.out.println("Check data 1 ne: "+belongOrganizationModel.getOrganizationName());
					if(checkOrgFail) {
						CardOrgForm cardOrgForm = new CardOrgForm(belongOrganizationModel,checkChangeOrg,parametters,checkOrgFail,false);
						cardOrgForm.addChangeListener(e->this.close());
						orderedList.add(cardOrgForm);
						widthOfCards += 250;
					}else {
						SignInOrgModel signInOrgModel = getOrg(belongOrganizationModel.getOrganizationId());
						SessionUtil.setOrgId(belongOrganizationModel);
						SessionUtil.setDetailOrg(signInOrgModel);
					}
				}
			}else {
				for(BelongOrganizationModel belongOrganizationModel : listBelongOrganizationModels) {
					System.out.println("Check data 2 ne: "+belongOrganizationModel.getOrganizationName());
					CardOrgForm cardOrgForm = new CardOrgForm(belongOrganizationModel,checkChangeOrg,parametters,false,false);
					cardOrgForm.addChangeListener(e->this.close());
					orderedList.add(cardOrgForm);
					widthOfCards += 250;
				}
			}
		}

		spTitle.setWidth(String.valueOf(widthOfCards+"px"));
	}

	public List<BelongOrganizationModel> getListOrg(){
		return this.listBelongOrganizationModels;
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

	private void openDialogChooseOrtherOrg(Runnable run) {
		DialogTemplate dialogTemplate = new DialogTemplate("Chọn đơn vị khác");

		ViewOrgFromRootForm viewOrgFromRootForm = new ViewOrgFromRootForm();
		dialogTemplate.setWidth("90%");
		dialogTemplate.setHeightFull();
		dialogTemplate.add(viewOrgFromRootForm);

		dialogTemplate.setLayoutMobile();
		dialogTemplate.getBtnSave().setText("Xác nhận chọn đơn vị");
		dialogTemplate.getBtnSave().addClickListener(e->{
			if(viewOrgFromRootForm.save()) {
				dialogTemplate.close();
				run.run();
			}
		});

		dialogTemplate.open();
	}



}
