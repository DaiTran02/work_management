package com.ngn.tdnv.task.forms.details;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.media.ApiInputMediaModel;
import com.ngn.api.media.ApiMediaModel;
import com.ngn.api.media.ApiMediaService;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tasks.ApiOutputTaskModel;
import com.ngn.api.tasks.ApiTaskService;
import com.ngn.api.tasks.actions.ApiActionService;
import com.ngn.api.tasks.actions.ApiCreatorActionModel;
import com.ngn.api.tasks.actions.ApiReportModel;
import com.ngn.api.tasks.actions.ApiUpdateProcessModel;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.tdnv.task.enums.StatusTaskEnum;
import com.ngn.tdnv.task.models.TaskOutputModel;
import com.ngn.tdnv.task.models.TaskProcessModel;
import com.ngn.utils.LocalDateUtil;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.ActtachmentForm;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.ConfirmDialogTemplate;
import com.ngn.utils.components.DialogTemplate;
import com.ngn.utils.components.NotificationTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.ngn.utils.uploads.UploadModuleBasic;
import com.ngn.utils.uploads.UploadModuleDataModel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

//Dùng để lập báo cáo khi mà giao nhiệm vụ con xuống.
public class TaskReportChildrenForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private BelongOrganizationModel belongOrganizationModel = SessionUtil.getOrg();
	private UserAuthenticationModel userAuthenticationModel = SessionUtil.getUser();

	private VerticalLayout vLayout = new VerticalLayout();
	private List<TaskOutputModel> listOutputTaskModels = new ArrayList<TaskOutputModel>();
	private List<TaskOutputModel> listTaskIsChoose = new ArrayList<TaskOutputModel>();
	private VerticalLayout vLayoutForTableReport = new VerticalLayout();

	private List<UploadModuleDataModel> listDataFileUpload = new ArrayList<UploadModuleDataModel>();
	private List<String> listAllIdAttachment = new ArrayList<String>();

	private ButtonTemplate btnCancel = new ButtonTemplate("Quay lại",FontAwesome.Solid.ARROW_LEFT.create());

	private String parentIdOfTask;
	public TaskReportChildrenForm(String parentIdOfTask) {
		this.parentIdOfTask = parentIdOfTask;
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		H2 headerReport = new H2("Tổng hợp báo cáo");
		headerReport.getStyle().setMargin("auto").setColor("black");

		vLayout.add(headerReport);
		this.add(vLayout);

	}

	@Override
	public void configComponent() {

		btnCancel.addClickListener(e->{
			this.removeAll();
			vLayout.removeAll();
			buildLayout();
			loadData();
		});

	}

	private void loadData() {
		listOutputTaskModels = new ArrayList<TaskOutputModel>();
		listTaskIsChoose = new ArrayList<TaskOutputModel>();
		ApiResultResponse<List<ApiOutputTaskModel>> data = ApiTaskService.getListSubTask(parentIdOfTask);
		if(data.isSuccess()) {
			data.getResult().stream().forEach(model->{
				listOutputTaskModels.add(new TaskOutputModel(model));
			});

		}

		listOutputTaskModels.stream().forEach(model->{
			vLayout.add(createLayout(model,true));
		});

		vLayout.add(vLayoutForTableReport);
	}

	//Tạo layout danh sách nhiệm vụ đã giao
	private Component createLayout(TaskOutputModel outputTaskModel,boolean isChoose) {

		HorizontalLayout hLayoutItem = new HorizontalLayout();

		Checkbox checkbox = new Checkbox();
		checkbox.getStyle().setMargin("auto");
		checkbox.setVisible(isChoose);

		VerticalLayout vLayoutItem = new VerticalLayout();
		
		vLayoutItem.setSpacing(false);

		Span spanAssginee = new Span();
		spanAssginee.getElement().getThemeList().add("badge success");
		String assignee = outputTaskModel.getAssignee().getOrganizationUserId() == null ? outputTaskModel.getAssignee().getOrganizationName() :
			outputTaskModel.getAssignee().getOrganizationName() + " ("+outputTaskModel.getAssignee().getOrganizationUserName().toString()+")";
		spanAssginee.setText("Đơn vị thực hiện: "+assignee);

		VerticalLayout vLayoutContent = new VerticalLayout();
		vLayoutContent.add(createLayoutKeyValue("Tiêu đề", outputTaskModel.getTitle(), null, null),createLayoutKeyValue("Nội dung:", outputTaskModel.getDescription(), null, null)
				);

		TaskProcessModel taskProcessModel = outputTaskModel.getProcesses().isEmpty() ? new TaskProcessModel() : outputTaskModel.getProcesses().get(0);

		TaskProgressViewForm taskProgressViewForm = new TaskProgressViewForm(outputTaskModel.getId(), taskProcessModel, true, outputTaskModel.getState().toString(),true);


		VerticalLayout vLayoutResult = new VerticalLayout();



		if(outputTaskModel.getState().equals(StatusTaskEnum.DAHOANTHANH.getKey()) || outputTaskModel.getState().equals(StatusTaskEnum.DAHOANTHANHQUAHAN.getKey())) {
			checkbox.setEnabled(true);
			ButtonTemplate buttonAttachmentResult = new ButtonTemplate("Tệp hoàn thành ("+outputTaskModel.getCompleted().getAttachments().size()+")",FontAwesome.Solid.PAPERCLIP.create());
			buttonAttachmentResult.addClickListener(e->{
				openDialogViewAttachment(outputTaskModel.getCompleted().getAttachments());
			});

			Component component = createLayoutKeyValue("Kết quả: ", "Nhiệm vụ đã hoàn thành và có thể tổng hợp", null,"#066601");
			component.getChildren().forEach(cm->{
				if(cm.getId().isPresent() && cm.getId().get().equals("content")) {
					cm.getStyle().setFontWeight(600);
				}
			});

			vLayoutResult.add(createLayoutKeyValue("Ngày hoàn thành: ", outputTaskModel.getCompleteTimeText(), null, null),buttonAttachmentResult,
					component);
		}else {
			checkbox.setEnabled(false);
			Component component = createLayoutKeyValue("Kết quả: ", "Nhiệm vụ này chưa hoàn thành không thể tổng hợp", null,"#660101");
			component.getChildren().forEach(cm->{
				if(cm.getId().isPresent() && cm.getId().get().equals("content")) {
					cm.getStyle().setFontWeight(600);
				}
			});
			vLayoutResult.add(component);
		}
		

		checkbox.addClickListener(e->{
			if(checkbox.getValue() == true) {
				listTaskIsChoose.add(outputTaskModel);
			}else {
				listTaskIsChoose.remove(outputTaskModel);
			}
		});
		
	

		ButtonTemplate btnViewTask = new ButtonTemplate("Xem thông tin nhiệm vụ",FontAwesome.Solid.EYE.create());
		btnViewTask.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnViewTask.addClickListener(e->openDialogViewDetail(outputTaskModel.getId()));

		vLayoutItem.setWidthFull();
		vLayoutItem.add(spanAssginee,new Hr(),vLayoutContent,taskProgressViewForm,vLayoutResult,btnViewTask);

		hLayoutItem.setWidthFull();
		hLayoutItem.add(checkbox,vLayoutItem);
		hLayoutItem.getStyle().setBoxShadow("rgba(99, 99, 99, 0.2) 0px 2px 8px 0px").setPadding("10px").setBorderRadius("10px");

		return hLayoutItem;
	}



	//Báo cáo nhiệm vụ đã hoàn thành cho đơn vị giao nhiệm vụ
	@SuppressWarnings("deprecation")
	private void openConfirmReport(String explainString) {
		ConfirmDialogTemplate confirmDialogTemplate = new ConfirmDialogTemplate("BÁO CÁO HOÀN THÀNH NHIỆM VỤ");

		listDataFileUpload = new ArrayList<UploadModuleDataModel>();
		List<Object> listIdAttach = new ArrayList<Object>();

		MultiSelectComboBox<UploadModuleDataModel> multiSelectCmbComment = new MultiSelectComboBox<UploadModuleDataModel>("Danh sách đính kèm hoàn thành");
		multiSelectCmbComment.setVisible(false);
		multiSelectCmbComment.setWidthFull();
		multiSelectCmbComment.setItemLabelGenerator(UploadModuleDataModel::getFileName);


		ButtonTemplate btnAttachment = new ButtonTemplate("Thêm đính kèm báo cáo",FontAwesome.Solid.PAPERCLIP.create());
		btnAttachment.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnAttachment.getStyle().setMarginLeft("auto");
		btnAttachment.addClickListener(e->{
			openDialogUploadFile(()->{
				multiSelectCmbComment.setItems(listDataFileUpload);
				multiSelectCmbComment.select(listDataFileUpload);
				if(listDataFileUpload.isEmpty()) {
					multiSelectCmbComment.setVisible(false);
				}else {
					multiSelectCmbComment.setVisible(true);
				}
				listAllIdAttachment.stream().forEach(model->{
					listIdAttach.add(model);
				});
			});
		});

		VerticalLayout vLayoutReport = new VerticalLayout();
		vLayoutReport.setWidthFull();

		NumberField numberField = new NumberField("Cập nhật tiến độ hoàn thành");
		numberField.setWidthFull();
		numberField.setValue(100.0);
		numberField.setReadOnly(true);

		TextField txtExplain = new TextField("Diễn giải");
		txtExplain.setWidthFull();
		txtExplain.setPlaceholder("Đã hoàn thành");	
		txtExplain.setValue(explainString);

		vLayoutReport.add(numberField,txtExplain);
		confirmDialogTemplate.addConfirmListener(e->{
			String explain = txtExplain.getValue().isEmpty() ? "Đã hoàn thành" : txtExplain.getValue();
			doUpdateProgress(100.0, explain, listIdAttach,()->{
				doReport(listAllIdAttachment);
			});
		});

		vLayoutReport.add(btnAttachment,multiSelectCmbComment);

		confirmDialogTemplate.add(vLayoutReport);


		confirmDialogTemplate.open();
	}

	private void doReport(List<String> listIdAttachment) {
		ApiReportModel apiReportModel = new ApiReportModel();
		apiReportModel.setCompletedTime(LocalDateUtil.localDateTimeToLong(LocalDateTime.now()));
		apiReportModel.setAttachments(listIdAttachment);
		apiReportModel.setCreator(getCreator());
		ApiResultResponse<Object> report = ApiActionService.doReport(parentIdOfTask, apiReportModel);
		if(report.isSuccess()) {
			NotificationTemplate.success("Thành công");
			fireEvent(new ClickEvent(this,false));
		}
	}

	//Cập nhật tiến độ hoàn thành
	private void doUpdateProgress(double percent,String explain,List<Object> listAttachments,Runnable run) {
		ApiUpdateProcessModel apiUpdateProcessModel = new ApiUpdateProcessModel();
		apiUpdateProcessModel.setPercent(percent);
		apiUpdateProcessModel.setExplain(explain);
		apiUpdateProcessModel.setCreator(getCreator());
		List<String> attachments = listAttachments.stream().map(Object::toString).collect(Collectors.toList());
		apiUpdateProcessModel.setAttachments(attachments);
		ApiResultResponse<Object> process = ApiActionService.doUpdateProcess(parentIdOfTask, apiUpdateProcessModel);
		if(process.isSuccess()) {
			run.run();
		}
	}


	//Lấy thông tin của người đang thực hiện
	private ApiCreatorActionModel getCreator() {
		ApiCreatorActionModel actionModel = new ApiCreatorActionModel();
		actionModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
		actionModel.setOrganizationName(belongOrganizationModel.getOrganizationName());
		actionModel.setOrganizationUserId(userAuthenticationModel.getId());
		actionModel.setOrganizationUserName(userAuthenticationModel.getUsername());
		return actionModel;
	}

	private void openDialogUploadFile(Runnable run) {
		DialogTemplate dialogTemplate = new DialogTemplate("Thêm đính kèm");

		TaskDoAttachmentForm taskDoAttachmentForm = new TaskDoAttachmentForm();

		taskDoAttachmentForm.addChangeListener(e->{
			listDataFileUpload.addAll(taskDoAttachmentForm.getListFileUpload());
			listAllIdAttachment.addAll(taskDoAttachmentForm.getListAttachment());
			run.run();
			dialogTemplate.close();
		});


		dialogTemplate.add(taskDoAttachmentForm);

		dialogTemplate.setWidth("50%");
		dialogTemplate.setHeight("70%");
		dialogTemplate.getBtnSave().setText("Thêm");
		dialogTemplate.getBtnSave().addClickListener(e->{
			taskDoAttachmentForm.uploadFile();
		});

		dialogTemplate.open();
	}

	private Component createLayoutKeyValue(String header,String content,String style,String cssStyle) {
		HorizontalLayout hLayout = new HorizontalLayout();
		hLayout.setSpacing(false);

		Span spanHeader = new Span(header);
		spanHeader.getStyle().setFontWeight(600);
		spanHeader.setMinWidth("65px");

		Span spanContent = new Span(content);
		spanContent.getStyle().setPaddingLeft("5px");
		spanContent.setId("content");

		if(style != null) {
			spanContent.getElement().getThemeList().add(style);
		}
		
		if(cssStyle != null) {
			spanContent.getStyle().setColor(cssStyle);
		}

		hLayout.add(spanHeader,spanContent);

		hLayout.setSpacing(false);
		return hLayout;
	}


	//Xem danh sách đính kèm
	private void openDialogViewAttachment(List<Object> listIdAttachment) {
		DialogTemplate dialogTemplate = new DialogTemplate("DANH SÁCH ĐÍNH KÈM");
		List<String> listAttachment = new ArrayList<String>();
		for(Object object : listIdAttachment) {
			listAttachment.add(object.toString());
		}
		ActtachmentForm acttachmentForm = new ActtachmentForm(listAttachment, false);
		dialogTemplate.add(acttachmentForm);
		dialogTemplate.setWidth("60%");
		dialogTemplate.setHeightFull();
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.open();
	}

	private void openDialogViewDetail(String idTask) {
		DialogTemplate dialogTemplate = new DialogTemplate("THÔNG TIN NHIỆM VỤ");

		TaskViewDetailForm taskViewDetailForm = new TaskViewDetailForm(idTask,belongOrganizationModel);
		taskViewDetailForm.setTypeOfTask(true, false, false, false);

		taskViewDetailForm.addChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});

		dialogTemplate.getFooter().removeAll();

		dialogTemplate.add(taskViewDetailForm);
		dialogTemplate.setSizeFull();


		dialogTemplate.open();
	}

	private void createLayoutForReport() {
		vLayout.removeAll();
		vLayoutForTableReport.removeAll();

		H2 headerReport = new H2("Kết quả báo cáo");
		headerReport.getStyle().setMargin("auto").setColor("black");
		vLayout.add(headerReport);

		btnCancel.setTooltipText("Quay lại");

		vLayout.add(btnCancel);
	}

	public void createLayoutReport() {
		
		VerticalLayout vLayoutReport = new VerticalLayout();

		TextField txtProgress = new TextField("Tiến độ hoàn thành");
		txtProgress.setValue("100%");
		txtProgress.setReadOnly(true);
		txtProgress.setWidthFull();


		TextField txtResion = new TextField("Diễn giải");
		txtResion.setPlaceholder("Đã hoàn thành");
		txtResion.setValue("Đã hoàn thành");
		txtResion.setWidthFull();

		vLayoutReport.add(txtProgress,txtResion);


		UploadModuleBasic uploadModuleBasic = new UploadModuleBasic();
		uploadModuleBasic.loadDisplay();
		uploadModuleBasic.initUpload();
		uploadModuleBasic.setWidthFull();
		
		List<Object> listIdAttachment = new ArrayList<Object>();
		
		
		for(UploadModuleDataModel dataAttach : uploadModuleBasic.getListFileUpload()) {
			File file = new File(dataAttach.getFileName());
			try {
				FileUtils.copyInputStreamToFile(dataAttach.getInputStream(), file);
				ApiInputMediaModel apiInputMediaModel = new ApiInputMediaModel();
				apiInputMediaModel.setFile(new FileSystemResource(file));
				apiInputMediaModel.setDescription(dataAttach.getDescription());
				apiInputMediaModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
				apiInputMediaModel.setOrganizationName(belongOrganizationModel.getOrganizationName());
				apiInputMediaModel.setOrganizationUserId(userAuthenticationModel.getId());
				apiInputMediaModel.setOrganizationUserName(userAuthenticationModel.getFullName());
				doCreateFile(apiInputMediaModel);
				FileUtils.delete(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		listAllIdAttachment.stream().forEach(model->{
			listIdAttachment.add(model);
		});
		
		ButtonTemplate btnReport = new ButtonTemplate("Báo cáo nhiệm vụ đã hoàn thành",FontAwesome.Solid.PAPER_PLANE.create());
		btnReport.getStyle().setMarginLeft("auto");
		btnReport.addClickListener(e->{
			openConfirmReport(txtResion.getValue());
		});

		vLayoutReport.add(uploadModuleBasic,btnReport);

		vLayoutReport.setWidthFull();

		if(listTaskIsChoose.isEmpty()) {
			openConfirmReport("Đã hoàn thành");
		}else {
			createLayoutForReport();
			vLayout.add(createLayoutReportListTaskChoose());
		}
	}

	private Component createLayoutReportListTaskChoose() {
		listAllIdAttachment = new ArrayList<String>();
		
		VerticalLayout vLayoutLeft = new VerticalLayout();
		Span spTitleLeft = new Span("*Danh sách nhiệm vụ hoàn thành đã chọn");
		spTitleLeft.getStyle().setFontWeight(600);
		vLayoutLeft.add(spTitleLeft);
		
		for(TaskOutputModel model : listTaskIsChoose) {
			vLayoutLeft.add(createLayout(model,false));
		}

		VerticalLayout vLayoutRight = new VerticalLayout();
		Span spTitleRight = new Span("*Thông tin báo cáo hoàn thành");
		spTitleRight.getStyle().setFontWeight(600);
		vLayoutRight.add(spTitleRight);

		TextField txtProgress = new TextField("Tiến độ hoàn thành");
		txtProgress.setValue("100%");
		txtProgress.setReadOnly(true);
		txtProgress.setWidthFull();


		TextField txtReason = new TextField("Diễn giải");
		txtReason.setPlaceholder("Đã hoàn thành");
		txtReason.setWidthFull();

		
		
		List<Object> listIdAttachment = new ArrayList<Object>();


		for(TaskOutputModel model : listTaskIsChoose) {
			model.getProcesses().get(0).getAttachments().forEach(attach->{
				listIdAttachment.add(attach);
			});
			model.getCompleted().getAttachments().forEach(attach->{
				listIdAttachment.add(attach.toString());
			});
		}
		
		listIdAttachment.forEach(item->{
			listAllIdAttachment.add(String.valueOf(item));
		});
		
		ButtonTemplate btnAttachment = new ButtonTemplate("Tổng số đính kèm từ các nhiệm vụ con ("+listIdAttachment.size()+")",FontAwesome.Solid.PAPERCLIP.create());
		btnAttachment.addClickListener(e->openDialogViewAttachment(listIdAttachment));

		vLayoutRight.add(txtProgress,txtReason,btnAttachment);

		UploadModuleBasic uploadModuleBasic = new UploadModuleBasic();
		uploadModuleBasic.loadDisplay();
		uploadModuleBasic.initUpload();
		uploadModuleBasic.setWidthFull();
		
		ButtonTemplate btnReport = new ButtonTemplate("Báo cáo nhiệm vụ đã hoàn thành",FontAwesome.Solid.PAPER_PLANE.create());
		btnReport.getStyle().setMarginLeft("auto");
		btnReport.addClickListener(e->{
			listAllIdAttachment.clear();
			getIdAttach(uploadModuleBasic.getListFileUpload());
			listIdAttachment.forEach(item->{
				listAllIdAttachment.add(String.valueOf(item));
			});
			doUpdateProgress(100, txtReason.getValue(), new ArrayList<Object>(), ()->{
				doReport(listAllIdAttachment);
			});
		});

		vLayoutRight.add(uploadModuleBasic,btnReport);

		vLayoutRight.setWidthFull();

		SplitLayout splitLayout = new SplitLayout(vLayoutLeft,vLayoutRight);
		splitLayout.setWidthFull();
		splitLayout.getStyle().set("gap", "20px");

		return splitLayout;
	}
	
	private void getIdAttach(List<UploadModuleDataModel> listAttach){
		for(UploadModuleDataModel dataAttach : listAttach) {
			File file = new File(dataAttach.getFileName());
			try {
				FileUtils.copyInputStreamToFile(dataAttach.getInputStream(), file);
				ApiInputMediaModel apiInputMediaModel = new ApiInputMediaModel();
				apiInputMediaModel.setFile(new FileSystemResource(file));
				apiInputMediaModel.setDescription(dataAttach.getDescription());
				apiInputMediaModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
				apiInputMediaModel.setOrganizationName(belongOrganizationModel.getOrganizationName());
				apiInputMediaModel.setOrganizationUserId(userAuthenticationModel.getId());
				apiInputMediaModel.setOrganizationUserName(userAuthenticationModel.getFullName());
				doCreateFile(apiInputMediaModel);
				FileUtils.delete(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void doCreateFile(ApiInputMediaModel apiModel) {
		try {
			ApiResultResponse<ApiMediaModel> data = ApiMediaService.createFile(apiModel);
			if(data.getStatus() == 200 || data.getStatus() == 201) {
				System.out.println(" What how much I have id list: "+data.getResult() );
				listAllIdAttachment.add(data.getResult().getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
