package com.ngn.tdnv.task.forms;

import java.io.File;
import java.time.LocalDateTime;
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
import com.ngn.api.organization.ApiUserGroupExpandModel;
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
import com.ngn.tdnv.doc.forms.create_task.TaskChooseGroupFollowForm;
import com.ngn.tdnv.doc.forms.create_task.TaskChooseOrgForm;
import com.ngn.tdnv.doc.forms.create_task.TaskChooseUserAssitantForm;
import com.ngn.tdnv.doc.forms.create_task.ViewDocFromFile;
import com.ngn.tdnv.doc.models.DocModel;
import com.ngn.tdnv.task.models.TaskOutputModel;
import com.ngn.utils.LocalDateUtil;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.DialogTemplate;
import com.ngn.utils.components.NotificationTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.ngn.utils.models.model_of_organization.GroupOranizationExModel;
import com.ngn.utils.models.model_of_organization.OrganizationModel;
import com.ngn.utils.models.model_of_organization.UserOranizationExModel;
import com.ngn.utils.uploads.UploadModuleBasic;
import com.ngn.utils.uploads.UploadModuleDataModel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBoxVariant;
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

public class TaskCreateChildForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private Logger logger = LogManager.getLogger(this);

	private List<String> listIdAttachment = new ArrayList<String>();

	private ButtonTemplate btnAssigned = new ButtonTemplate("Giao nhiệm vụ",FontAwesome.Solid.PAPER_PLANE.create());
	private Checkbox cbRequiredConfirm = new Checkbox("Xác nhận nhiệm vụ sau khi Cán bộ/ Đơn vị được giao báo cáo hoàn thành");

	private TextField txtTitle = new TextField("Tiêu đề nhiệm vụ *");
	private TextArea txtDescr = new TextArea("Nội dung nhiệm vụ *");
	private DateTimePicker dateStartTime = new DateTimePicker("Ngày bắt đầu thực hiện *");
	private DateTimePicker dateEndTime = new DateTimePicker("Hạn xử lý *");
	private ComboBox<Pair<String, String>> cmbUrgency = new ComboBox<Pair<String,String>>("Độ khẩn");
	private List<Pair<String, String>> listUrgency = new CommonsArrayList<Pair<String,String>>();

	private ComboBox<Pair<String, String>> cmbOwner = new ComboBox<Pair<String,String>>("Chọn người chỉ đạo (Người giao nhiệm vụ)");
	private ButtonTemplate btnConvertToAssineTaskInstead = new ButtonTemplate(FontAwesome.Solid.REPEAT.create());
	private ButtonTemplate btnConvertToUserAssineTask = new ButtonTemplate(FontAwesome.Solid.REPEAT.create());
	private TextField txtAssistant = new TextField("Cán bộ giao");
	private Map<GroupOranizationExModel, ApiUserGroupExpandModel> mapAssistant = new HashMap<GroupOranizationExModel, ApiUserGroupExpandModel>();
	private ButtonTemplate btnChooseAssistant = new ButtonTemplate(FontAwesome.Solid.HAND_POINTER.create());

	private ButtonTemplate btnChooseAssignee = new ButtonTemplate(FontAwesome.Solid.HAND_POINTER.create());
	private MultiSelectComboBox<Pair<String, String>> multiSelectCmbAssignee = new MultiSelectComboBox<Pair<String,String>>("Đơn vị xử lý (chủ trì) đã chọn");

	private ButtonTemplate btnChooseSupport = new ButtonTemplate(FontAwesome.Solid.HAND_POINTER.create());
	private MultiSelectComboBox<Pair<String,String>> multiSelectCmbSupport = new MultiSelectComboBox<Pair<String,String>>("Đơn vị phối hợp đã chọn");

	private ButtonTemplate btnChooseFollowUp = new ButtonTemplate(FontAwesome.Solid.HAND_POINTER.create());
	private MultiSelectComboBox<Pair<String, String>> multiSelectCmbFollowUp = new MultiSelectComboBox<Pair<String,String>>("Khối theo dõi đã chọn");

	private List<OrganizationModel> listOrgAssigneeIsChoose = new ArrayList<OrganizationModel>();
	private Map<OrganizationModel, UserOranizationExModel> mapOrgAndUserAssignee = new HashMap<OrganizationModel, UserOranizationExModel>();
	private Map<OrganizationModel, UserOranizationExModel> mapAllOrgAndUserAssinee = new HashMap<OrganizationModel, UserOranizationExModel>();

	private List<OrganizationModel> listOrgSupportIsChoose = new ArrayList<OrganizationModel>();
	private Map<OrganizationModel, UserOranizationExModel> mapOrgAndUserSupport = new HashMap<OrganizationModel, UserOranizationExModel>();
	private Map<OrganizationModel, UserOranizationExModel> mapAllOrgAndUserSupport = new HashMap<OrganizationModel, UserOranizationExModel>();

	private List<GroupOranizationExModel> listFollowerIsChoose = new ArrayList<GroupOranizationExModel>();
	private Map<GroupOranizationExModel, ApiUserGroupExpandModel> mapGroupAndUserFollower = new HashMap<GroupOranizationExModel, ApiUserGroupExpandModel>();
	private Map<GroupOranizationExModel, ApiUserGroupExpandModel> mapAllGroupAndUserFollower = new HashMap<GroupOranizationExModel, ApiUserGroupExpandModel>();


	private HorizontalLayout hLayoutGeneral = new HorizontalLayout();

	private VerticalLayout vLayoutLeft = new VerticalLayout();
	private VerticalLayout vLayoutRight = new VerticalLayout();
	private Details detailsInfoDoc = new Details();
	private Details detailFormTask = new Details();
	private UploadModuleBasic upload = new UploadModuleBasic();

	private DocModel docModel;
	private TaskOutputModel outputTaskModel;


	private String idDoc;
	private BelongOrganizationModel belongOrganizationModel;
	private UserAuthenticationModel userAuthenticationModel;
	private SignInOrgModel signInOrgModel;
	private String idTask;
	public TaskCreateChildForm(String idDoc,BelongOrganizationModel belongOrganizationModel,UserAuthenticationModel userAuthenticationModel,SignInOrgModel signInOrgModel,String idTask) {
		this.signInOrgModel = signInOrgModel;
		this.belongOrganizationModel = belongOrganizationModel;
		this.userAuthenticationModel = userAuthenticationModel;
		if(idDoc != null) {
			this.idDoc = idDoc;
			loadData();
		}


		buildLayout();
		configComponent();
		checkPermissionForTask();
		loadCmbAssignee();
		if(idTask!=null) {
			this.idTask = idTask;
			loadDataOfTask();
		}
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
		vLayoutLeft.setHeightFull();


		vLayoutRight.setHeightFull();
		vLayoutRight.add(detailsInfoDoc,detailFormTask);

		if(idDoc == null) {
			vLayoutRight.remove(detailsInfoDoc);
		}
		loadDataForFormTask(docModel);

		hLayoutGeneral.setPadding(false);
		hLayoutGeneral.add(vLayoutRight);
		hLayoutGeneral.setSizeFull();



		createLayoutFormTask();
		this.add(hLayoutGeneral);
	}

	@Override
	public void configComponent() {

		btnChooseAssignee.addClickListener(e->{
			openDialogChooseOrgToPerformTask(belongOrganizationModel.getOrganizationId());
		});

		btnChooseSupport.addClickListener(e->{
			openDialogChooseOrgToSupportTask(belongOrganizationModel.getOrganizationId());
		});

		btnChooseFollowUp.addClickListener(e->{
			openDialogChooseGroupFollower(belongOrganizationModel.getOrganizationId());
		});

		btnAssigned.addClickListener(e->{
			saveTask();
		});

		btnChooseAssistant.addClickListener(e->{
			openDialogChooseAssitant(belongOrganizationModel.getOrganizationId());
		});

	}

	private void checkPermissionForTask() {
	}

	public void loadData() {
		ApiResultResponse<ApiDocModel> data = ApiDocService.getAdoc(idDoc);
		if(data.isSuccess()) {
			docModel = new DocModel(data.getResult());

			if(!docModel.getAttachments().isEmpty()) {
				createLayoutForPDFView(docModel.getAttachments());
			}else {
				vLayoutRight.setSizeFull();
			}

			loadDataForFormTask(docModel);

//			if(docModel.getLeaderApproveTaskId() == null || docModel.getClassifyTaskId() == null) {
//				detailFormTask.setOpened(false);
//				detailFormTask.setEnabled(false);
//			}else {
//				detailFormTask.setEnabled(true);
//			}

		}else {
			logger.error(data);
		}

	}

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
			dateEndTime.setValue(LocalDateUtil.longToLocalDateTime(outputTaskModel.getEndTime()));
		}

		listUrgency.stream().forEach(model->{
			if(model.getKey().equals(outputTaskModel.getPriority())) {
				cmbUrgency.setValue(model);
			}
		});

		cmbOwner.getListDataView().getItems().forEach(model->{
			if(model.getKey().equals(outputTaskModel.getAssistant().getOrganizationUserId())) {
				cmbOwner.setValue(model);
			}
		});

		cbRequiredConfirm.setValue(outputTaskModel.isRequiredConfirm());
	}

	private void createLayoutForPDFView(List<String> listFile) {
		vLayoutLeft.removeAll();
		System.out.println("List FIle"+listFile);
		ViewDocFromFile viewDocFromFile = new ViewDocFromFile(listFile);
		viewDocFromFile.setSizeFull();
		viewDocFromFile.setPadding(false);
		vLayoutLeft.removeAll();
		vLayoutLeft.add(viewDocFromFile);
	}

	private void createLayoutInfoDoc(DocModel docModel) {
		VerticalLayout contentInFoDoc = new VerticalLayout();

		HorizontalLayout hLayout1 = new HorizontalLayout();
		hLayout1.setWidthFull();
		hLayout1.add(createLayoutKeyValue("Ký hiệu: ", docModel.getSymbol()),createLayoutKeyValue("Số hiệu: ", docModel.getNumber()),
				createLayoutKeyValue("Ngày ký: ", LocalDateUtil.dfDate.format(docModel.getRegDate())),createLayoutKeyValue("Người soạn thảo: ", docModel.getCreatorName()));


		HorizontalLayout hLayout2 = new HorizontalLayout();


		String classifyName = "";
		String leaderName = "";

		FilterClassifyLeaderModel filterClassifyLeaderModel = new FilterClassifyLeaderModel();
		filterClassifyLeaderModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
		filterClassifyLeaderModel.setLimit(100000);
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
		fiLeaderModel.setLimit(10000);
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

		ButtonTemplate btnUpdate = new ButtonTemplate("Cập nhật", FontAwesome.Solid.EDIT.create());
		btnUpdate.addThemeVariants(ButtonVariant.LUMO_TERTIARY,ButtonVariant.LUMO_SUCCESS);
		btnUpdate.addClickListener(e->{
			openDialogUpdateDoc(idDoc);
		});

		contentInFoDoc.add(hLayout1,createLayoutKeyValue("Trích yếu: ", docModel.getSummary()),hLayout2,btnUpdate);

		detailsInfoDoc.setSummaryText("Thông tin văn bản");
		detailsInfoDoc.add(contentInFoDoc);
		detailsInfoDoc.setOpened(true);
		detailsInfoDoc.addThemeVariants(DetailsVariant.REVERSE,DetailsVariant.FILLED);
		detailsInfoDoc.setWidthFull();

	}

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

		dateStartTime.setWidth("48%");;
		dateEndTime.setWidth("48%");
		dateEndTime.setHelperText("Để trống thì nhiệm vụ sẽ là không hạn xử lý");

		hLayout2.add(dateStartTime,icon,dateEndTime);
		hLayout2.setWidthFull();


		cmbOwner.setWidthFull();
		cmbOwner.addThemeVariants(ComboBoxVariant.LUMO_SMALL);

		txtAssistant.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		txtAssistant.setReadOnly(true);
		//		txtAssistant.setVisible(false);
		txtAssistant.setWidth("500px");

		//		btnConvertToAssineTaskInstead.setVisible(false);
		btnConvertToAssineTaskInstead.getStyle().setMarginTop("25px");
		btnConvertToAssineTaskInstead.addThemeVariants(ButtonVariant.LUMO_SMALL);
		btnConvertToAssineTaskInstead.setTooltipText("Giao thay nhiệm vụ");

		btnConvertToUserAssineTask.setVisible(false);
		btnConvertToUserAssineTask.getStyle().setMarginTop("25px");
		btnConvertToUserAssineTask.addThemeVariants(ButtonVariant.LUMO_SMALL);
		btnConvertToUserAssineTask.setTooltipText("Giao nhiệm vụ");

		btnChooseAssistant.getStyle().setMarginTop("25px");
		btnChooseAssistant.addThemeVariants(ButtonVariant.LUMO_SMALL);
		btnChooseAssistant.setTooltipText("Chọn người giao");

		HorizontalLayout hLayoutAssistant = new HorizontalLayout();
		hLayoutAssistant.setWidthFull();
		hLayoutAssistant.add(cmbOwner,txtAssistant,btnChooseAssistant);


		VerticalLayout vLayout3 = new VerticalLayout();

		HorizontalLayout hLayoutAssignee = new HorizontalLayout();

		btnChooseAssignee.addThemeVariants(ButtonVariant.LUMO_SMALL,ButtonVariant.LUMO_SUCCESS);
		btnChooseAssignee.getStyle().setMarginTop("29px");

		multiSelectCmbAssignee.setWidthFull();
		multiSelectCmbAssignee.addThemeVariants(MultiSelectComboBoxVariant.LUMO_SMALL);
		multiSelectCmbAssignee.setReadOnly(true);

		hLayoutAssignee.setWidthFull();
		hLayoutAssignee.add(multiSelectCmbAssignee,btnChooseAssignee);

		HorizontalLayout hLayoutSupport = new HorizontalLayout();

		btnChooseSupport.addThemeVariants(ButtonVariant.LUMO_SMALL);
		btnChooseSupport.getStyle().setMarginTop("29px");

		multiSelectCmbSupport.addThemeVariants(MultiSelectComboBoxVariant.LUMO_SMALL);
		multiSelectCmbSupport.setWidthFull();
		multiSelectCmbSupport.setReadOnly(true);

		hLayoutSupport.setWidthFull();
		hLayoutSupport.add(multiSelectCmbSupport,btnChooseSupport);


		HorizontalLayout hLayoutFollowUp = new HorizontalLayout();

		btnChooseFollowUp.addThemeVariants(ButtonVariant.LUMO_SMALL);
		btnChooseFollowUp.getStyle().setMarginTop("29px");

		multiSelectCmbFollowUp.setWidthFull();
		multiSelectCmbFollowUp.addThemeVariants(MultiSelectComboBoxVariant.LUMO_SMALL);
		multiSelectCmbFollowUp.setReadOnly(true);

		hLayoutFollowUp.setWidthFull();
		hLayoutFollowUp.add(multiSelectCmbFollowUp,btnChooseFollowUp);

		vLayout3.add(hLayoutAssignee,hLayoutSupport,hLayoutFollowUp);

		vLayout3.setWidthFull();


		btnAssigned.addThemeVariants(ButtonVariant.LUMO_SUCCESS,ButtonVariant.LUMO_PRIMARY);
		btnAssigned.getStyle().setMarginLeft("auto");

		cbRequiredConfirm.setValue(true);

		upload = new UploadModuleBasic();

		content.add(hLayout1,txtDescr,hLayout2,hLayoutAssistant,vLayout3,cbRequiredConfirm,upload,btnAssigned);

		if(idDoc != null) {
			content.remove(upload);		
		}else {
			upload.initUpload();
		}

		detailFormTask.removeAll();
		detailFormTask.setOpened(true);
		detailFormTask.addThemeVariants(DetailsVariant.REVERSE,DetailsVariant.FILLED);
		detailFormTask.setWidthFull();
		detailFormTask.setSummaryText("Thông tin giao nhiệm vụ");
		detailFormTask.add(content);
		detailFormTask.getStyle().setMarginBottom("10px");
	}

	private void loadDataForFormTask(DocModel docModel) {

		if(docModel != null) {
			txtTitle.setValue(docModel.getSummary());
			txtDescr.setValue(docModel.getSummary());
		}else {
			txtTitle.clear();
			txtDescr.clear();
		}
		dateStartTime.setValue(LocalDateTime.now().plusMinutes(1));
		dateStartTime.setMin(LocalDateTime.now());
		dateStartTime.setReadOnly(true);

		dateEndTime.addValueChangeListener(e->{
			dateStartTime.setValue(LocalDateTime.now().plusMinutes(1));
			dateStartTime.setMax(dateEndTime.getValue());
		});
		loadUrgency();
		loadCmbUserOwner(false);
		loadUserAssistant();


		if(idDoc == null) {
			listOrgAssigneeIsChoose.clear();
			mapOrgAndUserAssignee.clear();	
			loadCmbAssignee();

			listOrgSupportIsChoose.clear();
			mapOrgAndUserSupport.clear();
			loadCmbSupport();

			listFollowerIsChoose.clear();
			mapGroupAndUserFollower.clear();
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

	//Load Data For Owner
	private void loadCmbUserOwner(boolean checkAssignTaskInstead) {
		List<Pair<String, String>> listData = new ArrayList<Pair<String,String>>();
		try {
			ApiResultResponse<List<ApiUserGroupExpandModel>> data = ApiOrganizationService.getListUserForOwnerTask(belongOrganizationModel.getOrganizationId());
			data.getResult().stream().forEach(model->{
				listData.add(Pair.of(model.getUserId(),model.getMoreInfo().getFullName()));
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
		cmbOwner.setItems(listData);
		cmbOwner.setItemLabelGenerator(Pair::getRight);
		cmbOwner.setValue(listData.get(0));
	}

	//If the user does not choose Assistant
	private void loadUserAssistant() {
		GroupOranizationExModel groupOranizationExModel = new GroupOranizationExModel();
		if(signInOrgModel.getGroup().getId() == null) {
			groupOranizationExModel.setGroupId(belongOrganizationModel.getOrganizationId());
			groupOranizationExModel.setName("Ngoài tổ");
		}else {
			groupOranizationExModel.setGroupId(signInOrgModel.getGroup().getId());
			groupOranizationExModel.setName(signInOrgModel.getGroup().getName());
		}

		ApiUserGroupExpandModel apiUserGroupExpandModel = new ApiUserGroupExpandModel();
		apiUserGroupExpandModel.setUserId(userAuthenticationModel.getId());
		apiUserGroupExpandModel.setUserName(userAuthenticationModel.getFullName());

		txtAssistant.setValue(groupOranizationExModel.getName()+"("+userAuthenticationModel.getFullName()+")");

		mapAssistant.put(groupOranizationExModel, apiUserGroupExpandModel);
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
			String userName = m.getValue() == null ? "" : " ("+m.getValue().getUserName()+")";
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

	}

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
			String userName = m.getValue() == null ? "" : " ("+m.getValue().getUserName()+")"; 
			listDataSupport.add(Pair.of(m.getKey().getId(),m.getKey().getName()+userName));
			mapAllOrgAndUserSupport.put(m.getKey(), m.getValue());
		}

		multiSelectCmbSupport.setItems(listDataSupport);
		multiSelectCmbSupport.setItemLabelGenerator(Pair::getRight);
		multiSelectCmbSupport.select(listDataSupport);

	}

	private void loadCmbFollower() {
		mapAllGroupAndUserFollower = new HashMap<GroupOranizationExModel, ApiUserGroupExpandModel>();
		List<Pair<String, String>> listDataFollower = new ArrayList<Pair<String,String>>();
		if(!listFollowerIsChoose.isEmpty()) {
			listFollowerIsChoose.stream().forEach(model->{
				listDataFollower.add(Pair.of(model.getGroupId(),model.getName()));
				mapAllGroupAndUserFollower.put(model, null);
			});
		}

		for(Map.Entry<GroupOranizationExModel, ApiUserGroupExpandModel> m : mapGroupAndUserFollower.entrySet()) {
			String nameUser = m.getValue() == null ? "" : " (" + m.getValue().getUserName()+")";
			listDataFollower.add(Pair.of(m.getKey().getGroupId(),m.getKey().getName() + nameUser));
			mapAllGroupAndUserFollower.put(m.getKey(), m.getValue());
		}

		multiSelectCmbFollowUp.setItems(listDataFollower);
		multiSelectCmbFollowUp.setItemLabelGenerator(Pair::getRight);
		multiSelectCmbFollowUp.select(listDataFollower);
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
		dialogTemplate.open();
	}


	private void openDialogChooseOrgToPerformTask(String idParent) {
		DialogTemplate dialogTemplate = new DialogTemplate("CHỌN ĐƠN VỊ THỰC HIỆN");

		OrganizationModel organizationModel = getInfoOwner(belongOrganizationModel.getOrganizationId());
		TaskChooseOrgForm taskChooseOrgForm = new TaskChooseOrgForm(idParent,organizationModel,mapAllOrgAndUserAssinee,null,false);
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

		dialogTemplate.open();
		dialogTemplate.setSizeFull();
		dialogTemplate.getFooter().removeAll();

	}

	private void openDialogChooseOrgToSupportTask(String idParent) {
		DialogTemplate dialogTemplate = new DialogTemplate("CHỌN ĐƠN VỊ HỖ TRỢ");

		OrganizationModel organizationModel = getInfoOwner(belongOrganizationModel.getOrganizationId());
		TaskChooseOrgForm taskChooseOrgForm = new TaskChooseOrgForm(idParent,organizationModel,mapAllOrgAndUserAssinee,mapAllOrgAndUserSupport,false);
		dialogTemplate.add(taskChooseOrgForm);

		taskChooseOrgForm.addChangeListener(e->{
			mapOrgAndUserSupport.clear();
			mapOrgAndUserSupport.putAll(taskChooseOrgForm.getMapOrgIsChoose());
			loadCmbSupport();
			dialogTemplate.close();
		});

		dialogTemplate.open();
		dialogTemplate.setSizeFull();
		dialogTemplate.getFooter().removeAll();

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

		TaskChooseGroupFollowForm taskChooseGroupFollowForm = new TaskChooseGroupFollowForm(idParent,mapAllGroupAndUserFollower);
		dialogTemplate.add(taskChooseGroupFollowForm);
		taskChooseGroupFollowForm.addChangeListener(e->{
			mapGroupAndUserFollower.clear();
			mapGroupAndUserFollower.putAll(taskChooseGroupFollowForm.getMapGroupIsChoose());
			loadCmbFollower();
			dialogTemplate.close();
		});

		dialogTemplate.setSizeFull();
		dialogTemplate.getFooter().removeAll();

		dialogTemplate.open();
	}

	private void openDialogChooseAssitant(String idParent) {
		DialogTemplate dialogTemplate = new DialogTemplate("Chọn người giao nhiệm vụ");

		TaskChooseUserAssitantForm taskChooseUserAssitantForm = new TaskChooseUserAssitantForm(idParent,mapAssistant);

		dialogTemplate.getBtnSave().addClickListener(e->{
			taskChooseUserAssitantForm.getMapGroupAndUserIsChoose().forEach((k,v)->{
				txtAssistant.setValue(k.getName()+" ("+v.getMoreInfo().getFullName()+")");
				mapAssistant.clear();
				mapAssistant.put(k,v);
			});
			dialogTemplate.close();
		});

		taskChooseUserAssitantForm.addClickListener(e->{

		});

		dialogTemplate.add(taskChooseUserAssitantForm);
		dialogTemplate.setHeight("100%");
		dialogTemplate.setWidth("80%");
		dialogTemplate.open();
	}


	private Component createLayoutKeyValue(String header,String content) {
		HorizontalLayout hLayout = new HorizontalLayout();

		Span spanHeader = new Span(header);
		spanHeader.getStyle().setFontWeight(600);

		Span spanContent = new Span(content);

		hLayout.add(spanHeader,spanContent);


		return hLayout;
	}

	public void saveTask() {
		if(invalid() == false) {
			return;
		}

		for(Map.Entry<OrganizationModel, UserOranizationExModel> m : mapAllOrgAndUserAssinee.entrySet()) {
			ApiInputTaskModel apiInputTaskModel = new ApiInputTaskModel();
			apiInputTaskModel.setDocId(idDoc);
			apiInputTaskModel.setParentId(null);


			//Người chỉ đạo
			ApiOrgGeneralOfTaskModel owner = new ApiOrgGeneralOfTaskModel();
			owner.setOrganizationId(belongOrganizationModel.getOrganizationId());
			owner.setOrganizationName(belongOrganizationModel.getOrganizationName());
			owner.setOrganizationUserId(cmbOwner.getValue().getKey());
			owner.setOrganizationUserName(cmbOwner.getValue().getValue());

			apiInputTaskModel.setOwner(owner);

			//Người giao nhiệm vụ
			ApiOrgGeneralOfTaskModel assistant = new ApiOrgGeneralOfTaskModel();
			assistant.setOrganizationId(belongOrganizationModel.getOrganizationId());
			assistant.setOrganizationName(belongOrganizationModel.getOrganizationName());
			for(Map.Entry<GroupOranizationExModel, ApiUserGroupExpandModel> mAssistant : mapAssistant.entrySet()) {
				assistant.setOrganizationGroupId(mAssistant.getKey().getGroupId());
				assistant.setOrganizationGroupName(mAssistant.getKey().getName());
				assistant.setOrganizationUserId(mAssistant.getValue().getUserId());
				assistant.setOrganizationUserName(mAssistant.getValue().getUserName());;
			}

			if(mapAssistant.isEmpty()) {
				assistant.setOrganizationUserId(userAuthenticationModel.getId());
				assistant.setOrganizationUserName(userAuthenticationModel.getUsername());
				if(signInOrgModel.getGroup()!=null) {
					assistant.setOrganizationGroupId(signInOrgModel.getGroup().getId());
					assistant.setOrganizationGroupName(signInOrgModel.getName());
				}
			}

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

			for(Map.Entry<GroupOranizationExModel, ApiUserGroupExpandModel> mapFollower : mapAllGroupAndUserFollower.entrySet()) {
				ApiFollowerModel apiFollowerModel = new ApiFollowerModel();
				apiFollowerModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
				apiFollowerModel.setOrganizationName(belongOrganizationModel.getOrganizationName());
				apiFollowerModel.setOrganizationGroupId(mapFollower.getKey().getGroupId());
				apiFollowerModel.setOrganizationGroupName(mapFollower.getKey().getName());
				if(mapFollower.getValue()!=null) {
					apiFollowerModel.setOrganizationUserId(mapFollower.getValue().getUserId());
					apiFollowerModel.setOrganizationUserName(mapFollower.getValue().getUserName());
				}
				followers.add(apiFollowerModel);
			}

			apiInputTaskModel.setFollowers(followers);

			apiInputTaskModel.setPriority(cmbUrgency.getValue().getKey());
			apiInputTaskModel.setTitle(txtTitle.getValue());
			apiInputTaskModel.setDescription(txtDescr.getValue());
			if(dateEndTime.getValue() == null) {
				apiInputTaskModel.setEndTime(0);
			}else {
				apiInputTaskModel.setEndTime(LocalDateUtil.localDateTimeToLong(dateEndTime.getValue()));
			}

			apiInputTaskModel.setRequiredConfirm(cbRequiredConfirm.getValue());
			apiInputTaskModel.setClassifyTaskId(userAuthenticationModel.getId());
			apiInputTaskModel.setLeaderApproveTaskId(userAuthenticationModel.getId());

			ApiOrgGeneralOfTaskModel creator = new ApiOrgGeneralOfTaskModel();
			creator.setOrganizationId(belongOrganizationModel.getOrganizationId());
			creator.setOrganizationName(belongOrganizationModel.getOrganizationName());
			creator.setOrganizationUserId(userAuthenticationModel.getId());
			creator.setOrganizationUserName(userAuthenticationModel.getUsername());
			apiInputTaskModel.setCreator(creator);

			if(idDoc != null) {
				List<String> listAttach = new ArrayList<String>();
				for(String i : docModel.getAttachments()) {
					listAttach.add(i);
				}

				apiInputTaskModel.setAttachments(listAttach);
			}else {
				if(!upload.getListFileUpload().isEmpty()) {
					uploadFile();
					apiInputTaskModel.setAttachments(listIdAttachment);
				}
			}

			doCreateTask(apiInputTaskModel);
		}
	}

	int countSucces = 0;

	private void doCreateTask(ApiInputTaskModel apiInputTaskModel) {
		ApiResultResponse<Object> createTask = null;
		try {
			createTask = ApiTaskService.createChildTask(idTask,apiInputTaskModel);
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
			listIdAttachment = new ArrayList<String>();
			NotificationTemplate.success(createTask.getMessage());
			fireEvent(new ClickEvent(this,false));

			if(idDoc == null) {
				countSucces = 0;
				refreshMainLayout();
				loadDataForFormTask(null);
				createLayoutFormTask();
			}
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
				System.out.println("How much do I have id list: "+data.getResult() );
				listIdAttachment.add(data.getResult().getId());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private boolean invalid() {

		if(multiSelectCmbAssignee.isEmpty()) {
			multiSelectCmbAssignee.setErrorMessage("Vui lòng chọn đơn vị xử lý");
			multiSelectCmbAssignee.setInvalid(true);
			return false;
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

		return true;
	}

}