package com.ngn.tdnv.task.forms.details;

import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tasks.ApiOutputTaskModel;
import com.ngn.api.tasks.ApiTaskService;
import com.ngn.interfaces.FormInterface;
import com.ngn.tdnv.task.forms.components.ItemEventOfHistoryForm;
import com.ngn.tdnv.task.models.TaskOutputModel;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class TaskHistoryEventFormV2 extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private TaskOutputModel outputTaskModel;
	private VerticalLayout vLayout = new VerticalLayout();
	
	private String idTask;
	public TaskHistoryEventFormV2(String idTask) {
		this.idTask = idTask;
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(vLayout);
		
	}

	@Override
	public void configComponent() {
		
	}
	
	private void loadData() {
		ApiResultResponse<ApiOutputTaskModel> getDataTask = ApiTaskService.getAtask(idTask);
		outputTaskModel = new TaskOutputModel(getDataTask.getResult());
		createLayout();
	}
	
	private void createLayout() {
		vLayout.removeAll();
		vLayout.setSpacing(false);
		
		outputTaskModel.getEvents().forEach(model->{
			ItemEventOfHistoryForm itemEventOfHistoryForm = new ItemEventOfHistoryForm(model,outputTaskModel);
			vLayout.add(itemEventOfHistoryForm);
		});
	}

}
