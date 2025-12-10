package com.ngn.tdnv.do_something_with_url;

import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;

@Route(value = "orther")
public class DoSomethingWithUrlView extends VerticalLayoutTemplate implements HasUrlParameter<String>{
	private static final long serialVersionUID = 1L;
	
	public DoSomethingWithUrlView() {
		this.add(new Span("abx"));
		
	}

	@Override
	public void setParameter(BeforeEvent event,@OptionalParameter String parameter) {
		
	}

}
