package com.ngn.tdnv.dashboard.forms;

import com.ngn.interfaces.FormInterface;
import com.ngn.utils.CountMenuUtil;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.tabs.TabSheet;

public class DashboardForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private TabSheet tabSheet = new TabSheet();
	private CountMenuUtil countMenuUtil = new CountMenuUtil(SessionUtil.getOrg(), SessionUtil.getDetailOrg());

	private DashboardOwnerForm dashboardOwnerForm = new DashboardOwnerForm();
	private DashboardAssigneeForm dashboardAssegneeForm = new DashboardAssigneeForm();
	private DashboardSupportForm dashboardSupportForm = new DashboardSupportForm();
	private DashboardFollowerForm dashboardFollowerForm = new DashboardFollowerForm();


	public DashboardForm() {
		buildLayout();
		configComponent();

	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(tabSheet);
		createLayout();
	}

	@Override
	public void configComponent() {

	}

	private void createLayout() {

		tabSheet.setSizeFull();

		tabSheet.addSelectedChangeListener(e->{
			if(tabSheet.getSelectedIndex() == 0) {
				dashboardOwnerForm.createLayout();
			}
			
			if(tabSheet.getSelectedIndex() == 1) {
				dashboardAssegneeForm.createLayout();
			}
			if(tabSheet.getSelectedIndex() == 2) {
				dashboardSupportForm.createLayout();
			}
			if(tabSheet.getSelectedIndex() == 3) {
				dashboardFollowerForm.createLayout();
			}
		});

		tabSheet.add("Đã giao ("+countMenuUtil.countTaskOwner()+")", dashboardOwnerForm);
		tabSheet.add("Được giao ("+countMenuUtil.countTaskAssignee()+")", dashboardAssegneeForm);
		tabSheet.add("Phối hợp ("+countMenuUtil.countTaskSupport()+")", dashboardSupportForm);
		tabSheet.add("Theo dõi ("+countMenuUtil.countTaskFollow()+")", dashboardFollowerForm);
	}




}
