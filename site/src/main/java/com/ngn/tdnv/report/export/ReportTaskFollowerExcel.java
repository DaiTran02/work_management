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
import com.ngn.api.report.ApiFilterReportTaskModel;
import com.ngn.api.report.ApiListTaskFollowerModel;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tasks.ApiTaskService;
import com.ngn.api.utils.ApiKeyValueModel;
import com.ngn.utils.LocalDateUtil;
import com.vaadin.flow.server.StreamResource;

public class ReportTaskFollowerExcel extends ExcelExport{
	private String template = templatePath+"thong-ke-nhiem-vu.xlsx";

	private List<ApiListTaskFollowerModel> listData = new ArrayList<ApiListTaskFollowerModel>();
	private ApiFilterReportTaskModel apiFilterReportTaskModel = new ApiFilterReportTaskModel();
	private List<ApiKeyValueModel> listPriority = new ArrayList<ApiKeyValueModel>();
	private List<ApiKeyValueModel> listDataStatus = new ArrayList<ApiKeyValueModel>();
	private int total = 0;

	public StreamResource createReport() throws Exception{
		getData();
		FileInputStream fileInputStream = new FileInputStream(template);
		workbook = new XSSFWorkbook(fileInputStream);
		createSheet();
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		workbook.write(byteOutputStream);
		workbook.close();

		return getStreamResource("Nhiệm vụ theo dõi-"+LocalDateUtil.dfDateTime.format(new Date().getTime())+".xlsx", byteOutputStream.toByteArray());
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
		cell.setCellValue("Từ ngày: "+LocalDateUtil.dfDate.format(apiFilterReportTaskModel.getFromDate()) + " -> Đến ngày: "+LocalDateUtil.dfDate.format(apiFilterReportTaskModel.getToDate())
		+" Ngày lập: "+LocalDateUtil.dfDateTime.format(new Date().getTime()));

		row = sheet.getRow(3);
		cell = row.getCell(0);
		cell.setCellValue("Tổng số nhiệm vụ theo dõi: "+total);

		int indexRow = 5;
		int startDataRow = indexRow;
		XSSFRow rowTemplate = sheet.getRow(indexRow);

		int stt = 1;

		for(ApiListTaskFollowerModel taskFollowerModel : listData) {
			if(indexRow == startDataRow) {
				row = rowTemplate;
				indexRow++;
			}else {
				row = sheet.createRow(indexRow++);
				row.copyRowFrom(rowTemplate, cellCopyPolicy);
			}
			row.setHeight((short)-1);
			cell = row.getCell(0);
			cell.setCellValue(stt++);
			cell = row.getCell(1);
			cell.setCellValue(taskFollowerModel.getDocNumberText());
			cell = row.getCell(2);
			cell.setCellValue(taskFollowerModel.getDocSymbolText());
			cell = row.getCell(3);
			cell.setCellValue(taskFollowerModel.getCreateTimeText());
			cell = row.getCell(4);
			cell.setCellValue(taskFollowerModel.getEndTimeText());
			cell = row.getCell(5);
			cell.setCellValue(taskFollowerModel.getTitle());
			cell = row.getCell(6);
			cell.setCellValue(taskFollowerModel.getDescription());
			cell = row.getCell(7);
			cell.setCellValue(taskFollowerModel.getOwnerText());
			cell = row.getCell(8);
			cell.setCellValue(taskFollowerModel.getAssistantText());
			cell = row.getCell(9);
			cell.setCellValue(taskFollowerModel.getAssigneeText());
			cell = row.getCell(10);
			cell.setCellValue(taskFollowerModel.getSupportText());
			cell = row.getCell(11);
			cell.setCellValue(taskFollowerModel.getFollowerText());
			cell = row.getCell(12);
			cell.setCellValue(checkStatus(taskFollowerModel.getStatus()));
			cell = row.getCell(13);
			cell.setCellValue(getResult(taskFollowerModel));
			cell = row.getCell(14);
			cell.setCellValue(checkPriority(taskFollowerModel.getPriority()));
		}
	}
	
	private String getResult(ApiListTaskFollowerModel model) {
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

	public void setData(List<ApiListTaskFollowerModel> listData) {
		this.listData = listData;
	}

	public void setDataFilter(ApiFilterReportTaskModel apiFilterReportTaskModel) {
		this.apiFilterReportTaskModel = apiFilterReportTaskModel;
	}

	public void setTotal(int total) {
		this.total = total;
	}

}
