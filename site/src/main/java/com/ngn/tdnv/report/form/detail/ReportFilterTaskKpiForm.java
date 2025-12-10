package com.ngn.tdnv.report.form.detail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.flowingcode.vaadin.addons.fontawesome.FontAwesome.Solid.Icon;
import com.ngn.api.organization.ApiOrganizationModel;
import com.ngn.api.organization.ApiOrganizationService;
import com.ngn.api.organization.ApiUserGroupExpandModel;
import com.ngn.api.report.ApiFilterReportTaskKpiModel;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
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
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexDirection;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;

public class ReportFilterTaskKpiForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private boolean isUiMobile = false;
	
	private BelongOrganizationModel belongOrganizationModel = SessionUtil.getOrg();
	private FlexLayout hLayout = new FlexLayout();
	private DateTimePicker startDate = new DateTimePicker("Từ ngày");
	private DateTimePicker endDate = new DateTimePicker("Đến ngày");
	private ComboBox<Pair<String, String>> cmbOrgDoTask = new ComboBox<Pair<String,String>>("Đơn vị thực hiện nhiệm vụ");
	private ComboBox<Pair<String, String>> cmbUserDoTask = new ComboBox<Pair<String,String>>("Người thực hiện nhiệm vụ");
	private ButtonTemplate btnCreateReport = new ButtonTemplate("Lập báo cáo",FontAwesome.Solid.FILE_SIGNATURE.create());
	private ButtonTemplate btnDownload = new ButtonTemplate("Tải xuống",FontAwesome.Solid.FILE_DOWNLOAD.create());
	public ReportFilterTaskKpiForm() {
		checkUiMobile();
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.add(hLayout);
		createLayout();
	}

	@Override
	public void configComponent() {
		cmbUserDoTask.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});
		
		cmbOrgDoTask.addValueChangeListener(e->{
			loadCmbUserDoTask();
		});
		
		btnCreateReport.addClickListener(e->{
			fireEvent(new ClickEvent(this,false));
			btnDownload.setEnabled(true);
		});
	}
	
	private void loadData() {
		startDate.setLocale(LocalDateUtil.localeVietNam());
		startDate.setValue(LocalDateUtil.longToLocalDateTime(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getStartOfTheYear(SessionUtil.getYear()))));
		startDate.setDatePickerI18n(LocalDateUtil.i18nVietNam());
		
	
		endDate.setValue(LocalDateUtil.longToLocalDateTime(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getEndtOfTheYear(SessionUtil.getYear()))));
		endDate.setLocale(LocalDateUtil.localeVietNam());
		endDate.setDatePickerI18n(LocalDateUtil.i18nVietNam());
		
		loadCmbOwner();
	}
	
	private void createLayout() {
		startDate.addThemeVariants(DateTimePickerVariant.LUMO_SMALL);
		startDate.setWidth("200px");
		startDate.setDatePickerI18n(LocalDateUtil.i18nVietNam());
		
		Icon icon = FontAwesome.Solid.ARROW_RIGHT_LONG.create();
		icon.getStyle().setMarginTop("34px");
		icon.setSize("14px");
		
		endDate.addThemeVariants(DateTimePickerVariant.LUMO_SMALL);
		endDate.setWidth("200px");
		endDate.setDatePickerI18n(LocalDateUtil.i18nVietNam());
		
		cmbOrgDoTask.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbOrgDoTask.getStyle().set("--vaadin-combo-box-overlay-width", "350px");
		
		
		cmbUserDoTask.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbUserDoTask.getStyle().set("--vaadin-combo-box-overlay-width", "350px");
		
		HorizontalLayout hLayoutSearch = new HorizontalLayout();

		btnCreateReport.addThemeVariants(ButtonVariant.LUMO_SMALL);
		btnCreateReport.getStyle().setMarginTop("28px");

		btnDownload.getStyle().setMarginTop("28px");
		btnDownload.addThemeVariants(ButtonVariant.LUMO_SMALL);
		btnDownload.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
		btnDownload.setDownload();
		btnDownload.setEnabled(false);
		

		hLayout.removeAll();
		hLayout.setFlexWrap(FlexWrap.WRAP);
		hLayout.setFlexDirection(FlexDirection.ROW);
		hLayout.getStyle().set("gap", "3px");
		hLayout.add(startDate,icon,endDate,cmbOrgDoTask,cmbUserDoTask,
				hLayoutSearch,btnCreateReport,btnDownload,btnDownload.getAnchor());
		
		if(isUiMobile) {
			startDate.setWidth("49%");
			icon.setVisible(false);
			endDate.setWidth("49%");
			cmbUserDoTask.setWidth("49%");
			cmbOrgDoTask.setWidth("49%");
		}
	}
	
	private void loadCmbOwner() {
		List<Pair<String, String>> listPairOwner = new ArrayList<Pair<String,String>>();
		listPairOwner.add(Pair.of(belongOrganizationModel.getOrganizationId(),belongOrganizationModel.getOrganizationName()));
		listPairOwner.add(Pair.of("incChildOrgs","Tất cả (Đơn vị đang sử dụng và các đơn vị cấp dưới)"));
//		listPairOwner.add(Pair.of("incChildOrgsAndExcMyOrg","Tất cả đơn vị cấp dưới (Không có đơn vị đang sử dụng)"));

		List<ApiOrganizationModel> listOrg = getSubOrgs(belongOrganizationModel.getOrganizationId());
		listOrg.forEach(model->{
			listPairOwner.add(Pair.of(model.getId()," -- "+model.getName()));
		});

		cmbOrgDoTask.setItems(listPairOwner);
		cmbOrgDoTask.setItemLabelGenerator(Pair::getRight);
		cmbOrgDoTask.setValue(listPairOwner.get(0));
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
	
	private void loadCmbUserDoTask() {
		List<Pair<String, String>> listData = new ArrayList<Pair<String,String>>();
		listData.add(Pair.of(null,"Tất cả"));
		ApiResultResponse<List<ApiUserGroupExpandModel>> listUser = ApiOrganizationService.getListUserOrganizationEx(cmbOrgDoTask.getValue().getKey());
		if(listUser.isSuccess()) {
			listUser.getResult().stream().forEach(model->{
				listData.add(Pair.of(model.getUserId(),model.getMoreInfo().getFullName()));
			});
		}
		
		cmbUserDoTask.setItems(listData);
		cmbUserDoTask.setItemLabelGenerator(Pair::getValue);
		cmbUserDoTask.setValue(listData.get(0));
	}
	
	// Get list Org
	private List<ApiOrganizationModel> getSubOrgs(String idOrg){
		ApiResultResponse<List<ApiOrganizationModel>> getOrgs = ApiOrganizationService.getListOrganization(idOrg);
		if(getOrgs.isSuccess()) {
			return getOrgs.getResult();
		}
		return Collections.emptyList();
	}
	
	public ApiFilterReportTaskKpiModel getSearch() {
		ApiFilterReportTaskKpiModel apiFilterReportTaskKpiModel = new ApiFilterReportTaskKpiModel();
		
		apiFilterReportTaskKpiModel.setFromDate(LocalDateUtil.localDateTimeToLong(startDate.getValue()));
		apiFilterReportTaskKpiModel.setToDate(LocalDateUtil.localDateTimeToLong(endDate.getValue()));
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
			if(cmbOrgDoTask.getValue() != null && cmbOrgDoTask.getValue().getKey() != null) {
				if(cmbOrgDoTask.getValue().getKey().equals("incChildOrgs") || cmbOrgDoTask.getValue().getKey().equals("incChildOrgsAndExcMyOrg")) {
					apiFilterReportTaskKpiModel.setAssigneeOrganizationId(viewParentOrg.getOrganizationId());
					apiFilterReportTaskKpiModel.setDataScopeType(cmbOrgDoTask.getValue().getKey());
				}else {
					apiFilterReportTaskKpiModel.setAssigneeOrganizationId(cmbOrgDoTask.getValue().getKey());
				}
			}
		}else {
			if(cmbOrgDoTask.getValue().getKey().equals("incChildOrgs") || cmbOrgDoTask.getValue().getKey().equals("incChildOrgsAndExcMyOrg")) {
				apiFilterReportTaskKpiModel.setAssigneeOrganizationId(belongOrganizationModel.getOrganizationId());
				apiFilterReportTaskKpiModel.setDataScopeType(cmbOrgDoTask.getValue().getKey());
			}else {
				apiFilterReportTaskKpiModel.setAssigneeOrganizationId(cmbOrgDoTask.getValue().getKey());
			}
		}
		
		apiFilterReportTaskKpiModel.setAssigneeOrganizationUserId(cmbUserDoTask.getValue().getKey());
		
		return apiFilterReportTaskKpiModel;
	}
	
	// Get info of One Org
	private ApiOrganizationModel getInfoOrg(String idOrg) {
		ApiResultResponse<ApiOrganizationModel> getOrg = ApiOrganizationService.getOneOrg(idOrg);
		if(getOrg.isSuccess()) {
			return getOrg.getResult();
		}
		
		return new ApiOrganizationModel();
	}

	public ComboBox<Pair<String, String>> getCmbOrgDoTask() {
		return cmbOrgDoTask;
	}

	public ComboBox<Pair<String, String>> getCmbUserDoTask() {
		return cmbUserDoTask;
	}

	public ButtonTemplate getBtnCreateReport() {
		return btnCreateReport;
	}

	public void setBtnCreateReport(ButtonTemplate btnCreateReport) {
		this.btnCreateReport = btnCreateReport;
	}

	public ButtonTemplate getBtnDownload() {
		return btnDownload;
	}

	public void setBtnDownload(ButtonTemplate btnDownload) {
		this.btnDownload = btnDownload;
	}

}
