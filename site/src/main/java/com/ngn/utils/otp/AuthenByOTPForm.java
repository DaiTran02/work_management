package com.ngn.utils.otp;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;

public class AuthenByOTPForm extends ConfirmDialog{
	private static final long serialVersionUID = 1L;
	private HorizontalLayout hLayout = new HorizontalLayout();
	private static final int OTP_LENGTH = 6;
	private List<TextField> listField = new ArrayList<TextField>();

	@SuppressWarnings("deprecation")
	public AuthenByOTPForm() {
		this.add(hLayout);
		hLayout.setSizeFull();
		createLayout();
		this.open();
		
		this.setCancelText("Quay lại");
		this.setCancelable(true);
		this.setCloseOnEsc(false);
		
		this.setConfirmText("Xác nhận");
		this.setHeader("Xác thực OTP");
		
		this.addConfirmListener(e->{
			checkOTP();
		});
		
	}

	private int index = 0;

	private void createLayout() {
		hLayout.removeAll();
		index = 0;
		for (int i = 0; i < OTP_LENGTH; i++) {
			TextField field = new TextField();
			field.setWidth("50px");
			field.setMaxLength(1);
			field.setPattern("[0-9]");
			field.setAutocorrect(false);
			field.setAutofocus(i == 0);

			field.addValueChangeListener(e->{
				if(!field.getValue().isEmpty() && index < OTP_LENGTH-1) {
					listField.get(index+1).focus();
					index++;
				}
			});

			listField.add(field);
			hLayout.add(field);
		}

		listField.get(index).focus();
	}
	
	private void checkOTP() {
		fireEvent(new ClickEvent(this, false));
	}
	
	public Registration addChangeListener(ComponentEventListener<ClickEvent> listener) {
		return addListener(ClickEvent.class, listener);
	}

	public static class ClickEvent extends ComponentEvent<AuthenByOTPForm> {
		private static final long serialVersionUID = 1L;

		public ClickEvent(AuthenByOTPForm source, boolean fromClient) {
			super(source, fromClient);
		}
	}

}
