package com.ngn.tdnv.task.forms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.scheduling.annotation.Async;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.flowingcode.vaadin.addons.fontawesome.FontAwesome.Solid.Icon;
import com.ngn.api.doc.ApiDocService;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tags.ApiInputAddclassModel;
import com.ngn.api.tags.ApiTagFilterModel;
import com.ngn.api.tags.ApiTagModel;
import com.ngn.api.tags.ApiTagService;
import com.ngn.api.tasks.ApiOutputTaskModel;
import com.ngn.api.tasks.ApiTaskService;
import com.ngn.api.utils.ApiKeyValueModel;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.tdnv.doc.forms.DocOverviewForm;
import com.ngn.tdnv.tag.forms.TagForm;
import com.ngn.tdnv.task.enums.StatusTaskEnum;
import com.ngn.tdnv.task.forms.details.TaskCommentForm;
import com.ngn.tdnv.task.forms.details.TaskHistoryEventFormV2;
import com.ngn.tdnv.task.forms.details.TaskProgressViewForm;
import com.ngn.tdnv.task.forms.details.TaskViewDetailForm;
import com.ngn.tdnv.task.forms.details.TaskViewDetailFormV2;
import com.ngn.tdnv.task.forms.details.TaskViewFollowersForm;
import com.ngn.tdnv.task.forms.details.TaskViewOrgSupportsForm;
import com.ngn.tdnv.task.models.FollowerModel;
import com.ngn.tdnv.task.models.TaskOrgGeneralModel;
import com.ngn.tdnv.task.models.TaskOutputModel;
import com.ngn.tdnv.task.models.TaskProcessModel;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.ActtachmentForm;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.DialogTemplate;
import com.ngn.utils.components.NotificationTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.Style.AlignItems;
import com.vaadin.flow.dom.Style.Display;
import com.vaadin.flow.dom.Style.FlexDirection;
import com.vaadin.flow.dom.Style.Overflow;

import lombok.Data;

