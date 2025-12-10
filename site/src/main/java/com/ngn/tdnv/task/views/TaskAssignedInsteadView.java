package com.ngn.tdnv.task.views;

import com.ngn.utils.components.VerticalLayoutTemplate;
import com.ngn.views.MainLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

@Route(value = "instead_task", layout = MainLayout.class)
@PageTitle("Nhiệm vụ đã giao thay")
@PermitAll
public class TaskAssignedInsteadView extends VerticalLayoutTemplate{
	private static final long serialVersionUID = 1L;
	
	public TaskAssignedInsteadView() {
		
	}

}
