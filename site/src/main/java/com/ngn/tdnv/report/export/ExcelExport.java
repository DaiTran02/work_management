package com.ngn.tdnv.report.export;

import java.io.ByteArrayInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ngn.utils.PropsUtil;
import com.vaadin.flow.server.StreamResource;

public class ExcelExport {
	private Logger log = LogManager.getLogger(this);
	protected XSSFWorkbook workbook;
	protected XSSFRow row;
	protected XSSFCell cell;
	protected CellCopyPolicy cellCopyPolicy = new CellCopyPolicy();

	protected String templatePath = "";

	public ExcelExport() {
		cellCopyPolicy.createBuilder().cellStyle(true);
		cellCopyPolicy.createBuilder().cellValue(false);
		try {
			templatePath = PropsUtil.getURITemplate();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public StreamResource getStreamResource(String filename, byte[] content) {
        return new StreamResource(filename,
                () -> new ByteArrayInputStream(content));
    }

}
