package com.ngn.tdnv.doc.forms;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.annotation.Async;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.doc.ApiDocCompletedModel;
import com.ngn.api.doc.ApiDocModel;
import com.ngn.api.doc.ApiDocResultConfirmModel;
import com.ngn.api.doc.ApiDocService;
import com.ngn.api.doc.ApiFilterListDocModel;
import com.ngn.api.doc.ApiFilterSummaryDocModel;
import com.ngn.api.media.ApiInputMediaModel;
import com.ngn.api.media.ApiMediaModel;
import com.ngn.api.media.ApiMediaService;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tags.ApiInputAddclassModel;
import com.ngn.api.tags.ApiTagFilterModel;
import com.ngn.api.tags.ApiTagModel;
import com.ngn.api.tags.ApiTagService;
import com.ngn.api.tasks.ApiCreatorModel;
import com.ngn.api.utils.ApiKeyValueModel;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.models.sign_in_org.SignInOrgModel;
import com.ngn.tdnv.doc.forms.components.HeaderComponent;
import com.ngn.tdnv.doc.forms.create_task.TaskCreateFormV2;
import com.ngn.tdnv.doc.models.DocModel;
import com.ngn.tdnv.tag.forms.TagForm;
import com.ngn.utils.LocalDateUtil;
import com.ngn.utils.components.ActtachmentForm;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.ConfirmDialogTemplate;
import com.ngn.utils.components.DialogTemplate;
import com.ngn.utils.components.NotificationTemplate;
import com.ngn.utils.components.PaginationForm;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.ngn.utils.uploads.UploadModuleBasic;
import com.ngn.utils.uploads.UploadModuleDataModel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.dom.Style.AlignItems;
import com.vaadin.flow.dom.Style.Display;
import com.vaadin.flow.dom.Style.FlexDirection;

