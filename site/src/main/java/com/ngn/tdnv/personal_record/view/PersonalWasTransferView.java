package com.ngn.tdnv.personal_record.view;

import com.ngn.tdnv.personal_record.form.ListPersonalRecordTransferForm;
import com.ngn.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

@PageTitle(value = "Hồ sơ đã chuyển giao")
@Route(value = "personal_transfer",layout = MainLayout.class)
@PermitAll
public class PersonalWasTransferView extends VerticalLayout{
	private static final long serialVersionUID = 1L;

	public PersonalWasTransferView() {
		this.setSizeFull();
		this.add(new ListPersonalRecordTransferForm());
	}
}