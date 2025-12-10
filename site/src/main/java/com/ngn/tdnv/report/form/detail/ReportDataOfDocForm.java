package com.ngn.tdnv.report.form.detail;

import com.ngn.interfaces.FormInterface;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.VerticalLayoutTemplate;

public class ReportDataOfDocForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private ReportFilterDocForm reportFilterDocForm = new ReportFilterDocForm(SessionUtil.getOrg());
	
	public ReportDataOfDocForm() {
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(reportFilterDocForm);
		
	}

	@Override
	public void configComponent() {
		
	}

}
