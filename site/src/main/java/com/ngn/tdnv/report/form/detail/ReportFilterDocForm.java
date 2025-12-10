package com.ngn.tdnv.report.form.detail;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.flowingcode.vaadin.addons.fontawesome.FontAwesome.Solid.Icon;
import com.ngn.api.classify_task.ApiClassifyTaskModel;
import com.ngn.api.classify_task.ApiClassifyTaskService;
import com.ngn.api.doc.ApiDocService;
import com.ngn.api.leader_approve_task.ApiLeaderApproveTaskModel;
import com.ngn.api.leader_approve_task.ApiLeaderApproveTaskService;
import com.ngn.api.organization.ApiOrganizationModel;
import com.ngn.api.organization.ApiOrganizationService;
import com.ngn.api.organization.ApiUserGroupExpandModel;
import com.ngn.api.report.ApiFilterReportDocModel;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.utils.ApiKeyValueModel;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.setting.leader_classify.model.FilterClassifyLeaderModel;
import com.ngn.tdnv.doc.forms.DocFilterForm;
import com.ngn.utils.LocalDateUtil;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePickerVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexDirection;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ReportFilterDocForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private Logger log = LogManager.getLogger(DocFilterForm.class);
	private boolean isUiMobile = false;

	private VerticalLayout vLayout = new VerticalLayout();
	private DateTimePicker startDate = new DateTimePicker("Từ ngày");
	private DateTimePicker endDate = new DateTimePicker("Đến ngày");
	private ComboBox<Pair<String, String>> cmbStatus = new ComboBox<Pair<String,String>>("Trạng thái");

	private ComboBox<Pair<String, String>> cmbOwnerDoc = new ComboBox<Pair<String,String>>("Văn bản của đơn vị");

	private ComboBox<Pair<String, String>> cmbUserOfOwner = new ComboBox<Pair<String,String>>("Người soạn thảo");

	private ComboBox<Pair<String, String>> cmbClassifyTask = new ComboBox<Pair<String,String>>("Phân loại chỉ đạo");
	private List<Pair<String, String>> listClassifyTask = new ArrayList<Pair<String,String>>();

	private ComboBox<Pair<String,String>> cmbLeaderTask = new ComboBox<Pair<String,String>>("Người duyệt");
	private List<Pair<String, String>> listLeaderTask = new ArrayList<Pair<String,String>>();

	private ComboBox<Pair<String, String>> cmbCategory = new ComboBox<Pair<String,String>>("Loại văn bản");



	private ButtonTemplate btnCreateReport = new ButtonTemplate("Lập báo cáo",FontAwesome.Solid.FILE_SIGNATURE.create());
	private ButtonTemplate btnDownload = new ButtonTemplate("Tải xuống",FontAwesome.Solid.FILE_DOWNLOAD.create());
	private Span spDesr = new Span("*Click vào lập báo cáo để xem nội dung báo cáo");


	private BelongOrganizationModel belongOrganizationModel;
	public ReportFilterDocForm(BelongOrganizationModel belongOrganizationModel) {
		this.belongOrganizationModel = belongOrganizationModel;
		checkUiMobile();
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setWidthFull();
		vLayout.setWidthFull();
		vLayout.getStyle().setBoxShadow("rgba(0, 0, 0, 0.16) 0px 1px 4px").setPadding("11px 15px");
		this.add(vLayout);
		this.getStyle().setPadding("0");
		createLayout();
	}

	@Override
	public void configComponent() {
		btnCreateReport.addClickListener(e->{
			spDesr.setVisible(false);
			fireEvent(new ClickEvent(this,false));
			btnDownload.setEnabled(true);
		});

		startDate.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});

		startDate.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});

		cmbStatus.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});

		cmbUserOfOwner.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});
		
		cmbOwnerDoc.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});

		cmbClassifyTask.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});

		cmbLeaderTask.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});

	}

	public void loadData() {
		loadCmbStatus();
		loadCmbClassifyTask();
		loadCmbLeaderTask();
		initCategory();
		initCmbOwnerDoc();
		initCmbUserOfOwner();
	}

	private void createLayout() {
		vLayout.removeAll();
		FlexLayout hLayoutDate = new FlexLayout();
		Icon icon = FontAwesome.Solid.ARROW_RIGHT_LONG.create();
		icon.getStyle().setMarginTop("34px");
		icon.setSize("14px");

		startDate.setWidth("200px");
		startDate.addThemeVariants(DateTimePickerVariant.LUMO_SMALL);
		startDate.setLocale(LocalDateUtil.localeVietNam());
		startDate.setValue(LocalDateTime.now().minusMonths(1));

		endDate.setWidth("200px");
		endDate.addThemeVariants(DateTimePickerVariant.LUMO_SMALL);
		endDate.setLocale(LocalDateUtil.localeVietNam());
		endDate.setValue(LocalDateTime.now());

		cmbStatus.addThemeVariants(ComboBoxVariant.LUMO_SMALL);

		cmbOwnerDoc.setWidth("300px");
		cmbOwnerDoc.addThemeVariants(ComboBoxVariant.LUMO_SMALL);

		cmbCategory.addThemeVariants(ComboBoxVariant.LUMO_SMALL);

		cmbUserOfOwner.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbClassifyTask.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbLeaderTask.addThemeVariants(ComboBoxVariant.LUMO_SMALL);

		btnCreateReport.addThemeVariants(ButtonVariant.LUMO_SMALL);
		btnCreateReport.getStyle().setMarginTop("28px");

		btnDownload.getStyle().setMarginTop("28px");
		btnDownload.addThemeVariants(ButtonVariant.LUMO_SMALL);
		btnDownload.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
		btnDownload.setDownload();
		btnDownload.setEnabled(false);

		spDesr.getStyle().setMarginTop("32px").setFontWeight(600);


		hLayoutDate.add(startDate,icon,endDate,cmbCategory,cmbStatus,cmbOwnerDoc,cmbUserOfOwner,cmbClassifyTask,
				cmbLeaderTask,btnCreateReport,btnDownload,btnDownload.getAnchor(),spDesr);
		hLayoutDate.setWidthFull();
		hLayoutDate.setFlexWrap(FlexWrap.WRAP);
		hLayoutDate.setFlexDirection(FlexDirection.ROW);
		hLayoutDate.getStyle().set("gap", "5px");

		if(isUiMobile) {
			startDate.setWidth("49%");
			icon.setVisible(false);
			endDate.setWidth("49%");
			cmbStatus.setWidth("49%");
			cmbCategory.setWidth("49%");
			cmbOwnerDoc.setWidth("49%");
			cmbUserOfOwner.setWidth("49%");
			cmbClassifyTask.setWidth("49%");
			cmbLeaderTask.setWidth("49%");
		}


		vLayout.add(hLayoutDate);
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

	private void initCmbOwnerDoc() {
		List<Pair<String, String>> dataOwnerDoc = new ArrayList<Pair<String,String>>();
		ApiOrganizationModel currentOrg = getInfoOrg(belongOrganizationModel.getOrganizationId());
		
		
		if(currentOrg != null) {
			if(currentOrg.getLevel().getKey().equals("room")) {
				if(currentOrg.getParentId() != null) {
					ApiOrganizationModel parentOrg = getInfoOrg(currentOrg.getParentId());
					dataOwnerDoc.add(Pair.of(parentOrg.getId(),parentOrg.getName()));
					dataOwnerDoc.add(Pair.of("incChildOrgs","Tất cả ("+parentOrg.getName()+" và các đơn vị cấp dưới 1 cấp)"));
					dataOwnerDoc.add(Pair.of("incChildOrgsAndExcMyOrg","Tất cả đơn vị cấp dưới (Không có "+parentOrg.getName()+")"));
					List<ApiOrganizationModel> listSubOrg = getSubOrgs(parentOrg.getId());
					if(listSubOrg != null) {
						listSubOrg.forEach(model->{
							dataOwnerDoc.add(Pair.of(model.getId()," |-- "+model.getName()));
						});
					}
				}else {
					List<ApiOrganizationModel> listSubOrg = getSubOrgs(belongOrganizationModel.getOrganizationId());
					dataOwnerDoc.add(Pair.of(currentOrg.getId(),currentOrg.getName()));
					dataOwnerDoc.add(Pair.of("incChildOrgs","Tất cả (Đơn vị đang sử dụng và các đơn vị cấp dưới)"));
					dataOwnerDoc.add(Pair.of("incChildOrgsAndExcMyOrg","Tất cả đơn vị cấp dưới (Không có đơn vị đang sử dụng)"));
					if(listSubOrg != null) {
						listSubOrg.forEach(model->{
							dataOwnerDoc.add(Pair.of(model.getId()," |-- "+model.getName()));
						});
					}
				}
			}else {
				List<ApiOrganizationModel> listSubOrg = getSubOrgs(belongOrganizationModel.getOrganizationId());
				dataOwnerDoc.add(Pair.of(currentOrg.getId(),currentOrg.getName()));
				dataOwnerDoc.add(Pair.of("incChildOrgs","Tất cả (Đơn vị đang sử dụng và các đơn vị cấp dưới)"));
				dataOwnerDoc.add(Pair.of("incChildOrgsAndExcMyOrg","Tất cả đơn vị cấp dưới (Không có đơn vị đang sử dụng)"));
				if(listSubOrg != null) {
					listSubOrg.forEach(model->{
						dataOwnerDoc.add(Pair.of(model.getId()," |-- "+model.getName()));
					});
				}
			}
		}

		cmbOwnerDoc.setItems(dataOwnerDoc);
		cmbOwnerDoc.setItemLabelGenerator(Pair::getRight);
		cmbOwnerDoc.setValue(dataOwnerDoc.get(0));
		dataOwnerDoc.forEach(model->{
			if(model.getKey().equals(belongOrganizationModel.getOrganizationId())) {
				cmbOwnerDoc.setValue(model);
			}
		});
	}

	private void loadCmbStatus() {
		List<Pair<String, String>> listData = new ArrayList<Pair<String,String>>();
		listData.add(Pair.of(null,"Tất cả"));
		try {
			ApiResultResponse<List<ApiKeyValueModel>> getStatus = ApiDocService.getKeyValueStatus();
			if(getStatus.isSuccess()) {
				getStatus.getResult().forEach(model->{
					listData.add(Pair.of(model.getKey(),model.getName()));
				});
			}
		} catch (Exception e) {
		}
		
		cmbStatus.setItems(listData);
		cmbStatus.setItemLabelGenerator(Pair::getRight);
		cmbStatus.setValue(listData.get(0));
	}

	public void initCategory() {
		List<Pair<String, String>> listCategory = new ArrayList<Pair<String,String>>();
		listCategory.add(Pair.of(null,"Tất cả"));
		try {
			ApiResultResponse<List<ApiKeyValueModel>> data = ApiDocService.getKeyValueCategory();
			for(ApiKeyValueModel apiKeyValueModel :  data.getResult()) {
				listCategory.add(Pair.of(apiKeyValueModel.getKey(),apiKeyValueModel.getName()));
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		cmbCategory.setItems(listCategory);
		cmbCategory.setItemLabelGenerator(Pair::getRight);
		cmbCategory.setValue(listCategory.get(0));
	}


	private void initCmbUserOfOwner() {
		List<Pair<String, String>> listUserOfOwner = new ArrayList<Pair<String,String>>();
		listUserOfOwner.add(Pair.of(null,"Tất cả"));

		if(cmbOwnerDoc.getValue().getKey() != null) {
			ApiResultResponse<List<ApiUserGroupExpandModel>> getUser = ApiOrganizationService.getListUserOrganizationEx(cmbOwnerDoc.getValue().getKey());
			if(getUser.isSuccess()) {
				getUser.getResult().forEach(model->{
					listUserOfOwner.add(Pair.of(model.getUserId(),model.getFullName()));
				});
			}
		}

		cmbUserOfOwner.setItems(listUserOfOwner);
		cmbUserOfOwner.setItemLabelGenerator(Pair::getRight);
		cmbUserOfOwner.setValue(listUserOfOwner.get(0));
	}

	private void loadCmbClassifyTask() {
		listClassifyTask = new ArrayList<Pair<String,String>>();
		listClassifyTask.add(Pair.of(null,"Tất cả"));
		FilterClassifyLeaderModel filterClassifyLeaderModel = new FilterClassifyLeaderModel();
		filterClassifyLeaderModel.setLimit(0);
		filterClassifyLeaderModel.setSkip(0);
		filterClassifyLeaderModel.setActive(true);
		filterClassifyLeaderModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
		ApiResultResponse<List<ApiClassifyTaskModel>> data = ApiClassifyTaskService.getListClassify(filterClassifyLeaderModel);
		data.getResult().stream().forEach(model->{
			listClassifyTask.add(Pair.of(model.getId(),model.getName()));
		});
		cmbClassifyTask.setItems(listClassifyTask);
		cmbClassifyTask.setItemLabelGenerator(Pair::getRight);
		cmbClassifyTask.setValue(listClassifyTask.get(0));
	}

	private void loadCmbLeaderTask() {
		listLeaderTask = new ArrayList<Pair<String,String>>();
		listLeaderTask.add(Pair.of(null,"Tất cả"));
		FilterClassifyLeaderModel filterClassifyLeaderModel = new FilterClassifyLeaderModel();
		filterClassifyLeaderModel.setLimit(0);
		filterClassifyLeaderModel.setSkip(0);
		filterClassifyLeaderModel.setActive(true);
		filterClassifyLeaderModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
		ApiResultResponse<List<ApiLeaderApproveTaskModel>> data = ApiLeaderApproveTaskService.getListLeader(filterClassifyLeaderModel);
		data.getResult().stream().forEach(model->{
			listLeaderTask.add(Pair.of(model.getId(),model.getName()));
		});
		cmbLeaderTask.setItems(listLeaderTask);
		cmbLeaderTask.setItemLabelGenerator(Pair::getRight);
		cmbLeaderTask.setValue(listLeaderTask.get(0));
	}

	// Get list Org
	private List<ApiOrganizationModel> getSubOrgs(String idOrg){
		ApiResultResponse<List<ApiOrganizationModel>> getOrgs = ApiOrganizationService.getListOrganization(idOrg);
		if(getOrgs.isSuccess()) {
			return getOrgs.getResult();
		}
		return Collections.emptyList();
	}

	// Get info of One Org
	private ApiOrganizationModel getInfoOrg(String idOrg) {
		ApiResultResponse<ApiOrganizationModel> getOrg = ApiOrganizationService.getOneOrg(idOrg);
		if(getOrg.isSuccess()) {
			return getOrg.getResult();
		}

		return new ApiOrganizationModel();
	}

	public ApiFilterReportDocModel getSearch() {
		ApiFilterReportDocModel apiFilterReportDocModel = new ApiFilterReportDocModel();
		apiFilterReportDocModel.setFromDate(LocalDateUtil.localDateTimeToLong(startDate.getValue()));
		apiFilterReportDocModel.setToDate(LocalDateUtil.localDateTimeToLong(endDate.getValue()));
		
		BelongOrganizationModel viewParentOrg = SessionUtil.getParentBelongOrgModel();
		ApiOrganizationModel getCurrentOrg = getInfoOrg(belongOrganizationModel.getOrganizationId());
		if(viewParentOrg != null &&!getCurrentOrg.getParentId().equals(viewParentOrg.getOrganizationId())) {
			ApiOrganizationModel getParent = getInfoOrg(getCurrentOrg.getParentId());
			BelongOrganizationModel parentOrg = new BelongOrganizationModel();
			parentOrg.setOrganizationId(getParent.getId());
			parentOrg.setOrganizationName(getParent.getName());
			viewParentOrg = parentOrg;
		}else {
			viewParentOrg = null;
		}
		
		if(viewParentOrg != null && getCurrentOrg.getLevel().getKey().equals("room")) {
			if(cmbOwnerDoc.getValue() != null ) {
				if(cmbOwnerDoc.getValue().getKey().equals("incChildOrgs") || cmbOwnerDoc.getValue().getKey().equals("incChildOrgsAndExcMyOrg")) {
					apiFilterReportDocModel.setOrganizationId(viewParentOrg.getOrganizationId());
					apiFilterReportDocModel.setDataScopeType(cmbOwnerDoc.getValue().getKey());
				}else {
					apiFilterReportDocModel.setOrganizationId(cmbOwnerDoc.getValue().getKey());
				}
			}
		}else {
			if(cmbOwnerDoc.getValue() != null ) {
				if(cmbOwnerDoc.getValue().getKey().equals("incChildOrgs") || cmbOwnerDoc.getValue().getKey().equals("incChildOrgsAndExcMyOrg")) {
					apiFilterReportDocModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
					apiFilterReportDocModel.setDataScopeType(cmbOwnerDoc.getValue().getKey());
				}else {
					apiFilterReportDocModel.setOrganizationId(cmbOwnerDoc.getValue().getKey());
				}
			}
		}
		
		if(cmbUserOfOwner.getValue() != null && cmbUserOfOwner.getValue().getKey() != null) {
			apiFilterReportDocModel.setOrganizationUserId(cmbUserOfOwner.getValue().getKey());
		}
		
		
		apiFilterReportDocModel.setClassifyTaskId(cmbClassifyTask.getValue().getKey());
		apiFilterReportDocModel.setLeaderApproveTaskId(cmbLeaderTask.getValue().getKey());
		apiFilterReportDocModel.setStatus(cmbStatus.getValue().getKey());
		apiFilterReportDocModel.setSkip(0);
		apiFilterReportDocModel.setLimit(10);
		apiFilterReportDocModel.setCategory(cmbCategory.getValue().getKey());
		
		return apiFilterReportDocModel;
	}

	public ButtonTemplate getButtonDownLoad() {
		return this.btnDownload;
	}

	public List<Pair<String, String>> getListClass(){
		return this.listClassifyTask;
	}

	public List<Pair<String, String>> getListLeader(){
		return this.listLeaderTask;
	}

	public ComboBox<Pair<String, String>> getCmbStatus() {
		return cmbStatus;
	}

	public ComboBox<Pair<String, String>> getCmbOwnerDoc() {
		return cmbOwnerDoc;
	}

	public ComboBox<Pair<String, String>> getCmbUserGroup() {
		return cmbUserOfOwner;
	}

	public ComboBox<Pair<String, String>> getCmbClassifyTask() {
		return cmbClassifyTask;
	}

	public ComboBox<Pair<String, String>> getCmbLeaderTask() {
		return cmbLeaderTask;
	}



}
