package com.ngn.tdnv.task.forms.details;

import com.ngn.interfaces.FormInterface;
import com.ngn.tdnv.task.models.TaskDocInforModel;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class TaskViewDocForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private TaskDocInforModel docInforOfTaskModel;
	public TaskViewDocForm(TaskDocInforModel docInforOfTaskModel) {
		this.docInforOfTaskModel = docInforOfTaskModel;
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(createLayout());
	}

	@Override
	public void configComponent() {

	}

	private Component createLayout() {
		VerticalLayout vLayout = new VerticalLayout();

		//This class of table in file task.css in line 186
		Html html = new Html("<table class='table_doc'>"
					+ "<tr>"
						+ "<th>Kí hiệu</th>"
						+ "<td>"+docInforOfTaskModel.getSymbol()+"</td>"
					+ "</tr>"
					+ "<tr>"
						+ "<th>Số hiệu</th>"
						+ "<td>"+docInforOfTaskModel.getNumber()+"</td>"
					+ "</tr>"
					+ "<tr>"
						+ "<th>Trích yếu</th>"
						+ "<td>"+docInforOfTaskModel.getSummary()+"</td>"
					+ "</tr>"
				+ "</table>");
		
		vLayout.setSizeFull();
		vLayout.add(html);

		return vLayout;
	}

}
