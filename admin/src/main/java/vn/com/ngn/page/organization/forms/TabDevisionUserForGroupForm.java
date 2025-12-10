package vn.com.ngn.page.organization.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;

import vn.com.ngn.interfaces.FormInterface;

public class TabDevisionUserForGroupForm extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;

	private TabSheet tabSheet;

	private String parentId;
	private String groupId;
	/**
	 * Phân người dùng trong tổ giao việc
	 * @param parentId
	 * @param groupId
	 */
	public TabDevisionUserForGroupForm(String parentId,String groupId) {
		this.parentId = parentId;
		this.groupId = groupId;
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
		tabSheet = new TabSheet();

		UserExcludeGroupForm userExcludeGroupForm = new UserExcludeGroupForm(parentId,groupId);
		UserIncludeGroupForm userIncludeGroupForm = new UserIncludeGroupForm(parentId, groupId);

		tabSheet.add("Người dùng trong tổ",userIncludeGroupForm);
		tabSheet.add("Người dùng không có trong tổ",userExcludeGroupForm);

		tabSheet.addSelectedChangeListener(e->{
			userExcludeGroupForm.loadData();
			userIncludeGroupForm.loadData();
		});

		
		tabSheet.setSizeFull();

		return tabSheet;
	}

}
