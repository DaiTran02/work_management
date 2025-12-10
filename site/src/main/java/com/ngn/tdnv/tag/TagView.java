package com.ngn.tdnv.tag;

import com.ngn.tdnv.tag.forms.TagForm;
import com.ngn.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

@PageTitle(value = "Thẻ")
@Route(value = "tag",layout = MainLayout.class)
@PermitAll
public class TagView extends VerticalLayout{
	private static final long serialVersionUID = 1L;
	
	public TagView() {
		this.setSizeFull();
		TagForm tagForm = new TagForm(false,null);
		this.add(tagForm);
	}

}
