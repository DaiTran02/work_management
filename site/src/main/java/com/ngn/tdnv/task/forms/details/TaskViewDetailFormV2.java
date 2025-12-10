package com.ngn.tdnv.task.forms.details;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.io.FileSystemResource;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ngn.api.doc.ApiDocModel;
import com.ngn.api.doc.ApiDocService;
import com.ngn.api.media.ApiInputMediaModel;
import com.ngn.api.media.ApiMediaModel;
import com.ngn.api.media.ApiMediaService;
import com.ngn.api.organization.ApiUserGroupExpandModel;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tasks.ApiOutputTaskModel;
import com.ngn.api.tasks.ApiTaskService;
import com.ngn.api.tasks.actions.ApiAcceptModel;
import com.ngn.api.tasks.actions.ApiActionService;
import com.ngn.api.tasks.actions.ApiCompletedInputModel;
import com.ngn.api.tasks.actions.ApiConfirmModel;
import com.ngn.api.tasks.actions.ApiCreatorActionModel;
import com.ngn.api.tasks.actions.ApiCreatorDoRedoAndReportAgainModel;
import com.ngn.api.tasks.actions.ApiPedingModel;
import com.ngn.api.tasks.actions.ApiRatingModel;
import com.ngn.api.tasks.actions.ApiRedoModel;
import com.ngn.api.tasks.actions.ApiRefuseModel;
import com.ngn.api.tasks.actions.ApiRemindModel;
import com.ngn.api.tasks.actions.ApiReportModel;
import com.ngn.api.tasks.actions.ApiReverseModel;
import com.ngn.api.tasks.actions.ApiUnpendingModel;
import com.ngn.api.tasks.actions.ApiUpdateProcessModel;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.tdnv.doc.forms.create_task.TaskCreateForm;
import com.ngn.tdnv.doc.forms.create_task.TaskCreateFormV2;
import com.ngn.tdnv.doc.models.DocModel;
import com.ngn.tdnv.task.enums.CompletedStatusEnum;
import com.ngn.tdnv.task.enums.StatusTaskEnum;
import com.ngn.tdnv.task.forms.TaskCreateChildFormV2;
import com.ngn.tdnv.task.forms.broadcasts.BroadcastUtil;
import com.ngn.tdnv.task.forms.components.PinnedStatusForm;
import com.ngn.tdnv.task.forms.components.StepByStepDoTaskForm;
import com.ngn.tdnv.task.forms.components.StepByStepDoTaskModel;
import com.ngn.tdnv.task.models.TaskOutputModel;
import com.ngn.utils.LocalDateUtil;
import com.ngn.utils.PropsUtil;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.ConfirmDialogTemplate;
import com.ngn.utils.components.DetailsTemplate;
import com.ngn.utils.components.DialogTemplate;
import com.ngn.utils.components.NotificationTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.ngn.utils.uploads.UploadModuleBasic;
import com.ngn.utils.uploads.UploadModuleDataModel;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.html.Aside;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.Tabs.Orientation;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Background;
import com.vaadin.flow.theme.lumo.LumoUtility.BoxSizing;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.Flex;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Overflow;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;

