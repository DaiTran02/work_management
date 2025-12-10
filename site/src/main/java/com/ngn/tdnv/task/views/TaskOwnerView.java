package com.ngn.tdnv.task.views;

import java.util.List;
import java.util.Map;

import com.ngn.tdnv.task.forms.ListTasksOwnerForm;
import com.ngn.utils.SessionUtil;
import com.ngn.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

@Route(value = "task_ower",layout = MainLayout.class)
@PageTitle("Nhiệm vụ đã giao")
@PermitAll
public class TaskOwnerView extends VerticalLayout implements HasUrlParameter<String>{
	private static final long serialVersionUID = 1L;

	public TaskOwnerView() {
		this.setSizeFull();

	}

	@Override
	public void setParameter(BeforeEvent event,@OptionalParameter String parameter) {
		Location location = event.getLocation();
		QueryParameters queryParameters = location.getQueryParameters();
		Map<String, List<String>> parameters = queryParameters.getParameters();
		ListTasksOwnerForm listTasksOwnerForm = new ListTasksOwnerForm(SessionUtil.getOrg(),SessionUtil.getUser(),parameters);
		this.removeAll();
		this.add(listTasksOwnerForm);
	}

}
