package com.ngn.tdnv.task.forms.components;

import java.util.List;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.tasks.ApiTaskRedoModel;
import com.ngn.api.tasks.ApiTaskReverseModel;
import com.ngn.api.tasks.actions.ApiPedingModel;
import com.ngn.interfaces.FormInterface;
import com.ngn.tdnv.task.forms.details.TaskCommentForm;
import com.ngn.tdnv.task.forms.details.TaskProgressHistoryForm;
import com.ngn.tdnv.task.forms.details.TaskViewCompletedForm;
import com.ngn.tdnv.task.forms.details.TaskViewPenddingHistoryForm;
import com.ngn.tdnv.task.forms.details.TaskViewRatingForm;
import com.ngn.tdnv.task.forms.details.TaskViewRedoHistoryForm;
import com.ngn.tdnv.task.forms.details.TaskViewRemindHistoryForm;
import com.ngn.tdnv.task.forms.details.TaskViewReverseForm;
import com.ngn.tdnv.task.models.TaskCompletedModel;
import com.ngn.tdnv.task.models.TaskEventModel;
import com.ngn.tdnv.task.models.TaskOutputModel;
import com.ngn.tdnv.task.models.TaskRateModel;
import com.ngn.tdnv.task.models.TaskRemindModel;
import com.ngn.utils.LocalDateUtil;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.DialogTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style.AlignItems;
import com.vaadin.flow.dom.Style.Position;

