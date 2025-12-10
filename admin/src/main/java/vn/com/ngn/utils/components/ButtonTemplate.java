package vn.com.ngn.utils.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.server.StreamResource;

public class ButtonTemplate extends Button{
private static final long serialVersionUID = 1L;
	
	private String id;
	private Anchor anchor = new Anchor();
	
	public ButtonTemplate() {
		buildLayout();
	}
	
	public ButtonTemplate(String text) {
		this.setText(text);
		buildLayout();
	}
	
	public ButtonTemplate(Component icon) {
		this.setIcon(icon);
		buildLayout();
	}
	
	public ButtonTemplate(String text,Component icon) {
		this.setText(text);
		this.setIcon(icon);
		buildLayout();
	}

	public void buildLayout() {
		this.getStyle().setCursor("pointer");
	}

	public void configComponent() {
		
	}
	
	public void setCannotUse() {
		this.setVisible(false);
	}
	
	public void setDownload() {
		id = "download_"+System.currentTimeMillis();
		anchor.setId(id);
		anchor.setTarget("_new");
		anchor.getElement().setAttribute("download", true);
	}
	
	public void downLoad(StreamResource streamResource) {
		anchor.setEnabled(true);
		anchor.setHref(streamResource);
		
		Page page = UI.getCurrent().getPage();
		page.executeJs("document.getElementById('"+id+"').click();");
	}
	
	public void setOpenNewPage(String url) {
		id = "download_"+System.currentTimeMillis();
		anchor.setId(id);
		anchor.setTarget("_new");
		anchor.getElement().setAttribute("_blank", true);
		anchor.setEnabled(true);
		anchor.setHref(url);
		Page page = UI.getCurrent().getPage();
		page.executeJs("document.getElementById('"+id+"').click();");
	}

	public Anchor getAnchor() {
		return this.anchor;
	}

	public void setAnchor(Anchor anchor) {
		this.anchor = anchor;
	}
}
