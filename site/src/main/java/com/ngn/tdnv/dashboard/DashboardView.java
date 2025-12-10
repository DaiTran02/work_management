package com.ngn.tdnv.dashboard;

import com.ngn.tdnv.dashboard.forms.DashboardForm;
import com.ngn.views.MainLayout;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import jakarta.annotation.security.PermitAll;

@PermitAll
@PageTitle(value = "Thống kê")
@Route(value = "dashboard", layout = MainLayout.class)
@RouteAlias( value = "",layout = MainLayout.class)
@JavaScript("./themes/site/js/code/highcharts.js")
@JavaScript("./themes/site/js/code/modules/exporting.js")
@JavaScript("./themes/site/js/code/modules/accessibility.js")
@JavaScript("./themes/site/js/code/modules/variable-pie.js")
@JavaScript("./themes/site/js/code/modules/export-data.js")
public class DashboardView extends VerticalLayout{
	private static final long serialVersionUID = 1L;

	public DashboardView() {
		try {
			this.add(new DashboardForm());
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}
