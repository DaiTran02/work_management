package vn.com.ngn.utils.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.server.StreamResource;

public class ButtonDownLoadTemplate extends Div{
	private static final long serialVersionUID = 1L;
	
	private String id;
	private Button button = new Button();
	private Anchor anchor = new Anchor();
	
	public ButtonDownLoadTemplate() {
		buildLayout();
		configComponent();
	}

	public ButtonDownLoadTemplate(String text,Component icon) {
		button = new Button(text,icon);
		buildLayout();
		configComponent();
	}
	
	private void buildLayout() {
		id = "download_"+System.currentTimeMillis();
		anchor.setId(id);
		anchor.setTarget("_new");
		
		add(button,anchor);
	}
	
	private void configComponent() {
		
	}
	
	public void download() {
		Page page = UI.getCurrent().getPage();
		page.executeJs("document.getElementById('"+id+"').click();");
	}
	
	public void download(StreamResource streamResource) {
		anchor.setEnabled(true);
		anchor.setHref(streamResource);
		
		Page page = UI.getCurrent().getPage();
		page.executeJs("document.getElementById('"+id+"').click();");
	}
	
	public Button getButton() {
		return this.button;
	}
	
	public Anchor getAnchor() {
		return this.anchor;
	}
	
	public String getIdDownLoad() {
		return this.id;
	}
	
}




















