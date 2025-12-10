package com.ngn.tdnv.report.export;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ngn.api.report.ApiFilterReportDocModel;
import com.ngn.tdnv.report.models.ReportDocModel;
import com.ngn.utils.LocalDateUtil;
import com.vaadin.flow.server.StreamResource;

public class ReportDocExcel extends ExcelExport{
	private String template = templatePath+"vanban.xlsx";
	
	private List<ReportDocModel> listData = new ArrayList<ReportDocModel>();
	private ApiFilterReportDocModel apiFilterReportDocModel = new ApiFilterReportDocModel();
	private int total = 0;

	public StreamResource createReport() throws Exception{
		FileInputStream fileInputStream = new FileInputStream(template);
		workbook = new XSSFWorkbook(fileInputStream);
		createSheet();
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		workbook.write(byteOutputStream);
		workbook.close();

		return getStreamResource("Văn-Bản-"+LocalDateUtil.dfDateTime.format(new Date().getTime())+".xlsx", byteOutputStream.toByteArray());
	}
	
	public void createSheet() throws Exception{
		XSSFSheet sheet = workbook.getSheetAt(0);
		
		row = sheet.getRow(1);
		
		cell = row.getCell(0);
		cell.setCellValue("Từ ngày: "+LocalDateUtil.dfDate.format(apiFilterReportDocModel.getFromDate()) + " -> Đến ngày: "+LocalDateUtil.dfDate.format(apiFilterReportDocModel.getToDate())
						 +" Ngày lập: "+LocalDateUtil.dfDateTime.format(new Date().getTime()));
		
		row = sheet.getRow(3);
		cell = row.getCell(0);
		cell.setCellValue("Tổng số văn bản: "+total);
		
		int indexRow = 5;
		int startDataRow = indexRow;
		XSSFRow rowTemplate = sheet.getRow(indexRow);
		
		int stt = 1;
		for(ReportDocModel reportDocModel : listData) {
			
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
			cell.setCellValue(reportDocModel.getNumber());
			cell = row.getCell(2);
			cell.setCellValue(reportDocModel.getSymbol());
			cell = row.getCell(3);
			cell.setCellValue(reportDocModel.getRegDayText());
			cell = row.getCell(4);
			cell.setCellValue(reportDocModel.getCreateTimeText());
			cell = row.getCell(5);
			cell.setCellValue(reportDocModel.getSummary());
			cell = row.getCell(6);
			cell.setCellValue(reportDocModel.getOrgCreateName());
			cell = row.getCell(7);
			cell.setCellValue(reportDocModel.getSignerName());
			cell = row.getCell(8);
			cell.setCellValue(reportDocModel.getStatus().getName());
			cell = row.getCell(9);
			cell.setCellValue(reportDocModel.getSignerPosition());
			cell = row.getCell(10);
			cell.setCellValue(reportDocModel.getCreatorName());
		}
	}
	
	public void setData(List<ReportDocModel> listData) {
		this.listData = listData;
	}
	
	public void setDataFilter(ApiFilterReportDocModel apiFilterReportDocModel) {
		this.apiFilterReportDocModel = apiFilterReportDocModel;
	}
	
	public void setTotal(int total) {
		this.total = total;
	}

}
