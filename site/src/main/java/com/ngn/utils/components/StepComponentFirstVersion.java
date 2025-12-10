package com.ngn.utils.components;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style.AlignItems;
import com.vaadin.flow.dom.Style.Display;
import com.vaadin.flow.dom.Style.FlexDirection;

@Tag("step-component")
public class StepComponentFirstVersion extends Composite<Div>{
	private static final long serialVersionUID = 1L;
	
	   private List<Div> stepIndicators = new ArrayList<>();
	    private int currentStep = 0;
	    HorizontalLayout hLayout = new HorizontalLayout();
	    
	    public void ok() {
	    	getContent().add(hLayout);
	    	hLayout.setWidthFull();
	    	hLayout.getStyle().set("gap", "190px");
	    }
	    
	    public void addStep(String header,String date,String helper,boolean isActive,boolean isDone,boolean isLastStep) {
	    	currentStep++;
	    	hLayout.add(createStep(header,date, helper, isActive,isDone,currentStep,isLastStep));
	    }
	    
	    private Component createStep(String header,String date,String helper,boolean isActive,boolean isDone,int index,boolean isLastStep) {
	    	VerticalLayout vLayoutStep = new VerticalLayout();
	    	
	    	Div divHeader = new Div();
	    	
	    	Span spHeader = new Span(header);
	    	spHeader.getStyle().setFontWeight(600);
	    	
	    	Span spDate = new Span("( "+date+" )");
	    	
	    	divHeader.add(spHeader,spDate);
	    	divHeader.getStyle().setDisplay(Display.FLEX).setFlexDirection(FlexDirection.COLUMN).setAlignItems(AlignItems.CENTER);
	    	
	    	Span spHelper = new Span(helper);
	    	
	    	if(isActive) {
	    		spHeader.getStyle().setColor("#0db02b");
	    		spDate.getStyle().setColor("#0db02b");
	    		spHelper.getStyle().setColor("#0db02b");
	    	}
	    	
	    	vLayoutStep.add(divHeader,createStepIndicator(currentStep,isActive,isLastStep,isDone),spHelper);
	    	vLayoutStep.setWidth("100px");
	    	vLayoutStep.getStyle().setAlignItems(AlignItems.CENTER);
	    	vLayoutStep.setSpacing(false);
	    	
	    	
	    	return vLayoutStep;
	    }
	    

	    private Div createStepIndicator(int stepIndex,boolean state,boolean isLastStep,boolean isDone) {
	        Div step = new Div();
	        step.addClassName("step-indicator");
	        
	        if(isLastStep) {
	        	step.addClassName("last-step");
	        }
	        
	        if(state) {
	        	step.addClassName("active");
	        }
	        
	        if(isDone) {
	        	step.addClassName("done");
	        }
	        
	        Span spStep = new Span(stepIndex+"");
	        step.add(spStep);
	        
	        return step;
	    }

	    public void updateStep(int stepIndex) {
	        if (stepIndex < 0 || stepIndex >= stepIndicators.size()) {
	            throw new IllegalArgumentException("Invalid step index");
	        }
	        // Remove the active class from all steps
	        stepIndicators.forEach(step -> step.getClassNames().remove("active-step"));
	        // Add the active class to the current step
	        stepIndicators.get(stepIndex).addClassName("active-step");
	        currentStep = stepIndex;
	    }

	    public void nextStep() {
	        if (currentStep < stepIndicators.size() - 1) {
	            updateStep(currentStep + 1);
	        }
	    }

	    public void previousStep() {
	        if (currentStep > 0) {
	            updateStep(currentStep - 1);
	        }
	    }
}
