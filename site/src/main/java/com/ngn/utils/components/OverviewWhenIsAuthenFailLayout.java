package com.ngn.utils.components;

import com.ngn.interfaces.FormInterface;
import com.ngn.utils.PropsUtil;
import com.ngn.views.LoginView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexDirection;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style.AlignItems;
import com.vaadin.flow.dom.Style.Display;
import com.vaadin.flow.dom.Style.JustifyContent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "authen_fail")
@AnonymousAllowed
@PageTitle("Không thể xác thực")
public class OverviewWhenIsAuthenFailLayout extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	
	public OverviewWhenIsAuthenFailLayout() {
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		addClassNames("image-gallery-view");
		H2 header = new H2("HỆ THỐNG THEO DÕI NHIỆM VỤ");
		header.getStyle().setMargin("10px auto 0").setColor("#055895").setFontSize("55px");
		
		Span spTitle = new Span("*Không thể xác thực token, vui lòng xác thực lại hoặc liên hệ với quản trị viên để hỗ trợ sửa lỗi.");
		spTitle.getStyle().setFontWeight(600).setFontSize("16px");
		
		this.add(header,new Hr(),spTitle);
		
		FlexLayout flexLayout = new FlexLayout();
		
		OverviewCardForm overviewCardReload = new OverviewCardForm("Kiểm tra lại", "Kiểm tra lại token", "", "./images/lockreload.jpg");
		overviewCardReload.getImage().setWidth("270px");
		OverviewCardForm overviewCardLogin = new OverviewCardForm("Đăng nhập cách khác", "Đăng nhập bằng tên đăng nhập và mật khẩu", "", "./images/loginImage.png");
		OverviewCardForm overviewCardContact = new OverviewCardForm("Liên hệ quản trị viên", "Liên hệ quản trị viên giúp đỡ", "", "./images/contact3d.png");
		overviewCardContact.getImage().getStyle().setWidth("250px");
		
		overviewCardReload.addClickListener(e->{
			UI.getCurrent().getPage().reload();
		});
		
		overviewCardLogin.addClickListener(e->{
			UI.getCurrent().navigate(LoginView.class);
		});
		
		overviewCardContact.addClickListener(e->{
			openDialogContact();
		});
		
		flexLayout.add(overviewCardReload,overviewCardLogin,overviewCardContact);
		flexLayout.setFlexWrap(FlexWrap.WRAP);
		flexLayout.setFlexDirection(FlexDirection.ROW);
		flexLayout.getStyle().set("gap", "20px").setMargin("10px auto");
		
		this.getStyle().setMarginTop("5%").setAlignItems(AlignItems.CENTER);
		
		this.add(flexLayout);
		
	}

	@Override
	public void configComponent() {
		
	}
	
	private void openDialogContact() {
		DialogTemplate dialogTemplate = new DialogTemplate("THÔNG TIN LIÊN HỆ");
		dialogTemplate.setWidth("30%");
		dialogTemplate.setHeight("500px");
		
		VerticalLayout vLayout = new VerticalLayout();
		vLayout.setSizeFull();
		
		Div divHeader = new Div();
		
		String name = PropsUtil.getNameAdmin();
		
		Avatar avatar = new Avatar(name);
		avatar.setWidth("70px");
		avatar.setHeight("70px");
		
		Span spanName = new Span(name);
		
		divHeader.add(avatar,spanName);
		divHeader.getStyle().setMargin("0 auto").setDisplay(Display.FLEX).setJustifyContent(JustifyContent.CENTER)
		.setFlexDirection(com.vaadin.flow.dom.Style.FlexDirection.COLUMN).setAlignItems(AlignItems.CENTER);
		
		Component cpnSdt = createLayoutNameValue("Số điện thoại: ", PropsUtil.getPhoneNumberAdmin());
		
		Component cpnEmail = createLayoutNameValue("Email:", PropsUtil.getEmailAdmin());
		
		Span spLabel = new Span("*Vui lòng liên hệ theo thông tin bên dưới");
		spLabel.getStyle().setFontWeight(600);
		
		vLayout.add(divHeader,new Hr(),spLabel,cpnSdt,cpnEmail);
		vLayout.getStyle().setBoxShadow("rgba(0, 0, 0, 0.16) 0px 1px 4px").setPadding("50px").setBorderRadius("10px");
		
		dialogTemplate.add(vLayout);
		dialogTemplate.open();
		dialogTemplate.getFooter().removeAll();
	}
	
	private Component createLayoutNameValue(String header,String name) {
		HorizontalLayout hLayout = new HorizontalLayout();
		
		Span spHeader = new Span(header);
		spHeader.getStyle().setFontWeight(600).setWidth("120px");
		
		Span spName = new Span(name);
		
		hLayout.add(spHeader,spName);
		
		
		return hLayout;
	}

}
