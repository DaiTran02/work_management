package com.ngn.utils.notifications;

import com.ngn.api.notification.ApiNotificationService;
import com.ngn.interfaces.FormInterface;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.tabs.TabSheet;

public class TabNotificationForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private final ApiNotificationService apiNotificationService;
	private TabSheet tabs = new TabSheet();
	private NotificationBellForm orgNotifiForm;
	private NotificationBellForm userNotifiForm;
	
	public TabNotificationForm(ApiNotificationService apiNotificationService) {
		this.apiNotificationService = apiNotificationService;
		buildLayout();
		configComponent();
	}
	
	
	@Override
	public void buildLayout() {
		orgNotifiForm = new NotificationBellForm(apiNotificationService,false);
		userNotifiForm = new NotificationBellForm(apiNotificationService,true);
		tabs.add("Đơn vị", orgNotifiForm);
		tabs.add("Cá nhân", userNotifiForm);
		
		tabs.addSelectedChangeListener(e->{
			if(tabs.getSelectedIndex() == 0) {
				orgNotifiForm.loadData();
			}
			if(tabs.getSelectedIndex() == 1) {
				userNotifiForm.loadData();
			}
		});
		
		
		tabs.setMinWidth("300px");
		tabs.setMaxWidth("500px");
		tabs.setWidth("600px");
		
		tabs.setMaxHeight("600px");
		
		this.add(tabs);
	}

	@Override
	public void configComponent() {
		
	}
	
	public void updateData() {
		if(orgNotifiForm != null && userNotifiForm != null) {
			orgNotifiForm.loadData();
			userNotifiForm.loadData();
		}
	}

}
