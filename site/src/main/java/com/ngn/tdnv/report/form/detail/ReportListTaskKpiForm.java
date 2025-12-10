package com.ngn.tdnv.report.form.detail;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.flowingcode.vaadin.addons.fontawesome.FontAwesome.Solid.Icon;
import com.ngn.api.report.ApiFilterReportTaskKpiModel;
import com.ngn.api.report.ApiListTasksOwnerModel;
import com.ngn.api.report.ApiReportKpiModel;
import com.ngn.api.report.ApiReportService;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.interfaces.FormInterface;
import com.ngn.tdnv.report.export.ReportTaskKpiExcel;
import com.ngn.tdnv.task.enums.StatusTaskEnum;
import com.ngn.utils.LocalDateUtil;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ReportListTaskKpiForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private VerticalLayout vLayout = new VerticalLayout();
	private ReportFilterTaskKpiForm reportFilterTaskKpiForm = new ReportFilterTaskKpiForm();
	private Grid<ApiListTasksOwnerModel> grid = new Grid<ApiListTasksOwnerModel>(ApiListTasksOwnerModel.class,false);
	private List<ApiListTasksOwnerModel> listData = new ArrayList<ApiListTasksOwnerModel>();
	private int total = 0;
	private ApiReportKpiModel reportKpiModel = null;
	private ReportTaskKpiExcel reportTaskKpiExcel = new ReportTaskKpiExcel();
	
	public ReportListTaskKpiForm() {
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(reportFilterTaskKpiForm);
		this.add(vLayout);
		setUpGrid();
	}

	@Override
	public void configComponent() {
		reportFilterTaskKpiForm.addChangeListener(e->{
			loadData();
			createLayout();
		});
		
		
		reportFilterTaskKpiForm.getBtnDownload().addClickListener(e->{
			try {
				ApiFilterReportTaskKpiModel filterReportTaskKpiModel = new ApiFilterReportTaskKpiModel();
				filterReportTaskKpiModel = getSearch();
				
				reportTaskKpiExcel.setDataFilter(filterReportTaskKpiModel);
				reportTaskKpiExcel.setReportKpiModel(reportKpiModel);
				reportFilterTaskKpiForm.getBtnDownload().downLoad(reportTaskKpiExcel.createReport());
				
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		});
		
	}
	
	private void createLayout() {
		vLayout.removeAll();
		
		vLayout.addClassName("report-style");

		H1 header = new H1("Báo cáo KPI");
		header.getStyle().setMargin("auto");
		Span spTitle = new Span();
		String stringTitle = "Từ ngày: "+LocalDateUtil.dfDate.format(reportFilterTaskKpiForm.getSearch().getFromDate()) +" -> "
				+ "Đến ngày: "+ LocalDateUtil.dfDate.format(reportFilterTaskKpiForm.getSearch().getToDate()) + 
				" Ngày lập: " + LocalDateUtil.dfDateTime.format(LocalDateUtil.localDateTimeToLong(LocalDateTime.now()));
		spTitle.getStyle().setMargin("auto");
		spTitle.setText(stringTitle);
		
		
		
		String inforOrg = "";
		if(reportFilterTaskKpiForm.getCmbOrgDoTask().getValue().getKey().equals("incChildOrgs")) {
			inforOrg = "Toàn đơn vị (bao gồm các đơn vị/phòng trực thuộc";
		}else {
			inforOrg = reportFilterTaskKpiForm.getCmbOrgDoTask().getValue().getValue();
		}
		
		String inforUser = reportFilterTaskKpiForm.getCmbUserDoTask().getValue().getKey() == null ? "" : reportFilterTaskKpiForm.getCmbUserDoTask().getValue().getValue();
		
		
		String infor;
		if (inforUser.isEmpty()) {
		    infor = "Đơn vị được lập báo cáo: <b>" + inforOrg + "</b>";
		} else {
		    infor = "Cán bộ được lập báo cáo: <b>" + inforUser + "</b> thuộc đơn vị <b>" + inforOrg + "</b>";
		}

		// Sử dụng Vaadin Html component để render HTML
		Html htmlContent = new Html("<h5>" + infor + "</h5>");;

		Span spanFirst = new Span("I. Tóm tắt:");
		spanFirst.getStyle().setFontWeight(600);

		Span spanFirstTotal = new Span(" - Tổng số nhiệm vụ Kpi: "+total);
		Span spanMarkA = new Span("*Nhiệm vụ hoàn thành: "+reportKpiModel.getTaskCompleted()+"/"+total+" nhiệm vụ, chiếm: "+reportKpiModel.getMarkA()+"/100% ");
		
		Span spanMarkB = new Span("*Nhiệm vụ hoàn thành trong hạn: "
									+reportKpiModel.getTaskCompletedButNotThroughExpired()+"/"+reportKpiModel.getTaskCompleted()
									+" nhiệm vụ, chiếm: "+reportKpiModel.getMarkB()+"/100% ");
		
		Span spanMarkC = new Span("*Nhiệm vụ được đánh giá trên 3 sao: "
									+reportKpiModel.getTaskIsRatedHigherThanThreeStars()+"/"+reportKpiModel.getTaskCompleted()
									+" nhiệm vụ, chiếm: "+reportKpiModel.getMarkC()+"/100% ");
		H4 totalMark = new H4("*Tổng điểm: "+reportKpiModel.getTotalMark()+"/100");
		
		HorizontalLayout hLayoutFirst = new HorizontalLayout();
		hLayoutFirst.setWidthFull();
		hLayoutFirst.add(spanFirstTotal);
		
		VerticalLayout vLayoutMark = new VerticalLayout();
		hLayoutFirst.add(vLayoutMark);
		vLayoutMark.setWidth("80%");
		vLayoutMark.setPadding(false);
		vLayoutMark.getStyle().setPaddingLeft("20px");
		vLayoutMark.add(spanMarkA,spanMarkB,spanMarkC,totalMark);
		
		Span spanSeccond = new Span("II. Nội dung chi tiết:");
		spanSeccond.getStyle().setFontWeight(600);

		vLayout.add(header,spTitle,htmlContent,new Hr(),spanFirst,hLayoutFirst,spanSeccond,grid);
	}
	
	private void loadData() {
		listData.clear();
		ApiResultResponse<ApiReportKpiModel> data = ApiReportService.getReportKpi(getSearch());
		if(data.isSuccess()) {
			reportKpiModel = data.getResult();
			listData.addAll(data.getResult().getListTasks());
			total = listData.size();
		}
		grid.setItems(listData);
	}
	
	private ApiFilterReportTaskKpiModel getSearch() {
		ApiFilterReportTaskKpiModel apiFilterReportTaskKpiModel = new ApiFilterReportTaskKpiModel();
		apiFilterReportTaskKpiModel = reportFilterTaskKpiForm.getSearch();
		apiFilterReportTaskKpiModel.setIsKpi(true);
		return apiFilterReportTaskKpiModel;
	}
	
	private void setUpGrid() {
		grid.addColumn(ApiListTasksOwnerModel::getTitle).setHeader("Tên nhiệm vụ");
		grid.addColumn(ApiListTasksOwnerModel::getDescription).setHeader("Nội dung");
		grid.addColumn(model->{
			return StatusTaskEnum.toGetTitle(model.getState().toString());
		}).setHeader("Trạng thái");
		
		grid.addColumn(model->{
			return model.getOwner().getOrganizationName();
		}).setHeader("Xử lý");
		
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

}