public class ItemEventOfHistoryForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private VerticalLayout vLayout = new VerticalLayout();

	
	private TaskEventModel taskEventModel;
	private TaskOutputModel taskOutputModel;
	public ItemEventOfHistoryForm(TaskEventModel taskEventModel,TaskOutputModel taskOutputModel) {
		this.taskEventModel = taskEventModel;
		this.taskOutputModel = taskOutputModel;
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
	
	private void createLayout() {
		vLayout.removeAll();
		
		TaskEventModel eventModel = taskEventModel;
		
		HorizontalLayout hLayoutDate = new HorizontalLayout();
		hLayoutDate.getStyle().setPaddingLeft("40px").setPosition(Position.RELATIVE);
		hLayoutDate.addClassName("item_event_of_history");
		
		Span spTime = new Span("Ngày: "+taskEventModel.getCreateTimeText());
		spTime.getStyle().setBackground("#d0d0d0").setPadding("0 30px");
		hLayoutDate.add(spTime);
		
		VerticalLayout vLayoutContent1 = new VerticalLayout();
		vLayoutContent1.setWidthFull();
		vLayoutContent1.getStyle().setBorderLeft("1px solid #cbcbcb").setPadding("10px");
		
		vLayout.add(hLayoutDate,vLayoutContent1);
		
		VerticalLayout vLayoutContent = new VerticalLayout();
		vLayoutContent1.add(vLayoutContent);
		vLayoutContent.setWidthFull();
		vLayoutContent.getStyle().setBoxShadow("rgba(0, 0, 0, 0.16) 0px 1px 4px").setPadding("10px");
		
		
		HorizontalLayout hLayoutButton = new HorizontalLayout();
		hLayoutButton.setWidthFull();
		hLayoutButton.getStyle().setBorderTop("1px solid #0000001a").setMarginTop("auto");
		
		ButtonTemplate btnView = new ButtonTemplate("Xem tiến độ",FontAwesome.Solid.EYE.create());
		btnView.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		
		hLayoutButton.add(btnView);
		
		HorizontalLayout hLayoutHeader = new HorizontalLayout();
		hLayoutHeader.setWidthFull();
		hLayoutHeader.getStyle().setAlignItems(AlignItems.CENTER).setBorderBottom("1px solid #90c2ffad").setPaddingBottom("10px");
		
		H4 spanHeader;
		
		String width = "150px";
		String widthSpan = "auto";
		
		switch(eventModel.getAction()) {
		case "taonhiemvu":
			spanHeader = new H4(eventModel.getCreator().getTextDisplay()+" đã tạo nhiệm vụ mới.");
			spanHeader.getStyle().setFontWeight(600);
			spanHeader.setWidth(widthSpan);
			
			hLayoutHeader.add(FontAwesome.Solid.EDIT.create(),spanHeader);

			vLayoutContent.add(hLayoutHeader);
			eventModel.getDescriptions().forEach(model->{
				vLayoutContent.add(createLayoutKeyValue(model.getName()+":", model.getDescription(), width));
			});
			vLayoutContent.add(createLayoutKeyValue("Đơn vị thực hiện: ", eventModel.getCreator().getOrganizationName(), width),
					createLayoutKeyValue("Người thao tác: ", eventModel.getCreator().getOrganizationUserName().toString(), width));

			break;

		case "thuchiennhiemvu":
			spanHeader = new H4(eventModel.getCreator().getTextDisplay() +" "+ eventModel.getTitle()+".");
			spanHeader.getStyle().setFontWeight(600);
			spanHeader.setWidth(widthSpan);
			hLayoutHeader.add(FontAwesome.Solid.CIRCLE_PLAY.create(),spanHeader);
			
			vLayoutContent.add(hLayoutHeader);
			vLayoutContent.add(createLayoutKeyValue("Ngày thực hiện: ", eventModel.getCreateTimeText(), width),
					createLayoutKeyValue("Đơn vị thực hiện: ", eventModel.getCreator().getOrganizationName(), width),
					createLayoutKeyValue("Người thao tác: ", eventModel.getCreator().getOrganizationUserName().toString(), width));
			break;
			
			
		case "capnhattiendonhiemvu":
			spanHeader = new H4(eventModel.getCreator().getTextDisplay() + " " + eventModel.getTitle()+".");
			spanHeader.getStyle().setFontWeight(600);
			spanHeader.setWidth(widthSpan);
			
			hLayoutHeader.add(FontAwesome.Solid.BARS_PROGRESS.create(),spanHeader);
			
			vLayoutContent.add(hLayoutHeader);

			eventModel.getDescriptions().forEach(model->{
				vLayoutContent.add(createLayoutKeyValue(model.getName()+":", model.getDescription(), width));
			});
			
			vLayoutContent.add(createLayoutKeyValue("Đơn vị thực hiện: ", eventModel.getCreator().getOrganizationName(), width),
					createLayoutKeyValue("Người thao tác: ", eventModel.getCreator().getOrganizationUserName().toString(), width));
			
			btnView.setText("Xem lịch sử tiến độ");
			btnView.addClickListener(e->openDialogViewHistory(taskOutputModel.getId()));
			
			vLayoutContent.add(hLayoutButton);
			
			break;
		case "traodoiykiennhiemvu":
			spanHeader = new H4(eventModel.getCreator().getTextDisplay()+" " + eventModel.getTitle()+".");
			spanHeader.getStyle().setFontWeight(600);
			spanHeader.setWidth(widthSpan);
			
			hLayoutHeader.add(FontAwesome.Solid.COMMENT.create(),spanHeader);
			
			
			vLayoutContent.add(hLayoutHeader);
			eventModel.getDescriptions().forEach(model->{
				vLayoutContent.add(createLayoutKeyValue(model.getName()+":", model.getDescription(), width));
			});
			
			vLayoutContent.add(createLayoutKeyValue("Đơn vị thực hiện: ", eventModel.getCreator().getOrganizationName(), width),
					createLayoutKeyValue("Người thao tác: ", eventModel.getCreator().getOrganizationUserName().toString(), width));
			
			btnView.setText("Xem lịch sử trao đổi");
			btnView.addClickListener(e->openDialogComment(taskOutputModel.getId()));
			
			vLayoutContent.add(hLayoutButton);
			
			break;

		case "traloitraodoiykiennhiemvu":
			spanHeader = new H4(eventModel.getCreator().getTextDisplay()+" " + eventModel.getTitle()+".");
			spanHeader.getStyle().setFontWeight(600);
			spanHeader.setWidth(widthSpan);
			
			hLayoutHeader.add(FontAwesome.Solid.COMMENT_DOTS.create(),spanHeader);

			vLayoutContent.add(hLayoutHeader);
			
			eventModel.getDescriptions().forEach(model->{
				vLayoutContent.add(createLayoutKeyValue(model.getName()+":", model.getDescription(), width));
			});

			vLayoutContent.add(createLayoutKeyValue("Đơn vị thực hiện: ", eventModel.getCreator().getOrganizationName(), width),
					createLayoutKeyValue("Người thao tác: ", eventModel.getCreator().getOrganizationUserName().toString(), width));
			
			btnView.setText("Xem lịch sử trao đổi");
			btnView.addClickListener(e->openDialogComment(taskOutputModel.getId()));
			
			vLayoutContent.add(hLayoutButton);
			
			break;

		case "donvihotrophancanbohotro":
			spanHeader = new H4(eventModel.getTitle()+".");
			spanHeader.getStyle().setFontWeight(600);
			spanHeader.setWidth(widthSpan);
			
			hLayoutHeader.add(FontAwesome.Solid.USER_FRIENDS.create(),spanHeader);
			vLayoutContent.add(hLayoutHeader);
			
			eventModel.getDescriptions().forEach(model->{
				vLayoutContent.add(createLayoutKeyValue(model.getName(), model.getDescription(), width));
			});
			
			vLayoutContent.add(createLayoutKeyValue("Đơn vị thực hiện: ", eventModel.getCreator().getOrganizationName(), width),
					createLayoutKeyValue("Người thao tác: ", eventModel.getCreator().getOrganizationUserName().toString(), width));
			
			break;

		case "donvixulyphancanboxuly":
			spanHeader = new H4(eventModel.getTitle()+".");
			spanHeader.getStyle().setFontWeight(600);
			//.add("badge contrast");
			spanHeader.setWidth(widthSpan);
			
			hLayoutHeader.add(FontAwesome.Solid.USER_FRIENDS.create(),spanHeader);
			
			eventModel.getDescriptions().forEach(model->{
				vLayoutContent.add(createLayoutKeyValue(model.getName()+":", model.getDescription(), width));
			});
			
			vLayoutContent.add(createLayoutKeyValue("Đơn vị thực hiện: ", eventModel.getCreator().getOrganizationName(), width),
					createLayoutKeyValue("Người thao tác: ", eventModel.getCreator().getOrganizationUserName().toString(), width));
			
			break;

		case "nhacnhothuchiennhiemvu":
			spanHeader = new H4(eventModel.getTitle()+".");
			spanHeader.getStyle().setFontWeight(600);
			//.add("badge contrast");
			spanHeader.setWidth(widthSpan);
			
			hLayoutHeader.add(FontAwesome.Solid.BELL.create(),spanHeader);
			vLayoutContent.add(hLayoutHeader);
			
			eventModel.getDescriptions().forEach(model->{
				vLayoutContent.add(createLayoutKeyValue(model.getName()+":", model.getDescription(), width));
			});
			
			vLayoutContent.add(createLayoutKeyValue("Đơn vị thực hiện: ", eventModel.getCreator().getOrganizationName(), width),
					createLayoutKeyValue("Người thao tác: ", eventModel.getCreator().getOrganizationUserName().toString(), width));
			
			btnView.setText("Xem lịch sử nhắc nhở");
			btnView.addClickListener(e->openDialogViewHistoryRemid(taskOutputModel.getReminds()));
			
			vLayoutContent.add(hLayoutButton);
			
			break;

		case "trieuhoinhiemvu":
			spanHeader = new H4(eventModel.getTitle()+".");
			spanHeader.getStyle().setFontWeight(600);
			//.add("badge error");
			spanHeader.setWidth(widthSpan);
			
			hLayoutHeader.add(FontAwesome.Solid.UNDO.create(),spanHeader);
			vLayoutContent.add(hLayoutHeader);
			
			eventModel.getDescriptions().forEach(model->{
				vLayoutContent.add(createLayoutKeyValue(model.getName()+":", model.getDescription(), width));
			});
			
			vLayoutContent.add(
					createLayoutKeyValue("Đơn vị triệu hồi: ", eventModel.getCreator().getOrganizationName(), width),
					createLayoutKeyValue("Người triệu hồi: ", eventModel.getCreator().getOrganizationUserName().toString(), width));
			
			vLayoutContent.add(createLayoutKeyValue("Đơn vị thực hiện: ", eventModel.getCreator().getOrganizationName(), width),
					createLayoutKeyValue("Người thao tác: ", eventModel.getCreator().getOrganizationUserName().toString(), width));
			
			
			btnView.setText("Xem lịch sử triệu hồi");
			btnView.addClickListener(e->openDialogReverse(taskOutputModel.getReverse()));
			
			vLayoutContent.add(hLayoutButton);
			
			break;

		case "hoanthanhnhiemvu":
			spanHeader = new H4(eventModel.getTitle()+".");
			spanHeader.getStyle().setFontWeight(600);
			//.add("badge success");
			spanHeader.setWidth(widthSpan);
			
			hLayoutHeader.add(FontAwesome.Solid.UNDO.create(),spanHeader);
			vLayoutContent.add(hLayoutHeader);
			
			vLayoutContent.add(createLayoutKeyValue("Đơn vị hoàn thành:", eventModel.getCreator().getOrganizationName(), width)
					,createLayoutKeyValue("Người thực hiện: ", eventModel.getCreator().getOrganizationUserName().toString(), width));
			
			vLayoutContent.add(createLayoutKeyValue("Đơn vị thực hiện: ", eventModel.getCreator().getOrganizationName(), width),
					createLayoutKeyValue("Người thao tác: ", eventModel.getCreator().getOrganizationUserName().toString(), width));

			btnView.setText("Xem lịch sử hoàn thành");
			vLayoutContent.add(hLayoutButton);
			
			break;

		case "tamhoanthuchien":
			spanHeader = new H4(eventModel.getTitle()+".");
			spanHeader.getStyle().setFontWeight(600);
			//.add("badge error");
			spanHeader.setWidth(widthSpan);
			
			hLayoutHeader.add(FontAwesome.Solid.CIRCLE_STOP.create(),spanHeader);
			vLayoutContent.add(hLayoutHeader);
			
			eventModel.getDescriptions().forEach(model->{
				vLayoutContent.add(createLayoutKeyValue(model.getName()+":", model.getDescription(), width));
			});
			
			vLayoutContent.add(createLayoutKeyValue("Đơn vị tạm hoãn: ", eventModel.getCreator().getOrganizationName(), width),
					createLayoutKeyValue("Người tạm hoãn: ", eventModel.getCreator().getOrganizationUserName().toString(), width));
			
			btnView.setText("Xem lịch sử tạm hoãn");
			btnView.addClickListener(e->openDialogPedding(taskOutputModel.getPendingHistories()));
			
			vLayoutContent.add(hLayoutButton);
			
			break;

		case "tieptucthuchien":
			spanHeader = new H4(eventModel.getTitle()+".");
			spanHeader.getStyle().setFontWeight(600);
			//.add("badge success");
			spanHeader.setWidth(widthSpan);
			
			hLayoutHeader.add(FontAwesome.Solid.CIRCLE_PLAY.create(),spanHeader);
			vLayoutContent.add(hLayoutHeader);
			
			vLayoutContent.add(createLayoutKeyValue("Đơn vị thực hiện: ", eventModel.getCreator().getOrganizationName(), width),
					createLayoutKeyValue("Người thao tác: ", eventModel.getCreator().getOrganizationUserName().toString(), width));
			
			break;

		case "capnhatnhiemvu":
			spanHeader = new H4(eventModel.getCreator().getOrganizationUserName()+ " đã cập nhật nhiệm vụ.");
			spanHeader.getStyle().setFontWeight(600);
			//.add("badge");
			spanHeader.setWidth(widthSpan);
			
			hLayoutHeader.add(FontAwesome.Solid.EDIT.create(),spanHeader);
			vLayoutContent.add(hLayoutHeader);
			
			eventModel.getDescriptions().forEach(model->{
				vLayoutContent.add(createLayoutKeyValue(model.getName()+":", model.getDescription(), width));
			});
			
			vLayoutContent.add(createLayoutKeyValue("Đơn vị thực hiện: ", eventModel.getCreator().getOrganizationName(), width),
					createLayoutKeyValue("Người thao tác: ", eventModel.getCreator().getOrganizationUserName().toString(), width));
			
			
			break;
			
		case "xacnhanhoanthanh":
			spanHeader = new H4(eventModel.getCreator().getOrganizationUserName()+" "+ eventModel.getTitle()+".");
			spanHeader.getStyle().setFontWeight(600);
			//.add("badge success");
			spanHeader.setWidth(widthSpan);
			
			hLayoutHeader.add(FontAwesome.Solid.CIRCLE_CHECK.create(),spanHeader);
			vLayoutContent.add(hLayoutHeader);
			
			vLayoutContent.add(createLayoutKeyValue("Thời gian thực hiện: ", LocalDateUtil.dfDateTime.format(eventModel.getCreatedTime()), width));
			
			vLayoutContent.add(createLayoutKeyValue("Đơn vị thực hiện: ", eventModel.getCreator().getOrganizationName(), width),
					createLayoutKeyValue("Người thao tác: ", eventModel.getCreator().getOrganizationUserName().toString(), width));
			
			btnView.setText("Xem thông tin hoàn thành");
			btnView.addClickListener(e->openDialogComplete(taskOutputModel.getCompleted()));
			
			vLayoutContent.add(hLayoutButton);
			break;
			
		case "danhgianhiemvu":
			spanHeader = new H4(eventModel.getCreator().getOrganizationUserName()+" "+ eventModel.getTitle()+".");
			spanHeader.getStyle().setFontWeight(600);
			//.add("badge success");
			spanHeader.setWidth(widthSpan);
			
			hLayoutHeader.add(FontAwesome.Solid.STAR.create(),spanHeader);
			vLayoutContent.add(hLayoutHeader);
			
			vLayoutContent.add(createLayoutKeyValue("Đơn vị thực hiện: ", eventModel.getCreator().getOrganizationName(), width),
					createLayoutKeyValue("Người thao tác: ", eventModel.getCreator().getOrganizationUserName().toString(), width));
			
			btnView.setText("Xem lịch sử đánh giá");
			btnView.addClickListener(e->openDialogRate(taskOutputModel.getRating()));
			
			vLayoutContent.add(hLayoutButton);
			
			break;
			
		case "thuchienlainhiemvu":
			spanHeader = new H4(eventModel.getCreator().getOrganizationUserName()+" "+ eventModel.getTitle()+".");
			spanHeader.getStyle().setFontWeight(600);
			//.add("badge error");
			spanHeader.setWidth(widthSpan);
			
			hLayoutHeader.add(FontAwesome.Solid.REDO.create(),spanHeader);
			vLayoutContent.add(hLayoutHeader);
			
			vLayoutContent.add(createLayoutKeyValue("Lý do: ", eventModel.getDescriptions().get(0).getDescription(), width));
			
			vLayoutContent.add(createLayoutKeyValue("Đơn vị thực hiện: ", eventModel.getCreator().getOrganizationName(), width),
					createLayoutKeyValue("Người thao tác: ", eventModel.getCreator().getOrganizationUserName().toString(), width));
			
			btnView.setText("Xem lịch sử thực hiện lại");
			btnView.addClickListener(e->openDialogRedo(taskOutputModel.getRedoHistories()));
			
			vLayoutContent.add(hLayoutButton);
			
			break;
			
		case "tuchoixacnhan":
			spanHeader = new H4(eventModel.getCreator().getOrganizationUserName()+" "+ eventModel.getTitle());
			spanHeader.getStyle().setFontWeight(600);
			//.add("badge error");
			spanHeader.setWidth(widthSpan);
			
			hLayoutHeader.add(FontAwesome.Solid.XMARK_CIRCLE.create(),spanHeader);
			vLayoutContent.add(hLayoutHeader);
			
			vLayoutContent.add(createLayoutKeyValue("Nội dung: ", "Đơn vị giao đã từ chối xác nhận báo cáo từ đơn vị xử lý", width));
			vLayoutContent.add(createLayoutKeyValue("Đơn vị thực hiện: ", eventModel.getCreator().getOrganizationName(), width),
					createLayoutKeyValue("Người thao tác: ", eventModel.getCreator().getOrganizationUserName().toString(), width));
			
			btnView.setText("Xem lịch sử từ chối xác nhận");
			vLayoutContent.add(hLayoutButton);
			
			break;
		case "tuchoithuchien":
			spanHeader = new H4(eventModel.getCreator().getTextDisplay()+" đã từ chối xử lý nhiệm vụ.");
			spanHeader.getStyle().setFontWeight(600);
			spanHeader.setWidth(widthSpan);
			
			hLayoutHeader.add(FontAwesome.Solid.REMOVE.create(),spanHeader);
			vLayoutContent.add(hLayoutHeader);
			
			
			vLayoutContent.add(spanHeader,new Hr(),
					createLayoutKeyValue("Lý do: ", eventModel.getDescriptions().get(0).getDescription(), width));
			vLayoutContent.add(createLayoutKeyValue("Đơn vị thực hiện: ", eventModel.getCreator().getOrganizationName(), width),
					createLayoutKeyValue("Người thao tác: ", eventModel.getCreator().getOrganizationUserName().toString(), width));
			
			btnView.setText("Xem thông tin từ chối thực hiện");
			vLayoutContent.add(hLayoutButton);
			
			break;
		}
	}
	
	private void openDialogViewHistoryRemid(List<TaskRemindModel> listReminds) {
		DialogTemplate dialogTemplate = new DialogTemplate("LỊCH SỬ NHẮC NHỞ");
		
		TaskViewRemindHistoryForm taskViewRemindHistoryForm = new TaskViewRemindHistoryForm(listReminds);
		dialogTemplate.add(taskViewRemindHistoryForm);
		
		dialogTemplate.setWidth("60%");
		dialogTemplate.setHeightFull();
		
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setLayoutMobile();
		dialogTemplate.open();
		
	}
	
	private void openDialogViewHistory(String idTask) {
		DialogTemplate dialogTemplate = new DialogTemplate("LỊCH SỬ TIẾN CẬP NHẬT TIẾN ĐỘ");
		TaskProgressHistoryForm taskProgressHistoryForm = new TaskProgressHistoryForm(idTask);
		dialogTemplate.add(taskProgressHistoryForm);
		
		dialogTemplate.setWidth("70%");
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setLayoutMobile();
		dialogTemplate.open();
	}
	
	private void openDialogComment(String idTask) {
		DialogTemplate dialogTemplate = new DialogTemplate("TRAO ĐỔI Ý KIẾN CỦA NHIỆM VỤ");
		dialogTemplate.setWidth("70%");
		dialogTemplate.setHeightFull();

		TaskCommentForm taskViewCommandsForm = new TaskCommentForm(idTask);
		taskViewCommandsForm.loadData(taskOutputModel);
		taskViewCommandsForm.addChangeListener(e->{

		});
		dialogTemplate.add(taskViewCommandsForm);
		dialogTemplate.setLayoutMobile();
		dialogTemplate.getFooter().removeAll();

		dialogTemplate.open();
	}
	
	private void openDialogComplete(TaskCompletedModel taskCompleted) {
		DialogTemplate dialogTemplate = new DialogTemplate("THÔNG TIN HOÀN THÀNH");
		dialogTemplate.setWidth("70%");
		
		TaskViewCompletedForm taskViewCompletedForm = new TaskViewCompletedForm(taskCompleted);

		dialogTemplate.add(taskViewCompletedForm);
		dialogTemplate.setLayoutMobile();
		dialogTemplate.getFooter().removeAll();

		dialogTemplate.open();
	}
	
	private void openDialogRate(TaskRateModel taskRateModel) {
		DialogTemplate dialogTemplate = new DialogTemplate("LỊCH SỬ ĐÁNH GIÁ");
		dialogTemplate.setWidth("70%");
		
		TaskViewRatingForm taskViewRate = new TaskViewRatingForm(taskRateModel);
		

		dialogTemplate.add(taskViewRate);
		dialogTemplate.setLayoutMobile();
		dialogTemplate.getFooter().removeAll();

		dialogTemplate.open();
	}
	
	
	private void openDialogPedding(List<ApiPedingModel> apiPedingModel) {
		DialogTemplate dialogTemplate = new DialogTemplate("LỊCH SỬ TẠM HOÃN");
		dialogTemplate.setWidth("70%");
		
		TaskViewPenddingHistoryForm taskViewPenddingForm = new TaskViewPenddingHistoryForm(apiPedingModel);

		dialogTemplate.add(taskViewPenddingForm);
		dialogTemplate.setLayoutMobile();
		dialogTemplate.getFooter().removeAll();

		dialogTemplate.open();
	}
	
	private void openDialogReverse(ApiTaskReverseModel apiTaskReverseModel) {
		DialogTemplate dialogTemplate = new DialogTemplate("LỊCH SỬ TRIỆU HỒI");
		dialogTemplate.setWidth("70%");
		
		TaskViewReverseForm taskViewReverseForm = new TaskViewReverseForm(apiTaskReverseModel);
 
		dialogTemplate.add(taskViewReverseForm);
		dialogTemplate.setLayoutMobile();
		dialogTemplate.getFooter().removeAll();

		dialogTemplate.open();
	}
	
	private void openDialogRedo(List<ApiTaskRedoModel> apiTaskRedo) {
		DialogTemplate dialogTemplate = new DialogTemplate("LỊCH SỬ YÊU CẦU LÀM LẠI");
		dialogTemplate.setWidth("70%");
		
		TaskViewRedoHistoryForm taskViewRodoHistory = new TaskViewRedoHistoryForm(apiTaskRedo);
 
		dialogTemplate.add(taskViewRodoHistory);
		dialogTemplate.setLayoutMobile();
		dialogTemplate.getFooter().removeAll();

		dialogTemplate.open();
	}
	
	private Component createLayoutKeyValue(String header,String content,String style) {
		HorizontalLayout hLayout = new HorizontalLayout();

		Span spanHeader = new Span(header);
		spanHeader.getStyle().setFontWeight(600);

		Span spanContent = new Span(content);

		if(style != null) {
			spanHeader.getStyle().setWidth(style).setFlexShrink("0");
		}

		hLayout.add(spanHeader,spanContent);


		return hLayout;
	}
	
	

}
