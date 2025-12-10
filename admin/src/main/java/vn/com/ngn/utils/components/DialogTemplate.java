package vn.com.ngn.utils.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.icon.Icon;

public class DialogTemplate extends Dialog{
	private static final long serialVersionUID = 1L;
	
	private Button closeButton = new Button(new Icon("lumo", "cross"));
	private Button btnSave = new Button("Lưu");
	
	private Runnable onRun;
	public DialogTemplate() {
		super();
		this.removeAll();
		
		builLayout();
		configComponent();
	}
	
	public DialogTemplate(String textHeader,Runnable onRun) {
		super();
		H5 h5 = new H5(textHeader);
		h5.getStyle().setColor("white");
		this.setHeaderTitle(textHeader);
		this.onRun = onRun;
		builLayout();
		configComponent();
	}
	
	public void builLayout() {
		btnSave = new Button("Lưu");
		closeButton = new Button(new Icon("lumo", "cross"));
		setClassName("dialog-component");
		closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		closeButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
		closeButton.getStyle().setCursor("pointer");
		
		btnSave.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
		btnSave.getStyle().setCursor("pointer").setBorder("1px solid").setHeight("35px");
//		btnSave.addClickShortcut(Key.ENTER);
		
		getHeader().add(closeButton);
		getFooter().add(btnSave);
		this.setCloseOnOutsideClick(false);
	}

	public void configComponent() {
		closeButton.addClickListener(e->{
			onRun.run();
			close();
		});
		
	}
	
	public Button getBtnSave() {
		return this.btnSave;
	}
	
	public Button getBtnClose() {
		return this.closeButton;
	}

}