public class LazyLoadListTaskForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private boolean checkMobile = false;

	private List<Pair<String, String>> listPriority = new ArrayList<Pair<String,String>>();
	private List<TaskOutputModel> listTask = new ArrayList<TaskOutputModel>();
	private BelongOrganizationModel belongOrganizationModel = SessionUtil.getOrg();;
	private UserAuthenticationModel userAuthenticationModel = SessionUtil.getUser();
	private List<ApiTagModel> listTags = new ArrayList<ApiTagModel>();
	private boolean checkTaskOwner;
	private boolean checkTaskAssignee;
	private boolean checkTaskSupport;
	private boolean checkTaskFollow;

	private List<ApiKeyValueModel> listStatus = new ArrayList<ApiKeyValueModel>();
	
	private VirtualList<TaskOutputModel> lazyList = new VirtualList<TaskOutputModel>();

	/**
	 * Instantiates a new task list form.
	 * One form task to all task.
	 * @param checkTaskOwner the check task owner
	 * @param checkTaskAssignee the check task assignee
	 * @param checkTaskSupport the check task support
	 * @param checkTaskFollow the check task follow
	 */
	private Map<String, List<String>> parametters;
	public LazyLoadListTaskForm(boolean checkTaskOwner,boolean checkTaskAssignee,boolean checkTaskSupport,boolean checkTaskFollow,Map<String, List<String>> parametters) {
		this.checkTaskOwner = checkTaskOwner;
		this.checkTaskAssignee = checkTaskAssignee;
		this.checkTaskSupport = checkTaskSupport;
		this.checkTaskFollow = checkTaskFollow;
		loadDataTags();
		buildLayout();
		configComponent();
		//		loadData();
		this.parametters = parametters;
		UI.getCurrent().getPage().retrieveExtendedClientDetails(e->{
			if(e.getScreenWidth() < 768) {
				checkMobile = true;
			}
		});
		checkParametter();
		loadHardData();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(lazyList);
	}

	@Override
	public void configComponent() {

	}

	public void loadData() {
		int numThreads = Math.min(listTask.size(), Runtime.getRuntime().availableProcessors());
		numThreads = numThreads == 0 ? 1 :  Math.min(listTask.size(), Runtime.getRuntime().availableProcessors());

		lazyList.setItems(listTask);
		lazyList.setRenderer(checkMobile == true ? itemTaskMobile : itemTaskRender);
		
	}


	private void loadHardData() {
		ApiResultResponse<List<ApiKeyValueModel>> data = ApiDocService.getPriority();
		listPriority = new ArrayList<Pair<String,String>>();
		if(data.isSuccess()) {
			data.getResult().stream().forEach(model->{
				listPriority.add(Pair.of(model.getKey(),model.getName()));
			});
			refreshMainLayout();
		}


		listStatus = new ArrayList<ApiKeyValueModel>();
		ApiResultResponse<List<ApiKeyValueModel>> listApiStatus = ApiTaskService.getStatus();
		listStatus.addAll(listApiStatus.getResult());
	}

	private void checkParametter() {
		if(parametters != null) {
			if(parametters.containsKey("detail")) {
				String idTask = parametters.get("detail").get(0);
				if(checkIfTaskInTheSystem(idTask)) {
					openDialogViewDetail(idTask);
				}else {
					NotificationTemplate.error("Nhiệm vụ không tồn tại trong hệ thống.");
				}

			}
		}
	}

	@Async
	private boolean checkIfTaskInTheSystem(String idTask) {
		ApiResultResponse<ApiOutputTaskModel> data = ApiTaskService.getAtask(idTask);
		if(data.isSuccess()) {
			return true;
		}
		return false;
	}
	
	private ComponentRenderer<Component, TaskOutputModel> itemTaskRender = new ComponentRenderer<>(
			outputTaskModel ->{
				VerticalLayout vLayout = new VerticalLayout();

				HorizontalLayout hLayoutHeader = new HorizontalLayout();

				// Don vi giao
				Span spanOwner = new Span();
				spanOwner.getElement().getThemeList().add("badge pill");

				spanOwner.getElement().setProperty("title", "Đơn vị giao: "+outputTaskModel.getOwner().getOrganizationName() +" (Người chỉ đạo: "+outputTaskModel.getOwner().getOrganizationUserName()+")");

				spanOwner.setText("Đơn vị giao: "+outputTaskModel.getOwner().getOrganizationName() +" ("+outputTaskModel.getOwner().getOrganizationUserName()+")");

				spanOwner.getStyle().setOverflow(Overflow.HIDDEN).set("text-overflow", "ellipsis")
				.set("white-space", "nowrap").set("max-width", "400px").set("display", "block");

				// Don vi xu ly
				Span spanAssignee = null;
				if(outputTaskModel.getAssignee().getOrganizationUserId() != null) {
					spanAssignee = new Span("Đơn vị xử lý: "+outputTaskModel.getAssignee().getOrganizationName()+ " (" + outputTaskModel.getAssignee().getOrganizationUserName().toString()+")");
					spanAssignee.getElement().setProperty("title", outputTaskModel.getAssignee().getOrganizationName()+ " (" + outputTaskModel.getAssignee().getOrganizationUserName().toString()+")");
				}else {
					spanAssignee = new Span("Đơn vị xử lý: "+outputTaskModel.getAssignee().getOrganizationName());
					spanAssignee.getElement().setProperty("title", "Đơn vị xử lý: "+outputTaskModel.getAssignee().getOrganizationName());
				}
				spanAssignee.getElement().getThemeList().add("badge success pill");
				spanAssignee.getStyle().setOverflow(Overflow.HIDDEN).set("text-overflow", "ellipsis")
				.set("white-space", "nowrap").set("max-width", "600px").set("display", "block");

				Icon icon = FontAwesome.Solid.ARROW_RIGHT.create();
				icon.setSize("15px");
				icon.getStyle().setMarginTop("5px");

				//Status
				Span spValueStatus = spanStatus(outputTaskModel.getStatus().toString(),false);
				spValueStatus.getStyle().setMarginLeft("auto");

				hLayoutHeader.setWidthFull();
				hLayoutHeader.add(spanOwner,icon,spanAssignee,spValueStatus);
				hLayoutHeader.setPadding(false);
				hLayoutHeader.getStyle().setPaddingBottom("5px");


				//Content
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

				String sourceText = outputTaskModel.getDocId() == null ? "Tự phát" : "Văn bản";
				
				HorizontalLayout hLayoutSource = new HorizontalLayout();
				Span spHeader = new Span("Nguồn: ");
				spHeader.getStyle().setFontWeight(600);
				
				hLayoutSource.setPadding(false);
				hLayoutSource.getStyle().setAlignItems(AlignItems.CENTER);
				
				if(outputTaskModel.getDocId() != null) {
					ButtonTemplate btnViewDoc = new ButtonTemplate("Văn bản");
					btnViewDoc.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
					btnViewDoc.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
					hLayoutSource.add(spHeader,btnViewDoc);
					btnViewDoc.addClickListener(e->{
						openDialogOverViewDoc(outputTaskModel.getDocId(),true);
					});
				}else {
					hLayoutSource.add(spHeader,new Span(sourceText));
				}

				hLayoutDate.add(createLayoutKeyValue("Ngày giao: ", outputTaskModel.getCreateTimeText(),null, "100px"),
						createLayoutKeyValue("Hạn xử lý: ", outputTaskModel.getEndTimeText(), null,"100px"),
						createLayoutKeyValue("Thời hạn còn lại: ", outputTaskModel.getCalculateTimeRemainText(), null,"200px"),
						hLayoutSource);

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

				ButtonTemplate btnSupports = new ButtonTemplate("Đơn vị hỗ trợ ("+outputTaskModel.getSupports().size()+")");
				btnSupports.addThemeVariants(ButtonVariant.LUMO_SMALL);

				btnSupports.addClickListener(e->{
					openPopupSupports(outputTaskModel.getSupports());
				});

				ButtonTemplate btnFollower = new ButtonTemplate("Khối theo dõi ("+outputTaskModel.getCountFollowerText()+")");
				btnFollower.addThemeVariants(ButtonVariant.LUMO_SMALL);

				btnFollower.addClickListener(e->{
					openDialogFollowers(outputTaskModel.getFollowers() == null ? Collections.emptyList() : outputTaskModel.getFollowers());
				});
				
				
				ButtonTemplate btnTags = new ButtonTemplate("Phân loại",FontAwesome.Solid.TAGS.create());
				btnTags.addClickListener(e->{
					openDialogChooseTag(null,outputTaskModel.getId());
				});
				
				ApiTagModel tag = findTagOfDoc(outputTaskModel.getId());
				if(tag != null) {
					btnTags = new ButtonTemplate(tag.getName(),FontAwesome.Solid.TAGS.create());
					btnTags.getStyle().setColor(tag.getColor());
					btnTags.addClickListener(e->{
						openDialogChooseTag(tag.getId(),outputTaskModel.getId());
					});
				}
				

				hLayoutEvent.add(spPriority,btnProgress,btnAttachment,btnDiscuss,btnLog,btnSupports,btnFollower,btnView,btnTags);
				
				hLayoutEvent.getStyle().setAlignItems(AlignItems.CENTER);

				if(outputTaskModel.getParentId() != null) {
					Span spChild = new Span("Nhiệm vụ con");
					spChild.getElement().getThemeList().add("badge");
					spChild.getStyle().setMarginLeft("auto");
					hLayoutEvent.add(spChild);
				}

				vLayout.add(hLayoutHeader,vLayoutContent,hLayoutEvent);
				vLayout.setWidthFull();
				vLayout.getStyle().setBoxShadow("rgba(50, 50, 93, 0.25) 0px 2px 5px -1px, rgba(0, 0, 0, 0.3) 0px 1px 3px -1px")
				.setPadding("10px").setBorderRadius("10px").setMarginBottom("8px");
				vLayout.setSpacing(false);

				//This className in task.css in line 181.
				vLayout.addClassName("layout-grid-task");

				return vLayout;
			}
	);


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

	private void openPopupSupports(List<TaskOrgGeneralModel> supports) {
		DialogTemplate dialogTemplate = new DialogTemplate("DANH SÁCH ĐƠN VỊ HỖ TRỢ THỰC HIỆN NHIỆM VỤ");
		TaskViewOrgSupportsForm taskViewOrgSupportsForm = new TaskViewOrgSupportsForm(supports);
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.open();
		dialogTemplate.setWidth("40%");
		dialogTemplate.setHeight("60%");
		dialogTemplate.setLayoutMobile();
		dialogTemplate.add(taskViewOrgSupportsForm);
	}

	private void openDialogFollowers(List<FollowerModel> followers) {
		DialogTemplate dialogTemplate = new DialogTemplate("DANH SÁCH KHỐI THEO DÕI NHIỆM VỤ");
		dialogTemplate.setHeight("60%");
		dialogTemplate.setWidth("40%");
		dialogTemplate.setLayoutMobile();

		TaskViewFollowersForm taskViewFollowersForm = new TaskViewFollowersForm(followers);
		dialogTemplate.add(taskViewFollowersForm);
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.open();
	}


	private Span spanStatus(String status,boolean checkMobile) {
		Span spStatus = new Span();
		spStatus.getElement().getThemeList().add("badge");
		
		
		for(ApiKeyValueModel kstatus : listStatus) {

			if(status.equals(kstatus.getKey())) {
				if(checkMobile) {
					spStatus.add(kstatus.getName());
				}else {
					spStatus.add("Tình trạng: "+kstatus.getName());
				}
				break;
			}
		}

		
		//These class name in file task.css
		if(StatusTaskEnum.toCheckChuaThucHien(status)) {
			spStatus.addClassName("span_chua_thuc_hien");
		}
		else if(StatusTaskEnum.toCheckDaHoanThanh(status)) {
			spStatus.addClassName("span_da_hoan_thanh");
		}
		else if(StatusTaskEnum.toCheckDangThucHien(status)) {
			spStatus.addClassName("dang_thuc_hien");
		}
		else if(status.equals(StatusTaskEnum.TAMHOAN.getKey())) {
			spStatus.addClassName("span_tam_hoan");
		}
		else if(status.equals(StatusTaskEnum.THUCHIENLAI.getKey())) {
			spStatus.addClassName("thuc_hien_lai");
		}
		else if(StatusTaskEnum.toCheckChoXacNhan(status)) {
			spStatus.addClassName("cho_xac_nhan");
		}
		else if(status.equals(StatusTaskEnum.TUCHOITHUCHIEN.getKey())) {
			spStatus.addClassName("span_tu_choi_nhiem_vu");
		}
		else if(status.equals(StatusTaskEnum.TUCHOIXACNHAN.getKey())) {
			spStatus.addClassName("tu_choi_xac_nhan");
		}
		return spStatus;
	}
	
	private void openDialogOverViewDoc(String idDoc,boolean isViewDoc) {
		DialogTemplate dialogTemplate = new DialogTemplate("Thông tin văn bản");

		DocOverviewForm docOverviewForm = new DocOverviewForm(idDoc, null, null,isViewDoc);
		dialogTemplate.setWidth("80%");
		dialogTemplate.setHeightFull();
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.add(docOverviewForm);
		dialogTemplate.open();
	}

	private void openDialogViewDetail(String idTask) {
		DialogTemplate dialogTemplate = new DialogTemplate("THÔNG TIN NHIỆM VỤ");

		TaskViewDetailForm taskViewDetailForm = new TaskViewDetailForm(idTask,belongOrganizationModel);
		taskViewDetailForm.setTypeOfTask(checkTaskOwner, checkTaskAssignee, checkTaskSupport, checkTaskFollow);
		taskViewDetailForm.addChangeListener(e->{
			if(taskViewDetailForm.isCheckDeleteTask()) {
				dialogTemplate.close();
				refreshMainLayout();
			}
			fireEvent(new ClickEvent(this,false));
		});

		dialogTemplate.getFooter().removeAll();

		dialogTemplate.add(taskViewDetailForm);
		dialogTemplate.setSizeFull();
		dialogTemplate.setLayoutMobile();

		dialogTemplate.open();
	}

	private void openDialogViewDetailV2(String idTask) {
		DialogTemplate dialogTemplate = new DialogTemplate("THÔNG TIN NHỆM VỤ");

		TaskViewDetailFormV2 taskViewDetailFormV2 = new TaskViewDetailFormV2(idTask,checkTaskOwner, checkTaskAssignee, checkTaskSupport, checkTaskFollow);
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
		dialogTemplate.setLayoutMobile();
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
		dialogTemplate.setLayoutMobile();
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.open();


	}

	private void openDialogViewProgressTask(String idTask,TaskOutputModel outputTaskModel) {
		TaskProcessModel taskProcessModel = outputTaskModel.getProcesses().isEmpty() ? new TaskProcessModel() : outputTaskModel.getProcesses().get(0);
		DialogTemplate dialogTemplate = new DialogTemplate("TIẾN ĐỘ NHIỆM VỤ");
		TaskProgressViewForm taskProgressViewForm = new TaskProgressViewForm(idTask,taskProcessModel,checkTaskSupport,outputTaskModel.getState().toString(),true);
		dialogTemplate.add(taskProgressViewForm);
		dialogTemplate.getBtnClose().addClickListener(e->{
			if(taskProgressViewForm.isCheck()) {
				fireEvent(new ClickEvent(this,false));
			}
		});
		dialogTemplate.setWidth("70%");
		dialogTemplate.setHeight("40%");
		dialogTemplate.setLayoutMobile();
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.open();	
	}

	private void openDialogEvent(String idTask) {
		DialogTemplate dialogTemplate = new DialogTemplate("NHẬT KÝ NHIỆM VỤ");
		//		TaskHistoryEventForm taskEventForm = new TaskHistoryEventForm(idTask);
		TaskHistoryEventFormV2 taskEventForm = new TaskHistoryEventFormV2(idTask);
		dialogTemplate.add(taskEventForm);
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setWidth("60%");
		dialogTemplate.setHeightFull();
		dialogTemplate.setLayoutMobile();
		dialogTemplate.open();
	}

	public void setData(List<TaskOutputModel> listTask) {
		this.listTask = new ArrayList<TaskOutputModel>();
		this.listTask = listTask;
		loadData();
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
	
	
	private ComponentRenderer<Component, TaskOutputModel> itemTaskMobile = new ComponentRenderer<Component, TaskOutputModel>(outputTaskModel->{
		VerticalLayout vLayoutMobile = new VerticalLayout();

		HorizontalLayout hLayoutHeader = new HorizontalLayout();
		Span spanOwner = new Span("Người chỉ đạo: "+outputTaskModel.getOwner().getOrganizationUserName());
		spanOwner.getElement().getThemeList().add("badge pill");
		spanOwner.getStyle().setFontSize("10px");

		if(checkTaskAssignee || checkTaskSupport) {
			spanOwner.setText("Đơn vị giao: "+outputTaskModel.getOwner().getOrganizationName());
		}

		Span spValueStatus = spanStatus(outputTaskModel.getStatus().toString(),true);
		spValueStatus.getStyle().setMarginLeft("auto").setFontSize("10px").setMaxWidth("150px").setOverflow(Overflow.HIDDEN)
		.set("white-space", "nowrap").set("text-overflow", "ellipsis");;

		hLayoutHeader.setWidthFull();
		hLayoutHeader.add(spanOwner,spValueStatus);

		vLayoutMobile.add(hLayoutHeader);

		VerticalLayout vLayoutContent = new VerticalLayout();
		vLayoutContent.setWidthFull();
		vLayoutContent.setPadding(false);
		vLayoutContent.getStyle().set("gap", "5px");

		Span spTitle = new Span(outputTaskModel.getTitle());
		spTitle.setWidthFull();
		spTitle.getStyle().setFontSize("10px").setFontWeight(600).setOverflow(Overflow.HIDDEN)
		.set("white-space", "nowrap").set("text-overflow", "ellipsis");

		vLayoutContent.add(spTitle);

		HorizontalLayout hLayoutDate = new HorizontalLayout();
		hLayoutDate.setWidthFull();
		hLayoutDate.getStyle().setAlignItems(AlignItems.CENTER);

		Span spCreateTime = createLayoutKeyAndValueMobile("Ngày giao: ", outputTaskModel.getCreateTimeText());
		spCreateTime.setWidth("50%");

		Span spEndTime = createLayoutKeyAndValueMobile("Hạn xử lý: ", outputTaskModel.getEndTimeText());
		spEndTime.setWidth("50%");

		hLayoutDate.add(spCreateTime,spEndTime);
		hLayoutDate.setPadding(false);


		vLayoutContent.add(hLayoutDate);

		vLayoutMobile.add(vLayoutContent);

		HorizontalLayout hLayoutEvent = new HorizontalLayout();
		hLayoutEvent.setWidthFull();


		ButtonTemplate btnProgress = new ButtonTemplate(outputTaskModel.getProcesses().isEmpty() ? "0%" : outputTaskModel.getProcesses().get(0).getPercent()+"%",FontAwesome.Solid.BARS_PROGRESS.create());
		btnProgress.addThemeVariants(ButtonVariant.LUMO_SMALL);
		btnProgress.setTooltipText("Tiến độ thực hiện");
		btnProgress.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnProgress.addClickListener(e->{
			openDialogViewProgressTask(outputTaskModel.getId(),outputTaskModel);
		});

		ButtonTemplate btnAttachment = new ButtonTemplate(""+outputTaskModel.getAttachments().size(),FontAwesome.Solid.PAPERCLIP.create());
		btnAttachment.addThemeVariants(ButtonVariant.LUMO_SMALL);
		btnAttachment.setTooltipText("Đính kèm nhiệm vụ");
		btnAttachment.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnAttachment.addClickListener(e->{
			openDialogViewAttachment(outputTaskModel);
		});

		ButtonTemplate btnDiscuss = new ButtonTemplate(""+outputTaskModel.getComments().size(),FontAwesome.Solid.MESSAGE.create());
		btnDiscuss.addThemeVariants(ButtonVariant.LUMO_SMALL);
		btnDiscuss.setTooltipText("Trao đổi nhiệm vụ");
		btnDiscuss.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnDiscuss.addClickListener(e->{
			openDialogComment(outputTaskModel.getId());
		});

		ButtonTemplate btnLog = new ButtonTemplate(""+outputTaskModel.getEvents().size(),FontAwesome.Solid.FILE_LINES.create());
		btnLog.addThemeVariants(ButtonVariant.LUMO_SMALL);
		btnLog.setTooltipText("Lịch sử nhiệm vụ");
		btnLog.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnLog.addClickListener(e->{
			openDialogEvent(outputTaskModel.getId());
		});

		ButtonTemplate btnView = new ButtonTemplate(FontAwesome.Solid.EYE.create());
		btnView.addThemeVariants(ButtonVariant.LUMO_SMALL);
		btnView.setTooltipText("Xem chi tiết nhiệm vụ");
		btnView.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnView.addClickListener(e->{
			//			openDialogViewDetail(outputTaskModel.getId());
			openDialogViewDetailV2(outputTaskModel.getId());
		});

		ButtonTemplate btnSupports = new ButtonTemplate("Đơn vị hỗ trợ ("+outputTaskModel.getSupports().size()+")");
		btnSupports.addThemeVariants(ButtonVariant.LUMO_SMALL);

		btnSupports.addClickListener(e->{
			openPopupSupports(outputTaskModel.getSupports());
		});

		ButtonTemplate btnFollower = new ButtonTemplate("Khối theo dõi ("+outputTaskModel.getFollowers().size()+")");
		btnFollower.addThemeVariants(ButtonVariant.LUMO_SMALL);

		btnFollower.addClickListener(e->{
			openDialogFollowers(outputTaskModel.getFollowers());
		});

		hLayoutEvent.add(btnProgress,btnAttachment,btnDiscuss,btnLog,btnView);
		hLayoutEvent.getStyle().setAlignItems(AlignItems.CENTER);

		vLayoutMobile.add(hLayoutEvent);


		vLayoutMobile.getStyle().setBorderRadius("10px").setPadding("10px").setBoxShadow("rgba(0, 0, 0, 0.24) 0px 3px 8px");
		vLayoutMobile.setHeight("100px");
		vLayoutMobile.setSpacing(false);
		vLayoutMobile.setWidthFull();
		return vLayoutMobile;
	});


	private Span createLayoutKeyAndValueMobile(String header,String value) {
		Span span = new Span();
		H5 h5 = new H5(header);
		h5.getStyle().setFontSize("10px");

		Span spValue = new Span(value);
		spValue.getStyle().setFontSize("10px");

		span.add(h5,spValue);
		span.getStyle().setDisplay(Display.FLEX).setAlignItems(AlignItems.CENTER).set("gap", "5px");

		return span;

	}
	
	private void loadDataTags() {
		ApiTagFilterModel apiTagFilterModel = new ApiTagFilterModel();
		apiTagFilterModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
		apiTagFilterModel.setUserId(userAuthenticationModel.getId());
		apiTagFilterModel.setSkip(0);
		apiTagFilterModel.setLimit(0);
		apiTagFilterModel.setType("Task");
		apiTagFilterModel.setActive(true);
		
		listTags = new ArrayList<ApiTagModel>();
		ApiResultResponse<List<ApiTagModel>> data = ApiTagService.getListTags(apiTagFilterModel);
		if(data.isSuccess()) {
			listTags.addAll(data.getResult());
		}
	}
	
	private ApiTagModel findTagOfDoc(String id) {
		for(ApiTagModel apiTagModel : listTags) {
			if(apiTagModel.getClassIds() != null) {
				for(String i : apiTagModel.getClassIds()) {
					if(i.equals(id)) {
						return apiTagModel;
					}
				}
			}
		}
		return null;
	}
	
	private void openDialogChooseTag(String tagId,String idDoc) {
		DialogTemplate dialogTemplate = new DialogTemplate("Chọn phân loại");
		
		ApiInputAddclassModel apiInputAddclassModel = new ApiInputAddclassModel();
		
		List<String> listAddclassId = new ArrayList<String>();
		listAddclassId.add(idDoc);
		
		apiInputAddclassModel.setClassIds(listAddclassId);
		apiInputAddclassModel.setType("Task");
		apiInputAddclassModel.setTagId(tagId);
		
		TagForm tagForm = new TagForm(true,apiInputAddclassModel);
		tagForm.addChangeListener(e->{
			loadDataTags();
			loadData();
		});

		dialogTemplate.add(tagForm);
		
		dialogTemplate.setWidth("70%");
		dialogTemplate.setHeightFull();
		dialogTemplate.getFooter().removeAll();
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

