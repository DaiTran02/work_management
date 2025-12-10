package com.ngn.setting.components;

import java.util.List;
import java.util.Map;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.sign_in_org.ApiSignInOrgModel;
import com.ngn.api.sign_in_org.ApiSignInOrgService;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.sign_in_org.SignInOrgModel;
import com.ngn.utils.CheckParametersUtil;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.NotificationTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Background;
import com.vaadin.flow.theme.lumo.LumoUtility.BorderRadius;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Overflow;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;

public class CardOrgForm extends ListItem{
	private static final long serialVersionUID = 1L;

	private BelongOrganizationModel belongOrganizationModel;
	private boolean checkChangeOrg = false;
	private Map<String, List<String>> parametters;
	private boolean checkOrgFail = false;
	public CardOrgForm(BelongOrganizationModel belongOrganizationModel,boolean checkChangeOrg,
			Map<String, List<String>> parametters,boolean checkOrdFail,boolean viewAllOrg ) {
		this.belongOrganizationModel = belongOrganizationModel;
		this.checkChangeOrg = checkChangeOrg;
		this.parametters = parametters;
		this.checkOrgFail = checkOrdFail;
		if(viewAllOrg) {
			createLayoutViewAllOrg();
		}else {
			createLayout();
		}
	}

	private void createLayoutViewAllOrg() {
		addClassNames(Background.CONTRAST_5, Display.FLEX, FlexDirection.COLUMN, AlignItems.START, Padding.MEDIUM,
				BorderRadius.LARGE);
		this.setWidth("250px");
		this.getStyle().setCursor("pointer");
		this.getElement().setProperty("title", "Chọn đơn vị này");
		VerticalLayout vLayout = new VerticalLayout();

		Div div = new Div();
		div.setHeight("150px");
		div.addClassNames(Background.CONTRAST, Display.FLEX, AlignItems.CENTER, JustifyContent.CENTER,
				Margin.Bottom.MEDIUM, Overflow.HIDDEN, BorderRadius.MEDIUM, Width.FULL);

		Image image = new Image();
		image.setWidth("100%");
		image.setSrc("https://quanlyvanban.hanoi.gov.vn/qlvbdh/login/img/bg-login.svg");
		image.setAlt("Hinh nen");
		div.add(image);

		Avatar avatar = new Avatar("+");
		avatar.setWidth("70px");
		avatar.setHeight("70px");
		avatar.getStyle().setBackground("white");

		Span header = new Span();
		header.addClassNames(FontSize.XLARGE, FontWeight.SEMIBOLD, AlignItems.CENTER);
		header.setText("Chọn đơn vị khác");
		header.getStyle().setMargin("auto");

		HorizontalLayout hLayoutAvatar = new HorizontalLayout();
		hLayoutAvatar.add(avatar);
		hLayoutAvatar.getStyle().setMargin("auto").setMarginBottom("0").setMarginTop("-35px");

		Span spanDsr = new Span();
		spanDsr.addClassNames(FontSize.SMALL, TextColor.SECONDARY, AlignItems.CENTER);
		spanDsr.setText("Tài khoản này có vai trò xem toàn bộ hệ thống theo dõi đôn đốc");

		vLayout.add(header,createLayoutKeyValue("Mô tả: ", "Chọn đơn vị khác ngoài các đơn vị trên",null),
				createLayoutKeyValue("Chức vụ: ", null, spanDsr));
		
		this.addClickListener(e->{
			fireEvent(new ClickEvent(this, false));
		});
		
		add(div,hLayoutAvatar,vLayout);
	}

