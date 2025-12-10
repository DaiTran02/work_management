package com.ngn.tdnv.task.forms.details;

import java.util.List;

import com.ngn.interfaces.FormInterface;
import com.ngn.tdnv.task.models.TaskOrgGeneralModel;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;

public class TaskViewOrgSupportsForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private Grid<TaskOrgGeneralModel> grid = new Grid<TaskOrgGeneralModel>(TaskOrgGeneralModel.class,false);

	private List<TaskOrgGeneralModel> supports;
	public TaskViewOrgSupportsForm(List<TaskOrgGeneralModel> supports) {
		this.supports = supports;
		buildLayout();
		configComponent();
		loadData();
	}
	
	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(setUpGrid());
	}

	@Override
	public void configComponent() {
		
	}
	
	public void loadData() {
		if(supports.isEmpty()) {
		}else {
			grid.setItems(supports);
		}
	}
	
	private Component setUpGrid() {
		grid = new Grid<TaskOrgGeneralModel>(TaskOrgGeneralModel.class,false);
		grid.addColumn(TaskOrgGeneralModel::getOrganizationName).setHeader("Tên đơn vị hỗ trợ");
		grid.addColumn(model->{
			if(model.getOrganizationUserId() == null) {
				return "Đang cập nhật";
			}else {
				return model.getOrganizationUserName();
			}
		}).setHeader("Tên người dùng được phân hỗ trợ");
		return grid;
	}

}
