package com.ngn.tdnv.task.forms.components;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.interfaces.FormInterface;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class StepByStepDoTaskForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private int index = 1;
	
	private HorizontalLayout hLayout = new HorizontalLayout();
	private Div lastDivLine = null;
	
	public StepByStepDoTaskForm() {
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.setWidthFull();
		hLayout.setWidthFull();
		this.add(hLayout);
		hLayout.addClassName("step_task_container");
	}

	@Override
	public void configComponent() {
		
	}
	
	public void clear() {
		index = 1;
		hLayout.removeAll();
	}
	
	private boolean previousIsDone = false;
	public void addStepItem(String header,String helper,boolean isActive,boolean isDone) {
		Div divLine = new Div();
		divLine.addClassName("div_line_connect");
		
		if(isDone) {
			divLine.addClassName("connect_done");
			
			//new
			lastDivLine = divLine;
		}
		
		if (isActive && lastDivLine != null) {
	        lastDivLine.getElement().getClassList().add("run_animation");

	        // Sau animation, xóa class để nó không lặp lại
	        lastDivLine.getElement().executeJs(
	            "setTimeout(() => this.classList.remove('run_animation'), 1000);"
	        );
	    }
		
		if(isActive && previousIsDone) {
			 divLine.addClassName("run_animation");
		}
		
		hLayout.add(createItem(header, helper, isActive, isDone),divLine);
		index++;
		previousIsDone = isDone;
	}
	
	public void removeLastComponent() {
		if(!hLayout.getChildren().toList().isEmpty()) {
			Component[] components = hLayout.getChildren().toArray(Component[]::new);
			hLayout.remove(components[components.length-1]);
		}
	}
	
	private Component createItem(String header,String helper,boolean isActive,boolean isDone) {
		Div divItem = new Div();
		divItem.addClassNames("item-step");
		
		Span spHeader = new Span(header);
		spHeader.getStyle().setFontWeight(600);
		
		String help = "";
		if(helper != null) {
			help = helper.isEmpty() ? "" : "("+helper+")";
		}
		
		Span spHelp = new Span(help);
		
		ButtonTemplate btnIndex = new ButtonTemplate();
		btnIndex.addClassName("item_index");
		if(isActive) {
			divItem.addClassName("active");
			btnIndex.addClassName("active");
		}
		
		if(isDone) {
			divItem.addClassName("done");
			btnIndex.addClassName("done");
		}
		
		btnIndex.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		if(index == 1) {
			btnIndex.setIcon(FontAwesome.Solid.ARROW_RIGHT_TO_FILE.create());
		}
		
		if(index == 2) {
			btnIndex.setIcon(FontAwesome.Solid.FILE_EDIT.create());
		}
		
		if(index == 3) {
			btnIndex.setIcon(FontAwesome.Solid.USER_CHECK.create());
		}
		
		if(index == 4) {
			btnIndex.setIcon(FontAwesome.Regular.CHECK_SQUARE.create());
		}
		
		if(index == 5) {
			btnIndex.setIcon(FontAwesome.Solid.STAR.create());
		}
		
		
		divItem.add(spHeader,spHelp,btnIndex);
		
		return divItem;
	}

}
