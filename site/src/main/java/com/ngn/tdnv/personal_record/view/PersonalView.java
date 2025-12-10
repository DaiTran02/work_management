package com.ngn.tdnv.personal_record.view;

import com.ngn.tdnv.personal_record.form.ListPersonalForm;
import com.ngn.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

@PageTitle(value = "Hồ sơ cá nhân")
@Route(value = "personal",layout = MainLayout.class)
@PermitAll
public class PersonalView extends VerticalLayout{
	private static final long serialVersionUID = 1L;

	public PersonalView() {
		this.setSizeFull();
		this.add(new ListPersonalForm());
	}
}
