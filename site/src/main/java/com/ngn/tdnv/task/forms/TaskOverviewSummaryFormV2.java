package com.ngn.tdnv.task.forms;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.addons.yuri0x7c1.bslayout.BsColumn;
import org.vaadin.addons.yuri0x7c1.bslayout.BsColumn.Size;
import org.vaadin.addons.yuri0x7c1.bslayout.BsLayout;
import org.vaadin.addons.yuri0x7c1.bslayout.BsRow;

import com.ngn.api.tasks.ApiTaskSummaryModel;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;

public class TaskOverviewSummaryFormV2 extends VerticalLayoutTemplate{
	private static final long serialVersionUID = 1L;

	private List<ButtonTemplate> listButtons = new ArrayList<>();
	private BsLayout bsLayout = new BsLayout();
	private List<ApiTaskSummaryModel> listData = new ArrayList<>();

	public TaskOverviewSummaryFormV2() {
		buildLayout();
	}

	public void buildLayout() {
		setWidthFull();
		getStyle().setPadding("0");
		add(bsLayout);
	}

	public void loadData(List<ApiTaskSummaryModel> listData) {
		this.listData.clear();
		this.listData.addAll(listData);
		createLayoutEvent();
	}

	private void createLayoutEvent() {
		bsLayout.removeAll();
		BsRow row = bsLayout.addRow();
		bsLayout.addClassName("bslayout--padding");
		listButtons.clear();

		listData.forEach(model -> row.addColumn(createTaskColumn(model)).addSize(Size.MD, 3));
		bsLayout.setWidthFull();
	}

	private BsColumn createTaskColumn(ApiTaskSummaryModel model) {
		ButtonTemplate mainButton = createButton(model.getKey(), model.getShortName()+ ": "+model.getCount(), getTheme(model.getKey()));
		Span countSpan = createCountSpan(model.getCount());
		TaskSummaryForm taskSummary = new TaskSummaryForm(mainButton, countSpan);
		taskSummary.addClassName("task__summary-" + model.getKey());

		List<ButtonTemplate> childButtons = new ArrayList<>();
		model.getChild().forEach(child -> {
			ButtonTemplate childButton = createButton(child.getKey(), child.getShortName() + ": " + child.getCount(), ButtonVariant.LUMO_TERTIARY);
			childButtons.add(childButton);
		});

		taskSummary.addMoreButton(childButtons.toArray(new ButtonTemplate[0]));
		listButtons.addAll(childButtons);
		return new BsColumn(taskSummary);
	}

	private ButtonTemplate createButton(String id, String text, ButtonVariant... variants) {
		ButtonTemplate button = new ButtonTemplate(text);
		button.setId(id);
		button.addThemeVariants(variants);
		return button;
	}

	private Span createCountSpan(int count) {
		Span span = new Span(String.valueOf(count));
		span.getElement().getThemeList().add("badge contrast");
		return span;
	}

	private ButtonVariant[] getTheme(String key) {
		switch (key) {
		case "dangthuchien": return new ButtonVariant[]{ButtonVariant.LUMO_TERTIARY};
		case "dahoanthanh": return new ButtonVariant[]{ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SUCCESS};
		case "choxacnhan": return new ButtonVariant[]{ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR};
		case "khac": return new ButtonVariant[]{ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR};
		default: return new ButtonVariant[]{ButtonVariant.LUMO_TERTIARY};
		}
	}

	public List<ButtonTemplate> getListButtons() {
		return listButtons;
	}
}