public class DocListForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private Logger log = LogManager.getLogger(DocListForm.class);
	private boolean isMobileLayout = false;
	private VerticalLayout layoutGrid = new VerticalLayout();

	private List<DocModel> listModel = new ArrayList<DocModel>();

	private List<Checkbox> listCheckBox = new ArrayList<Checkbox>();
	private List<DocModel> listDocIsChoose = new ArrayList<DocModel>();

	private List<ApiKeyValueModel> listStatus = new ArrayList<ApiKeyValueModel>();
	private List<String> listIdAttachment = new ArrayList<String>();
	
	private List<ApiTagModel> listTags = new ArrayList<ApiTagModel>();

	private PaginationForm paginationForm;

	private DocFilterForm filterDocForm;

	private UserAuthenticationModel userAuthenticationModel;
	private BelongOrganizationModel belongOrganizationModel;
	private SignInOrgModel signInOrgModel;
	private boolean checkAddDoc;
	private DocModel docModel;
	private Map<String, List<String>> parametters;
	private String docCategory;
	public DocListForm(UserAuthenticationModel userAuthenticationModel,BelongOrganizationModel belongOrganizationModel,SignInOrgModel signInOrgModel,boolean checkAddDoc,
			DocModel docModel,Map<String, List<String>> parametters,String docCategory) {
		this.userAuthenticationModel = userAuthenticationModel;
		this.belongOrganizationModel = belongOrganizationModel;
		this.signInOrgModel = signInOrgModel;
		this.checkAddDoc = checkAddDoc;
		this.docCategory = docCategory;
		if(docModel != null) {
			this.docModel = docModel;
		}
		if(parametters != null) {
			this.parametters = parametters;
		}
		checkMobile();
		buildLayout();
		configComponent();
		loadDataTags();
		loadData();
		checkPermission();
		checkParameters();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		filterDocForm = new DocFilterForm(belongOrganizationModel,parametters,docCategory);

		paginationForm = new PaginationForm(()->{
			if(paginationForm != null) {
				loadData();
			}
		});
		
		if(isMobileLayout) {
			paginationForm.setLayoutMobile();
		}
		
		paginationForm.getCmbItem().setValue(10);
		layoutGrid.setSizeFull();
		this.add(filterDocForm,paginationForm,layoutGrid);
	}

	@Override
	public void configComponent() {
		filterDocForm.getBtnAddDoc().addClickListener(e->{
			openDialogCreateDoc();
		});

		filterDocForm.addChangeListener(e->{
			loadData();
		});

	}

	@Async
	public void loadData() {
		listModel = new ArrayList<DocModel>();
		listStatus = new ArrayList<ApiKeyValueModel>();

		try {
			ApiResultResponse<List<ApiKeyValueModel>> dataStatus = ApiDocService.getKeyValueStatus();
			if(dataStatus.isSuccess()) {
				dataStatus.getResult().forEach(model->{
					listStatus.add(model);
				});
			}
		} catch (Exception e) {
		}


		try {
			ApiResultResponse<List<ApiDocModel>> data = ApiDocService.getListDoc(initValueSeach());
			paginationForm.setItemCount(data.getTotal());

			data.getResult().stream().forEach(model->{
				listModel.add(new DocModel(model));
			});

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		layoutGrid.removeAll();
		listModel.forEach(model->{
			layoutGrid.add(createItemDoc(model));
		});

		//		listDataProvider = new ListDataProvider<DocModel>(listModel);
		//		gridDoc.setItems(listDataProvider);
		refreshMainLayout();
	}

	private void checkPermission() {


	}

	private void checkMobile() {
		UI.getCurrent().getPage().retrieveExtendedClientDetails(e->{
			if(e.getScreenWidth() < 768) {
				isMobileLayout = true;
			}
		});
	}

	private ApiFilterListDocModel initValueSeach() {
		ApiFilterListDocModel apiFilterListDocModel = filterDocForm.getFilter();
		apiFilterListDocModel.setSkip(paginationForm.getSkip());
		apiFilterListDocModel.setLimit(paginationForm.getLimit());

		return apiFilterListDocModel;
	}




	private Component createItemDoc(DocModel docModel) {
		VerticalLayout vLayoutItemDoc = new VerticalLayout();
		vLayoutItemDoc.setWidthFull();
		vLayoutItemDoc.setSpacing(false);

		HorizontalLayout hLayout1 = new HorizontalLayout();
		hLayout1.setWidthFull();
		String widthHeader = "60px";
		hLayout1.add(createLayoutKeyAndValue("Ký hiệu: ", docModel.getSymbol(), "52px", null),
				createLayoutKeyAndValue("Số hiệu: ", docModel.getNumber(), "52px", null),
				createLayoutKeyAndValue("Ngày ký: ", docModel.getRegTimeText(), widthHeader, null),
				createLayoutKeyAndValue("Người ký: ", docModel.getSignerName(), "65px", null));

		Span spStatus = checkSpanStatus(docModel.getStatus().getKey());
		spStatus.getStyle().setMarginLeft("auto");
		hLayout1.add(spStatus);

		vLayoutItemDoc.add(hLayout1);

		vLayoutItemDoc.add(createLayoutKeyAndValue("Trích yếu: ", docModel.getSummary(), "65px", null));

		HorizontalLayout hLayout2 = new HorizontalLayout();
		hLayout2.setWidthFull();

		hLayout2.add(createLayoutKeyAndValue("Người soạn thảo: ", docModel.getOwner().getOrganizationUserName(), "115px", null),
				createLayoutKeyAndValue("Ngày vào hệ thống: ", docModel.getCreateTimeText(), "135px", null));

		vLayoutItemDoc.add(hLayout2);

		HorizontalLayout hLayoutButton = new HorizontalLayout();

		//Attachment
		ButtonTemplate btnAttachment = new ButtonTemplate(docModel.getAttachments().size()+" đính kèm",FontAwesome.Solid.PAPERCLIP.create());
		btnAttachment.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnAttachment.addClickListener(e->{
			openDialogViewAttachment(docModel.getAttachments());
		});

		//View Tasks
		ButtonTemplate btnCountTask = new ButtonTemplate(docModel.getCountTaskCompleted()+"/"+docModel.getCountTask()+" Nhiệm vụ hoàn thành",FontAwesome.Solid.LIST_CHECK.create());
		btnCountTask.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		if(docModel.getCountTask() == 0) {
			btnCountTask.addThemeVariants(ButtonVariant.LUMO_ERROR);
		}else {
			btnCountTask.getStyle().setColor("hsl(114.06deg 98.98% 17.41%)");
		}

		btnCountTask.addClickListener(e->{
			openDialogViewTask(docModel);
		});

		//Confirm Completed
		ButtonTemplate btnCompletedDoc = new ButtonTemplate("Xác nhận hoàn thành",FontAwesome.Solid.CHECK_CIRCLE.create());
		btnCompletedDoc.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnCompletedDoc.setEnabled(false);

		ButtonTemplate btnViewDocCompleted = new ButtonTemplate("Văn bản đã hoàn thành",FontAwesome.Solid.EYE.create());
		btnViewDocCompleted.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnViewDocCompleted.setVisible(false);


		if(docModel.getCountTaskCompleted() == docModel.getCountTask() && docModel.getCountTaskCompleted() > 0) {
			btnCompletedDoc.setEnabled(true);
			btnCompletedDoc.addClickListener(e->{
				openDialogConfirmCompleteDoc(docModel.getId());
			});
		}

		if(docModel.getResultConfirm() != null) {
			btnViewDocCompleted.setText("Thông tin hoàn thành");
			btnCompletedDoc.setVisible(false);
			btnViewDocCompleted.setVisible(true);
			btnViewDocCompleted.addClickListener(e->{
				openDialogViewResultCompleted(docModel.getResultConfirm());
			});
		}
		btnCompletedDoc.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

		//Create new task
		ButtonTemplate btnCreateTask = new ButtonTemplate("Giao nhiệm vụ",FontAwesome.Solid.MAIL_FORWARD.create());
		btnCreateTask.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnCreateTask.addClickListener(e->{
			openDialogCreateTask2(docModel);
		});

		//View detail doc
		ButtonTemplate btnViewDoc = new ButtonTemplate("Xem",FontAwesome.Solid.EYE.create());
		btnViewDoc.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnViewDoc.setTooltipText("Tổng quan văn bản");
		btnViewDoc.addClickListener(e->{
			openDialogOverViewDoc(docModel);
		});

		//Update doc
		ButtonTemplate btnEdit = new ButtonTemplate("Sửa",FontAwesome.Solid.EDIT.create());
		btnEdit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnEdit.addClickListener(e->{
			openDialogUpdateDoc(docModel.getId());
		});

		//Delete doc
		ButtonTemplate btnDelete = new ButtonTemplate("Xóa",FontAwesome.Solid.TRASH.create());
		btnDelete.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnDelete.addThemeVariants(ButtonVariant.LUMO_ERROR);
		btnDelete.addClickListener(e ->{
			openConfirmDialogDelete(docModel.getId());
		});
		
		
		ButtonTemplate btnTags = new ButtonTemplate("thẻ",FontAwesome.Solid.TAGS.create());
		btnTags.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnTags.addClickListener(e->{
			openDialogChooseTag(null,docModel.getId());
		});
		
		ApiTagModel tag = findTagOfDoc(docModel.getId());
		if(tag != null) {
			btnTags = new ButtonTemplate(tag.getName(),FontAwesome.Solid.TAGS.create());
			btnTags.getStyle().setColor(tag.getColor()).setMarginLeft("auto");
			btnTags.addClickListener(e->{
				openDialogChooseTag(tag.getId(),docModel.getId());
			});
		}
		
		if(docModel.getCountTask()>0) {
			btnDelete.setEnabled(false);
		}

		if(isMobileLayout) {
			btnAttachment.setText("");
			btnCountTask.setText("");
			btnCompletedDoc.setText("");
			btnViewDocCompleted.setText("");
			btnCreateTask.setText("");
			btnViewDoc.setText("");
			btnEdit.setText("");
			btnDelete.setText("");
			btnTags.setText("");
			
			spStatus.setVisible(false);
			
		}

		hLayoutButton.add(btnAttachment,btnCountTask,btnCompletedDoc,btnViewDocCompleted,btnCreateTask,btnViewDoc,btnEdit,btnDelete,btnTags);
		
		hLayoutButton.setWidthFull();


		//Selected doc
		if(checkAddDoc) {
			Checkbox checkbox = new Checkbox("Chọn văn bản");
			checkbox.getStyle().setMarginLeft("auto");
			if(this.docModel != null) {
				if(this.docModel.equals(docModel)) {
					checkbox.setValue(true);
					listDocIsChoose.clear();
					listDocIsChoose.add(docModel);
				}
			}

			checkbox.addClickListener(e->{
				listCheckBox.stream().forEach(md->{
					if(md.equals(e.getSource())) {
						if(checkbox.getValue() == true) {
							listDocIsChoose.clear();
							listDocIsChoose.add(docModel);
						}
					}else {
						md.setValue(false);
					}
				});

				fireEvent(new ClickEvent(this,false));
			});


			listCheckBox.add(checkbox);
			hLayoutButton.add(checkbox);
		}
		hLayoutButton.getStyle().setAlignItems(AlignItems.CENTER);
		vLayoutItemDoc.add(new Hr(),hLayoutButton);


		vLayoutItemDoc.getStyle().setBoxShadow("rgba(0, 0, 0, 0.16) 0px 1px 4px").setPadding("10px").setBorderRadius("10px");
		

		
		
		return vLayoutItemDoc;
	}
	

	private Span checkSpanStatus(String keyStatus) {
		Span spStatus = new Span();
		if(keyStatus.equals("dangthuchien")) {
			spStatus.setText("Đang thực hiện");
			spStatus.getElement().getThemeList().add("badge");
		}else if(keyStatus.equals("chuagiaonhiemvu")) {
			spStatus.setText("Chưa giao nhiệm vụ");
			spStatus.getElement().getThemeList().add("badge error");
		}else if(keyStatus.equals("vanbandahoanthanh")) {
			spStatus.setText("Văn bản đã hoàn thành");
			spStatus.getElement().getThemeList().add("badge success");
		}
		return spStatus;
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
		
		if(isMobileLayout) {
			hlayout.getStyle().setDisplay(Display.FLEX).setFlexDirection(FlexDirection.COLUMN);
		}

		hlayout.add(spHeader,spValue);
		//		hlayout.getStyle().setBorderBottom("1px solid #c3c3c3").setPadding("5px");
		return hlayout;
	}

	private void checkParameters() {
		if(parametters != null) {
			if(parametters.containsKey("detail")) {
				String idDoc = parametters.get("detail").get(0);
				openDialogUpdateDoc(idDoc);
			}else if(parametters.containsKey("assign")) {
				String idDoc = parametters.get("assign").get(0);
				DocModel docPara = getADoc(idDoc);
				if(docPara != null) {
					openDialogCreateTask2(docPara);
				}else {
					NotificationTemplate.error("Không tìm thấy văn bản nguồn");
				}
			}

			if(parametters.containsKey("func")) {
				String func = parametters.get("func").get(0);
				String idDoc = parametters.get("docid") == null ? "" : parametters.get("docid").get(0);
				String iOfficeId = "";
				DocModel docModel = null;
				if(idDoc.isEmpty()) {
					iOfficeId = parametters.get("iofficeid") == null ? "" : parametters.get("iofficeid").get(0);
					docModel = getADocByIofficeId(iOfficeId);
				}else {
					docModel = getADoc(idDoc);
				}

				if(docModel != null) {
					UI.getCurrent().getPage().setTitle(docModel.getSummary());
					if(func.equals("task-assign")) {
						openDialogCreateTask2(docModel);
					}

					if(func.equals("view-task-assign")) {
						openDialogViewTask(docModel);
					}
				}else {
					NotificationTemplate.error("Không tìm thấy văn bản trong hệ thống");
				}

			}

		}
	}

	private void openDialogCreateDoc() {
		DialogTemplate dialogTemplate = new DialogTemplate("THÊM VĂN BẢN MỚI");
		DocEditForm docForm = new DocEditForm(null,()->{
			loadData();
			filterDocForm.loadData();
			dialogTemplate.close();
			NotificationTemplate.success("Thành công");
		},userAuthenticationModel,belongOrganizationModel,signInOrgModel,docCategory);


		dialogTemplate.add(docForm);
		dialogTemplate.setHeightFull();
		dialogTemplate.setWidth("80%");
		dialogTemplate.setLayoutMobile();
		dialogTemplate.getBtnSave().addClickListener(e->{
			docForm.saveADoc();
		});

		dialogTemplate.open();
	}

	private void openDialogUpdateDoc(String idDoc) {
		DialogTemplate dialogTemplate = new DialogTemplate("CHỈNH SỬA VĂN BẢN");
		DocEditForm editDocForm = new DocEditForm(idDoc,()->{
			loadData();
			dialogTemplate.close();
			NotificationTemplate.success("Thành công");
		},userAuthenticationModel,belongOrganizationModel,signInOrgModel,docCategory);
		dialogTemplate.add(editDocForm);

		dialogTemplate.getBtnSave().addClickListener(e->{
			editDocForm.saveADoc();
		});

		dialogTemplate.setHeightFull();
		dialogTemplate.setWidth("80%");
		dialogTemplate.setLayoutMobile();
		dialogTemplate.open();
	}

	private void openConfirmDialogDelete(String idDoc) {
		ConfirmDialogTemplate confirmDialogTemplate = new ConfirmDialogTemplate("XÓA VĂN BẢN");

		confirmDialogTemplate.setText("Xác nhận xóa văn bản");
		confirmDialogTemplate.setDelete();
		confirmDialogTemplate.addConfirmListener(e->{
			doDelete(idDoc);
		});
		confirmDialogTemplate.open();
	}

	private void doDelete(String idDoc) {
		try {
			ApiResultResponse<Object> delete = ApiDocService.deleteDoc(idDoc);
			if(delete.getStatus() == 200 || delete.getStatus() == 201) {
				loadData();
				NotificationTemplate.success(delete.getMessage());
			}else {
				NotificationTemplate.error(delete.getMessage());
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}


	private void openDialogCreateTask2(DocModel docModel) {
		DialogTemplate dialogTemplate = new DialogTemplate("Giao nhiệm vụ mới");
		//		TaskCreateForm createTaskForm = new TaskCreateForm(docModel.getId(),belongOrganizationModel, userAuthenticationModel,signInOrgModel,null);
		TaskCreateFormV2 createTaskForm = new TaskCreateFormV2(docModel.getId(),belongOrganizationModel, userAuthenticationModel,signInOrgModel,null);
		dialogTemplate.add(createTaskForm);

		createTaskForm.addChangeListener(e->{
			loadData();
			filterDocForm.loadData();
			refreshMainLayout();
			dialogTemplate.close();
		});

		dialogTemplate.setHeightFull();
		if(docModel.getAttachments().isEmpty()) {
			dialogTemplate.setWidth("60%");
		}else {
			dialogTemplate.setWidth("100%");
		}
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setLayoutMobile();

		dialogTemplate.open();
	}

	private void openDialogViewTask(DocModel docModel) {
		DialogTemplate dialogTemplate = new DialogTemplate("Danh sách nhiệm vụ của văn bản");

		ApiFilterSummaryDocModel apiFilterSummaryDocModel = new ApiFilterSummaryDocModel();
		apiFilterSummaryDocModel.setDocId(docModel.getId());
		apiFilterSummaryDocModel.setFromDate(filterDocForm.getFilter().getFromDate());
		apiFilterSummaryDocModel.setToDate(filterDocForm.getFilter().getToDate());

		ListTasksOfDocForm listTasksOfDocForm = new ListTasksOfDocForm(docModel,apiFilterSummaryDocModel);
		listTasksOfDocForm.addChangeListener(e->{
			loadData();
		});

		dialogTemplate.add(listTasksOfDocForm);
		dialogTemplate.setWidth("100%");
		dialogTemplate.setHeightFull();
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setLayoutMobile();
		dialogTemplate.open();
	}

	private void openDialogConfirmCompleteDoc(String idDoc) {
		ConfirmDialogTemplate confirmDialogTemplate = new ConfirmDialogTemplate("Hoàn thành văn bản");
		VerticalLayout vLayoutCompleteDoc = new VerticalLayout();
		vLayoutCompleteDoc.setWidthFull();
		DateTimePicker dateCompleted = new DateTimePicker("Thời gian văn bản hoàn thành");
		dateCompleted.setLocale(LocalDateUtil.localeVietNam());
		dateCompleted.setValue(LocalDateTime.now());
		dateCompleted.setWidthFull();

		TextArea txtContent = new TextArea();
		txtContent.setWidthFull();
		txtContent.setPlaceholder("Văn bản này đã hoàn thành");
		txtContent.setValue("Văn bản này đã hoàn thành");

		listIdAttachment = new ArrayList<String>();
		UploadModuleBasic upload = new UploadModuleBasic();
		upload.initUpload();


		vLayoutCompleteDoc.add(dateCompleted,txtContent,upload);

		confirmDialogTemplate.setText(vLayoutCompleteDoc);
		confirmDialogTemplate.open();

		ApiDocCompletedModel apiDocCompletedModel = new ApiDocCompletedModel();
		apiDocCompletedModel.setContent(txtContent.getValue());
		apiDocCompletedModel.setCompletedTime(LocalDateUtil.localDateTimeToLong(dateCompleted.getValue()));
		apiDocCompletedModel.setCreator(getCreatetor());

		confirmDialogTemplate.addConfirmListener(e->{
			if(!upload.getListFileUpload().isEmpty()) {
				uploadFile(upload);
				apiDocCompletedModel.setAttachments(listIdAttachment);
			}
			doCompleted(idDoc, apiDocCompletedModel);
		});
	}

	private void doCompleted(String idDoc,ApiDocCompletedModel apiDocCompletedModel) {
		ApiResultResponse<Object> completed = ApiDocService.completeDoc(idDoc, apiDocCompletedModel);
		if(completed.isSuccess()) {
			NotificationTemplate.success("Văn bản đã cập nhật");
			loadData();
			filterDocForm.loadData();
		}else {
			NotificationTemplate.warning("Có lỗi xảy ra");
		}
	}

	private void openDialogViewAttachment(List<String> attachments) {
		DialogTemplate dialogTemplate = new DialogTemplate("Danh sách tệp đính kèm");
		ActtachmentForm acttachmentForm = new ActtachmentForm(attachments,true);

		dialogTemplate.setWidth("50%");
		dialogTemplate.add(acttachmentForm);
		dialogTemplate.open();
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setLayoutMobile();
	}

	private void openDialogOverViewDoc(DocModel docModel) {
		DialogTemplate dialogTemplate = new DialogTemplate("Thông tin văn bản");

		ApiFilterSummaryDocModel apiFilterSummaryDocModel = new ApiFilterSummaryDocModel();
		apiFilterSummaryDocModel.setDocId(docModel.getId());
		apiFilterSummaryDocModel.setFromDate(filterDocForm.getFilter().getFromDate());
		apiFilterSummaryDocModel.setToDate(filterDocForm.getFilter().getToDate());

		DocOverviewForm docOverviewForm = new DocOverviewForm(docModel.getId(), docModel, apiFilterSummaryDocModel,false);
		docOverviewForm.addChangeListener(e->{
			dialogTemplate.close();
			loadData();
		});
		dialogTemplate.setWidth("90%");
		dialogTemplate.setHeightFull();
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.add(docOverviewForm);
		dialogTemplate.setLayoutMobile();
		dialogTemplate.open();
	}

	private void openDialogViewResultCompleted(ApiDocResultConfirmModel resultConfirmModel) {
		DialogTemplate dialogTemplate = new DialogTemplate("Văn bản hoàn thành");

		HeaderComponent headerComponent = new HeaderComponent("Thông tin văn bản hoàn thành");
		VerticalLayout vLayoutResult = new VerticalLayout();
		vLayoutResult.setWidthFull();
		String width = "200px";
		vLayoutResult.add(createLayoutKeyAndValue("Ngày hoàn thành: ", LocalDateUtil.dfDateTime.format(resultConfirmModel.getCompletedTime()), width, null),
				createLayoutKeyAndValue("Ngày xác nhận: ", LocalDateUtil.dfDateTime.format(resultConfirmModel.getConfirmedTime()), width, null),
				createLayoutKeyAndValue("Nội dung hoàn thành: ", resultConfirmModel.getContent(), width, null),
				createLayoutKeyAndValue("Người xác nhận: ", resultConfirmModel.getCreator() == null ? "Đang cập nhật" : resultConfirmModel.getCreator().getOrganizationUserName(), width, null));

		ButtonTemplate btnViewAttachment = new ButtonTemplate("Đính kèm ("+resultConfirmModel.getAttachments().size()+")",FontAwesome.Solid.PAPERCLIP.create());
		btnViewAttachment.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnViewAttachment.getStyle().setMarginLeft("auto");
		btnViewAttachment.addClickListener(e->{
			openDialogViewAttachment(resultConfirmModel.getAttachments());
		});

		vLayoutResult.add(btnViewAttachment);

		headerComponent.addLayout(vLayoutResult);
		headerComponent.getStyle().setPadding("10px");

		dialogTemplate.add(headerComponent);

		dialogTemplate.setWidth("60%");
		dialogTemplate.getFooter().removeAll();

		dialogTemplate.open();
	}


	public List<DocModel> getListDocIsChoose(){
		return this.listDocIsChoose;
	}

	private DocModel getADoc(String idDoc) {
		ApiResultResponse<ApiDocModel> dataDoc = ApiDocService.getAdoc(idDoc);
		if(dataDoc.isSuccess()) {
			DocModel oneDocModel = new DocModel(dataDoc.getResult());
			return oneDocModel;
		}
		return null;
	}

	private ApiCreatorModel getCreatetor() {
		ApiCreatorModel apiCreatorModel = new ApiCreatorModel();

		apiCreatorModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
		apiCreatorModel.setOrganizationName(belongOrganizationModel.getOrganizationName());
		apiCreatorModel.setOrganizationUserId(userAuthenticationModel.getId());
		apiCreatorModel.setOrganizationUserName(userAuthenticationModel.getFullName());

		return apiCreatorModel;
	}

	private void uploadFile(UploadModuleBasic upload) {
		List<UploadModuleDataModel> listFile = upload.getListFileUpload();
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
			if(data.getStatus() == 200 || data.getStatus() == 201) {
				listIdAttachment.add(data.getResult().getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private DocModel getADocByIofficeId(String iofficeId) {

		ApiResultResponse<ApiDocModel> dataDoc = ApiDocService.getAdocByOfficeId(iofficeId);
		if(dataDoc.isSuccess()) {
			DocModel docModel = new DocModel(dataDoc.getResult());
			return docModel;
		}

		return null;
	}
	
	private void loadDataTags() {
		ApiTagFilterModel apiTagFilterModel = new ApiTagFilterModel();
		apiTagFilterModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
		apiTagFilterModel.setUserId(userAuthenticationModel.getId());
		apiTagFilterModel.setSkip(0);
		apiTagFilterModel.setLimit(0);
		apiTagFilterModel.setType("Doc");
		apiTagFilterModel.setActive(true);
		
		listTags = new ArrayList<ApiTagModel>();
		ApiResultResponse<List<ApiTagModel>> data = ApiTagService.getListTags(apiTagFilterModel);
		if(data.isSuccess()) {
			listTags.addAll(data.getResult());
		}
	}
	
	private ApiTagModel findTagOfDoc(String idDoc) {
		for(ApiTagModel apiTagModel : listTags) {
			if(apiTagModel.getClassIds() != null) {
				for(String i : apiTagModel.getClassIds()) {
					if(i.equals(idDoc)) {
						return apiTagModel;
					}
				}
			}
		}
		return null;
	}
	
	private void openDialogChooseTag(String tagId,String idDoc) {
		DialogTemplate dialogTemplate = new DialogTemplate("Chọn thẻ");
		
		ApiInputAddclassModel apiInputAddclassModel = new ApiInputAddclassModel();
		
		List<String> listAddclassId = new ArrayList<String>();
		listAddclassId.add(idDoc);
		
		apiInputAddclassModel.setClassIds(listAddclassId);
		apiInputAddclassModel.setType("Doc");
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
}


