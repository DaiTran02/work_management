package com.ngn.utils.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
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

public class OverviewCardForm extends ListItem{
	private static final long serialVersionUID = 1L;
	
	private Image image = new Image();
	
	public OverviewCardForm(String title,String dsrTitle,String text,String url) {
		addClassNames(Background.CONTRAST_5, Display.FLEX, FlexDirection.COLUMN, AlignItems.START, Padding.MEDIUM,
				BorderRadius.LARGE);
		this.setWidth("100%");
		this.getStyle().set("cursor", "pointer");

		Div div = new Div();
		div.addClassNames(Background.CONTRAST, Display.FLEX, AlignItems.CENTER, JustifyContent.CENTER,
				Margin.Bottom.MEDIUM, Overflow.HIDDEN, BorderRadius.MEDIUM, Width.FULL);
		div.setHeight("150px");
		this.setWidth("230px");
		div.getStyle().setBackground("white");

		
		image.setWidth("120px");
		image.setSrc(url);
		image.setAlt("");

		
		div.add(image);

		Span header = new Span();
		header.addClassNames(FontSize.XLARGE, FontWeight.SEMIBOLD);
		header.setText(title);

		Span subtitle = new Span();
		subtitle.addClassNames(FontSize.SMALL, TextColor.SECONDARY);
		subtitle.setText(dsrTitle);

		Paragraph description = new Paragraph(
				text);
		description.addClassName(Margin.Vertical.MEDIUM);

		Span badge = new Span();
		badge.getElement().setAttribute("theme", "badge");
		badge.setText("Label");

		add(div, header, subtitle, description);
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

}
