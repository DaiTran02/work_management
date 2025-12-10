package com.ngn.utils.components;


import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.UIScope;


@UIScope
public class ChipComponent<T> extends Composite<HorizontalLayout> implements HasStyle,HasSize{
	private static final long serialVersionUID = 1L;
	
	protected T item;
	protected ItemLabelGenerator<T> itemLabelGenerator=Object::toString;
	
	protected final Button btnDelete=new Button(VaadinIcon.CLOSE_CIRCLE.create());
	@SuppressWarnings("deprecation")
	protected final Label label=new Label();
	protected Icon icon=new Icon();
	
	public ChipComponent(Icon icon,final T item) {
		this.icon=icon;
		this.item=item;
		initUI();

		
	}
	
	protected void initUI() {
		this.btnDelete.setDisableOnClick(true);
		this.btnDelete.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY_INLINE);
		this.btnDelete.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
//		this.btnDelete.getStyle().set("font-size", "var(--lumo-font-size-m)");
		
		this.label.setSizeUndefined();
		this.btnDelete.setSizeUndefined();
		
		this.getContent().setSpacing(true);
		this.getContent().setAlignItems(Alignment.CENTER);
		
		final Style style = this.getContent().getStyle();
		style.set("background-color", "var(--lumo-contrast-10pct)");
		style.set("border-radius", "var(--lumo-font-size-s)");
		style.set("margin", "var(--lumo-space-xs)");
		style.set("padding-left", " var(--lumo-space-s)");
		
		if(this.icon==null) {
			this.getContent().add(this.label, this.btnDelete);
		}else {
			this.getContent().add(this.icon,this.label, this.btnDelete);
		}
		
	}
	
	@Override
	protected void onAttach(final AttachEvent attachEvent)
	{
		this.updateTextFromItemLabelGenerator();
	}

	public T getItem()
	{
		return this.item;
	}

	public void setItemLabelGenerator(final ItemLabelGenerator<T> itemLabelGenerator)
	{
		this.itemLabelGenerator = itemLabelGenerator;
	}
	
	/**
	 * Updates the text of the {@link Label} from the integrated {@link ItemLabelGenerator}
	 */
	public void updateTextFromItemLabelGenerator()
	{
		this.label.setText(this.itemLabelGenerator.apply(this.item));
	}

	public Registration addBtnDeleteClickListener(final ComponentEventListener<ClickEvent<Button>> listener)
	{
		return this.btnDelete.addClickListener(listener);
	}

	public void setReadonly(final boolean readOnly)
	{
		this.btnDelete.setEnabled(!readOnly);
	}

}