public class TaskViewDetailFormV2 extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private boolean isAllowBroadcast = PropsUtil.isAllowBroadcast();
	private Registration taskBroadcaster;
	private UI ui = UI.getCurrent();
	private BelongOrganizationModel belongOrganizationModel = SessionUtil.getOrg();
	private UserAuthenticationModel userAuthenticationModel = SessionUtil.getUser();
	private List<String> listIdAttachment = new ArrayList<String>();
	private List<UploadModuleDataModel> listDataFileUpload = new ArrayList<UploadModuleDataModel>();

	private Span spWarning = new Span("Đây là nhiệm vụ được phân từ một nhiệm vụ khác");
	private HorizontalLayout hLayoutHeader = new HorizontalLayout();
	private HorizontalLayout hLayoutGeneral = new HorizontalLayout();
	private VerticalLayout vLayoutCenter = new VerticalLayout();
	private Tabs tabsRight = new Tabs();
	private VerticalLayout vLayoutLeft = new VerticalLayout();
	
	private StepByStepDoTaskForm stepByStepDoTaskForm = new StepByStepDoTaskForm();
	private List<StepByStepDoTaskModel> listSteps = new ArrayList<StepByStepDoTaskModel>();

	private List<Component> listPinneds = new ArrayList<Component>();
	private boolean checkDeleteTask = false;
	private Tab tabChildTask = new Tab("");

	// Buttons
	private ButtonTemplate btnAssignAgain = new ButtonTemplate("Giao lại nhiệm vụ",FontAwesome.Solid.MAIL_FORWARD.create());
	private ButtonTemplate btnAccept = new ButtonTemplate("Bắt đầu thực hiện",FontAwesome.Solid.CIRCLE_PLAY.create());
	private ButtonTemplate btnReverseCompleted = new ButtonTemplate("Triệu hồi nhiệm vụ",FontAwesome.Solid.REFRESH.create());
	private ButtonTemplate btnRefuse = new ButtonTemplate("Từ chối nhiệm vụ",FontAwesome.Solid.MULTIPLY.create());
	private ButtonTemplate btnRating = new ButtonTemplate("Đánh giá nhiệm vụ",FontAwesome.Solid.STAR.create());
	private ButtonTemplate btnComfirm = new ButtonTemplate("Xác nhận",FontAwesome.Solid.CHECK.create());
	private ButtonTemplate btnRefuseConfirm = new ButtonTemplate("Từ chối báo cáo",FontAwesome.Regular.TIMES_CIRCLE.create());
	private ButtonTemplate btnPedding = new ButtonTemplate("Tạm hoãn",FontAwesome.Solid.CIRCLE_PAUSE.create());
	private ButtonTemplate btnUnPendding = new ButtonTemplate("Tiếp tục nhiệm vụ",FontAwesome.Solid.CIRCLE_PLAY.create());
	private ButtonTemplate btnRemind = new ButtonTemplate("Nhắc nhở",FontAwesome.Solid.BELL.create());
	private ButtonTemplate btnUpdate = new ButtonTemplate("Cập nhật",FontAwesome.Solid.EDIT.create());
	private ButtonTemplate btnCompleted = new ButtonTemplate("Hoàn thành nhiệm vụ",FontAwesome.Solid.CHECK_CIRCLE.create());
	private ButtonTemplate btnDeleteTask = new ButtonTemplate("Xóa nhiệm vụ",FontAwesome.Solid.TRASH.create());
	private ButtonTemplate btnRedo = new ButtonTemplate("Thực hiện lại",FontAwesome.Solid.ARROW_LEFT_ROTATE.create());
	private ButtonTemplate btnCreateChildTask = new ButtonTemplate("Giao tiếp nhiệm vụ",FontAwesome.Solid.ARROW_TURN_RIGHT.create());
	private ButtonTemplate btnAssignUserAssignee = new ButtonTemplate("Phân cán bộ xử lý",FontAwesome.Solid.USER_PLUS.create());
	private ButtonTemplate btnAssignUserSupport = new ButtonTemplate("Phân cán bộ phối hợp",FontAwesome.Solid.USER_GROUP.create());
	private ButtonTemplate btnAssignUserSupportAgain = new ButtonTemplate("Phân lại cán bộ",FontAwesome.Solid.USER_GROUP.create());
	private ButtonTemplate btnEditAssignee = new ButtonTemplate("Chỉnh sửa",FontAwesome.Solid.EDIT.create());
	private ButtonTemplate btnReportConfirmCompleted = new ButtonTemplate("Báo cáo nhiệm vụ",FontAwesome.Solid.PAPER_PLANE.create());
	private ButtonTemplate btnReasonRefuse = new ButtonTemplate(FontAwesome.Solid.INFO.create());
	private ButtonTemplate btnReason = new ButtonTemplate("Xem lý do",FontAwesome.Solid.INFO.create());
	private ButtonTemplate btnHistory = new ButtonTemplate("Nhật ký nhiệm vụ",FontAwesome.Solid.HISTORY.create());
	private ButtonTemplate btnRedoAndReportAgain = new ButtonTemplate("Thực hiện và báo cáo lại",FontAwesome.Regular.CIRCLE_PLAY.create());



	private String idTask;
	private TaskOutputModel outTaskOutputModel = new TaskOutputModel();
	private DocModel docModel = new DocModel();

	private boolean isOwner = false;
	private boolean isAssignee = false;
	private boolean isSupport = false;
	private boolean isFollow = false;
	public TaskViewDetailFormV2(String idTask,boolean isOwner,boolean isAssignee,boolean isSupport,boolean isFollow) {
		this.idTask = idTask;
		this.isOwner = isOwner;
		this.isAssignee = isAssignee;
		this.isSupport = isSupport;
		this.isFollow = isFollow;
		loadData();
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();

		hLayoutHeader.setWidthFull();
		hLayoutHeader.setHeight("50px");
		hLayoutHeader.getStyle().setBorderRadius("10px").setAlignItems(com.vaadin.flow.dom.Style.AlignItems.CENTER);
//		this.add(spWarning,hLayoutHeader);
		
		stepByStepDoTaskForm.setWidth("95%");
		
		this.setAlignItems(Alignment.CENTER);
		this.add(stepByStepDoTaskForm);
		
		spWarning.setVisible(false);
		spWarning.getStyle().setFontWeight(600);

		tabsRight.setOrientation(Orientation.VERTICAL);
		tabsRight.addClassNames(Flex.GROW, Flex.SHRINK,Overflow.HIDDEN);

		Tab tab1 = new Tab(new Span("Thông tin nhiệm vụ"));

		Tab tab2 = new Tab(new Span("Thông tin đơn vị"));

		Tab tab3 = new Tab(new Span("Thông tin văn bản"));

		Tab tab4 = new Tab(new Span("Lịch sử nhiệm vụ"));

		tabsRight.add(tab1,tab2,tab3,tab4);

		if(outTaskOutputModel.getCountSubTask() > 0) {
			tabChildTask = new Tab("Nhiệm vụ đã phân ("+outTaskOutputModel.getCountSubTask()+")");
			tabsRight.add(tabChildTask);
		}

		tabsRight.setHeightFull();

		Aside side = new Aside();
		side.addClassNames(Display.FLEX, FlexDirection.COLUMN, Flex.GROW_NONE, Flex.SHRINK_NONE, Background.CONTRAST_5);
		side.setWidth("12rem");
		side.getStyle().setBorderRadius("10px").setBoxShadow("rgba(0, 0, 0, 0.16) 0px 1px 4px");

		Header header = new Header();
		header.addClassNames(Display.FLEX, FlexDirection.ROW, Width.FULL, AlignItems.CENTER, Padding.MEDIUM,
				BoxSizing.BORDER);
		
		H4 channels = new H4("CÁC CHỨC NĂNG");
		
		channels.addClassNames(Flex.GROW, Margin.NONE);

		header.add(channels);

		side.add(header,new Hr(),tabsRight);

		hLayoutGeneral.add(vLayoutLeft,vLayoutCenter,side);
		hLayoutGeneral.setWidthFull();
		hLayoutGeneral.setHeight("90%");

		vLayoutLeft.setWidth("19rem");
		vLayoutLeft.setHeightFull();
		createLayoutLeft();


		vLayoutCenter.setWidthFull();
		vLayoutCenter.getStyle().setOverflow(com.vaadin.flow.dom.Style.Overflow.AUTO);
		vLayoutCenter.setHeightFull();
		createLayoutCenter(outTaskOutputModel,docModel);

		hLayoutGeneral.expand(vLayoutCenter);
		this.add(hLayoutGeneral);

		UI.getCurrent().getPage().retrieveExtendedClientDetails(e->{
			if(e.getScreenWidth() < 768) {
				createLayoutMobile();
			}
		});
	}

	@Override
	public void configComponent() {
		tabsRight.addSelectedChangeListener(e->{
			createLayoutCenter(outTaskOutputModel,docModel);
		});

		btnRefuse.addClickListener(e->openConfirmRefuse());
		btnAccept.addClickListener(e->openConfirmAccept());
		btnCompleted.addClickListener(e->openConfirmCompleted());
		btnDeleteTask.addClickListener(e->openConfirmDeleted(idTask));
		btnReverseCompleted.addClickListener(e->openConfirmReverseCompleted());
		btnComfirm.addClickListener(e->openComfirmDoComfirm());
		btnRefuseConfirm.addClickListener(e->openDialogRefuseConfirm());
		btnPedding.addClickListener(e->openConfirmPedding());
		btnUnPendding.addClickListener(e->openConfirmUnPedding());
		btnRedo.addClickListener(e->openConfirmRedo());
		btnRedoAndReportAgain.addClickListener(e->doRedoAndReportAgain());
		btnRating.addClickListener(e->openConfirmRating());
		btnRemind.addClickListener(e->openDialogRemid());
		btnUpdate.addClickListener(e->openDialogUpdateTask(outTaskOutputModel));
		btnCreateChildTask.addClickListener(e->openDialogCreateChildTask(outTaskOutputModel));
		btnAssignUserAssignee.addClickListener(e->openDialogAssignUserAssignee());
		btnAssignUserSupport.addClickListener(e->openDialogAssignUserSupport());
		btnAssignUserSupportAgain.addClickListener(e->openDialogAssignUserSupport());
		btnReportConfirmCompleted.addClickListener(e->{
			if(outTaskOutputModel.getCountSubTask() > 0) {
				openDialogReport();
			}else {
				openConfirmReport();
			}
		});
		btnAssignAgain.addClickListener(e->openDialogAssignTaskAgain());
	}

	private void createLayoutMobile() {
		this.removeAll();
		VerticalLayout vLayout = new VerticalLayout();

		DetailsTemplate detailTabs = new DetailsTemplate("Các chức năng");
		detailTabs.add(tabsRight);
		detailTabs.setOpened(false);

		vLayoutLeft.setWidthFull();
		vLayoutLeft.setHeight("400px");
		
		vLayoutCenter.setWidthFull();
		vLayoutCenter.setPadding(false);
		vLayoutCenter.getStyle().setMarginTop("-5px");


		hLayoutHeader.setHeight("auto");
		hLayoutHeader.getStyle().setDisplay(com.vaadin.flow.dom.Style.Display.FLEX).setFlexDirection(com.vaadin.flow.dom.Style.FlexDirection.COLUMN);

		vLayout.add(detailTabs,hLayoutHeader,vLayoutLeft,vLayoutCenter);
		this.add(vLayout);
	}


	private void loadData() {

		listPinneds = new ArrayList<Component>();

		ApiResultResponse<ApiOutputTaskModel> data = ApiTaskService.getAtask(idTask);
		if(data.isSuccess()) {
			outTaskOutputModel = new TaskOutputModel(data.getResult());
		}

		if(outTaskOutputModel.getDocId() != null) {
			docModel = new DocModel(getDetailDoc(outTaskOutputModel.getDocId()));
		}

		if(outTaskOutputModel.isRequiredConfirm()) {
			listPinneds.add(new PinnedStatusForm("Nhiệm vụ cần xác nhận khi báo cáo hoàn thành", "#034fc3"));
		}

		if(outTaskOutputModel.getCountSubTask() > 0) {
			listPinneds.add(new PinnedStatusForm("Nhiệm vụ đã phân từ nhiệm vụ: "+outTaskOutputModel.getCountSubTask(), "rgb(215 191 19)"));
			listPinneds.add(new PinnedStatusForm("Nhiệm vụ con đã hoàn thành: "+countTaskSubCompleted()+"/"+outTaskOutputModel.getCountSubTask(), "rgb(32 205 0)"));
		}

		if(outTaskOutputModel.getParentId() != null) {
			spWarning.setVisible(true);
		}
		checkPermission(outTaskOutputModel);
		createLayoutCenter(outTaskOutputModel,docModel);
	}

	private ApiDocModel getDetailDoc(String idDoc) {
		ApiResultResponse<ApiDocModel> doc = ApiDocService.getAdoc(idDoc);
		if(doc.isSuccess()) {
			return doc.getResult();
		}
		return null;
	}
	
	private void loadStepLayout() {
		stepByStepDoTaskForm.clear();
		listSteps.forEach(model->{
			stepByStepDoTaskForm.addStepItem(model.getHeader(), model.getHelper(), model.isActive(), model.isDone());
		});
		stepByStepDoTaskForm.removeLastComponent();
	}

	private void doNotificationToOrtherUser() {
		List<JsonObject> listTask = new ArrayList<>();
		ApiResultResponse<ApiOutputTaskModel> data = ApiTaskService.getAtask(idTask);
		ApiOutputTaskModel apiOutputTaskModel = data.getResult();
		apiOutputTaskModel.setDocModel(getDetailDoc(apiOutputTaskModel.getDocId()));
		apiOutputTaskModel.setCountSubTaskIsCompleted(countTaskSubCompleted());
		Object object = new Object();
		object = apiOutputTaskModel;
		Gson gson = new Gson();
		try {
			String jsonString = gson.toJson(object);
			JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
			listTask.add(jsonObject);
			BroadcastUtil.broadcast(listTask);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createLayoutLeft() {
		vLayoutLeft.removeAll();

		H4 headerLeft = new H4("THAO TÁC XỬ LÝ");

		vLayoutLeft.add(headerLeft,new Hr());

		btnAccept.setWidthFull();
		btnAssignAgain.setWidthFull();
		btnAssignUserAssignee.setWidthFull();
		btnAssignUserSupport.setWidthFull();
		btnAssignUserSupportAgain.setWidthFull();
		btnComfirm.setWidthFull();
		btnCompleted.setWidthFull();
		btnCreateChildTask.setWidthFull();
		btnDeleteTask.setWidthFull();
		btnEditAssignee.setWidthFull();
		btnHistory.setWidthFull();
		btnPedding.setWidthFull();
		btnRating.setWidthFull();
		btnReason.setWidthFull();
		btnReasonRefuse.setWidthFull();
		btnRedo.setWidthFull();
		btnRedoAndReportAgain.setWidthFull();
		btnRefuse.setWidthFull();
		btnRefuseConfirm.setWidthFull();
		btnRemind.setWidthFull();
		btnReportConfirmCompleted.setWidthFull();
		btnReverseCompleted.setWidthFull();
		btnUnPendding.setWidthFull();
		btnUpdate.setWidthFull();

		vLayoutLeft.add(btnAccept,btnAssignAgain,btnAssignUserAssignee,btnAssignUserSupport,btnAssignUserSupportAgain,
				btnComfirm,btnCompleted,btnCreateChildTask,btnEditAssignee,btnHistory,
				btnPedding,btnRating,btnReason,btnReasonRefuse,btnRedo,btnRedoAndReportAgain,btnRefuse,
				btnRefuseConfirm,btnRemind,btnReportConfirmCompleted,btnReverseCompleted,btnUnPendding,btnUpdate,btnDeleteTask);

		btnDeleteTask.addThemeVariants(ButtonVariant.LUMO_ERROR);

		btnCompleted.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
		btnAccept.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
		btnRefuseConfirm.addThemeVariants(ButtonVariant.LUMO_ERROR);

		btnRefuse.addThemeVariants(ButtonVariant.LUMO_ERROR);

		vLayoutLeft.getStyle().setOverflow(com.vaadin.flow.dom.Style.Overflow.AUTO).setBorderRadius("10px")
		.setBoxShadow("rgba(0, 0, 0, 0.16) 0px 1px 4px").setPadding("10px");
	}

	private void createLayoutCenter(TaskOutputModel taskOutputModel,DocModel docModel) {
		vLayoutCenter.removeAll();
		vLayoutCenter.setPadding(false);

		if(tabsRight.getSelectedIndex() == 0) {
			HorizontalLayout hLayoutPanner = new HorizontalLayout();
			hLayoutPanner.setWidthFull();
			
			if(StatusTaskEnum.toCheckTaskIsNotComplete(taskOutputModel.getState().toString())) {
				if(taskOutputModel.getReminds() != null && !taskOutputModel.getReminds().isEmpty()) {
					TaskViewRemindForm taskViewRemindForm = new TaskViewRemindForm(taskOutputModel.getReminds());
					hLayoutPanner.add(taskViewRemindForm);
					vLayoutCenter.add(hLayoutPanner);
				}
				
				if(taskOutputModel.getReverse() != null) {
					TaskViewReverseForm taskViewReverseForm = new TaskViewReverseForm(taskOutputModel.getReverse());
					hLayoutPanner.add(taskViewReverseForm);
					vLayoutCenter.add(hLayoutPanner);
				}
				
			}
			
			//Pedding
			if(StatusTaskEnum.toCheckTaskIsPedding(taskOutputModel.getState().toString())) {
				if(taskOutputModel.getPending() != null) {
					TaskViewPenddingForm taskViewPenddingForm = new TaskViewPenddingForm(taskOutputModel.getPending());
					hLayoutPanner.add(taskViewPenddingForm);
					vLayoutCenter.add(hLayoutPanner);
				}
			}
			
			//Redo
			if(StatusTaskEnum.toCheckTaskIsRedo(taskOutputModel.getState().toString())){
				if(taskOutputModel.getRedo() != null) {
					TaskViewRedoForm taskViewRedoForm = new TaskViewRedoForm(taskOutputModel.getRedo());
					hLayoutPanner.add(taskViewRedoForm);
					vLayoutCenter.add(hLayoutPanner);
				}
			}
			
			//Wait for confirm
			if(StatusTaskEnum.toCheckTaskIsWaitForConfirm(taskOutputModel.getState().toString())) {
				if(taskOutputModel.getReported() != null) {
					TaskViewWaitForConfirmForm taskViewWaitForConfirmForm = new TaskViewWaitForConfirmForm(taskOutputModel.getReported());
					hLayoutPanner.add(taskViewWaitForConfirmForm);
					vLayoutCenter.add(hLayoutPanner);
				}
			}
			
			//Refuse task
			if(taskOutputModel.getState().equals(StatusTaskEnum.TUCHOITHUCHIEN.getKey())){
				if(taskOutputModel.getRefuse() != null) {
					TaskViewRefuseForm taskViewRefuseForm = new TaskViewRefuseForm(taskOutputModel.getRefuse());
					hLayoutPanner.add(taskViewRefuseForm);
					vLayoutCenter.add(hLayoutPanner);
				}
			}
			
			//Refuse confirm
			if(StatusTaskEnum.toCheckTaskIsRefuseConfirm(taskOutputModel.getState().toString())){
				TaskViewRefuseConfirmForm taskViewRefuseConfirmForm = new TaskViewRefuseConfirmForm(taskOutputModel.getConfirmRefuse(),taskOutputModel.getConfirmRefuseHisories());
				hLayoutPanner.add(taskViewRefuseConfirmForm);
				vLayoutCenter.add(hLayoutPanner);
			}
			
			//Task is completed
			if(StatusTaskEnum.toCheckTaskIsCompleted(taskOutputModel.getState().toString())) {
				if(taskOutputModel.getCompleted() != null) {
					TaskViewCompletedForm taskViewCompletedForm = new TaskViewCompletedForm(taskOutputModel.getCompleted());
					hLayoutPanner.add(taskViewCompletedForm);
				}
				
				if(taskOutputModel.getRating() != null) {
					TaskViewRatingForm taskViewRatingForm = new TaskViewRatingForm(taskOutputModel.getRating());
					hLayoutPanner.add(taskViewRatingForm);
				}
				vLayoutCenter.add(hLayoutPanner);
			}
			
			TaskViewDetailOfTaskForm taskForm = new TaskViewDetailOfTaskForm(taskOutputModel,isOwner,isAssignee,isSupport,isFollow);
			taskForm.getStyle().setMarginTop("-5px");
			
			vLayoutCenter.add(taskForm);
		}

		if(tabsRight.getSelectedIndex() == 1) {
			TaskViewDetailOfOrgForm taskViewDetailOfOrgForm = new TaskViewDetailOfOrgForm(taskOutputModel);
			vLayoutCenter.add(taskViewDetailOfOrgForm);
		}

		if(tabsRight.getSelectedIndex() == 2) {
			TaskViewDetailOfDocForm taskViewDetailOfDocForm = 
					new TaskViewDetailOfDocForm(taskOutputModel,isOwner,isAssignee,isSupport,isFollow,docModel);
			taskViewDetailOfDocForm.addChangeListener(e->{
				if(isAllowBroadcast == true) {
					doNotificationToOrtherUser();
				}else {
					loadData();
				}
			});
			vLayoutCenter.add(taskViewDetailOfDocForm);
		}

		if(tabsRight.getSelectedIndex() == 3) {
			TaskHistoryEventFormV2 taskEventForm = new TaskHistoryEventFormV2(idTask);
			taskEventForm.setHeight("100%");
			vLayoutCenter.add(taskEventForm);
		}

		if(tabsRight.getSelectedIndex() == 4) {
			TaskListDetailChildrenForm taskDetailChildrenForm = new TaskListDetailChildrenForm(idTask);
			taskDetailChildrenForm.loadData();
			vLayoutCenter.add(taskDetailChildrenForm);
		}

	}


	private void checkPermission(TaskOutputModel taskOutputModel) {
		setAllButtonNotVisible();
		listSteps.clear();
		if(isOwner) {
			if(taskOutputModel.getState().equals(StatusTaskEnum.CHUATHUCHIEN.getKey())) {
				btnPedding.setVisible(true);
				btnUpdate.setVisible(true);
				btnDeleteTask.setVisible(true);
				btnAccept.setVisible(true);
				btnRemind.setVisible(true);

				listPinneds.add(new PinnedStatusForm("Nhiệm vụ chưa thực hiện","#8b0006"));
				
				listSteps.add(new StepByStepDoTaskModel("Chờ thực hiện", "Chờ thực hiện", true, false));
				listSteps.add(new StepByStepDoTaskModel("Đang thực hiện", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Chờ xác nhận", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Hoàn thành", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Đánh giá", "", false, false));

			}else if(taskOutputModel.getState().equals(StatusTaskEnum.DANGTHUCHIEN.getKey())) {
				btnPedding.setVisible(true);
				btnUpdate.setVisible(true);
				btnDeleteTask.setVisible(true);
				btnCompleted.setVisible(true);
				btnRemind.setVisible(true);

				listPinneds.add(new PinnedStatusForm("Nhiệm vụ đang thực hiện","rgb(117 233 121)"));
				
				listSteps.add(new StepByStepDoTaskModel("Chờ thực hiện", "", false, true));
				listSteps.add(new StepByStepDoTaskModel("Đang thực hiện", "", true, false));
				listSteps.add(new StepByStepDoTaskModel("Chờ xác nhận", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Hoàn thành", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Đánh giá", "", false, false));

			}else if(taskOutputModel.getState().equals(StatusTaskEnum.TAMHOAN.getKey())) {
				btnUnPendding.setVisible(true);
				btnUpdate.setVisible(true);
				btnDeleteTask.setVisible(true);

				listPinneds.add(new PinnedStatusForm("Nhiệm vụ đang tạm hoãn","#5a307a"));
				
				listSteps.add(new StepByStepDoTaskModel("Chờ thực hiện", "Tạm hoãn", true, false));
				listSteps.add(new StepByStepDoTaskModel("Đang thực hiện", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Chờ xác nhận", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Hoàn thành", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Đánh giá", "", false, false));

			}else if(taskOutputModel.getState().equals(StatusTaskEnum.CHOXACNHAN.getKey())) {
				btnComfirm.setVisible(true);
				btnRefuseConfirm.setVisible(true);

				listPinneds.add(new PinnedStatusForm("Nhiệm vụ đang chờ xác nhận","#4f7c54"));
				
				listSteps.add(new StepByStepDoTaskModel("Chờ thực hiện", "", false, true));
				listSteps.add(new StepByStepDoTaskModel("Đang thực hiện", "", false, true));
				listSteps.add(new StepByStepDoTaskModel("Chờ xác nhận", "", true, false));
				listSteps.add(new StepByStepDoTaskModel("Hoàn thành", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Đánh giá", "", false, false));

			}else if(taskOutputModel.getState().equals(StatusTaskEnum.TUCHOITHUCHIEN.getKey())) {
				btnPedding.setVisible(true);
				btnUpdate.setVisible(true);
				btnDeleteTask.setVisible(true);
				btnRemind.setVisible(true);
				btnAssignAgain.setVisible(true);

				listPinneds.add(new PinnedStatusForm("Đơn vị xử lý từ chối thực hiện","#000000"));
				
				listSteps.add(new StepByStepDoTaskModel("Chờ thực hiện", "Từ chối thực hiện", true, true));
				listSteps.add(new StepByStepDoTaskModel("Đang thực hiện", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Chờ xác nhận", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Hoàn thành", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Đánh giá", "", false, false));

			}else if(taskOutputModel.getState().equals(StatusTaskEnum.THUCHIENLAI.getKey())) {
				btnPedding.setVisible(true);
				btnUpdate.setVisible(true);
				btnDeleteTask.setVisible(true);
				btnRemind.setVisible(true);
				btnCompleted.setVisible(true);

				listPinneds.add(new PinnedStatusForm("Nhiệm vụ đang được thực hiện lại","#958d33"));
				
				listSteps.add(new StepByStepDoTaskModel("Chờ thực hiện", "", false, true));
				listSteps.add(new StepByStepDoTaskModel("Đang thực hiện", "Thực hiện lại", true, false));
				listSteps.add(new StepByStepDoTaskModel("Chờ xác nhận", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Hoàn thành", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Đánh giá", "", false, false));

			}else if(taskOutputModel.getState().equals(StatusTaskEnum.TUCHOIXACNHAN.getKey())) {
				btnPedding.setVisible(true);
				btnUpdate.setVisible(true);
				btnDeleteTask.setVisible(true);
				btnRemind.setVisible(true);
				btnCompleted.setVisible(true);
				
				listSteps.add(new StepByStepDoTaskModel("Chờ thực hiện", "", false, true));
				listSteps.add(new StepByStepDoTaskModel("Đang thực hiện", "Từ chối xác nhận", true, false));
				listSteps.add(new StepByStepDoTaskModel("Chờ xác nhận", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Hoàn thành", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Đánh giá", "", false, false));

				listPinneds.add(new PinnedStatusForm("Đơn vị giao từ chối báo cáo","#ff0000"));
			}else if(taskOutputModel.getState().equals(StatusTaskEnum.DAHOANTHANH.getKey())) {
				btnRating.setVisible(true);
				btnRedo.setVisible(true);

				
				listSteps.add(new StepByStepDoTaskModel("Chờ thực hiện", "", false, true));
				listSteps.add(new StepByStepDoTaskModel("Đang thực hiện", "", false, true));
				listSteps.add(new StepByStepDoTaskModel("Chờ xác nhận", "", false, true));
				if(taskOutputModel.getRating() != null) {
					btnRating.setText("Đánh giá lại nhiệm vụ");
					listSteps.add(new StepByStepDoTaskModel("Hoàn thành", CompletedStatusEnum.findComletedStatus(taskOutputModel.getCompleted().getCompletedStatus()), false, true));
					listSteps.add(new StepByStepDoTaskModel("Đánh giá", taskOutputModel.getRating().getStar()+" sao", true, false));
				}else {
					listSteps.add(new StepByStepDoTaskModel("Hoàn thành", CompletedStatusEnum.findComletedStatus(taskOutputModel.getCompleted().getCompletedStatus()), true, false));
					listSteps.add(new StepByStepDoTaskModel("Đánh giá", "Chưa đánh giá", false, false));
				}
				

				listPinneds.add(new PinnedStatusForm("Nhiệm vụ đã hoàn thành","rgb(14 223 0)"));
			}
		}

		if(isAssignee) {
			if(taskOutputModel.getState().equals(StatusTaskEnum.CHUATHUCHIEN.getKey())) {

				btnAccept.setVisible(true);
				btnRefuse.setVisible(true);
				btnAssignUserAssignee.setVisible(true);

				//If you want to assgin it to a specific person
				if(taskOutputModel.getAssignee().getOrganizationUserId() != null) {
					btnAssignUserAssignee.setVisible(true);
					btnAssignUserAssignee.setText("Phân lại xử lý");
				}else {
					btnAssignUserAssignee.setVisible(true);
				}

				listPinneds.add(new PinnedStatusForm("Nhiệm vụ chưa thực hiện","#8b0006"));
				
				listSteps.add(new StepByStepDoTaskModel("Chờ thực hiện", "Chờ thực hiện", true, false));
				listSteps.add(new StepByStepDoTaskModel("Đang thực hiện", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Chờ xác nhận", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Hoàn thành", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Đánh giá", "", false, false));

			}else if(taskOutputModel.getState().equals(StatusTaskEnum.DANGTHUCHIEN.getKey())) {
				btnCreateChildTask.setVisible(true);
				btnCompleted.setVisible(true);

				// The task is being performed but I still want to assign it to a specific person
				if(taskOutputModel.getAssignee().getOrganizationUserId() != null) {
					btnAssignUserAssignee.setVisible(true);
					btnAssignUserAssignee.setText("Phân lại xử lý");
				}else {
					btnAssignUserAssignee.setVisible(true);
				} 

				//Repost task complete when is Confirm
				if(taskOutputModel.isRequiredConfirm()) {
					btnReportConfirmCompleted.setVisible(true);
					btnCompleted.setVisible(false);
				}

				listPinneds.add(new PinnedStatusForm("Nhiệm vụ đang thực hiện","rgb(117 233 121)"));
				
				listSteps.add(new StepByStepDoTaskModel("Chờ thực hiện", "", false, true));
				listSteps.add(new StepByStepDoTaskModel("Đang thực hiện", "", true, false));
				listSteps.add(new StepByStepDoTaskModel("Chờ xác nhận", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Hoàn thành", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Đánh giá", "", false, false));

			}else if(taskOutputModel.getState().equals(StatusTaskEnum.TAMHOAN.getKey())) {
				listPinneds.add(new PinnedStatusForm("Nhiệm vụ đang tạm hoãn","#5a307a"));
				
				listSteps.add(new StepByStepDoTaskModel("Chờ thực hiện", "Tạm hoãn", true, false));
				listSteps.add(new StepByStepDoTaskModel("Đang thực hiện", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Chờ xác nhận", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Hoàn thành", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Đánh giá", "", false, false));
				
			}else if(taskOutputModel.getState().equals(StatusTaskEnum.CHOXACNHAN.getKey())) {
				listPinneds.add(new PinnedStatusForm("Nhiệm vụ đang chờ xác nhận","#4f7c54"));
				
				listSteps.add(new StepByStepDoTaskModel("Chờ thực hiện", "", false, true));
				listSteps.add(new StepByStepDoTaskModel("Đang thực hiện", "", false, true));
				listSteps.add(new StepByStepDoTaskModel("Chờ xác nhận", "", true, false));
				listSteps.add(new StepByStepDoTaskModel("Hoàn thành", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Đánh giá", "", false, false));
				
				btnReverseCompleted.setVisible(true);
			}else if(taskOutputModel.getState().equals(StatusTaskEnum.TUCHOITHUCHIEN.getKey())) {
				listPinneds.add(new PinnedStatusForm("Đơn vị xử lý từ chối thực hiện","#000000"));
				
				listSteps.add(new StepByStepDoTaskModel("Chờ thực hiện", "Từ chối thực hiện", true, true));
				listSteps.add(new StepByStepDoTaskModel("Đang thực hiện", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Chờ xác nhận", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Hoàn thành", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Đánh giá", "", false, false));

			}else if(taskOutputModel.getState().equals(StatusTaskEnum.THUCHIENLAI.getKey())) {

				//				btnRedoAndReportAgain.setVisible(true);

				btnCreateChildTask.setVisible(true);
				btnCompleted.setVisible(true);

				// The task is being performed but I still want to assign it to a specific person
				if(taskOutputModel.getAssignee().getOrganizationUserId() != null) {
					btnAssignUserAssignee.setVisible(true);
					btnAssignUserAssignee.setText("Phân lại xử lý");
				}else {
					btnAssignUserAssignee.setVisible(true);
				} 

				//Repost task complete when is Confirm
				if(taskOutputModel.isRequiredConfirm()) {
					btnReportConfirmCompleted.setVisible(true);
					btnCompleted.setVisible(false);
				}

				listPinneds.add(new PinnedStatusForm("Nhiệm vụ chờ thực hiện lại","#958d33"));
				
				listSteps.add(new StepByStepDoTaskModel("Chờ thực hiện", "", false, true));
				listSteps.add(new StepByStepDoTaskModel("Đang thực hiện", "Thực hiện lại", true, false));
				listSteps.add(new StepByStepDoTaskModel("Chờ xác nhận", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Hoàn thành", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Đánh giá", "", false, false));

			}else if(taskOutputModel.getState().equals(StatusTaskEnum.TUCHOIXACNHAN.getKey())) {
				btnRedoAndReportAgain.setVisible(true);
				// The task is being performed but I still want to assign it to a specific person
				if(taskOutputModel.getAssignee().getOrganizationUserId() != null) {
					btnAssignUserAssignee.setVisible(true);
					btnAssignUserAssignee.setText("Phân lại xử lý");
				}else {
					btnAssignUserAssignee.setVisible(true);
				} 

				listPinneds.add(new PinnedStatusForm("Đơn vị giao từ chối báo cáo","#ff0000"));
				
				listSteps.add(new StepByStepDoTaskModel("Chờ thực hiện", "", false, true));
				listSteps.add(new StepByStepDoTaskModel("Đang thực hiện", "Từ chối xác nhận", true, false));
				listSteps.add(new StepByStepDoTaskModel("Chờ xác nhận", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Hoàn thành", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Đánh giá", "", false, false));
				
			}else if(taskOutputModel.getState().equals(StatusTaskEnum.DAHOANTHANH.getKey())) {

				btnReverseCompleted.setVisible(true);

				listPinneds.add(new PinnedStatusForm("Nhiệm vụ đã hoàn thành","rgb(14 223 0)"));
				
				listSteps.add(new StepByStepDoTaskModel("Chờ thực hiện", "", false, true));
				listSteps.add(new StepByStepDoTaskModel("Đang thực hiện", "", false, true));
				listSteps.add(new StepByStepDoTaskModel("Chờ xác nhận", "", false, true));
				if(taskOutputModel.getRating() != null) {
					btnRating.setText("Đánh giá lại nhiệm vụ");
					listSteps.add(new StepByStepDoTaskModel("Hoàn thành", CompletedStatusEnum.findComletedStatus(taskOutputModel.getCompleted().getCompletedStatus()), false, true));
					listSteps.add(new StepByStepDoTaskModel("Đánh giá", taskOutputModel.getRating().getStar()+" sao", true, false));
				}else {
					listSteps.add(new StepByStepDoTaskModel("Hoàn thành", CompletedStatusEnum.findComletedStatus(taskOutputModel.getCompleted().getCompletedStatus()), true, false));
					listSteps.add(new StepByStepDoTaskModel("Đánh giá", "Chưa đánh giá", false, false));
				}
				
			}
		}

		if(isSupport) {
			if(taskOutputModel.getState().equals(StatusTaskEnum.CHUATHUCHIEN.getKey())) {
				listPinneds.add(new PinnedStatusForm("Nhiệm vụ chưa thực hiện","#8b0006"));
				
				listSteps.add(new StepByStepDoTaskModel("Chờ thực hiện", "Chờ thực hiện", true, false));
				listSteps.add(new StepByStepDoTaskModel("Đang thực hiện", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Chờ xác nhận", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Hoàn thành", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Đánh giá", "", false, false));
				
			}else if(taskOutputModel.getState().equals(StatusTaskEnum.DANGTHUCHIEN.getKey())) {
				listPinneds.add(new PinnedStatusForm("Nhiệm vụ đang thực hiện","rgb(117 233 121)"));
				
				listSteps.add(new StepByStepDoTaskModel("Chờ thực hiện", "", false, true));
				listSteps.add(new StepByStepDoTaskModel("Đang thực hiện", "", true, false));
				listSteps.add(new StepByStepDoTaskModel("Chờ xác nhận", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Hoàn thành", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Đánh giá", "", false, false));
				
			}else if(taskOutputModel.getState().equals(StatusTaskEnum.TAMHOAN.getKey())) {
				listPinneds.add(new PinnedStatusForm("Nhiệm vụ đang tạm hoãn","#5a307a"));
				
				listSteps.add(new StepByStepDoTaskModel("Chờ thực hiện", "Tạm hoãn", true, false));
				listSteps.add(new StepByStepDoTaskModel("Đang thực hiện", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Chờ xác nhận", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Hoàn thành", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Đánh giá", "", false, false));
				
			}else if(taskOutputModel.getState().equals(StatusTaskEnum.CHOXACNHAN.getKey())) {
				listPinneds.add(new PinnedStatusForm("Nhiệm vụ đang chờ xác nhận","#4f7c54"));
				
				listSteps.add(new StepByStepDoTaskModel("Chờ thực hiện", "", false, true));
				listSteps.add(new StepByStepDoTaskModel("Đang thực hiện", "", false, true));
				listSteps.add(new StepByStepDoTaskModel("Chờ xác nhận", "", true, false));
				listSteps.add(new StepByStepDoTaskModel("Hoàn thành", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Đánh giá", "", false, false));
				
			}else if(taskOutputModel.getState().equals(StatusTaskEnum.TUCHOITHUCHIEN.getKey())) {
				listPinneds.add(new PinnedStatusForm("Đơn vị xử lý từ chối thực hiện","#000000"));
				
				listSteps.add(new StepByStepDoTaskModel("Chờ thực hiện", "Từ chối thực hiện", true, true));
				listSteps.add(new StepByStepDoTaskModel("Đang thực hiện", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Chờ xác nhận", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Hoàn thành", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Đánh giá", "", false, false));
				
			}else if(taskOutputModel.getState().equals(StatusTaskEnum.THUCHIENLAI.getKey())) {
				listPinneds.add(new PinnedStatusForm("Nhiệm vụ chờ thực hiện lại","#958d33"));
				
				listSteps.add(new StepByStepDoTaskModel("Chờ thực hiện", "", false, true));
				listSteps.add(new StepByStepDoTaskModel("Đang thực hiện", "Thực hiện lại", true, false));
				listSteps.add(new StepByStepDoTaskModel("Chờ xác nhận", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Hoàn thành", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Đánh giá", "", false, false));
				
			}else if(taskOutputModel.getState().equals(StatusTaskEnum.TUCHOIXACNHAN.getKey())) {
				listPinneds.add(new PinnedStatusForm("Đơn vị giao từ chối báo cáo","#ff0000"));
				
				listSteps.add(new StepByStepDoTaskModel("Chờ thực hiện", "", false, true));
				listSteps.add(new StepByStepDoTaskModel("Đang thực hiện", "Từ chối xác nhận", true, false));
				listSteps.add(new StepByStepDoTaskModel("Chờ xác nhận", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Hoàn thành", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Đánh giá", "", false, false));
				
			}else if(taskOutputModel.getState().equals(StatusTaskEnum.DAHOANTHANH.getKey())) {
				listPinneds.add(new PinnedStatusForm("Nhiệm vụ đã hoàn thành","rgb(14 223 0)"));
				
				listSteps.add(new StepByStepDoTaskModel("Chờ thực hiện", "", false, true));
				listSteps.add(new StepByStepDoTaskModel("Đang thực hiện", "", false, true));
				listSteps.add(new StepByStepDoTaskModel("Chờ xác nhận", "", false, true));
				if(taskOutputModel.getRating() != null) {
					btnRating.setText("Đánh giá lại nhiệm vụ");
					listSteps.add(new StepByStepDoTaskModel("Hoàn thành", CompletedStatusEnum.findComletedStatus(taskOutputModel.getCompleted().getCompletedStatus()), false, true));
					listSteps.add(new StepByStepDoTaskModel("Đánh giá", taskOutputModel.getRating().getStar()+" sao", true, false));
				}else {
					listSteps.add(new StepByStepDoTaskModel("Hoàn thành", CompletedStatusEnum.findComletedStatus(taskOutputModel.getCompleted().getCompletedStatus()), true, false));
					listSteps.add(new StepByStepDoTaskModel("Đánh giá", "Chưa đánh giá", false, false));
				}
				
			}
			taskOutputModel.getSupports().stream().forEach(model->{
				if(model.getOrganizationId().equals(belongOrganizationModel.getOrganizationId())) {
					if(model.getOrganizationUserId() == null) {
						btnAssignUserSupport.setVisible(true);
					}else {
						btnAssignUserSupportAgain.setVisible(true);
					}
					listPinneds.add(new PinnedStatusForm("Vui lòng hỗ trợ đơn vị xử lý để xử lý nhiệm vụ", "#8b0006"));
				}
			});
		}

		if(isFollow) {
			if(taskOutputModel.getState().equals(StatusTaskEnum.CHUATHUCHIEN.getKey())) {
				btnPedding.setVisible(true);
				btnUpdate.setVisible(true);
				btnDeleteTask.setVisible(true);
				btnAccept.setVisible(true);
				btnRemind.setVisible(true);

				listPinneds.add(new PinnedStatusForm("Nhiệm vụ chưa thực hiện","#8b0006"));
				
				listSteps.add(new StepByStepDoTaskModel("Chờ thực hiện", "Chờ thực hiện", true, false));
				listSteps.add(new StepByStepDoTaskModel("Đang thực hiện", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Chờ xác nhận", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Hoàn thành", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Đánh giá", "", false, false));

			}else if(taskOutputModel.getState().equals(StatusTaskEnum.DANGTHUCHIEN.getKey())) {
				btnPedding.setVisible(true);
				btnUpdate.setVisible(true);
				btnDeleteTask.setVisible(true);
				btnCompleted.setVisible(true);
				btnRemind.setVisible(true);

				listPinneds.add(new PinnedStatusForm("Nhiệm vụ đang thực hiện","rgb(117 233 121)"));
				
				listSteps.add(new StepByStepDoTaskModel("Chờ thực hiện", "", false, true));
				listSteps.add(new StepByStepDoTaskModel("Đang thực hiện", "", true, false));
				listSteps.add(new StepByStepDoTaskModel("Chờ xác nhận", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Hoàn thành", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Đánh giá", "", false, false));

			}else if(taskOutputModel.getState().equals(StatusTaskEnum.TAMHOAN.getKey())) {
				btnUnPendding.setVisible(true);
				btnUpdate.setVisible(true);
				btnDeleteTask.setVisible(true);

				listPinneds.add(new PinnedStatusForm("Nhiệm vụ đang tạm hoãn","#5a307a"));
				
				listSteps.add(new StepByStepDoTaskModel("Chờ thực hiện", "Tạm hoãn", true, false));
				listSteps.add(new StepByStepDoTaskModel("Đang thực hiện", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Chờ xác nhận", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Hoàn thành", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Đánh giá", "", false, false));

			}else if(taskOutputModel.getState().equals(StatusTaskEnum.CHOXACNHAN.getKey())) {
				btnComfirm.setVisible(true);
				btnRefuseConfirm.setVisible(true);

				listPinneds.add(new PinnedStatusForm("Nhiệm vụ đang chờ xác nhận","#4f7c54"));
				
				listSteps.add(new StepByStepDoTaskModel("Chờ thực hiện", "", false, true));
				listSteps.add(new StepByStepDoTaskModel("Đang thực hiện", "", false, true));
				listSteps.add(new StepByStepDoTaskModel("Chờ xác nhận", "", true, false));
				listSteps.add(new StepByStepDoTaskModel("Hoàn thành", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Đánh giá", "", false, false));

			}else if(taskOutputModel.getState().equals(StatusTaskEnum.TUCHOITHUCHIEN.getKey())) {
				btnPedding.setVisible(true);
				btnUpdate.setVisible(true);
				btnDeleteTask.setVisible(true);
				btnRemind.setVisible(true);
				btnAssignAgain.setVisible(true);

				listPinneds.add(new PinnedStatusForm("Đơn vị xử lý từ chối thực hiện","#000000"));
				
				listSteps.add(new StepByStepDoTaskModel("Chờ thực hiện", "Từ chối thực hiện", true, true));
				listSteps.add(new StepByStepDoTaskModel("Đang thực hiện", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Chờ xác nhận", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Hoàn thành", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Đánh giá", "", false, false));

			}else if(taskOutputModel.getState().equals(StatusTaskEnum.THUCHIENLAI.getKey())) {
				btnPedding.setVisible(true);
				btnUpdate.setVisible(true);
				btnDeleteTask.setVisible(true);
				btnRemind.setVisible(true);
				btnCompleted.setVisible(true);

				listPinneds.add(new PinnedStatusForm("Nhiệm vụ chờ thực hiện lại","#958d33"));
				
				listSteps.add(new StepByStepDoTaskModel("Chờ thực hiện", "", false, true));
				listSteps.add(new StepByStepDoTaskModel("Đang thực hiện", "Thực hiện lại", true, false));
				listSteps.add(new StepByStepDoTaskModel("Chờ xác nhận", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Hoàn thành", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Đánh giá", "", false, false));

			}else if(taskOutputModel.getState().equals(StatusTaskEnum.TUCHOIXACNHAN.getKey())) {
				btnPedding.setVisible(true);
				btnUpdate.setVisible(true);
				btnDeleteTask.setVisible(true);
				btnRemind.setVisible(true);
				btnCompleted.setVisible(true);

				listPinneds.add(new PinnedStatusForm("Đơn vị giao từ chối báo cáo","#ff0000"));
				
				listSteps.add(new StepByStepDoTaskModel("Chờ thực hiện", "", false, true));
				listSteps.add(new StepByStepDoTaskModel("Đang thực hiện", "Từ chối xác nhận", true, false));
				listSteps.add(new StepByStepDoTaskModel("Chờ xác nhận", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Hoàn thành", "", false, false));
				listSteps.add(new StepByStepDoTaskModel("Đánh giá", "", false, false));
				
			}else if(taskOutputModel.getState().equals(StatusTaskEnum.DAHOANTHANH.getKey())) {
				btnRating.setVisible(true);
				btnRedo.setVisible(true);
				if(taskOutputModel.getRating() != null) {
					btnRating.setText("Đánh giá lại nhiệm vụ");
				}

				listPinneds.add(new PinnedStatusForm("Nhiệm vụ đã hoàn thành","rgb(14 223 0)"));
				
				listSteps.add(new StepByStepDoTaskModel("Chờ thực hiện", "", false, true));
				listSteps.add(new StepByStepDoTaskModel("Đang thực hiện", "", false, true));
				listSteps.add(new StepByStepDoTaskModel("Chờ xác nhận", "", false, true));
				if(taskOutputModel.getRating() != null) {
					btnRating.setText("Đánh giá lại nhiệm vụ");
					listSteps.add(new StepByStepDoTaskModel("Hoàn thành", CompletedStatusEnum.findComletedStatus(taskOutputModel.getCompleted().getCompletedStatus()), false, true));
					listSteps.add(new StepByStepDoTaskModel("Đánh giá", taskOutputModel.getRating().getStar()+" sao", true, false));
				}else {
					listSteps.add(new StepByStepDoTaskModel("Hoàn thành", CompletedStatusEnum.findComletedStatus(taskOutputModel.getCompleted().getCompletedStatus()), true, false));
					listSteps.add(new StepByStepDoTaskModel("Đánh giá", "Chưa đánh giá", false, false));
				}
				
			}
		}
		
//		createLayoutHeader();
		loadStepLayout();
	}
	

	//Phân cán bộ hỗ trợ 
	private void openDialogAssignUserSupport() {
		DialogTemplate dialogTemplate = new DialogTemplate("PHÂN CÁN BỘ HỖ TRỢ");
		ApiUserGroupExpandModel apiUserGroupExpandModel = new ApiUserGroupExpandModel();
		outTaskOutputModel.getSupports().forEach(model->{
			if(model.getOrganizationId().equals(belongOrganizationModel.getOrganizationId())) {
				if(model.getOrganizationUserId() != null) {
					apiUserGroupExpandModel.setUserId(model.getOrganizationUserId().toString());
					apiUserGroupExpandModel.setUserName(model.getOrganizationUserName().toString());
				}
			}
		});
		TaskDivisionUserForm taskDivisionUserForm = new TaskDivisionUserForm(idTask, false,null,apiUserGroupExpandModel);
		taskDivisionUserForm.addChangeListener(e->{
			if(isAllowBroadcast == true) {
				doNotificationToOrtherUser();
			}else {
				loadData();
			}
			dialogTemplate.close();
		});

		dialogTemplate.getBtnSave().addClickListener(e->{
			taskDivisionUserForm.save();
		});

		dialogTemplate.setHeightFull();
		dialogTemplate.setWidth("50%");
		dialogTemplate.add(taskDivisionUserForm);
		dialogTemplate.setLayoutMobile();
		dialogTemplate.open();

	}

	//Tạo nhiệm vụ con
	private void openDialogCreateChildTask(TaskOutputModel outputTaskModel) {
		DialogTemplate dialogTemplate = new DialogTemplate("GIAO TIẾP NHIỆM VỤ");
		TaskCreateChildFormV2 updateTaskForm = new TaskCreateChildFormV2(outputTaskModel.getDocId(), belongOrganizationModel, userAuthenticationModel, SessionUtil.getDetailOrg(), idTask);


		updateTaskForm.addChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
			if(isAllowBroadcast == true) {
				doNotificationToOrtherUser();
			}else {
				loadData();
			}
			dialogTemplate.close();
		});
		dialogTemplate.add(updateTaskForm);
		if(outputTaskModel.getAttachments().isEmpty()) {
			dialogTemplate.setWidth("60%");
			dialogTemplate.setHeightFull();
		}else {
			dialogTemplate.setSizeFull();
		}
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setLayoutMobile();
		dialogTemplate.open();
	}

	private void doRedoAndReportAgain() {
		ApiCreatorDoRedoAndReportAgainModel creatorActionModel = new ApiCreatorDoRedoAndReportAgainModel();
		creatorActionModel.setCreator(getCreator());
		ApiResultResponse<Object> redoAndReportAgain = ApiActionService.doRedoAndReportAgain(idTask, creatorActionModel);
		if(redoAndReportAgain.isSuccess()) {
			if(isAllowBroadcast == true) {
				doNotificationToOrtherUser();
			}else {
				loadData();
			}
		}
	}

	//Xin được thực lại nhiệm vụ vì một lý do nào đó
	 
	@SuppressWarnings("deprecation")
	private void openConfirmReverseCompleted() {
		ConfirmDialogTemplate confirmDialogTemplate = new ConfirmDialogTemplate("TRIỆU HỒI LẠI NHIỆM VỤ");

		listIdAttachment = new ArrayList<String>();
		listDataFileUpload = new ArrayList<UploadModuleDataModel>();

		MultiSelectComboBox<UploadModuleDataModel> multiSelectCmbComment = new MultiSelectComboBox<UploadModuleDataModel>("Danh sách đính kèm");
		multiSelectCmbComment.setVisible(false);
		multiSelectCmbComment.setWidthFull();
		multiSelectCmbComment.setItemLabelGenerator(UploadModuleDataModel::getFileName);

		ButtonTemplate btnAttachment = new ButtonTemplate("Thêm đính kèm",FontAwesome.Solid.PAPERCLIP.create());
		btnAttachment.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnAttachment.getStyle().setMarginLeft("auto");
		btnAttachment.addClickListener(e->{
			openDialogUploadFile(()->{
				multiSelectCmbComment.setItems(listDataFileUpload);
				multiSelectCmbComment.select(listDataFileUpload);
				if(listDataFileUpload.isEmpty()) {
					multiSelectCmbComment.setVisible(false);
				}else {
					multiSelectCmbComment.setVisible(true);
				}
			});
		});

		TextArea txtReasonReverse = new TextArea("Lý do triệu hồi lại nhiệm vụ");
		txtReasonReverse.setWidthFull();
		txtReasonReverse.setHeight("200px");

		VerticalLayout vLayoutReverse = new VerticalLayout();
		vLayoutReverse.setWidthFull();
		vLayoutReverse.add(txtReasonReverse,btnAttachment,multiSelectCmbComment);

		confirmDialogTemplate.add(vLayoutReverse);
		if(txtReasonReverse.getValue().isEmpty()) {
			confirmDialogTemplate.getBtnConfirm().setEnabled(false);
		}
		txtReasonReverse.addValueChangeListener(e->{
			if(txtReasonReverse.getValue().isEmpty()) {
				confirmDialogTemplate.getBtnConfirm().setEnabled(false);
			}else {
				confirmDialogTemplate.getBtnConfirm().setEnabled(true);
			}
		});
		confirmDialogTemplate.addConfirmListener(e->doReverseCompleted(txtReasonReverse.getValue()));
		confirmDialogTemplate.open();
	}

	private void doReverseCompleted(String reasonReverse) {
		ApiReverseModel apiReverseModel = new ApiReverseModel();
		apiReverseModel.setAttachments(listIdAttachment);
		apiReverseModel.setReasonReverse(reasonReverse);
		apiReverseModel.setCreator(getCreator());


		ApiResultResponse<Object> reverse = ApiActionService.doReverse(idTask, apiReverseModel);
		if(reverse.isSuccess()) {
			NotificationTemplate.success("Thành công");
			fireEvent(new ClickEvent(this,false));
			if(isAllowBroadcast == true) {
				doNotificationToOrtherUser();
			}else {
				loadData();
			}
		}
	}

	// Giao lại nhiệm vụ nếu đơn vị được giao từ chối nhiệm vụ 
	private void openDialogAssignTaskAgain() {
		DialogTemplate dialogTemplate = new DialogTemplate("Giao lại nhiệm vụ");

		//This class CreateTaskForm in package doc -> forms
		TaskCreateForm taskAssginAgain = new TaskCreateForm(outTaskOutputModel.getDocId(), belongOrganizationModel, userAuthenticationModel, SessionUtil.getDetailOrg(), idTask);
		taskAssginAgain.setCheckAssignTaskAgain(true);
		taskAssginAgain.addChangeListener(e->{
			if(isAllowBroadcast == true) {
				doNotificationToOrtherUser();
			}else {
				loadData();
			}
			fireEvent(new ClickEvent(this,false));
			dialogTemplate.close();
		});
		dialogTemplate.add(taskAssginAgain);
		if(outTaskOutputModel.getAttachments().isEmpty()) {
			dialogTemplate.setWidth("50%");
			dialogTemplate.setHeightFull();
		}else {
			dialogTemplate.setSizeFull();
		}
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setLayoutMobile();
		dialogTemplate.open();

	}

	//Xác nhận nhiệm vụ đã hoàn thành sau khi người được giao báo cáo hoàn thành
	 
	@SuppressWarnings("deprecation")
	private void openComfirmDoComfirm() {
		ConfirmDialogTemplate confirmDialogTemplate = new ConfirmDialogTemplate("XÁC NHẬN NHIỆM VỤ ĐÃ HOÀN THÀNH");

		VerticalLayout vLayoutConfirm = new VerticalLayout();

		vLayoutConfirm.add(createLayoutKeyAndValue("Ngày giao nhiệm vụ: ", LocalDateUtil.dfDateTime.format(outTaskOutputModel.getCreatedTime()), "135px",null));

		DateTimePicker dateEndTime = new DateTimePicker("Hạn xử lý nhiệm vụ");
		dateEndTime.setLocale(LocalDateUtil.localeVietNam());


		TextField txtEndTime = new TextField("Hạn xử lý nhiệm vụ");
		txtEndTime.setId("damnn");
		if(outTaskOutputModel.getEndTime() != 0) {

			LocalDateTime localEndTime = LocalDateUtil.longToLocalDateTime(outTaskOutputModel.getEndTime());
			dateEndTime.setValue(localEndTime);
			dateEndTime.setReadOnly(true);
			dateEndTime.setWidthFull();


			vLayoutConfirm.add(dateEndTime);
		}else {

			txtEndTime.setWidthFull();
			txtEndTime.setValue("Nhiệm vụ này không hạn");
			txtEndTime.setReadOnly(true);
			txtEndTime.setId("khonghan");

			vLayoutConfirm.add(txtEndTime);
		}

		DateTimePicker dateTimePicker = new DateTimePicker("Xác nhận thời gian hoàn thành");
		dateTimePicker.setValue(LocalDateTime.now());
		dateTimePicker.setMax(LocalDateTime.now().plusDays(1));
		dateTimePicker.setLocale(LocalDateUtil.localeVietNam());

		ComboBox<Pair<String, String>> cmbCheckStatus = new ComboBox<Pair<String,String>>("Trạng thái");
		cmbCheckStatus.setWidthFull();
		cmbCheckStatus.setReadOnly(true);
		cmbCheckStatus.setItems(getListStatusCompleted());
		cmbCheckStatus.setItemLabelGenerator(Pair::getValue);
		if(txtEndTime.getId().get().equals("khonghan")) {
			cmbCheckStatus.setValue(cmbCheckStatus.getListDataView().getItem(2));
		}else {
			dateTimePicker.setMin(LocalDateUtil.longToLocalDateTime(outTaskOutputModel.getCreatedTime()));
			if(dateEndTime.getValue().isBefore(dateTimePicker.getValue())) {
				cmbCheckStatus.setValue(cmbCheckStatus.getListDataView().getItem(1));
			}else if(dateEndTime.getValue().isAfter(dateTimePicker.getValue())) {
				cmbCheckStatus.setValue(cmbCheckStatus.getListDataView().getItem(0));
			}
		}
		dateTimePicker.addValueChangeListener(e->{
			if(txtEndTime.getId().get().equals("khonghan")) {
				cmbCheckStatus.setValue(cmbCheckStatus.getListDataView().getItem(2));
			}else {
				if(dateEndTime.getValue().isBefore(dateTimePicker.getValue())) {
					cmbCheckStatus.setValue(cmbCheckStatus.getListDataView().getItem(1));
				}else if(dateEndTime.getValue().isAfter(dateTimePicker.getValue())) {
					cmbCheckStatus.setValue(cmbCheckStatus.getListDataView().getItem(0));
				}
			}
		});


		vLayoutConfirm.add(dateTimePicker,cmbCheckStatus);

		confirmDialogTemplate.add(vLayoutConfirm);
		confirmDialogTemplate.addConfirmListener(e->doConfirm(LocalDateUtil.localDateTimeToLong(dateTimePicker.getValue())));
		confirmDialogTemplate.open();
	}

	//Tạo danh sách status
	private List<Pair<String, String>> getListStatusCompleted(){
		List<Pair<String, String>> listStatusCompleted = new ArrayList<Pair<String,String>>();
		listStatusCompleted.add(Pair.of("tronghan","Xác nhận nhiệm vụ trong hạn"));
		listStatusCompleted.add(Pair.of("quahan","Xác nhận nhiệm vụ quá hạn"));
		listStatusCompleted.add(Pair.of("khonghan","Nhiệm vụ không hạn"));
		return listStatusCompleted;
	}

	private void doConfirm(long completedTime) {
		ApiConfirmModel apiConfirmModel = new ApiConfirmModel();
		apiConfirmModel.setCompletedTime(completedTime);
		apiConfirmModel.setCreator(getCreator());
		System.out.println(apiConfirmModel);
		ApiResultResponse<Object> confirm = ApiActionService.doConfirm(idTask, apiConfirmModel);
		if(confirm.isSuccess()) {
			if(isAllowBroadcast == true) {
				doNotificationToOrtherUser();
			}else {
				loadData();
			}
			NotificationTemplate.success("Thành công");
			fireEvent(new ClickEvent(this,false));
		}
	}

	// Từ chối báo cáo của đơn vị xử lý
	private void openDialogRefuseConfirm() {
		DialogTemplate dialogTemplate = new DialogTemplate("TỪ CHỐI BÁO CÁO");

		TaskRefuseConfirmForm taskRefuseConfirmForm = new TaskRefuseConfirmForm(idTask);
		dialogTemplate.add(taskRefuseConfirmForm);
		taskRefuseConfirmForm.addChangeListener(e->{
			if(isAllowBroadcast == true) {
				doNotificationToOrtherUser();
			}else {
				loadData();
			}
			dialogTemplate.close();
		});

		dialogTemplate.getBtnSave().setText("Xác nhận từ chối báo cáo");
		dialogTemplate.getBtnSave().addClickListener(e->{
			taskRefuseConfirmForm.refuseConfirm();
		});

		dialogTemplate.setHeightFull();
		dialogTemplate.setWidth("70%");
		dialogTemplate.open();
	}

	//Báo cáo nhiệm vụ đã hoàn thành cho đơn vị giao nhiệm vụ
	 
	@SuppressWarnings("deprecation")
	private void openConfirmReport() {
		ConfirmDialogTemplate confirmDialogTemplate = new ConfirmDialogTemplate("BÁO CÁO HOÀN THÀNH NHIỆM VỤ");

		listIdAttachment = new ArrayList<String>();
		listDataFileUpload = new ArrayList<UploadModuleDataModel>();

		MultiSelectComboBox<UploadModuleDataModel> multiSelectCmbComment = new MultiSelectComboBox<UploadModuleDataModel>("Danh sách đính kèm");
		multiSelectCmbComment.setVisible(false);
		multiSelectCmbComment.setWidthFull();
		multiSelectCmbComment.setItemLabelGenerator(UploadModuleDataModel::getFileName);

		ButtonTemplate btnAttachment = new ButtonTemplate("Thêm đính kèm",FontAwesome.Solid.PAPERCLIP.create());
		btnAttachment.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnAttachment.getStyle().setMarginLeft("auto");
		btnAttachment.addClickListener(e->{
			openDialogUploadFile(()->{
				multiSelectCmbComment.setItems(listDataFileUpload);
				multiSelectCmbComment.select(listDataFileUpload);
				if(listDataFileUpload.isEmpty()) {
					multiSelectCmbComment.setVisible(false);
				}else {
					multiSelectCmbComment.setVisible(true);
				}
			});
		});

		VerticalLayout vLayoutReport = new VerticalLayout();
		vLayoutReport.setWidthFull();

//		if(!outTaskOutputModel.getProcesses().isEmpty() && outTaskOutputModel.getProcesses().get(0).getPercent() == 100) {
//			Span span = new Span("Xác nhận báo cáo nhiệm vụ đã hoàn thành");
//			vLayoutReport.add(span);
//			confirmDialogTemplate.addConfirmListener(e->doReport());
//		}else {
			NumberField numberField = new NumberField("Cập nhật tiến độ hoàn thành");
			numberField.setWidthFull();
			numberField.setValue(100.0);
			numberField.setReadOnly(true);

			TextArea txtExplain = new TextArea("Diễn giải");
			txtExplain.setWidthFull();
			txtExplain.setPlaceholder("Đã hoàn thành");
			txtExplain.setHeight("200px");
			

			vLayoutReport.add(numberField,txtExplain);
			confirmDialogTemplate.addConfirmListener(e->{
				String explain = txtExplain.getValue().isEmpty() ? "Đã hoàn thành" : txtExplain.getValue();
				List<String> listFileOfPerogress = new ArrayList<String>();
				if(outTaskOutputModel.getProcesses() != null) {
					if(!outTaskOutputModel.getProcesses().isEmpty()) {
						listFileOfPerogress.addAll(outTaskOutputModel.getProcesses().get(0).getAttachments());
					}
				}

				//Update progress again before report task
				doUpdateProgress(100.0, explain, listFileOfPerogress,()->{
					doReport();
				});
			});
//		}

		vLayoutReport.add(btnAttachment,multiSelectCmbComment);

		confirmDialogTemplate.add(vLayoutReport);


		confirmDialogTemplate.open();
	}

	//Báo cáo nhiệm vụ hoàn thành
	private void openDialogReport() {
		DialogTemplate dialogTemplate = new DialogTemplate("BÁO CÁO HOÀNH THÀNH NHIỆM VỤ");

		TaskReportChildrenForm taskListChildrenForm = new TaskReportChildrenForm(idTask);
		dialogTemplate.add(taskListChildrenForm);

		taskListChildrenForm.addChangeListener(e->{
			if(isAllowBroadcast == true) {
				doNotificationToOrtherUser();
			}else {
				loadData();
			}
			dialogTemplate.close();
		});

		dialogTemplate.getBtnSave().setText("Tổng hợp báo cáo");

		dialogTemplate.getBtnSave().addClickListener(e->{
			taskListChildrenForm.createLayoutReport();
		});

		dialogTemplate.setSizeFull();
		dialogTemplate.open();
	}

	private int countTaskSubCompleted() {
		int countCompleted = 0;
		ApiResultResponse<List<ApiOutputTaskModel>> data = ApiTaskService.getListSubTask(idTask);
		if(data.isSuccess()) {
			for(ApiOutputTaskModel apiOutputTaskModel : data.getResult()) {
				if(apiOutputTaskModel.getCompleted() != null) {
					countCompleted++;
				}
			}

		}
		return countCompleted;
	}

	private void doReport() {
		ApiReportModel apiReportModel = new ApiReportModel();
		apiReportModel.setCompletedTime(LocalDateUtil.localDateTimeToLong(LocalDateTime.now()));
		apiReportModel.setAttachments(listIdAttachment);
		apiReportModel.setCreator(getCreator());
		
		System.out.println("data report ne: "+apiReportModel);
		
		ApiResultResponse<Object> report = ApiActionService.doReport(idTask, apiReportModel);
		if(report.isSuccess()) {
			if(isAllowBroadcast == true) {
				doNotificationToOrtherUser();
			}else {
				loadData();
			}
			NotificationTemplate.success("Thành công");
			fireEvent(new ClickEvent(this,false));
		}
	}

	//Phân cán bộ xử lý
	private void openDialogAssignUserAssignee() {
		DialogTemplate dialogTemplate = new DialogTemplate("PHÂN CÁN BỘ XỬ LÝ");
		ApiUserGroupExpandModel apiUserGroupExpandModel = new ApiUserGroupExpandModel();
		if(outTaskOutputModel.getAssignee().getOrganizationUserId() != null) {
			apiUserGroupExpandModel.setUserId(outTaskOutputModel.getAssignee().getOrganizationUserId().toString());
		}
		TaskDivisionUserForm taskDivisionUserForm = new TaskDivisionUserForm(idTask,true,apiUserGroupExpandModel,null);
		taskDivisionUserForm.addChangeListener(e->{
			if(isAllowBroadcast == true) {
				doNotificationToOrtherUser();
			}else {
				loadData();
			}
			dialogTemplate.close();
			fireEvent(new ClickEvent(this,false));
		});

		dialogTemplate.getBtnSave().addClickListener(e->{
			taskDivisionUserForm.save();
		});

		dialogTemplate.setHeightFull();
		dialogTemplate.setWidth("50%");
		dialogTemplate.add(taskDivisionUserForm);
		dialogTemplate.setLayoutMobile();
		dialogTemplate.open();
	}

	//Từ chối thực hiện nhiệm vụ
	 
	@SuppressWarnings("deprecation")
	private void openConfirmRefuse() {
		ConfirmDialogTemplate confirmDialogTemplate = new ConfirmDialogTemplate("TỪ CHỐI NHIỆM VỤ");
		VerticalLayout vLayoutConfirmRefuse = new VerticalLayout();
		vLayoutConfirmRefuse.setWidthFull();
		listDataFileUpload = new ArrayList<UploadModuleDataModel>();
		listIdAttachment = new ArrayList<String>();
		MultiSelectComboBox<UploadModuleDataModel> multiSelectCmbComment = new MultiSelectComboBox<UploadModuleDataModel>("Danh sách đính kèm");
		multiSelectCmbComment.setVisible(false);
		multiSelectCmbComment.setWidthFull();
		multiSelectCmbComment.setItemLabelGenerator(UploadModuleDataModel::getFileName);

		ButtonTemplate btnAttach = new ButtonTemplate("Thêm đính kèm",FontAwesome.Solid.PAPERCLIP.create());
		btnAttach.getStyle().setMarginLeft("auto").setMarginTop("-10px");
		btnAttach.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnAttach.addClickListener(e->{
			openDialogUploadFile(()->{
				multiSelectCmbComment.setItems(listDataFileUpload);
				multiSelectCmbComment.select(listDataFileUpload);
				if(listDataFileUpload.isEmpty()) {
					multiSelectCmbComment.setVisible(false);
				}else {
					multiSelectCmbComment.setVisible(true);
				}
			});
		});



		TextArea txtReasonRefuse = new TextArea("Lý do từ chối");
		txtReasonRefuse.setWidthFull();
		txtReasonRefuse.setHeight("200px");
		
		vLayoutConfirmRefuse.add(txtReasonRefuse,btnAttach,multiSelectCmbComment);

		confirmDialogTemplate.add(vLayoutConfirmRefuse);

		confirmDialogTemplate.setText("Xác nhận từ");
		confirmDialogTemplate.open();

		confirmDialogTemplate.addConfirmListener(e->{
			if(!txtReasonRefuse.getValue().isEmpty()) {
				deRefuse(txtReasonRefuse.getValue(),listIdAttachment);
				confirmDialogTemplate.close();
			}else {
				txtReasonRefuse.setErrorMessage("Vui lòng nhập lý do từ chối");
				txtReasonRefuse.setInvalid(true);
				txtReasonRefuse.focus();
			}
		});

		if(txtReasonRefuse.getValue().isBlank()) {
			confirmDialogTemplate.getBtnConfirm().setEnabled(false);
		}

		txtReasonRefuse.addValueChangeListener(e->{
			if(txtReasonRefuse.getValue().isBlank()) {
				confirmDialogTemplate.getBtnConfirm().setEnabled(false);
			}else {
				confirmDialogTemplate.getBtnConfirm().setEnabled(true);
			}
		});

	}


	private void deRefuse(String reasonRefuse,List<String> listIdAttachment) {
		ApiRefuseModel apiRefuseModel = new ApiRefuseModel();
		apiRefuseModel.setReasonRefuse(reasonRefuse);
		apiRefuseModel.setAttachments(listIdAttachment);

		ApiCreatorActionModel creator = getCreator();
		apiRefuseModel.setCreator(creator);

		ApiResultResponse<Object> data = ApiActionService.doRefuse(idTask, apiRefuseModel);
		if(data.isSuccess()) {
			NotificationTemplate.success("Đã từ chối nhiệm vụ này");
			if(isAllowBroadcast == true) {
				doNotificationToOrtherUser();
			}else {
				loadData();
			}
			fireEvent(new ClickEvent(this,false));
		}
	}

	//Thực hiện lại nhiệm vụ
	private void openConfirmRedo() {
		DialogTemplate dialogTemplate = new DialogTemplate("THỰC HIỆN LẠI NHIỆM VỤ");
		TextArea txtReasonRedo = new TextArea("Lý do yêu cầu làm lại");
		txtReasonRedo.setWidthFull();
		txtReasonRedo.setHeight("200px");
		
		UploadModuleBasic uploadModuleBasic = new UploadModuleBasic();
		uploadModuleBasic.initUpload();
		dialogTemplate.add(txtReasonRedo,uploadModuleBasic);
		dialogTemplate.getBtnSave().addClickListener(e->{
			listIdAttachment = new ArrayList<String>();
			uploadFile(uploadModuleBasic.getListFileUpload());
			if(!txtReasonRedo.isEmpty()) {
				if(doRedo(txtReasonRedo.getValue(), listIdAttachment)) {
					if(isAllowBroadcast == true) {
						doNotificationToOrtherUser();
					}else {
						loadData();
					}
					dialogTemplate.close();
				}
			}else {
				txtReasonRedo.setErrorMessage("Vui lòng nhập lý do làm lại");
				txtReasonRedo.setInvalid(true);
				txtReasonRedo.focus();
			}

		});

		dialogTemplate.setWidth("40%");
		dialogTemplate.setLayoutMobile();
		dialogTemplate.open();

	}

	private boolean doRedo(String reasonRedo,List<String> listAttachment) {
		ApiRedoModel apiRedoModel = new ApiRedoModel();
		apiRedoModel.setReasonRedo(reasonRedo);
		apiRedoModel.setCreator(getCreator());
		apiRedoModel.setAttachments(listAttachment);
		ApiResultResponse<Object> redo = ApiActionService.doRedo(idTask, apiRedoModel);
		if(redo.isSuccess()) {
			NotificationTemplate.success("Thành công");
			fireEvent(new ClickEvent(this,false));
			if(isAllowBroadcast == true) {
				doNotificationToOrtherUser();
			}else {
				loadData();
			}
			return true;
		}

		return false;

	}

	//Đánh giá nhiệm vụ sau khi hoàn thành
	private void openConfirmRating() {
		DialogTemplate confirmDialogTemplate = new DialogTemplate("ĐÁNH GIÁ NHIỆM VỤ");
		TaskRatingForm taskRatingForm = new TaskRatingForm(outTaskOutputModel.getRating(),outTaskOutputModel.isRequiredKpi());
		confirmDialogTemplate.add(taskRatingForm);
		confirmDialogTemplate.getBtnSave().addClickListener(e->{
			if(taskRatingForm.getTxtDesr().getValue().isEmpty()) {
				taskRatingForm.getTxtDesr().setErrorMessage("Vui lòng nhập nhận xét");
				taskRatingForm.getTxtDesr().setInvalid(true);
				taskRatingForm.getTxtDesr().focus();
			}else {
				if(outTaskOutputModel.isRequiredKpi()) {
					if(doRatingKPI(taskRatingForm.getStar(), taskRatingForm.getExplain(), 
							taskRatingForm.getSliderMarkA().getMark(), 
							taskRatingForm.getSliderMarkB().getMark(), 
							taskRatingForm.getSliderMarkC().getMark())) {
						if(isAllowBroadcast == true) {
							doNotificationToOrtherUser();
						}else {
							loadData();
						}
						confirmDialogTemplate.close();
					}
					
				}else {
					if(doRating(taskRatingForm.getStar(), taskRatingForm.getExplain())) {
						if(isAllowBroadcast == true) {
							doNotificationToOrtherUser();
						}else {
							loadData();
						}
						confirmDialogTemplate.close();
					}
				}
			}
		});

		confirmDialogTemplate.setWidth("30%");
		confirmDialogTemplate.setLayoutMobile();
		confirmDialogTemplate.open();

	}

	private boolean doRating(int star,String explain) {
		ApiRatingModel apiRatingModel = new ApiRatingModel();
		apiRatingModel.setExplain(explain);
		apiRatingModel.setStar(star);
		apiRatingModel.setCreator(getCreator());
		ApiResultResponse<Object> rating = ApiActionService.doRating(idTask, apiRatingModel);
		if(rating.isSuccess()) {
			NotificationTemplate.success("Thành công");
			fireEvent(new ClickEvent(this,false));
			return true;
		}else {
			NotificationTemplate.warning(rating.getMessage());
			return false;
		}
	}
	
	private boolean doRatingKPI(int star, String explain, double markA, double markB, double markC) {
		ApiRatingModel apiRatingModel = new ApiRatingModel();
		apiRatingModel.setExplain(explain);
		apiRatingModel.setStar(star);
		apiRatingModel.setCreator(getCreator());
		apiRatingModel.setMarkA(markA);
		apiRatingModel.setMarkB(markB);
		apiRatingModel.setMarkC(markC);
		ApiResultResponse<Object> rating = ApiActionService.doRating(idTask, apiRatingModel);
		if(rating.isSuccess()) {
			NotificationTemplate.success("Thành công");
			fireEvent(new ClickEvent(this,false));
			return true;
		}else {
			NotificationTemplate.warning(rating.getMessage());
			return false;
		}
	}

	//Xác nhận nhiệm vụ đã hoàn thành khi đơn vị giao không yêu cầu báo cáo hoàn thành.
	 
	@SuppressWarnings("deprecation")
	private void openConfirmCompleted() {
		ConfirmDialogTemplate confirmDialogTemplate = new ConfirmDialogTemplate("XÁC NHẬN HOÀN THÀNH NHIỆM VỤ");
		DateTimePicker dateTimePicker = new DateTimePicker("Thời gian hoàn thành nhiệm vụ");

		listIdAttachment = new ArrayList<String>();
		listDataFileUpload = new ArrayList<UploadModuleDataModel>();

		MultiSelectComboBox<UploadModuleDataModel> multiSelectCmbComment = new MultiSelectComboBox<UploadModuleDataModel>("Danh sách đính kèm");
		multiSelectCmbComment.setVisible(false);
		multiSelectCmbComment.setWidthFull();
		multiSelectCmbComment.setItemLabelGenerator(UploadModuleDataModel::getFileName);

		ButtonTemplate btnAttachment = new ButtonTemplate("Thêm đính kèm",FontAwesome.Solid.PAPERCLIP.create());
		btnAttachment.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnAttachment.getStyle().setMarginLeft("auto");
		btnAttachment.addClickListener(e->{
			openDialogUploadFile(()->{
				multiSelectCmbComment.setItems(listDataFileUpload);
				multiSelectCmbComment.select(listDataFileUpload);
				if(listDataFileUpload.isEmpty()) {
					multiSelectCmbComment.setVisible(false);
				}else {
					multiSelectCmbComment.setVisible(true);
				}
			});
		});

		listIdAttachment = new ArrayList<String>();
		dateTimePicker.setValue(LocalDateTime.now());

		VerticalLayout vLayoutConfirm = new VerticalLayout();


		if(!outTaskOutputModel.getProcesses().isEmpty() && outTaskOutputModel.getProcesses().get(0).getPercent() == 100) {
			vLayoutConfirm.add(dateTimePicker);
			confirmDialogTemplate.addConfirmListener(e->{
				doCompleted(LocalDateUtil.localDateTimeToLong(dateTimePicker.getValue()));
			});
		}else {

			NumberField numberField = new NumberField("Cập nhật tiến độ hoàn thành");
			numberField.setWidthFull();
			numberField.setValue(100.0);
			numberField.setReadOnly(true);
			vLayoutConfirm.add(dateTimePicker,numberField);
			vLayoutConfirm.setWidthFull();
			confirmDialogTemplate.addConfirmListener(e->{
				doUpdateProgress(100.0, "Đã hoàn thành", new ArrayList<String>(),()->{
					doCompleted(LocalDateUtil.localDateTimeToLong(dateTimePicker.getValue()));
				});
			});
		}
		vLayoutConfirm.add(btnAttachment,multiSelectCmbComment);
		confirmDialogTemplate.add(vLayoutConfirm);


		confirmDialogTemplate.open();
	}

	//Cập nhật tiến độ hoàn thành
	private void doUpdateProgress(double percent,String explain,List<String> listAttachments,Runnable run) {
		ApiUpdateProcessModel apiUpdateProcessModel = new ApiUpdateProcessModel();
		apiUpdateProcessModel.setPercent(percent);
		apiUpdateProcessModel.setExplain(explain);
		apiUpdateProcessModel.setCreator(getCreator());
		apiUpdateProcessModel.setAttachments(listAttachments);
		ApiResultResponse<Object> process = ApiActionService.doUpdateProcess(idTask, apiUpdateProcessModel);
		if(process.isSuccess()) {
			run.run();
		}
	}

	private void doCompleted(long completedTime) {
		ApiCompletedInputModel apiCompletedModel = new ApiCompletedInputModel();
		apiCompletedModel.setAttachments(listIdAttachment);
		apiCompletedModel.setCompletedTime(completedTime);
		apiCompletedModel.setCreator(getCreator());
		if(isOwner) {
			apiCompletedModel.setIgnoreRequiredConfirm(true);
		}
		ApiResultResponse<Object> completed = ApiActionService.doCompleted(idTask, apiCompletedModel);
		if(completed.isSuccess()) {
			if(isAllowBroadcast == true) {
				doNotificationToOrtherUser();
			}else {
				loadData();
			}
			NotificationTemplate.success("Nhiệm vụ đã hoàn thành");
			fireEvent(new ClickEvent(this,false));
		}else {
			NotificationTemplate.error(completed.getMessage());
		}
	}

	//Bắt đầu thực hiện nhiệm vụ
	private void openConfirmAccept() {
		ConfirmDialogTemplate confirmDialogTemplate = new ConfirmDialogTemplate("BẮT ĐẦU THỰC HIỆN NHIỆM VỤ");
		confirmDialogTemplate.setText("Bạn có muốn bắt đầu thực hiện nhiệm vụ này?");
		confirmDialogTemplate.addConfirmListener(e->{
			doAccept();
		});
		confirmDialogTemplate.open();
	}

	private void doAccept() {
		ApiAcceptModel apiAcceptModel = new ApiAcceptModel();
		apiAcceptModel.setCreator(getCreator());
		ApiResultResponse<Object> accept = ApiActionService.doAccept(idTask, apiAcceptModel);
		if(accept.isSuccess()) {
			NotificationTemplate.success("Thành công");
			if(isAllowBroadcast == true) {
				doNotificationToOrtherUser();
			}else {
				loadData();
			}
			fireEvent(new ClickEvent(this,false));
		}
	}

	//Nhắc nhở thực hiện
	private void openDialogRemid() {
		DialogTemplate dialogTemplate = new DialogTemplate("NHẮC NHỞ");
		TaskRemidForm taskRemidForm = new TaskRemidForm();

		dialogTemplate.getBtnSave().setText("Nhắc nhở");
		dialogTemplate.add(taskRemidForm);
		dialogTemplate.getBtnSave().addClickListener(e->{
			listIdAttachment = new ArrayList<String>();
			uploadFile(taskRemidForm.getListUpload());
			if(doRemid(taskRemidForm.getReasonRemind(),listIdAttachment)) {
				dialogTemplate.close();
			}
		});

		dialogTemplate.setWidth("30%");
		dialogTemplate.setLayoutMobile();
		dialogTemplate.open();
	}

	private boolean doRemid(String reasonRemind,List<String> listAttachments) {
		ApiRemindModel apiRemindModel = new ApiRemindModel();
		apiRemindModel.setReasonRemind(reasonRemind);
		apiRemindModel.setAttachments(listAttachments);
		apiRemindModel.setCreator(getCreator());
		ApiResultResponse<Object> remind = ApiActionService.doRemind(idTask, apiRemindModel);
		if(remind.isSuccess()) {
			NotificationTemplate.success("Thành công");
			fireEvent(new ClickEvent(this,false));
			if(isAllowBroadcast == true) {
				doNotificationToOrtherUser();
			}else {
				loadData();
			}
			return true;
		}else {
			NotificationTemplate.error(remind.getMessage());
			return false;
		}
	}

	//Tạm dừng thực hiện nhiệm vụ
	 
	@SuppressWarnings("deprecation")
	private void openConfirmPedding() {
		ConfirmDialogTemplate confirmDialogTemplate = new ConfirmDialogTemplate("TẠM HOÃN NHIỆM VỤ");

		MultiSelectComboBox<UploadModuleDataModel> multiSelectCmbComment = new MultiSelectComboBox<UploadModuleDataModel>("Danh sách đính kèm");
		multiSelectCmbComment.setVisible(false);
		multiSelectCmbComment.setWidthFull();
		multiSelectCmbComment.setItemLabelGenerator(UploadModuleDataModel::getFileName);

		ButtonTemplate btnAttachment = new ButtonTemplate("Thêm đính kèm",FontAwesome.Solid.PAPERCLIP.create());
		btnAttachment.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnAttachment.getStyle().setMarginLeft("auto");
		btnAttachment.addClickListener(e->{
			openDialogUploadFile(()->{
				multiSelectCmbComment.setItems(listDataFileUpload);
				multiSelectCmbComment.select(listDataFileUpload);
				if(listDataFileUpload.isEmpty()) {
					multiSelectCmbComment.setVisible(false);
				}else {
					multiSelectCmbComment.setVisible(true);
				}});
		});

		TextArea txtReasonPedding = new TextArea("Lý do tạm hoãn nhiệm vụ");
		txtReasonPedding.setWidthFull();
		txtReasonPedding.setHeight("200px");

		VerticalLayout vLayoutPedding = new VerticalLayout();
		vLayoutPedding.setWidthFull();
		vLayoutPedding.add(txtReasonPedding,btnAttachment,multiSelectCmbComment);

		confirmDialogTemplate.add(vLayoutPedding);

		if(txtReasonPedding.getValue().isEmpty()) {
			confirmDialogTemplate.getBtnConfirm().setEnabled(false);
		}
		txtReasonPedding.addValueChangeListener(e->{
			if(!txtReasonPedding.isEmpty()) {
				confirmDialogTemplate.getBtnConfirm().setEnabled(true);
			}
		});
		confirmDialogTemplate.addConfirmListener(e->doPedding(txtReasonPedding.getValue()));
		confirmDialogTemplate.open();
	}

	private void doPedding(String reasonPedding) {
		ApiPedingModel apiPedingModel = new ApiPedingModel();
		apiPedingModel.setAttachments(listIdAttachment);
		apiPedingModel.setCreator(getCreator());
		apiPedingModel.setReasonPending(reasonPedding);
		ApiResultResponse<Object> pedding = ApiActionService.doPedding(idTask, apiPedingModel);
		if(pedding.isSuccess()) {
			if(isAllowBroadcast == true) {
				doNotificationToOrtherUser();
			}else {
				loadData();
			}
			NotificationTemplate.warning("Nhiệm vụ đã tạm dừng");
			fireEvent(new ClickEvent(this,false));
		}
	}

	//Tiếp tục thực hiện nhiệm vụ sau khi tạm dừng
	private void openConfirmUnPedding() {
		ConfirmDialogTemplate confirmDialogTemplate = new ConfirmDialogTemplate("TIẾP TỤC THỰC HIỆN NHIỆM VỤ");
		confirmDialogTemplate.setText("Xác nhận tiếp tục thực hiện nhiệm vụ");
		confirmDialogTemplate.addConfirmListener(e->doUnPedding());
		confirmDialogTemplate.open();
	}


	private void doUnPedding() {
		ApiUnpendingModel apiUnpendingModel = new ApiUnpendingModel();
		apiUnpendingModel.setCreator(getCreator());
		ApiResultResponse<Object> unPedding = ApiActionService.doUnPendding(idTask, apiUnpendingModel);
		if(unPedding.isSuccess()) {
			if(isAllowBroadcast == true) {
				doNotificationToOrtherUser();
			}else {
				loadData();
			}
			NotificationTemplate.success("Nhiệm vụ đã tiếp tục thực hiện");
			fireEvent(new ClickEvent(this,false));
		}
	}

	//Cập nhật nhiệm vụ
	private void openDialogUpdateTask(TaskOutputModel outputTaskModel) {
		DialogTemplate dialogTemplate = new DialogTemplate("CẬP NHẬT NHIỆM VỤ");

		//This class CreateTaskForm in package doc -> forms
//		TaskCreateForm updateTaskForm = new TaskCreateForm(outputTaskModel.getDocId(), belongOrganizationModel, userAuthenticationModel, SessionUtil.getDetailOrg(), idTask);
		TaskCreateFormV2 updateTaskForm = new TaskCreateFormV2(outputTaskModel.getDocId(), belongOrganizationModel, userAuthenticationModel, SessionUtil.getDetailOrg(), idTask);
		updateTaskForm.addChangeListener(e->{
			//			doNotificationToOrtherUser();
			fireEvent(new ClickEvent(this,false));
			refreshMainLayout();
			if(isAllowBroadcast == true) {
				doNotificationToOrtherUser();
			}else {
				loadData();
			}
			dialogTemplate.close();
		});
		dialogTemplate.add(updateTaskForm);
		if( docModel.getAttachments() == null || outputTaskModel.getAttachments().isEmpty() && docModel.getAttachments().isEmpty()) {
			dialogTemplate.setWidth("50%");
			dialogTemplate.setHeightFull();
		}else {
			dialogTemplate.setSizeFull();
		}
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setLayoutMobile();
		dialogTemplate.open();
	}

	//Xoa nhiệm vụ
	private void openConfirmDeleted(String idTask) {
		ConfirmDialogTemplate confirmDialogTemplate = new ConfirmDialogTemplate("XÓA NHIỆM VỤ");
		confirmDialogTemplate.setText("Xác nhận xóa nhiệm vụ này");
		confirmDialogTemplate.addConfirmListener(e->{
			doDeletedTask(idTask);
		});
		confirmDialogTemplate.setDelete();
		confirmDialogTemplate.open();
	}

	private void doDeletedTask(String idTask) {
		ApiResultResponse<Object> delete = ApiTaskService.doDelete(idTask);
		if(delete.isSuccess()) {
			checkDeleteTask = true;
			NotificationTemplate.success("Đã xóa nhiệm vụ thành công");
			fireEvent(new ClickEvent(this,false));
		}
	}

	public boolean isCheckDeleteTask() {
		return checkDeleteTask;
	}

	// Dialog upload file
	private void openDialogUploadFile(Runnable run) {
		DialogTemplate dialogTemplate = new DialogTemplate("Thêm đính kèm");

		TaskDoAttachmentForm taskDoAttachmentForm = new TaskDoAttachmentForm();

		taskDoAttachmentForm.addChangeListener(e->{
			listDataFileUpload.addAll(taskDoAttachmentForm.getListFileUpload());
			listIdAttachment.addAll(taskDoAttachmentForm.getListAttachment());
			run.run();
			dialogTemplate.close();
		});


		dialogTemplate.add(taskDoAttachmentForm);

		dialogTemplate.setWidth("50%");
		dialogTemplate.setHeight("70%");
		dialogTemplate.getBtnSave().setText("Thêm");
		dialogTemplate.setLayoutMobile();
		dialogTemplate.getBtnSave().addClickListener(e->{
			taskDoAttachmentForm.uploadFile();
		});

		dialogTemplate.open();
	}

	//Them file
	private void uploadFile(List<UploadModuleDataModel> listFile) {
		for(UploadModuleDataModel uploadModuleDataModel : listFile) {
			File file = new File(uploadModuleDataModel.getFileName());
			try {
				FileUtils.copyInputStreamToFile(uploadModuleDataModel.getInputStream(), file);
				ApiInputMediaModel apiInputMediaModel = new ApiInputMediaModel();
				apiInputMediaModel.setFile(new FileSystemResource(file));
				apiInputMediaModel.setDescription(uploadModuleDataModel.getDescription());
				apiInputMediaModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
				apiInputMediaModel.setOrganizationName(belongOrganizationModel.getOrganizationName());
				apiInputMediaModel.setOrganizationUserId(userAuthenticationModel.getId());
				apiInputMediaModel.setOrganizationUserName(userAuthenticationModel.getFullName());
				doCreateFile(apiInputMediaModel);
				FileUtils.delete(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void doCreateFile(ApiInputMediaModel apiModel) {
		try {
			ApiResultResponse<ApiMediaModel> data = ApiMediaService.createFile(apiModel);
			if(data.isSuccess()) {
				listIdAttachment.add(data.getResult().getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//Lấy thông tin của người đang thực hiện
	private ApiCreatorActionModel getCreator() {
		ApiCreatorActionModel actionModel = new ApiCreatorActionModel();
		actionModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
		actionModel.setOrganizationName(belongOrganizationModel.getOrganizationName());
		actionModel.setOrganizationUserId(userAuthenticationModel.getId());
		actionModel.setOrganizationUserName(userAuthenticationModel.getFullName());
		return actionModel;
	}

	private Component createLayoutKeyAndValue(String key,String value,String widthHeader,String style) {
		HorizontalLayout hlayout = new HorizontalLayout();

		Span spHeader = new Span(key);
		spHeader.setWidth(widthHeader);
		spHeader.getStyle().setFontWeight(600).setFlexShrink("0");

		Span spValue = new Span(value);
		if(style != null) {
			spValue.getStyle().setColor(style);
		}

		hlayout.setWidthFull();
		hlayout.add(spHeader,spValue);
		hlayout.getStyle().setBorderBottom("1px solid #c3c3c3").setPadding("5px");
		return hlayout;
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		if(isAllowBroadcast == true) {
			taskBroadcaster = BroadcastUtil.register(newStateTask->{
				if(!newStateTask.isEmpty()) {
					ui.access(()->{
						Gson gson = new Gson();
						JsonObject task = newStateTask.get(0);
						ApiOutputTaskModel apiOutputTaskModel = gson.fromJson(task, ApiOutputTaskModel.class);
						if(apiOutputTaskModel.getId().equals(idTask)) {
							outTaskOutputModel = new TaskOutputModel(apiOutputTaskModel);

							DocModel docModelNoti = new DocModel();
							if(apiOutputTaskModel.getDocModel() != null) {
								docModelNoti = new DocModel(apiOutputTaskModel.getDocModel());
							}
							

							listPinneds = new ArrayList<Component>();
							if(outTaskOutputModel.isRequiredConfirm()) {
								listPinneds.add(new PinnedStatusForm("Nhiệm vụ cần xác nhận khi báo cáo hoàn thành", "#034fc3"));
							}

							if(outTaskOutputModel.getCountSubTask() > 0) {
								listPinneds.add(new PinnedStatusForm("Nhiệm vụ đã phân từ nhiệm vụ: "+outTaskOutputModel.getCountSubTask(), "rgb(215 191 19)"));
								listPinneds.add(new PinnedStatusForm("Nhiệm vụ con đã hoàn thành: "+apiOutputTaskModel.getCountSubTaskIsCompleted()+"/"+outTaskOutputModel.getCountSubTask(), "rgb(32 205 0)"));
							}
							checkPermission(outTaskOutputModel);
							createLayoutCenter(outTaskOutputModel,docModelNoti);
							NotificationTemplate.warning("Nhiệm vụ vừa có cập nhật mới");
						}
					});
				}
			});
		}
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
		if(isAllowBroadcast == true) {
			taskBroadcaster.remove();
			taskBroadcaster = null;
		}
	}

	private void setAllButtonNotVisible() {
		btnAccept.setVisible(false);
		btnAssignAgain.setVisible(false);
		btnAssignUserAssignee.setVisible(false);
		btnAssignUserSupport.setVisible(false);
		btnAssignUserSupportAgain.setVisible(false);
		btnComfirm.setVisible(false);
		btnCompleted.setVisible(false);
		btnCreateChildTask.setVisible(false);
		btnDeleteTask.setVisible(false);
		btnEditAssignee.setVisible(false);
		btnHistory.setVisible(false);
		btnPedding.setVisible(false);
		btnRating.setVisible(false);
		btnReason.setVisible(false);
		btnReasonRefuse.setVisible(false);
		btnRedo.setVisible(false);
		btnRedoAndReportAgain.setVisible(false);
		btnRefuse.setVisible(false);
		btnRefuseConfirm.setVisible(false);
		btnRemind.setVisible(false);
		btnReportConfirmCompleted.setVisible(false);
		btnReverseCompleted.setVisible(false);
		btnUnPendding.setVisible(false);
		btnUpdate.setVisible(false);
	}

}
