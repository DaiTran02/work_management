package com.ngn.tdnv.doc.forms;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.FileSystemResource;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.classify_task.ApiClassifyTaskModel;
import com.ngn.api.classify_task.ApiClassifyTaskService;
import com.ngn.api.doc.ApiDocInputModel;
import com.ngn.api.doc.ApiDocModel;
import com.ngn.api.doc.ApiDocOrgModel;
import com.ngn.api.doc.ApiDocService;
import com.ngn.api.leader_approve_task.ApiLeaderApproveTaskModel;
import com.ngn.api.leader_approve_task.ApiLeaderApproveTaskService;
import com.ngn.api.media.ApiInputMediaModel;
import com.ngn.api.media.ApiMediaModel;
import com.ngn.api.media.ApiMediaService;
import com.ngn.api.organization.ApiMoreinfoModel;
import com.ngn.api.organization.ApiOrganizationModel;
import com.ngn.api.organization.ApiOrganizationService;
import com.ngn.api.organization.ApiUserGroupExpandModel;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.utils.ApiKeyValueModel;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.models.sign_in_org.SignInOrgModel;
import com.ngn.setting.leader_classify.form.ClassifyTaskForm;
import com.ngn.setting.leader_classify.form.LeaderApproveTaskForm;
import com.ngn.setting.leader_classify.model.FilterClassifyLeaderModel;
import com.ngn.tdnv.doc.forms.create_task.TaskChooseUserAssitantForm;
import com.ngn.tdnv.doc.forms.create_task.level_pass.TaskChooseAllOrgForReceiversForm;
import com.ngn.tdnv.doc.models.DocInputModel;
import com.ngn.tdnv.doc.models.DocInputModel.Owner;
import com.ngn.tdnv.doc.models.DocModel;
import com.ngn.utils.LocalDateUtil;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.ActtachmentForm;
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
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

