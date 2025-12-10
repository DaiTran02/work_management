package com.ngn.tdnv.task.views;

import java.util.List;
import java.util.Map;

import com.ngn.tdnv.task.forms.ListTasksFollowerForm;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.ngn.views.MainLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

@Route(value = "task_follower", layout = MainLayout.class)
@PageTitle("Theo dõi nhiệm vụ")
@PermitAll
public class TaskFollowerView extends VerticalLayoutTemplate implements HasUrlParameter<String>{
	private static final long serialVersionUID = 1L;
	
	public TaskFollowerView() {
		this.setSizeFull();
		
	}

	@Override
	public void setParameter(BeforeEvent event,@OptionalParameter String parameter) {
		Location location = event.getLocation();
		QueryParameters queryParameters = location.getQueryParameters();
		Map<String, List<String>> parameters = queryParameters.getParameters();
		ListTasksFollowerForm listTasksFollowerForm = new ListTasksFollowerForm(SessionUtil.getOrg(), SessionUtil.getDetailOrg(),parameters);
		this.removeAll();
		this.add(listTasksFollowerForm);
	}

}
