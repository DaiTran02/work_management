package vn.com.ngn.page.organization.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;

import vn.com.ngn.interfaces.FormInterface;

public class TabDevisionUserForRoleForm extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	
	private String parentId,roleId;
	/**
	 * Phân người dùng trong vai trò
	 * @param parentId
	 * @param roleId
	 */
	public TabDevisionUserForRoleForm(String parentId,String roleId) {
		this.parentId = parentId;
		this.roleId = roleId;
		buildLayout();
		configComponent();
		
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(createTabs());
	}

	@Override
	public void configComponent() {
		
	}

	
	public Component createTabs() {
		TabSheet tabSheet = new TabSheet();

		UserInRoleForm userInRoleForm = new UserInRoleForm(parentId, roleId);
		UserExcludeRoleForm userExcludeRoleForm = new UserExcludeRoleForm(parentId, roleId);
		
		tabSheet.add("Người dùng trong vai trò",userInRoleForm);
		tabSheet.add("Người dùng không có trong vai trò",userExcludeRoleForm);

		tabSheet.addSelectedChangeListener(e->{
			userInRoleForm.loadData();
			userExcludeRoleForm.loadData();
		});

		
		tabSheet.setSizeFull();

		return tabSheet;
	}
}
