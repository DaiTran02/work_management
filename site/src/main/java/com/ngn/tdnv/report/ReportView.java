package com.ngn.tdnv.report;

import com.ngn.tdnv.report.form.ReportForm;
import com.ngn.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

@Route(value = "report", layout = MainLayout.class)
@PageTitle(value = "Báo cáo")
@PermitAll
public class ReportView extends VerticalLayout{
	private static final long serialVersionUID = 1L;
	
	public ReportView() {
		this.setSizeFull();
		this.add(new ReportForm());
	}

}
