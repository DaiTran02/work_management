package com.ngn.utils.notifications;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.notification.ApiNotificationModel;
import com.ngn.api.notification.ApiNotificationService;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.interfaces.FormInterface;
import com.ngn.tdnv.task.forms.details.TaskViewDetailFormV2;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.DialogTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style.Display;
import com.vaadin.flow.dom.Style.FlexDirection;

public class NotificationDetailForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private final ApiNotificationService apiNotificationService;
	private ApiNotificationModel apiNotificationModel;
	private ButtonTemplate btnViewTask = new ButtonTemplate("Xem thông tin nhiệm vụ",FontAwesome.Solid.EYE.create());
	private boolean isLayoutMobile = false;
	
	private VerticalLayout vLayout = new VerticalLayout();
	
	private String idNotifi;
	public NotificationDetailForm(ApiNotificationService apiNotificationService,String idNotifi) {
		this.apiNotificationService = apiNotificationService;
		this.idNotifi = idNotifi;
		checkMobileLayout();
		loadData();
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(vLayout);
		vLayout.setWidthFull();
	}

	@Override
	public void configComponent() {
		btnViewTask.addClickListener(e->{
			openDialogViewDetailV2(apiNotificationModel.getObjectId());
		});
	}
	
	private void loadData() {
		ApiResultResponse<ApiNotificationModel> data = apiNotificationService.getInfoNotifi(idNotifi);
		apiNotificationModel = data.getResult();
		if(data.isSuccess()) {
			createLayout(data.getResult());
			ApiResultResponse<Object> doView = apiNotificationService.setMarkViewed(idNotifi);
			if(doView.isSuccess()) {
				fireEvent(new ClickEvent(this, false));
				System.out.println("View success");
			}
		}
	}
	
	private void checkMobileLayout() {
		UI.getCurrent().getPage().retrieveExtendedClientDetails(e->{
			if(e.getScreenWidth() < 768) {
				isLayoutMobile = true;
			}
		});
	}
	
	private void createLayout(ApiNotificationModel notificationModel) {
		vLayout.removeAll();
		String widthHeader = "200px";
		vLayout.add(createLayoutKeyAndValue("Ngày thông báo: ", notificationModel.getCreatedTimeText(), widthHeader, idNotifi),
				createLayoutKeyAndValue("Tiêu đề: ", notificationModel.getTitle(), widthHeader, widthHeader),
				createLayoutKeyAndValue("Nội dung: ", notificationModel.getContent(), widthHeader, widthHeader),
				createLayoutKeyAndValue("Loại thông báo: ", notificationModel.getObject().getName(), widthHeader, widthHeader),
				createLayoutKeyAndValue("Nơi nhận thông báo: ", notificationModel.getReceiver().getOrganizationName(), widthHeader, widthHeader));
		HorizontalLayout hLayoutButton = new HorizontalLayout();
		hLayoutButton.setWidthFull();
		hLayoutButton.add(btnViewTask);
		btnViewTask.getStyle().setMarginLeft("auto");
		
		vLayout.add(hLayoutButton);
	}
	
	private Component createLayoutKeyAndValue(String key,String value,String widthHeader,String style) {
		HorizontalLayout hlayout = new HorizontalLayout();
		
		Span spHeader = new Span(key);
		spHeader.setWidth(widthHeader);
		spHeader.getStyle().setFontWeight(600).setFlexShrink("0");
		
		Span spValue = new Span(value);
		if(style != null) {
			spValue.getStyle().setColor(style);
		}
		
		if(isLayoutMobile) {
			hlayout.getStyle().setDisplay(Display.FLEX).setFlexDirection(FlexDirection.COLUMN);
		}
		
		hlayout.setWidthFull();
		hlayout.add(spHeader,spValue);
		hlayout.getStyle().setBorderBottom("1px solid #c3c3c3").setPadding("5px");
		
		
		return hlayout;
	}
	
	private void openDialogViewDetailV2(String idTask) {
		DialogTemplate dialogTemplate = new DialogTemplate("THÔNG TIN NHỆM VỤ");
		
		TaskViewDetailFormV2 taskViewDetailFormV2 = new TaskViewDetailFormV2(idTask,false,false,false,false);
		dialogTemplate.add(taskViewDetailFormV2);
		taskViewDetailFormV2.addChangeListener(e->{
			if(taskViewDetailFormV2.isCheckDeleteTask()) {
				dialogTemplate.close();
				refreshMainLayout();
			}
			fireEvent(new ClickEvent(this,false));
		});
		
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setSizeFull();
		dialogTemplate.open();
	}

}
