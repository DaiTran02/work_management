package com.ngn.tdnv.doc.forms.create_task;


import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.FileSystemResource;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.flowingcode.vaadin.addons.fontawesome.FontAwesome.Solid.Icon;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.ngn.api.classify_task.ApiClassifyTaskModel;
import com.ngn.api.classify_task.ApiClassifyTaskService;
import com.ngn.api.doc.ApiDocModel;
import com.ngn.api.doc.ApiDocService;
import com.ngn.api.leader_approve_task.ApiLeaderApproveTaskModel;
import com.ngn.api.leader_approve_task.ApiLeaderApproveTaskService;
import com.ngn.api.media.ApiInputMediaModel;
import com.ngn.api.media.ApiMediaModel;
import com.ngn.api.media.ApiMediaService;
import com.ngn.api.organization.ApiOrganizationModel;
import com.ngn.api.organization.ApiOrganizationService;
import com.ngn.api.organization.ApiUserOrganizationExModel;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tasks.ApiFollowerModel;
import com.ngn.api.tasks.ApiInputTaskModel;
import com.ngn.api.tasks.ApiOrgGeneralOfTaskModel;
import com.ngn.api.tasks.ApiOutputTaskModel;
import com.ngn.api.tasks.ApiTaskService;
import com.ngn.api.utils.ApiKeyValueModel;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.models.sign_in_org.SignInOrgModel;
import com.ngn.setting.leader_classify.model.FilterClassifyLeaderModel;
import com.ngn.tdnv.doc.forms.DocEditForm;
import com.ngn.tdnv.doc.forms.DocListForm;
import com.ngn.tdnv.doc.forms.components.HeaderComponent;
import com.ngn.tdnv.doc.forms.create_task.form_selected.TaskSelectOwnerForm;
import com.ngn.tdnv.doc.forms.create_task.level_pass.TaskChooseAllOrgFormv2;
import com.ngn.tdnv.doc.models.DocModel;
import com.ngn.tdnv.task.models.TaskOrgGeneralModel;
import com.ngn.tdnv.task.models.TaskOutputModel;
import com.ngn.utils.LocalDateUtil;
import com.ngn.utils.PropsUtil;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.ConfirmDialogTemplate;
import com.ngn.utils.components.DialogTemplate;
import com.ngn.utils.components.NotificationTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.ngn.utils.models.model_of_organization.OrganizationModel;
import com.ngn.utils.models.model_of_organization.UserOranizationExModel;
import com.ngn.utils.uploads.UploadModuleBasic;
import com.ngn.utils.uploads.UploadModuleDataModel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBoxVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextAreaVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.dom.Style.AlignItems;
import com.vaadin.flow.dom.Style.Display;
import com.vaadin.flow.dom.Style.FlexDirection;
import com.vaadin.flow.dom.Style.FlexWrap;

