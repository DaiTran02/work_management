package com.ngn.tdnv.report.form.detail;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ngn.api.report.ApiFilterReportDocModel;
import com.ngn.api.report.ApiReportDocModel;
import com.ngn.api.report.ApiReportService;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.interfaces.FormInterface;
import com.ngn.tdnv.report.export.ReportDocExcel;
import com.ngn.tdnv.report.models.ReportDocModel;
import com.ngn.utils.LocalDateUtil;
import com.ngn.utils.SessionUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexDirection;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ReportDocForm extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;
	private ReportDocExcel reportDocExcel = new ReportDocExcel();
	private VerticalLayout vLayout = new VerticalLayout();

	private ReportFilterDocForm reportFilterDocForm = new ReportFilterDocForm(SessionUtil.getOrg());
	private Grid<ReportDocModel> grid = new Grid<ReportDocModel>(ReportDocModel.class,false);

	private List<ReportDocModel> listReportDocs = new ArrayList<ReportDocModel>();
	private int total = 0;

	public ReportDocForm() {
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.setPadding(false);
		reportFilterDocForm.setPadding(false);
		vLayout.setSizeFull();
		this.add(reportFilterDocForm,vLayout);
	}

	@Override
	public void configComponent() {
		reportFilterDocForm.addChangeListener(e->{
			loadData();
		});

		reportFilterDocForm.getButtonDownLoad().addClickListener(e->{
			try {
				ApiFilterReportDocModel apiFilterReportDocModel = reportFilterDocForm.getSearch();
				apiFilterReportDocModel.setSkip(0);
				apiFilterReportDocModel.setLimit(0);
				
				List<ReportDocModel> listDownload = getDataDownload(apiFilterReportDocModel);
				
				reportDocExcel.setDataFilter(apiFilterReportDocModel);
				reportDocExcel.setData(listDownload);
				reportDocExcel.setTotal(listDownload.size());
				reportFilterDocForm.getButtonDownLoad().downLoad(reportDocExcel.createReport());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
	}
	
	private List<ReportDocModel>  getDataDownload(ApiFilterReportDocModel apiFilterReportDocModel) {
		System.out.println("First: "+LocalDateUtil.longToLocalDateTime(System.currentTimeMillis()));
		List<ReportDocModel> listDownload = new ArrayList<ReportDocModel>();
		try {
			ApiResultResponse<List<ApiReportDocModel>> data = ApiReportService.getReportDoc(apiFilterReportDocModel);
			if(data.isSuccess()) {
				listDownload = data.getResult().stream().map(ReportDocModel::new).toList();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Second: "+LocalDateUtil.longToLocalDateTime(System.currentTimeMillis()));
		
		return listDownload;
	}

	private void loadData() {
		try {
			ApiResultResponse<List<ApiReportDocModel>> data = ApiReportService.getReportDoc(reportFilterDocForm.getSearch());
			if(data.isSuccess()) {
				total = data.getTotal();
				listReportDocs = data.getResult().stream().map(ReportDocModel::new).toList();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		loadLayout();
	}

	private void loadLayout() {
		vLayout.removeAll();
		//This class in file report.css
		vLayout.addClassName("report-style");
		
		H1 header = new H1("DANH SÁCH VĂN BẢN");
		header.getStyle().setMargin("auto");
		Span spTitle = new Span();
		String stringTitle = "Từ ngày: "+LocalDateUtil.dfDate.format(reportFilterDocForm.getSearch().getFromDate()) +" -> "
				+ "Đến ngày: "+ LocalDateUtil.dfDate.format(reportFilterDocForm.getSearch().getToDate()) + 
				" Ngày lập: " + LocalDateUtil.dfDateTime.format(LocalDateUtil.localDateTimeToLong(LocalDateTime.now()));
		spTitle.getStyle().setMargin("auto");
		spTitle.setText(stringTitle);
		
		Component cpmFilter = layoutFilter();

		Span spanFirst = new Span("I. Tóm tắt:");
		spanFirst.getStyle().setFontWeight(600);

		Span spanFirstTotal = new Span(" - Tổng số văn bản: "+total);

		Span spanSeccond = new Span("II. Nội dung chi tiết:");
		spanSeccond.getStyle().setFontWeight(600);

		vLayout.add(header,spTitle,cpmFilter,new Hr(),spanFirst,spanFirstTotal,spanSeccond,createGrid());
		grid.setItems(listReportDocs);

	}

//	private Component createLayout() {
//		VerticalLayout vLayoutComponent = new VerticalLayout();
//
//		String displayData = "";
//
//		int count = 1;
//		
//		if(listReportDocs.isEmpty()) {
//			displayData += "<tr>"
//					+ "<th colspan=10 class = 'header_group' >Không có văn bản nào</th>"
//					+ "</tr>";
//		}else {
//			for(ReportDocModel doclistModel : listReportDocs) {
//				displayData += "<tr>"
//						+ "<td>"+count +"</td>"
//						+"<td>"+doclistModel.getCategory().getName() +"</td>"
//						+"<td>"+doclistModel.getSecurity().getName() +"</td>"
//						+"<td>"+doclistModel.getNumber() +"</td>"
//						+"<td>"+doclistModel.getSymbol() +"</td>"
//						+"<td>"+doclistModel.getSummary() +"</td>"
//						+"<td>"+doclistModel.getSignerName() +"</td>"
//						+"<td>"+getNameClassify(doclistModel.getClassifyTaskId()) +"</td>"
//						+"<td>"+getNameLeader(doclistModel.getLeaderApproveTaskId()) +"</td>"
//						+"<td>"+doclistModel.getOwner().getOrganizationUserName()+"</td>"
//						+"<td>"+doclistModel.getCountTaskText()+"</td>"
//						+ "</tr>";
//				count++;
//			}
//			
//			if(total > 10) {
//				displayData += "<tr>"
//						+ "<td colspan=11 style='text-align:center; color:blue;'><<- Còn tiếp, tải xuống để xem tiếp ->></td>"
//						+ "</tr>";
//			}
//			
//		}
//		
//
//		String display = "<table id='table_report'>"
//				+ "<tr>"
//				+ "<th>STT</th>"
//				+ "<th>Loại văn bản</th>"
//				+ "<th>Độ mật</th>"
//				+ "<th>Số hiệu</th>"
//				+ "<th>Kí hiệu</th>"
//				+ "<th>Nội dung/Trích yếu</th>"
//				+ "<th>Người ký</th>"
//				+ "<th>Phân loại chỉ đạo</th>"
//				+ "<th>Người duyệt</th>"
//				+ "<th>Người soạn thảo</th>"
//				+ "<th>Tình trạng</th>"
//				+ "</tr>"
//				+ displayData
//				+ "</table>";
//
//		Html html = new Html(display);
//		vLayoutComponent.add(html);
//
//		return vLayoutComponent;
//	}
	
	private Component createGrid() {
		grid = new Grid<ReportDocModel>(ReportDocModel.class,false);
		
		grid.addColumn(model->{
			return model.getCategory().getName();
		}).setHeader("Loại văn bản");
		
		grid.addColumn(model->{
			return model.getSecurity().getName();
		}).setHeader("Độ mật");
		
		grid.addColumn(model->{
			return model.getNumber();
		}).setHeader("Số hiệu");
		
		grid.addColumn(model->{
			return model.getSymbol();
		}).setHeader("Ký hiệu");
		
		grid.addColumn(model->{
			return model.getSummary();
		}).setHeader("Trích yếu");
		
		grid.addColumn(model->{
			return model.getStatus().getName();
		}).setHeader("Trạng thái");
		
		return grid;
	}

//	private String getNameClassify(String idClassify) {
//		String name = "Chưa cập nhật";
//		for(Pair<String, String> p : reportFilterDocForm.getListClass()) {
//			if(p.getKey()!=null) {
//				if(p.getKey().equals(idClassify)) {
//					name = "";
//					name += p.getRight();
//				}
//			}
//		}
//
//		return name;
//
//	}
//
//	private String getNameLeader(String idLeader) {
//		String name = "Chưa cập nhật";
//		for(Pair<String, String> p : reportFilterDocForm.getListLeader()) {
//			if(p.getKey()!=null) {
//				if(p.getKey().equals(idLeader)) {
//					name = "";
//					name += p.getRight();
//				}
//			}
//		}
//
//		return name;
//	}
	
	private FlexLayout layoutFilter() {
		FlexLayout flexLayout = new FlexLayout();
		
		if(reportFilterDocForm.getCmbStatus().getValue().getKey() != null) {
			flexLayout.add(createLayoutFilter("Trạng thái:", reportFilterDocForm.getCmbStatus().getValue().getValue()));
		}
		
		if(reportFilterDocForm.getCmbOwnerDoc().getValue().getKey() != null) {
			flexLayout.add(createLayoutFilter("Văn bản thuộc đơn vị:", reportFilterDocForm.getCmbOwnerDoc().getValue().getValue()));
		}
		
		if(reportFilterDocForm.getCmbUserGroup().getValue() != null && reportFilterDocForm.getCmbUserGroup().getValue().getKey() != null) {
			flexLayout.add(createLayoutFilter("Người soạn thảo:", reportFilterDocForm.getCmbUserGroup().getValue().getValue()));
		}
		
		if(reportFilterDocForm.getCmbLeaderTask().getValue().getKey() != null) {
			flexLayout.add(createLayoutFilter("Phân loại chỉ đạo:", reportFilterDocForm.getCmbLeaderTask().getValue().getValue()));
		}
		
		if(reportFilterDocForm.getCmbClassifyTask().getValue().getKey() != null) {
			flexLayout.add(createLayoutFilter("Người duyệt:", reportFilterDocForm.getCmbClassifyTask().getValue().getValue()));
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
	

}



