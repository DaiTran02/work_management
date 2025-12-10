package com.ngn.tdnv.report.form.detail;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.flowingcode.vaadin.addons.fontawesome.FontAwesome.Solid.Icon;
import com.ngn.api.report.ApiFilterReportTaskModel;
import com.ngn.api.report.ApiListTasksOwnerModel;
import com.ngn.api.report.ApiReportService;
import com.ngn.api.report.ApiReportSupportModel;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tasks.ApiTaskService;
import com.ngn.api.utils.ApiKeyValueModel;
import com.ngn.enums.DataOfEnum;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.tdnv.report.export.ReportTaskOwnerExcel;
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

public class ReportListTaskOwnerForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private boolean isUser = SessionUtil.checkDataOf() == null ? true : SessionUtil.checkDataOf().getKey().equals(DataOfEnum.TOCANHAN.getKey());
	private UserAuthenticationModel userAuthenticationModel = SessionUtil.getUser();

	private VerticalLayout vLayout = new VerticalLayout();

	private ReportFilterTasksOwnerForm reportFilterTasksOwnerForm = new ReportFilterTasksOwnerForm();
	@SuppressWarnings("unused")
	private BelongOrganizationModel belongOrganizationModel = SessionUtil.getOrg();
	private ReportTaskOwnerExcel reportTaskOwnerExcel = new ReportTaskOwnerExcel();
	private Grid<ApiListTasksOwnerModel> grid = new Grid<ApiListTasksOwnerModel>(ApiListTasksOwnerModel.class,false);

	private List<ApiKeyValueModel> listStatus = new ArrayList<ApiKeyValueModel>();

	private List<ApiListTasksOwnerModel> listData = new ArrayList<ApiListTasksOwnerModel>();
	private int total = 0;

	public ReportListTaskOwnerForm() {
		buildLayout();
		configComponent();
		getData();
	}

	@Override
	public void buildLayout() {
		vLayout.setSizeFull();
		this.add(reportFilterTasksOwnerForm,vLayout);
	}

	@Override
	public void configComponent() {
		reportFilterTasksOwnerForm.addChangeListener(e->{
			loadData();
		});

		reportFilterTasksOwnerForm.getBtnDownload().addClickListener(e->{
			try {

				ApiFilterReportTaskModel apiFilterReportTaskModel = getSearch();
				apiFilterReportTaskModel.setSkip(0);
				apiFilterReportTaskModel.setLimit(0);

				List<ApiListTasksOwnerModel> dataDowload = getDataDownload(apiFilterReportTaskModel);

				reportTaskOwnerExcel.setTotal(dataDowload.size());
				reportTaskOwnerExcel.setDataFilter(getSearch());
				reportTaskOwnerExcel.setData(dataDowload);
				reportFilterTasksOwnerForm.getBtnDownload().downLoad(reportTaskOwnerExcel.createReport());
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		});

	}

	private List<ApiListTasksOwnerModel> getDataDownload(ApiFilterReportTaskModel apiFilterReportTaskModel){
		List<ApiListTasksOwnerModel> listDataDownload = new ArrayList<ApiListTasksOwnerModel>();
		try {
			ApiResultResponse<List<ApiListTasksOwnerModel>> data = ApiReportService.getReportTaskOwner(apiFilterReportTaskModel);
			if(data.isSuccess()) {
				listDataDownload.addAll(data.getResult());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return listDataDownload;
	}

	public void loadData() {
		listData = new ArrayList<ApiListTasksOwnerModel>();
		System.out.println("Thời gian trước khi gọi api: "+LocalDateUtil.dfDateTime.format(System.currentTimeMillis()));
		try {
			ApiResultResponse<List<ApiListTasksOwnerModel>> data = ApiReportService.getReportTaskOwner(getSearch());
			if(data.isSuccess()) {
				total = data.getTotal();
				listData.addAll(data.getResult());
			}
			System.out.println("Thời gian sau khi gọi api: "+LocalDateUtil.dfDateTime.format(System.currentTimeMillis()));
		} catch (Exception e) {
			e.printStackTrace();
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
		vLayout.getStyle().setBoxShadow("rgba(0, 0, 0, 0.16) 0px 1px 4px");

		//This class in file report.css
		vLayout.addClassName("report-style");

		H1 header = new H1("Danh sách nhiệm vụ đã giao và các đơn vị trực thuộc");
		header.getStyle().setMargin("auto");
		Span spTitle = new Span();
		String stringTitle = "Từ ngày: "+LocalDateUtil.dfDate.format(reportFilterTasksOwnerForm.getSearch().getFromDate()) +" -> "
				+ "Đến ngày: "+ LocalDateUtil.dfDate.format(reportFilterTasksOwnerForm.getSearch().getToDate()) + 
				" Ngày lập: " + LocalDateUtil.dfDateTime.format(LocalDateUtil.localDateTimeToLong(LocalDateTime.now()));
		spTitle.getStyle().setMargin("auto");
		spTitle.setText(stringTitle);

		Component hLayoutComponent = layoutFilter();

		Span spanFirst = new Span("I. Tóm tắt:");
		spanFirst.getStyle().setFontWeight(600);

		Span spanFirstTotal = new Span(" - Tổng số nhiệm vụ đã giao: "+total);

		Span spanSeccond = new Span("II. Nội dung chi tiết:");
		spanSeccond.getStyle().setFontWeight(600);

		vLayout.add(header,spTitle,hLayoutComponent,new Hr(),spanFirst,spanFirstTotal,spanSeccond,createGrid());

		grid.setItems(listData);


	}
	//	int count = 1;
	//	private Component createLayout(List<ApiListTasksOwnerModel> listData) {
	//		VerticalLayout layoutHtml = new VerticalLayout();
	//		System.out.println("Thời gian trước khi render: "+LocalDateUtil.dfDateTime.format(System.currentTimeMillis()));
	//		StringBuilder displayData = new StringBuilder();
	//		
	//		count = 1;
	//		
	//		int numThreads = Math.min(listData.size(), Runtime.getRuntime().availableProcessors());
	//		numThreads = numThreads == 0 ? 1 :  Math.min(listData.size(), Runtime.getRuntime().availableProcessors());
	//		for(ApiListTasksOwnerModel apiListTasksOwnerModel : listData) {
	//			String summaryDoc = apiListTasksOwnerModel.getDocInfo() == null ? "" : "Trích yếu: "+apiListTasksOwnerModel.getDocInfo().getSummary()+" / ";
	//			displayData.append( "<tr>"
	//							+ "<td>"+count+"</td>"
	//							+ "<td>"+apiListTasksOwnerModel.getCreateTimeText()+"</td>"
	//							+ "<td>"+apiListTasksOwnerModel.getTitle()+"</td>"
	//							+ "<td>"+summaryDoc+ "Nội dung: "+apiListTasksOwnerModel.getDescription()+"</td>"
	//							+ "<td>"+apiListTasksOwnerModel.getAssignee().getOrganizationName()+"</td>"
	//							+ "<td>"+convertSupport(apiListTasksOwnerModel.getSupports())+"</td>"
	//							+ "<td>"+apiListTasksOwnerModel.getFollowerText()+"</td>"
	//							+ "<td>"+apiListTasksOwnerModel.getStartTimeText()+"</td>"
	//							+ "<td>"+apiListTasksOwnerModel.getEndTimeText()+"</td>"
	//							+ "<td>"+checkStatus(apiListTasksOwnerModel.getStatus())+"</td>"
	//							+ "<td>"+apiListTasksOwnerModel.getExplainProcess()+"</td>"
	//							+ "</tr>");
	//			count++;
	//		}
	//		
	//		if(total > 10) {
	//			displayData.append("<tr>"
	//					+ "<td colspan=11 style='text-align:center; color:blue;'><<- Còn tiếp, tải xuống để xem tiếp ->></td>"
	//					+ "</tr>");
	//		}
	//		System.out.println("Thời gian sau khi render: "+LocalDateUtil.dfDateTime.format(System.currentTimeMillis()));
	//		
	//		UI.getCurrent().access(()->{
	//			String display = "<table id='table_report'>"
	//					+ "<tr>"
	//					+ "<th>STT</th>"
	//					+ "<th style='width:75px'>Ngày ban hành</th>"
	//					+ "<th>Tiêu đề</th>"
	//					+ "<th>Trích yếu (từ văn bản) / Nội dung (từ nhiệm vụ)</th>"
	//					+ "<th>Xử lý</th>"
	//					+ "<th style='width:200px'>Phối hợp</th>"
	//					+ "<th style='width:200px'>Theo dõi</th>"
	//					+ "<th style='width:75px'>Ngày bắt đầu</th>"
	//					+ "<th style='width:75px'>Hạn xử lý</th>"
	//					+ "<th style='width:100px'>Tình trạng</th>"
	//					+ "<th style='width:100px'>Kết quả</th>"
	//					+ "</tr>"
	//					+displayData
	//					+ "</table>";
	//			
	//			Html htmlDisplay = new Html(display);
	//			layoutHtml.add(htmlDisplay);
	//		});
	//		return layoutHtml;
	//	}

	private Component createGrid() {
		grid = new Grid<ApiListTasksOwnerModel>(ApiListTasksOwnerModel.class,false);

		grid.addColumn(model->{
			return model.getCreateTimeText();
		}).setHeader("Ngày ban hành");

		grid.addColumn(ApiListTasksOwnerModel::getTitle).setHeader("Tiêu đề");
		grid.addColumn(ApiListTasksOwnerModel::getDescription).setHeader("Nội dung");
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

		if(reportFilterTasksOwnerForm.getCmbLeaderApproveTaskId().getValue().getKey() != null) {
			flexLayout.add(createLayoutFilter("Người kết luận:", reportFilterTasksOwnerForm.getCmbLeaderApproveTaskId().getValue().getValue()));
		}

		if(reportFilterTasksOwnerForm.getCmbClassifyTaskId().getValue().getKey() != null) {
			flexLayout.add(createLayoutFilter("Loại chỉ đạo:", reportFilterTasksOwnerForm.getCmbClassifyTaskId().getValue().getValue()));
		}

		if(reportFilterTasksOwnerForm.getCmbPriority().getValue().getKey() != null) {
			flexLayout.add(createLayoutFilter("Độ khẩn:", reportFilterTasksOwnerForm.getCmbPriority().getValue().getValue()));
		}

		if(reportFilterTasksOwnerForm.getCmbStatus().getValue().getKey() != null) {
			flexLayout.add(createLayoutFilter("Trạng thái:", reportFilterTasksOwnerForm.getCmbStatus().getValue().getValue()));
		}

		//		if(reportFilterTasksOwnerForm.getCmbOwnerOrgUserId().getValue().getKey() != null) {
		//			flexLayout.add(createLayoutFilter("Người giao:", reportFilterTasksOwnerForm.getCmbOwnerOrgUserId().getValue().getValue()));
		//		}

		if(reportFilterTasksOwnerForm.getCmbOwnerAssign().getValue().getKey() != null) {
			flexLayout.add(createLayoutFilter("Đơn vị giao: ", reportFilterTasksOwnerForm.getCmbOwnerAssign().getValue().getValue()));
		}
		
		if(isUser) {
			flexLayout.add(createLayoutFilter("Người giao nhiệm vụ: ", userAuthenticationModel.getFullName()));
		}

		if(reportFilterTasksOwnerForm.getCmbAssigneeOrgId().getValue().getKey() != null) {
			flexLayout.add(createLayoutFilter("Đơn vị xử lý: ", reportFilterTasksOwnerForm.getCmbAssigneeOrgId().getValue().getValue()));
		}

		if(reportFilterTasksOwnerForm.getCmbSupportOrgId().getValue().getKey() != null) {
			flexLayout.add(createLayoutFilter("Đơn vị phối hợp:", reportFilterTasksOwnerForm.getCmbSupportOrgId().getValue().getValue()));
		}

		if(reportFilterTasksOwnerForm.getCmbFollowerOrgId().getValue().getKey() != null) {
			flexLayout.add(createLayoutFilter("Theo dõi:", reportFilterTasksOwnerForm.getCmbFollowerOrgId().getValue().getValue()));
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
		ApiFilterReportTaskModel apiFilterReportTaskModel = reportFilterTasksOwnerForm.getSearch();
		apiFilterReportTaskModel.setSkip(0);
		apiFilterReportTaskModel.setLimit(10);
		return apiFilterReportTaskModel;
	}

}

