package com.ngn.tdnv.personal_record.form;

import java.util.stream.Collectors;

import com.ngn.api.personal_record.ApiPersonalDetailModel;
import com.ngn.api.personal_record.ApiPersonalRecordModel;
import com.ngn.api.personal_record.ApiPersonalService;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.doc.ApiDocModel;
import com.ngn.api.tasks.ApiOutputTaskModel;
import com.ngn.interfaces.FormInterface;
import com.ngn.utils.LocalDateUtil;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;

public class TransferredForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
    
	private String idPersonal;
	private VerticalLayout vLayout = new VerticalLayout();
	private TextField txtTitle = new TextField("Tiêu đề");
	private TextArea txtDescription = new TextArea("Mô tả");
	private TextField txtOldUsers = new TextField("Người đã xử lý");
	private TextField txtTransferTime = new TextField("Ngày chuyển giao");

	private Grid<ApiDocModel> gridDocs = new Grid<>(ApiDocModel.class,false);
	private Grid<ApiOutputTaskModel> gridTasks = new Grid<>(ApiOutputTaskModel.class,false);
	
	private ButtonTemplate btnConfirm = new ButtonTemplate("Xác nhận thực hiện",FontAwesome.Solid.CHECK_CIRCLE.create());
	private ButtonTemplate btnReject = new ButtonTemplate("Không nhận",FontAwesome.Solid.TIMES_CIRCLE.create());
    
	public TransferredForm(String idPersonal) {
		this.idPersonal = idPersonal;
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		vLayout.setWidthFull();

		txtTitle.setWidthFull();
		txtTitle.setReadOnly(true);

		txtDescription.setWidthFull();
		txtDescription.setReadOnly(true);
		txtDescription.setHeight("120px");

		txtOldUsers.setWidthFull();
		txtOldUsers.setReadOnly(true);

		txtTransferTime.setWidthFull();
		txtTransferTime.setReadOnly(true);

		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidthFull();
		hl.add(txtOldUsers, txtTransferTime);
		hl.expand(txtOldUsers);

		// Docs grid: show only summary
		gridDocs = new Grid<>(ApiDocModel.class,false);
		gridDocs.addColumn(ApiDocModel::getSummary).setHeader("Trích yếu");
		gridDocs.addComponentColumn(model->{
			HorizontalLayout hLayout = new HorizontalLayout();
			ButtonTemplate btnView = new ButtonTemplate("Xem chi tiết",FontAwesome.Solid.EYE.create());
			btnView.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnView.addClickListener(e->viewDocDetail(model.getId()));
			hLayout.add(btnView);
			return hLayout;
		}).setHeader("Thao tác").setWidth("120px").setFlexGrow(0);
		gridDocs.setWidthFull();

		// Tasks grid: show only title
		gridTasks = new Grid<>(ApiOutputTaskModel.class,false);
		gridTasks.addColumn(ApiOutputTaskModel::getTitle).setHeader("Tiêu đề nhiệm vụ");
		gridTasks.addComponentColumn(model->{
			HorizontalLayout hLayout = new HorizontalLayout();
			ButtonTemplate btnView = new ButtonTemplate("Xem chi tiết",FontAwesome.Solid.EYE.create());
			btnView.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnView.addClickListener(e->viewTaskDetail(model.getId()));
			hLayout.add(btnView);
			return hLayout;
		}).setHeader("Thao tác").setWidth("120px").setFlexGrow(0);
		gridTasks.setWidthFull();

		vLayout.add(txtTitle, txtDescription, hl, gridDocs, gridTasks);
		this.add(vLayout, createActionButtons());
	}

	@Override
	public void configComponent() {
		btnConfirm.addClickListener(e->confirmAction());
		btnReject.addClickListener(e->rejectAction());
	}
	
	private HorizontalLayout createActionButtons() {
		HorizontalLayout hLayout = new HorizontalLayout();
		hLayout.setWidthFull();
		btnConfirm.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
		btnReject.addThemeVariants(ButtonVariant.LUMO_ERROR);
		hLayout.add(btnConfirm, btnReject);
		hLayout.getStyle().setMarginTop("20px");
		return hLayout;
	}
	
	private void confirmAction() {
		// TODO: Add action to confirm (call API for accepting the transferred personal)
		System.out.println("Xác nhận thực hiện hồ sơ: " + idPersonal);
	}
	
	private void rejectAction() {
		// TODO: Add action to reject (call API for rejecting the transferred personal)
		System.out.println("Không nhận hồ sơ: " + idPersonal);
	}
	
	private void viewDocDetail(String docId) {
		// TODO: Open dialog or navigate to view document detail
		System.out.println("View doc: " + docId);
	}
	
	private void viewTaskDetail(String taskId) {
		// TODO: Open dialog or navigate to view task detail
		System.out.println("View task: " + taskId);
	}

	public void loadData() {
		try {
			ApiResultResponse<ApiPersonalDetailModel> data = ApiPersonalService.getDetailPersonal(idPersonal);
			if(data != null && data.isSuccess() && data.getResult() != null) {
				ApiPersonalDetailModel model = data.getResult();
				txtTitle.setValue(model.getTitle() == null ? "" : model.getTitle());
				txtDescription.setValue(model.getDescription() == null ? "" : model.getDescription());

				if(model.getOldUsers() != null && !model.getOldUsers().isEmpty()) {
					String names = model.getOldUsers().stream().map(u->u.getFullName()).collect(Collectors.joining(", "));
					txtOldUsers.setValue(names);
				}else {
					txtOldUsers.setValue("");
				}

				if(model.getDocs() != null) {
					gridDocs.setItems(model.getDocs());
				} else {
					gridDocs.setItems();
				}

				if(model.getTasks() != null) {
					gridTasks.setItems(model.getTasks());
				} else {
					gridTasks.setItems();
				}
			}

			// transferTime is available on the record model (ApiPersonalRecordModel)
			ApiResultResponse<ApiPersonalRecordModel> record = ApiPersonalService.getOnePersonal(idPersonal);
			if(record != null && record.isSuccess() && record.getResult() != null) {
				ApiPersonalRecordModel r = record.getResult();
				if(r.getTransferTime() != null) {
					txtTransferTime.setValue(LocalDateUtil.dfDateTime.format(r.getTransferTime()));
				} else {
					txtTransferTime.setValue("");
				}
			}
		} catch (Exception e) {
			// swallow for now (follow project pattern)
		}
	}

}
