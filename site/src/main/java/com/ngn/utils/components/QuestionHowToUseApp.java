package com.ngn.utils.components;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.interfaces.FormInterface;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.dom.Style.JustifyContent;

public class QuestionHowToUseApp extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private Image image = new Image("./images/question2.jpg", "anh");
	
	private Runnable onRun;
	public QuestionHowToUseApp(Runnable onRun) {
		this.onRun = onRun;
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.setWidthFull();
		this.setHeight("360px");
		this.getStyle().setBoxShadow("rgba(0, 0, 0, 0.24) 0px 3px 8px").setPadding("10px").setBorderRadius("10px");
		H3 h3 = new H3("Hệ thống theo dõi nhiệm vụ");
		h3.getStyle().setFontSize("25px").setMargin("0 auto");
		
		Span spanDsr = new Span("Đây là lần đầu đăng nhập của tài khoản này, bạn có muốn hệ thống giới thiệu sơ qua các chức có trong hệ thống không.");
		spanDsr.getStyle().setFontWeight(600);
		
		image.setHeight("200px");
		image.getStyle().setMargin("0 auto");
		
		HorizontalLayoutTemplate hLayout = new HorizontalLayoutTemplate();
		
		ButtonTemplate btnYes = new ButtonTemplate("Có hãy giới thiệu sơ qua",FontAwesome.Solid.ARROW_UP_RIGHT_FROM_SQUARE.create());
		btnYes.addClickListener(e->{
			fireEvent(new ClickEvent(this, false));
		});
		
		
		ButtonTemplate btnNo = new ButtonTemplate("Bỏ qua",FontAwesome.Solid.ANGLES_RIGHT.create());
		btnNo.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnNo.addThemeVariants(ButtonVariant.LUMO_ERROR);
		btnNo.addClickListener(e->{
			onRun.run();
		});
		
		hLayout.setWidthFull();
		hLayout.add(btnNo,btnYes);
		hLayout.getStyle().setJustifyContent(JustifyContent.SPACE_BETWEEN);
		
		
		this.add(h3,spanDsr,image,hLayout);
		
	}

	@Override
	public void configComponent() {
		
	}

}
