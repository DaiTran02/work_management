package com.ngn.utils.components;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.flowingcode.vaadin.addons.fontawesome.FontAwesome.Solid.Icon;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

@Tag("step-component-second-version")
public class StepComponentSecondVersion extends Composite<Div>{
	private static final long serialVersionUID = 1L;
	
	private int currentStep = 0;
	HorizontalLayout hLayout = new HorizontalLayout();
	
	
	public StepComponentSecondVersion() {
		getContent().add(hLayout);
		getStyle().setWidth("100%");
		hLayout.addClassName("node_container");
	}
	
	public void addStep(String name,boolean isActive,boolean isDone,boolean isLastStep) {
		currentStep++;
		Div div = new Div();
		div.addClassName("connector");
		if(isLastStep) {
			div.addClassName("last_connector");
		}
		hLayout.add(createStepIndicator(name, currentStep, isActive, isDone,isLastStep),div);
	}
	
	private Div createStepIndicator(String name,int stepIndex,boolean isActive,boolean isDone,boolean isLastStep) {
		Div step = new Div();
		step.addClassName("node_step");
		step.setWidth("300px");
		Icon icon = FontAwesome.Solid.CHECK_CIRCLE.create();
		icon.setVisible(false);
		icon.setSize("15px");
		
		Span spName = new Span(name);
		spName.getStyle().setMargin("0 auto");
		
		if(isActive) {
			step.addClassName("active");
		}
		
		if(isDone) {
			step.addClassName("node_done");
			icon.setVisible(true);
		}
		
		if(isLastStep) {
			step.addClassName("last_node");
		}
		
		step.add(spName,icon);
		
		return step;
	}
	
	
	public void refreshStep() {
		hLayout.removeAll();
	}

}
