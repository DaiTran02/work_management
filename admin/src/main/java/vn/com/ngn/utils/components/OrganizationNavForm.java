package vn.com.ngn.utils.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

import vn.com.ngn.interfaces.FormInterface;

public class OrganizationNavForm extends Div implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	List<Pair<String, String>> list = new ArrayList<Pair<String,String>>();
	
	public OrganizationNavForm() {
		this.setWidth("80%");
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.removeAll();
		for(int i = 0;i<list.size();i++) {
			Pair<String, String> item = list.get(i);
			Button btnItem = new Button(list.get(i).getRight());
			btnItem.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnItem.getStyle().setCursor("pointer");
			
			btnItem.addClickListener(e->{
				removeItem(item);
			});
			if(i!=0) {
				this.add(new Span(">"));
			}
			
			add(btnItem);
		}
		
	}

	@Override
	public void configComponent() {
		
	}
	
	public void addItem(Pair<String, String> item) {
		list.add(item);
		buildLayout();
	}
	
	public void removeItem(Pair<String, String> item) {
		int index = list.indexOf(item);
		list = list.subList(0, index+1);
		buildLayout();
	}
	
	public Pair<String, String> getCurrentItem(){
		return list.get(list.size()-1);
	}
	
	
	public Pair<String, String> getItem(){
		return list.get(list.size()-2);
	}

}
