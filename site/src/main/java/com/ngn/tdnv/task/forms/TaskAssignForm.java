package com.ngn.tdnv.task.forms;

import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.models.sign_in_org.SignInOrgModel;
import com.ngn.tdnv.doc.forms.create_task.TaskCreateFormV2;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.VerticalLayoutTemplate;

public class TaskAssignForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private BelongOrganizationModel belongOrganizationModel = SessionUtil.getOrg();
	private UserAuthenticationModel userAuthenticationModel = SessionUtil.getUser();
	private SignInOrgModel signInOrgModel = SessionUtil.getDetailOrg();
//	private TaskCreateForm createTaskForm;
	private TaskCreateFormV2 createTaskForm;
	
	
	public TaskAssignForm() {
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
//		createTaskForm = new TaskCreateForm(null, belongOrganizationModel, userAuthenticationModel, signInOrgModel, null);
		createTaskForm = new TaskCreateFormV2(null, belongOrganizationModel, userAuthenticationModel, signInOrgModel, null);
		createTaskForm.setTaskAssign(true);
		createTaskForm.getDetailFormTask().getStyle().setBackground("white").setBoxShadow("rgba(0, 0, 0, 0.16) 0px 1px 4px");
		this.setSizeFull();
		this.add(createTaskForm);
		
	}

	@Override
	public void configComponent() {
		
	}

}
