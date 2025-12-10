package vn.com.ngn.page.user;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;
import vn.com.ngn.page.user.form.UserForm;
import vn.com.ngn.views.MainLayout;

@PageTitle(value = "Quản lý người dùng")
@Route(value = "user",layout = MainLayout.class)
@PermitAll
public class UserView extends VerticalLayout{
	private static final long serialVersionUID = 1L;
	
	public UserView() {
		this.setSizeFull();
		this.add(new UserForm());
	}

}
