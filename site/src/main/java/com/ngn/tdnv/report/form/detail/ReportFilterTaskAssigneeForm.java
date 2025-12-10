package com.ngn.tdnv.report.form.detail;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.flowingcode.vaadin.addons.fontawesome.FontAwesome.Solid.Icon;
import com.ngn.api.doc.ApiDocService;
import com.ngn.api.organization.ApiOrganizationService;
import com.ngn.api.organization.ApiUserGroupExpandModel;
import com.ngn.api.report.ApiFilterReportTaskModel;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tasks.ApiTaskService;
import com.ngn.api.utils.ApiKeyValueModel;
import com.ngn.enums.DataOfEnum;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.UserAuthenticationModel;
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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class ReportFilterTaskAssigneeForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private boolean isUiMobile = false;
	private boolean isUser = SessionUtil.checkDataOf() == null ? true : SessionUtil.checkDataOf().getKey().equals(DataOfEnum.TOCANHAN.getKey());
	private UserAuthenticationModel userAuthenticationModel = SessionUtil.getUser();

	private FlexLayout hLayout = new FlexLayout();

	private DateTimePicker startDate = new DateTimePicker("Từ ngày");
	private DateTimePicker endDate = new DateTimePicker("Đến ngày");

	private ComboBox<Pair<String, String>> cmbPriority = new ComboBox<Pair<String,String>>("Độ khẩn");
	private ComboBox<Pair<String, String>> cmbStatus = new ComboBox<Pair<String,String>>("Trạng thái");
	private ComboBox<Pair<String, String>> cmbUserDoTask = new ComboBox<Pair<String,String>>("Người thực hiện nhiệm vụ");

	private ButtonTemplate btnCreateReport = new ButtonTemplate("Lập báo cáo",FontAwesome.Solid.FILE_SIGNATURE.create());
	private ButtonTemplate btnDownload = new ButtonTemplate("Tải xuống",FontAwesome.Solid.FILE_DOWNLOAD.create());

	private ButtonTemplate btnSearch = new ButtonTemplate(FontAwesome.Solid.SEARCH.create());
	private Span spDesr = new Span("*Click vào lập báo cáo để xem nội dung báo cáo");

	private BelongOrganizationModel belongOrganizationModel = SessionUtil.getOrg();

	public ReportFilterTaskAssigneeForm() {
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
		btnSearch.addClickListener(e->{
			fireEvent(new ClickEvent(this,false));
		});

		btnCreateReport.addClickListener(e->{
			spDesr.setVisible(false);
			fireEvent(new ClickEvent(this,false));
			btnDownload.setEnabled(true);
		});

		cmbUserDoTask.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});
		
		cmbPriority.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});
		
		cmbStatus.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});

		cmbUserDoTask.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});
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
		
		cmbPriority.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbStatus.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbUserDoTask.addThemeVariants(ComboBoxVariant.LUMO_SMALL);

		HorizontalLayout hLayoutSearch = new HorizontalLayout();

		btnSearch.getStyle().setMarginTop("28px");
		btnSearch.addThemeVariants(ButtonVariant.LUMO_SMALL);

		hLayoutSearch.add(btnSearch);


		btnCreateReport.addThemeVariants(ButtonVariant.LUMO_SMALL);
		btnCreateReport.getStyle().setMarginTop("28px");

		btnDownload.getStyle().setMarginTop("28px");
		btnDownload.addThemeVariants(ButtonVariant.LUMO_SMALL);
		btnDownload.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
		btnDownload.setDownload();
		btnDownload.setEnabled(false);
		
		spDesr.getStyle().setMarginTop("32px").setFontWeight(600);

		hLayout.removeAll();
		hLayout.setFlexWrap(FlexWrap.WRAP);
		hLayout.setFlexDirection(FlexDirection.ROW);
		hLayout.getStyle().set("gap", "3px");
		hLayout.add(startDate,icon,endDate,cmbPriority,cmbStatus,cmbUserDoTask,
				hLayoutSearch,btnCreateReport,btnDownload,btnDownload.getAnchor(),spDesr);
		
		if(isUiMobile) {
			startDate.setWidth("49%");
			icon.setVisible(false);
			endDate.setWidth("49%");
			cmbPriority.setWidth("49%");
			cmbStatus.setWidth("49%");
			cmbUserDoTask.setWidth("49%");
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

	private void loadData() {
		startDate.setLocale(LocalDateUtil.localeVietNam());
		startDate.setValue(LocalDateUtil.longToLocalDateTime(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getStartOfTheYear(SessionUtil.getYear()))));


		endDate.setValue(LocalDateUtil.longToLocalDateTime(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getEndtOfTheYear(SessionUtil.getYear()))));
		endDate.setLocale(LocalDateUtil.localeVietNam());

		loadCmbPriority();
		loadCmbStatus();
		loadCmbUserDoTask();
	}



	private void loadCmbPriority() {
		List<Pair<String, String>> listData = new ArrayList<Pair<String,String>>();
		listData.add(Pair.of(null,"Tất cả"));
		ApiResultResponse<List<ApiKeyValueModel>> dataPriority = ApiDocService.getPriority();
		if(dataPriority.isSuccess()) {
			dataPriority.getResult().stream().forEach(model->{
				listData.add(Pair.of(model.getKey(),model.getName()));
			});
		}

		cmbPriority.setItems(listData);
		cmbPriority.setItemLabelGenerator(Pair::getValue);
		cmbPriority.setValue(listData.get(0));
	}

	private void loadCmbStatus() {
		List<Pair<String, String>> listData = new ArrayList<Pair<String,String>>();
		listData.add(Pair.of(null,"Tất cả"));
		ApiResultResponse<List<ApiKeyValueModel>> dataStatus = ApiTaskService.getStatus();
		if(dataStatus.isSuccess()) {
			dataStatus.getResult().stream().forEach(model->{
				listData.add(Pair.of(model.getKey(),model.getName()));
			});
		}

		cmbStatus.setItems(listData);
		cmbStatus.setItemLabelGenerator(Pair::getValue);
		cmbStatus.setValue(listData.get(0));
	}
	
	private void loadCmbUserDoTask() {
		List<Pair<String, String>> listData = new ArrayList<Pair<String,String>>();
		listData.add(Pair.of(null,"Tất cả"));
		ApiResultResponse<List<ApiUserGroupExpandModel>> listUser = ApiOrganizationService.getListUserOrganizationEx(belongOrganizationModel.getOrganizationId());
		if(listUser.isSuccess()) {
			listUser.getResult().stream().forEach(model->{
				listData.add(Pair.of(model.getUserId(),model.getMoreInfo().getFullName()));
			});
		}
		
		cmbUserDoTask.setItems(listData);
		cmbUserDoTask.setItemLabelGenerator(Pair::getValue);
		cmbUserDoTask.setValue(listData.get(0));
		if(isUser) {
			try {
				cmbUserDoTask.setValue(listData.stream().filter(data->data.getKey() == null ? "nah".equals(userAuthenticationModel.getId())  
						: data.getKey().equals(userAuthenticationModel.getId())).findFirst().get());
				cmbUserDoTask.setReadOnly(true);
			} catch (Exception e) {
			}
		}
	}


	public ApiFilterReportTaskModel getSearch() {
		ApiFilterReportTaskModel apiFilterReportTaskModel = new ApiFilterReportTaskModel();
		apiFilterReportTaskModel.setFromDate(LocalDateUtil.localDateTimeToLong(startDate.getValue()));
		apiFilterReportTaskModel.setToDate(LocalDateUtil.localDateTimeToLong(endDate.getValue()));
		apiFilterReportTaskModel.setAssigneeOrganizationId(belongOrganizationModel.getOrganizationId());
		apiFilterReportTaskModel.setPriority(cmbPriority.getValue().getKey());
		apiFilterReportTaskModel.setStatus(cmbStatus.getValue().getKey());
		apiFilterReportTaskModel.setAssigneeOrganizationUserId(cmbUserDoTask.getValue().getKey());


		return apiFilterReportTaskModel;
	}

	public ButtonTemplate getBtnDownload() {
		return btnDownload;
	}

	public ComboBox<Pair<String, String>> getCmbPriority() {
		return cmbPriority;
	}

	public ComboBox<Pair<String, String>> getCmbStatus() {
		return cmbStatus;
	}

	public ComboBox<Pair<String, String>> getCmbUserDoTask() {
		return cmbUserDoTask;
	}
}
