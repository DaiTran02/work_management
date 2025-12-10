package com.ngn.tdnv.task.forms.details;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;

import com.ngn.api.media.ApiInputMediaModel;
import com.ngn.api.media.ApiMediaModel;
import com.ngn.api.media.ApiMediaService;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tasks.ApiCreatorModel;
import com.ngn.api.tasks.ApiOutputTaskModel;
import com.ngn.api.tasks.ApiTaskRefuseConfirmModel;
import com.ngn.api.tasks.ApiTaskService;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.tdnv.task.models.TaskOutputModel;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.ActtachmentForm;
import com.ngn.utils.components.NotificationTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.ngn.utils.uploads.UploadModuleBasic;
import com.ngn.utils.uploads.UploadModuleDataModel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;

public class TaskRefuseConfirmForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private BelongOrganizationModel belongOrganizationModel = SessionUtil.getOrg();
	private UserAuthenticationModel userAuthenticationModel = SessionUtil.getUser();
	private VerticalLayout vLayout = new VerticalLayout();
	private TextArea txtReasonConfirmRefuse = new TextArea("Lý do từ chối");
	private UploadModuleBasic upload=new UploadModuleBasic();
	private TaskOutputModel taskOutputModel = new TaskOutputModel();
	private List<String> listIdAttachments = new ArrayList<String>();
	private String idTask;
	public TaskRefuseConfirmForm(String idTask) {
		this.idTask = idTask;
		buildLayout();
		configComponent();
		loadData();
	}
	
	@Override
	public void buildLayout() {
		this.setWidthFull();
		this.add(vLayout);
		vLayout.setWidthFull();
	}

	@Override
	public void configComponent() {
		
	}
	
	private void loadData() {
		ApiResultResponse<ApiOutputTaskModel> dataTask = ApiTaskService.getAtask(idTask);
		if(dataTask.isSuccess()) {
			taskOutputModel = new TaskOutputModel(dataTask.getResult());
			createLayout();
		}
	}
	
	private void createLayout() {
		vLayout.removeAll();
		H3 header = new H3("Thông tin báo cáo");
		header.getStyle().setMargin("auto");
		
		String headerWidth = "100px";
		
		vLayout.add(header);
		
		VerticalLayout vLayoutLeft = new VerticalLayout();
		VerticalLayout vLayoutRight = new VerticalLayout();
		
		vLayoutLeft.add(createLayoutKeyAndValue("Thời gian: ", taskOutputModel.getReported().getCompletedTimeText(), headerWidth, null),
				createLayoutKeyAndValue("Diễn giải: ", taskOutputModel.getReported().getReportedStatus(), headerWidth, null)
				);
		vLayoutLeft.setWidth("50%");
		
		vLayoutRight.add(initDocActtachment(taskOutputModel.getReported().getAttachments()));
		vLayoutRight.setWidth("50%");
		
		SplitLayout splitLayout = new SplitLayout(vLayoutLeft,vLayoutRight);
		splitLayout.setWidthFull();
		splitLayout.setHeight("300px");
		vLayout.add(splitLayout);
		
		txtReasonConfirmRefuse.setWidthFull();
		txtReasonConfirmRefuse.setPlaceholder("Nội dung báo cáo chưa rõ ràng");
		txtReasonConfirmRefuse.setHeight("200px");
		upload.setWidthFull();
		upload.initUpload();
		
		VerticalLayout vLayoutReason = new VerticalLayout();
		vLayoutReason.setWidthFull();
		H3 headerReason = new H3("Thông từ chối");
		headerReason.getStyle().setMargin("auto");
		vLayoutReason.add(headerReason,txtReasonConfirmRefuse,upload);
		vLayoutReason.getStyle().set("border-top", "1px solid #a9a9a9").setPadding("10px");
		
		vLayout.add(vLayoutReason);
		
	}
	
	private Component initDocActtachment(List<String> listAttachments) {
		ActtachmentForm acttachmentForm = new ActtachmentForm(listAttachments,true);
		acttachmentForm.setSpanTitle("Danh sách file báo cáo từ đơn vị được giao");
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
	
	public void refuseConfirm() {
		ApiTaskRefuseConfirmModel apiTaskRefuseConfirmModel = new ApiTaskRefuseConfirmModel();
		if(txtReasonConfirmRefuse.getValue().isEmpty()) {
			txtReasonConfirmRefuse.setValue("Nội dung báo cáo chưa rõ ràng");
		}
		apiTaskRefuseConfirmModel.setReasonConfirmRefuse(txtReasonConfirmRefuse.getValue());
		apiTaskRefuseConfirmModel.setCreator(getCreateTor());
		if(!upload.getListFileUpload().isEmpty()) {
			uploadFile();
		}
		
		doRefuse(apiTaskRefuseConfirmModel);
		
	}
	
	private void doRefuse(ApiTaskRefuseConfirmModel apiTaskRefuseConfirmModel) {
		ApiResultResponse<Object> refuseConfirm = ApiTaskService.doRefuseConfirmTask(idTask, apiTaskRefuseConfirmModel);
		if(refuseConfirm.isSuccess()) {
			NotificationTemplate.success("Thành công");
			fireEvent(new ClickEvent(this, false));
		}else {
			NotificationTemplate.error("Có lỗi xảy ra");
		}
	}
	
	private void doCreateFile(ApiInputMediaModel apiModel) {
		try {
			ApiResultResponse<ApiMediaModel> data = ApiMediaService.createFile(apiModel);
			if(data.getStatus() == 200 || data.getStatus() == 201) {
				System.out.println("How much do I have id list: "+data.getResult() );
				listIdAttachments.add(data.getResult().getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void uploadFile() {
		List<UploadModuleDataModel> listFile = upload.getListFileUpload();
		for(UploadModuleDataModel uploadModuleDataModel : listFile) {
			File file = new File(uploadModuleDataModel.getFileName());
			System.out.println("How much do I have files: "+uploadModuleDataModel.getFileName());
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
	
	private ApiCreatorModel getCreateTor() {
		ApiCreatorModel apiCreatorModel = new ApiCreatorModel();
		
		apiCreatorModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
		apiCreatorModel.setOrganizationName(belongOrganizationModel.getOrganizationName());
		apiCreatorModel.setOrganizationUserId(userAuthenticationModel.getId());
		apiCreatorModel.setOrganizationUserName(userAuthenticationModel.getFullName());
		
		return apiCreatorModel;
	}

}
