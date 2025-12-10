package com.ngn.tdnv.report.form;

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

public class CardReportForm extends ListItem {
    private static final long serialVersionUID = 1L;

    public CardReportForm(String title, String dsrTitle, String text, String url) {
        addClassNames(Background.CONTRAST_5, Display.FLEX, FlexDirection.COLUMN, AlignItems.START, Padding.MEDIUM,
                BorderRadius.LARGE, "card-animation");
        
        // Animation delay ngẫu nhiên
        this.getElement().getStyle().set("animation-delay", "0." + (int)(Math.random() * 5) + "s");
        
        // Style cơ bản cho card
        this.getStyle()
            .set("cursor", "pointer")
            .set("transition", "all 0.3s ease")
            .set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)")
            .set("transform", "translateY(0)")
            .set("z-index", "1");

        Div div = new Div();
        div.addClassNames(Background.CONTRAST, Display.FLEX, AlignItems.CENTER, JustifyContent.CENTER,
                Margin.Bottom.MEDIUM, Overflow.HIDDEN, BorderRadius.MEDIUM, Width.FULL);
        div.setHeight("160px");
        div.getStyle()
           .setBackground("#225c965e")
           .set("transition", "all 0.3s ease");

        Image image = new Image();
        image.setWidth("70%");
        image.setSrc(url);
        image.setAlt("");
        image.getStyle().set("transition", "transform 0.3s ease");

        div.add(image);

        Span header = new Span();
        header.addClassNames(FontSize.XLARGE, FontWeight.SEMIBOLD);
        header.setText(title);

        Span subtitle = new Span();
        subtitle.addClassNames(FontSize.SMALL, TextColor.SECONDARY);
        subtitle.setText(dsrTitle);

        Paragraph description = new Paragraph(text);
        description.addClassName(Margin.Vertical.MEDIUM);

        add(div, header, subtitle, description);

        // Hiệu ứng khi hover
        this.getElement().addEventListener("mouseenter", e -> {
            this.getStyle()
                .set("box-shadow", "0 10px 20px rgba(0,0,0,0.2)")
                .set("transform", "translateY(-5px)")
                .set("z-index", "10");
//            div.getStyle().setBackground("#225c96");
            image.getStyle().set("transform", "scale(1.05)");
        });
        
        this.getElement().addEventListener("mouseleave", e -> {
            this.getStyle()
                .set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)")
                .set("transform", "translateY(0)")
                .set("z-index", "1");
//            div.getStyle().setBackground("#225c965e");
            image.getStyle().set("transform", "scale(1)");
        });
    }
}
