package vn.com.ngn.page.home;

import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import jakarta.annotation.security.PermitAll;
import vn.com.ngn.api.dashboard.ApiDashBoardService;
import vn.com.ngn.page.home.form.DashboardForm;
import vn.com.ngn.utils.components.VerticalLayoutTemplate;
import vn.com.ngn.views.MainLayout;


@PermitAll
@PageTitle("Tổng quan")
@Route(value = "home",layout = MainLayout.class)
@RouteAlias(value = "/",layout = MainLayout.class)
//@JavaScript("./code/modules/exporting.js")
//@JavaScript("./code/modules/accessibility.js")
//@JavaScript("./code/modules/treemap.js")
//@JavaScript("./code/modules/treegraph.js")
//@JavaScript("./code/highcharts.js")
@JavaScript("https://code.highcharts.com/highcharts.js")
@JavaScript("https://code.highcharts.com/modules/exporting.js")
@JavaScript("https://code.highcharts.com/modules/accessibility.js")
@JavaScript("https://code.highcharts.com/modules/treemap.js")
@JavaScript("https://code.highcharts.com/modules/treegraph.js")
public class HomeView extends VerticalLayoutTemplate{
	private static final long serialVersionUID = 1L;
	
	public HomeView(ApiDashBoardService apiDashBoardService) {
		this.setSizeFull();
		this.add(new DashboardForm(apiDashBoardService));
	}

}
