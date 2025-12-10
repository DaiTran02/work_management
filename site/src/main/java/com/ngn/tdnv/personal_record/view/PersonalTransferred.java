package com.ngn.tdnv.personal_record.view;

import com.ngn.tdnv.personal_record.form.ListPersonalTransferredForm;
import com.ngn.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

@PageTitle(value = "Hồ sơ được chuyển giao")
@Route(value = "transferred",layout = MainLayout.class)
@PermitAll
public class PersonalTransferred extends VerticalLayout{
	private static final long serialVersionUID = 1L;

	public PersonalTransferred() {
		this.setSizeFull();
		this.add(new ListPersonalTransferredForm());
	}
}
