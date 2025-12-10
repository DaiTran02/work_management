package com.ngn.utils.components;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.icon.Icon;

public class DialogTemplate extends Dialog{
	private static final long serialVersionUID = 1L;
	
	private ButtonTemplate closeButton = new ButtonTemplate(new Icon("lumo", "cross"));
	private ButtonTemplate btnSave = new ButtonTemplate("Lưu");
	
	public DialogTemplate() {
		super();
		this.removeAll();
		
		builLayout();
		configComponent();
		setLayoutMobile();
	}
	
	public DialogTemplate(String textHeader) {
		super();
		H5 h5 = new H5(textHeader);
		h5.getStyle().setColor("white");
		this.setHeaderTitle(textHeader);
		builLayout();
		configComponent();
		setLayoutMobile();
	}
	
	public void builLayout() {
		this.setDraggable(true);
		this.setResizable(true);
		this.setCloseOnOutsideClick(false);
		this.setCloseOnEsc(true);
		
		btnSave = new ButtonTemplate("Lưu");
		btnSave.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
		btnSave.getStyle().setBorder("1px solid");
		
		closeButton = new ButtonTemplate(FontAwesome.Regular.WINDOW_CLOSE.create());
		closeButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
		closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		closeButton.getStyle().setFontSize("24px").setMarginRight("-13px").setColor("hsl(3deg 100% 50%)");
		
		getHeader().add(closeButton);
		getFooter().add(btnSave);
	}

	public void configComponent() {
		closeButton.addClickListener(e->{
			close();
		});
		
	}
	
	public void setLayoutMobile() {
		UI.getCurrent().getPage().retrieveExtendedClientDetails(e->{
			if(e.getScreenWidth() < 768) {
				this.setWidthFull();
			}
		});
	}
	
	public Button getBtnSave() {
		return this.btnSave;
	}
	
	public Button getBtnClose() {
		return this.closeButton;
	}

}

