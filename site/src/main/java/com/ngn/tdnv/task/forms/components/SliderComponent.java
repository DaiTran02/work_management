package com.ngn.tdnv.task.forms.components;

import org.vaadin.addons.componentfactory.PaperSlider;
import org.vaadin.addons.componentfactory.PaperSliderVariant;

import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.html.Span;

public class SliderComponent extends VerticalLayoutTemplate{
	private static final long serialVersionUID = 1L;
	private PaperSlider paperSlider = new PaperSlider();
	private Span spTotal = new Span("0%");
	
	public SliderComponent(String title,String helper) {
		paperSlider.setLabel(title);
		paperSlider.setHelperText(helper);
		paperSlider.setWidthFull();
		paperSlider.addThemeVariants(PaperSliderVariant.LUMO_SECONDARY);
		paperSlider.setManualValidation(true);
		paperSlider.setPinned(true);
		
		spTotal.getStyle().setMarginLeft("auto");
		
		this.add(paperSlider,spTotal);
		this.setWidthFull();
		this.setSpacing(false);
		this.setPadding(false);
		
		paperSlider.addValueChangeListener(e->{
			spTotal.setText(e.getValue()+"%");
			fireEvent(new ClickEvent(this, false));
		});
	}

	public PaperSlider getPaperSlider() {
		return paperSlider;
	}

	public void setPaperSlider(PaperSlider paperSlider) {
		this.paperSlider = paperSlider;
	}
	
	public void setMark(int mark) {
		this.paperSlider.setValue(mark);
	}
	
	public int getMark() {
		return this.paperSlider.getValue();
	}

}
