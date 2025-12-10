package com.ngn.tdnv.task.forms.details;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.FileSystemResource;
import org.vaadin.addons.yuri0x7c1.bslayout.BsLayout;
import org.vaadin.addons.yuri0x7c1.bslayout.BsRow;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.flowingcode.vaadin.addons.fontawesome.FontAwesome.Solid.Icon;
import com.ngn.api.doc.ApiDocModel;
import com.ngn.api.doc.ApiDocService;
import com.ngn.api.media.ApiInputMediaModel;
import com.ngn.api.media.ApiMediaModel;
import com.ngn.api.media.ApiMediaService;
import com.ngn.api.organization.ApiUserGroupExpandModel;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tasks.ApiInputTaskModel;
import com.ngn.api.tasks.ApiOrgGeneralOfTaskModel;
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
import com.ngn.api.utils.ApiKeyValueModel;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.models.sign_in_org.SignInOrgModel;
import com.ngn.tdnv.doc.enumdoc.DocCategoryEnum;
import com.ngn.tdnv.doc.forms.DocEditForm;
import com.ngn.tdnv.doc.forms.DocListForm;
import com.ngn.tdnv.doc.forms.DocOverviewForm;
import com.ngn.tdnv.doc.forms.create_task.TaskCreateForm;
import com.ngn.tdnv.doc.models.DocModel;
import com.ngn.tdnv.task.enums.StatusTaskEnum;
import com.ngn.tdnv.task.forms.TaskCreateChildForm;
import com.ngn.tdnv.task.models.TaskCommentModel;
import com.ngn.tdnv.task.models.TaskCompletedModel;
import com.ngn.tdnv.task.models.TaskOutputModel;
import com.ngn.tdnv.task.models.TaskOutputRefuseModel;
import com.ngn.tdnv.task.models.TaskProcessModel;
import com.ngn.tdnv.task.models.TaskRateModel;
import com.ngn.tdnv.task.models.TaskRemindModel;
import com.ngn.utils.LocalDateUtil;
import com.ngn.utils.PropsUtil;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.ActtachmentForm;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.ConfirmDialogTemplate;
import com.ngn.utils.components.DetailsTemplate;
import com.ngn.utils.components.DialogTemplate;
import com.ngn.utils.components.MenuBarTemplate;
import com.ngn.utils.components.NotificationTemplate;
import com.ngn.utils.components.OnboardingView;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.ngn.utils.uploads.UploadModuleBasic;
import com.ngn.utils.uploads.UploadModuleDataModel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBoxVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayoutVariant;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Style.AlignItems;
import com.vaadin.flow.dom.Style.Display;
import com.vaadin.flow.dom.Style.FlexDirection;
import com.vaadin.flow.dom.Style.JustifyContent;
import com.vaadin.flow.dom.Style.Overflow;
import com.vaadin.flow.dom.Style.Position;

