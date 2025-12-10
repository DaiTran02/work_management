package com.ngn.utils.components;

import com.ngn.interfaces.FormInterface;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class RatingModule extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private HorizontalLayout hLayoutStart = new HorizontalLayout();
	private Icon[] arrStar = new Icon[5];
	private int star = 1;
	
	
	public RatingModule() {
		buildLayout();
		configComponent();
		setStar(1);
	}
	
	@Override
	public void buildLayout() {
		this.setSizeFull();
		hLayoutStart.setWidthFull();
		
		this.add(hLayoutStart);
	}

	@Override
	public void configComponent() {
		for(int i = 0 ; i < 5 ; i++) {
			Icon iconStar = VaadinIcon.STAR.create();
			
			//The class in the file rating.css
			iconStar.addClassName("star-unselect");
			
			arrStar[i] = iconStar;
			
			hLayoutStart.add(iconStar);
			
			int index = i;
			int starValue = index+1;
			iconStar.addClickListener(e->{
				setStar(starValue);
				fireEvent(new ClickEvent(this, false));
			});
		
		}
	}
	
	public void setStar(int starInput) {
		this.star = starInput;
		
		int index = star - 1;
		
		for(int i = 0 ; i < arrStar.length ; i++) {
			if(i<=index) {
				//The class in the file rating.css
				arrStar[i].addClassName("star-selected");
			} else {
				//The class in the file rating.css
				arrStar[i].removeClassName("star-selected");
			}
		}
	}
	
	public int getStar() {
		return star;
	}

}
