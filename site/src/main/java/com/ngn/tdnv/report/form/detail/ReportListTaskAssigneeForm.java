package com.ngn.tdnv.report.form.detail;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.flowingcode.vaadin.addons.fontawesome.FontAwesome.Solid.Icon;
import com.ngn.api.report.ApiFilterReportTaskModel;
import com.ngn.api.report.ApiListTaskAssigneeModel;
import com.ngn.api.report.ApiReportService;
import com.ngn.api.report.ApiReportSupportModel;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tasks.ApiTaskService;
import com.ngn.api.utils.ApiKeyValueModel;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.tdnv.report.export.ReportTaskAssigneeExcel;
import com.ngn.utils.LocalDateUtil;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexDirection;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ReportListTaskAssigneeForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	

	private VerticalLayout vLayout = new VerticalLayout();
	private Grid<ApiListTaskAssigneeModel> grid = new Grid<ApiListTaskAssigneeModel>(ApiListTaskAssigneeModel.class,false);
	private ReportFilterTaskAssigneeForm reportFilterTaskAssigneeForm = new ReportFilterTaskAssigneeForm();
	private List<ApiListTaskAssigneeModel> listData = new ArrayList<ApiListTaskAssigneeModel>();
	private ReportTaskAssigneeExcel reportTaskAssigneeExcel = new ReportTaskAssigneeExcel();
	private BelongOrganizationModel belongOrganizationModel = SessionUtil.getOrg();
	private List<ApiKeyValueModel> listStatus = new ArrayList<ApiKeyValueModel>();
	private int total = 0;
	
	public ReportListTaskAssigneeForm() {
		buildLayout();
		configComponent();
		getData();
	}
	
	@Override
	public void buildLayout() {
		this.setSizeFull();
		vLayout.setSizeFull();
		this.add(reportFilterTaskAssigneeForm,vLayout);
		
	}

	@Override
	public void configComponent() {
		reportFilterTaskAssigneeForm.getBtnDownload().addClickListener(e->{
			try {
				
				List<ApiListTaskAssigneeModel> listDownload = getListDownload();
				
				reportTaskAssigneeExcel.setTotal(listDownload.size());
				reportTaskAssigneeExcel.setDataFilter(getSearch());
				reportTaskAssigneeExcel.setData(listDownload);
				reportFilterTaskAssigneeForm.getBtnDownload().downLoad(reportTaskAssigneeExcel.createReport());
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		});
		
		reportFilterTaskAssigneeForm.addChangeListener(e->{
			loadData();
		});
		
	}
	
	private List<ApiListTaskAssigneeModel> getListDownload(){
		System.out.println("First: "+LocalDateUtil.longToLocalDateTime(System.currentTimeMillis()));
		List<ApiListTaskAssigneeModel> listDownload = new ArrayList<ApiListTaskAssigneeModel>();
		
		ApiFilterReportTaskModel apiFilterReportTaskModel = getSearch();
		apiFilterReportTaskModel.setSkip(0);
		apiFilterReportTaskModel.setLimit(0);
		
		ApiResultResponse<List<ApiListTaskAssigneeModel>> data = ApiReportService.getReportTaskAssignee(apiFilterReportTaskModel);
		
		if(data.isSuccess()) {
			listDownload.addAll(data.getResult());
		}
		
		System.out.println("Second: "+LocalDateUtil.longToLocalDateTime(System.currentTimeMillis()));
		
		return listDownload;
	}
	
	private void loadData() {
		listData = new ArrayList<ApiListTaskAssigneeModel>();
		ApiResultResponse<List<ApiListTaskAssigneeModel>> data = ApiReportService.getReportTaskAssignee(getSearch());
		if(data.isSuccess()) {
			total = data.getTotal();
			listData.addAll(data.getResult());
		}
		loadLayout();
	}
	
	private void getData() {
		listStatus = new ArrayList<ApiKeyValueModel>();
		ApiResultResponse<List<ApiKeyValueModel>> data = ApiTaskService.getStatus();
		listStatus.addAll(data.getResult());
	}
	
	private void loadLayout() {
		vLayout.removeAll();
		//This class in file report.css
		vLayout.addClassName("report-style");
		
		H1 header = new H1("Danh sách nhiệm vụ được giao thuộc "+belongOrganizationModel.getOrganizationName());
		header.getStyle().setMargin("auto");
		Span spTitle = new Span();
		String stringTitle = "Từ ngày: "+LocalDateUtil.dfDate.format(reportFilterTaskAssigneeForm.getSearch().getFromDate()) +" -> "
				+ "Đến ngày: "+ LocalDateUtil.dfDate.format(reportFilterTaskAssigneeForm.getSearch().getToDate()) + 
				" Ngày lập: " + LocalDateUtil.dfDateTime.format(LocalDateUtil.localDateTimeToLong(LocalDateTime.now()));
		spTitle.getStyle().setMargin("auto");
		spTitle.setText(stringTitle);
		
		Component cpnFilter = layoutFilter();

		Span spanFirst = new Span("I. Tóm tắt:");
		spanFirst.getStyle().setFontWeight(600);

		Span spanFirstTotal = new Span(" - Tổng số nhiệm vụ được giao: "+total);

		Span spanSeccond = new Span("II. Nội dung chi tiết:");
		spanSeccond.getStyle().setFontWeight(600);

		vLayout.add(header,spTitle,cpnFilter,new Hr(),spanFirst,spanFirstTotal,spanSeccond,createGrid());
		grid.setItems(listData);
	}
	
	private Component createGrid() {
		grid = new Grid<ApiListTaskAssigneeModel>(ApiListTaskAssigneeModel.class,false);
		
		grid.addColumn(model->{
			return model.getCreateTimeText();
		}).setHeader("Ngày ban hành");
		
		grid.addColumn(ApiListTaskAssigneeModel::getTitle).setHeader("Tiêu đề");
		grid.addColumn(ApiListTaskAssigneeModel::getDescription).setHeader("Nội dung");
		grid.addColumn(model->{
			return model.getOwner().getOrganizationName();
		}).setHeader("Xử lý");
		
		grid.addColumn(model->{
			return convertSupport(model.getSupports());
		}).setHeader("Phối hợp");
		
		grid.addColumn(model->{
			return model.getFollowerText();
		}).setHeader("Theo dõi");
		
		grid.addColumn(model->{
			return checkStatus(model.getStatus());
		}).setHeader("Trạng thái");
		
		grid.addComponentColumn(model->{
			VerticalLayout vLayoutResult = new VerticalLayout();
			if(model.getRating() != null) {
				HorizontalLayout hLayoutStars = new HorizontalLayout();
				for(int i = 1; i <= model.getRating().getStar(); i++) {
					Icon icon = FontAwesome.Solid.STAR.create();
					icon.getStyle().setColor("#ffce44");
					hLayoutStars.add(icon);
				}
				Span spDsr = new Span(model.getRating().getExplain());
				vLayoutResult.add(hLayoutStars,spDsr);
			}else {
				if(model.getCompleted() != null) {
					vLayoutResult.add(new Span(convertResult(model.getCompleted().getCompletedStatus())));
				}else {
					vLayoutResult.add(new Span("Nhiệm vụ đang trong quá trình thực hiện"));
				}
			}
			
			
			return vLayoutResult;
		}).setHeader("Kết quả gần nhất");
		
		grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
		
		return grid;
	}
	
	private String convertResult(String statusCompleted) {
		String result = "";
		switch(statusCompleted) {
		case "khonghan":{
			result = "Đã hoàn thành";
			break;
		}
		case "tronghan":{
			result = "Đã hoàn thành trong hạn";
			break;
		}
		case "quahan":{
			result = "Đã hoàn thành quá hạn";
			break;
		}
		}

		return result;
	}
	
	private String convertSupport(List<ApiReportSupportModel> list) {
		String nameSupport = "Không có";
		
		if(!list.isEmpty()) {
			nameSupport = "";
			for(ApiReportSupportModel apiReportSupportModel : list) {
				nameSupport += apiReportSupportModel.getOrganizationName()+",";
			}
		}
		
		
		if(nameSupport.endsWith(",")) {
			nameSupport = nameSupport.substring(0, nameSupport.length() -1);
		}
		
		return nameSupport;
	}
	
	private String checkStatus(String key) {
		String name = "Không có";
			for(ApiKeyValueModel apiKeyValueModel : listStatus) {
				if(apiKeyValueModel.getKey().equals(key)) {
					name = apiKeyValueModel.getName();
				}
			}
		return name;
	}
	
	private FlexLayout layoutFilter() {
		FlexLayout flexLayout = new FlexLayout();
		
		if(reportFilterTaskAssigneeForm.getCmbPriority().getValue().getKey() != null) {
			flexLayout.add(createLayoutFilter("Độ khẩn:", reportFilterTaskAssigneeForm.getCmbPriority().getValue().getValue()));
		}
		
		if(reportFilterTaskAssigneeForm.getCmbStatus().getValue().getKey() != null){
			flexLayout.add(createLayoutFilter("Trạng thái:", reportFilterTaskAssigneeForm.getCmbStatus().getValue().getValue()));
		}
		
		if(reportFilterTaskAssigneeForm.getCmbUserDoTask().getValue().getKey() != null) {
			flexLayout.add(createLayoutFilter("Người thực hiện nhiệm vụ:", reportFilterTaskAssigneeForm.getCmbUserDoTask().getValue().getValue()));
		}
		
		flexLayout.setFlexWrap(FlexWrap.WRAP);
		flexLayout.setFlexDirection(FlexDirection.ROW);
		flexLayout.getStyle().set("gap", "3px");
		
		return flexLayout;
	}
	
	private Component createLayoutFilter(String header,String value) {
		HorizontalLayout hLayout = new HorizontalLayout();
		
		Span spHeader = new Span(header);
		spHeader.getStyle().setFontWeight(600);
		
		Span spValue = new Span(value);
		
		hLayout.add(spHeader,spValue);
		
		hLayout.getStyle().setBackground("#06041629").setHeight("28px").setPadding("5px").setBorderRadius("20px").setBoxShadow("rgba(100, 100, 111, 0.2) 0px 7px 29px 0px");
		
		return hLayout;
	}
	
	private ApiFilterReportTaskModel getSearch() {
		ApiFilterReportTaskModel apiFilterReportTaskModel = reportFilterTaskAssigneeForm.getSearch();
		apiFilterReportTaskModel.setSkip(0);
		apiFilterReportTaskModel.setLimit(10);
		return apiFilterReportTaskModel;
	}

}
