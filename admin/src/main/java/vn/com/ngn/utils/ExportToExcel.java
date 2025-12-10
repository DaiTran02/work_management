package vn.com.ngn.utils;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;

import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.vaadin.flow.server.StreamResource;

public class ExportToExcel {
	protected XSSFWorkbook workbook;
	protected XSSFRow row;
	protected XSSFCell cell;
	protected CellCopyPolicy cellCopyPolicy = new CellCopyPolicy();
	
	protected LocalDate startDate;
	protected LocalDate endDate;
	
	
	public ExportToExcel() {
		cellCopyPolicy.createBuilder().cellStyle(true);
		cellCopyPolicy.createBuilder().cellValue(false);
		
	}
	
	
	public StreamResource getStreamResource(String filename, byte[] content) {
        return new StreamResource(filename, ()-> new ByteArrayInputStream(content));
    }
	
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
}
