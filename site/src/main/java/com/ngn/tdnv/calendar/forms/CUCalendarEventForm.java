package com.ngn.tdnv.calendar.forms;

import java.time.Duration;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.interfaces.FormInterface;
import com.ngn.tdnv.calendar.enums.CalendarPeriodEnum;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.timepicker.TimePicker;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class CUCalendarEventForm extends VerticalLayoutTemplate implements FormInterface {
	private static final long serialVersionUID = 1L;

	HorizontalLayout hMain = new HorizontalLayout();

    VerticalLayout vLeft = new VerticalLayout();
    FormLayout formLayoutLeft = new FormLayout();
    ComboBox<CalendarPeriodEnum> cmbPeriod = new ComboBox<>();
    DatePicker dpFrom = new DatePicker();
    DatePicker dpTo = new DatePicker();
    TimePicker tpFrom = new TimePicker();
    TimePicker tpTo = new TimePicker();
    TextArea txtContent = new TextArea("Nội dung");
    VerticalLayout vSuggest = new VerticalLayout();
    TextArea txtNote = new TextArea("Ghi chú");

    VerticalLayout vRight = new VerticalLayout();
    H4 captionHost = new H4("Chủ trì");
    ButtonTemplate btnSelectHost = new ButtonTemplate("Chọn chủ trì", FontAwesome.Solid.HAND_POINT_UP.create());
    FormLayout formLayoutHost = new FormLayout();

    H4 captionAttendeeRequired = new H4("Người tham dự (bắt buộc)");
    ButtonTemplate btnSelectAttendeeRequired = new ButtonTemplate("Chọn người tham dự bắt buộc", FontAwesome.Solid.HAND_POINT_UP.create());
    FormLayout formLayoutAttendeeRequired = new FormLayout();

    H4 captionAttendeeNoRequired = new H4("Người tham dự (Không bắt buộc)");
    ButtonTemplate btnSelectAttendeeNoRequired = new ButtonTemplate("Chọn người tham dự không bắt buộc", FontAwesome.Solid.HAND_POINT_UP.create());
    FormLayout formLayoutAttendeeNoRequired = new FormLayout();

    HorizontalLayout hLocation = new HorizontalLayout();
    Span spanLocation = new Span();
    ButtonTemplate btnSelectLocation = new ButtonTemplate(FontAwesome.Solid.SEARCH_LOCATION.create());

    H4 captionResource = new H4();
    ButtonTemplate btnAddResource = new ButtonTemplate("Trang thiết bi",FontAwesome.Solid.PLUS.create());
    VerticalLayout vResourceList = new VerticalLayout();

    public CUCalendarEventForm(){
        buildLayout();
        configComponent();
    }

    @Override
    public void buildLayout() {
        this.add(hMain);

        hMain.add(vLeft, vRight);
        /* Build left */
        vLeft.add(formLayoutLeft);

        formLayoutLeft.addFormItem(cmbPeriod,"");
        formLayoutLeft.addFormItem(dpFrom,"");
        formLayoutLeft.addFormItem(dpTo,"");
        formLayoutLeft.addFormItem(tpFrom,"");
        formLayoutLeft.addFormItem(tpTo,"");

        formLayoutLeft.addFormItem(txtContent,"");
        formLayoutLeft.addFormItem(vSuggest,"");
        formLayoutLeft.addFormItem(txtNote,"");

        /* Build right */
        vRight.add(captionHost);
        vRight.add(btnSelectHost);
        vRight.add(formLayoutHost);

        vRight.add(captionAttendeeRequired);
        vRight.add(btnSelectAttendeeRequired);
        vRight.add(formLayoutAttendeeRequired);

        vRight.add(captionAttendeeNoRequired);
        vRight.add(btnSelectAttendeeNoRequired);
        vRight.add(formLayoutAttendeeNoRequired);

        vRight.add(new Hr());

        vRight.add(hLocation);
        hLocation.add(spanLocation,btnSelectLocation);

        hLocation.setWidthFull();

        vRight.add(captionResource);
        vRight.add(btnAddResource);
        vRight.add(vResourceList);
    }

    @Override
    public void configComponent() {
        cmbPeriod.setItems(CalendarPeriodEnum.values());
        cmbPeriod.setItemLabelGenerator(CalendarPeriodEnum::getCaption);

        cmbPeriod.setValue(CalendarPeriodEnum.DAY);

//        dpFrom.setLocale(new Locale("vi", "VN"));
//        dpTo.setLocale(new Locale("vi", "VN"));
//        tpFrom.setLocale(new Locale("vi", "VN"));
//        tpTo.setLocale(new Locale("vi", "VN"));

        tpFrom.setStep(Duration.ofMinutes(15));
        tpTo.setStep(Duration.ofMinutes(15));
    }
}
