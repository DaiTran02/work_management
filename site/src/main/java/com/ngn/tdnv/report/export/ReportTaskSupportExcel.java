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
import com.ngn.api.report.ApiListTaskSupportModel;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tasks.ApiTaskService;
import com.ngn.api.utils.ApiKeyValueModel;
import com.ngn.utils.LocalDateUtil;
import com.vaadin.flow.server.StreamResource;

public class ReportTaskSupportExcel extends ExcelExport{
	private String template = templatePath+"thong-ke-nhiem-vu.xlsx";

	private List<ApiListTaskSupportModel> listData = new ArrayList<ApiListTaskSupportModel>();
	private ApiFilterReportTaskModel apiFilterReportTaskModel = new ApiFilterReportTaskModel();
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

		return getStreamResource("Nhiệm vụ hỗ trợ-"+LocalDateUtil.dfDateTime.format(new Date().getTime())+".xlsx", byteOutputStream.toByteArray());
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
		cell.setCellValue("Tổng số nhiệm vụ hỗ trợ: "+total);

		int indexRow = 5;
		int startDataRow = indexRow;
		XSSFRow rowTemplate = sheet.getRow(indexRow);

		int stt = 1;

		for(ApiListTaskSupportModel taskAssigneeModel : listData) {
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
			cell.setCellValue(taskAssigneeModel.getDocNumberText());
			cell = row.getCell(2);
			cell.setCellValue(taskAssigneeModel.getDocSymbolText());
			cell = row.getCell(3);
			cell.setCellValue(taskAssigneeModel.getCreateTimeText());
			cell = row.getCell(4);
			cell.setCellValue(taskAssigneeModel.getEndTimeText());
			cell = row.getCell(5);
			cell.setCellValue(taskAssigneeModel.getTitle());
			cell = row.getCell(6);
			cell.setCellValue(taskAssigneeModel.getDescription());
			cell = row.getCell(7);
			cell.setCellValue(taskAssigneeModel.getOwner().getOrganizationName()+"("+taskAssigneeModel.getOwner().getOrganizationUserName()+")");
			cell = row.getCell(8);
			cell.setCellValue(taskAssigneeModel.getAssistant().getOrganizationName()+"("+taskAssigneeModel.getAssistant().getOrganizationUserName()+")");
			cell = row.getCell(9);
			cell.setCellValue(taskAssigneeModel.getAssigneeText());
			cell = row.getCell(10);
			cell.setCellValue(taskAssigneeModel.getSupportText());
			cell = row.getCell(11);
			cell.setCellValue(taskAssigneeModel.getFollowerText());
			cell = row.getCell(12);
			cell.setCellValue(checkStatus(taskAssigneeModel.getStatus()));
			cell = row.getCell(13);
			cell.setCellValue(getResult(taskAssigneeModel));
			cell = row.getCell(14);
			cell.setCellValue(checkPriority(taskAssigneeModel.getPriority()));

			stt++;
		}
	}
	
	private String getResult(ApiListTaskSupportModel model) {
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


	public void setData(List<ApiListTaskSupportModel> listData) {
		this.listData = listData;
	}

	public void setDataFilter(ApiFilterReportTaskModel apiFilterReportTaskModel) {
		this.apiFilterReportTaskModel = apiFilterReportTaskModel;
	}

	public void setTotal(int total) {
		this.total = total;
	}

}
