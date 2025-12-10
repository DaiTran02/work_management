package vn.com.ngn.page.organization.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;

import vn.com.ngn.interfaces.FormInterface;

public class TabDevisionUserForm extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;
	TabSheet tabSheet;
	private String parentId;
	public TabDevisionUserForm(String parentId) {
		this.removeAll();
		this.parentId = parentId;
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(createLayoutTab());

	}

	@Override
	public void configComponent() {

	}


	private Component createLayoutTab() {
		tabSheet = new TabSheet();
		
		
		UserIncludeOrgForm userIncludeOrgForm = new UserIncludeOrgForm(parentId);
		UserExcludeOrgForm userExcludeOrgForm = new UserExcludeOrgForm(parentId);

		tabSheet.add("Người dùng trong đơn vị",userIncludeOrgForm);
		tabSheet.add("Người dùng không thuộc đơn vị",userExcludeOrgForm);

		tabSheet.addSelectedChangeListener(e->{
			userIncludeOrgForm.loadData();
			userExcludeOrgForm.loadData();
		});
		
		tabSheet.setSizeFull();
		return tabSheet;
	}
	

	


}
