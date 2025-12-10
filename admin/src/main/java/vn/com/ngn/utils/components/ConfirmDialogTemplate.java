package vn.com.ngn.utils.components;

import com.vaadin.flow.component.confirmdialog.ConfirmDialog;

public class ConfirmDialogTemplate extends ConfirmDialog{
	private static final long serialVersionUID = 1L;
	
	public ConfirmDialogTemplate() {
		super();
		this.setConfirmText("Xác nhận");
		this.setCancelText("Quay lại");
	}
	
	public ConfirmDialogTemplate(String title) {
		super();
		this.setHeader(title);
		this.setConfirmText("Xác nhận");
		this.setCancelText("Quay lại");
	}

	
}
