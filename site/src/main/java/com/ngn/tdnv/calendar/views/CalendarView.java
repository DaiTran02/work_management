package com.ngn.tdnv.calendar.views;

import com.ngn.tdnv.calendar.forms.CalendarForm;
import com.ngn.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

@Route(value = "calendar",layout = MainLayout.class)
@PageTitle("Lịch công tác")
@PermitAll
public class CalendarView extends VerticalLayout {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CalendarForm formCalendar = new CalendarForm();

    public CalendarView(){
        this.add(formCalendar);

        this.setSizeFull();
    }
}
