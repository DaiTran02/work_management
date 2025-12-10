package com.ngn.setting.leader_classify.form;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ngn.api.leader_approve_task.ApiLeaderApproveTaskModel;
import com.ngn.api.leader_approve_task.ApiLeaderApproveTaskModel.Creator;
import com.ngn.api.leader_approve_task.ApiLeaderApproveTaskService;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.utils.components.NotificationTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class EditLeaderForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private Logger logger = LogManager.getFormatterLogger(this);

	private TextField txtName = new TextField("Tên");
	private Checkbox cbActive = new Checkbox("Hoạt động");
	
	private BelongOrganizationModel belongOrganizationModel;
	private UserAuthenticationModel userAuthenticationModel;
	private String idLeader;
	private int order;
	public EditLeaderForm(String idLeader,int order,BelongOrganizationModel belongOrganizationModel,UserAuthenticationModel userAuthenticationModel) {
		this.order = order;
		this.belongOrganizationModel = belongOrganizationModel;
		this.userAuthenticationModel = userAuthenticationModel;
		buildLayout();
		configComponent();
		if(idLeader != null) {
			this.idLeader = idLeader;
			loadData();
		}
	}
	
	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(createLayout());
	}

	@Override
	public void configComponent() {
		
	}

	public void loadData() {
		ApiResultResponse<ApiLeaderApproveTaskModel> data = ApiLeaderApproveTaskService.getALeader(idLeader);
		if(data.isSuccess()) {
			txtName.setValue(data.getResult().getName());
			cbActive.setValue(data.getResult().isActive());
			order = data.getResult().getOrder();
		}else {
			NotificationTemplate.error(data.getMessage());
			logger.error(data.getMessage());
		}
		
	}
	
	private Component createLayout() {
		HorizontalLayout layout = new HorizontalLayout();
		
		txtName.setWidthFull();
		cbActive.setWidth("150px");
		cbActive.getStyle().setMarginTop("30px");
		cbActive.setValue(true);
		
		layout.expand(txtName);
		layout.add(txtName,cbActive);
		layout.setWidthFull();
		
		return layout;
	}
	
	public void saveLeader() {
		ApiLeaderApproveTaskModel apiLeaderApproveTaskModel = new ApiLeaderApproveTaskModel();
		apiLeaderApproveTaskModel.setName(txtName.getValue());
		apiLeaderApproveTaskModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
		apiLeaderApproveTaskModel.setOrganizationName(belongOrganizationModel.getOrganizationName());
		apiLeaderApproveTaskModel.setActive(cbActive.getValue());
		Creator creator = apiLeaderApproveTaskModel.new Creator();
		creator.setOrganizationId(belongOrganizationModel.getOrganizationId());
		creator.setOrganizationName(belongOrganizationModel.getOrganizationName());
		creator.setOrganizationUserId(userAuthenticationModel.getId());
		creator.setOrganizationUserName(userAuthenticationModel.getUsername());
		apiLeaderApproveTaskModel.setCreator(creator);
		
		if(idLeader != null) {
			apiLeaderApproveTaskModel.setOrder(order);
			doUpdateLeader(apiLeaderApproveTaskModel);
		}else {
			apiLeaderApproveTaskModel.setOrder(order+1);
			doCreateLeader(apiLeaderApproveTaskModel);
		}
	}
	
	private void doCreateLeader(ApiLeaderApproveTaskModel apiLeaderApproveTaskModel) {
		ApiResultResponse<Object> data = ApiLeaderApproveTaskService.createLeader(apiLeaderApproveTaskModel);
		if(data.isSuccess()) {
			NotificationTemplate.success(data.getMessage());
			fireEvent(new ClickEvent(this,false));
		}else {
			NotificationTemplate.error(data.getMessage());
		}
	}
	
	private void doUpdateLeader(ApiLeaderApproveTaskModel apiLeaderApproveTaskModel) {
		ApiResultResponse<Object> data = ApiLeaderApproveTaskService.updateLeader(idLeader, apiLeaderApproveTaskModel);
		if(data.isSuccess()) {
			NotificationTemplate.success(data.getMessage());
			fireEvent(new ClickEvent(this,false));
		}else {
			NotificationTemplate.error(data.getMessage());
		}
	}
	
}



















