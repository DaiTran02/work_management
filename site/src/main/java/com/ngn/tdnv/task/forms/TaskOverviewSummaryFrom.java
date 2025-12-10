package com.ngn.tdnv.task.forms;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.addons.yuri0x7c1.bslayout.BsColumn;
import org.vaadin.addons.yuri0x7c1.bslayout.BsLayout;
import org.vaadin.addons.yuri0x7c1.bslayout.BsRow;
import org.vaadin.addons.yuri0x7c1.bslayout.BsColumn.Size;

import com.ngn.api.tasks.ApiTaskSummaryModel;
import com.ngn.interfaces.FormInterface;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;

public class TaskOverviewSummaryFrom extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private List<ButtonTemplate> listButtons = new ArrayList<ButtonTemplate>();

	private BsLayout bsLayout = new BsLayout();
	private List<ApiTaskSummaryModel> listData = new ArrayList<ApiTaskSummaryModel>();
	public TaskOverviewSummaryFrom() {
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		this.setWidthFull();
		this.getStyle().setPadding("0");
		this.add(bsLayout);
	}

	@Override
	public void configComponent() {
		
	}
	
	public void loadData(List<ApiTaskSummaryModel> listData) {
		this.listData = new ArrayList<ApiTaskSummaryModel>();
		this.listData.addAll(listData);
		createLayoutEvent();
	}
	
	private void createLayoutEvent() {
		bsLayout.removeAll();
		BsRow row = bsLayout.addRow();
		bsLayout.addClassName("bslayout--padding");
		listButtons = new ArrayList<ButtonTemplate>();
		for(ApiTaskSummaryModel model : listData) {
			switch(model.getKey()) {
			case "dangthuchien":
				ButtonTemplate btnDangThucHien = new ButtonTemplate("Đang thực hiện");
				btnDangThucHien.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
				btnDangThucHien.setId("dangthuchien");
				listButtons.add(btnDangThucHien);

				Span spanCountDangThucHien = new Span(String.valueOf(model.getCount()));
				spanCountDangThucHien.getElement().getThemeList().add("badge contrast");
				spanCountDangThucHien.getStyle().setMarginLeft("auto");


				TaskSummaryForm taskSummaryDangThucHien = new TaskSummaryForm(btnDangThucHien, spanCountDangThucHien);
				taskSummaryDangThucHien.addClassName("task__summary-dangthuchien");

				List<ButtonTemplate> buttonTemplates = new ArrayList<ButtonTemplate>();
				model.getChild().forEach(model2->{
					ButtonTemplate buttonTemplate = new ButtonTemplate(model2.getShortName()+ ": "+model2.getCount());
					buttonTemplate.setHeight("15px");
					buttonTemplate.setId(model2.getKey());
					buttonTemplate.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
					buttonTemplate.getStyle().setColor("black");

					buttonTemplates.add(buttonTemplate);
					listButtons.add(buttonTemplate);
				});

				ButtonTemplate[] buttonTemplates2 =  buttonTemplates.toArray(new ButtonTemplate[buttonTemplates.size()]);

				taskSummaryDangThucHien.addMoreButton(buttonTemplates2);

				BsColumn bsColumn = row.addColumn(new BsColumn(taskSummaryDangThucHien));
				bsColumn.addSize(Size.MD,3);
				break;
				
			case "choxacnhan":
				ButtonTemplate btnChoXacNhan = new ButtonTemplate("Chờ xác nhận");
				btnChoXacNhan.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
				btnChoXacNhan.addThemeVariants(ButtonVariant.LUMO_ERROR);
				btnChoXacNhan.getStyle().setColor("#8b7b00");
				btnChoXacNhan.setId("choxacnhan");
				listButtons.add(btnChoXacNhan);

				Span spanCountChoXacNhan = new Span(String.valueOf(model.getCount()));
				spanCountChoXacNhan.getElement().getThemeList().add("badge contrast");

				TaskSummaryForm taskSummaryChoXacNhan = new TaskSummaryForm(btnChoXacNhan, spanCountChoXacNhan);
				taskSummaryChoXacNhan.addClassName("task__summary-choxacnhan");

				List<ButtonTemplate> buttonTemplatesChoXacNhan = new ArrayList<ButtonTemplate>();
				model.getChild().forEach(model2->{
					ButtonTemplate buttonTemplate = new ButtonTemplate(model2.getShortName()+ ": "+model2.getCount());
					buttonTemplate.setHeight("15px");
					buttonTemplate.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
					buttonTemplate.setId(model2.getKey());
					buttonTemplate.getStyle().setColor("black");

					buttonTemplatesChoXacNhan.add(buttonTemplate);
					listButtons.add(buttonTemplate);
				});

				ButtonTemplate[] btnChoXacNhans =  buttonTemplatesChoXacNhan.toArray(new ButtonTemplate[buttonTemplatesChoXacNhan.size()]);

				taskSummaryChoXacNhan.addMoreButton(btnChoXacNhans);
				BsColumn bsColumn3 = row.addColumn(new BsColumn(taskSummaryChoXacNhan));
				bsColumn3.addSize(Size.MD,3);
				break;

			case "dahoanthanh":
				ButtonTemplate btnDaHoanThanh = new ButtonTemplate("Đã hoàn thành");
				btnDaHoanThanh.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
				btnDaHoanThanh.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
				btnDaHoanThanh.setId("dahoanthanh");
				listButtons.add(btnDaHoanThanh);

				Span spanCountDaHoanThanh = new Span(String.valueOf(model.getCount()));
				spanCountDaHoanThanh.getElement().getThemeList().add("badge contrast");


				TaskSummaryForm taskSummaryDaHoanThanh = new TaskSummaryForm(btnDaHoanThanh, spanCountDaHoanThanh);
				taskSummaryDaHoanThanh.addClassName("task__summary-dahoanthanh");

				List<ButtonTemplate> buttonTemplatesDHT = new ArrayList<ButtonTemplate>();
				model.getChild().forEach(model2->{
					ButtonTemplate buttonTemplate = new ButtonTemplate(model2.getShortName()+ ": "+model2.getCount());
					buttonTemplate.setHeight("15px");
					buttonTemplate.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
					buttonTemplate.getStyle().setColor("black");
					buttonTemplate.setId(model2.getKey());


					buttonTemplatesDHT.add(buttonTemplate);
					listButtons.add(buttonTemplate);
				});

				ButtonTemplate[] btnDaHoanThanhs =  buttonTemplatesDHT.toArray(new ButtonTemplate[buttonTemplatesDHT.size()]);

				taskSummaryDaHoanThanh.addMoreButton(btnDaHoanThanhs);
				BsColumn bsColumn2 = row.addColumn(new BsColumn(taskSummaryDaHoanThanh));
				bsColumn2.addSize(Size.MD,3);
				break;

			case "khac":
				ButtonTemplate btnKhac = new ButtonTemplate("Khác");
				btnKhac.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
				btnKhac.addThemeVariants(ButtonVariant.LUMO_ERROR);

				Span spanKhac = new Span(String.valueOf(model.getCount()));
				spanKhac.getElement().getThemeList().add("badge contrast");


				TaskSummaryForm taskSummaryKhac = new TaskSummaryForm(btnKhac, spanKhac);
				taskSummaryKhac.addClassName("task__summary-khac");

				List<ButtonTemplate> buttonTemplatesKhac = new ArrayList<ButtonTemplate>();
				model.getChild().forEach(model2->{
					ButtonTemplate buttonTemplate = new ButtonTemplate(model2.getShortName()+ ": "+model2.getCount());
					buttonTemplate.setHeight("15px");
					buttonTemplate.setId(model2.getKey());
					buttonTemplate.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
					buttonTemplate.getStyle().setColor("black");
					buttonTemplatesKhac.add(buttonTemplate);
					
					listButtons.add(buttonTemplate);
					
				});

				ButtonTemplate[] btnKhacs =  buttonTemplatesKhac.toArray(new ButtonTemplate[buttonTemplatesKhac.size()]);

				taskSummaryKhac.addMoreButton(btnKhacs);

				BsColumn bsColumn4 = row.addColumn(new BsColumn(taskSummaryKhac));
				bsColumn4.addSize(Size.MD,3);
				break;
			}
		}
		
		bsLayout.setWidthFull();
		
	}

	public List<ButtonTemplate> getListButtons() {
		return listButtons;
	}

}
