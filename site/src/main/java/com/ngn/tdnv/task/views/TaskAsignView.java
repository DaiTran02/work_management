package com.ngn.tdnv.task.views;

import com.ngn.tdnv.task.forms.TaskAssignForm;
import com.ngn.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

@Route(value = "asign",layout = MainLayout.class)
@PermitAll
@PageTitle("Giao nhiệm vụ")
public class TaskAsignView extends VerticalLayout{
	private static final long serialVersionUID = 1L;

	
	public TaskAsignView() {
		this.setSizeFull();
		this.add(new TaskAssignForm());
	}
	
}