public class TaskViewDetailForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private Logger logger = LogManager.getLogger(DocEditForm.class);

	private boolean isLayoutMobile = false;

	//Button event
	private VerticalLayout vLayoutButton = new VerticalLayout();
	private HorizontalLayout hLayoutButton = new HorizontalLayout();
	private ButtonTemplate btnAssignAgain = new ButtonTemplate("Giao lại nhiệm vụ",FontAwesome.Solid.MAIL_FORWARD.create());
	private ButtonTemplate btnAccept = new ButtonTemplate("Bắt đầu thực hiện",FontAwesome.Solid.CIRCLE_PLAY.create());
	private ButtonTemplate btnReverseCompleted = new ButtonTemplate("Làm lại",FontAwesome.Solid.REFRESH.create());
	private ButtonTemplate btnRefuse = new ButtonTemplate("Từ chối nhiệm vụ",FontAwesome.Solid.MULTIPLY.create());
	private ButtonTemplate btnRating = new ButtonTemplate("Đánh giá nhiệm vụ",FontAwesome.Solid.STAR.create());
	private ButtonTemplate btnComfirm = new ButtonTemplate("Xác nhận nhiệm vụ hoàn thành",FontAwesome.Solid.CHECK.create());
	private ButtonTemplate btnRefuseConfirm = new ButtonTemplate("Từ chối báo cáo",FontAwesome.Regular.TIMES_CIRCLE.create());
	private ButtonTemplate btnPedding = new ButtonTemplate("Tạm hoãn nhiệm vụ",FontAwesome.Solid.CIRCLE_PAUSE.create());
	private ButtonTemplate btnUnPendding = new ButtonTemplate("Tiếp tục nhiệm vụ",FontAwesome.Solid.CIRCLE_PLAY.create());
	private ButtonTemplate btnRemind = new ButtonTemplate("Nhắc nhở",FontAwesome.Solid.BELL.create());
	private ButtonTemplate btnUpdate = new ButtonTemplate("Cập nhật",FontAwesome.Solid.EDIT.create());
	private ButtonTemplate btnCompleted = new ButtonTemplate("Hoàn thành nhiệm vụ",FontAwesome.Solid.CHECK_CIRCLE.create());
	private ButtonTemplate btnDeleteTask = new ButtonTemplate("Xóa nhiệm vụ",FontAwesome.Solid.TRASH.create());
	private ButtonTemplate btnRedo = new ButtonTemplate("Thực hiện lại",FontAwesome.Solid.ARROW_LEFT_ROTATE.create());
	private ButtonTemplate btnCreateChildTask = new ButtonTemplate("Giao tiếp nhiệm vụ",FontAwesome.Solid.ARROW_TURN_RIGHT.create());
	private ButtonTemplate btnAssignUserAssignee = new ButtonTemplate("Phân cán bộ xử lý",FontAwesome.Solid.USER_PLUS.create());
	private ButtonTemplate btnAssignUserSupport = new ButtonTemplate("Phân cán bộ phối hợp",FontAwesome.Solid.USER_GROUP.create());
	private ButtonTemplate btnAssignUserSupportAgain = new ButtonTemplate("Phân lại cán bộ phối hợp",FontAwesome.Solid.USER_GROUP.create());
	private ButtonTemplate btnEditAssignee = new ButtonTemplate("Chỉnh sửa",FontAwesome.Solid.EDIT.create());
	private ButtonTemplate btnReportConfirmCompleted = new ButtonTemplate("Báo cáo nhiệm vụ đã hoàn thành",FontAwesome.Solid.PAPER_PLANE.create());
	private ButtonTemplate btnReasonRefuse = new ButtonTemplate(FontAwesome.Solid.INFO.create());
	private ButtonTemplate btnReason = new ButtonTemplate("Xem lý do",FontAwesome.Solid.INFO.create());
	private ButtonTemplate btnHowToUse = new ButtonTemplate("Cách dùng",FontAwesome.Solid.CIRCLE_QUESTION.create());
	private ButtonTemplate btnHistory = new ButtonTemplate("Nhật ký nhiệm vụ",FontAwesome.Solid.HISTORY.create());
	private ButtonTemplate btnRedoAndReportAgain = new ButtonTemplate("Thực hiện và báo cáo lại",FontAwesome.Regular.CIRCLE_PLAY.create());
	private Span spRefuse = new Span();
	private Span spRequiConfirm = new Span("Nhiệm vụ cần đơn vị giao xác nhận sau khi hoàn thành");
	private Span spSupport = new Span();
	private Span spDetail = new Span();
	private Span spCountSubTask = new Span();
	private Span spCheckSubTask = new Span();
	private Span spInfoChild = new Span("Đây là nhiệm vụ được phân từ một nhiệm vụ khác");

	private TaskCommentForm taskCommentForm = new TaskCommentForm(null);

	//Button view
	private MenuBarTemplate menuBarBell = new MenuBarTemplate();
	private MenuBarTemplate menuBarRate = new MenuBarTemplate();
	private MenuBarTemplate menuBarReverse = new MenuBarTemplate();
	private MenuBarTemplate menuBarRedo = new MenuBarTemplate();


	private VerticalLayout vLayoutRating = new VerticalLayout();

	private SplitLayout splitLayout;
	private VerticalLayout vLayoutMobile = new VerticalLayout();

	private VerticalLayout vLayoutLeft = new VerticalLayout();
	private DetailsTemplate detailsTask = new DetailsTemplate("Thông tin nhiệm vụ",FontAwesome.Solid.INFO_CIRCLE.create());
	private DetailsTemplate detailsResult = new DetailsTemplate("Kết quả",FontAwesome.Solid.FILE_CIRCLE_CHECK.create());
	private DetailsTemplate detailsDoc = new DetailsTemplate("Thông tin văn bản",FontAwesome.Solid.MONEY_CHECK.create());
	private DetailsTemplate detailsDiscuss = new DetailsTemplate("Trao đổi ý kiến",FontAwesome.Solid.COMMENT_DOTS.create());

	private VerticalLayout vLayoutRight = new VerticalLayout();
	private DetailsTemplate detailOrg = new DetailsTemplate("Thông tin xử lý",FontAwesome.Solid.ID_CARD.create());
	private DetailsTemplate detailsProgress = new DetailsTemplate("Tiến độ nhiệm vụ",FontAwesome.Solid.BARS_PROGRESS.create());
	private DetailsTemplate detailsChildTasks = new DetailsTemplate("Nhiệm vụ đã được giao từ nhiệm vụ",FontAwesome.Solid.TASKS.create());

	private OnboardingView onboardingView = new OnboardingView();

	private boolean checkTaskOwner = false;
	private boolean checkTaskAssignee = false;
	private boolean checkTaskSupport = false;
	private boolean checkTaskFollow = false;

	private boolean checkDeleteTask = false;

	private List<Pair<String, String>> listPriority = new ArrayList<Pair<String,String>>();
	private List<String> listIdAttachment = new ArrayList<String>();

	private List<UploadModuleDataModel> listDataFileUpload = new ArrayList<UploadModuleDataModel>();


	private TaskOutputModel outputTaskModel;
	private SignInOrgModel signInOrgModel = SessionUtil.getDetailOrg();
	private BelongOrganizationModel belongOrganizationModel;
	private String idTask;
	private UserAuthenticationModel userAuthenticationModel;
	public TaskViewDetailForm(String idTask,BelongOrganizationModel belongOrganizationModel) {
		this.idTask = idTask;
		System.out.println(idTask);
		userAuthenticationModel = SessionUtil.getUser();
		this.belongOrganizationModel = belongOrganizationModel;
		buildLayout();
		checkLayoutMobile();
		configComponent();
		loadData();
		setVisibleButton();
		checkPermission();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.setSpacing(false);


		detailsResult.setVisible(false);

		vLayoutLeft.setWidthFull();
		vLayoutLeft.setPadding(false);
		vLayoutLeft.add(detailsResult,detailsTask,detailsDoc,detailsDiscuss);
		vLayoutLeft.getStyle().set("padding", "0 3px 0 0").setMargin("10px 10px 0 0");




		vLayoutRight.add(detailOrg,detailsProgress,detailsChildTasks);
		detailsChildTasks.setOpened(false);
		detailsChildTasks.setVisible(false);
		vLayoutRight.getStyle().set("padding", "0 0 0 3px").setMargin("10px 0 0 10px");;

		splitLayout = new SplitLayout(vLayoutLeft,vLayoutRight);
		vLayoutLeft.setWidth("60%");
		vLayoutRight.setWidth("40%");
		splitLayout.addThemeVariants(SplitLayoutVariant.LUMO_SMALL);
		splitLayout.setSizeFull();

		Hr hr = new Hr();
		hr.getStyle().setMargin("10px 0 0 0");

		this.add(vLayoutButton,btnHowToUse,hr,splitLayout);
		createLayoutButton();
	}

	@Override
	public void configComponent() {
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
		btnUpdate.addClickListener(e->openDialogUpdateTask(outputTaskModel));
		btnCreateChildTask.addClickListener(e->openDialogCreateChildTask(outputTaskModel));
		btnAssignUserAssignee.addClickListener(e->openDialogAssignUserAssignee());
		btnAssignUserSupport.addClickListener(e->openDialogAssignUserSupport());
		btnAssignUserSupportAgain.addClickListener(e->openDialogAssignUserSupport());
		btnReportConfirmCompleted.addClickListener(e->{
			if(outputTaskModel.getCountSubTask() > 0) {
				openDialogReport();
			}else {
				openConfirmReport();
			}
		});
//		btnReasonRefuse.addClickListener(e->openViewDetailRefuse(outputTaskModel.getRefuse()));
		detailsChildTasks.addOpenedChangeListener(e->{
			loadDetailTasksChild(idTask);
		});
//		btnReason.addClickListener(e->openViewDetailRefuse(outputTaskModel.getRefuse()));
		btnAssignAgain.addClickListener(e->openDialogAssignTaskAgain());
		btnHowToUse.addClickListener(e->onboardingView.onStartBoarding());
		btnHistory.addClickListener(e->openDialogShowHistory());
	}

	public void setTypeOfTask(boolean checkTaskOwner,boolean checkTaskAssignee,boolean checkTaskSupport,boolean checkTaskFollow) {
		this.checkTaskOwner = checkTaskOwner;
		this.checkTaskAssignee = checkTaskAssignee;
		this.checkTaskSupport = checkTaskSupport;
		this.checkTaskFollow = checkTaskFollow;
		checkPermission();
		loadDetailDoc(outputTaskModel);
	}

	private void setVisibleButton() {
		btnAccept.setVisible(false);
		btnCompleted.setVisible(false);
		btnDeleteTask.setVisible(false);
		btnRefuse.setVisible(false);
		btnRemind.setVisible(false);
		btnReverseCompleted.setVisible(false);
		btnUpdate.setVisible(false);
		btnAssignUserAssignee.setVisible(false);
		btnAssignUserSupport.setVisible(false);
		btnAssignUserSupportAgain.setVisible(false);
		btnCreateChildTask.setVisible(false);
		btnPedding.setVisible(false);
		btnUnPendding.setVisible(false);
		btnRedo.setVisible(false);
		btnRating.setVisible(false);
		btnComfirm.setVisible(false);
		btnRefuseConfirm.setVisible(false);
		btnEditAssignee.setVisible(false);
		btnReportConfirmCompleted.setVisible(false);
		btnReason.setVisible(false);
		btnAssignAgain.setVisible(false);
		spRequiConfirm.setVisible(false);
		spSupport.setVisible(false);
		spDetail.setVisible(false);
		spCountSubTask.setVisible(false);
		spCheckSubTask.setVisible(false);
		btnHowToUse.setVisible(false);
		btnRedoAndReportAgain.setVisible(false);
	}

	private void checkPermission() {
		setVisibleButton();

		//Check if this is an assgined task
		if(checkTaskAssignee) {
			if(outputTaskModel.getState().equals(StatusTaskEnum.DANGTHUCHIEN.getKey())) {
				btnCreateChildTask.setVisible(true);
				btnCompleted.setVisible(true);

				// The task is being performed but I still want to assign it to a specific person
				if(outputTaskModel.getAssignee().getOrganizationUserId() != null) {
					btnAssignUserAssignee.setVisible(true);
					btnAssignUserAssignee.setText("Phân lại xử lý");
				}else {
					btnAssignUserAssignee.setVisible(true);
				} 

				//Repost task complete when is Confirm
				if(outputTaskModel.isRequiredConfirm()) {
					btnReportConfirmCompleted.setVisible(true);
					spRequiConfirm.setVisible(true);
					btnCompleted.setVisible(false);
				}
				spDetail.setText("Nhiệm vụ đang thực hiện");
				spDetail.addClassName("span_dang_thuc_hien");
				spDetail.setVisible(true);
			}else if(outputTaskModel.getState().equals(StatusTaskEnum.CHUATHUCHIEN.getKey())) {
				//If you want to assgin it to a specific person
				if(outputTaskModel.getAssignee().getOrganizationUserId() != null) {
					btnAccept.setVisible(true);
					btnRefuse.setVisible(true);
					btnAssignUserAssignee.setVisible(true);
					btnAssignUserAssignee.setText("Phân lại xử lý");
				}else {
					btnAssignUserAssignee.setVisible(true);
					btnRefuse.setVisible(true);
					btnAccept.setVisible(true);
				}
				spDetail.setText("Nhiệm vụ chưa thực hiện");
				spDetail.addClassName("span_chua_thuc_hien");
				spDetail.setVisible(true);
				
				
				onboardingView.createStep(spDetail, "Thông tin trạng thái nhiệm vụ", "Thông tin này thể hiện nhiệm vụ đang ở trạng thái nào");
				onboardingView.createStep(btnAssignUserAssignee, "Phân xử lý", "Phân nhiệm vụ này cho người dùng");
				onboardingView.createStep(btnAccept, "Bắt đầu thực hiện", "Bắt đầu thực hiện nhiệm vụ");
				onboardingView.createStep(btnRefuse, "Từ chối", "Từ chối nhiệm vụ đã được giao");

			} else if(outputTaskModel.getState().equals(StatusTaskEnum.DAHOANTHANH.getKey())) {
				btnReverseCompleted.setVisible(true);
				//If you want to re-evaluate 
				if(outputTaskModel.getRating() != null) {
					btnRating.setText("Đánh giá lại nhiệm vụ");
				}
			} else if(outputTaskModel.getState().equals(StatusTaskEnum.CHOXACNHAN.getKey())) {
				spRequiConfirm.setVisible(true);
				spRequiConfirm.setText("Nhiệm vụ đang chờ xác nhận hoàn thành từ đơn vị giao");
			} else if(outputTaskModel.getState().equals(StatusTaskEnum.THUCHIENLAI.getKey())) {
				btnRedoAndReportAgain.setVisible(true);
				btnAssignUserAssignee.setVisible(true);
				btnAssignUserAssignee.setText("Phân lại xử lý");


				spDetail.setText("Nhiệm vụ đang chờ thực hiện lại");
				spDetail.addClassName("span_dang_thuc_hien");
				spDetail.setVisible(true);
			}else if(outputTaskModel.getState().equals(StatusTaskEnum.TUCHOITHUCHIEN.getKey())) {
				spDetail.setText("Đơn vị/Cán bộ xử lý đã từ chối thực hiện");
				//				spRefuse.setText(("Nhiệm vụ từ chối thực hiện: "+outputTaskModel.getRefuse().getCreator().getOrganizationName()+
				//						"(" +outputTaskModel.getRefuse().getCreator().getOrganizationUserName()+ ")" +" đã từ chối thực hiện nhiệm vụ"));
				spDetail.addClassName("span_chua_thuc_hien");
				spDetail.setVisible(true);
				btnReason.setVisible(true);
			}else if(outputTaskModel.getState().equals(StatusTaskEnum.TUCHOIXACNHAN.getKey())) {
				//If you want to assgin it to a specific person
				if(outputTaskModel.getAssignee().getOrganizationUserId() != null) {
					btnRefuse.setVisible(true);
					btnAssignUserAssignee.setVisible(true);
					btnAssignUserAssignee.setText("Phân lại xử lý");
				}else {
					btnAssignUserAssignee.setVisible(true);
				}
				btnRedoAndReportAgain.setVisible(true);
				spDetail.setText("Nhiệm vụ cần thực hiện lại báo cáo");
				spDetail.addClassName("span_chua_thuc_hien");
				spDetail.setVisible(true);
				spRefuse.setText("Đơn vị giao đã từ chối báo cáo");
				spRefuse.setVisible(true);
			}

			if(outputTaskModel.getCountSubTask() > 0) {
				spCountSubTask.addClassName("span_dang_thuc_hien");
				spCountSubTask.setText("Tổng nhiệm vụ đã được giao từ nhiệm vụ: "+outputTaskModel.getCountSubTask());
				spCountSubTask.setVisible(true);		

				spCheckSubTask.addClassName("span_da_hoan_thanh");
				spCheckSubTask.setText("Nhiệm vụ đã hoàn thành: "+countTaskSubCompleted()+"/"+outputTaskModel.getCountSubTask());
				spCheckSubTask.setVisible(true);			
			}
		}

		//Check if this is a support task
		if(checkTaskSupport) {
			outputTaskModel.getSupports().stream().forEach(model->{
				if(model.getOrganizationId().equals(belongOrganizationModel.getOrganizationId())) {
					if(model.getOrganizationUserId() == null) {
						btnAssignUserSupport.setVisible(true);
					}else {
						btnAssignUserSupportAgain.setVisible(true);
					}
				}
			});
			spSupport.setVisible(true);
			String assigneeUser = outputTaskModel.getAssignee().getOrganizationUserId() == null ? "" : " ("+ outputTaskModel.getAssignee().getOrganizationUserName().toString()+") ";
			spSupport.setText("Vui lòng hỗ trợ đơn vị "+outputTaskModel.getAssignee().getOrganizationName()
					+assigneeUser +" thực hiện nhiệm vụ");
			spSupport.getElement().getThemeList().add("badge contrast");
		}


		//Check if this is an owner task
		if(checkTaskOwner) {
			if(outputTaskModel.getState().equals(StatusTaskEnum.DANGTHUCHIEN.getKey())) {
				btnPedding.setVisible(true);
				btnUpdate.setVisible(true);
				btnDeleteTask.setVisible(true);
				btnCompleted.setVisible(true);
				btnRemind.setVisible(true);
				spDetail.setText("Nhiệm vụ đang thực hiện");
				spDetail.addClassName("span_dang_thuc_hien");
				spDetail.setVisible(true);

				onboardingView.createStep(btnPedding, "Tạm hoãn nhiệm vụ", "Nhiệm vụ sẽ tạm hoãn, các chức năng thực hiện nhiệm vụ sẽ không thể sử dụng");
				onboardingView.createStep(btnRemind, "Nhắc nhở", "Nhắc nhở về một vấn đề nào đó để thực hiện tốt nhiệm vụ");
				onboardingView.createStep(btnUpdate, "Cập nhật", "Cập nhật thông tin nhiệm vụ, chỉ đơn vị giao mới được thực hiện chức năng này");
				onboardingView.createStep(btnDeleteTask, "Xóa nhiệm vụ", "Xóa nhiệm vụ này, nhiệm vụ không thể khôi phục sau khi xóa");

			}else if(outputTaskModel.getState().equals(StatusTaskEnum.CHUATHUCHIEN.getKey())) {

				btnPedding.setVisible(true);
				btnUpdate.setVisible(true);
				btnDeleteTask.setVisible(true);
				btnAccept.setVisible(true);
				btnRemind.setVisible(true);

				onboardingView.createStep(btnPedding, "Tạm hoãn nhiệm vụ", "Nhiệm vụ sẽ tạm hoãn, các chức năng thực hiện nhiệm vụ sẽ không thể sử dụng");
				onboardingView.createStep(btnRemind, "Nhắc nhở", "Nhắc nhở về một vấn đề nào đó để thực hiện tốt nhiệm vụ");
				onboardingView.createStep(btnUpdate, "Cập nhật", "Cập nhật thông tin nhiệm vụ, chỉ đơn vị giao mới được thực hiện chức năng này");
				onboardingView.createStep(btnDeleteTask, "Xóa nhiệm vụ", "Xóa nhiệm vụ này, nhiệm vụ không thể khôi phục sau khi xóa");
				onboardingView.createStep(spDetail, "Thông tin trạng thái nhiệm vụ", "Thông tin này thể hiện nhiệm vụ đang ở trạng thái nào");

				spDetail.setText("Nhiệm vụ chưa thực hiện");
				spDetail.addClassName("span_chua_thuc_hien");
				spDetail.setVisible(true);
			}else if(outputTaskModel.getState().equals(StatusTaskEnum.DAHOANTHANH.getKey())) {
				btnRating.setVisible(true);
				btnRedo.setVisible(true);
				if(outputTaskModel.getRating() != null) {
					btnRating.setText("Đánh giá lại nhiệm vụ");
				}
				spDetail.setText("Nhiệm vụ đã hoàn thành");
				spDetail.addClassName("span_da_hoan_thanh");
				spDetail.setVisible(true);
			}else if(outputTaskModel.getState().equals(StatusTaskEnum.TAMHOAN.getKey())) {
				btnUnPendding.setVisible(true);
				btnUpdate.setVisible(true);
				btnDeleteTask.setVisible(true);
				spDetail.setText("Nhiệm vụ đang tạm hoãn");
				spDetail.addClassName("span_tam_hoan");
				spDetail.setVisible(true);
			}else if(outputTaskModel.getState().equals(StatusTaskEnum.CHOXACNHAN.getKey())) {
				btnComfirm.setVisible(true);
				btnRefuseConfirm.setVisible(true);
			}else if(outputTaskModel.getState().equals(StatusTaskEnum.TUCHOITHUCHIEN.getKey())){
				btnPedding.setVisible(true);
				btnUpdate.setVisible(true);
				btnDeleteTask.setVisible(true);
				btnRemind.setVisible(true);
				spDetail.setText("Đơn vị/Cán bộ xử lý đã từ chối thực hiện");
				//				spRefuse.setText(("Nhiệm vụ từ chối thực hiện: "+outputTaskModel.getRefuse().getCreator().getOrganizationName()+
				//						"(" +outputTaskModel.getRefuse().getCreator().getOrganizationUserName()+ ")" +" đã từ chối thực hiện nhiệm vụ"));
				//				hLayoutRefuse.setVisible(true);
				spDetail.addClassName("span_chua_thuc_hien");
				spDetail.setVisible(true);
				btnReason.setVisible(true);
				btnAssignAgain.setVisible(true);
			}else if(outputTaskModel.getState().equals(StatusTaskEnum.THUCHIENLAI.getKey())){
				btnPedding.setVisible(true);
				btnUpdate.setVisible(true);
				btnDeleteTask.setVisible(true);
				btnRemind.setVisible(true);
				btnCompleted.setVisible(true);
				spDetail.setText("Nhiệm vụ chờ thực hiện lại");
				spDetail.addClassName("span_dang_thuc_hien");
				spDetail.setVisible(true);
			}else if(outputTaskModel.getState().equals(StatusTaskEnum.TUCHOIXACNHAN.getKey())) {
				btnPedding.setVisible(true);
				btnUpdate.setVisible(true);
				btnDeleteTask.setVisible(true);
				btnRemind.setVisible(true);
				spDetail.setText("Nhiệm vụ đang chờ thực hiện lại báo cáo");
				spDetail.addClassName("span_dang_thuc_hien");
				spDetail.setVisible(true);
				
			}
		}


		//Check if this is a follower task and it is the same as the owner
		if(checkTaskFollow) {
			if(outputTaskModel.getState().equals(StatusTaskEnum.DANGTHUCHIEN.getKey())) {
				btnPedding.setVisible(true);
				btnUpdate.setVisible(true);
				btnDeleteTask.setVisible(true);
				btnRemind.setVisible(true);
				spDetail.setText("Nhiệm vụ đang thực hiện");
				spDetail.addClassName("span_dang_thuc_hien");
				spDetail.setVisible(true);
			}else if(outputTaskModel.getState().equals(StatusTaskEnum.CHUATHUCHIEN.getKey())) {

				btnPedding.setVisible(true);
				btnUpdate.setVisible(true);
				btnDeleteTask.setVisible(true);
				btnRemind.setVisible(true);
				spDetail.setText("Nhiệm vụ chưa thực hiện");
				spDetail.addClassName("span_chua_thuc_hien");
				spDetail.setVisible(true);
			}else if(outputTaskModel.getState().equals(StatusTaskEnum.DAHOANTHANH.getKey())) {
				btnRating.setVisible(true);
				btnRedo.setVisible(true);
				if(outputTaskModel.getRating() != null) {
					btnRating.setText("Đánh giá lại nhiệm vụ");
				}
				spDetail.setText("Nhiệm vụ đã hoàn thành");
				spDetail.addClassName("span_da_hoan_thanh");
				spDetail.setVisible(true);
			}else if(outputTaskModel.getState().equals(StatusTaskEnum.TAMHOAN.getKey())) {
				btnUnPendding.setVisible(true);
				btnUpdate.setVisible(true);
				btnDeleteTask.setVisible(true);
				spDetail.setText("Nhiệm vụ đang tạm hoãn");
				spDetail.addClassName("span_tam_hoan");
				spDetail.setVisible(true);
			}else if(outputTaskModel.getState().equals(StatusTaskEnum.CHOXACNHAN.getKey())) {
				btnComfirm.setVisible(true);
			}else if(outputTaskModel.getState().equals(StatusTaskEnum.TUCHOITHUCHIEN.getKey())){
				btnPedding.setVisible(true);
				btnUpdate.setVisible(true);
				btnDeleteTask.setVisible(true);
				btnRemind.setVisible(true);
				spDetail.setText("Đơn vị/Cán bộ xử lý đã từ chối thực hiện");
				//				spRefuse.setText(("Nhiệm vụ từ chối thực hiện: "+outputTaskModel.getRefuse().getCreator().getOrganizationName()+
				//						"(" +outputTaskModel.getRefuse().getCreator().getOrganizationUserName()+ ")" +" đã từ chối thực hiện nhiệm vụ"));
				//				hLayoutRefuse.setVisible(true);
				spDetail.addClassName("span_chua_thuc_hien");
				spDetail.setVisible(true);
				btnReason.setVisible(true);
				btnAssignAgain.setVisible(true);
			}else if(outputTaskModel.getState().equals(StatusTaskEnum.THUCHIENLAI.getKey())){
				btnPedding.setVisible(true);
				btnUpdate.setVisible(true);
				btnDeleteTask.setVisible(true);
				btnRemind.setVisible(true);
				spDetail.setText("Nhiệm vụ chờ thực hiện lại");
				spDetail.addClassName("span_dang_thuc_hien");
				spDetail.setVisible(true);
			}else if(outputTaskModel.getState().equals(StatusTaskEnum.TUCHOIXACNHAN.getKey())) {
				btnPedding.setVisible(true);
				btnUpdate.setVisible(true);
				btnDeleteTask.setVisible(true);
				btnRemind.setVisible(true);
				spDetail.setText("Nhiệm vụ đang chờ thực hiện lại báo cáo");
				spDetail.addClassName("span_dang_thuc_hien");
				spDetail.setVisible(true);
			}
		}

		if(outputTaskModel.getCountSubTask() > 0) {
			detailsChildTasks.setVisible(true);
		}
	}


	//Load all data of a task
	public void loadData() {
		onboardingView.removeAllListStep();
		//Get data if priority
		ApiResultResponse<List<ApiKeyValueModel>> dataOfPriority = ApiDocService.getPriority();
		if(dataOfPriority.isSuccess()) {
			dataOfPriority.getResult().stream().forEach(model->{
				listPriority.add(Pair.of(model.getKey(),model.getName()));
			});
		}

		ApiResultResponse<ApiOutputTaskModel> data = ApiTaskService.getAtask(idTask);
		if(data.isSuccess()) {
			outputTaskModel = new TaskOutputModel(data.getResult());
		}



		if(outputTaskModel.getCountSubTask() > 0) {
			detailsChildTasks.setVisible(true);
		}

		menuBarBell.removeAll();
		MenuItem menuItemBell = menuBarBell.addItem(createLayoutIconContent(FontAwesome.Solid.BELL.create(), "Nhắc nhở ("+outputTaskModel.getReminds().size()+")", "rgb(255 221 221 / 68%)", "rgb(104 18 18)"));
		SubMenu subMenuBell = menuItemBell.getSubMenu();
		if(!outputTaskModel.getReminds().isEmpty()) {
			TaskViewRemindForm taskViewRemindForm = new TaskViewRemindForm(outputTaskModel.getReminds());
			subMenuBell.add(taskViewRemindForm);
		}

		menuBarRate.removeAll();
		String stars = outputTaskModel.getRating() == null ? "Đánh giá (Chưa đánh giá)" : "Đánh giá ("+outputTaskModel.getRating().getStar()+" sao)";
		MenuItem menuRateItem = menuBarRate.addItem(createLayoutIconContent(FontAwesome.Solid.STAR.create(), stars, "rgb(239 245 222 / 68%)", "rgb(102 105 2"));
		SubMenu subRate = menuRateItem.getSubMenu();
		if(outputTaskModel.getRating() != null) {
			TaskViewRatingForm taskViewRatingForm = new TaskViewRatingForm(outputTaskModel.getRating());
			subRate.add(taskViewRatingForm);
		}

		menuBarReverse.removeAll();
		menuBarReverse.setVisible(false);
		MenuItem menuItemReverse = menuBarReverse.addItem(createLayoutIconContent(FontAwesome.Solid.ARROW_ROTATE_BACK.create(), "Nhiệm vụ thu hồi", "rgb(255 221 240 / 68%)", "rgb(104 18 93)"));
		SubMenu subMenuReverse = menuItemReverse.getSubMenu();
		if(outputTaskModel.getReverse() != null) {
			TaskViewReverseForm taskViewReverseForm = new TaskViewReverseForm(outputTaskModel.getReverse());
			subMenuReverse.add(taskViewReverseForm);
			menuBarReverse.setVisible(true);
		}

		menuBarRedo.removeAll();
		menuBarRedo.setVisible(false);
		MenuItem menuItemRedo = menuBarRedo.addItem(createLayoutIconContent(FontAwesome.Solid.REPEAT.create(), "Nhiệm vụ thực hiện lại", "rgb(221 252 255 / 68%)", "rgb(12 60 84)"));
		SubMenu subMenuRedo = menuItemRedo.getSubMenu();
		if(outputTaskModel.getRedo() != null) {
			TaskViewRedoForm taskViewRedoForm = new TaskViewRedoForm(outputTaskModel.getRedo());
			subMenuRedo.add(taskViewRedoForm);
			menuBarRedo.setVisible(true);
		}

		if(outputTaskModel.getState().equals(StatusTaskEnum.DAHOANTHANH.getKey())) {
			loadDetailResult(outputTaskModel);
		}else {
			detailsResult.setVisible(false);
		}

		if(outputTaskModel.getParentId() != null) {
			spInfoChild.setVisible(true);
		}

		loadDetailTask(outputTaskModel);
		loadDetailDoc(outputTaskModel);
		onboardingView.createStep(detailsDoc, "Thông tin văn bản", "Hiện thị thông tin văn bản mà nhiệm vụ được giao từ văn bản");
		loadDetailOrg(outputTaskModel);
		LoadDetailsProgress(outputTaskModel);
		loadDetailsDiscuss(outputTaskModel.getComments());
		checkPermission();
	}

	private void createLayoutButton() {
		vLayoutButton.removeAll();
		hLayoutButton.removeAll();
		HorizontalLayout hLayoutSpan = new HorizontalLayout();


		btnCompleted.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
		btnAccept.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

		btnRefuse.addThemeVariants(ButtonVariant.LUMO_ERROR);

		btnDeleteTask.addThemeVariants(ButtonVariant.LUMO_ERROR);

		spRequiConfirm.getElement().getThemeList().add("badge");
		spRequiConfirm.addClassName("span_cho_xac_nhan");

		spDetail.getElement().getThemeList().add("badge");
		spCountSubTask.getElement().getThemeList().add("badge");
		spCheckSubTask.getElement().getThemeList().add("badge");

		spInfoChild.setVisible(false);
		spInfoChild.getElement().getThemeList().add("badge");
		spInfoChild.getStyle().setMarginLeft("auto");

		spRefuse.getStyle().setColor("red");

		btnReasonRefuse.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnReasonRefuse.addThemeVariants(ButtonVariant.LUMO_ERROR);
		btnReasonRefuse.getStyle().setMarginTop("-7px").setPadding("0");
		btnReasonRefuse.setTooltipText("Xem chi tiết lý do từ chối");

		btnHowToUse.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnHowToUse.getStyle().setPosition(Position.ABSOLUTE).setRight("80px").setTop("1px").setZIndex(1000).setColor("white");

		vLayoutRating.setVisible(false);
		vLayoutRating.setWidth("30%");


		menuBarBell.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);
		menuBarBell.getStyle().setMarginLeft("auto");


		menuBarRate.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);
		menuBarRate.getStyle().setPadding("0");

		menuBarReverse.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);

		menuBarRedo.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);
		
		btnRefuseConfirm.addThemeVariants(ButtonVariant.LUMO_ERROR);
		btnRedoAndReportAgain.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

		hLayoutButton.setWidthFull();

		hLayoutButton.add(btnAssignUserAssignee,btnAssignUserSupport,btnAssignUserSupportAgain,btnCreateChildTask,btnPedding,btnUnPendding,btnRedo,
				btnRating,btnAccept,btnRedoAndReportAgain,btnRefuse,btnComfirm,btnRefuseConfirm,btnReverseCompleted,btnRemind,btnCompleted,btnUpdate,
				btnDeleteTask,btnReportConfirmCompleted,btnReason,btnAssignAgain,btnHowToUse,btnHistory,menuBarBell,menuBarRate,menuBarReverse,menuBarRedo);
		hLayoutSpan.add(spRequiConfirm,spDetail,spCountSubTask,spCheckSubTask,spSupport,spInfoChild);
		hLayoutSpan.setWidthFull();


		vLayoutButton.add(hLayoutButton,hLayoutSpan);
	}

	private void checkLayoutMobile() {
		UI.getCurrent().getPage().retrieveExtendedClientDetails(e->{
			if(e.getScreenWidth() < 768) {
				isLayoutMobile = true;
				vLayoutButton.removeAll();
				BsLayout bsLayout = new BsLayout();
				BsRow row = bsLayout.addRow();
				row.getStyle().set("gap", "5px");

				row.add(btnAssignUserAssignee,btnAssignUserSupport,btnAssignUserSupportAgain,btnCreateChildTask,btnPedding,btnUnPendding,btnRedo,
						btnRating,btnAccept,btnRedoAndReportAgain,btnRefuse,btnComfirm,btnRefuseConfirm,btnReverseCompleted,btnRemind,btnCompleted,btnUpdate,
						btnDeleteTask,btnReportConfirmCompleted,btnReason,btnAssignAgain,btnHowToUse,btnHistory,menuBarBell,menuBarRate,menuBarReverse,menuBarRedo,
						spRequiConfirm,spDetail,spCountSubTask,spCheckSubTask,spSupport);

				menuBarBell.getStyle().setMargin("none");
				btnHowToUse.setVisible(false);
				vLayoutButton.add(bsLayout);

				this.replace(splitLayout, vLayoutMobile);
				vLayoutMobile.add(detailOrg,detailsTask,detailsDoc,detailsProgress,detailsDiscuss,detailsResult,detailsChildTasks);
				vLayoutMobile.setWidthFull();

			}
		});
	}

	private Span createLayoutIconContent(Icon icon,String content,String background,String color) {
		Span sp = new Span();
		sp.add(icon,new Text(content));
		sp.getStyle().setBackground(background).setColor(color);
		icon.setSize("15px");
		sp.getStyle().setDisplay(Display.FLEX).setFlexDirection(FlexDirection.ROW).setAlignItems(AlignItems.CENTER)
		.set("gap", "10px").setFontSize("13px").setPadding("6px").setBorderRadius("5px").setFontWeight(600).setHeight("16px").setCursor("pointer");

		return sp;
	}

	//Load data for task
	private void loadDetailTask(TaskOutputModel outputTaskModel) {
		VerticalLayout vLayoutGeneral = new VerticalLayout();

		vLayoutGeneral.setWidthFull();
		Component componentTitle = createLayoutKeyValue("Tiêu đề: ", outputTaskModel.getTitle(),null,"rgb(147 8 0)");
		componentTitle.getChildren().forEach(item->{
			if(item.getId().isPresent() &&  item.getId().get().equals("content")) {
				item.getStyle().setFontWeight(600).set("font-style", "italic");
			}
		});


		Component componentStatus = createLayoutKeyValue("Tình trạng: ", checkStatus(outputTaskModel.getStatus().toString()), null,"#007f18");

		vLayoutGeneral.add(componentTitle,createLayoutKeyValue("Nội dung: ", outputTaskModel.getDescription(),null,"#003fa9"),componentStatus);


		HorizontalLayout hLayoutDate = new HorizontalLayout();
		hLayoutDate.setWidthFull();


		VerticalLayout hLayoutCompletedDate = new VerticalLayout();
		hLayoutCompletedDate.setWidth("300px");
		hLayoutCompletedDate.getStyle().setPadding("0");
		hLayoutCompletedDate.setVisible(false);

		//		if(outputTaskModel.getState().equals(StatusTaskEnum.DAHOANTHANH.getKey())) {
		//			hLayoutCompletedDate.add(createLayoutKeyValue("Ngày hoàn thành: ", outputTaskModel.getCompleteTimeText(),null));
		//			hLayoutCompletedDate.setVisible(true);
		//		}

		hLayoutDate.add(createLayoutKeyValue("Ngày giao: ", outputTaskModel.getCreateTimeText(),null,null));
		hLayoutDate.add(createLayoutKeyValue("Hạn xử lý: ", outputTaskModel.getEndTimeText(), null,null));

		hLayoutDate.add(hLayoutCompletedDate);

		hLayoutDate.add(createLayoutKeyValue("Thời gian còn lại: ", outputTaskModel.getCalculateTimeRemainText(), null,null));


		VerticalLayout vLayoutCalculate = new VerticalLayout();
		vLayoutCalculate.getStyle().setPadding("0");

		vLayoutCalculate.add(createLayoutKeyValue("Thời gian đã giao: ", 
				outputTaskModel.getCalculateCreateTimeText(), null,null));

		vLayoutCalculate.add(createLayoutKeyValue("Ngày bắt đầu thực hiện: ", outputTaskModel.getStartTimeText(), null,null),
				createLayoutKeyValue("Thời gian đã thực hiện: ",outputTaskModel.getCalculateStartTimeText(), null,null));

		if(!outputTaskModel.getReminds().isEmpty()) {
			TaskRemindModel taskRemindModel = outputTaskModel.getReminds().get(outputTaskModel.getReminds().size() - 1);
			vLayoutGeneral.add(createLayoutKeyValue("Nhắc nhở: ",taskRemindModel.getReasonRemind(), null,null),
					createLayoutKeyValue("Người nhắc nhở: ", taskRemindModel.getCreator().getOrganizationUserName(), null,null));
		}

		if(isLayoutMobile) {
			componentStatus.getStyle().setWidth("100%");
			hLayoutDate.getStyle().setDisplay(Display.FLEX).setFlexDirection(FlexDirection.COLUMN);
		}


		HorizontalLayout hLayoutButton = new HorizontalLayout();
		Button btnPriority = new Button();

		for(Pair<String, String> p : listPriority) {
			if(p.getKey().equals(outputTaskModel.getPriority())) {
				btnPriority = new Button(p.getRight(),FontAwesome.Solid.FLAG.create());
				switch(outputTaskModel.getPriority()) {
				case "thuong":
					break;
				case "khan":
					btnPriority.getStyle().setColor("#c6a100");
					break;
				case "hoatoc":
					btnPriority.addThemeVariants(ButtonVariant.LUMO_ERROR);
					break;
				}

			}
		}

		//This class in file task.css
		btnPriority.addClassName("task__button--border-radius");


		ButtonTemplate btnAttachment = new ButtonTemplate(""+outputTaskModel.getAttachments().size(),FontAwesome.Solid.PAPERCLIP.create());
		//This class in file task.css
		btnAttachment.addClassName("task__button--border-radius");
		btnAttachment.setTooltipText("Đính kèm từ đơn vị giao");
		btnAttachment.addClickListener(e->{
			openDialogViewAttachment(outputTaskModel.getAttachments());
		});

		ButtonTemplate btnCommand = new ButtonTemplate(outputTaskModel.getComments().size()+"",FontAwesome.Solid.COMMENTS.create());
		//This class in file task.css
		btnCommand.addClassName("task__button--border-radius");
		btnCommand.setTooltipText("Trao đổi ý kiến");
		btnCommand.addClickListener(e->{
			taskCommentForm.getTxtComment().focus();
		});

		hLayoutButton.setWidthFull();
		hLayoutButton.add(btnPriority,btnAttachment,btnCommand);

		vLayoutGeneral.add(hLayoutDate,vLayoutCalculate,new Hr(),hLayoutButton);


		detailsTask.removeAll();
		detailsTask.add(vLayoutGeneral);
		onboardingView.createStep(vLayoutGeneral, "Thông tin nhiệm vụ", "Thông tin về nhiệm vụ bao gồm về tiêu đề, nội dung, tình trạng nhiệm vụ, ngày giao, hạn xử lý...");
	}



	//Load data for result
	private void loadDetailResult(TaskOutputModel outputTaskModel) {
		detailsResult.setVisible(true);
		detailsResult.removeAll();
		detailsDiscuss.setOpened(false);
		detailsDoc.setOpened(false);

		VerticalLayout vLayoutResult = new VerticalLayout();
		vLayoutResult.setWidthFull();

		TaskCompletedModel taskCompletedModel = outputTaskModel.getCompleted();

		vLayoutResult.add(createLayoutKeyValue("Thời gian hoàn thành:", outputTaskModel.getCompleteTimeText(), null,null),
				createLayoutKeyValue("Đơn vị hoàn thành:", taskCompletedModel.getCreator().getOrganizationName(), null,null),
				createLayoutKeyValue("Người báo cáo hoành thành:", taskCompletedModel.getCreator().getOrganizationUserName().toString(), null,null));

		if(outputTaskModel.getState().equals(StatusTaskEnum.DAHOANTHANH.getKey())) {
			String value = "";
			if(outputTaskModel.getEndTime() > 0) {
				long result = calculateBetweenDays(LocalDateUtil.longToLocalDate(outputTaskModel.getCompleted().getCompletedTime()),LocalDateUtil.longToLocalDate(outputTaskModel.getEndTime()));
				long result2 = Math.abs(result);
				value =  result > 0 ? "Hoàn thành trước hạn "+result2 +" ngày" : "Hoàn thành sau hạn "+result2+" ngày";
			}else {
				value = "Nhiệm vụ này không hạn, và hoàn thành cách ngày bắt đầu thực hiện là "+outputTaskModel.getCalculateStartTimeText();
			}

			vLayoutResult.add(createLayoutKeyValue("Thành tích: ", value, null,null));
		}


		if(outputTaskModel.getRating() != null) {
			TaskRateModel taskRateModel = outputTaskModel.getRating();
			vLayoutRating.setVisible(true);
			createLayoutRating(taskRateModel);
			vLayoutResult.add(vLayoutRating);
		}

		ButtonTemplate btnAttachment = new ButtonTemplate("Kết quả đính kèm ("+taskCompletedModel.getAttachments().size()+")",FontAwesome.Solid.PAPERCLIP.create());
		//This class in file task.css
		btnAttachment.addClassName("task__button--border-radius");
		btnAttachment.setTooltipText("Đính kèm từ đơn vị báo cáo");
		btnAttachment.addClickListener(e->{
			openDialogViewAttachment(taskCompletedModel.getAttachments());
		});


		vLayoutResult.add(new Hr());
		vLayoutResult.add(btnAttachment);


		detailsResult.add(vLayoutResult);
	}

	private long calculateBetweenDays(LocalDate before,LocalDate after) {
		return ChronoUnit.DAYS.between(before, after);
	}

	//Load data for doc
	private void loadDetailDoc(TaskOutputModel outputTaskModel) {
		VerticalLayout vLayout = new VerticalLayout();
		vLayout.setWidthFull();


		if(outputTaskModel.getDocId() != null) {
			DocModel docModel = getDetailDoc(outputTaskModel.getDocId());

			vLayout.add(createLayoutKeyValue("Trích yếu: ", docModel.getSummary(),null,"#000089"));

			HorizontalLayout hLayout = new HorizontalLayout();
			hLayout.setWidthFull();
			hLayout.add(createLayoutKeyValue("Ký hiệu: ", docModel.getSymbol(),null,null),createLayoutKeyValue("Số hiệu: ", docModel.getNumber(),null,null)
					,createLayoutKeyValue("Người ký: ", docModel.getSignerName(),null,null),createLayoutKeyValue("Chức vụ: ", docModel.getSignerPosition(), idTask,null)
					);

			HorizontalLayout hLayoutButton = new HorizontalLayout();
			ButtonTemplate btnViewDoc = new ButtonTemplate("Xem thông tin văn bản",FontAwesome.Solid.EYE.create());
			btnViewDoc.addClickListener(e->{
				openDialogOverViewDoc(outputTaskModel.getDocId(),checkTaskOwner);
			});
			btnViewDoc.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			hLayoutButton.add(btnViewDoc);
			if(checkTaskOwner) {
				if(PropsUtil.isChangeDoc()) {
					ButtonTemplate btnEdit = new ButtonTemplate("Thay đổi văn bản nguồn",FontAwesome.Solid.EDIT.create());
					btnEdit.addClickListener(e->{
						openDialogAddDoc(PropsUtil.isChangeDoc(),docModel);
					});
					btnEdit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
					hLayoutButton.add(btnEdit);
				}
			}

			HorizontalLayout hLayoutDate = new HorizontalLayout();
			hLayoutDate.setWidthFull();
			hLayoutDate.add(createLayoutKeyValue("Ngày ký: ", LocalDateUtil.dfDate.format(docModel.getRegDate()), null,null));

			Component component = createLayoutKeyValue("Loại văn bản: ", checkDocCategory(docModel.getCategory().getKey()), null, null);

			vLayout.add(hLayout,hLayoutDate,component,new Hr(),hLayoutButton);
		}else {
			Span spTitleWarning = new Span("Nhiệm vụ chưa có văn bản nguồn");
			vLayout.add(spTitleWarning);	
			if(checkTaskOwner) {
				if(PropsUtil.isChangeDoc() == true) {
					ButtonTemplate btnAddDoc = new ButtonTemplate("Thêm văn bản nguồn",FontAwesome.Solid.PLUS.create());

					btnAddDoc.addClickListener(e->{
						openDialogAddDoc(PropsUtil.isChangeDoc(),null);
					});			

					vLayout.add(btnAddDoc);
				}
			}

		}
		detailsDoc.removeAll();
		detailsDoc.add(vLayout);


		//		onboardingView.createStep(vLayout, "Thông tin văn bản", "Hiện thị thông tin văn bản mà nhiệm vụ được giao từ văn bản");
	}

	private String checkDocCategory(String docCategory) {
		if(docCategory.equals(DocCategoryEnum.CVDEN.getKey())) {
			return DocCategoryEnum.CVDEN.getTitle();
		}else {
			return DocCategoryEnum.CVDI.getTitle();
		}
	}

	private DocModel getDetailDoc(String idDoc) {
		ApiResultResponse<ApiDocModel> doc = ApiDocService.getAdoc(idDoc);
		if(doc.isSuccess()) {
			DocModel docModel = new DocModel(doc.getResult());
			return docModel;
		}
		return null;
	}

	//Load data for info org
	private void loadDetailOrg(TaskOutputModel outputTaskModel) {
		VerticalLayout vLayoutGeneral = new VerticalLayout();
		vLayoutGeneral.setWidthFull();
		vLayoutGeneral.add(createLayoutKeyValueForDetailInfoOrg("Đơn vị giao: ", outputTaskModel.getOwner().getOrganizationName()+
				" ("+outputTaskModel.getOwner().getOrganizationUserName()+") Người giao: "+outputTaskModel.getAssistant().getOrganizationUserName().toString(), "badge error",null));

		vLayoutGeneral.add(new Hr());

		String userAssignee = outputTaskModel.getAssignee().getOrganizationUserId() == null ? outputTaskModel.getAssignee().getOrganizationName()
				: outputTaskModel.getAssignee().getOrganizationName() + " (" + outputTaskModel.getAssignee().getOrganizationUserName().toString() +")";
		vLayoutGeneral.add(createLayoutKeyValueForDetailInfoOrg("Đơn vị thực hiện: ", userAssignee,"badge success",null));
		vLayoutGeneral.add(new Hr());

		List<String> listSupport = new ArrayList<String>();
		if(outputTaskModel.getSupports().isEmpty()) {
			listSupport.add("Không có đơn vị hỗ trợ nào");
		}else {
			outputTaskModel.getSupports().stream().forEach(model->{
				if(model.getOrganizationUserId() != null) {
					listSupport.add(model.getOrganizationName()+" ("+model.getOrganizationUserName()+") ");
				}else {
					listSupport.add(model.getOrganizationName());
				}
			});
		}


		MultiSelectComboBox<String> multiSelectSupport = new MultiSelectComboBox<String>();
		multiSelectSupport.setReadOnly(true);
		multiSelectSupport.setItems(listSupport);
		multiSelectSupport.select(listSupport);
		multiSelectSupport.setWidth("85%");
		multiSelectSupport.addThemeVariants(MultiSelectComboBoxVariant.LUMO_SMALL);


		vLayoutGeneral.add(createLayoutKeyValueForDetailInfoOrg("Đơn vị phối hợp "+outputTaskModel.getSupports().size()+": ", "","badge contrast",multiSelectSupport));
		vLayoutGeneral.add(new Hr());

		List<String> listFollow = new ArrayList<String>();
		if(outputTaskModel.getFollowers() != null) {
			if(outputTaskModel.getFollowers().isEmpty()) {
				listFollow.add("Không có khối theo dõi nào");
			}else {
				outputTaskModel.getFollowers().stream().forEach(model->{
					if(model.getOrganizationUserId()!= null) {
						listFollow.add(model.getOrganizationName()+ " ("+model.getOrganizationUserName()+") ");
					}else {
						listFollow.add(model.getOrganizationName());
					}
				});
			}
		}else {
			listFollow.add("Không có khối theo dõi nào");
		}
		


		MultiSelectComboBox<String> multiSelectFollower = new MultiSelectComboBox<String>();
		multiSelectFollower.setReadOnly(true);
		multiSelectFollower.setItems(listFollow);
		multiSelectFollower.select(listFollow);
		multiSelectFollower.setWidth("85%");
		multiSelectFollower.addThemeVariants(MultiSelectComboBoxVariant.LUMO_SMALL);

		vLayoutGeneral.add(createLayoutKeyValueForDetailInfoOrg("Khối theo dõi "+outputTaskModel.getCountFollowerText()+": ", "", "badge",multiSelectFollower));


		btnEditAssignee.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

		vLayoutGeneral.add(btnEditAssignee);

		detailOrg.removeAll();
		detailOrg.add(vLayoutGeneral);
		onboardingView.createStep(vLayoutGeneral, "Thông tin đơn vị", "Hiện thị thông tin đơn vị giao, người chỉ đạo, đơn vị xử lý, đơn vị phối hợp, và khối theo dõi");
	}

	//Load data for Progress
	private void LoadDetailsProgress(TaskOutputModel outputTaskModel) {

		TaskProcessModel taskProcessModel = outputTaskModel.getProcesses().isEmpty() ? new TaskProcessModel() : outputTaskModel.getProcesses().get(0);

		TaskProgressViewForm taskProgressViewForm = new TaskProgressViewForm(idTask, taskProcessModel,checkTaskSupport,outputTaskModel.getState().toString(),true);
		taskProgressViewForm.addChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});

		detailsProgress.removeAll();

		detailsProgress.add(taskProgressViewForm);
		onboardingView.createStep(taskProgressViewForm, "Tiến độ nhiệm vụ", "Tiến độ nhiệm vụ cho bạn biết được nhiệm vụ đã làm tới đâu, đơn vị xử lý sẽ cập nhật thông tin tiến độ");
	}

	//Load data for comment
	private void loadDetailsDiscuss(List<TaskCommentModel> listTaskCommentModels) {
		taskCommentForm = new TaskCommentForm(idTask);
		detailsDiscuss.removeAll();
		detailsDiscuss.getStyle().setBackground("none");
		detailsDiscuss.add(taskCommentForm);



		onboardingView.createStep(detailsDiscuss, "Trao đổi ý kiến", "Nơi trao đổi ý kiến với các đơn vị khác tại đây, chỉ có thể trao đổi khi mà nhiệm vụ đang trong trạng thái là đang thực hiện");
		onboardingView.createStep(menuBarBell, "Thông tin nhắc nhở", "Ở đây thông báo rằng nhiệm vụ được nhắc nhở từ đơn vị giao bao nhiêu lần, nhấp vào để xem chi tiết nhắc nhở");
		onboardingView.createStep(menuBarRate, "Đánh giá", "Thông báo rằng nhiệm vụ đã được đánh giá hay chưa, nhấp vào để xem chi tiết đánh giá");
	}

	//Load data for child
	private void loadDetailTasksChild(String idParentId) {

		if(detailsChildTasks.isOpened()) {
			TaskListDetailChildrenForm taskDetailChildrenForm = new TaskListDetailChildrenForm(idParentId);
			taskDetailChildrenForm.loadData();
			detailsChildTasks.removeAll();
			detailsChildTasks.add(taskDetailChildrenForm);
		}

		detailsChildTasks.setHeight("auto");

	}


	private Component createLayoutKeyValue(String header,String content,String style,String css) {
		HorizontalLayout hLayout = new HorizontalLayout();
		hLayout.setSpacing(false);

		Span spanHeader = new Span(header);
		spanHeader.getStyle().setFontWeight(600);

		Span spanContent = new Span(content);
		spanContent.setId("content");


		if(style != null) {
			spanContent.getElement().getThemeList().add(style);
		}

		if(css != null) {
			spanContent.getStyle().setColor(css);
		}

		hLayout.add(spanHeader,spanContent);
		hLayout.getStyle().setDisplay(Display.FLEX).set("gap", "10px");
		if(isLayoutMobile) {
			hLayout.getStyle().setFlexDirection(FlexDirection.COLUMN)
			.setBorder("1px solid #dbe0e9").setPadding("5px").setBorderRadius("10px");
			hLayout.setWidthFull();
		}


		hLayout.setPadding(false);
		return hLayout;
	}

	private Component createLayoutKeyValueForDetailInfoOrg(String header,String content,String style,Component layoutContent) {
		HorizontalLayout hLayout = new HorizontalLayout();
		hLayout.setSpacing(false);

		Span spanHeader = new Span(header);
		spanHeader.getStyle().setFontWeight(600);
		spanHeader.setWidth("140px");
		spanHeader.getStyle().setMargin("auto 0");

		Span spanContent = new Span(content);
		spanContent.getStyle().setWidth("80%").setOverflow(Overflow.HIDDEN).set("text-overflow", "ellipsis").setJustifyContent(JustifyContent.LEFT);

		if(style != null) {
			spanContent.getElement().getThemeList().add(style);
		}

		if(layoutContent == null) {
			hLayout.add(spanHeader,spanContent);
		}else {
			hLayout.add(spanHeader,layoutContent);
			layoutContent.getStyle().setWidth("87%");
		}


		hLayout.getStyle().setDisplay(Display.FLEX).set("gap", "10px");
		hLayout.setWidthFull();
		hLayout.setPadding(false);

		if(isLayoutMobile) {
			hLayout.getStyle().setFlexDirection(FlexDirection.COLUMN)
			.setBorder("1px solid #dbe0e9").setPadding("5px").setBorderRadius("10px");
		}

		return hLayout;
	}

	//Xem danh sách đính kèm
	private void openDialogViewAttachment(List<Object> listIdAttachment) {
		DialogTemplate dialogTemplate = new DialogTemplate("DANH SÁCH ĐÍNH KÈM");
		List<String> listAttachment = new ArrayList<String>();
		for(Object object : listIdAttachment) {
			listAttachment.add(object.toString());
		}
		ActtachmentForm acttachmentForm = new ActtachmentForm(listAttachment, true);
		dialogTemplate.add(acttachmentForm);
		dialogTemplate.setWidth("60%");
		dialogTemplate.setHeightFull();
		dialogTemplate.getFooter().removeAll();
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



		TextField txtReasonRefuse = new TextField("Lý do từ chối");
		txtReasonRefuse.setWidthFull();

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
			NotificationTemplate.success("Thành công");
			loadData();
			checkPermission();
			fireEvent(new ClickEvent(this,false));
		}
	}

	@SuppressWarnings("unused")
	private void openViewDetailRefuse(TaskOutputRefuseModel taskOutputRefuseModel) {
		DialogTemplate dialogTemplate = new DialogTemplate("CHI TIẾT LÝ DO TỪ CHỐI");

		TaskViewReasonRefuseForm taskViewReasonRefuseForm = new TaskViewReasonRefuseForm(taskOutputRefuseModel);

		dialogTemplate.add(taskViewReasonRefuseForm);
		dialogTemplate.setWidth("60%");
		dialogTemplate.setHeightFull();

		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setLayoutMobile();
		dialogTemplate.open();
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
			loadData();
			checkPermission();
			fireEvent(new ClickEvent(this,false));
		}
	}

	private void doRedoAndReportAgain() {
		ApiCreatorDoRedoAndReportAgainModel creatorActionModel = new ApiCreatorDoRedoAndReportAgainModel();
		creatorActionModel.setCreator(getCreator());
		ApiResultResponse<Object> redoAndReportAgain = ApiActionService.doRedoAndReportAgain(idTask, creatorActionModel);
		if(redoAndReportAgain.isSuccess()) {
			loadData();
			checkPermission();
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


		if(!outputTaskModel.getProcesses().isEmpty() && outputTaskModel.getProcesses().get(0).getPercent() == 100) {
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

	private void doCompleted(long completedTime) {
		ApiCompletedInputModel apiCompletedModel = new ApiCompletedInputModel();
		apiCompletedModel.setAttachments(listIdAttachment);
		apiCompletedModel.setCompletedTime(completedTime);
		apiCompletedModel.setCreator(getCreator());
		if(checkTaskOwner) {
			apiCompletedModel.setIgnoreRequiredConfirm(true);
		}
		ApiResultResponse<Object> completed = ApiActionService.doCompleted(idTask, apiCompletedModel);
		if(completed.isSuccess()) {
			loadData();
			checkPermission();
			NotificationTemplate.success("Thành công");
			fireEvent(new ClickEvent(this,false));
		}else {
			NotificationTemplate.success(completed.getMessage());
		}
	}

	//Xin được thực lại nhiệm vụ vì một lý do nào đó
	
	@SuppressWarnings("deprecation")
	private void openConfirmReverseCompleted() {
		ConfirmDialogTemplate confirmDialogTemplate = new ConfirmDialogTemplate("THU HỒI LẠI NHIỆM VỤ");

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

		TextField txtReasonReverse = new TextField("Lý do thực hiện lại nhiệm vụ");
		txtReasonReverse.setWidthFull();

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
			loadData();
			checkPermission();
			NotificationTemplate.success("Thành công");
			fireEvent(new ClickEvent(this,false));
		}
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

		if(!outputTaskModel.getProcesses().isEmpty() && outputTaskModel.getProcesses().get(0).getPercent() == 100) {
			Span span = new Span("Xác nhận báo cáo nhiệm vụ đã hoàn thành");
			vLayoutReport.add(span);
			confirmDialogTemplate.addConfirmListener(e->doReport());
		}else {
			NumberField numberField = new NumberField("Cập nhật tiến độ hoàn thành");
			numberField.setWidthFull();
			numberField.setValue(100.0);
			numberField.setReadOnly(true);

			TextField txtExplain = new TextField("Diễn giải");
			txtExplain.setWidthFull();
			txtExplain.setPlaceholder("Đã hoàn thành");			

			vLayoutReport.add(numberField,txtExplain);
			confirmDialogTemplate.addConfirmListener(e->{
				String explain = txtExplain.getValue().isEmpty() ? "Đã hoàn thành" : txtExplain.getValue();
				List<String> listFileOfPerogress = new ArrayList<String>();
				if(outputTaskModel.getProcesses() != null) {
					if(!outputTaskModel.getProcesses().isEmpty()) {
						listFileOfPerogress.addAll(outputTaskModel.getProcesses().get(0).getAttachments());
					}
				}

				//Update progress again before report task
				doUpdateProgress(100.0, explain, listFileOfPerogress,()->{
					doReport();
				});
			});
		}

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
			loadData();
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
		ApiResultResponse<Object> report = ApiActionService.doReport(idTask, apiReportModel);
		if(report.isSuccess()) {
			loadData();
			checkPermission();
			NotificationTemplate.success("Thành công");
			fireEvent(new ClickEvent(this,false));
		}
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


	//Xác nhận nhiệm vụ đã hoàn thành sau khi người được giao báo cáo hoàn thành
	
	@SuppressWarnings("deprecation")
	private void openComfirmDoComfirm() {
		ConfirmDialogTemplate confirmDialogTemplate = new ConfirmDialogTemplate("XÁC NHẬN NHIỆM VỤ ĐÃ HOÀN THÀNH");

		VerticalLayout vLayoutConfirm = new VerticalLayout();

		vLayoutConfirm.add(createLayoutKeyValue("Ngày giao nhiệm vụ: ", LocalDateUtil.dfDate.format(outputTaskModel.getCreatedTime()), null,null));

		DateTimePicker dateEndTime = new DateTimePicker("Hạn xử lý nhiệm vụ");
		dateEndTime.setLocale(LocalDateUtil.localeVietNam());


		TextField txtEndTime = new TextField("Hạn xử lý nhiệm vụ");
		txtEndTime.setId("damnn");
		if(outputTaskModel.getEndTime() != 0) {

			LocalDateTime localEndTime = LocalDateUtil.longToLocalDateTime(outputTaskModel.getEndTime());
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
			dateTimePicker.setMin(LocalDateUtil.longToLocalDateTime(outputTaskModel.getCreatedTime()));
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

	private void doConfirm(long completedTime) {
		ApiConfirmModel apiConfirmModel = new ApiConfirmModel();
		apiConfirmModel.setCompletedTime(completedTime);
		apiConfirmModel.setCreator(getCreator());
		ApiResultResponse<Object> confirm = ApiActionService.doConfirm(idTask, apiConfirmModel);
		if(confirm.isSuccess()) {
			loadData();
			checkPermission();
			NotificationTemplate.success("Thành công");
			fireEvent(new ClickEvent(this,false));
		}
	}

	//Tạo danh sách status
	private List<Pair<String, String>> getListStatusCompleted(){
		List<Pair<String, String>> listStatusCompleted = new ArrayList<Pair<String,String>>();
		listStatusCompleted.add(Pair.of("tronghan","Xác nhận nhiệm vụ trong hạn"));
		listStatusCompleted.add(Pair.of("quahan","Xác nhận nhiệm vụ quá hạn"));
		listStatusCompleted.add(Pair.of("khonghan","Nhiệm vụ không hạn"));
		return listStatusCompleted;
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

		TextField txtReasonPedding = new TextField("Lý do tạm hoãn nhiệm vụ");
		txtReasonPedding.setWidthFull();

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
			loadData();
			checkPermission();
			NotificationTemplate.success("Thành công");
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
			loadData();
			checkPermission();
			NotificationTemplate.success("Thành công");
			fireEvent(new ClickEvent(this,false));
		}
	}

	//Thực hiện lại nhiệm vụ
	private void openConfirmRedo() {
		DialogTemplate dialogTemplate = new DialogTemplate("THỰC HIỆN LẠI NHIỆM VỤ");
		TextArea txtReasonRedo = new TextArea("Lý do yêu cầu làm lại");
		txtReasonRedo.setWidthFull();
		UploadModuleBasic uploadModuleBasic = new UploadModuleBasic();
		uploadModuleBasic.initUpload();
		dialogTemplate.add(txtReasonRedo,uploadModuleBasic);
		dialogTemplate.getBtnSave().addClickListener(e->{
			listIdAttachment = new ArrayList<String>();
			uploadFile(uploadModuleBasic.getListFileUpload());
			if(!txtReasonRedo.isEmpty()) {
				if(doRedo(txtReasonRedo.getValue(), listIdAttachment)) {
					loadData();
					dialogTemplate.close();
				}
			}else {
				txtReasonRedo.setErrorMessage("Vui lòng nhập lý do làm lại");
				txtReasonRedo.setInvalid(true);
				txtReasonRedo.focus();
			}

		});

		dialogTemplate.setWidth("40%");
		dialogTemplate.setHeight("70%");
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
			return true;
		}

		return false;

	}

	//Đánh giá nhiệm vụ sau khi hoàn thành
	private void openConfirmRating() {
		DialogTemplate confirmDialogTemplate = new DialogTemplate("ĐÁNH GIÁ NHIỆM VỤ");
		TaskRatingForm taskRatingForm = new TaskRatingForm(outputTaskModel.getRating(),outputTaskModel.isRequiredKpi());
		confirmDialogTemplate.add(taskRatingForm);
		confirmDialogTemplate.getBtnSave().addClickListener(e->{
			if(taskRatingForm.getTxtDesr().getValue().isEmpty()) {
				taskRatingForm.getTxtDesr().setErrorMessage("Vui lòng nhập nhận xét");
				taskRatingForm.getTxtDesr().setInvalid(true);
				taskRatingForm.getTxtDesr().focus();
			}else {
				if(doRating(taskRatingForm.getStar(), taskRatingForm.getExplain())) {
					loadData();
					confirmDialogTemplate.close();
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
			loadData();
			return true;
		}else {
			NotificationTemplate.error(remind.getMessage());
			return false;
		}
	}

	//Cập nhật nhiệm vụ
	private void openDialogUpdateTask(TaskOutputModel outputTaskModel) {
		DialogTemplate dialogTemplate = new DialogTemplate("CẬP NHẬT NHIỆM VỤ");

		//This class CreateTaskForm in package doc -> forms
		TaskCreateForm updateTaskForm = new TaskCreateForm(outputTaskModel.getDocId(), belongOrganizationModel, userAuthenticationModel, SessionUtil.getDetailOrg(), idTask);
		updateTaskForm.addChangeListener(e->{
			loadData();
			fireEvent(new ClickEvent(this,false));
			refreshMainLayout();
			dialogTemplate.close();
		});
		dialogTemplate.add(updateTaskForm);
		if(outputTaskModel.getAttachments().isEmpty()) {
			dialogTemplate.setWidth("50%");
			dialogTemplate.setHeightFull();
		}else {
			dialogTemplate.setSizeFull();
		}
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setLayoutMobile();
		dialogTemplate.open();
	}

	//Tạo nhiệm vụ con
	private void openDialogCreateChildTask(TaskOutputModel outputTaskModel) {
		DialogTemplate dialogTemplate = new DialogTemplate("GIAO TIẾP NHIỆM VỤ");
		TaskCreateChildForm updateTaskForm = new TaskCreateChildForm(outputTaskModel.getDocId(), belongOrganizationModel, userAuthenticationModel, SessionUtil.getDetailOrg(), idTask);


		updateTaskForm.addChangeListener(e->{
			loadData();
			fireEvent(new ClickEvent(this,false));
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

	//Phân cán bộ xử lý
	private void openDialogAssignUserAssignee() {
		DialogTemplate dialogTemplate = new DialogTemplate("PHÂN CÁN BỘ XỬ LÝ");
		ApiUserGroupExpandModel apiUserGroupExpandModel = new ApiUserGroupExpandModel();
		if(outputTaskModel.getAssignee().getOrganizationUserId() != null) {
			apiUserGroupExpandModel.setUserId(outputTaskModel.getAssignee().getOrganizationUserId().toString());
		}
		TaskDivisionUserForm taskDivisionUserForm = new TaskDivisionUserForm(idTask,true,apiUserGroupExpandModel,null);
		taskDivisionUserForm.addChangeListener(e->{
			loadData();
			checkPermission();
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

	//Phân cán bộ hỗ trợ 
	private void openDialogAssignUserSupport() {
		DialogTemplate dialogTemplate = new DialogTemplate("PHÂN CÁN BỘ HỖ TRỢ");
		ApiUserGroupExpandModel apiUserGroupExpandModel = new ApiUserGroupExpandModel();
		outputTaskModel.getSupports().forEach(model->{
			if(model.getOrganizationId().equals(belongOrganizationModel.getOrganizationId())) {
				if(model.getOrganizationUserId() != null) {
					apiUserGroupExpandModel.setUserId(model.getOrganizationUserId().toString());
					apiUserGroupExpandModel.setUserName(model.getOrganizationUserName().toString());
				}
			}
		});
		TaskDivisionUserForm taskDivisionUserForm = new TaskDivisionUserForm(idTask, false,null,apiUserGroupExpandModel);
		taskDivisionUserForm.addChangeListener(e->{
			loadData();
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


	//Lấy thông tin của người đang thực hiện
	private ApiCreatorActionModel getCreator() {
		ApiCreatorActionModel actionModel = new ApiCreatorActionModel();
		actionModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
		actionModel.setOrganizationName(belongOrganizationModel.getOrganizationName());
		actionModel.setOrganizationUserId(userAuthenticationModel.getId());
		actionModel.setOrganizationUserName(userAuthenticationModel.getFullName());
		return actionModel;
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
				logger.error(e.getMessage());
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
			logger.error(e.getMessage());
			e.printStackTrace();
		}
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

	//Xem van ban
	@SuppressWarnings("unused")
	private void openDialogViewDoc(String docId) {
		DialogTemplate dialogTemplate = new DialogTemplate("Thông tin văn bản");

		DocEditForm editDocForm = new DocEditForm(docId, ()->{

		}, userAuthenticationModel, belongOrganizationModel, signInOrgModel,null);
		editDocForm.setReadOnly();		
		dialogTemplate.add(editDocForm);
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setSizeFull();
		dialogTemplate.setLayoutMobile();
		dialogTemplate.open();
	}
	
	private void openDialogOverViewDoc(String idDoc,boolean isViewDoc) {
		DialogTemplate dialogTemplate = new DialogTemplate("Thông tin văn bản");
		
//		ApiFilterSummaryDocModel apiFilterSummaryDocModel = new ApiFilterSummaryDocModel();
//		apiFilterSummaryDocModel.setDocId(docModel.getId());
//		apiFilterSummaryDocModel.setFromDate(filterDocForm.getFilter().getFromDate());
//		apiFilterSummaryDocModel.setToDate(filterDocForm.getFilter().getToDate());
		
		DocOverviewForm docOverviewForm = new DocOverviewForm(idDoc, null, null,isViewDoc);
		dialogTemplate.setWidth("80%");
		dialogTemplate.setHeightFull();
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.add(docOverviewForm);
		dialogTemplate.open();
	}

	//Thêm đánh giá 
	private void createLayoutRating(TaskRateModel taskRateModel) {
		vLayoutRating.removeAll();

		//		vLayoutRating.getStyle().setMarginLeft("auto").setMarginTop("-10px").setPadding("10px").setBoxShadow("rgba(99, 99, 99, 0.2) 0px 2px 8px 0px");
		vLayoutRating.setWidthFull();

		HorizontalLayout hLayout1 = new HorizontalLayout();
		HorizontalLayout hLayoutStar = new HorizontalLayout();

		for(int i = 1;i <= taskRateModel.getStar(); i++) {
			Icon icon = FontAwesome.Solid.STAR.create();
			icon.getStyle().setColor("#ffce44");
			hLayoutStar.add(icon);
		}

		Span spStar = new Span("Đánh giá: ");
		spStar.getStyle().setFontWeight(600);
		hLayout1.add(spStar,hLayoutStar);


		vLayoutRating.add(hLayout1,createLayoutKeyValue("Nhận xét: ", taskRateModel.getExplain(), null,null),
				createLayoutKeyValue("Người nhận xét: ", taskRateModel.getCreator().getOrganizationName()+" ("+taskRateModel.getCreator().getOrganizationUserName()+")", idTask,null));
	}

	private String checkStatus(String status) {
		String status2 = "";
		ApiResultResponse<List<ApiKeyValueModel>> listStatus = ApiTaskService.getStatus();
		for(ApiKeyValueModel kstatus : listStatus.getResult()) {

			if(status.equals(kstatus.getKey())) {
				status2 = kstatus.getName();
				break;
			}
		}
		return status2;
	}

	//Thêm văn bản nguồn

	private DocModel docIsChoose;

	private void openDialogAddDoc(boolean checkDocRequired,DocModel docModel) {
		DialogTemplate dialogTemplate = new DialogTemplate("Thêm văn bản nguồn");
		DocListForm listDocForm = new DocListForm(userAuthenticationModel, belongOrganizationModel, signInOrgModel, checkDocRequired,docModel,null,null);


		listDocForm.addChangeListener(e->{
			docIsChoose = listDocForm.getListDocIsChoose().get(0);
		});
		dialogTemplate.add(listDocForm);
		dialogTemplate.getBtnSave().addClickListener(e->{
			if(docIsChoose != null) {
				ApiInputTaskModel apiInputTaskModel = new ApiInputTaskModel(outputTaskModel);
				apiInputTaskModel.setDocId(docIsChoose.getId());

				ApiOrgGeneralOfTaskModel creator = new ApiOrgGeneralOfTaskModel();
				creator.setOrganizationId(belongOrganizationModel.getOrganizationId());
				creator.setOrganizationName(belongOrganizationModel.getOrganizationName());
				creator.setOrganizationUserId(userAuthenticationModel.getId());
				creator.setOrganizationUserName(userAuthenticationModel.getUsername());

				apiInputTaskModel.setCreator(creator);
				if(updateTaskAfterAddDoc(apiInputTaskModel)) {
					dialogTemplate.close();

				}
			}
		});	
		dialogTemplate.open();
		dialogTemplate.setSizeFull();

	}

	private boolean updateTaskAfterAddDoc(ApiInputTaskModel apiInputTaskModel) {
		try {
			ApiResultResponse<ApiInputTaskModel> updateTask = ApiTaskService.updateTask(idTask, apiInputTaskModel);
			if(updateTask.isSuccess()) {
				NotificationTemplate.success("Thành công");
				loadData();			
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void openDialogAssignTaskAgain() {
		DialogTemplate dialogTemplate = new DialogTemplate("Giao lại nhiệm vụ");

		//This class CreateTaskForm in package doc -> forms
		TaskCreateForm taskAssginAgain = new TaskCreateForm(outputTaskModel.getDocId(), belongOrganizationModel, userAuthenticationModel, SessionUtil.getDetailOrg(), idTask);
		taskAssginAgain.setCheckAssignTaskAgain(true);
		taskAssginAgain.addChangeListener(e->{
			loadData();
			fireEvent(new ClickEvent(this,false));
			dialogTemplate.close();
		});
		dialogTemplate.add(taskAssginAgain);
		if(outputTaskModel.getAttachments().isEmpty()) {
			dialogTemplate.setWidth("50%");
			dialogTemplate.setHeightFull();
		}else {
			dialogTemplate.setSizeFull();
		}
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setLayoutMobile();
		dialogTemplate.open();

	}

	private void openDialogShowHistory() {
		DialogTemplate dialogTemplate = new DialogTemplate("NHẬT KÝ NHIỆM VỤ");
		TaskHistoryEventForm taskEventForm = new TaskHistoryEventForm(idTask);
		dialogTemplate.add(taskEventForm);
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setWidth("90%");
		dialogTemplate.setHeightFull();
		dialogTemplate.setLayoutMobile();
		dialogTemplate.open();
	}
	
	private void openDialogRefuseConfirm() {
		DialogTemplate dialogTemplate = new DialogTemplate("TỪ CHỐI BÁO CÁO");
		
		TaskRefuseConfirmForm taskRefuseConfirmForm = new TaskRefuseConfirmForm(idTask);
		dialogTemplate.add(taskRefuseConfirmForm);
		taskRefuseConfirmForm.addChangeListener(e->{
			loadData();
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

}
