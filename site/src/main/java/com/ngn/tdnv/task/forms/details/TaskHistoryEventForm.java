package com.ngn.tdnv.task.forms.details;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.flowingcode.vaadin.addons.fontawesome.FontAwesome.Solid.Icon;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tasks.ApiOutputTaskModel;
import com.ngn.api.tasks.ApiTaskService;
import com.ngn.interfaces.FormInterface;
import com.ngn.tdnv.task.models.TaskEventModel;
import com.ngn.tdnv.task.models.TaskOutputModel;
import com.ngn.utils.LocalDateUtil;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class TaskHistoryEventForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private VerticalLayout vLayout = new VerticalLayout();
	private TaskOutputModel outputTaskModel;

	private String idTask;
	public TaskHistoryEventForm(String idTask) {
		this.idTask = idTask;
		loadData();
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.setWidthFull();
		this.add(vLayout);
		createLayout();
	}

	@Override
	public void configComponent() {

	}

	public void loadData() {
		ApiResultResponse<ApiOutputTaskModel> getDataTask = ApiTaskService.getAtask(idTask);
		outputTaskModel = new TaskOutputModel(getDataTask.getResult());
	}

	private void createLayout() {
		vLayout.removeAll();

		for(TaskEventModel eventModel : outputTaskModel.getEvents()) {
			if(eventModel!=null) {
				Icon icon = FontAwesome.Solid.ARROW_UP_LONG.create();
				icon.setSize("20px");
				icon.getStyle().setMarginLeft("8px").setColor("hsl(224.83deg 65.6% 42.32% / 94%)");
				vLayout.add(createLayoutEvent(eventModel),icon);
			}
		}

		Component[] components = vLayout.getChildren().toArray(Component[]::new);
		vLayout.remove(components[components.length-1]);


	}

	private Component createLayoutEvent(TaskEventModel eventModel) {
		HorizontalLayout hLayoutEvent = new HorizontalLayout();

		Button btnEvent = new Button(FontAwesome.Solid.CHECK.create());
		Span spanHeader;

		VerticalLayout vLayoutContent = new VerticalLayout();

		switch(eventModel.getAction()) {
		case "taonhiemvu":
			spanHeader = new Span(eventModel.getCreator().getOrganizationUserName()+ " (" + eventModel.getCreator().getOrganizationName()+")"+" đã tạo nhiệm vụ mới ");
			spanHeader.getElement().getThemeList().add("badge success");

			btnEvent = new Button(FontAwesome.Solid.FILE_SIGNATURE.create());

			Icon iconTNV = FontAwesome.Solid.ARROW_RIGHT_LONG.create();
			iconTNV.setSize("14px");
			iconTNV.getStyle().setMarginTop("5px");

			HorizontalLayout hLayoutDateTNV = new HorizontalLayout(createLayoutKeyValue("Ngày giao: ", LocalDateUtil.dfDateTime.format(eventModel.getCreatedTime()), null));
			vLayoutContent.add(spanHeader);
			eventModel.getDescriptions().forEach(model->{
				vLayoutContent.add(createLayoutKeyValue(model.getName()+":", model.getDescription(), null));
			});

			vLayoutContent.add(hLayoutDateTNV);
			break;

		case "thuchiennhiemvu":

			spanHeader = new Span(eventModel.getCreator().getOrganizationUserName().toString() +" (" + eventModel.getCreator().getOrganizationName()+") "+ eventModel.getTitle());
			spanHeader.getElement().getThemeList().add("badge success");

			btnEvent = new Button(FontAwesome.Solid.FILE_CIRCLE_CHECK.create());

			vLayoutContent.add(spanHeader,createLayoutKeyValue("Ngày thực hiện: ", LocalDateUtil.dfDateTime.format(eventModel.getCreatedTime()), null));
			break;
		case "capnhattiendonhiemvu":
			spanHeader = new Span( eventModel.getCreator().getOrganizationUserName()+ " (" + eventModel.getCreator().getOrganizationName()+") " + eventModel.getTitle());
			spanHeader.getElement().getThemeList().add("badge success");

			btnEvent = new Button(FontAwesome.Solid.BATTERY_3.create());
			vLayoutContent.add(spanHeader);

			eventModel.getDescriptions().forEach(model->{
				vLayoutContent.add(createLayoutKeyValue(model.getName()+":", model.getDescription(), null));
			});
			
			vLayoutContent.add(createLayoutKeyValue("Thời gian cập nhật: ", LocalDateUtil.dfDateTime.format(eventModel.getCreatedTime()), null));
			break;
		case "traodoiykiennhiemvu":
			spanHeader = new Span(eventModel.getCreator().getOrganizationUserName()+ " (" + eventModel.getCreator().getOrganizationName()+") " + eventModel.getTitle());
			spanHeader.getElement().getThemeList().add("badge success");

			btnEvent = new Button(FontAwesome.Solid.COMMENT.create());
			vLayoutContent.add(spanHeader);
			eventModel.getDescriptions().forEach(model->{
				vLayoutContent.add(createLayoutKeyValue(model.getName()+":", model.getDescription(), null));
			});
			
			vLayoutContent.add(createLayoutKeyValue("Thời gian thực hiện: ",LocalDateUtil.dfDateTime.format(eventModel.getCreatedTime()), null));
			break;

		case "traloitraodoiykiennhiemvu":
			spanHeader = new Span(eventModel.getCreator().getOrganizationUserName()+ " (" + eventModel.getCreator().getOrganizationName()+") " + eventModel.getTitle());
			spanHeader.getElement().getThemeList().add("badge success");

			btnEvent = new Button(FontAwesome.Solid.COMMENT.create());

			vLayoutContent.add(spanHeader);
			
			eventModel.getDescriptions().forEach(model->{
				vLayoutContent.add(createLayoutKeyValue(model.getName()+":", model.getDescription(), null));
			});
			
			vLayoutContent.add(createLayoutKeyValue("Thời gian thực hiện: ",LocalDateUtil.dfDateTime.format(eventModel.getCreatedTime()), null));
			break;

		case "donvihotrophancanbohotro":
			spanHeader = new Span(eventModel.getTitle());
			spanHeader.getElement().getThemeList().add("badge success");

			btnEvent = new Button(FontAwesome.Solid.USER_GROUP.create());
			vLayoutContent.add(spanHeader,createLayoutKeyValue("Thời gian thực hiện: ", LocalDateUtil.dfDateTime.format(eventModel.getCreatedTime()), null));
			
			eventModel.getDescriptions().forEach(model->{
				vLayoutContent.add(createLayoutKeyValue(model.getName(), model.getDescription(), null));
			});
			
			break;

		case "donvixulyphancanboxuly":
			spanHeader = new Span(eventModel.getTitle());
			spanHeader.getElement().getThemeList().add("badge success");

			btnEvent = new Button(FontAwesome.Solid.USER_PLUS.create());
			vLayoutContent.add(spanHeader,
					createLayoutKeyValue("Thời gian thực hiện: ", LocalDateUtil.dfDateTime.format(eventModel.getCreatedTime()), null));
			eventModel.getDescriptions().forEach(model->{
				vLayoutContent.add(createLayoutKeyValue(model.getName()+":", model.getDescription(), null));
			});
			
			break;

		case "nhacnhothuchiennhiemvu":
			spanHeader = new Span(eventModel.getTitle());
			spanHeader.getElement().getThemeList().add("badge error");

			btnEvent = new Button(FontAwesome.Solid.BELL.create());
			vLayoutContent.add(spanHeader,createLayoutKeyValue("Thời gian thực hiện: ", LocalDateUtil.dfDateTime.format(eventModel.getCreatedTime()), null));
			eventModel.getDescriptions().forEach(model->{
				vLayoutContent.add(createLayoutKeyValue(model.getName()+":", model.getDescription(), null));
			});
			
			break;

		case "trieuhoinhiemvu":
			spanHeader = new Span(eventModel.getTitle());
			spanHeader.getElement().getThemeList().add("badge contrast");

			btnEvent = new Button(FontAwesome.Solid.REFRESH.create());
			vLayoutContent.add(spanHeader);
			
			eventModel.getDescriptions().forEach(model->{
				vLayoutContent.add(createLayoutKeyValue(model.getName()+":", model.getDescription(), null));
			});
			
			vLayoutContent.add(
					createLayoutKeyValue("Đơn vị triệu hồi: ", eventModel.getCreator().getOrganizationName(), null),
					createLayoutKeyValue("Người triệu hồi: ", eventModel.getCreator().getOrganizationUserName().toString(), null),
					createLayoutKeyValue("Thời gian thực hiện: ", LocalDateUtil.dfDateTime.format(eventModel.getCreatedTime()), null));
			
			break;

		case "hoanthanhnhiemvu":
			spanHeader = new Span(eventModel.getTitle());
			spanHeader.getElement().getThemeList().add("badge success");

			btnEvent = new Button(FontAwesome.Solid.CHECK.create());
			vLayoutContent.add(spanHeader,createLayoutKeyValue("Đơn vị hoàn thành:", eventModel.getCreator().getOrganizationName(), null)
					,createLayoutKeyValue("Người thực hiện: ", eventModel.getCreator().getOrganizationUserName().toString(), null),createLayoutKeyValue("Thời gian: ", LocalDateUtil.dfDateTime.format(eventModel.getCreatedTime()), null));

			break;

		case "tamhoanthuchien":
			spanHeader = new Span(eventModel.getTitle());
			spanHeader.getElement().getThemeList().add("badge error");

			btnEvent = new Button(FontAwesome.Solid.CIRCLE_PAUSE.create());
			vLayoutContent.add(spanHeader);
			eventModel.getDescriptions().forEach(model->{
				vLayoutContent.add(createLayoutKeyValue(model.getName()+":", model.getDescription(), null));
			});
			
			vLayoutContent.add(createLayoutKeyValue("Đơn vị tạm hoãn: ", eventModel.getCreator().getOrganizationName(), null),
					createLayoutKeyValue("Người tạm hoãn: ", eventModel.getCreator().getOrganizationUserName().toString(), null),
					createLayoutKeyValue("Thời gian thực hiện: ", LocalDateUtil.dfDateTime.format(eventModel.getCreatedTime()), null));
			break;

		case "tieptucthuchien":
			spanHeader = new Span(eventModel.getTitle());
			spanHeader.getElement().getThemeList().add("badge success");

			btnEvent = new Button(FontAwesome.Solid.ARROW_RIGHT_ROTATE.create());
			vLayoutContent.add(spanHeader,createLayoutKeyValue("Người thực hiện: ", eventModel.getCreator().getOrganizationUserName().toString(), null),
					createLayoutKeyValue("Thời gian thực hiện: ", LocalDateUtil.dfDateTime.format(eventModel.getCreatedTime()), null));

			break;

		case "capnhatnhiemvu":
			spanHeader = new Span(eventModel.getCreator().getOrganizationUserName()+ " đã cập nhật nhiệm vụ");
			spanHeader.getElement().getThemeList().add("badge success");

			btnEvent = new Button(FontAwesome.Solid.EDIT.create());

			Icon iconCNNV = FontAwesome.Solid.ARROW_RIGHT_LONG.create();
			iconCNNV.setSize("14px");
			iconCNNV.getStyle().setMarginTop("5px");

			HorizontalLayout hLayoutDateCNNV = new HorizontalLayout(createLayoutKeyValue("Ngày giao: ", LocalDateUtil.dfDateTime.format(eventModel.getCreatedTime()), null));
			vLayoutContent.add(spanHeader);
			eventModel.getDescriptions().forEach(model->{
				vLayoutContent.add(createLayoutKeyValue(model.getName()+":", model.getDescription(), null));
			});

			vLayoutContent.add(hLayoutDateCNNV);
			break;
			
		case "xacnhanhoanthanh":
			spanHeader = new Span(eventModel.getCreator().getOrganizationUserName()+" "+ eventModel.getTitle());
			spanHeader.getElement().getThemeList().add("badge success");
			
			vLayoutContent.add(spanHeader,createLayoutKeyValue("Thời gian thực hiện: ", LocalDateUtil.dfDateTime.format(eventModel.getCreatedTime()), null));
			break;
			
		case "danhgianhiemvu":
			spanHeader = new Span(eventModel.getCreator().getOrganizationUserName()+" "+ eventModel.getTitle());
			spanHeader.getElement().getThemeList().add("badge success");
			
			btnEvent = new Button(FontAwesome.Solid.STAR.create());
			
			vLayoutContent.add(spanHeader,createLayoutKeyValue("Thời gian thực hiện: ", LocalDateUtil.dfDateTime.format(eventModel.getCreatedTime()), null));
			break;
			
		case "thuchienlainhiemvu":
			spanHeader = new Span(eventModel.getCreator().getOrganizationUserName()+" "+ eventModel.getTitle());
			spanHeader.getElement().getThemeList().add("badge contrast");
			
			btnEvent = new Button(FontAwesome.Solid.ARROW_LEFT_ROTATE.create());
			
			vLayoutContent.add(spanHeader,createLayoutKeyValue("Lý do: ", eventModel.getDescriptions().get(0).getDescription(), null),
					createLayoutKeyValue("Thời gian thực hiện: ", LocalDateUtil.dfDateTime.format(eventModel.getCreatedTime()), null));
			break;
			
		case "tuchoixacnhan":
			spanHeader = new Span(eventModel.getCreator().getOrganizationUserName()+" "+ eventModel.getTitle());
			spanHeader.getElement().getThemeList().add("badge contrast");
			
			btnEvent = new Button(FontAwesome.Solid.CLOSE.create());
			
			vLayoutContent.add(spanHeader,createLayoutKeyValue("Nội dung: ", "Đơn vị giao đã từ chối xác nhận báo cáo từ đơn vị xử lý", idTask),
					createLayoutKeyValue("Thời gian thực hiện: ", LocalDateUtil.dfDateTime.format(eventModel.getCreatedTime()), null));
			break;
		}


		btnEvent.getStyle().setMargin("auto");
		btnEvent.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

		vLayoutContent.setWidthFull();
		vLayoutContent.getStyle().setBoxShadow("rgba(99, 99, 99, 0.2) 0px 2px 8px 0px").setBorderRadius("15px").setPadding("22px");


		hLayoutEvent.add(btnEvent,vLayoutContent);
		hLayoutEvent.setWidthFull();

		return hLayoutEvent;
	}

	private Component createLayoutKeyValue(String header,String content,String style) {
		HorizontalLayout hLayout = new HorizontalLayout();

		Span spanHeader = new Span(header);
		spanHeader.getStyle().setFontWeight(600);

		Span spanContent = new Span(content);

		if(style != null) {
			spanContent.getElement().getThemeList().add(style);
		}

		hLayout.add(spanHeader,spanContent);


		return hLayout;
	}


}
