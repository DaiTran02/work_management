package com.ngn.tdnv.task.forms.components;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.flowingcode.vaadin.addons.fontawesome.FontAwesome.Solid.Icon;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.dom.Style.AlignItems;
import com.vaadin.flow.dom.Style.Display;

public class PinnedStatusForm extends VerticalLayoutTemplate{
	private static final long serialVersionUID = 1L;
	private boolean isMobileLayout = false;

	public PinnedStatusForm(String text,String colorPin) {
		checkMobileLayout();
		this.setWidth("auto");
		HorizontalLayout pinnedStatus = new HorizontalLayout();
		pinnedStatus.setWidth("auto");
		pinnedStatus.getStyle().setDisplay(Display.FLEX).setAlignItems(AlignItems.CENTER).setBackground("#f9f9f9")
		.setPadding("5px").setBorder("1px solid #ddd").setBorderRadius("8px").setBoxShadow(" 0 4px 6px rgba(0, 0, 0, 0.1)");

		// Ghim
		Div pin = new Div();
		pin.getStyle().setWidth("10px").setHeight("10px").setFlexShrink("0").setBackground(colorPin).setBorder("2px solid #fff")
		.setBorderRadius("50%").setBoxShadow("0 2px 4px rgba(0, 0, 0, 0.2)").setMarginRight("10px");
		
		Icon iconTag = FontAwesome.Solid.TAG.create();
		iconTag.setColor(colorPin);

		// Text
		Span statusText = new Span(text);

		// Thêm ghim và text vào layout
		pinnedStatus.add(iconTag, statusText);
		
		if(isMobileLayout) {
			pinnedStatus.setWidthFull();
			this.setWidthFull();
		}

		// Thêm vào layout chính
		add(pinnedStatus);
	}
	
	private void checkMobileLayout() {
		UI.getCurrent().getPage().retrieveExtendedClientDetails(e->{
			if(e.getScreenWidth() < 768) {
				isMobileLayout = true;
			}
		});
	}

}