public class TaskCreateFormV2 extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private Logger logger = LogManager.getLogger(this);
	private boolean isUiMobile = false;
	private boolean	isUserAllPermission = PropsUtil.isAllPermission();

	//If assigned spontaneously
	private boolean isTaskAssign = false;

	private List<String> listIdAttachmentInUploadModule = new ArrayList<String>();

	private ButtonTemplate btnAttachmentOfDoc = new ButtonTemplate();

	private ButtonTemplate btnAssigned = new ButtonTemplate("Giao nhiệm vụ",FontAwesome.Solid.PAPER_PLANE.create());
	private Checkbox cbRequiredConfirm = new Checkbox("Nhiệm vụ cần được xác nhận từ đơn vị giao sau khi Cán bộ/ Đơn vị được giao báo cáo nhiệm vụ hoàn thành");
	private Checkbox cbRequiredKPI = new Checkbox("Đưa nhiệm vụ này vào đánh giá KPI");

	private TextField txtTitle = new TextField("Tiêu đề nhiệm vụ *");
	private TextArea txtDescr = new TextArea("Nội dung nhiệm vụ *");
	private DateTimePicker dateStartTime = new DateTimePicker("Thời gian giao nhiệm vụ *");

	private DatePicker dateEndTime2 = new DatePicker("Hạn xử lý");
	private TimePicker timePicker = new TimePicker();

	private ComboBox<Pair<String, String>> cmbUrgency = new ComboBox<Pair<String,String>>("Độ khẩn");
	private List<Pair<String, String>> listUrgency = new CommonsArrayList<Pair<String,String>>();

	private ApiOrganizationModel currentOrg;

	private TextField txtUserOfOwner = new TextField("Người chỉ đạo giao nhiệm vụ");
	private ButtonTemplate btnChooseOwner = new ButtonTemplate("Chọn người chỉ đạo",FontAwesome.Solid.HAND_POINTER.create());
	private Map<ApiOrganizationModel, ApiUserOrganizationExModel> mapUserOfOwner = new HashMap<ApiOrganizationModel, ApiUserOrganizationExModel>();

	private TextField txtUserAssitant = new TextField("Người soạn thảo nhiệm vụ (mặc định là người đang soạn)");
	private ButtonTemplate btnChooseUserAssitant = new ButtonTemplate("Chọn người soạn thảo",FontAwesome.Solid.HAND_POINTER.create());
	private Map<ApiOrganizationModel, ApiUserOrganizationExModel> mapUserOfAssistant = new HashMap<ApiOrganizationModel, ApiUserOrganizationExModel>();

	private ButtonTemplate btnChooseAssignee = new ButtonTemplate("Chọn đơn vị xử lý",FontAwesome.Solid.HAND_POINTER.create());
	private MultiSelectComboBox<Pair<String, String>> multiSelectCmbAssignee = new MultiSelectComboBox<Pair<String,String>>("Đơn vị xử lý (chủ trì) đã chọn (bắt buộc)");

	private ButtonTemplate btnChooseSupport = new ButtonTemplate("Chọn đơn vị phối hợp",FontAwesome.Solid.HAND_POINTER.create());
	private MultiSelectComboBox<Pair<String,String>> multiSelectCmbSupport = new MultiSelectComboBox<Pair<String,String>>("Đơn vị phối hợp đã chọn (cần chọn đơn vị xử lý mới chọn được đơn vị phối hợp)");

	private ButtonTemplate btnChooseFollowUp = new ButtonTemplate("Chọn đơn vị theo dõi",FontAwesome.Solid.HAND_POINTER.create());
	private MultiSelectComboBox<Pair<String, String>> multiSelectCmbFollowUp = new MultiSelectComboBox<Pair<String,String>>("Khối theo dõi hoặc đồng chỉ đạo (nếu có)");


	private List<OrganizationModel> listOrgAssigneeIsChoose = new ArrayList<OrganizationModel>();
	private Map<OrganizationModel, UserOranizationExModel> mapOrgAndUserAssignee = new HashMap<OrganizationModel, UserOranizationExModel>();
	private Map<OrganizationModel, UserOranizationExModel> mapAllOrgAndUserAssinee = new HashMap<OrganizationModel, UserOranizationExModel>();

	private List<OrganizationModel> listOrgSupportIsChoose = new ArrayList<OrganizationModel>();
	private Map<OrganizationModel, UserOranizationExModel> mapOrgAndUserSupport = new HashMap<OrganizationModel, UserOranizationExModel>();
	private Map<OrganizationModel, UserOranizationExModel> mapAllOrgAndUserSupport = new HashMap<OrganizationModel, UserOranizationExModel>();

	private List<OrganizationModel> listOrgFollowerIsChoose = new ArrayList<OrganizationModel>();
	private Map<OrganizationModel, UserOranizationExModel> mapOrgAndUserFollower = new HashMap<OrganizationModel, UserOranizationExModel>();
	private Map<OrganizationModel, UserOranizationExModel> mapAllOrgAndUserFollower = new HashMap<OrganizationModel, UserOranizationExModel>();

	private HorizontalLayout hLayoutGeneral = new HorizontalLayout();

	private VerticalLayout vLayoutLeft = new VerticalLayout();
	private VerticalLayout vLayoutRight = new VerticalLayout();
	private Details detailsInfoDoc = new Details();
	private Details detailFormTask = new Details();
	private UploadModuleBasic upload = new UploadModuleBasic();

	private DocModel docModel;
	private TaskOutputModel outputTaskModel;

	private boolean checkUpdateTask = false;
	private boolean checkAssignTaskAgain = false;

	private String idDoc;
	private BelongOrganizationModel belongOrganizationModel;
	private UserAuthenticationModel userAuthenticationModel;
	private SignInOrgModel signInOrgModel;
	private String idTask;
	public TaskCreateFormV2(String idDoc,BelongOrganizationModel belongOrganizationModel,UserAuthenticationModel userAuthenticationModel,SignInOrgModel signInOrgModel,String idTask) {
		this.signInOrgModel = signInOrgModel;
		this.belongOrganizationModel = belongOrganizationModel;
		this.userAuthenticationModel = userAuthenticationModel;
		if(idDoc != null) {
			this.idDoc = idDoc;
			loadData();
		}
		checkUiMobile();
		buildLayout();
		initCurrentOwner();
		initCurrentAssistant();
		loadCmbAssignee();
		if(idTask != null) {
			this.checkUpdateTask = true;
			this.idTask = idTask;
			loadDataOfTask();
		}
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.setPadding(false);

		if(docModel != null) {
			if(!docModel.getAttachments().isEmpty()) {
				hLayoutGeneral.add(vLayoutLeft);
				vLayoutRight.setWidth("50%");
			}else {
				vLayoutRight.setSizeFull();
			}
			createLayoutInfoDoc(docModel);
		}else {
			createLayoutDocEmpty();
		}

		if(outputTaskModel != null) {
			if(!outputTaskModel.getAttachments().isEmpty()) {
				hLayoutGeneral.add(vLayoutLeft);
				vLayoutRight.setWidth("50%");
			}else {
				vLayoutRight.setSizeFull();
			}
		}

		vLayoutLeft.setWidth("50%");


		vLayoutRight.setHeightFull();
		vLayoutRight.add(detailsInfoDoc,detailFormTask);

		//		if(idDoc == null) {
		//			vLayoutRight.remove(detailsInfoDoc);
		//		}
		loadDataForFormTask(docModel);

		hLayoutGeneral.setPadding(false);
		hLayoutGeneral.add(vLayoutRight);
		hLayoutGeneral.setSizeFull();



		createLayoutFormTask();
		this.add(hLayoutGeneral);
		if(docModel != null && !docModel.getAttachments().isEmpty()) {
			createLayoutForPDFView(docModel.getAttachments());
		}else {
			vLayoutRight.setSizeFull();
		}

		if(isUiMobile) {
			hLayoutGeneral.getStyle().setDisplay(Display.FLEX).setFlexDirection(FlexDirection.COLUMN);
			vLayoutLeft.setWidthFull();
			vLayoutRight.setWidthFull();
		}

	}

	@Override
	public void configComponent() {
		
//        PopoverUserOfOwner popoverUserOfOwner = new PopoverUserOfOwner();
//        popoverUserOfOwner.setTarget(txtUserOfOwner);
//        popoverUserOfOwner.setPosition(PopoverPosition.BOTTOM);
//        
//        popoverUserOfOwner.addChangeListener(e->{
//			mapUserOfOwner.clear();
//			mapUserOfOwner.putAll(popoverUserOfOwner.getMapOwnerSelected());
//			initTxtOwner();
//			popoverUserOfOwner.close();
//        });
        
//		txtUserOfOwner.addValueChangeListener(e->{
//			popoverUserOfOwner.loadData(txtUserOfOwner.getValue());
//		});
//        
//		txtUserOfOwner.addFocusListener(e->{
//			popoverUserOfOwner.doFocus();
//		});

		
//		PopoverUserOfOwner popoverUserOfAssistant = new PopoverUserOfOwner();
//		popoverUserOfAssistant.setTarget(txtUserAssitant);
//		popoverUserOfAssistant.setPosition(PopoverPosition.BOTTOM);
//		
//		popoverUserOfAssistant.addChangeListener(e->{
//			mapUserOfAssistant.clear();
//			mapUserOfAssistant.putAll(popoverUserOfAssistant.getMapOwnerSelected());
//			initTxtAssistant();
//			popoverUserOfAssistant.close();
//		});
//		
//		txtUserAssitant.addValueChangeListener(e->popoverUserOfAssistant.loadData(txtUserAssitant.getValue()));
//		txtUserAssitant.addFocusListener(e->popoverUserOfAssistant.doFocus());
		
		
		btnChooseUserAssitant.addClickListener(e->{
			openDialogChooseAssistant();
		});
		

		btnChooseAssignee.addClickListener(e->{
			//			openDialogChooseOrg(belongOrganizationModel.getOrganizationId());
			openDialogChooseOrgToPerformTask(belongOrganizationModel.getOrganizationId());
		});

		btnChooseSupport.addClickListener(e->{
			//			openDialogChooseOrgForSupports(belongOrganizationModel.getOrganizationId());
			openDialogChooseOrgToSupportTask(belongOrganizationModel.getOrganizationId());
		});

		btnChooseFollowUp.addClickListener(e->{
			openDialogChooseGroupFollower(belongOrganizationModel.getOrganizationId());
		});

		btnChooseOwner.addClickListener(e->{
			openDialogChooseOwner();
		});


		btnAssigned.addClickListener(e->{
			saveTask();
		});

		dateEndTime2.addValueChangeListener(e->{
			if(dateEndTime2.getValue() == null) {
				timePicker.setValue(null);
			}else {
				timePicker.setValue(LocalTime.MAX);
			}
		});

	}

	public void loadData() {
		ApiResultResponse<ApiDocModel> data = ApiDocService.getAdoc(idDoc);
		if(data.isSuccess()) {
			docModel = new DocModel(data.getResult());
			loadDataForFormTask(docModel);
		}else {
			logger.error(data);
		}
	}

	private void checkUiMobile() {
		try {
			UI.getCurrent().getPage().retrieveExtendedClientDetails(e->{
				if(e.getScreenWidth() < 768) {
					isUiMobile = true;
				}
			});
		} catch (Exception e) {
		}
	}

	//Get data of task when is update or something
	private void loadDataOfTask() {

		ApiResultResponse<ApiOutputTaskModel> dataTask = ApiTaskService.getAtask(idTask);
		outputTaskModel = new TaskOutputModel(dataTask.getResult());
		List<String> listAttact = new ArrayList<String>();
		dataTask.getResult().getAttachments().stream().forEach(model->{
			listAttact.add(model.toString());
		});
		if(!listAttact.isEmpty()) {
			createLayoutForPDFView(listAttact);
		}

		txtTitle.setValue(outputTaskModel.getTitle());
		txtDescr.setValue(outputTaskModel.getDescription());
		if(outputTaskModel.getEndTime() != 0) {
			dateEndTime2.setValue(LocalDateUtil.longToLocalDate(outputTaskModel.getEndTime()));
			timePicker.setValue(LocalDateUtil.longToLocalTime(outputTaskModel.getEndTime()));

		}

		if(outputTaskModel.getCreatedTime() != 0) {
			dateStartTime.setValue(LocalDateUtil.longToLocalDateTime(outputTaskModel.getCreatedTime()));
		}

		listUrgency.stream().forEach(model->{
			if(model.getKey().equals(outputTaskModel.getPriority())) {
				cmbUrgency.setValue(model);
			}
		});


		// Người chỉ đạo
		mapUserOfOwner.clear();

		ApiOrganizationModel orgOwner = new ApiOrganizationModel();
		orgOwner.setId(outputTaskModel.getOwner().getOrganizationId());
		orgOwner.setName(outputTaskModel.getOwner().getOrganizationName());

		ApiUserOrganizationExModel userOwner = new ApiUserOrganizationExModel();
		userOwner.setUserId(outputTaskModel.getOwner().getOrganizationUserId().toString());
		userOwner.setFullName(outputTaskModel.getOwner().getOrganizationUserName().toString());
		mapUserOfOwner.put(orgOwner, userOwner);

		initTxtOwner();

		// Người soạn thảo
		mapUserOfAssistant.clear();
		TaskOrgGeneralModel	assitantce = outputTaskModel.getAssistant();

		System.out.println("Check: "+assitantce);

		ApiOrganizationModel assitantceOrg = new ApiOrganizationModel();
		assitantceOrg.setId(assitantce.getOrganizationId());
		assitantceOrg.setName(assitantce.getOrganizationName());

		ApiUserOrganizationExModel assitanceUser = new ApiUserOrganizationExModel();
		assitanceUser.setUserId(assitantce.getOrganizationUserId().toString());
		assitanceUser.setUserName(assitantce.getOrgUserName());
		assitanceUser.setFullName(assitantce.getOrgUserName());

		mapUserOfAssistant.put(assitantceOrg, assitanceUser);
		initTxtAssistant();



		//		ApiResultResponse<ApiOrganizationModel> getOrg = ApiOrganizationService.getOneOrg(outputTaskModel.getAssignee().getOrganizationId());
		OrganizationModel organizationModel = new OrganizationModel();
		organizationModel.setId(outputTaskModel.getAssignee().getOrganizationId());
		organizationModel.setName(outputTaskModel.getAssignee().getOrganizationName());

		listOrgAssigneeIsChoose.clear();
		if(outputTaskModel.getAssignee().getOrganizationUserId() != null) {
			UserOranizationExModel userOranizationExModel = new UserOranizationExModel();
			userOranizationExModel.setUserId(outputTaskModel.getAssignee().getOrganizationUserId().toString());
			userOranizationExModel.setFullName(outputTaskModel.getAssignee().getOrganizationUserName().toString());
			mapOrgAndUserAssignee.put(organizationModel, userOranizationExModel);
		}else {
			mapOrgAndUserAssignee.put(organizationModel, null);
		}
		loadCmbAssignee();
		multiSelectCmbAssignee.setReadOnly(true);

		//Support
		listOrgSupportIsChoose.clear();
		if(!outputTaskModel.getSupports().isEmpty()) {

			for(TaskOrgGeneralModel orgGeneralOfTaskModel : outputTaskModel.getSupports()) {
				OrganizationModel orgSupport = new OrganizationModel();
				orgSupport.setId(orgGeneralOfTaskModel.getOrganizationId());
				orgSupport.setName(orgGeneralOfTaskModel.getOrganizationName());
				if(orgGeneralOfTaskModel.getOrganizationUserId()!=null) {
					UserOranizationExModel userOranizationExModel = new UserOranizationExModel();
					userOranizationExModel.setUserId(orgGeneralOfTaskModel.getOrganizationUserId().toString());
					userOranizationExModel.setFullName(orgGeneralOfTaskModel.getOrganizationUserName().toString());
					mapOrgAndUserSupport.put(orgSupport, userOranizationExModel);
				}else {
					mapOrgAndUserSupport.put(orgSupport, null);
				}
			}
			loadCmbSupport();
		}

		multiSelectCmbSupport.setReadOnly(true);

		//Follower

		try {
			outputTaskModel.getFollowers().forEach(model->{
				OrganizationModel organizationFollower = new OrganizationModel();
				organizationFollower.setId(model.getOrganizationId());
				organizationFollower.setName(model.getOrganizationName());
				if(model.getOrganizationUserId() != null) {
					UserOranizationExModel userOranizationExModel = new UserOranizationExModel();
					userOranizationExModel.setUserId(model.getOrganizationUserId().toString());
					userOranizationExModel.setUserName(model.getOrganizationUserName().toString());
					userOranizationExModel.setFullName(model.getOrganizationUserName().toString());
					mapOrgAndUserFollower.put(organizationFollower, userOranizationExModel);
				}else {
					listOrgFollowerIsChoose.add(organizationFollower);
				}
			});
		} catch (Exception e) {
		}

		loadCmbFollower();
		multiSelectCmbFollowUp.setReadOnly(true);

		cbRequiredConfirm.setValue(outputTaskModel.isRequiredConfirm());
		cbRequiredKPI.setValue(outputTaskModel.isRequiredKpi());

		if(checkUpdateTask) {
			btnAssigned.setText("Cập nhật nhiệm vụ");
			btnAssigned.setIcon(FontAwesome.Solid.EDIT.create());

			//			btnChooseAssignee.setEnabled(false);
		}
	}

	//View PDF
	private void createLayoutForPDFView(List<String> listFile) {
		vLayoutLeft.removeAll();
		System.out.println("List FIle"+listFile);
		ViewDocFromFile viewDocFromFile = new ViewDocFromFile(listFile);
		viewDocFromFile.setSizeFull();
		viewDocFromFile.setPadding(false);
		if(viewDocFromFile.getListFileCmb().isEmpty()) {
			vLayoutLeft.setVisible(false);
			hLayoutGeneral.remove(vLayoutLeft);
			hLayoutGeneral.setWidthFull();
			vLayoutRight.setWidthFull();
		}else {
			vLayoutLeft.removeAll();
			vLayoutLeft.add(viewDocFromFile);
			viewDocFromFile.getCmbFile().setLabel("Danh sách file đính kèm từ văn bản");
			btnAttachmentOfDoc.addClickListener(e->viewDocFromFile.getCmbFile().focus());
		}

	}

	//Load detail doc
	private void createLayoutInfoDoc(DocModel docModel) {
		VerticalLayout contentInFoDoc = new VerticalLayout();

		ButtonTemplate btnUpdate = new ButtonTemplate("Cập nhật", FontAwesome.Solid.EDIT.create());
		btnUpdate.addThemeVariants(ButtonVariant.LUMO_TERTIARY,ButtonVariant.LUMO_SUCCESS);
		btnUpdate.addClickListener(e->{
			openDialogUpdateDoc(idDoc);
		});

		if(!docModel.getOwner().getOrganizationId().equals(belongOrganizationModel.getOrganizationId())) {
			Span spanWarningOwner = new Span("Cảnh báo: Văn bản này không thuộc quyền quản lý của đơn vị đang sử dụng, bạn sẽ không thấy văn bản này ở danh sách văn bản của bạn sau khi sử dụng xong, "
					+ "nhiệm vụ vẫn sẽ được giao dựa vào thông tin của văn bản và thuộc quyền quản lý của đơn vị này.");

			spanWarningOwner.getStyle().setBackground("#ffe4e487").setBorderLeft("5px solid #ff6f6f").setPadding("5px").setBorderRadius("10px").setColor("#9f3030").setFontWeight(500);

			btnUpdate.setEnabled(false);

			contentInFoDoc.add(spanWarningOwner);
		}

		HorizontalLayout hLayout1 = new HorizontalLayout();
		hLayout1.setWidthFull();
		hLayout1.add(createLayoutKeyValue("Ký hiệu: ", docModel.getSymbol()),createLayoutKeyValue("Số hiệu: ", docModel.getNumber()),
				createLayoutKeyValue("Ngày ký: ", LocalDateUtil.dfDate.format(docModel.getRegDate())),createLayoutKeyValue("Người soạn thảo: ", docModel.getOwner().getOrganizationUserName()));

		if(isUiMobile) {
			hLayout1.getStyle().setDisplay(Display.FLEX).setFlexWrap(FlexWrap.WRAP);
		}

		HorizontalLayout hLayout2 = new HorizontalLayout();


		String classifyName = "";
		String leaderName = "";

		FilterClassifyLeaderModel filterClassifyLeaderModel = new FilterClassifyLeaderModel();
		filterClassifyLeaderModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
		filterClassifyLeaderModel.setLimit(0);
		filterClassifyLeaderModel.setSkip(0);
		filterClassifyLeaderModel.setActive(true);
		ApiResultResponse<List<ApiClassifyTaskModel>> data = ApiClassifyTaskService.getListClassify(filterClassifyLeaderModel);
		for(ApiClassifyTaskModel apiClassifyTaskModel : data.getResult()) {
			if(apiClassifyTaskModel.getId().equals(docModel.getClassifyTaskId())) {
				classifyName = apiClassifyTaskModel.getName();
			}
		}

		FilterClassifyLeaderModel fiLeaderModel = new FilterClassifyLeaderModel();
		fiLeaderModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
		fiLeaderModel.setSkip(0);
		fiLeaderModel.setLimit(0);
		fiLeaderModel.setActive(true);
		ApiResultResponse<List<ApiLeaderApproveTaskModel>> dataLeader = ApiLeaderApproveTaskService.getListLeader(fiLeaderModel);
		for(ApiLeaderApproveTaskModel apiLeaderApproveTaskModel : dataLeader.getResult()) {
			if(apiLeaderApproveTaskModel.getId().equals(docModel.getLeaderApproveTaskId())) {
				leaderName = apiLeaderApproveTaskModel.getName();
			}
		}

		hLayout2.add(createLayoutKeyValue("Phân loại chỉ đạo: ",docModel.getClassifyTaskId() == null ? "Vui lòng cập nhật" : classifyName),
				createLayoutKeyValue("Người duyệt: ", docModel.getLeaderApproveTaskId() == null ? "Vui lòng cập nhật" : leaderName));
		hLayout2.setWidthFull();


		if(docModel != null) {
			btnAttachmentOfDoc.setText("Đính kèm từ văn bản ("+docModel.getAttachments().size()+")");
		}
		btnAttachmentOfDoc.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

		HorizontalLayout hLayoutButton = new HorizontalLayout();
		hLayoutButton.add(btnUpdate,btnAttachmentOfDoc);

		if(docIsChoose != null) {
			ButtonTemplate btnChangeDoc = new ButtonTemplate("Đổi văn bản",FontAwesome.Solid.REPEAT.create());
			btnChangeDoc.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnChangeDoc.addClickListener(e->{
				openDialogAddDoc(true, docIsChoose,()->{
					if(docIsChoose != null) {
						createLayoutInfoDoc(docIsChoose);
						idDoc = docIsChoose.getId();
						this.docModel = docIsChoose;
					}

				});
			});

			ButtonTemplate btnDeletedDoc = new ButtonTemplate("Bỏ chọn văn bản",FontAwesome.Solid.CLOSE.create());
			btnDeletedDoc.addThemeVariants(ButtonVariant.LUMO_ERROR);
			btnDeletedDoc.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnDeletedDoc.addClickListener(e->{
				ConfirmDialogTemplate confirmDialogTemplate = new ConfirmDialogTemplate("Bỏ chọn văn bản này");
				confirmDialogTemplate.setText("Bỏ chọn văn bản thì nhiệm vụ sẽ trở thành nhiệm vụ tự phát");
				confirmDialogTemplate.addConfirmListener(ev->{
					docIsChoose = null;
					idDoc = null;
					this.docModel = null;
					createLayoutDocEmpty();
				});
				confirmDialogTemplate.setCancelable(true);
				confirmDialogTemplate.open();
			});		
			hLayoutButton.add(btnChangeDoc,btnDeletedDoc);
		}

		contentInFoDoc.add(hLayout1,createLayoutKeyValue("Trích yếu: ", docModel.getSummary()),hLayout2,hLayoutButton);

		detailsInfoDoc.removeAll();
		detailsInfoDoc.setSummaryText("Thông tin văn bản");
		detailsInfoDoc.add(contentInFoDoc);
		detailsInfoDoc.setOpened(true);
		detailsInfoDoc.addThemeVariants(DetailsVariant.REVERSE,DetailsVariant.FILLED);
		detailsInfoDoc.setWidthFull();
		detailsInfoDoc.getStyle().setBackground("white").setBoxShadow("rgba(0, 0, 0, 0.16) 0px 1px 4px");
	}

	private void createLayoutDocEmpty() {
		detailsInfoDoc.removeAll();
		VerticalLayout vLayoutInfoDoc = new VerticalLayout();
		Span spTitle = new Span("Nhiệm vụ không văn bản sẽ được đưa vào danh sách nhiệm vụ tự phát");
		ButtonTemplate btnChooseDoc = new  ButtonTemplate("Chọn văn bản",FontAwesome.Solid.PLUS.create());
		btnChooseDoc.addClickListener(e->{
			openDialogAddDoc(true, docIsChoose,()->{
				if(docIsChoose != null) {
					createLayoutInfoDoc(docIsChoose);
					idDoc = docIsChoose.getId();
					this.docModel = docIsChoose;
				}

			});
		});
		vLayoutInfoDoc.add(spTitle,btnChooseDoc);	
		detailsInfoDoc.setSummaryText("Thông tin văn bản");
		detailsInfoDoc.add(vLayoutInfoDoc);
		detailsInfoDoc.setOpened(true);
		detailsInfoDoc.addThemeVariants(DetailsVariant.REVERSE,DetailsVariant.FILLED);
		detailsInfoDoc.setWidthFull();
		detailsInfoDoc.getStyle().setBackground("white").setBoxShadow("rgba(0, 0, 0, 0.16) 0px 1px 4px");
	}


	//Layout Task
	private void createLayoutFormTask() {
		VerticalLayout content = new VerticalLayout();
		content.setWidthFull();

		txtTitle.setWidthFull();
		txtTitle.addThemeVariants(TextFieldVariant.LUMO_SMALL);

		HorizontalLayout hLayout1 = new HorizontalLayout();

		txtDescr.setWidthFull();
		txtDescr.addThemeVariants(TextAreaVariant.LUMO_SMALL);

		cmbUrgency.addThemeVariants(ComboBoxVariant.LUMO_SMALL);


		hLayout1.setWidthFull();
		hLayout1.add(txtTitle,cmbUrgency);


		HorizontalLayout hLayout2 = new HorizontalLayout();

		Icon icon = FontAwesome.Solid.ARROW_RIGHT.create();
		icon.setSize("15px");
		icon.getStyle().set("margin-top", "35px");

		dateStartTime.setWidth("48%");
		dateStartTime.setDatePickerI18n(LocalDateUtil.i18nVietNam());


		dateEndTime2.setHelperText("Để trống thì nhiệm vụ sẽ là không hạn xử lý");
		dateEndTime2.setI18n(LocalDateUtil.i18nVietNam());
		dateEndTime2.setLocale(LocalDateUtil.localeVietNam());

		HorizontalLayout hLayoutDateEndTime = new HorizontalLayout();
		hLayoutDateEndTime.setWidth("48%");
		hLayoutDateEndTime.getStyle().setAlignItems(AlignItems.CENTER);

		hLayoutDateEndTime.add(dateEndTime2,timePicker);

		dateEndTime2.setWidthFull();
		timePicker.setWidthFull();
		timePicker.getStyle().setMarginTop("10px");
		timePicker.setHelperText("");
		

		hLayout2.add(dateStartTime,icon,hLayoutDateEndTime);
		hLayout2.setWidthFull();


		VerticalLayout vLayout3 = new VerticalLayout();

		String widthButton = "180px";
		
		
		VerticalLayout vLayoutDirectedInformation = new VerticalLayout();
		
		HeaderComponent directedInfor = new HeaderComponent("Thông tin chỉ đạo");
		directedInfor.addLayout(vLayoutDirectedInformation);
		
		
		VerticalLayout vLayoutInformationPerformance = new VerticalLayout();
		
		HeaderComponent informationPerfor = new HeaderComponent("Thông tin thực hiện");
		informationPerfor.addLayout(vLayoutInformationPerformance);
		

		//Choose Owner
		HorizontalLayout hLayoutOwner = new HorizontalLayout();
		hLayoutOwner.setWidthFull();

		btnChooseOwner.setWidth(widthButton);
		btnChooseOwner.addThemeVariants(ButtonVariant.LUMO_SMALL);
		btnChooseOwner.addThemeVariants(ButtonVariant.LUMO_ERROR);
		btnChooseOwner.getStyle().setMarginTop("28px");

		txtUserOfOwner.addThemeVariants(TextFieldVariant.LUMO_SMALL);
//		txtUserOfOwner.setReadOnly(true);
		txtUserOfOwner.setWidthFull();
		txtUserOfOwner.setClearButtonVisible(true);

		hLayoutOwner.add(txtUserOfOwner,btnChooseOwner);
		


		//Choose Assistant
		HorizontalLayout hLayoutAssistant = new HorizontalLayout();
		hLayoutAssistant.setWidthFull();

		btnChooseUserAssitant.addThemeVariants(ButtonVariant.LUMO_SMALL);
		btnChooseUserAssitant.getStyle().setMarginTop("28px");
		btnChooseUserAssitant.setWidth(widthButton);

		txtUserAssitant.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		txtUserAssitant.setWidthFull();
		txtUserAssitant.setClearButtonVisible(true);

		hLayoutAssistant.add(txtUserAssitant,btnChooseUserAssitant);


		//Choose Assignee
		HorizontalLayout hLayoutAssignee = new HorizontalLayout();

		btnChooseAssignee.addThemeVariants(ButtonVariant.LUMO_SMALL,ButtonVariant.LUMO_SUCCESS);
		btnChooseAssignee.getStyle().setMarginTop("28px");
		btnChooseAssignee.setWidth(widthButton);


		multiSelectCmbAssignee.setWidthFull();
		multiSelectCmbAssignee.addThemeVariants(MultiSelectComboBoxVariant.LUMO_SMALL);
		multiSelectCmbAssignee.setReadOnly(true);

		hLayoutAssignee.setWidthFull();
		hLayoutAssignee.add(multiSelectCmbAssignee,btnChooseAssignee);
		hLayoutAssignee.getStyle().setAlignItems(AlignItems.CENTER);

		//Choose Support
		HorizontalLayout hLayoutSupport = new HorizontalLayout();

		btnChooseSupport.addThemeVariants(ButtonVariant.LUMO_SMALL);
		btnChooseSupport.getStyle().setMarginTop("28px");
		btnChooseSupport.setWidth(widthButton);

		multiSelectCmbSupport.addThemeVariants(MultiSelectComboBoxVariant.LUMO_SMALL);
		multiSelectCmbSupport.setWidthFull();
		multiSelectCmbSupport.setReadOnly(true);

		hLayoutSupport.setWidthFull();
		hLayoutSupport.add(multiSelectCmbSupport,btnChooseSupport);
		hLayoutSupport.getStyle().setAlignItems(AlignItems.CENTER);

		
		txtUserOfOwner.setReadOnly(true);
		txtUserAssitant.setReadOnly(true);

		//Choose Follower
		HorizontalLayout hLayoutFollowUp = new HorizontalLayout();

		btnChooseFollowUp.addThemeVariants(ButtonVariant.LUMO_SMALL);
		btnChooseFollowUp.getStyle().setMarginTop("28px");
		btnChooseFollowUp.setWidth(widthButton);

		multiSelectCmbFollowUp.setWidthFull();
		multiSelectCmbFollowUp.addThemeVariants(MultiSelectComboBoxVariant.LUMO_SMALL);
		multiSelectCmbFollowUp.setReadOnly(true);

		hLayoutFollowUp.setWidthFull();
		hLayoutFollowUp.add(multiSelectCmbFollowUp,btnChooseFollowUp);
		hLayoutFollowUp.getStyle().setAlignItems(AlignItems.CENTER);
		
		
		vLayoutDirectedInformation.add(hLayoutOwner,hLayoutFollowUp,hLayoutAssistant);
		vLayoutInformationPerformance.add(hLayoutAssignee,hLayoutSupport);

		vLayout3.add(directedInfor,informationPerfor);

		vLayout3.setWidthFull();


		btnAssigned.addThemeVariants(ButtonVariant.LUMO_SUCCESS,ButtonVariant.LUMO_PRIMARY);
		btnAssigned.getStyle().setMarginLeft("auto");


		cbRequiredConfirm.setValue(true);

		upload = new UploadModuleBasic();

		Span titleAttach = new Span("*Thêm đính kèm");
		if(idDoc != null) {
			titleAttach.setText("*Đính kèm nhiệm vụ được lấy từ văn bản, hoặc có thể thêm đính kèm tại đây");
		}

		titleAttach.getStyle().setFontWeight(600);

		content.add(hLayout1,txtDescr,hLayout2,vLayout3,cbRequiredKPI,cbRequiredConfirm,titleAttach,upload,btnAssigned);
		upload.initUpload();

		detailFormTask.removeAll();
		detailFormTask.setOpened(true);
		detailFormTask.addThemeVariants(DetailsVariant.REVERSE,DetailsVariant.FILLED);
		detailFormTask.setWidthFull();
		detailFormTask.setSummaryText("Thông tin giao nhiệm vụ");
		detailFormTask.add(content);
		detailFormTask.getStyle().setMarginBottom("10px");
		detailFormTask.getStyle().setBackground("white").setBoxShadow("rgba(0, 0, 0, 0.16) 0px 1px 4px");
	}


	//Data form task
	private void loadDataForFormTask(DocModel docModel) {

		if(docModel != null) {
			txtTitle.setValue(docModel.getSummary());
			txtDescr.setValue(docModel.getSummary());
		}else {
			txtTitle.clear();
			txtDescr.clear();
		}
		dateStartTime.setValue(LocalDateTime.now());
		dateStartTime.setMax(LocalDateTime.now().plusDays(1));
		dateStartTime.setLocale(LocalDateUtil.localeVietNam());

		//		dateEndTime.addValueChangeListener(e->{
		//			dateStartTime.setValue(LocalDateTime.now().plusMinutes(1));
		//			dateStartTime.setMax(dateEndTime.getValue());
		//			
		//			
		//			dateEndTime.setValue(LocalDateTime.now());
		//		});
		loadUrgency();

		if(idDoc == null) {
			listOrgAssigneeIsChoose.clear();
			mapOrgAndUserAssignee.clear();	
			loadCmbAssignee();

			listOrgSupportIsChoose.clear();
			mapOrgAndUserSupport.clear();
			loadCmbSupport();

			//			listFollowerIsChoose.clear();
			//			mapGroupAndUserFollower.clear();

			listOrgFollowerIsChoose.clear();
			mapOrgAndUserFollower.clear();

			loadCmbFollower();
		}

	}


	private void loadUrgency() {
		listUrgency.clear();
		try {
			ApiResultResponse<List<ApiKeyValueModel>> data = ApiDocService.getPriority();
			if(data.getStatus() == 201 || data.getStatus() == 200) {
				data.getResult().stream().forEach(model->{
					listUrgency.add(Pair.of(model.getKey(),model.getName()));
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		cmbUrgency.setItems(listUrgency);
		cmbUrgency.setItemLabelGenerator(Pair::getRight);
		cmbUrgency.setValue(listUrgency.get(0));
	}

	//Owner
	private void openDialogChooseOwner() {
		DialogTemplate dialogTemplate = new DialogTemplate("Chọn người chỉ đạo");

		TaskSelectOwnerForm taskSelectOwnerForm = new TaskSelectOwnerForm(belongOrganizationModel.getOrganizationId(),mapUserOfOwner);
		dialogTemplate.add(taskSelectOwnerForm);
		taskSelectOwnerForm.addChangeListener(e->{
			mapUserOfOwner.clear();
			mapUserOfOwner.putAll(taskSelectOwnerForm.getMapOwnerSelected());
			initTxtOwner();
			dialogTemplate.close();
		});

		dialogTemplate.getBtnSave().addClickListener(e->{
			taskSelectOwnerForm.save();
		});

		dialogTemplate.setSizeFull();
		dialogTemplate.open();
	}

	private void initTxtOwner() {
		mapUserOfOwner.forEach((k,v)->{
			txtUserOfOwner.setValue(v.getFullName()+" ("+k.getName()+")");
		});
	}


	private void initCurrentOwner() {
		currentOrg = getOrg(belongOrganizationModel.getOrganizationId());
		if(currentOrg != null) {
			if(currentOrg.getUserOrganizationExpands() != null) {
				currentOrg.getUserOrganizationExpands().forEach(model->{
					if(model.getUserId().equals(userAuthenticationModel.getId())) {
						mapUserOfOwner.put(currentOrg, model);
						txtUserOfOwner.setValue(model.getFullName() + " ("+currentOrg.getName()+")");
					}
				});
			}
		}
	}

	//Assistant
	private void openDialogChooseAssistant() {
		DialogTemplate dialogTemplate = new DialogTemplate("Người soạn thảo nhiệm vụ");
		TaskSelectOwnerForm taskSelectOwnerForm = new TaskSelectOwnerForm(belongOrganizationModel.getOrganizationId(), mapUserOfAssistant);
		taskSelectOwnerForm.addChangeListener(e->{
			mapUserOfAssistant.clear();
			mapUserOfAssistant.putAll(taskSelectOwnerForm.getMapOwnerSelected());
			initTxtAssistant();
			dialogTemplate.close();
		});

		dialogTemplate.getBtnSave().addClickListener(e->{
			taskSelectOwnerForm.save();
		});
		dialogTemplate.add(taskSelectOwnerForm);

		dialogTemplate.setSizeFull();
		dialogTemplate.open();
	}

	private void initTxtAssistant() {
		mapUserOfAssistant.forEach((k,v)->{
			txtUserAssitant.setValue(v.getFullName()+" ("+k.getName()+")");
		});
	}

	private void initCurrentAssistant() {
		if(currentOrg != null) {
			if(currentOrg.getUserOrganizationExpands() != null) {
				currentOrg.getUserOrganizationExpands().forEach(model->{
					if(model.getUserId().equals(userAuthenticationModel.getId())) {
						mapUserOfAssistant.put(currentOrg, model);
						txtUserAssitant.setValue(model.getFullName() + " ("+currentOrg.getName()+")");
					}
				});
			}
		}
	}

	private ApiOrganizationModel getOrg(String idOrg) {
		ApiResultResponse<ApiOrganizationModel> data = ApiOrganizationService.getOneOrg(idOrg);
		if(data.isSuccess()) {
			return data.getResult();
		}
		return null;
	}


	//Load data for Assignee
	private void loadCmbAssignee() {
		mapAllOrgAndUserAssinee = new HashMap<OrganizationModel, UserOranizationExModel>();
		List<Pair<String, String>> listDataAssignee = new ArrayList<Pair<String,String>>();
		if(!listOrgAssigneeIsChoose.isEmpty()) {
			listOrgAssigneeIsChoose.stream().forEach(model->{
				listDataAssignee.add(Pair.of(model.getId(),model.getName()));
				mapAllOrgAndUserAssinee.put(model, null);
			});
		}
		for(Map.Entry<OrganizationModel, UserOranizationExModel> m : mapOrgAndUserAssignee.entrySet()) {
			String userName = m.getValue() == null ? "" : " ("+m.getValue().getFullName()+")";
			listDataAssignee.add(Pair.of(m.getKey().getId(),m.getKey().getName()+userName));
			mapAllOrgAndUserAssinee.put(m.getKey(), m.getValue());
		}

		multiSelectCmbAssignee.setItems(listDataAssignee);
		multiSelectCmbAssignee.setItemLabelGenerator(Pair::getRight);
		multiSelectCmbAssignee.select(listDataAssignee);

		btnChooseSupport.setEnabled(true);
		if(mapOrgAndUserAssignee.isEmpty()) {
			btnChooseSupport.setEnabled(false);
		}

		if(checkUpdateTask) {
			btnChooseAssignee.setVisible(false);
		}

	}

	//Load data for Support
	private void loadCmbSupport() {
		mapAllOrgAndUserSupport = new HashMap<OrganizationModel, UserOranizationExModel>();
		List<Pair<String,String>> listDataSupport = new ArrayList<Pair<String,String>>();
		if(!listOrgSupportIsChoose.isEmpty()) {
			listOrgSupportIsChoose.stream().forEach(model->{
				listDataSupport.add(Pair.of(model.getId(),model.getName()));
				mapAllOrgAndUserSupport.put(model, null);
			});
		}
		for(Map.Entry<OrganizationModel, UserOranizationExModel> m : mapOrgAndUserSupport.entrySet()) {
			String userName = m.getValue() == null ? "" : " ("+m.getValue().getFullName()+")"; 
			listDataSupport.add(Pair.of(m.getKey().getId(),m.getKey().getName()+userName));
			mapAllOrgAndUserSupport.put(m.getKey(), m.getValue());
		}

		multiSelectCmbSupport.setItems(listDataSupport);
		multiSelectCmbSupport.setItemLabelGenerator(Pair::getRight);
		multiSelectCmbSupport.select(listDataSupport);

	}


	//Load data for Follower
	private void loadCmbFollower() {
		mapAllOrgAndUserFollower = new HashMap<OrganizationModel, UserOranizationExModel>();
		List<Pair<String, String>> listDataOrgFollower = new ArrayList<Pair<String,String>>();
		if(!listOrgFollowerIsChoose.isEmpty()) {
			listOrgFollowerIsChoose.stream().forEach(model->{
				listDataOrgFollower.add(Pair.of(model.getId(),model.getName()));
				mapAllOrgAndUserFollower.put(model, null);
			});
		}

		for(Map.Entry<OrganizationModel, UserOranizationExModel> m : mapOrgAndUserFollower.entrySet()) {
			// Username = fullname
			String nameUser = m.getValue() == null ? "" : " ( "+m.getValue().getFullName()+" ) ";
			listDataOrgFollower.add(Pair.of(m.getKey().getId(),m.getKey().getName() + nameUser));
			mapAllOrgAndUserFollower.put(m.getKey(), m.getValue());
		}

		multiSelectCmbFollowUp.setItems(listDataOrgFollower);
		multiSelectCmbFollowUp.setItemLabelGenerator(Pair::getRight);
		multiSelectCmbFollowUp.select(listDataOrgFollower);
	}

	private void openDialogUpdateDoc(String idDoc) {
		DialogTemplate dialogTemplate = new DialogTemplate("CHỈNH SỬA VĂN BẢN");
		DocEditForm editDocForm = new DocEditForm(idDoc,()->{
			detailsInfoDoc.removeAll();
			loadData();
			createLayoutInfoDoc(docModel);
			dialogTemplate.close();
			NotificationTemplate.success("Thành công");
		},userAuthenticationModel,belongOrganizationModel,signInOrgModel,null);
		dialogTemplate.add(editDocForm);

		dialogTemplate.getBtnSave().addClickListener(e->{
			editDocForm.saveADoc();
		});

		dialogTemplate.setHeightFull();
		dialogTemplate.setWidth("80%");
		dialogTemplate.setLayoutMobile();
		dialogTemplate.open();
	}


	private void openDialogChooseOrgToPerformTask(String idParent) {
		DialogTemplate dialogTemplate = new DialogTemplate("CHỌN ĐƠN VỊ THỰC HIỆN");

		OrganizationModel organizationModel = getInfoOwner(belongOrganizationModel.getOrganizationId());

		if(isUserAllPermission) {
			TaskChooseAllOrgFormv2 taskChooseAllOrgForm = new TaskChooseAllOrgFormv2(idParent,organizationModel,mapAllOrgAndUserAssinee,null,checkUpdateTask);
			dialogTemplate.add(taskChooseAllOrgForm);
			taskChooseAllOrgForm.addChangeListener(e->{
				mapOrgAndUserAssignee.clear();
				mapOrgAndUserAssignee.putAll(taskChooseAllOrgForm.getMapOrgIsChoose());
				loadCmbAssignee();

				mapOrgAndUserSupport.clear();
				loadCmbSupport();

				if(mapOrgAndUserAssignee.size() > 1) {
					multiSelectCmbSupport.setVisible(false);
					btnChooseSupport.setVisible(false);
					btnChooseSupport.setEnabled(false);
				}else {
					multiSelectCmbSupport.setVisible(true);
					btnChooseSupport.setVisible(true);
					btnChooseSupport.setEnabled(true);
				}

				if(mapOrgAndUserAssignee.isEmpty()) {
					btnChooseSupport.setEnabled(false);
				}
				dialogTemplate.close();
			});

			dialogTemplate.getBtnClose().addClickListener(e->{
				mapOrgAndUserAssignee.clear();
				mapOrgAndUserAssignee.putAll(taskChooseAllOrgForm.getMapOrgIsChoose());
				loadCmbAssignee();

				mapOrgAndUserSupport.clear();
				loadCmbSupport();

				if(mapOrgAndUserAssignee.size() > 1) {
					multiSelectCmbSupport.setVisible(false);
					btnChooseSupport.setVisible(false);
					btnChooseSupport.setEnabled(false);
				}else {
					multiSelectCmbSupport.setVisible(true);
					btnChooseSupport.setVisible(true);
					btnChooseSupport.setEnabled(true);
				}

				if(mapOrgAndUserAssignee.isEmpty()) {
					btnChooseSupport.setEnabled(false);
				}
				dialogTemplate.close();
			});

		}else {
			TaskChooseOrgForm taskChooseOrgForm = new TaskChooseOrgForm(idParent,organizationModel,mapAllOrgAndUserAssinee,null,checkUpdateTask);
			dialogTemplate.add(taskChooseOrgForm);

			taskChooseOrgForm.addChangeListener(e->{
				mapOrgAndUserAssignee.clear();
				mapOrgAndUserAssignee.putAll(taskChooseOrgForm.getMapOrgIsChoose());
				loadCmbAssignee();

				mapOrgAndUserSupport.clear();
				loadCmbSupport();

				if(mapOrgAndUserAssignee.size() > 1) {
					multiSelectCmbSupport.setVisible(false);
					btnChooseSupport.setVisible(false);
					btnChooseSupport.setEnabled(false);
				}else {
					multiSelectCmbSupport.setVisible(true);
					btnChooseSupport.setVisible(true);
					btnChooseSupport.setEnabled(true);
				}

				if(mapOrgAndUserAssignee.isEmpty()) {
					btnChooseSupport.setEnabled(false);
				}
				dialogTemplate.close();
			});
		}


		dialogTemplate.open();
		dialogTemplate.setSizeFull();
		dialogTemplate.setLayoutMobile();
		dialogTemplate.getFooter().removeAll();

	}

	private void openDialogChooseOrgToSupportTask(String idParent) {
		DialogTemplate dialogTemplate = new DialogTemplate("CHỌN ĐƠN VỊ PHỐI HỢP");
		OrganizationModel organizationModel = getInfoOwner(belongOrganizationModel.getOrganizationId());
		if(isUserAllPermission) {
			TaskChooseAllOrgFormv2 taskChooseAllOrgForm = new TaskChooseAllOrgFormv2(idParent,organizationModel,mapAllOrgAndUserAssinee,mapAllOrgAndUserSupport,checkUpdateTask);
			dialogTemplate.add(taskChooseAllOrgForm);

			taskChooseAllOrgForm.addChangeListener(e->{
				mapOrgAndUserSupport.clear();
				mapOrgAndUserSupport.putAll(taskChooseAllOrgForm.getMapOrgIsChoose());
				loadCmbSupport();
				dialogTemplate.close();
			});
		}else {
			TaskChooseOrgForm taskChooseOrgForm = new TaskChooseOrgForm(idParent,organizationModel,mapAllOrgAndUserAssinee,mapAllOrgAndUserSupport,checkUpdateTask);
			dialogTemplate.add(taskChooseOrgForm);

			taskChooseOrgForm.addChangeListener(e->{
				mapOrgAndUserSupport.clear();
				mapOrgAndUserSupport.putAll(taskChooseOrgForm.getMapOrgIsChoose());
				loadCmbSupport();
				dialogTemplate.close();
			});
		}

		dialogTemplate.open();
		dialogTemplate.setSizeFull();
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setLayoutMobile();

	}

	private OrganizationModel getInfoOwner(String idOrg) {
		ApiResultResponse<ApiOrganizationModel> getInfoOrg = ApiOrganizationService.getOneOrg(idOrg);
		if(getInfoOrg.isSuccess()) {
			return new OrganizationModel(getInfoOrg.getResult());
		}

		return null;
	}

	private void openDialogChooseGroupFollower(String idParent) {
		DialogTemplate dialogTemplate = new DialogTemplate("Chọn khối theo dõi");

		OrganizationModel organizationModel = getInfoOwner(belongOrganizationModel.getOrganizationId());

		TaskChooseFollowerByOrgForm taskChooseFollowerByOrgForm = new TaskChooseFollowerByOrgForm(idParent, organizationModel, mapAllOrgAndUserFollower, checkUpdateTask);
		taskChooseFollowerByOrgForm.addChangeListener(e->{
			listOrgFollowerIsChoose.clear();
			mapOrgAndUserFollower.clear();
			mapOrgAndUserFollower.putAll(taskChooseFollowerByOrgForm.getMapOrgIsChoose());


			loadCmbFollower();
			dialogTemplate.close();
		});

		dialogTemplate.add(taskChooseFollowerByOrgForm);

		dialogTemplate.setSizeFull();
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setLayoutMobile();
		dialogTemplate.open();
	}



	private Component createLayoutKeyValue(String header,String content) {
		HorizontalLayout hLayout = new HorizontalLayout();

		Span spanHeader = new Span(header);
		spanHeader.getStyle().setFontWeight(600);

		Span spanContent = new Span(content);

		hLayout.add(spanHeader,spanContent);

		if(isUiMobile) {
			hLayout.getStyle().setDisplay(Display.FLEX).setFlexDirection(FlexDirection.COLUMN).setBorder("1px solid #c9d8e1")
			.setPadding("5px").setBorderRadius("10px");
			hLayout.setWidthFull();
		}


		return hLayout;
	}

	private List<String> listAttachGetInDoc = new ArrayList<String>();

	public void saveTask() {
		if(invalid() == false) {
			return;
		}
		if(idDoc != null) {
			listAttachGetInDoc = new ArrayList<String>();
			for(String i : docModel.getAttachments()) {
				listAttachGetInDoc.add(i);
			}
			if(!upload.getListFileUpload().isEmpty()) {
				uploadFile();
			}

			listAttachGetInDoc.addAll(listIdAttachmentInUploadModule);
		}else {
			if(!upload.getListFileUpload().isEmpty()) {
				uploadFile();
			}
		}

		if(idDoc == null) {
			openConfirmAssignNoDoc();
		}else {
			for(Map.Entry<OrganizationModel, UserOranizationExModel> m : mapAllOrgAndUserAssinee.entrySet()) {
				ApiInputTaskModel apiInputTaskModel = new ApiInputTaskModel();
				apiInputTaskModel.setDocId(idDoc);
				apiInputTaskModel.setParentId(null);
				apiInputTaskModel.setCreateTime(LocalDateUtil.localDateTimeToLong(dateStartTime.getValue()));

				//Người chỉ đạo

				//				owner.setOrganizationId(belongOrganizationModel.getOrganizationId());
				//				owner.setOrganizationName(belongOrganizationModel.getOrganizationName());
				//				owner.setOrganizationUserId(cmbOwner.getValue().getKey());
				//				owner.setOrganizationUserName(cmbOwner.getValue().getValue());

				ApiOrgGeneralOfTaskModel owner = new ApiOrgGeneralOfTaskModel();
				mapUserOfOwner.forEach((key,value)->{
					owner.setOrganizationId(key.getId());
					owner.setOrganizationName(key.getName());
					owner.setOrganizationUserId(value.getUserId());
					owner.setOrganizationUserName(value.getFullName());
				});

				apiInputTaskModel.setOwner(owner);

				//Người giao nhiệm vụ
				ApiOrgGeneralOfTaskModel assistant = new ApiOrgGeneralOfTaskModel();
				//				assistant.setOrganizationId(belongOrganizationModel.getOrganizationId());
				//				assistant.setOrganizationName(belongOrganizationModel.getOrganizationName());
				//				for(Map.Entry<GroupOranizationExModel, ApiUserGroupExpandModel> mAssistant : mapAssistant.entrySet()) {
				//					assistant.setOrganizationGroupId(mAssistant.getKey().getGroupId());
				//					assistant.setOrganizationGroupName(mAssistant.getKey().getName());
				//					assistant.setOrganizationUserId(mAssistant.getValue().getUserId());
				//					assistant.setOrganizationUserName(mAssistant.getValue().getUserName());;
				//				}
				//
				//				if(mapAssistant.isEmpty()) {
				//					assistant.setOrganizationUserId(userAuthenticationModel.getId());
				//					assistant.setOrganizationUserName(userAuthenticationModel.getUsername());
				//					if(signInOrgModel.getGroup()!=null) {
				//						assistant.setOrganizationGroupId(signInOrgModel.getGroup().getId());
				//						assistant.setOrganizationGroupName(signInOrgModel.getName());
				//					}
				//				}
				//

				mapUserOfAssistant.forEach((org,user)->{
					assistant.setOrganizationId(org.getId());
					assistant.setOrganizationName(org.getName());
					assistant.setOrganizationUserId(user.getUserId());
					assistant.setOrganizationUserName(user.getFullName());
				});

				System.out.println("Check"+assistant);

				apiInputTaskModel.setAssistant(assistant);


				//Đơn vị xử lý
				ApiOrgGeneralOfTaskModel assigneeModel = new ApiOrgGeneralOfTaskModel();
				assigneeModel.setOrganizationId(m.getKey().getId());
				assigneeModel.setOrganizationName(m.getKey().getName());
				if(m.getValue() != null) {
					assigneeModel.setOrganizationUserId(m.getValue().getUserId());
					assigneeModel.setOrganizationUserName(m.getValue().getFullName());
				}
				apiInputTaskModel.setAssignee(assigneeModel);

				//Đơn vị hỗ trợ
				List<ApiOrgGeneralOfTaskModel> supports = new ArrayList<ApiOrgGeneralOfTaskModel>();

				for(Map.Entry<OrganizationModel, UserOranizationExModel> mSupport : mapAllOrgAndUserSupport.entrySet()) {
					ApiOrgGeneralOfTaskModel supportModel = new ApiOrgGeneralOfTaskModel();
					supportModel.setOrganizationId(mSupport.getKey().getId());
					supportModel.setOrganizationName(mSupport.getKey().getName());
					if(mSupport.getValue() != null) {
						supportModel.setOrganizationUserId(mSupport.getValue().getUserId());
						supportModel.setOrganizationUserName(mSupport.getValue().getFullName());
					}
					supports.add(supportModel);
				}


				apiInputTaskModel.setSupports(supports);

				//Khối theo dõi
				List<ApiFollowerModel> followers = new ArrayList<ApiFollowerModel>();

				for(Map.Entry<OrganizationModel, UserOranizationExModel> mapFollower : mapAllOrgAndUserFollower.entrySet()) {
					ApiFollowerModel apiFollowerModel = new ApiFollowerModel();
					apiFollowerModel.setOrganizationId(mapFollower.getKey().getId());
					apiFollowerModel.setOrganizationName(mapFollower.getKey().getName());
					if(mapFollower.getValue() != null) {
						apiFollowerModel.setOrganizationUserId(mapFollower.getValue().getUserId());
						apiFollowerModel.setOrganizationUserName(mapFollower.getValue().getFullName());
					}
					followers.add(apiFollowerModel);
				}
				apiInputTaskModel.setFollowers(followers);

				apiInputTaskModel.setPriority(cmbUrgency.getValue().getKey());
				apiInputTaskModel.setTitle(txtTitle.getValue());
				apiInputTaskModel.setDescription(txtDescr.getValue());
				if(dateEndTime2.getValue() == null) {
					apiInputTaskModel.setEndTime(0);
				}else {
					//					apiInputTaskModel.setEndTime(LocalDateUtil.localDateTimeToLong(dateEndTime.getValue()));
					LocalDate localDate = dateEndTime2.getValue();
					LocalTime localTime = timePicker.getValue();
					LocalDateTime endTime = localDate.atTime(localTime);
					apiInputTaskModel.setEndTime(LocalDateUtil.localDateTimeToLong(endTime));
				}

				apiInputTaskModel.setRequiredConfirm(cbRequiredConfirm.getValue());
				apiInputTaskModel.setRequiredKpi(cbRequiredKPI.getValue());
				apiInputTaskModel.setClassifyTaskId(userAuthenticationModel.getId());
				apiInputTaskModel.setLeaderApproveTaskId(userAuthenticationModel.getId());

				ApiOrgGeneralOfTaskModel creator = new ApiOrgGeneralOfTaskModel();
				creator.setOrganizationId(belongOrganizationModel.getOrganizationId());
				creator.setOrganizationName(belongOrganizationModel.getOrganizationName());
				creator.setOrganizationUserId(userAuthenticationModel.getId());
				creator.setOrganizationUserName(userAuthenticationModel.getFullName());
				apiInputTaskModel.setCreator(creator);


				if(idDoc != null) {

					apiInputTaskModel.setAttachments(listAttachGetInDoc);
				}else {
					apiInputTaskModel.setAttachments(listIdAttachmentInUploadModule);
				}

				if(checkAssignTaskAgain) {
					doCreateTask(apiInputTaskModel);
				}else {
					if(checkUpdateTask) {
						doUpdateTask(apiInputTaskModel);
					}else {
						doCreateTask(apiInputTaskModel);
					}
				}
			}
		}
	}


	int countSucces = 0;

	private void doCreateTask(ApiInputTaskModel apiInputTaskModel) {
		ApiResultResponse<ApiInputTaskModel> createTask = null;
		try {
			createTask = ApiTaskService.createTask(apiInputTaskModel);
			if(createTask.isSuccess()) {
				countSucces++;
			}else {
				NotificationTemplate.warning(createTask.getMessage());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		if(countSucces == mapAllOrgAndUserAssinee.size()) {
			listIdAttachmentInUploadModule = new ArrayList<String>();
			NotificationTemplate.success(createTask.getMessage());
			fireEvent(new ClickEvent(this,false));

			if(isTaskAssign) {
				countSucces = 0;
				refreshMainLayout();
				mapAllOrgAndUserAssinee.clear();
				mapAllOrgAndUserSupport.clear();
				mapAllOrgAndUserFollower.clear();
				docModel = null;
				docIsChoose = null;
				idDoc = null;
				loadDataForFormTask(null);
				createLayoutDocEmpty();
			}
		}
	}

	private void doUpdateTask(ApiInputTaskModel apiInputTaskModel) {
		try {
			ApiResultResponse<ApiInputTaskModel> updateTask = ApiTaskService.updateTask(idTask, apiInputTaskModel);
			if(updateTask.isSuccess()) {
				listIdAttachmentInUploadModule = new ArrayList<String>();
				NotificationTemplate.success("Cập nhật thành công");
				fireEvent(new ClickEvent(this,false));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void uploadFile() {
		List<UploadModuleDataModel> listFile = upload.getListFileUpload();
		for(UploadModuleDataModel uploadModuleDataModel : listFile) {
			File file = new File(uploadModuleDataModel.getFileName());
			System.out.println("How much do I have files: "+uploadModuleDataModel.getFileName());
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
			if(data.getStatus() == 200 || data.getStatus() == 201) {
				System.out.println(" What how much I have id list: "+data.getResult() );
				listIdAttachmentInUploadModule.add(data.getResult().getId());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	//When is assign task no doc
	private DocModel docIsChoose = null;
	@SuppressWarnings("deprecation")
	private void openConfirmAssignNoDoc() {
		ConfirmDialogTemplate confirmDialogTemplate = new ConfirmDialogTemplate(checkUpdateTask == false ? "XÁC NHẬN GIAO NHIỆM VỤ KHÔNG VĂN BẢN" 
				: "NHIỆM VỤ VẪN CHƯA CẬP NHẬT VĂN BẢN" );

		Span spText = new Span(checkUpdateTask == false ? "Nhiệm vụ không văn bản sẽ được liệt vào nhiệm vụ tự phát, bạn có thể cập nhật văn bản trong cập nhật nhiệm vụ, hoặc thêm văn bản tại đây."
				:"Nhiệm vụ này vẫn chưa thêm văn bản, bạn có thể cập nhật văn bản trong cập nhật nhiệm vụ, hoặc thêm văn bản tại đây.");
		VerticalLayout vLayoutDoc = new VerticalLayout();
		vLayoutDoc.setWidthFull();

		HorizontalLayout hLayoutDocIsChoose = new HorizontalLayout();

		ButtonTemplate btnRemoveDoc = new ButtonTemplate(FontAwesome.Solid.REMOVE.create());
		btnRemoveDoc.getStyle().setMarginTop("29px");
		btnRemoveDoc.setTooltipText("Bỏ văn bản đã chọn");
		btnRemoveDoc.addThemeVariants(ButtonVariant.LUMO_ERROR);

		TextField txtDesrDoc = new TextField("Văn bản đã chọn");
		txtDesrDoc.setReadOnly(true);
		txtDesrDoc.setWidthFull();

		hLayoutDocIsChoose.setWidthFull();
		hLayoutDocIsChoose.add(txtDesrDoc,btnRemoveDoc);
		hLayoutDocIsChoose.setVisible(false);

		ButtonTemplate buttonChooseDoc = new ButtonTemplate("Thêm văn bản nguồn",FontAwesome.Solid.PLUS.create());
		buttonChooseDoc.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		buttonChooseDoc.getStyle().setMarginLeft("auto");
		buttonChooseDoc.addClickListener(e->{
			openDialogAddDoc(true, docIsChoose,()->{
				buttonChooseDoc.setText("Thay đổi văn bản nguồn");
				hLayoutDocIsChoose.setVisible(true);
				txtDesrDoc.setValue(docIsChoose.getSummary());
				txtDesrDoc.setTooltipText(docIsChoose.getSummary());
			});
		});

		btnRemoveDoc.addClickListener(e->{
			docIsChoose = null;
			buttonChooseDoc.setText("Thêm văn bản nguồn");
			hLayoutDocIsChoose.setVisible(false);
		});

		vLayoutDoc.add(spText,buttonChooseDoc,hLayoutDocIsChoose);

		confirmDialogTemplate.add(vLayoutDoc);

		confirmDialogTemplate.addConfirmListener(e->{
			if(docIsChoose != null) {
				docIsChoose.getAttachments().stream().forEach(item->{
					listIdAttachmentInUploadModule.add(item);
				});
			}
			for(Map.Entry<OrganizationModel, UserOranizationExModel> m : mapAllOrgAndUserAssinee.entrySet()) {
				ApiInputTaskModel apiInputTaskModel = new ApiInputTaskModel();
				if(docIsChoose != null) {
					apiInputTaskModel.setDocId(docIsChoose.getId());
				}else {
					apiInputTaskModel.setDocId(null);
				}
				apiInputTaskModel.setParentId(null);


				ApiOrgGeneralOfTaskModel owner = new ApiOrgGeneralOfTaskModel();
				mapUserOfOwner.forEach((key,value)->{
					owner.setOrganizationId(key.getId());
					owner.setOrganizationName(key.getName());
					owner.setOrganizationUserId(value.getUserId());
					owner.setOrganizationUserName(value.getFullName());
				});

				apiInputTaskModel.setOwner(owner);


				//Người giao nhiệm vụ
				ApiOrgGeneralOfTaskModel assistant = new ApiOrgGeneralOfTaskModel();
				mapUserOfAssistant.forEach((org,user)->{
					assistant.setOrganizationId(org.getId());
					assistant.setOrganizationName(org.getName());
					assistant.setOrganizationUserId(user.getUserId());
					assistant.setOrganizationUserName(user.getFullName());
				});

				apiInputTaskModel.setAssistant(assistant);


				//Đơn vị xử lý
				ApiOrgGeneralOfTaskModel assigneeModel = new ApiOrgGeneralOfTaskModel();
				assigneeModel.setOrganizationId(m.getKey().getId());
				assigneeModel.setOrganizationName(m.getKey().getName());
				if(m.getValue() != null) {
					assigneeModel.setOrganizationUserId(m.getValue().getUserId());
					assigneeModel.setOrganizationUserName(m.getValue().getFullName());
				}
				apiInputTaskModel.setAssignee(assigneeModel);

				//Đơn vị hỗ trợ
				List<ApiOrgGeneralOfTaskModel> supports = new ArrayList<ApiOrgGeneralOfTaskModel>();

				for(Map.Entry<OrganizationModel, UserOranizationExModel> mSupport : mapAllOrgAndUserSupport.entrySet()) {
					ApiOrgGeneralOfTaskModel supportModel = new ApiOrgGeneralOfTaskModel();
					supportModel.setOrganizationId(mSupport.getKey().getId());
					supportModel.setOrganizationName(mSupport.getKey().getName());
					if(mSupport.getValue() != null) {
						supportModel.setOrganizationUserId(mSupport.getValue().getUserId());
						supportModel.setOrganizationUserName(mSupport.getValue().getFullName());
					}
					supports.add(supportModel);
				}


				apiInputTaskModel.setSupports(supports);

				//Khối theo dõi
				List<ApiFollowerModel> followers = new ArrayList<ApiFollowerModel>();

				for(Map.Entry<OrganizationModel, UserOranizationExModel> mapFollower : mapAllOrgAndUserFollower.entrySet()) {
					ApiFollowerModel apiFollowerModel = new ApiFollowerModel();
					apiFollowerModel.setOrganizationId(mapFollower.getKey().getId());
					apiFollowerModel.setOrganizationName(mapFollower.getKey().getName());
					if(mapFollower.getValue() != null) {
						apiFollowerModel.setOrganizationUserId(mapFollower.getValue().getUserId());
						apiFollowerModel.setOrganizationUserName(mapFollower.getValue().getFullName());
					}
					followers.add(apiFollowerModel);
				}
				apiInputTaskModel.setFollowers(followers);

				apiInputTaskModel.setPriority(cmbUrgency.getValue().getKey());
				apiInputTaskModel.setTitle(txtTitle.getValue());
				apiInputTaskModel.setDescription(txtDescr.getValue());
				if(dateEndTime2.getValue() == null) {
					apiInputTaskModel.setEndTime(0);
				}else {
					LocalDate localDate = dateEndTime2.getValue();
					LocalTime localTime = timePicker.getValue();
					LocalDateTime endTime = localDate.atTime(localTime);
					apiInputTaskModel.setEndTime(LocalDateUtil.localDateTimeToLong(endTime));
				}

				apiInputTaskModel.setRequiredConfirm(cbRequiredConfirm.getValue());
				apiInputTaskModel.setRequiredKpi(cbRequiredKPI.getValue());
				apiInputTaskModel.setClassifyTaskId(userAuthenticationModel.getId());
				apiInputTaskModel.setLeaderApproveTaskId(userAuthenticationModel.getId());

				ApiOrgGeneralOfTaskModel creator = new ApiOrgGeneralOfTaskModel();
				creator.setOrganizationId(belongOrganizationModel.getOrganizationId());
				creator.setOrganizationName(belongOrganizationModel.getOrganizationName());
				creator.setOrganizationUserId(userAuthenticationModel.getId());
				creator.setOrganizationUserName(userAuthenticationModel.getUsername());
				apiInputTaskModel.setCreator(creator);


				if(listIdAttachmentInUploadModule != null && !listIdAttachmentInUploadModule.isEmpty()) {
					apiInputTaskModel.setAttachments(listIdAttachmentInUploadModule);
				}
				if(checkAssignTaskAgain) {
					doCreateTask(apiInputTaskModel);
				}else {
					if(checkUpdateTask) {
						doUpdateTask(apiInputTaskModel);
					}else {
						doCreateTask(apiInputTaskModel);
					}
				}
			}
		});
		confirmDialogTemplate.open();
	}

	private void openDialogAddDoc(boolean checkDocRequired,DocModel docModel,Runnable onRun) {
		DialogTemplate dialogTemplate = new DialogTemplate("Thêm văn bản nguồn");
		DocListForm listDocForm = new DocListForm(userAuthenticationModel, belongOrganizationModel, signInOrgModel, checkDocRequired,docModel,null,null);
		listDocForm.addChangeListener(e->{
			docIsChoose = listDocForm.getListDocIsChoose().get(0);
		});
		dialogTemplate.add(listDocForm);
		dialogTemplate.getBtnSave().addClickListener(e->{
			onRun.run();
			dialogTemplate.close();
		});	
		dialogTemplate.open();
		dialogTemplate.setSizeFull();
		dialogTemplate.setLayoutMobile();

	}

	private boolean invalid() {

		if(multiSelectCmbAssignee.isEmpty()) {
			multiSelectCmbAssignee.setErrorMessage("Vui lòng chọn đơn vị xử lý");
			multiSelectCmbAssignee.setInvalid(true);
			return false;
		}

		if(PropsUtil.isFollower()) {
			if(multiSelectCmbFollowUp.isEmpty()) {
				multiSelectCmbFollowUp.setErrorMessage("Phải có ít nhất một khối theo dõi");
				multiSelectCmbFollowUp.setInvalid(true);
				return false;
			}
		}

		if(txtTitle.getValue().isEmpty()) {
			txtTitle.setErrorMessage("Vui lòng nhập đầy đủ tiêu đề");
			txtTitle.setInvalid(true);
			txtTitle.focus();
			return false;
		}

		if(txtDescr.getValue().isEmpty()) {
			txtDescr.setErrorMessage("Vui lòng nhập nội dung");
			txtDescr.setInvalid(true);
			txtTitle.focus();
			return false;
		}
		
		if(cbRequiredKPI.getValue() == true && dateEndTime2.getValue() == null) {
			dateEndTime2.setErrorMessage("Vui lòng nhập thời gian hoàn thành vì là nhiệm vụ được tính KPI");
			dateEndTime2.setInvalid(true);
			dateEndTime2.focus();
			return false;
		}

		return true;
	}

	public void setCheckAssignTaskAgain(boolean checkAssignTaskAgain) {
		this.checkAssignTaskAgain = checkAssignTaskAgain;
	}

	public Details getDetailFormTask() {
		return detailFormTask;
	}

	public void setDetailFormTask(Details detailFormTask) {
		this.detailFormTask = detailFormTask;
	}

	public boolean isTaskAssign() {
		return isTaskAssign;
	}

	public void setTaskAssign(boolean isTaskAssign) {
		this.isTaskAssign = isTaskAssign;
	}

}

