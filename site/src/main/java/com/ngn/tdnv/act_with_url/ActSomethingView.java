package com.ngn.tdnv.act_with_url;

import java.util.List;
import java.util.Map;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.tdnv.act_with_url.form.DoAssginTaskWithUrlForm;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

@Route("act_url")
@PageTitle("Theo dõi nhiệm vụ")
@PermitAll
public class ActSomethingView extends VerticalLayoutTemplate implements HasUrlParameter<String>{
	private static final long serialVersionUID = 1L;
	private VerticalLayout vLayout = new VerticalLayout();
	private ButtonTemplate btnMoveToSite = new ButtonTemplate("Vào hệ thống theo dõi nhiệm vụ",FontAwesome.Solid.ARROW_RIGHT.create());
	private ButtonTemplate btnMoveToDoc = new ButtonTemplate("Quay lại hệ thống văn bản",FontAwesome.Solid.ARROW_LEFT.create());
	public ActSomethingView() {
		this.add(vLayout);
		buildLayout();
	}
	
	private void buildLayout() {
		vLayout.removeAll();
		HorizontalLayout hLayout = new HorizontalLayout();
		
		btnMoveToSite.getStyle().setMarginLeft("auto");
		
		hLayout.setWidthFull();
		hLayout.getStyle().setBorderBottom("1px solid #c5c5c5");
		hLayout.add(btnMoveToDoc,btnMoveToSite);
		vLayout.add(hLayout);
		createLayout();
		
	}
	
	private void createLayout() {
		
	}

	@Override
	public void setParameter(BeforeEvent event,@OptionalParameter String parameter) {
		Location location = event.getLocation();
		QueryParameters queryParameters = location.getQueryParameters();
		Map<String, List<String>> parameters = queryParameters.getParameters();
		DoAssginTaskWithUrlForm doAssginTaskWithUrl = new DoAssginTaskWithUrlForm(parameters);
		vLayout.add(doAssginTaskWithUrl);
	}

}
