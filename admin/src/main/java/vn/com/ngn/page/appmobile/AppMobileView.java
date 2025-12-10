package vn.com.ngn.page.appmobile;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;
import vn.com.ngn.page.appmobile.forms.AppMobileForm;
import vn.com.ngn.views.MainLayout;

@Route(value = "mobile",layout = MainLayout.class)
@PageTitle(value = "Quản lý ứng dụng di động")
@PermitAll
public class AppMobileView extends VerticalLayout{
	private static final long serialVersionUID = 1L;
	
	public AppMobileView() {
		this.add(new AppMobileForm());
	}

}
