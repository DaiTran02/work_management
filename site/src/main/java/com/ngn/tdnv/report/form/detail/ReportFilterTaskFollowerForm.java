package com.ngn.tdnv.report.form.detail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

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
import com.ngn.api.report.ApiFilterReportTaskModel;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tasks.ApiTaskService;
import com.ngn.api.utils.ApiKeyValueModel;
import com.ngn.enums.DataOfEnum;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.setting.leader_classify.model.FilterClassifyLeaderModel;
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

public class ReportFilterTaskFollowerForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private boolean isUiMobile = false;
	private boolean isUser = SessionUtil.checkDataOf() == null ? true : SessionUtil.checkDataOf().getKey().equals(DataOfEnum.TOCANHAN.getKey());
	private UserAuthenticationModel userAuthenticationModel = SessionUtil.getUser();
	
	private FlexLayout hLayout = new FlexLayout();

	private DateTimePicker startDate = new DateTimePicker("Từ ngày");
	private DateTimePicker endDate = new DateTimePicker("Đến ngày");

	private ComboBox<Pair<String, String>> cmbLeaderApproveTaskId = new ComboBox<Pair<String,String>>("Người kết luận");
	private ComboBox<Pair<String, String>> cmbClassifyTaskId = new ComboBox<Pair<String,String>>("Loại chỉ đạo");
	private ComboBox<Pair<String, String>> cmbPriority = new ComboBox<Pair<String,String>>("Độ khẩn");
	private ComboBox<Pair<String, String>> cmbStatus = new ComboBox<Pair<String,String>>("Trạng thái");
	private ComboBox<Pair<String, String>> cmbOwnerOrgUserId = new ComboBox<Pair<String,String>>("Người giao");
	private ComboBox<Pair<String, String>> cmbAssigneeOrgId = new ComboBox<Pair<String,String>>("Đơn vị xử lý");
	private ComboBox<Pair<String, String>> cmbSupportOrgId = new ComboBox<Pair<String,String>>("Đơn vị phối hợp");
	private ComboBox<Pair<String, String>> cmbFollowerOrgId = new ComboBox<Pair<String,String>>("Theo dõi");

	private ButtonTemplate btnCreateReport = new ButtonTemplate("Lập báo cáo",FontAwesome.Solid.FILE_SIGNATURE.create());
	private ButtonTemplate btnDownload = new ButtonTemplate("Tải xuống",FontAwesome.Solid.FILE_DOWNLOAD.create());

	private ButtonTemplate btnSearch = new ButtonTemplate(FontAwesome.Solid.SEARCH.create());
	
	private Span spDesr = new Span("*Click vào lập báo cáo để xem nội dung báo cáo");

	private BelongOrganizationModel belongOrganizationModel = SessionUtil.getOrg();

	public ReportFilterTaskFollowerForm() {
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
		
		cmbFollowerOrgId.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});
		
		cmbFollowerOrgId.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});
		
		cmbOwnerOrgUserId.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});
		
		cmbAssigneeOrgId.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});
		
		cmbLeaderApproveTaskId.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});
		
		cmbClassifyTaskId.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});
		
		cmbPriority.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});
		
		cmbStatus.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});
		
		cmbSupportOrgId.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});
	}

	private void createLayout() {
		startDate.addThemeVariants(DateTimePickerVariant.LUMO_SMALL);
		startDate.setWidth("200px");
		
		Icon icon = FontAwesome.Solid.ARROW_RIGHT_LONG.create();
		icon.getStyle().setMarginTop("34px");
		icon.setSize("14px");
		
		endDate.addThemeVariants(DateTimePickerVariant.LUMO_SMALL);
		endDate.setWidth("200px");
		cmbLeaderApproveTaskId.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbClassifyTaskId.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbPriority.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbStatus.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbOwnerOrgUserId.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbAssigneeOrgId.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbSupportOrgId.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbFollowerOrgId.addThemeVariants(ComboBoxVariant.LUMO_SMALL);

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
		hLayout.add(startDate,icon,endDate,cmbLeaderApproveTaskId,cmbClassifyTaskId,cmbPriority,cmbStatus,
				cmbOwnerOrgUserId,cmbAssigneeOrgId,cmbSupportOrgId,cmbFollowerOrgId,hLayoutSearch,btnCreateReport,btnDownload,btnDownload.getAnchor(),spDesr);
		if(isUiMobile) {
			startDate.setWidth("49%");
			icon.setVisible(false);
			endDate.setWidth("49%");
			cmbLeaderApproveTaskId.setWidth("49%");
			cmbClassifyTaskId.setWidth("49%");
			cmbPriority.setWidth("49%");
			cmbStatus.setWidth("49%");
			cmbOwnerOrgUserId.setWidth("49%");
			cmbAssigneeOrgId.setWidth("49%");
			cmbSupportOrgId.setWidth("49%");
			cmbFollowerOrgId.setWidth("49%");
		}
		
		if(isUser) {
			cmbFollowerOrgId.setVisible(false);
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
		startDate.setDatePickerI18n(LocalDateUtil.i18nVietNam());
		


		endDate.setValue(LocalDateUtil.longToLocalDateTime(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getEndtOfTheYear(SessionUtil.getYear()))));
		endDate.setLocale(LocalDateUtil.localeVietNam());
		endDate.setDatePickerI18n(LocalDateUtil.i18nVietNam());

		loadCmbLeaderTask();
		loadCmbClassify();
		loadCmbPriority();
		loadCmbStatus();
		loadCmbOwnsUser();
		loadCmdAssignee();
		loadCmbSupport();
		loadCmbFollow();
	}

	private void loadCmbLeaderTask() {
		List<Pair<String, String>> listData = new ArrayList<Pair<String,String>>();
		listData.add(Pair.of(null,"Tất cả"));
		FilterClassifyLeaderModel filterClassifyLeaderModel = new FilterClassifyLeaderModel();
		filterClassifyLeaderModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
		filterClassifyLeaderModel.setSkip(0);
		filterClassifyLeaderModel.setLimit(1000000);
		ApiResultResponse<List<ApiLeaderApproveTaskModel>> data = ApiLeaderApproveTaskService.getListLeader(filterClassifyLeaderModel);
		if(data.isSuccess()) {
			data.getResult().stream().forEach(model->{
				listData.add(Pair.of(model.getId(),model.getName()));
			});
		}

		cmbLeaderApproveTaskId.setItems(listData);
		cmbLeaderApproveTaskId.setItemLabelGenerator(Pair::getRight);
		cmbLeaderApproveTaskId.setValue(listData.get(0));
	}

	private void loadCmbClassify() {
		List<Pair<String, String>> listData = new ArrayList<Pair<String,String>>();
		listData.add(Pair.of(null,"Tất cả"));
		FilterClassifyLeaderModel filterClassifyLeaderModel = new FilterClassifyLeaderModel();
		filterClassifyLeaderModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
		filterClassifyLeaderModel.setSkip(0);
		filterClassifyLeaderModel.setLimit(100000);
		ApiResultResponse<List<ApiClassifyTaskModel>> data = ApiClassifyTaskService.getListClassify(filterClassifyLeaderModel);
		if(data.isSuccess()) {
			data.getResult().stream().forEach(model->{
				listData.add(Pair.of(model.getId(),model.getName()));
			});
		}

		cmbClassifyTaskId.setItems(listData);
		cmbClassifyTaskId.setItemLabelGenerator(Pair::getRight);
		cmbClassifyTaskId.setValue(listData.get(0));
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

	private void loadCmbOwnsUser() {
		List<Pair<String, String>> listData = new ArrayList<Pair<String,String>>();
		listData.add(Pair.of(null,"Tất cả"));
		ApiResultResponse<List<ApiUserGroupExpandModel>> listUser = ApiOrganizationService.getListUserOrganizationEx(belongOrganizationModel.getOrganizationId());
		if(listUser.isSuccess()) {
			listUser.getResult().stream().forEach(model->{
				listData.add(Pair.of(model.getUserId(),model.getMoreInfo().getFullName()));
			});
		}

		cmbOwnerOrgUserId.setItems(listData);
		cmbOwnerOrgUserId.setItemLabelGenerator(Pair::getValue);
		cmbOwnerOrgUserId.setValue(listData.get(0));

	}

	private void loadCmdAssignee() {
		List<Pair<String, String>> listAssignee = new ArrayList<Pair<String,String>>();
		listAssignee.add(Pair.of(null,"Tất cả"));
		
		ApiOrganizationModel infoCurrentOrg = getInfoOrg(belongOrganizationModel.getOrganizationId());
		// Các đơn vị ngang cấp
		List<ApiOrganizationModel> listOrgSame = getSubOrgs(infoCurrentOrg.getParentId());
		listOrgSame.forEach(model->{
			if(model.getId().equals(belongOrganizationModel.getOrganizationId())) {
				listAssignee.add(Pair.of(model.getId(),model.getName()));
			}else {
				listAssignee.add(Pair.of(model.getId(),model.getName() + " ( Đơn vị cùng cấp )"));
			}
			
		});


		// Các đơn vị cấp dưới của đơn vị hiện tại
		List<ApiOrganizationModel> listOrg = getSubOrgs(belongOrganizationModel.getOrganizationId());

		listOrg.stream().forEach(model->{
			listAssignee.add(Pair.of(model.getId(), " -- "+model.getName()));
		});

		cmbAssigneeOrgId.setItems(listAssignee);
		cmbAssigneeOrgId.setItemLabelGenerator(Pair::getRight);
		if(!listAssignee.isEmpty()) {
			cmbAssigneeOrgId.setValue(listAssignee.get(0));
		}

	}
	
	
	private void loadCmbSupport() {
		List<Pair<String, String>> listPairSupport = new ArrayList<Pair<String,String>>();
		listPairSupport.add(Pair.of(null,"Tất cả"));
		ApiOrganizationModel infoCurrentOrg = getInfoOrg(belongOrganizationModel.getOrganizationId());
		// Các đơn vị ngang cấp
		List<ApiOrganizationModel> listOrgSame = getSubOrgs(infoCurrentOrg.getParentId());
		listOrgSame.forEach(model->{
			if(model.getId().equals(belongOrganizationModel.getOrganizationId())) {
				listPairSupport.add(Pair.of(model.getId(),model.getName()));
			}else {
				listPairSupport.add(Pair.of(model.getId(),model.getName() + " (Đơn vị cùng cấp)"));
			}
		});

		
		// Các đơn vị cấp dưới của đơn vị hiện tại
		List<ApiOrganizationModel> listOrg = getSubOrgs(belongOrganizationModel.getOrganizationId());

		listOrg.stream().forEach(model->{
			listPairSupport.add(Pair.of(model.getId()," -- "+model.getName()));
		});

		cmbSupportOrgId.setItems(listPairSupport);
		cmbSupportOrgId.setItemLabelGenerator(Pair::getValue);
		cmbSupportOrgId.setValue(listPairSupport.get(0));
	}
	
	
	private void loadCmbFollow() {
		List<Pair<String, String>> listPairFollow = new ArrayList<Pair<String,String>>();
		listPairFollow.add(Pair.of(null,"Tất cả"));
		listPairFollow.add(Pair.of(belongOrganizationModel.getOrganizationId(),belongOrganizationModel.getOrganizationName()));
		
		List<ApiOrganizationModel> listOrgs = getSubOrgs(belongOrganizationModel.getOrganizationId());
		listOrgs.forEach(model->{
			listPairFollow.add(Pair.of(model.getId()," -- "+model.getName()));
		});

		cmbFollowerOrgId.setItems(listPairFollow);
		cmbFollowerOrgId.setItemLabelGenerator(Pair::getValue);
		cmbFollowerOrgId.setValue(listPairFollow.get(0));
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

	public ApiFilterReportTaskModel getSearch() {
		ApiFilterReportTaskModel apiFilterReportTaskModel = new ApiFilterReportTaskModel();
		apiFilterReportTaskModel.setFromDate(LocalDateUtil.localDateTimeToLong(startDate.getValue()));
		apiFilterReportTaskModel.setToDate(LocalDateUtil.localDateTimeToLong(endDate.getValue()));
		apiFilterReportTaskModel.setAssigneeOrganizationId(cmbAssigneeOrgId.getValue().getKey());
		apiFilterReportTaskModel.setClassifyTaskId(cmbClassifyTaskId.getValue().getKey());
		apiFilterReportTaskModel.setLeaderApproveTaskId(cmbLeaderApproveTaskId.getValue().getKey());
		apiFilterReportTaskModel.setFollowerOrganizationId(belongOrganizationModel.getOrganizationId());
		apiFilterReportTaskModel.setFollowerOrganizationGroupId(cmbFollowerOrgId.getValue().getKey());
		apiFilterReportTaskModel.setOwnerOrganizationId(belongOrganizationModel.getOrganizationId());
		apiFilterReportTaskModel.setOwnerOrganizationUserId(cmbOwnerOrgUserId.getValue().getKey());
		apiFilterReportTaskModel.setPriority(cmbPriority.getValue().getKey());
		apiFilterReportTaskModel.setStatus(cmbStatus.getValue().getKey());
		apiFilterReportTaskModel.setSupportOrganizationId(cmbSupportOrgId.getValue().getKey());
		if(isUser) {
			apiFilterReportTaskModel.setFollowerOrganizationId(null);
			apiFilterReportTaskModel.setFollowerOrganizationUserId(userAuthenticationModel.getId());
		}


		return apiFilterReportTaskModel;
	}

	public ButtonTemplate getBtnDownload() {
		return btnDownload;
	}
	
	public ComboBox<Pair<String, String>> getCmbGroup(){
		return this.cmbFollowerOrgId;
	}

	public ComboBox<Pair<String, String>> getCmbLeaderApproveTaskId() {
		return cmbLeaderApproveTaskId;
	}

	public ComboBox<Pair<String, String>> getCmbClassifyTaskId() {
		return cmbClassifyTaskId;
	}

	public ComboBox<Pair<String, String>> getCmbPriority() {
		return cmbPriority;
	}

	public ComboBox<Pair<String, String>> getCmbStatus() {
		return cmbStatus;
	}

	public ComboBox<Pair<String, String>> getCmbOwnerOrgUserId() {
		return cmbOwnerOrgUserId;
	}

	public ComboBox<Pair<String, String>> getCmbAssigneeOrgId() {
		return cmbAssigneeOrgId;
	}

	public ComboBox<Pair<String, String>> getCmbSupportOrgId() {
		return cmbSupportOrgId;
	}

	public ComboBox<Pair<String, String>> getCmbFollowerOrgId() {
		return cmbFollowerOrgId;
	}
}
