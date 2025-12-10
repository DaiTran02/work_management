package com.ngn.setting.leader_classify.form;

import com.ngn.interfaces.FormInterface;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.tabs.TabSheet;

public class ControlLeaderAndClassifyForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private TabSheet tabSheet;
	public ControlLeaderAndClassifyForm() {
		buildLayout();
		configComponent();
		checkLayoutMobile();
	}
	
	@Override
	public void buildLayout() {
		this.add(createLayout());
		
	}

	@Override
	public void configComponent() {
		
	}
	
	private void checkLayoutMobile() {
		try {
			UI.getCurrent().getPage().retrieveExtendedClientDetails(e->{
				if(e.getScreenWidth() < 768) {
					this.setPadding(false);
				}
			});
		} catch (Exception e) {
		}
	}

	private Component createLayout() {
		tabSheet = new TabSheet();
		tabSheet.setSizeFull();
		
		tabSheet.add("Phân loại chỉ đạo", new ClassifyTaskForm(SessionUtil.getOrg(), SessionUtil.getUser()));
		tabSheet.add("Người duyệt", new LeaderApproveTaskForm(SessionUtil.getOrg(), SessionUtil.getUser()));
		
		
		return tabSheet;
	}
	
}
