package com.ngn.setting.leader_classify.form;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ngn.api.classify_task.ApiClassifyTaskModel;
import com.ngn.api.classify_task.ApiClassifyTaskModel.Creator;
import com.ngn.api.classify_task.ApiClassifyTaskService;
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

public class EditClassifyForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private Logger logger = LogManager.getFormatterLogger(this);

	private TextField txtName = new TextField("Tên");
	private Checkbox cbActive = new Checkbox("Hoạt động");
	
	private BelongOrganizationModel belongOrganizationModel;
	private UserAuthenticationModel userAuthenticationModel;
	private String idClassify;
	private int order;
	public EditClassifyForm(String idClassify,BelongOrganizationModel belongOrganizationModel,UserAuthenticationModel userAuthenticationModel,int order) {
		this.belongOrganizationModel = belongOrganizationModel;
		this.userAuthenticationModel = userAuthenticationModel;
		this.order = order;
		buildLayout();
		configComponent();
		if(idClassify!=null) {
			this.idClassify = idClassify;
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
		try {
			ApiResultResponse<ApiClassifyTaskModel> data = ApiClassifyTaskService.getAClassify(idClassify);
			txtName.setValue(data.getResult().getName());
			cbActive.setValue(data.getResult().isActive());
			order = data.getResult().getOrder();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
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
	
	public void saveClassify() {
		ApiClassifyTaskModel apiClassifyTaskModel = new ApiClassifyTaskModel();
		apiClassifyTaskModel.setName(txtName.getValue());
		apiClassifyTaskModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
		apiClassifyTaskModel.setOrganizationName(belongOrganizationModel.getOrganizationName());
		apiClassifyTaskModel.setActive(cbActive.getValue());
		Creator creator =  apiClassifyTaskModel.new Creator();
		creator.setOrganizationId(belongOrganizationModel.getOrganizationId());
		creator.setOrganizationName(belongOrganizationModel.getOrganizationName());
		creator.setOrganizationUserId(userAuthenticationModel.getId());
		creator.setOrganizationUserName(userAuthenticationModel.getUsername());
		apiClassifyTaskModel.setCreator(creator);
		
		if(idClassify!=null) {
			apiClassifyTaskModel.setOrder(order+1);
			doUpdate(apiClassifyTaskModel);
		}else {
			apiClassifyTaskModel.setOrder(order);
			doCreate(apiClassifyTaskModel);
		}
		
	}
	
	private void doCreate(ApiClassifyTaskModel apiClassifyTaskModel) {
		ApiResultResponse<Object> data = ApiClassifyTaskService.createClassify(apiClassifyTaskModel);
		if(data.isSuccess()) {
			NotificationTemplate.success(data.getMessage());
			fireEvent(new ClickEvent(this,false));
		}else {
			NotificationTemplate.error(data.getMessage());
		}
	}
	
	private void doUpdate(ApiClassifyTaskModel apiClassifyTaskModel) {
		ApiResultResponse<Object> data = ApiClassifyTaskService.updateClassify(idClassify, apiClassifyTaskModel);
		if(data.isSuccess()) {
			NotificationTemplate.success(data.getMessage());
			fireEvent(new ClickEvent(this,false));
		}else {
			NotificationTemplate.error(data.getMessage());
		}
	}

}