	private void createLayout() {
		addClassNames(Background.CONTRAST_5, Display.FLEX, FlexDirection.COLUMN, AlignItems.START, Padding.MEDIUM,
				BorderRadius.LARGE);
		this.setWidth("250px");
		this.getStyle().setCursor("pointer");
		this.getElement().setProperty("title", "Chọn đơn vị này");
		VerticalLayout vLayout = new VerticalLayout();



		Div div = new Div();
		div.setHeight("150px");
		div.addClassNames(Background.CONTRAST, Display.FLEX, AlignItems.CENTER, JustifyContent.CENTER,
				Margin.Bottom.MEDIUM, Overflow.HIDDEN, BorderRadius.MEDIUM, Width.FULL);

		Image image = new Image();
		image.setWidth("100%");
		image.setSrc("https://quanlyvanban.hanoi.gov.vn/qlvbdh/login/img/bg-login.svg");
		image.setAlt("Hinh nen");
		div.add(image);

		Avatar avatar = new Avatar(belongOrganizationModel.getOrganizationName());
		avatar.setWidth("70px");
		avatar.setHeight("70px");
		avatar.getStyle().setBackground("white");

		Button btnCheck = new Button(FontAwesome.Solid.CIRCLE_CHECK.create());
		btnCheck.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnCheck.setTooltipText("Bạn đang ở trong đơn vị này");
		btnCheck.getStyle().setMarginLeft("-32px").setMarginTop("36px");

		HorizontalLayout hLayoutAvatar = new HorizontalLayout();
		if(checkChangeOrg) {
			if(SessionUtil.getOrg().getOrganizationId().equals(belongOrganizationModel.getOrganizationId())) {
				hLayoutAvatar.add(avatar,btnCheck);
			}else {
				hLayoutAvatar.add(avatar);
			}

		}else {
			hLayoutAvatar.add(avatar);
		}
		hLayoutAvatar.getStyle().setMargin("auto").setMarginBottom("0").setMarginTop("-35px");




		Span header = new Span();
		header.addClassNames(FontSize.XLARGE, FontWeight.SEMIBOLD, AlignItems.CENTER);
		header.setText(belongOrganizationModel.getOrganizationName());
		header.getStyle().setMargin("auto");



		SignInOrgModel signInOrgModel = getOrg(belongOrganizationModel.getOrganizationId());

		Span spanDsr = new Span();
		spanDsr.addClassNames(FontSize.SMALL, TextColor.SECONDARY, AlignItems.CENTER);
		spanDsr.setText("Mô tả: "+signInOrgModel.getDescription());



		Span spanRole = new Span();
		spanRole.addClassNames(FontSize.SMALL, TextColor.SECONDARY, AlignItems.CENTER);

		String checkRole = signInOrgModel.getRoles().getName().isEmpty() ? "Chưa cập nhật" : "";
		spanRole.add(checkRole);
		for(String stringRole : signInOrgModel.getRoles().getName()) {
			spanRole.add(stringRole + " ");
		}
		CheckParametersUtil checkParametersUtil = new CheckParametersUtil(parametters);
		this.addClickListener(e->{
			if(SessionUtil.getOrgId() != null) {
				if(SessionUtil.getOrgId().equals(belongOrganizationModel.getOrganizationId())) {
					if(checkOrgFail) {
						SessionUtil.setOrgId(belongOrganizationModel);
						SessionUtil.setDetailOrg(signInOrgModel);
						checkParametersUtil.handleParam();
						fireEvent(new ClickEvent(this,false));
					}
					NotificationTemplate.warning("Hiện đang ở trong đơn vị này");
				}else {
					SessionUtil.setOrgId(belongOrganizationModel);
					SessionUtil.setDetailOrg(signInOrgModel);
					UI.getCurrent().getPage().reload();
				}
			}else {
				SessionUtil.setOrgId(belongOrganizationModel);
				SessionUtil.setDetailOrg(signInOrgModel);
				checkParametersUtil.handleParam();
				fireEvent(new ClickEvent(this,false));
			}
		});

		vLayout.add(header,createLayoutKeyValue("Mô tả: ", signInOrgModel.getDescription(),null),createLayoutKeyValue("Chức vụ: ", null, spanRole));

		add(div,hLayoutAvatar,vLayout);
	}

	private Component createLayoutKeyValue(String header,String content,Span spValue) {
		HorizontalLayout hLayout = new HorizontalLayout();

		Span spanHeader = new Span(header);
		spanHeader.getStyle().setFontWeight(600);

		Span spanContent = new Span(content);
		if(content == null) {
			spanContent = spValue;
		}

		hLayout.add(spanHeader,spanContent);


		return hLayout;
	}


	private SignInOrgModel getOrg(String id) {
		try {
			ApiResultResponse<ApiSignInOrgModel> getData = ApiSignInOrgService.getDetailOrg(id);
			SignInOrgModel signInOrgModel = new SignInOrgModel(getData.getResult());
			return signInOrgModel;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Registration addChangeListener(ComponentEventListener<ClickEvent> listener) {
		return addListener(ClickEvent.class, listener);
	}

	public static class ClickEvent extends ComponentEvent<CardOrgForm> {
		private static final long serialVersionUID = 1L;

		public ClickEvent(CardOrgForm source, boolean fromClient) {
			super(source, fromClient);
		}
	}

}





















