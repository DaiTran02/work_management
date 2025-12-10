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
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.NotificationTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.ngn.utils.uploads.UploadModuleBasic;
import com.ngn.utils.uploads.UploadModuleDataModel;

public class TaskDoAttachmentForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private UploadModuleBasic uploadModule = new UploadModuleBasic();
	
	private BelongOrganizationModel belongOrganizationModel = SessionUtil.getOrg();
	private UserAuthenticationModel userAuthenticationModel = SessionUtil.getUser();
	
	private List<String> listIdAttachment = new ArrayList<String>();
	
	public TaskDoAttachmentForm() {
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		uploadModule.initUpload();
		this.add(uploadModule);
		
	}

	@Override
	public void configComponent() {
		
	}
	
	public void uploadFile() {
		for(UploadModuleDataModel uploadModuleDataModel : uploadModule.getListFileUpload()) {
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
	
	
	int count = 0;
	
	private void doCreateFile(ApiInputMediaModel apiModel) {
		try {
			ApiResultResponse<ApiMediaModel> data = ApiMediaService.createFile(apiModel);
			if(data.isSuccess()) {
				listIdAttachment.add(data.getResult().getId());
				count++;
				if(count == uploadModule.getListFileUpload().size()) {
					NotificationTemplate.success("Thêm đính kèm thành công");
					fireEvent(new ClickEvent(this,false));
				}
			}else {
				NotificationTemplate.error(data.getMessage());
			}
		} catch (Exception e) {
			NotificationTemplate.warning("Tệp đính kèm rỗng hoặc đã bị hỏng");
			e.printStackTrace();
		}
	}
	
	public List<UploadModuleDataModel> getListFileUpload(){
		return uploadModule.getListFileUpload();
	}
	
	public List<String> getListAttachment(){
		return listIdAttachment;
	}

}
