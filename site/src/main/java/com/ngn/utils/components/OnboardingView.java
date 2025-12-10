package com.ngn.utils.components;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.componentfactory.PopupPosition;
import com.vaadin.componentfactory.onboarding.Onboarding;
import com.vaadin.componentfactory.onboarding.OnboardingStep;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class OnboardingView extends Onboarding{
	private static final long serialVersionUID = 1L;
	private Onboarding onboarding = new Onboarding();
	private List<OnboardingStep> listSteps = new ArrayList<OnboardingStep>();

	
	public OnboardingView() {
		this.createCloseOnboardingButton();
	}
	
	public void onStartBoarding() {
		listSteps.forEach(onboard->{
			onboarding.addStep(onboard);
		});
		onboarding.start();
	}
	
	
	public OnboardingStep createStep(Component component,String header,String content) {
		OnboardingStep onboardingStep = new OnboardingStep(component);
		VerticalLayout vContent = new VerticalLayout();
		vContent.add(new Paragraph(content));
		vContent.setWidthFull();
		vContent.getStyle().set("max-width", "30rem");
		
		H3 h3 = new H3(header);
		h3.getStyle().setMarginLeft("5px").setMarginRight("auto").setMarginTop("5px").setWidth("100%");
		
		onboardingStep.setContent(vContent);
		onboardingStep.getTargetElement().getChildren().forEach(md->{
		});
		
		
		onboardingStep.setPosition(PopupPosition.BOTTOM);
		onboardingStep.addBeforePopupShownListener(popup -> {
			
			popup.getHeader().removeAll();
			popup.getHeader().add(h3);
			
			popup.getFooter().getElement().getChildren().forEach(btn->{
				if(btn.getAttribute("id").equals("next-step-button")) {
					btn.setText("Tiếp tục");
				}
				if(btn.getAttribute("id").equals("previous-step-button")) {
					btn.setText("Quay lại");
				}
				if(btn.getAttribute("id").equals("close-button-footer")) {
					btn.setText("Hoàn tất");
				}
			});
		});
		listSteps.add(onboardingStep);
		return onboardingStep;
	}

	public void addStep(OnboardingStep onboardingStep) {
		listSteps.add(onboardingStep);
	}
	
	public void removeAllListStep() {
		if(!listSteps.isEmpty()) {
			listSteps.clear();
		}
	}
	
	public OnboardingStep getStep(OnboardingStep onboardingStep) {
		for(OnboardingStep model : onboarding.getSteps()) {
			if(model.equals(onboardingStep)) {
				return model;
			}
		}
		return null;
	}
	
	public void doEvent(int index,Runnable run) {
		if(!onboarding.getSteps().isEmpty()) {
			onboarding.getSteps().get(index).addBeforePopupShownListener(t ->{
				run.run();
			});
		}
	}

}
