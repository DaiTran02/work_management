package com.ngn.tdnv.doc.forms;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.doc.ApiDocModel;
import com.ngn.api.doc.ApiDocService;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.models.sign_in_org.SignInOrgModel;
import com.ngn.tdnv.doc.forms.create_task.TaskCreateFormV2;
import com.ngn.tdnv.doc.models.DocModel;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.ActtachmentForm;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.ConfirmDialogTemplate;
import com.ngn.utils.components.DialogTemplate;
import com.ngn.utils.components.NotificationTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.ngn.utils.models.model_of_organization.OrganizationModel;
import com.ngn.utils.models.model_of_organization.UserOranizationExModel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style.AlignItems;
import com.vaadin.flow.dom.Style.Display;
import com.vaadin.flow.dom.Style.FlexDirection;

public class DocInfoForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private boolean isMobile = false;
	private VerticalLayoutTemplate vLayout = new VerticalLayoutTemplate();
	private ButtonTemplate btnAssign = new ButtonTemplate("Giao nhiệm vụ",FontAwesome.Solid.MAIL_FORWARD.create());
	private ButtonTemplate btnUpdate = new ButtonTemplate("Cập nhật",FontAwesome.Solid.EDIT.create());
	private ButtonTemplate btnDelete = new ButtonTemplate("Xóa",FontAwesome.Solid.TRASH.create());

	private BelongOrganizationModel belongOrganizationModel = SessionUtil.getOrg();
	private SignInOrgModel signInOrgModel = SessionUtil.getDetailOrg();
	private UserAuthenticationModel userAuthenticationModel = SessionUtil.getUser();
	private DocModel docModel = new DocModel();

	private boolean isViewDoc = false;
	private String idDoc;
	public DocInfoForm(String idDoc,boolean isViewDoc) {
		this.idDoc = idDoc;
		this.isViewDoc = isViewDoc;
		checkMobile();
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		System.out.println("ID Doc: "+idDoc);
		if(isViewDoc) {
			btnAssign.setEnabled(false);
			btnUpdate.setEnabled(false);
			btnDelete.setEnabled(false);
		}

		this.add(vLayout);
		vLayout.getStyle().setBoxShadow("rgba(0, 0, 0, 0.16) 0px 1px 4px").setPadding("10px");
	}

	@Override
	public void configComponent() {
		if(docModel != null) {
			btnAssign.addClickListener(e->openDialogCreateTask2(docModel));
			btnUpdate.addClickListener(e->openDialogUpdateDoc(docModel));
			btnDelete.addClickListener(e->openConfirmDialogDelete(idDoc));
		}
	}

	public void loadData() {
		ApiResultResponse<ApiDocModel> data = ApiDocService.getAdoc(idDoc);
		if(data.isSuccess()) {
			docModel = new DocModel(data.getResult());
		}
		createLayout(docModel);
	}

	private void checkMobile() {
		UI.getCurrent().getPage().retrieveExtendedClientDetails(e->{
			if(e.getScreenWidth() < 780) {
				isMobile = true;
			}
		});
	}

	private void createLayout(DocModel docModel) {
		vLayout.removeAll();

		btnAssign.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnAssign.getStyle().setMarginLeft("auto");


		btnUpdate.addThemeVariants(ButtonVariant.LUMO_TERTIARY);


		btnDelete.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnDelete.addThemeVariants(ButtonVariant.LUMO_ERROR);

		if(docModel.getCountTask() > 0) {
			btnDelete.setEnabled(false);
		}

		HorizontalLayout hLayoutButton = new HorizontalLayout();
		hLayoutButton.setWidthFull();
		hLayoutButton.add(btnAssign,btnUpdate,btnDelete);
		hLayoutButton.getStyle().setBorderBottom("1px solid #d9d3d3");

		String widthHeader = "200px";

		vLayout.add(hLayoutButton);

		HorizontalLayout hLayoutGeneral = new HorizontalLayout();

		hLayoutGeneral.setWidthFull();

		VerticalLayout vLayoutLeft = new VerticalLayout();
		vLayoutLeft.setWidth("50%");
		
		MultiSelectComboBox<Pair<OrganizationModel, UserOranizationExModel>> multilReceiver = new MultiSelectComboBox<Pair<OrganizationModel,UserOranizationExModel>>();
		List<Pair<OrganizationModel, UserOranizationExModel>> listDataReceiver = new ArrayList<Pair<OrganizationModel,UserOranizationExModel>>();
		docModel.getReceivers().forEach(model->{
			OrganizationModel organizationModel = new OrganizationModel();
			organizationModel.setId(model.getOrganizationId());
			organizationModel.setName(model.getOrganizationName());
			UserOranizationExModel userOranizationExModel = null;
			if(model.getOrganizationUserId() != null) {
				userOranizationExModel = new UserOranizationExModel();
				userOranizationExModel.setUserId(model.getOrganizationUserId().toString());
				userOranizationExModel.setUserName(model.getOrganizationUserName() == null ? "Đang cập nhật" : model.getOrganizationName());
			}
			listDataReceiver.add(Pair.of(organizationModel,userOranizationExModel));
		});
		
		multilReceiver.setItems(listDataReceiver);
		multilReceiver.select(listDataReceiver);
		multilReceiver.setReadOnly(true);
		multilReceiver.setItemLabelGenerator(model->{
			String user = model.getValue() != null ? model.getValue().getFullName() : "";
			return model.getKey().getName() + user;
		});
		
		
		vLayoutLeft.add(createLayoutKeyAndValue("Số hiệu: ", docModel.getNumber(), widthHeader, null),
				createLayoutKeyAndValue("Ký hiệu: ", docModel.getSymbol(), widthHeader, null),
				createLayoutKeyAndValue("Độ mật: ", docModel.getSecurity().getName(), widthHeader, null),
				createLayoutKeyAndValue("Thể loại: ", docModel.getType(), widthHeader, idDoc),
				createLayoutKeyAndValue("Người ký: ", docModel.getSignerName(), widthHeader, idDoc),
				createLayoutKeyAndValue("Chức vụ người ký: ", docModel.getSignerPosition(), widthHeader, idDoc),
//				createLayoutKeyAndValue("Nơi ban hành: ", docModel.getOrgCreateNameText(), widthHeader, idDoc),
//				createLayoutKeyAndValue("Nơi nhận: ", docModel.getOrgReceiveNameText(), widthHeader, idDoc),
				createLayoutKeyAndComponent("Nơi nhận: ", multilReceiver, widthHeader),
				createLayoutKeyAndValue("Trích yếu: ", docModel.getSummary(), widthHeader, idDoc),
				createLayoutKeyAndValue("Đơn vị quản lý: ", docModel.getOwner().getOrganizationName(), widthHeader, idDoc),
				createLayoutKeyAndValue("Người lập văn bản: ", docModel.getOwner().getOrganizationUserName(), widthHeader, idDoc),
				createLayoutKeyAndValue("Mã văn bản: ", docModel.getIOfficeIdText(), widthHeader, idDoc),
				createLayoutKeyAndValue("Ngày văn bản vào hệ thống: ", docModel.getCreateTimeText(), widthHeader, idDoc),
				createLayoutKeyAndValue("Ngày ký: ", docModel.getRegTimeText(), widthHeader, idDoc),
				createLayoutKeyAndValue("Ngày cập nhật văn bản: ", docModel.getUpdateTimeText(), widthHeader, idDoc),
				createLayoutKeyAndValue("Nhiệm vụ đã giao từ văn bản: ", docModel.getCountTaskText(), widthHeader, idDoc));

		VerticalLayout vLayoutRight = new VerticalLayout();
		vLayoutRight.setWidth("50%");
		vLayoutRight.add(initDocActtachment(docModel));
		vLayoutRight.getStyle().setBorderLeft("1px solid #d9d3d3");
		hLayoutGeneral.add(vLayoutLeft,vLayoutRight);
		if(isMobile) {
			hLayoutGeneral.getStyle().setDisplay(Display.FLEX).setFlexDirection(FlexDirection.COLUMN);
			vLayoutLeft.setWidthFull();
			vLayoutRight.setWidthFull();
			vLayoutRight.getStyle().setBorderLeft("none").setBorderTop("1px solid #d9d3d3");
		}
		vLayout.add(hLayoutGeneral);

	}

	private Component initDocActtachment(DocModel docModel) {
		ActtachmentForm acttachmentForm = new ActtachmentForm(docModel.getAttachments(),true);
		return acttachmentForm;
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

	private Component createLayoutKeyAndComponent(String key,Component value, String widthHeader) {
		HorizontalLayout hlayout = new HorizontalLayout();

		Span spHeader = new Span(key);
		spHeader.setWidth(widthHeader);
		spHeader.getStyle().setFontWeight(600).setFlexShrink("0");

		value.getStyle().setWidth("100%");

		hlayout.setWidthFull();
		hlayout.add(spHeader,value);
		hlayout.getStyle().setBorderBottom("1px solid #c3c3c3").setPadding("5px").setAlignItems(AlignItems.CENTER).setPaddingTop("0");
		return hlayout;
	}

	private void openDialogUpdateDoc(DocModel docModel) {
		DialogTemplate dialogTemplate = new DialogTemplate("CHỈNH SỬA VĂN BẢN");
		DocEditForm editDocForm = new DocEditForm(docModel.getId(),()->{
			loadData();
			dialogTemplate.close();
			NotificationTemplate.success("Thành công");
		},userAuthenticationModel,belongOrganizationModel,signInOrgModel,docModel.getCategory().getKey());
		dialogTemplate.add(editDocForm);

		dialogTemplate.getBtnSave().addClickListener(e->{
			editDocForm.saveADoc();
		});

		dialogTemplate.setHeightFull();
		dialogTemplate.setWidth("80%");
		dialogTemplate.setLayoutMobile();
		dialogTemplate.open();
	}

	private void openConfirmDialogDelete(String idDoc) {
		ConfirmDialogTemplate confirmDialogTemplate = new ConfirmDialogTemplate("XÓA VĂN BẢN");

		confirmDialogTemplate.setText("Xác nhận xóa văn bản");
		confirmDialogTemplate.setDelete();
		confirmDialogTemplate.addConfirmListener(e->{
			doDelete(idDoc);
		});
		confirmDialogTemplate.open();
	}

	private void doDelete(String idDoc) {
		try {
			ApiResultResponse<Object> delete = ApiDocService.deleteDoc(idDoc);
			if(delete.getStatus() == 200 || delete.getStatus() == 201) {
				NotificationTemplate.success(delete.getMessage());
				fireEvent(new ClickEvent(this, false));
			}else {
				NotificationTemplate.error(delete.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void openDialogCreateTask2(DocModel docModel) {
		DialogTemplate dialogTemplate = new DialogTemplate("Giao nhiệm vụ mới");
		TaskCreateFormV2 createTaskForm = new TaskCreateFormV2(docModel.getId(),belongOrganizationModel, userAuthenticationModel,signInOrgModel,null);
		dialogTemplate.add(createTaskForm);

		createTaskForm.addChangeListener(e->{
			loadData();
			refreshMainLayout();
			dialogTemplate.close();
		});

		dialogTemplate.setHeightFull();
		if(docModel.getAttachments().isEmpty()) {
			dialogTemplate.setWidth("60%");
		}else {
			dialogTemplate.setWidth("100%");
		}
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setLayoutMobile();

		dialogTemplate.open();
	}

}
