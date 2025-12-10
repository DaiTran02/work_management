package com.ngn.tdnv.report.export;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ngn.api.doc.ApiDocService;
import com.ngn.api.report.ApiFilterReportTaskKpiModel;
import com.ngn.api.report.ApiListTasksOwnerModel;
import com.ngn.api.report.ApiReportKpiModel;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tasks.ApiTaskService;
import com.ngn.api.utils.ApiKeyValueModel;
import com.ngn.utils.LocalDateUtil;
import com.vaadin.flow.server.StreamResource;

public class ReportTaskKpiExcel extends ExcelExport{
	private String template = templatePath+"thong-ke-nhiem-vu-kpi.xlsx";

	private ApiReportKpiModel reportKpiModel = new ApiReportKpiModel();
	private List<ApiListTasksOwnerModel> listData = new ArrayList<ApiListTasksOwnerModel>();
	private ApiFilterReportTaskKpiModel apiFilterReportTaskKpiModel = new ApiFilterReportTaskKpiModel();
	private int total = 0;

	private List<ApiKeyValueModel> listPriority = new ArrayList<ApiKeyValueModel>();
	private List<ApiKeyValueModel> listDataStatus = new ArrayList<ApiKeyValueModel>();

	public StreamResource createReport() throws Exception{
		getData();
		FileInputStream fileInputStream = new FileInputStream(template);
		workbook = new XSSFWorkbook(fileInputStream);
		createSheet();
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		workbook.write(byteOutputStream);
		workbook.close();

		return getStreamResource("Thống kê nhiệm vụ KPI-"+LocalDateUtil.dfDateTime.format(new Date().getTime())+".xlsx", byteOutputStream.toByteArray());
	}

	private void getData() {
		listPriority = new ArrayList<ApiKeyValueModel>();
		ApiResultResponse<List<ApiKeyValueModel>> dataOfPriority = ApiDocService.getPriority();
		listPriority.addAll(dataOfPriority.getResult());

		listDataStatus = new ArrayList<ApiKeyValueModel>();
		ApiResultResponse<List<ApiKeyValueModel>> listStatus = ApiTaskService.getStatus();
		listDataStatus.addAll(listStatus.getResult());

	}