public class DocEditForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private Logger logger = LogManager.getLogger(DocEditForm.class);
	private boolean checkIdDoc = false;

	private UploadModuleBasic upload=new UploadModuleBasic();
	private List<String>listFileDelete = new ArrayList<String>();

	private List<String> listIdAttachment = new ArrayList<String>();
	private List<String> listIdAccachmentForUpdate = new ArrayList<String>();

	private ComboBox<Pair<String, String>>cmbDocCategory = new ComboBox<Pair<String,String>>("Loại văn bản");
	private List<Pair<String, String>> listDocCategory = new ArrayList<Pair<String,String>>();

	private ComboBox<Pair<String,String>>cmbDocSecurity = new ComboBox<>("Độ mật");
	private List<Pair<String, String>> listItemSecurity = new ArrayList<Pair<String,String>>();

	private TextField txtDocNumber = new TextField("Số hiệu");
	private TextField txtDocSymbol = new TextField("Ký hiệu");
	private TextField txtDocType = new TextField("Thể loại");
	private DatePicker dateIsued = new DatePicker("Ngày ban hành");
	private TextField txtUserSigner = new TextField("Người ký");
	private TextArea txtDocSummary = new TextArea("Trích yếu");
	
	private MultiSelectComboBox<Pair<OrganizationModel, UserOranizationExModel>> multiComboboxReceivers = new MultiSelectComboBox<Pair<OrganizationModel,UserOranizationExModel>>("Nơi nhận");
	private ButtonTemplate btnChooseReceivers = new ButtonTemplate("Chọn nơi nhận",FontAwesome.Solid.HAND_POINTER.create());
	private Map<OrganizationModel, UserOranizationExModel> mapOrgAndUserForReceivers = new HashMap<OrganizationModel, UserOranizationExModel>();
	
	private TextField txtSignerPosition = new TextField("Chức vụ");
	private Checkbox cbActive = new Checkbox("Hoạt động");


	private ButtonTemplate btnChooseFollowUp = new ButtonTemplate(FontAwesome.Solid.HAND_POINTER.create());
	private TextField txtUserGroup = new TextField("Cán bộ soạn văn bản");
	private Map<GroupOranizationExModel, ApiUserGroupExpandModel> mapUserAndGroup = new HashMap<GroupOranizationExModel, ApiUserGroupExpandModel>();
	private ButtonTemplate btnResetChooseUser = new ButtonTemplate(FontAwesome.Solid.REFRESH.create());


	private ComboBox<Pair<String, String>> cmbClassifyTaskId = new ComboBox<Pair<String,String>>("Phân loại chỉ đạo");
	private List<Pair<String, String>> listDataCmbClassify = new ArrayList<Pair<String,String>>();
	private ButtonTemplate btnAddClassifyTaskId = new ButtonTemplate(FontAwesome.Solid.PLUS.create());

	private ComboBox<Pair<String, String>> cmbLeaderApproveTaskId = new ComboBox<Pair<String,String>>("Người duyệt");
	private List<Pair<String, String>> listDataCmbLeader = new ArrayList<Pair<String,String>>();
	private ButtonTemplate btnAddLeader = new ButtonTemplate(FontAwesome.Solid.PLUS.create());

	private UserAuthenticationModel userAuthenticationModel;
	private BelongOrganizationModel belongOrganizationModel;
	private SignInOrgModel signInOrgModel;

	private String idDoc;
	private Runnable onRun;
	private String docCategory;
	public DocEditForm(String idDoc,Runnable onRun ,UserAuthenticationModel userAuthenticationModel,
			BelongOrganizationModel belongOrganizationModel,SignInOrgModel signInOrgModel,String docCategory) {
		this.userAuthenticationModel = userAuthenticationModel;
		this.belongOrganizationModel = belongOrganizationModel;
		this.signInOrgModel = signInOrgModel;
		this.onRun = onRun;
		this.docCategory = docCategory;
		if(idDoc != null) {
			this.idDoc = idDoc;
			checkIdDoc = true;
		}
		buildLayout();
		configComponent();
		initCmbDocCategory();
		initCmbDocSecurity();
		initDateIsued();
		loadCmbClassify();
		loadCmbLeader();
		loadNewDoc();
		if(checkIdDoc) {
			loadData();
		}
		System.out.println("ID Doc ne: "+idDoc);
	}

	@Override
	public void buildLayout() {
		this.setWidthFull();
		this.add(createLayout(),upload);
		initUpload();

	}

	@Override
	public void configComponent() {
		btnAddClassifyTaskId.addClickListener(e->{
			openDialogClassifyTaskId();
		});

		btnAddLeader.addClickListener(e->{
			openDialogLeader();
		});

		btnChooseReceivers.addClickListener(e->{
			openDialogChooseReceived();
		});

		btnChooseFollowUp.addClickListener(e->{
			openDialogChooseOrgForFollower(belongOrganizationModel.getOrganizationId());
		});

		btnResetChooseUser.addClickListener(e->{
			setDefaultAssistant();
			txtUserGroup.setValue(userAuthenticationModel.getFullName()+"(Mặc định là người đang soạn)");
		});
		
	}

	private void loadData() {
		try {
			ApiResultResponse<ApiDocModel> dataDoc = ApiDocService.getAdoc(idDoc);
			DocModel docModel = new DocModel(dataDoc.getResult());
			txtDocNumber.setValue(docModel.getNumber());
			txtDocSummary.setValue(docModel.getSummary());
			txtDocSymbol.setValue(docModel.getSymbol());
			txtDocType.setValue(docModel.getType());
			txtUserSigner.setValue(docModel.getSignerName());
			dateIsued.setValue(LocalDateUtil.longToLocalDate(docModel.getRegDate()));
			txtSignerPosition.setValue(docModel.getSignerPosition());
			if(docModel.getReceivers() != null && !docModel.getReceivers().isEmpty()) {
				docModel.getReceivers().forEach(model->{
					OrganizationModel organizationModel = new OrganizationModel();
					organizationModel.setId(model.getOrganizationId());
					organizationModel.setName(model.getOrganizationName());
					UserOranizationExModel userOranizationExModel = null;
					if(model.getOrganizationUserId() != null) {
						userOranizationExModel = new UserOranizationExModel();
						userOranizationExModel.setUserId(model.getOrganizationUserId().toString());
						userOranizationExModel.setUserName(model.getOrganizationUserName().toString());
						userOranizationExModel.setFullName(model.getOrganizationUserName().toString());
					}
					mapOrgAndUserForReceivers.put(organizationModel, userOranizationExModel);
				});
				initMultiSelectCmb();
			}
			
			if(docModel.getOwner() != null) {
				GroupOranizationExModel groupOranizationExModel = new GroupOranizationExModel();
				groupOranizationExModel.setGroupId(docModel.getOwner().getOrganizationGroupId());
				groupOranizationExModel.setName(docModel.getOwner().getOrganizationGroupName());
				
				ApiUserGroupExpandModel apiUserGroupExpandModel = new ApiUserGroupExpandModel();
				apiUserGroupExpandModel.setUserId(docModel.getOwner().getOrganizationUserId());
				apiUserGroupExpandModel.setFullName(docModel.getOwner().getOrganizationUserName());
				
				mapUserAndGroup.put(groupOranizationExModel, apiUserGroupExpandModel);
				mapUserAndGroup.forEach((k,v)->{
					txtUserGroup.setValue(v.getFullName() +" ( "+k.getName()+" )");
				});
				
			}

			listDocCategory.stream().forEach(model->{
				if(model.getKey().equals(docModel.getCategory().getKey())) {
					cmbDocCategory.setValue(model);
				}
			});

			listItemSecurity.stream().forEach(model->{
				if(model.getKey().equals(docModel.getSecurity().getKey())) {
					cmbDocSecurity.setValue(model);
				}
			});

			if(!docModel.getAttachments().isEmpty()) {
				this.add(initDocActtachment(docModel));
			}

			listIdAccachmentForUpdate = docModel.getAttachments();

			listDataCmbClassify.stream().forEach(model->{
				if(model.getKey() != null) {
					if(model.getLeft().equals(docModel.getClassifyTaskId())) {
						cmbClassifyTaskId.setValue(model);
					}
				}
			});

			listDataCmbLeader.stream().forEach(model->{
				if(model.getKey() !=null && model.getKey().equals(docModel.getLeaderApproveTaskId())) {
					cmbLeaderApproveTaskId.setValue(model);
				}
			});



			cbActive.setValue(docModel.isActive());


		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void initCmbDocCategory() {

		listDocCategory.clear();
		try {
			ApiResultResponse<List<ApiKeyValueModel>> data = ApiDocService.getKeyValueCategory();
			data.getResult().forEach(model->{
				listDocCategory.add(Pair.of(model.getKey(),model.getName()));
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		cmbDocCategory.setItems(listDocCategory);
		cmbDocCategory.setItemLabelGenerator(Pair::getRight);
		
		if(docCategory != null) {
			listDocCategory.forEach(model->{
				if(model.getKey() != null && model.getKey().equals(docCategory)) {
					cmbDocCategory.setValue(model);
					cmbDocCategory.setReadOnly(true);
				}
			});
		}else {
			cmbDocCategory.setValue(listDocCategory.get(0));
			cmbDocCategory.setReadOnly(false);
		}
	}

	private void initCmbDocSecurity() {
		listItemSecurity.clear();
		try {
			ApiResultResponse<List<ApiKeyValueModel>> data = ApiDocService.getSecurity();
			data.getResult().stream().forEach(model->{
				listItemSecurity.add(Pair.of(model.getKey(),model.getName()));
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		cmbDocSecurity.setItems(listItemSecurity);
		cmbDocSecurity.setItemLabelGenerator(Pair::getRight);
		cmbDocSecurity.setValue(listItemSecurity.get(0));

	}

	private void initDateIsued() {
		dateIsued.setLocale(Locale.of("vi","VN"));
	}

	private Component createLayout() {
		VerticalLayout mainLayout = new VerticalLayout();

		cmbDocCategory.setSizeFull();
		cmbDocSecurity.setSizeFull();
		txtDocNumber.setSizeFull();
		txtDocSymbol.setSizeFull();
		txtDocType.setSizeFull();
		dateIsued.setSizeFull();
		dateIsued.setValue(LocalDate.now());

		txtUserSigner.setSizeFull();
		txtDocSummary.setSizeFull();
		txtSignerPosition.setSizeFull();


		HorizontalLayout layout1 = new HorizontalLayout();
		layout1.add(cmbDocCategory,cmbDocSecurity);
		layout1.setSizeFull();

		HorizontalLayout layout2 = new HorizontalLayout();
		layout2.add(txtDocNumber,txtDocSymbol);
		layout2.setSizeFull();

		HorizontalLayout layout3 = new HorizontalLayout();
		layout3.add(dateIsued,txtDocType);
		layout3.setSizeFull();

		HorizontalLayout layout4 = new HorizontalLayout();
		multiComboboxReceivers.setWidthFull();
		btnChooseReceivers.getStyle().setMarginTop("30px");
		btnChooseReceivers.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
		layout4.add(multiComboboxReceivers,btnChooseReceivers);
		layout4.setSizeFull();

		HorizontalLayout layout5 = new HorizontalLayout();
		layout5.add(txtUserSigner,txtSignerPosition);
		layout5.setWidthFull();


		VerticalLayout layout6 = new VerticalLayout();


		HorizontalLayout hLayout6_1 = new HorizontalLayout(btnAddClassifyTaskId,cmbClassifyTaskId,cmbLeaderApproveTaskId,btnAddLeader);
		hLayout6_1.setWidthFull();

		btnAddClassifyTaskId.getStyle().setMarginTop("28px");
		btnAddClassifyTaskId.setTooltipText("Thêm nhanh phân loại chỉ đạo");

		btnAddLeader.getStyle().setMarginTop("28px");
		btnAddLeader.setTooltipText("Thêm nhanh người duyệt");
		cmbClassifyTaskId.setWidthFull();
		cmbLeaderApproveTaskId.setWidthFull();


		layout6.add(hLayout6_1);
		layout6.setWidthFull();

		UI.getCurrent().getPage().retrieveExtendedClientDetails(e->{
			if(e.getScreenWidth() < 768) {
				hLayout6_1.removeAll();
				hLayout6_1.add(btnAddClassifyTaskId,cmbClassifyTaskId);
				HorizontalLayout hLayout6_2 = new HorizontalLayout();
				hLayout6_2.add(btnAddLeader,cmbLeaderApproveTaskId);
				hLayout6_2.setWidthFull();
				layout6.add(hLayout6_2);
			}
		});

		cbActive.setValue(true);


		HorizontalLayout hLayoutOwner = new HorizontalLayout();
		hLayoutOwner.add(txtUserGroup,btnChooseFollowUp,btnResetChooseUser);
		txtUserGroup.setWidthFull();
		txtUserGroup.setReadOnly(true);
		setDefaultAssistant();
		txtUserGroup.setValue(userAuthenticationModel.getFullName()+"(Mặc định là người đang soạn)");
		btnChooseFollowUp.getStyle().setMarginTop("27px");
		btnResetChooseUser.getStyle().setMarginTop("27px");
		btnResetChooseUser.setTooltipText("Làm lại");

		hLayoutOwner.setWidthFull();

		mainLayout.setSizeFull();
		mainLayout.add(layout1,layout2,layout3,layout5,txtDocSummary,layout4);

		mainLayout.add(hLayoutOwner,layout6,cbActive);

		return mainLayout;
	}
	
	private void loadNewDoc() {
		txtDocType.setValue("Công văn");
		txtUserSigner.setValue(userAuthenticationModel.getFullName());
	}
	
	


	//Chọn người soạn
	private void openDialogChooseOrgForFollower(String idParent) {
		DialogTemplate dialogTemplate = new DialogTemplate("Chọn người soạn");

		TaskChooseUserAssitantForm listChooseGroupForm = new TaskChooseUserAssitantForm(idParent,mapUserAndGroup);
		listChooseGroupForm.addChangeListener(e->{
			mapUserAndGroup.clear();
			mapUserAndGroup.putAll(listChooseGroupForm.getMapGroupAndUserIsChoose());
			mapUserAndGroup.forEach((k,v)->{
				txtUserGroup.setValue(v.getMoreInfo().getFullName() +"( "+k.getName()+" )");
			});
		});

		dialogTemplate.getBtnSave().addClickListener(e->{
			dialogTemplate.close();
		});

		dialogTemplate.add(listChooseGroupForm);
		dialogTemplate.setHeight("100%");
		dialogTemplate.setWidth("80%");
		dialogTemplate.setLayoutMobile();
		dialogTemplate.open();
	}

	//Set mặc định người soạn
	private void setDefaultAssistant() {
		mapUserAndGroup.clear();
		GroupOranizationExModel groupOranizationExModel = new GroupOranizationExModel();
		ApiUserGroupExpandModel apiUserGroupExpandModel = new ApiUserGroupExpandModel();

		if(signInOrgModel.getGroup().getId() !=  null ) {

			groupOranizationExModel.setGroupId(signInOrgModel.getGroup().getId());
			groupOranizationExModel.setName(signInOrgModel.getGroup().getName());
		}else {
			groupOranizationExModel.setGroupId(belongOrganizationModel.getOrganizationId());
			groupOranizationExModel.setName("Ngoài tổ");
		}

		apiUserGroupExpandModel.setUserId(userAuthenticationModel.getId());
		apiUserGroupExpandModel.setUserName(userAuthenticationModel.getUsername());

		txtUserGroup.setValue(userAuthenticationModel.getUsername());

		ApiMoreinfoModel apiMoreinfoModel = new ApiMoreinfoModel();
		apiMoreinfoModel.setFullName(userAuthenticationModel.getFullName());
		apiUserGroupExpandModel.setMoreInfo(apiMoreinfoModel);

		mapUserAndGroup.put(groupOranizationExModel, apiUserGroupExpandModel);
	}



	public void saveADoc() {
		if(!upload.getListFileUpload().isEmpty()) {
			uploadFile();
		}

		if(!listFileDelete.isEmpty()) {
			doRemoveFile();
		}

		DocInputModel docInputModel = new DocInputModel();
		docInputModel.setNumber(txtDocNumber.getValue());
		docInputModel.setSymbol(txtDocSymbol.getValue());
		docInputModel.setSecurity(cmbDocSecurity.getValue().getLeft());
		docInputModel.setRegDate(LocalDateUtil.localDateToLong(dateIsued.getValue()));
		docInputModel.setType(txtDocType.getValue());
		docInputModel.setSignerName(txtUserSigner.getValue());
		docInputModel.setSummary(txtDocSummary.getValue());
		docInputModel.setCategory(cmbDocCategory.getValue().getKey());
		docInputModel.setActive(cbActive.getValue());
		docInputModel.setSignerPosition(txtSignerPosition.getValue());
		
		if(!mapOrgAndUserForReceivers.isEmpty()) {
			List<ApiDocOrgModel> listReceiveds = new ArrayList<ApiDocOrgModel>();
			mapOrgAndUserForReceivers.forEach((k,v)->{
				ApiDocOrgModel apiDocOrgModel = new ApiDocOrgModel();
				apiDocOrgModel.setOrganizationId(k.getId());
				apiDocOrgModel.setOrganizationName(k.getName());
				if(v != null) {
					apiDocOrgModel.setOrganizationUserId(v.getUserId());
					apiDocOrgModel.setOrganizationUserName(v.getFullName());
				}
				listReceiveds.add(apiDocOrgModel);
			});
			docInputModel.setReceivers(listReceiveds);
		}
		

		Owner owner = docInputModel.new Owner();
		owner.setOrganizationId(belongOrganizationModel.getOrganizationId());
		owner.setOrganizationName(belongOrganizationModel.getOrganizationName());

		owner.setOrganizationUserId(userAuthenticationModel.getId());
		owner.setOrganizationUserName(userAuthenticationModel.getUsername());
		owner.setOrganizationGroupId(signInOrgModel.getGroup().getId());
		owner.setOrganizationGroupName(signInOrgModel.getGroup().getName());
		if(!mapUserAndGroup.isEmpty()) {
			for(Map.Entry<GroupOranizationExModel, ApiUserGroupExpandModel> m : mapUserAndGroup.entrySet()) {
				owner.setOrganizationGroupId(m.getKey().getGroupId());
				owner.setOrganizationGroupName(m.getKey().getName());
				owner.setOrganizationUserId(m.getValue().getUserId());
				owner.setOrganizationUserName(m.getValue().getMoreInfo() == null ? "" :m.getValue().getMoreInfo().getFullName() );
			}
		}
		docInputModel.setOwner(owner);
		if(cmbClassifyTaskId.getValue() != null) {
			docInputModel.setClassifyTaskId(cmbClassifyTaskId.getValue().getKey());
		}

		if(cmbLeaderApproveTaskId.getValue() != null) {
			docInputModel.setLeaderApproveTaskId(cmbLeaderApproveTaskId.getValue().getKey());
		}
		

		if(checkIdDoc == true) {
			listIdAccachmentForUpdate.addAll(listIdAttachment);
			docInputModel.setAttachments(listIdAccachmentForUpdate);

			doUpdateDoc(docInputModel);
		}else {
			docInputModel.setAttachments(listIdAttachment);
			doCreateNewDoc(docInputModel);
		}

	}

	private void doCreateNewDoc(DocInputModel docInputModel) {
		ApiDocInputModel apiDocInputModel = new ApiDocInputModel(docInputModel);

		try {
			ApiResultResponse<ApiDocModel> createNewDoc = ApiDocService.createDoc(apiDocInputModel);
			if(createNewDoc.getStatus() == 201 || createNewDoc.getStatus() == 200) {
				upload.clear();
				onRun.run();
			}else {
				NotificationTemplate.error(createNewDoc.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void doUpdateDoc(DocInputModel docInputModel) {
		ApiDocInputModel apiDocInputModel = new ApiDocInputModel(docInputModel);
		try {
			ApiResultResponse<Object> updateAdoc = ApiDocService.updateDoc(idDoc, apiDocInputModel);
			if(updateAdoc.getStatus() == 201 || updateAdoc.getStatus() == 200) {
				upload.clear();
				onRun.run();
			}else {
				NotificationTemplate.error(updateAdoc.getMessage());
			}
		} catch (Exception e) {
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

	private void doRemoveFile() {
		listFileDelete.stream().forEach(idFile->{
			listIdAccachmentForUpdate.remove(idFile);
		});
	}

	private void initUpload() {
		upload.initUpload();
	}


	private Component initDocActtachment(DocModel docModel) {
		ActtachmentForm acttachmentForm = new ActtachmentForm(docModel.getAttachments(),false);
		acttachmentForm.addChangeListener(e->{
			listFileDelete.clear();
			listFileDelete.addAll(acttachmentForm.getListDelete());
		});
		return acttachmentForm;
	}

	private void doCreateFile(ApiInputMediaModel apiModel) {
		try {
			ApiResultResponse<ApiMediaModel> data = ApiMediaService.createFile(apiModel);
			if(data.getStatus() == 200 || data.getStatus() == 201) {
				listIdAttachment.add(data.getResult().getId());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private void loadCmbClassify() {
		listDataCmbClassify = new ArrayList<Pair<String,String>>();
		listDataCmbClassify.add(Pair.of(null,""));

		FilterClassifyLeaderModel filterClassifyLeaderModel = new FilterClassifyLeaderModel();
		filterClassifyLeaderModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
		filterClassifyLeaderModel.setLimit(100000);
		filterClassifyLeaderModel.setSkip(0);
		filterClassifyLeaderModel.setActive(true);
		ApiResultResponse<List<ApiClassifyTaskModel>> data = ApiClassifyTaskService.getListClassify(filterClassifyLeaderModel);
		data.getResult().stream().forEach(model->{
			listDataCmbClassify.add(Pair.of(model.getId(),model.getName()));
		});

		cmbClassifyTaskId.setItems(listDataCmbClassify);
		cmbClassifyTaskId.setItemLabelGenerator(Pair::getRight);
		cmbClassifyTaskId.setValue(listDataCmbClassify.get(0));
	}

	private void loadCmbLeader() {
		listDataCmbLeader = new ArrayList<Pair<String,String>>();
		listDataCmbLeader.add(Pair.of(null,""));

		FilterClassifyLeaderModel fiLeaderModel = new FilterClassifyLeaderModel();
		fiLeaderModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
		fiLeaderModel.setSkip(0);
		fiLeaderModel.setLimit(10000);
		fiLeaderModel.setActive(true);
		ApiResultResponse<List<ApiLeaderApproveTaskModel>> data = ApiLeaderApproveTaskService.getListLeader(fiLeaderModel);
		data.getResult().stream().forEach(model->{
			listDataCmbLeader.add(Pair.of(model.getId(),model.getName()));
		});

		cmbLeaderApproveTaskId.setItems(listDataCmbLeader);
		cmbLeaderApproveTaskId.setItemLabelGenerator(Pair::getRight);
		cmbLeaderApproveTaskId.setValue(listDataCmbLeader.get(0));
	}

	private void openDialogClassifyTaskId() {
		DialogTemplate dialogTemplate = new DialogTemplate("THÊM PHÂN LOẠI CHỈ ĐẠO");

		ClassifyTaskForm classifyTaskForm = new ClassifyTaskForm(SessionUtil.getOrg(), SessionUtil.getUser());
		classifyTaskForm.addChangeListener(e->{
			loadCmbClassify();
		});


		dialogTemplate.add(classifyTaskForm);
		dialogTemplate.setSizeFull();
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setLayoutMobile();
		dialogTemplate.open();
	}

	private void openDialogLeader() {
		DialogTemplate dialogTemplate = new DialogTemplate("THÊM NGƯỜI DUYỆT");

		LeaderApproveTaskForm leaderApproveTaskForm = new LeaderApproveTaskForm(SessionUtil.getOrg(), SessionUtil.getUser());
		leaderApproveTaskForm.addChangeListener(e->{
			loadCmbLeader();
		});

		dialogTemplate.add(leaderApproveTaskForm);
		dialogTemplate.setSizeFull();
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setLayoutMobile();

		dialogTemplate.open();
	}
	
	private void openDialogChooseReceived() {
		DialogTemplate dialogTemplate = new DialogTemplate("CHỌN NƠI NHẬN VĂN BẢN");
		OrganizationModel organizationModel = getInfoOwner(belongOrganizationModel.getOrganizationId());
		TaskChooseAllOrgForReceiversForm taskChooseAllOrgForReceiversForm = 
				new TaskChooseAllOrgForReceiversForm(belongOrganizationModel.getOrganizationId(), organizationModel, mapOrgAndUserForReceivers, checkIdDoc);
		dialogTemplate.getFooter().removeAll();
		taskChooseAllOrgForReceiversForm.addChangeListener(e->{
			mapOrgAndUserForReceivers.clear();
			mapOrgAndUserForReceivers.putAll(taskChooseAllOrgForReceiversForm.getMapOrgIsChoose());
			initMultiSelectCmb();
			dialogTemplate.close();
		});
		dialogTemplate.add(taskChooseAllOrgForReceiversForm);
		dialogTemplate.setSizeFull();
		dialogTemplate.open();
	}
	
	private void initMultiSelectCmb() {
		List<Pair<OrganizationModel, UserOranizationExModel>> listReceived = new ArrayList<Pair<OrganizationModel,UserOranizationExModel>>();
		for(Map.Entry<OrganizationModel, UserOranizationExModel> m : mapOrgAndUserForReceivers.entrySet()) {
			listReceived.add(Pair.of(m.getKey(),m.getValue()));
		}
		multiComboboxReceivers.setItems(listReceived);
		multiComboboxReceivers.select(listReceived);
		multiComboboxReceivers.setReadOnly(true);
		multiComboboxReceivers.setItemLabelGenerator(model->{
			if(model.getValue() != null) {
				return model.getKey().getName() + " ("+model.getValue().getFullName()+")";
			}
			return model.getKey().getName();
		});
	}
	
	
	private OrganizationModel getInfoOwner(String idOrg) {
		ApiResultResponse<ApiOrganizationModel> getInfoOrg = ApiOrganizationService.getOneOrg(idOrg);
		if(getInfoOrg.isSuccess()) {
			return new OrganizationModel(getInfoOrg.getResult());
		}

		return null;
	}


	public void setReadOnly() {
		cmbDocCategory.setReadOnly(true);
		cmbDocSecurity.setReadOnly(true);
		txtDocNumber.setReadOnly(true);
		txtDocSymbol.setReadOnly(true);
		txtDocType.setReadOnly(true);
		dateIsued.setReadOnly(true);
		txtUserSigner.setReadOnly(true);
		txtDocSummary.setReadOnly(true);
		txtSignerPosition.setReadOnly(true);
		cmbLeaderApproveTaskId.setReadOnly(true);
		cmbClassifyTaskId.setReadOnly(true);
		btnAddClassifyTaskId.setVisible(false);
		btnAddLeader.setVisible(false);
		cbActive.setReadOnly(true);
		btnChooseFollowUp.setVisible(false);
		btnResetChooseUser.setVisible(false);
		this.remove(upload);
	}
}