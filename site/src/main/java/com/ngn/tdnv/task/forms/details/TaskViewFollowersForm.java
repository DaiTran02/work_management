package com.ngn.tdnv.task.forms.details;

import java.util.List;

import com.ngn.interfaces.FormInterface;
import com.ngn.tdnv.task.models.FollowerModel;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;

public class TaskViewFollowersForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private Grid<FollowerModel> grid = new Grid<FollowerModel>(FollowerModel.class,false);

	private List<FollowerModel> followers;
	public TaskViewFollowersForm(List<FollowerModel> followers) {
		this.followers = followers;
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
	
	private void loadData() {
		if(!(followers == null)) {
			grid.setItems(followers);
		}
	}
	
	private Component setUpGrid() {
		grid = new Grid<FollowerModel>(FollowerModel.class,false);
		
		grid.addColumn(FollowerModel::getOrganizationName).setHeader("Tên đơn vị theo dõi");
		
		grid.addColumn(model->{
			if(model.getOrganizationUserId() == null) {
				return "Đang cập nhật";
			}else {
				return model.getOrganizationUserName();
			}
		}).setHeader("Tên người dùng được phân theo dõi");
		
		
		return grid;
	}

}
