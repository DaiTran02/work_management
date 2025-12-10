package com.ngn.tdnv.report.form.detail;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.flowingcode.vaadin.addons.fontawesome.FontAwesome.Solid.Icon;
import com.ngn.api.report.ApiFilterReportTaskModel;
import com.ngn.api.report.ApiListTaskFollowerModel;
import com.ngn.api.report.ApiReportService;
import com.ngn.api.report.ApiReportSupportModel;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tasks.ApiTaskService;
import com.ngn.api.utils.ApiKeyValueModel;
import com.ngn.enums.DataOfEnum;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.tdnv.report.export.ReportTaskFollowerExcel;
import com.ngn.utils.LocalDateUtil;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ReportListTasksFollowerForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private boolean isUser = SessionUtil.checkDataOf() == null ? true : SessionUtil.checkDataOf().getKey().equals(DataOfEnum.TOCANHAN.getKey());
	private UserAuthenticationModel userAuthenticationModel = SessionUtil.getUser();

	private VerticalLayout vLayout = new VerticalLayout();
	private Grid<ApiListTaskFollowerModel> grid = new Grid<ApiListTaskFollowerModel>(ApiListTaskFollowerModel.class,false);
	private ReportFilterTaskFollowerForm reportFilterTaskFollowerForm = new ReportFilterTaskFollowerForm();
	private int total = 0;
	private List<ApiListTaskFollowerModel> listData = new ArrayList<ApiListTaskFollowerModel>();
	private ReportTaskFollowerExcel reportTaskFollowerExcel = new ReportTaskFollowerExcel();
	private BelongOrganizationModel belongOrganizationModel = SessionUtil.getOrg();
	private List<ApiKeyValueModel> listStatus = new ArrayList<ApiKeyValueModel>();
	
	public ReportListTasksFollowerForm() {
		buildLayout();
		configComponent();
		getData();
	}
	
	@Override
	public void buildLayout() {
		this.setSizeFull();
		vLayout.setSizeFull();
		this.add(reportFilterTaskFollowerForm,vLayout);
		
	}

	@Override
	public void configComponent() {
		reportFilterTaskFollowerForm.addChangeListener(e->{
			loadData();
		});
		
		reportFilterTaskFollowerForm.getBtnDownload().addClickListener(e->{
			try {
				List<ApiListTaskFollowerModel> dataDownload = getDataDownload();
				reportTaskFollowerExcel.setTotal(dataDownload.size());
				reportTaskFollowerExcel.setDataFilter(getSearch());
				reportTaskFollowerExcel.setData(dataDownload);
				reportFilterTaskFollowerForm.getBtnDownload().downLoad(reportTaskFollowerExcel.createReport());
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		});
	}
	
	private List<ApiListTaskFollowerModel> getDataDownload(){
		ApiFilterReportTaskModel apiFilterReportTaskModel = getSearch();
		apiFilterReportTaskModel.setSkip(0);
		apiFilterReportTaskModel.setLimit(0);
		
		ApiResultResponse<List<ApiListTaskFollowerModel>> data = ApiReportService.getReportTaskFollower(apiFilterReportTaskModel); 
		
		return data.getResult();
	}
	
	private void loadData() {
		listData = new ArrayList<ApiListTaskFollowerModel>();
		ApiResultResponse<List<ApiListTaskFollowerModel>> data = ApiReportService.getReportTaskFollower(getSearch());
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
		
		String stringHeader = "";
		if(reportFilterTaskFollowerForm.getCmbGroup().getValue().getKey() == null) {
			stringHeader = "Danh sách nhiệm vụ theo dõi của đơn vị "+belongOrganizationModel.getOrganizationName();
		}else {
			stringHeader = "Danh sách nhiệm vụ theo dõi thuộc tổ "+reportFilterTaskFollowerForm.getCmbGroup().getValue().getValue();
//			+" CỦA ĐƠN VỊ "+belongOrganizationModel.getOrganizationName()
		}
		
		if(isUser) {
			stringHeader = "Danh sách nhiệm vụ theo dõi của "+userAuthenticationModel.getFullName();
		}
		
		H1 header = new H1(stringHeader);
		header.getStyle().setMargin("auto");
		
		Span spTitle = new Span();
		String stringTitle = "Từ ngày: "+LocalDateUtil.dfDate.format(reportFilterTaskFollowerForm.getSearch().getFromDate()) +" -> "
				+ "Đến ngày: "+ LocalDateUtil.dfDate.format(reportFilterTaskFollowerForm.getSearch().getToDate()) + 
				" Ngày lập: " + LocalDateUtil.dfDateTime.format(LocalDateUtil.localDateTimeToLong(LocalDateTime.now()));
		spTitle.getStyle().setMargin("auto");
		spTitle.setText(stringTitle);
		
		HorizontalLayout componentFilter = createLayoutFilter();
		componentFilter.setWidthFull();
		

		Span spanFirst = new Span("I. Tóm tắt:");
		spanFirst.getStyle().setFontWeight(600);

		Span spanFirstTotal = new Span(" - Tổng số nhiệm vụ theo dõi: "+total);

		Span spanSeccond = new Span("II. Nội dung chi tiết:");
		spanSeccond.getStyle().setFontWeight(600);

		vLayout.add(header,spTitle,componentFilter,new Hr(),spanFirst,spanFirstTotal,spanSeccond,createGrid());
		grid.setItems(listData);
	}
	
//	private Component createLayout(List<ApiListTaskFollowerModel> listData) {
//		VerticalLayout layoutHtml = new VerticalLayout();
//		StringBuilder displayData = new StringBuilder();
//		int count = 1;
//		for(ApiListTaskFollowerModel apiListTaskFollowerModel : listData) {
//			String summaryDoc = apiListTaskFollowerModel.getDocInfo() == null ? "" : "Trích yếu: "+apiListTaskFollowerModel.getDocInfo().getSummary()+" / ";
//			displayData.append("<tr>"
//					+ "<td>"+count+"</td>"
//					+ "<td>"+apiListTaskFollowerModel.getCreateTimeText()+"</td>"
//					+ "<td>"+apiListTaskFollowerModel.getTitle()+"</td>"
//					+ "<td>"+summaryDoc + "Nội dung: "+apiListTaskFollowerModel.getDescription()+"</td>"
//					+ "<td>"+apiListTaskFollowerModel.getAssignee().getOrganizationName()+"</td>"
//					+ "<td>"+convertSupport(apiListTaskFollowerModel.getSupports())+"</td>"
//					+ "<td>"+apiListTaskFollowerModel.getStartTimeText()+"</td>"
//					+ "<td>"+apiListTaskFollowerModel.getEndTimeText()+"</td>"
//					+ "<td>"+checkStatus(apiListTaskFollowerModel.getStatus())+"</td>"
//					+ "<td>"+apiListTaskFollowerModel.getExplainProcess()+"</td>"
//					+ "</tr>");
//			
//			count++;
//		}
//		
//		if(total > 10) {
//			displayData.append("<tr>"
//					+ "<td colspan=11 style='text-align:center; color:blue;'><<- Còn tiếp, tải xuống để xem tiếp ->></td>"
//					+ "</tr>");
//		}
//		
//		
//		String display = "<table id='table_report'>"
//				+ "<tr>"
//				+ "<th>STT</th>"
//				+ "<th style='width:75px'>Ngày ban hành</th>"
//				+ "<th>Tiêu đề</th>"
//				+ "<th>Trích yếu (từ văn bản) / Nội dung (từ nhiệm vụ)</th>"
//				+ "<th>Xử lý</th>"
//				+ "<th style='width:200px'>Phối hợp</th>"
//				+ "<th style='width:75px'>Ngày bắt đầu</th>"
//				+ "<th style='width:75px'>Hạn xử lý</th>"
//				+ "<th style='width:100px'>Tình trạng xử lý</th>"
//				+ "<th style='width:100px'>Kết quả</th>"
//				+ "</tr>"
//				+displayData
//				+ "</table>";
//		
//		Html htmlDisplay = new Html(display);
//		layoutHtml.add(htmlDisplay);
//		return layoutHtml;
//	}
	
	private Component createGrid() {
		grid = new Grid<ApiListTaskFollowerModel>(ApiListTaskFollowerModel.class,false);
		
		grid.addColumn(model->{
			return model.getCreateTimeText();
		}).setHeader("Ngày ban hành");
		
		grid.addColumn(ApiListTaskFollowerModel::getTitle).setHeader("Tiêu đề");
		grid.addColumn(ApiListTaskFollowerModel::getDescription).setHeader("Nội dung");
		grid.addColumn(model->{
			return model.getOwner().getOrganizationName();
		}).setHeader("Xử lý");
		
		grid.addColumn(model->{
			return convertSupport(model.getSupports());
		}).setHeader("Phối hợp");
		
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
	
	private String checkStatus(String key) {
		String name = "Không có";
		for(ApiKeyValueModel apiKeyValueModel : listStatus) {
			if(apiKeyValueModel.getKey().equals(key)) {
				name = apiKeyValueModel.getName();
			}
		}
		return name;
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
	
	private HorizontalLayout createLayoutFilter() {
		HorizontalLayout hLayoutFilter = new HorizontalLayout();
		if(reportFilterTaskFollowerForm.getCmbLeaderApproveTaskId().getValue().getKey() != null) {
			hLayoutFilter.add(createLayoutFilter("Người kết luận:", reportFilterTaskFollowerForm.getCmbLeaderApproveTaskId().getValue().getValue()));
		}
		
		if(reportFilterTaskFollowerForm.getCmbClassifyTaskId().getValue().getKey() != null) {
			hLayoutFilter.add(createLayoutFilter("Loại chỉ đạo:", reportFilterTaskFollowerForm.getCmbClassifyTaskId().getValue().getValue()));
		}
		
		if(reportFilterTaskFollowerForm.getCmbPriority().getValue().getKey() != null) {
			hLayoutFilter.add(createLayoutFilter("Độ khẩn:", reportFilterTaskFollowerForm.getCmbPriority().getValue().getValue()));
		}
		
		if(reportFilterTaskFollowerForm.getCmbStatus().getValue().getKey() != null) {
			hLayoutFilter.add(createLayoutFilter("Trạng thái:", reportFilterTaskFollowerForm.getCmbStatus().getValue().getValue()));
		}
		
		if(reportFilterTaskFollowerForm.getCmbOwnerOrgUserId().getValue().getKey() != null) {
			hLayoutFilter.add(createLayoutFilter("Người giao: ", reportFilterTaskFollowerForm.getCmbOwnerOrgUserId().getValue().getValue()));
		}
		
		if(reportFilterTaskFollowerForm.getCmbAssigneeOrgId().getValue().getKey() != null) {
			hLayoutFilter.add(createLayoutFilter("Đơn vị xử lý:", reportFilterTaskFollowerForm.getCmbAssigneeOrgId().getValue().getValue()));
		}
		
		if(reportFilterTaskFollowerForm.getCmbSupportOrgId().getValue().getKey() != null) {
			hLayoutFilter.add(createLayoutFilter("Đơn vị phối hợp:", reportFilterTaskFollowerForm.getCmbSupportOrgId().getValue().getValue()));
		}
		
		return hLayoutFilter;
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
		ApiFilterReportTaskModel apiFilterReportTaskModel = reportFilterTaskFollowerForm.getSearch();
		apiFilterReportTaskModel.setSkip(0);
		apiFilterReportTaskModel.setLimit(10);
		return apiFilterReportTaskModel;
	}

}