	public void createSheet() throws Exception{
		XSSFSheet sheet = workbook.getSheetAt(0);
		row = sheet.getRow(1);

		cell = row.getCell(0);
		cell.setCellValue("Từ ngày: "+LocalDateUtil.dfDate.format(apiFilterReportTaskKpiModel.getFromDate()) + " -> Đến ngày: "+LocalDateUtil.dfDate.format(apiFilterReportTaskKpiModel.getToDate())
		+" Ngày lập: "+LocalDateUtil.dfDateTime.format(new Date().getTime()));

		row = sheet.getRow(2);
		cell = row.getCell(0);
		cell.setCellValue("Thông tin xét điểm");
		
		cell = row.getCell(5);
		cell.setCellValue("Tỉ lệ/100%");
		
		row = sheet.getRow(3);
		cell = row.getCell(0);
		cell.setCellValue("Nhiệm vụ đã hoàn thành: "+reportKpiModel.getTaskCompleted()+"/"+total+" nhiệm vụ.(a)");
		
		cell = row.getCell(5);
		cell.setCellValue(reportKpiModel.getMarkA()+"%");
		
		row = sheet.getRow(4);
		cell = row.getCell(0);
		cell.setCellValue("Nhiệm vụ đã hoàn thành và trong hạn xử lý: "+reportKpiModel.getTaskCompletedButNotThroughExpired()+"/"+reportKpiModel.getTaskCompleted()+" nhiệm vụ hoàn thành.(b)");
		
		cell = row.getCell(5);
		cell.setCellValue(reportKpiModel.getMarkB()+"%");
		
		row = sheet.getRow(5);
		cell = row.getCell(0);
		cell.setCellValue("Nhiệm vụ đã hoàn thành và được đánh giá trên 3 sao: "+reportKpiModel.getTaskIsRatedHigherThanThreeStars()+"/"+reportKpiModel.getTaskCompleted()+" nhiệm vụ hoàn thành.(c)");
		
		cell = row.getCell(5);
		cell.setCellValue(reportKpiModel.getMarkC()+"%");
		
		row = sheet.getRow(6);
		cell = row.getCell(0);
		cell.setCellValue("Tổng điểm = (a+b+c)/3");
		cell = row.getCell(5);
		cell.setCellValue(reportKpiModel.getTotalPercent()+"% ("+reportKpiModel.getTotalMark()+"/100)");
		
		
		row = sheet.getRow(7);
		cell = row.getCell(0);
		cell.setCellValue("Tổng số nhiệm vụ KPI đã giao: "+total);

		int indexRow = 9;
		int startDataRow = indexRow;
		XSSFRow rowTemplate = sheet.getRow(indexRow);

		int stt = 1;

		for(ApiListTasksOwnerModel tasksOwnerModel : listData) {
			if(indexRow == startDataRow) {
				row = rowTemplate;
				indexRow++;
			}else {
				row = sheet.createRow(indexRow++);
				row.copyRowFrom(rowTemplate, cellCopyPolicy);
			}
			row.setHeight((short)-1);
			cell = row.getCell(0);
			cell.setCellValue(stt);
			cell = row.getCell(1);
			cell.setCellValue(tasksOwnerModel.getDocNumberText());
			cell = row.getCell(2);
			cell.setCellValue(tasksOwnerModel.getDocSymbolText());
			cell = row.getCell(3);
			cell.setCellValue(tasksOwnerModel.getCreateTimeText());
			cell = row.getCell(4);
			cell.setCellValue(tasksOwnerModel.getEndTimeText());
			cell = row.getCell(5);
			cell.setCellValue(tasksOwnerModel.getTitle());
			cell = row.getCell(6);
			cell.setCellValue(tasksOwnerModel.getDescription());
			cell = row.getCell(7);
			cell.setCellValue(tasksOwnerModel.getOwner().getOrganizationName()+"("+tasksOwnerModel.getOwner().getOrganizationUserName()+")");
			cell = row.getCell(8);
			cell.setCellValue(tasksOwnerModel.getAssistant().getOrganizationName()+"("+tasksOwnerModel.getAssistant().getOrganizationUserName()+")");
			cell = row.getCell(9);
			cell.setCellValue(tasksOwnerModel.getAssigneeText());
			cell = row.getCell(10);
			cell.setCellValue(tasksOwnerModel.getSupportText());
			cell = row.getCell(11);
			cell.setCellValue(tasksOwnerModel.getFollowerText());
			cell = row.getCell(12);
			cell.setCellValue(checkStatus(tasksOwnerModel.getStatus()));
			cell = row.getCell(13);
			cell.setCellValue(getResult(tasksOwnerModel));
			cell = row.getCell(14);
			cell.setCellValue(checkPriority(tasksOwnerModel.getPriority()));

			stt++;
		}
	}
	
	private String getResult(ApiListTasksOwnerModel model) {
		if(model.getRating() != null) {
			return "Nhiệm vụ được đánh giá: "+model.getRating().getStar()+" sao / Nội dung: "+model.rating.getExplain();
		}else {
			if(model.getCompleted() != null) {
				return model.getCompleted().getCompletedStatus();
			}else {
				return "Nhiệm vụ đang trong quá trình thực hiện";
			}
		}
	}

	private String checkPriority(String priority) {
		for(ApiKeyValueModel apiKeyValueModel : listPriority) {
			if(apiKeyValueModel.getKey().equals(priority)) {
				return apiKeyValueModel.getName();
			}
		}

		return "";
	}

	private String checkStatus(String status) {
		for(ApiKeyValueModel kstatus : listDataStatus) {
			if(status.equals(kstatus.getKey())) {
				return kstatus.getName();
			}
		}
		return "";
	}
	

	public ApiReportKpiModel getReportKpiModel() {
		return reportKpiModel;
	}

	public void setReportKpiModel(ApiReportKpiModel reportKpiModel) {
		this.total = reportKpiModel.getListTasks().size();
		this.listData = reportKpiModel.getListTasks();
		this.reportKpiModel = reportKpiModel;
	}

	public void setDataFilter(ApiFilterReportTaskKpiModel apiFilterReportTaskKpiModel) {
		this.apiFilterReportTaskKpiModel = apiFilterReportTaskKpiModel;
	}
}
