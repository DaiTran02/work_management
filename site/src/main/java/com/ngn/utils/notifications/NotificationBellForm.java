package com.ngn.utils.notifications;

import java.util.List;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.notification.ApiNotifiFilterModel;
import com.ngn.api.notification.ApiNotificationModel;
import com.ngn.api.notification.ApiNotificationService;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.DialogTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.dom.Style.Overflow;

public class NotificationBellForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private final ApiNotificationService apiNotificationService;
	private BelongOrganizationModel belongOrganizationModel = SessionUtil.getOrg();
	private UserAuthenticationModel userAuthenticationModel = SessionUtil.getUser();
	private Grid<ApiNotificationModel> grid = new Grid<ApiNotificationModel>(ApiNotificationModel.class,false);
	private ButtonTemplate btnReadAll = new ButtonTemplate("Đọc tất cả",FontAwesome.Solid.CHECK_CIRCLE.create());
	private Span spDesr = new Span("*Các thông báo sẽ tự động xóa khỏi hệ thống sau 90 ngày ");
	private Span spEmpty = new Span("Không có thông báo nào");
	
	private boolean isNotifiUser = false;
	public NotificationBellForm(ApiNotificationService apiNotificationService,boolean isNotifiUser) {
		this.apiNotificationService = apiNotificationService;
		this.isNotifiUser = isNotifiUser;
		buildLayout();
		configComponent();
		loadData();
	}
	
	@Override
	public void buildLayout() {
		this.add(btnReadAll);
		spDesr.getStyle().setFontSize("10px").setMarginTop("auto");
		btnReadAll.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnReadAll.getStyle().setMargin("0px 0px 0px auto");
		btnReadAll.setHeight("10px");
		btnReadAll.setEnabled(false);
		spEmpty.setVisible(false);
		spEmpty.getStyle().setMargin("auto");
		this.add(createGridNotifi(),spEmpty,spDesr);
	}

	@Override
	public void configComponent() {
		btnReadAll.addClickListener(e->{
			doViewAll(); 
		});
		
	}
	
	public void loadData() {
		try {
			ApiNotifiFilterModel apiNotifiFilterModel = new ApiNotifiFilterModel();
			apiNotifiFilterModel.setFromDate(0);
			apiNotifiFilterModel.setToDate(0);
			apiNotifiFilterModel.setSkip(0);
			apiNotifiFilterModel.setLimit(0);
			if(belongOrganizationModel != null && belongOrganizationModel.getOrganizationId() != null) {
				apiNotifiFilterModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
			}
			if(isNotifiUser) {
				apiNotifiFilterModel.setOrganizationUserId(userAuthenticationModel.getId());
			}
			
			ApiResultResponse<List<ApiNotificationModel>> listNotifi = apiNotificationService.getAllNotifi(apiNotifiFilterModel);
			if(listNotifi.isSuccess()) {
				if(listNotifi.getResult().isEmpty()) {
					spEmpty.setVisible(true);
					grid.setVisible(false);
				}else {
					grid.setItems(listNotifi.getResult());
				}
				
				for(ApiNotificationModel model : listNotifi.getResult()) {
					if(model.isViewed() == false) {
						btnReadAll.setEnabled(true);
						break;
					}else {
						btnReadAll.setEnabled(false);
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	private Component createGridNotifi() {
		grid = new Grid<ApiNotificationModel>(ApiNotificationModel.class,false);
		
		grid.addComponentColumn(model->{
			VerticalLayoutTemplate vLayout = new VerticalLayoutTemplate();
			
			H5 header = new H5(model.getTitle());
			header.getStyle().setWidth("100%").setOverflow(Overflow.HIDDEN).set("text-overflow", "ellipsis");
			
			Span spContent = new Span(model.getContent());
			spContent.getStyle().setFontSize("12px").setWidth("100%").setOverflow(Overflow.HIDDEN).set("text-overflow", "ellipsis");;
			
			vLayout.add(header,spContent);
			
			if(model.isViewed()) {
				header.getStyle().setColor("#000000a3");
				spContent.getStyle().setColor("#000000a3");
				Span spTimeViewed = new Span("Đã đọc lúc "+model.getViewedTimeText());
				spTimeViewed.getStyle().set("font-style", "italic").setFontSize("11px");
				vLayout.add(spTimeViewed);
			}else {
				spContent.getStyle().setFontWeight(500);
			}
			
			
			vLayout.setWidthFull();
			
			return vLayout;
		});
		
//		grid.addComponentColumn(model->{
//			ButtonTemplate btnView = new ButtonTemplate(FontAwesome.Solid.EYE.create());
//			btnView.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
//			btnView.addClickListener(e->{
//				openDialogViewNotifi(model.getId());
//			});
//			
//			return btnView;
//		}).setWidth("70px").setFlexGrow(0);
		
		grid.addItemClickListener(e->{
			openDialogViewNotifi(e.getItem().getId());
		});
		
		
		grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		
		
		
		return grid;
	}
	
	private void doViewAll() {
		ApiNotifiFilterModel apiNotifiFilterModel = new ApiNotifiFilterModel();
		apiNotifiFilterModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
		apiNotifiFilterModel.setOrganizationUserId(userAuthenticationModel.getId());
		ApiResultResponse<Object> viewAll = apiNotificationService.setAllMarkViewed(apiNotifiFilterModel);
		if(viewAll.isSuccess()) {
			loadData();
			System.out.println(viewAll.getMessage());
		}else {
			System.out.println(viewAll.getMessage());
		}
	}
	
	private void openDialogViewNotifi(String idNotifi) {
		DialogTemplate dialogTemplate = new DialogTemplate("Chi tiết thông báo");
		
		NotificationDetailForm notificationDetailForm = new NotificationDetailForm(apiNotificationService, idNotifi);
		notificationDetailForm.addChangeListener(e->{
			loadData();
		});
		dialogTemplate.add(notificationDetailForm);
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setWidth("60%");
		dialogTemplate.setLayoutMobile();
		dialogTemplate.open();
		
	}

}
