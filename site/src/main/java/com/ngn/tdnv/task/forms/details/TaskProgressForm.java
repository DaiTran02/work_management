package com.ngn.tdnv.task.forms.details;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
import com.ngn.tdnv.task.models.TaskOutputModel;
import com.ngn.tdnv.task.models.TaskProcessModel;
import com.ngn.utils.LocalDateUtil;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.ConfirmDialogTemplate;
import com.ngn.utils.components.DialogTemplate;
import com.ngn.utils.components.NotificationTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.ngn.utils.uploads.UploadModuleBasic;
import com.ngn.utils.uploads.UploadModuleDataModel;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;

public class TaskProgressForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private VerticalLayout vLayout = new VerticalLayout();

	private NumberField txtPercent = new NumberField("Tiến độ %");
	private TextArea txtExplain = new TextArea("Diễn giải");
	private UploadModuleBasic uploadModule = new UploadModuleBasic();
	private ButtonTemplate btnUpdateProgress = new ButtonTemplate("Cập nhật tiến độ",FontAwesome.Solid.BARS_PROGRESS.create());

	private BelongOrganizationModel belongOrganizationModel = SessionUtil.getOrg();
	private UserAuthenticationModel userAuthenticationModel = SessionUtil.getUser();

	private TaskOutputModel outputTaskModel;
	private TaskProcessModel taskProcessModel = new TaskProcessModel();

	private List<String> listIdAttachment = new ArrayList<String>();
	private String taskId;
	public TaskProgressForm(String taskId) {
		this.taskId = taskId;
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(vLayout);
		createLayout();
		loadData();
	}

	@Override
	public void configComponent() {
		txtPercent.addValueChangeListener(e->{
			if(invalid() == false) {
				return;
			}
		});

		btnUpdateProgress.addClickListener(e->{
			listIdAttachment = new ArrayList<String>();
			uploadFile(uploadModule.getListFileUpload());
			doUpdateProgress(txtPercent.getValue(), txtExplain.getValue(), listIdAttachment);
			checkProcess(txtPercent.getValue());
		});
	}

	private void loadData() {
		ApiResultResponse<ApiOutputTaskModel> data = ApiTaskService.getAtask(taskId);
		if(data.isSuccess()) {
			outputTaskModel = new TaskOutputModel(data.getResult());
		}

		if(!outputTaskModel.getProcesses().isEmpty()) {
			taskProcessModel = outputTaskModel.getProcesses().get(0);
			txtPercent.setValue((double) taskProcessModel.getPercent());
			txtExplain.setValue(taskProcessModel.getExplain());
		}
	}

	private void checkProcess(Double process) {
		if(process.equals(100.0)) {
			confirmDialogCompletedTask();
		}

	}

	private void createLayout() {
		vLayout.removeAll();
		vLayout.setWidthFull();

		txtPercent.setWidthFull();
		txtPercent.setHelperText("Phần trăm tiến độ thực hiện được có giá trị từ 0% -> 100%");

		txtExplain.setWidthFull();
		txtExplain.setHeight("200px");

		uploadModule.initUpload();
		btnUpdateProgress.getStyle().setMarginLeft("auto");

		vLayout.add(txtPercent,txtExplain,uploadModule,btnUpdateProgress);
	}

	private boolean invalid() {
		if(txtPercent.getValue() > 100) {
			txtPercent.setErrorMessage("Phần trăm không được lớn hơn 100");
			txtPercent.focus();
			txtPercent.setInvalid(true);
			return false;
		} else if(txtPercent.getValue() <0) {
			txtPercent.setErrorMessage("Phần trăm không được bé hơn 0");
			txtPercent.focus();
			txtPercent.setInvalid(true);
			return false;
		}

		return true;
	}


	private boolean doUpdateProgress(double percent,String explain,List<String> listAttachments) {
		ApiUpdateProcessModel apiUpdateProcessModel = new ApiUpdateProcessModel();
		apiUpdateProcessModel.setPercent(percent);
		apiUpdateProcessModel.setExplain(explain);
		apiUpdateProcessModel.setCreator(getCreator());
		apiUpdateProcessModel.setAttachments(listAttachments);
		ApiResultResponse<Object> process = ApiActionService.doUpdateProcess(taskId, apiUpdateProcessModel);
		if(process.isSuccess()) {
			NotificationTemplate.success("Thành công");
			fireEvent(new ClickEvent(this,false));
			return true;
		}else {
			NotificationTemplate.error(process.getMessage());
			return false;
		}
	}

	private ApiCreatorActionModel getCreator() {
		ApiCreatorActionModel actionModel = new ApiCreatorActionModel();
		actionModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
		actionModel.setOrganizationName(belongOrganizationModel.getOrganizationName());
		actionModel.setOrganizationUserId(userAuthenticationModel.getId());
		actionModel.setOrganizationUserName(userAuthenticationModel.getFullName());
		return actionModel;
	}

	private void uploadFile(List<UploadModuleDataModel> listFile) {
		for(UploadModuleDataModel uploadModuleDataModel : listFile) {
			File file = new File(uploadModuleDataModel.getFileName());
			try {
				FileUtils.copyInputStreamToFile(uploadModuleDataModel.getInputStream(), file);
				ApiInputMediaModel apiInputMediaModel = new ApiInputMediaModel();
				apiInputMediaModel.setFile(new FileSystemResource(file));
				apiInputMediaModel.setDescription(uploadModuleDataModel.getDescription());
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
			if(data.isSuccess()) {
				listIdAttachment.add(data.getResult().getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public double getPercent() {
		return this.txtPercent.getValue();
	}

	public String getExplain() {
		return this.txtExplain.getValue();
	}

	public List<UploadModuleDataModel> getListUpload(){
		return this.uploadModule.getListFileUpload();
	}

	public TaskOutputModel getOutputTaskModel() {
		return this.outputTaskModel;
	}

	private void confirmDialogCompletedTask() {
		ConfirmDialog dialogTemplate = new ConfirmDialog("Hoàn thành nhiệm vụ", "Tiến độ đã đạt 100% hoàn thành, nhiệm vụ có thể báo cáo là đã hoàn thành.", "Báo cáo đã hoàn thành", e->{
			openConfirmReport();
		});
		dialogTemplate.setText("Tiến độ đã đạt 100% hoàn thành, nhiệm vụ có thể báo cáo là đã hoàn thành.");
		dialogTemplate.setConfirmText("Báo cáo đã hoàn thành");
		dialogTemplate.setCancelable(true);
		dialogTemplate.setCancelText("Không báo cáo, chỉ cập nhật tiến độ");
		dialogTemplate.open();
	}

	private List<UploadModuleDataModel> listDataFileUpload = new ArrayList<UploadModuleDataModel>();

	@SuppressWarnings("deprecation")
	private void openConfirmReport() {
		ConfirmDialogTemplate confirmDialogTemplate = new ConfirmDialogTemplate("BÁO CÁO HOÀN THÀNH NHIỆM VỤ");

		listIdAttachment = new ArrayList<String>();
		listDataFileUpload = new ArrayList<UploadModuleDataModel>();

		MultiSelectComboBox<UploadModuleDataModel> multiSelectCmbComment = new MultiSelectComboBox<UploadModuleDataModel>("Danh sách đính kèm");
		multiSelectCmbComment.setVisible(false);
		multiSelectCmbComment.setWidthFull();
		multiSelectCmbComment.setItemLabelGenerator(UploadModuleDataModel::getFileName);

		ButtonTemplate btnAttachment = new ButtonTemplate("Thêm đính kèm",FontAwesome.Solid.PAPERCLIP.create());
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
			});
		});

		VerticalLayout vLayoutReport = new VerticalLayout();
		vLayoutReport.setWidthFull();

		//		if(!outTaskOutputModel.getProcesses().isEmpty() && outTaskOutputModel.getProcesses().get(0).getPercent() == 100) {
		//			Span span = new Span("Xác nhận báo cáo nhiệm vụ đã hoàn thành");
		//			vLayoutReport.add(span);
		//			confirmDialogTemplate.addConfirmListener(e->doReport());
		//		}else {
		NumberField numberField = new NumberField("Cập nhật tiến độ hoàn thành");
		numberField.setWidthFull();
		numberField.setValue(100.0);
		numberField.setReadOnly(true);

		TextArea txtExplain = new TextArea("Diễn giải");
		txtExplain.setWidthFull();
		txtExplain.setPlaceholder("Đã hoàn thành");
		txtExplain.setHeight("200px");


		vLayoutReport.add(numberField,txtExplain);
		confirmDialogTemplate.addConfirmListener(e->{
			doReport();
		});
		//		}

		vLayoutReport.add(btnAttachment,multiSelectCmbComment);

		confirmDialogTemplate.add(vLayoutReport);


		confirmDialogTemplate.open();
	}


	private void openDialogUploadFile(Runnable run) {
		DialogTemplate dialogTemplate = new DialogTemplate("Thêm đính kèm");

		TaskDoAttachmentForm taskDoAttachmentForm = new TaskDoAttachmentForm();

		taskDoAttachmentForm.addChangeListener(e->{
			listDataFileUpload.addAll(taskDoAttachmentForm.getListFileUpload());
			listIdAttachment.addAll(taskDoAttachmentForm.getListAttachment());
			run.run();
			dialogTemplate.close();
		});


		dialogTemplate.add(taskDoAttachmentForm);

		dialogTemplate.setWidth("50%");
		dialogTemplate.setHeight("70%");
		dialogTemplate.getBtnSave().setText("Thêm");
		dialogTemplate.setLayoutMobile();
		dialogTemplate.getBtnSave().addClickListener(e->{
			taskDoAttachmentForm.uploadFile();
		});

		dialogTemplate.open();
	}

	private void doReport() {
		ApiReportModel apiReportModel = new ApiReportModel();
		apiReportModel.setCompletedTime(LocalDateUtil.localDateTimeToLong(LocalDateTime.now()));
		apiReportModel.setAttachments(listIdAttachment);
		apiReportModel.setCreator(getCreator());

		System.out.println("data report ne: "+apiReportModel);

		ApiResultResponse<Object> report = ApiActionService.doReport(taskId, apiReportModel);
		if(report.isSuccess()) {
			loadData();
			NotificationTemplate.success("Thành công");
			fireEvent(new ClickEvent(this,false));
		}
	}

}
