package com.ngn.tdnv.task.forms.details;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.flowingcode.vaadin.addons.fontawesome.FontAwesome.Solid.Icon;
import com.ngn.api.doc.ApiDocModel;
import com.ngn.api.doc.ApiDocService;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tasks.ApiInputTaskModel;
import com.ngn.api.tasks.ApiOrgGeneralOfTaskModel;
import com.ngn.api.tasks.ApiTaskService;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.models.sign_in_org.SignInOrgModel;
import com.ngn.tdnv.doc.enumdoc.DocCategoryEnum;
import com.ngn.tdnv.doc.forms.DocListForm;
import com.ngn.tdnv.doc.forms.DocOverviewForm;
import com.ngn.tdnv.doc.models.DocModel;
import com.ngn.tdnv.task.models.TaskOutputModel;
import com.ngn.utils.PropsUtil;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.DialogTemplate;
import com.ngn.utils.components.NotificationTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class TaskViewDetailOfDocForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private boolean isMobileLayout = false;
	private VerticalLayout vLayout = new VerticalLayout();


	private DocModel docIsChoose;

	private TaskOutputModel outputTaskModel;
	private boolean isOwner;
	private DocModel docModel;
	public TaskViewDetailOfDocForm(TaskOutputModel outputTaskModel,boolean isOwner,boolean isAssignee,boolean isSupport,boolean isFollow,DocModel docModel) {
		this.outputTaskModel = outputTaskModel;
		this.isOwner = isOwner;
		this.docModel = docModel;
		checkIsLayoutMobile();
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.add(vLayout);
		this.getStyle().setBoxShadow("rgba(0, 0, 0, 0.16) 0px 1px 4px").setPadding("10px").setBorderRadius("10px");
		createLayout();
	}

	@Override
	public void configComponent() {

	}
	
	private void checkIsLayoutMobile() {
		UI.getCurrent().getPage().retrieveExtendedClientDetails(e->{
			if(e.getScreenWidth() < 768) {
				isMobileLayout = true;
			}
		});
	}

	private void createLayout() {
		vLayout.removeAll();
		
		HorizontalLayout hLayoutHeader = new HorizontalLayout();
		hLayoutHeader.setWidthFull();
		hLayoutHeader.setAlignItems(Alignment.CENTER);
		hLayoutHeader.getStyle().setBorderBottom("1px solid #eff2f4").setPaddingBottom("15px");
		
		Icon icon = FontAwesome.Solid.FILE_WORD.create();
		icon.setSize("14px");
		
		H4 header = new H4("THÔNG TIN VĂN BẢN");
		hLayoutHeader.add(icon,header);
		
		vLayout.add(hLayoutHeader);

		if(outputTaskModel.getDocId() != null) {
			String width = "150px";
			DocModel docModel = getDetailDoc(outputTaskModel.getDocId());
			vLayout.add(createLayoutKeyAndValue("Trích yếu: ", docModel.getSummary(), width, null),
					createLayoutKeyAndValue("Ký hiệu: ", docModel.getSymbol(), width, width),
					createLayoutKeyAndValue("Số hiệu: ", docModel.getNumber(), width, width),
					createLayoutKeyAndValue("Người ký: ", docModel.getSignerName(), width, width),
					createLayoutKeyAndValue("Chức vụ: ", docModel.getSignerPosition(), width, width),
					createLayoutKeyAndValue("Ngày ký: ", docModel.getRegTimeText(), width, width),
					createLayoutKeyAndValue("Loại văn bản: ", checkDocCategory(docModel.getCategory().getKey()), width, width));

			HorizontalLayout hlayoutButton = new HorizontalLayout();
			hlayoutButton.setWidthFull();
			ButtonTemplate btnViewDoc = new ButtonTemplate("Xem thông tin văn bản",FontAwesome.Solid.EYE.create());
			btnViewDoc.addClickListener(e->{
				openDialogOverViewDoc(outputTaskModel.getDocId(),isOwner);
			});
			btnViewDoc.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			hlayoutButton.add(btnViewDoc);
//			if(isOwner) {
//				if(PropsUtil.isChangeDoc()) {
//					ButtonTemplate btnEdit = new ButtonTemplate("Thay đổi văn bản nguồn",FontAwesome.Solid.EDIT.create());
//					btnEdit.addClickListener(e->{
//						openDialogAddDoc(PropsUtil.isChangeDoc(),docModel);
//					});
//					btnEdit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
//					if(isMobileLayout) {
//						btnViewDoc.setText("Chỉnh sửa");
//					}
//					hlayoutButton.add(btnEdit);
//				}
//			}
			
			if(PropsUtil.isChangeDoc()) {
				ButtonTemplate btnEdit = new ButtonTemplate("Thay đổi văn bản nguồn",FontAwesome.Solid.EDIT.create());
				btnEdit.addClickListener(e->{
					openDialogAddDoc(PropsUtil.isChangeDoc(),docModel);
				});
				btnEdit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
				if(isMobileLayout) {
					btnViewDoc.setText("Chỉnh sửa");
				}
				hlayoutButton.add(btnEdit);
			}
			
			if(isMobileLayout) {
				btnViewDoc.setText("Xem");
			}

			vLayout.add(hlayoutButton);

		}else {
			Span spTitleWarning = new Span("Nhiệm vụ này là nhiệm vụ tự phát không có văn bản nguồn, đơn vị giao có thể thêm văn bản nguồn cho nhiệm vụ.");
			vLayout.add(spTitleWarning);	
			if(isOwner) {
				if(PropsUtil.isChangeDoc() == true) {
					ButtonTemplate btnAddDoc = new ButtonTemplate("Thêm văn bản nguồn",FontAwesome.Solid.PLUS.create());

					btnAddDoc.addClickListener(e->{
						openDialogAddDoc(PropsUtil.isChangeDoc(),null);
					});			

					vLayout.add(btnAddDoc);
				}
			}

		}
		

		
	}

	private void openDialogOverViewDoc(String idDoc,boolean isViewDoc) {
		DialogTemplate dialogTemplate = new DialogTemplate("Thông tin văn bản");

		DocOverviewForm docOverviewForm = new DocOverviewForm(idDoc, null, null,isViewDoc);
		dialogTemplate.setWidth("80%");
		dialogTemplate.setHeightFull();
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.add(docOverviewForm);
		dialogTemplate.open();
	}

	private void openDialogAddDoc(boolean checkDocRequired,DocModel docModel) {
		DialogTemplate dialogTemplate = new DialogTemplate("Thêm văn bản nguồn");
		BelongOrganizationModel belongOrganizationModel = SessionUtil.getOrg();
		UserAuthenticationModel userAuthenticationModel = SessionUtil.getUser();
		SignInOrgModel signInOrgModel = SessionUtil.getDetailOrg();
		DocListForm listDocForm = new DocListForm(userAuthenticationModel, belongOrganizationModel, signInOrgModel, checkDocRequired,docModel,null,null);


		listDocForm.addChangeListener(e->{
			docIsChoose = listDocForm.getListDocIsChoose().get(0);
		});
		dialogTemplate.add(listDocForm);
		dialogTemplate.getBtnSave().addClickListener(e->{
			if(docIsChoose != null) {
				ApiInputTaskModel apiInputTaskModel = new ApiInputTaskModel(outputTaskModel);
				apiInputTaskModel.setDocId(docIsChoose.getId());

				ApiOrgGeneralOfTaskModel creator = new ApiOrgGeneralOfTaskModel();
				creator.setOrganizationId(belongOrganizationModel.getOrganizationId());
				creator.setOrganizationName(belongOrganizationModel.getOrganizationName());
				creator.setOrganizationUserId(userAuthenticationModel.getId());
				creator.setOrganizationUserName(userAuthenticationModel.getFullName());

				apiInputTaskModel.setCreator(creator);
				if(updateTaskAfterAddDoc(apiInputTaskModel)) {
					dialogTemplate.close();

				}
			}
		});	
		dialogTemplate.open();
		dialogTemplate.setSizeFull();
	}

	private boolean updateTaskAfterAddDoc(ApiInputTaskModel apiInputTaskModel) {
		try {
			ApiResultResponse<ApiInputTaskModel> updateTask = ApiTaskService.updateTask(outputTaskModel.getId(), apiInputTaskModel);
			if(updateTask.isSuccess()) {
				NotificationTemplate.success("Thành công");
				fireEvent(new ClickEvent(this, false));			
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private Component createLayoutKeyAndValue(String key,String value,String widthHeader,String style) {
		HorizontalLayout hlayout = new HorizontalLayout();

		Span spHeader = new Span(key);
		spHeader.setWidth(widthHeader);
		spHeader.getStyle().setFontWeight(600).setFlexShrink("0");

		Span spValue = new Span(value);
		if(style != null) {
			spValue.getStyle().setColor(style);
		}

		hlayout.setWidthFull();
		hlayout.add(spHeader,spValue);
		hlayout.getStyle().setBorderBottom("1px solid #c3c3c3").setPadding("5px");
		return hlayout;
	}

	private String checkDocCategory(String docCategory) {
		if(docCategory.equals(DocCategoryEnum.CVDEN.getKey())) {
			return DocCategoryEnum.CVDEN.getTitle();
		}else {
			return DocCategoryEnum.CVDI.getTitle();
		}
	}

	private DocModel getDetailDoc(String idDoc) {
		try {
			ApiResultResponse<ApiDocModel> doc = ApiDocService.getAdoc(idDoc);
			if(doc.isSuccess()) {
				DocModel docModel = new DocModel(doc.getResult());
				return docModel;
			}
		} catch (Exception e) {
			return this.docModel;
		}
		return null;
	}

//	private Span createLayoutIconContent(Icon icon,String content,String background,String color) {
//		Span sp = new Span();
//		sp.add(icon,new Text(content));
//		sp.getStyle().setBackground(background).setColor(color);
//		icon.setSize("15px");
//		sp.getStyle().setDisplay(com.vaadin.flow.dom.Style.Display.FLEX)
//		.setFlexDirection(com.vaadin.flow.dom.Style.FlexDirection.ROW).setAlignItems(com.vaadin.flow.dom.Style.AlignItems.CENTER)
//		.set("gap", "10px").setFontSize("13px").setPadding("6px").setBorderRadius("5px").setFontWeight(600).setHeight("16px").setCursor("pointer");
//
//		return sp;
//	}

}
