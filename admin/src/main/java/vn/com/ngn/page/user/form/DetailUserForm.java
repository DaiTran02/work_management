package vn.com.ngn.page.user.form;

import com.vaadin.flow.component.html.Span;

import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.user.form.details.InfoOfOrgForm;
import vn.com.ngn.page.user.form.details.InfoOfUserForm;
import vn.com.ngn.utils.components.TabsTemplate;

public class DetailUserForm extends TabsTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private String idUser;
	public DetailUserForm(String idUser) {
		this.idUser = idUser;
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		addTab(new Span("Thông tin người dùng"), new  InfoOfUserForm(idUser));
		addTab(new Span("Thông tin đơn vị"), new InfoOfOrgForm(idUser));
	}

	@Override
	public void configComponent() {
		
	}

}
