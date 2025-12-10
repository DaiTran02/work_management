package com.ngn.tdnv.process_doc_and_schedule;

import com.ngn.utils.components.VerticalLayoutTemplate;
import com.ngn.views.MainLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.dom.Style.AlignItems;
import com.vaadin.flow.dom.Style.JustifyContent;
import com.vaadin.flow.dom.Style.TextAlign;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

@PermitAll
@Route(value = "processed_doc", layout = MainLayout.class)
@PageTitle("Văn bản trình xử lý")
public class ProcessedDocView extends VerticalLayoutTemplate{
	private static final long serialVersionUID = 1L;
	
	public ProcessedDocView() {
		Span span = new Span("Module/Plugin văn bản trình xử lý chưa được kích hoạt");
		span.getStyle().setFontWeight(700);
		this.setSizeFull();
		this.getStyle().setAlignItems(AlignItems.CENTER).setTextAlign(TextAlign.CENTER).setJustifyContent(JustifyContent.CENTER);
		this.add(span);
	}

}
