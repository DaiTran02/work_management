package com.ngn.tdnv.task.forms.details;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.flowingcode.vaadin.addons.fontawesome.FontAwesome.Solid.Icon;
import com.ngn.api.doc.ApiDocService;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tasks.ApiOutputTaskModel;
import com.ngn.api.tasks.ApiTaskService;
import com.ngn.api.utils.ApiKeyValueModel;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.tdnv.task.enums.StatusTaskEnum;
import com.ngn.tdnv.task.models.TaskOutputModel;
import com.ngn.tdnv.task.models.TaskProcessModel;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.ActtachmentForm;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.DialogTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style.AlignItems;
import com.vaadin.flow.dom.Style.Display;
import com.vaadin.flow.dom.Style.FlexDirection;

import lombok.Data;

//Dùng để xem các nhiệm vụ con khi mà đơn vị được giao giao tiếp nhiệm vụ con.
public class TaskListDetailChildrenForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private VerticalLayout vLayout = new VerticalLayout();
	private List<TaskOutputModel> listOutputTaskModels = new ArrayList<TaskOutputModel>();
	private List<Pair<String, String>> listPriority = new ArrayList<Pair<String,String>>();
	private BelongOrganizationModel belongOrganizationModel = SessionUtil.getOrg();
	
	private String parentIdOfTask;
	public TaskListDetailChildrenForm(String parentidOfTask) {
		this.parentIdOfTask = parentidOfTask;
		buildLayout();
		configComponent();
	}
	

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(vLayout);
		
	}

	@Override
	public void configComponent() {
		
	}
	
	public void loadData() {
		listOutputTaskModels = new ArrayList<TaskOutputModel>();
		ApiResultResponse<List<ApiOutputTaskModel>> data = ApiTaskService.getListSubTask(parentIdOfTask);
		if(data.isSuccess()) {
			data.getResult().stream().forEach(model->{
				listOutputTaskModels.add(new TaskOutputModel(model));
			});
		
		}
		
		listPriority = new ArrayList<Pair<String,String>>();
		ApiResultResponse<List<ApiKeyValueModel>> dataOfPriority = ApiDocService.getPriority();
		if(dataOfPriority.isSuccess()) {
			dataOfPriority.getResult().stream().forEach(model->{
				listPriority.add(Pair.of(model.getKey(),model.getName()));
			});
		}
		
		listOutputTaskModels.stream().forEach(model->{
			vLayout.add(createLayoutItem(model));
		});
	}
	
	private Component createLayoutItem(TaskOutputModel outputTaskModel) {
		VerticalLayout vLayout = new VerticalLayout();

		HorizontalLayout hLayoutHeader = new HorizontalLayout();
		Span spanOwner = new Span("Người chỉ đạo: "+outputTaskModel.getOwner().getOrganizationUserName());
		spanOwner.getElement().getThemeList().add("badge pill");


		Span spanAssignee = null;
		if(outputTaskModel.getAssignee().getOrganizationUserId() != null) {
			spanAssignee = new Span("Đơn vị xử lý: "+
				outputTaskModel.getAssignee().getOrganizationName()+ 
				" (" + outputTaskModel.getAssignee().getOrgUserName()+")");
		}else {
			spanAssignee = new Span("Đơn vị xử lý: "+outputTaskModel.getAssignee().getOrganizationName());
		}
		spanAssignee.getElement().getThemeList().add("badge success pill");

		Icon icon = FontAwesome.Solid.ARROW_RIGHT.create();
		icon.setSize("15px");
		icon.getStyle().setMarginTop("5px");

		Span spValueStatus = spanStatus(outputTaskModel.getStatus().toString());
		spValueStatus.getStyle().setMarginLeft("auto");

		hLayoutHeader.setWidthFull();
		hLayoutHeader.add(spanOwner,icon,spanAssignee,spValueStatus);
		hLayoutHeader.setPadding(false);
		hLayoutHeader.getStyle().setPaddingBottom("5px");


		VerticalLayout vLayoutContent = new VerticalLayout();
		vLayoutContent.setWidthFull();
		vLayoutContent.setPadding(false);
		vLayoutContent.getStyle().set("gap", "5px");


		Component componentTitle = createLayoutKeyValue("Tiêu đề: ", outputTaskModel.getTitle(), null,"52px");
		componentTitle.getStyle().setWidth("100%");
		vLayoutContent.add(componentTitle);

		HorizontalLayout hLayoutDate = new HorizontalLayout();
		hLayoutDate.setWidthFull();
		hLayoutDate.getStyle().setAlignItems(AlignItems.CENTER);



		hLayoutDate.add(createLayoutKeyValue("Ngày giao: ", outputTaskModel.getCreateTimeText(),null, "100px"),
				createLayoutKeyValue("Hạn xử lý: ", outputTaskModel.getEndTimeText(), null,"100px"),
				createLayoutKeyValue("Thời hạn còn lại: ", outputTaskModel.getCalculateTimeRemainText(), null,"200px"));

		vLayoutContent.add(hLayoutDate);

		//Don vi ho tro va theo doi

		HorizontalLayout vLayoutViewOrg = new HorizontalLayout();
		vLayoutViewOrg.setWidthFull();
		vLayoutViewOrg.setPadding(false);
		vLayoutViewOrg.getStyle().setPadding("0");

		//Xử lý sử kiện
		HorizontalLayout hLayoutEvent = new HorizontalLayout();
		hLayoutEvent.setWidthFull();

		Span spPriority = checkPriority(outputTaskModel);

		ButtonTemplate btnProgress = new ButtonTemplate(outputTaskModel.getProcesses().isEmpty() ? "0%" : outputTaskModel.getProcesses().get(0).getPercent()+"%",FontAwesome.Solid.BARS_PROGRESS.create());
		btnProgress.addThemeVariants(ButtonVariant.LUMO_SMALL);
		btnProgress.setTooltipText("Tiến độ thực hiện");
		btnProgress.addClickListener(e->{
			openDialogViewProgressTask(outputTaskModel.getId(),outputTaskModel);
		});

		ButtonTemplate btnAttachment = new ButtonTemplate(""+outputTaskModel.getAttachments().size(),FontAwesome.Solid.PAPERCLIP.create());
		btnAttachment.addThemeVariants(ButtonVariant.LUMO_SMALL);
		btnAttachment.setTooltipText("Đính kèm nhiệm vụ");
		btnAttachment.addClickListener(e->{
			openDialogViewAttachment(outputTaskModel);
		});

		ButtonTemplate btnDiscuss = new ButtonTemplate(""+outputTaskModel.getComments().size(),FontAwesome.Solid.MESSAGE.create());
		btnDiscuss.addThemeVariants(ButtonVariant.LUMO_SMALL);
		btnDiscuss.setTooltipText("Trao đổi nhiệm vụ");
		btnDiscuss.addClickListener(e->{
			openDialogComment(outputTaskModel.getId());
		});

		ButtonTemplate btnLog = new ButtonTemplate(""+outputTaskModel.getEvents().size(),FontAwesome.Solid.FILE_LINES.create());
		btnLog.addThemeVariants(ButtonVariant.LUMO_SMALL);
		btnLog.setTooltipText("Lịch sử nhiệm vụ");
		btnLog.addClickListener(e->{
			openDialogEvent(outputTaskModel.getId());
		});

		ButtonTemplate btnView = new ButtonTemplate("Xem chi tiết",FontAwesome.Solid.EYE.create());
		btnView.addThemeVariants(ButtonVariant.LUMO_SMALL);
		btnView.setTooltipText("Xem chi tiết nhiệm vụ");
		btnView.addClickListener(e->{
//			openDialogViewDetail(outputTaskModel.getId());
			openDialogViewDetailV2(outputTaskModel.getId());
		});


		hLayoutEvent.add(spPriority,btnProgress,btnAttachment,btnDiscuss,btnLog,btnView);
		hLayoutEvent.getStyle().setAlignItems(AlignItems.CENTER);

		vLayout.add(hLayoutHeader,vLayoutContent,hLayoutEvent);
		vLayout.setWidthFull();
		vLayout.getStyle().setBoxShadow("rgba(50, 50, 93, 0.25) 0px 2px 5px -1px, rgba(0, 0, 0, 0.3) 0px 1px 3px -1px").setPadding("5px").setBorderRadius("10px").setMarginBottom("8px");
		vLayout.setSpacing(false);

		//This className in task.css in line 181.
		vLayout.addClassName("layout-grid-task");

		return vLayout;
	}
	
	private Span checkPriority(TaskOutputModel outputTaskModel) {
		Span spPriority = new Span();
		for(Pair<String, String> p : listPriority) {
			if(p.getKey().equals(outputTaskModel.getPriority())) {
				spPriority.add(FontAwesome.Solid.FLAG.create(),new Span(p.getRight()));
				spPriority.getStyle().setDisplay(Display.FLEX).setFlexDirection(FlexDirection.ROW).setPadding("8px")
				.setFontSize("12px").setHeight("12px").setBorderRadius("2px").setBackground("#eaeff7").setColor("#173b70").set("gap", "5px");
				switch(outputTaskModel.getPriority()) {
				case "thuong":
					spPriority.getElement().setProperty("title", "Độ khẩn: Thường");
					break;
				case "khan":
					spPriority.getStyle().setBackground("#f5f7ea").setColor("#9eac0f");
					spPriority.getElement().setProperty("title", "Độ khẩn: Khẩn");
					break;
				case "hoatoc":
					spPriority.getStyle().setBackground("#f7eaea").setColor("#ac0f0f");
					spPriority.getElement().setProperty("title", "Độ khẩn: Hỏa tốc");
					break;
				}

			}
		}
		return spPriority;
	}
	
	
	private Component createLayoutKeyValue(String header,String content,String style,String width) {
		HorizontalLayout hLayout = new HorizontalLayout();
		hLayout.setSpacing(false);

		Span spanHeader = new Span(header);
		spanHeader.getStyle().setFontWeight(600);
		spanHeader.setWidth(width);

		Span spanContent = new Span(content);
		spanContent.getStyle().setPaddingLeft("5px");
		spanContent.setWidth("95%");
		spanContent.getElement().setProperty("title", content);
		//The className in the file task.css
		spanContent.addClassName("span--overflow");

		if(style != null) {
			spanContent.getElement().getThemeList().add(style);
		}

		hLayout.add(spanHeader,spanContent);
		hLayout.setSpacing(false);
		return hLayout;
	}
	
	
	private Span spanStatus(String status) {
		Span spStatus = new Span();
		spStatus.getElement().getThemeList().add("badge");
		ApiResultResponse<List<ApiKeyValueModel>> listStatus = ApiTaskService.getStatus();
		for(ApiKeyValueModel kstatus : listStatus.getResult()) {

			if(status.equals(kstatus.getKey())) {
				spStatus.add(kstatus.getName());
				break;
			}
		}

		//These class name in file task.css
		if(status.equals(StatusTaskEnum.CHUATHUCHIEN.getKey())) {
			spStatus.getElement().getThemeList().add("badge error");
		}else if(status.equals(StatusTaskEnum.DAHOANTHANH.getKey())) {
			spStatus.getElement().getThemeList().add("badge success");
		}else if(status.equals(StatusTaskEnum.DANGTHUCHIEN.getKey())) {
			spStatus.getElement().getThemeList().add("badge");
		}else if(status.equals(StatusTaskEnum.TAMHOAN.getKey())) {
			spStatus.getElement().getThemeList().add("badge error");
		}else if(status.equals(StatusTaskEnum.DAHOANTHANHQUAHAN.getKey())) {
			spStatus.addClassName("dahoanthanh_quahan");
		}else if(status.equals(StatusTaskEnum.THUCHIENLAI.getKey())) {
			spStatus.getElement().getThemeList().add("badge");
		}

		return spStatus;
	}
	
	@SuppressWarnings("unused")
	private void openDialogViewDetail(String idTask) {
		DialogTemplate dialogTemplate = new DialogTemplate("THÔNG TIN NHIỆM VỤ");

		TaskViewDetailForm taskViewDetailForm = new TaskViewDetailForm(idTask,belongOrganizationModel);
		taskViewDetailForm.setTypeOfTask(true, false, false, false);

		taskViewDetailForm.addChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});

		dialogTemplate.getFooter().removeAll();

		dialogTemplate.add(taskViewDetailForm);
		dialogTemplate.setSizeFull();


		dialogTemplate.open();
	}

	private void openDialogViewDetailV2(String idTask) {
		DialogTemplate dialogTemplate = new DialogTemplate("THÔNG TIN NHỆM VỤ");
		
		TaskViewDetailFormV2 taskViewDetailFormV2 = new TaskViewDetailFormV2(idTask,true, false, false, false);
		dialogTemplate.add(taskViewDetailFormV2);
		taskViewDetailFormV2.addChangeListener(e->{
			if(taskViewDetailFormV2.isCheckDeleteTask()) {
				dialogTemplate.close();
				refreshMainLayout();
			}
			fireEvent(new ClickEvent(this,false));
		});
		
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setSizeFull();
		dialogTemplate.open();
	}

	private void openDialogComment(String idTask) {
		DialogTemplate dialogTemplate = new DialogTemplate("TRAO ĐỔI Ý KIẾN CỦA NHIỆM VỤ");
		dialogTemplate.setWidth("70%");
		dialogTemplate.setHeightFull();

		TaskCommentForm taskViewCommandsForm = new TaskCommentForm(idTask);
		taskViewCommandsForm.addChangeListener(e->{

		});
		dialogTemplate.add(taskViewCommandsForm);

		dialogTemplate.getFooter().removeAll();

		dialogTemplate.open();

	}

	private void openDialogViewAttachment(TaskOutputModel outputTaskModel) {
		DialogTemplate dialogTemplate = new DialogTemplate("DANH SÁCH ĐÍNH KÈM");
		List<String> listAttachment = new ArrayList<String>();
		for(Object object : outputTaskModel.getAttachments()) {
			listAttachment.add(object.toString());
		}
		ActtachmentForm acttachmentForm = new ActtachmentForm(listAttachment, true);
		dialogTemplate.add(acttachmentForm);
		dialogTemplate.setWidth("60%");
		dialogTemplate.setHeightFull();
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.open();


	}

	private void openDialogViewProgressTask(String idTask,TaskOutputModel outputTaskModel) {
		TaskProcessModel taskProcessModel = outputTaskModel.getProcesses().isEmpty() ? new TaskProcessModel() : outputTaskModel.getProcesses().get(0);
		DialogTemplate dialogTemplate = new DialogTemplate("TIẾN ĐỘ NHIỆM VỤ");
		TaskProgressViewForm taskProgressViewForm = new TaskProgressViewForm(idTask,taskProcessModel,false,outputTaskModel.getState().toString(),true);
		dialogTemplate.add(taskProgressViewForm);
		dialogTemplate.getBtnClose().addClickListener(e->{
			if(taskProgressViewForm.isCheck()) {
				fireEvent(new ClickEvent(this,false));
			}
		});
		dialogTemplate.setWidth("70%");
		dialogTemplate.setHeight("40%");
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.open();	
	}

	private void openDialogEvent(String idTask) {
		DialogTemplate dialogTemplate = new DialogTemplate("NHẬT KÝ NHIỆM VỤ");
		TaskHistoryEventForm taskEventForm = new TaskHistoryEventForm(idTask);
		dialogTemplate.add(taskEventForm);
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setWidth("90%");
		dialogTemplate.setHeightFull();
		dialogTemplate.open();
	}
	
	@Data
	public class MultiSelectComboboxModel{
		private String name;
		
		public MultiSelectComboboxModel(String name){
			this.name = name;
		}
	}

}
