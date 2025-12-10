package com.ngn.tdnv.calendar.forms;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.interfaces.FormInterface;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.DialogTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;

public class CalendarForm extends VerticalLayoutTemplate implements FormInterface {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ButtonTemplate btnNewEvent = new ButtonTemplate("Thêm sự kiện", FontAwesome.Solid.PLUS.create());

    public CalendarForm(){
        buildLayout();
        configComponent();
    }

    @Override
    public void buildLayout() {

    }

    @Override
    public void configComponent() {
        btnNewEvent.addClickListener(e->{
            DialogTemplate dialogTemplate = new DialogTemplate("THÊM SỰ KIỆN");

            CUCalendarEventForm cuCalendarEventForm = new CUCalendarEventForm();
            dialogTemplate.add(cuCalendarEventForm);
            dialogTemplate.setSizeFull();
            dialogTemplate.open();
        });
    }
}
