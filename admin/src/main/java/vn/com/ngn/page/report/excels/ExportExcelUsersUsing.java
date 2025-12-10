package vn.com.ngn.page.report.excels;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.impl.common.XPath.XPathCompileException;

import com.vaadin.flow.server.StreamResource;

import vn.com.ngn.page.report.models.UserOrganizationModel;
import vn.com.ngn.utils.ExportToExcel;

public class ExportExcelUsersUsing extends ExportToExcel{
	private List<UserOrganizationModel> listData = new ArrayList<UserOrganizationModel>();

	private String orgName;
	private boolean checkManyOrg = false;
	public ExportExcelUsersUsing(String orgName,boolean checkManyOrg) {
		this.orgName = orgName;
		this.checkManyOrg = checkManyOrg;
	}

	public StreamResource createReport() throws Exception{
		FileInputStream fileExcelStream;
		if(checkManyOrg == false) {
			fileExcelStream = new FileInputStream("./template/thong-ke-nguoi-dung-trong-don-vi.xlsx");
		}else {
			fileExcelStream = new FileInputStream("./template/thong-ke-nguoi-dung-many-org.xlsx");
		}
		workbook = new XSSFWorkbook(fileExcelStream);

		createSheet();

		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		workbook.write(byteOutputStream);

		workbook.close();
		return getStreamResource("Nguoi-dung.xlsx", byteOutputStream.toByteArray());
	}

	public void createSheet() throws XPathCompileException{
		XSSFSheet sheet = workbook.getSheetAt(0);

		row = sheet.getRow(1);
		cell = row.getCell(0);
		if(checkManyOrg == false) {
			cell.setCellValue("Đơn vị: "+orgName);
		}else {
			cell.setCellValue("Đơn vị: "+orgName + " và các đơn vị trực thuộc");
		}

		row = sheet.getRow(3);
		cell = row.getCell(0);
		cell.setCellValue("Tổng số người dùng: "+listData.size());

		int indexRow = 5;
		int startDataRow = indexRow;

		XSSFRow rowTemplate = sheet.getRow(indexRow);

		int stt = 1;
		if(checkManyOrg == false) {
			for(UserOrganizationModel userOrganizationModel : listData) {
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
				cell.setCellValue(userOrganizationModel.getMoreInfo().getFullName());

				cell = row.getCell(2);
				cell.setCellValue(userOrganizationModel.getMoreInfo().getUsername());

				cell = row.getCell(3);
				cell.setCellValue(userOrganizationModel.getMoreInfo().getEmail());

				cell = row.getCell(4);
				String positionName = userOrganizationModel.getPositionText(); 
				cell.setCellValue(positionName);

				cell = row.getCell(5);
				String accountOffice = userOrganizationModel.getAccountIOfficeText();
				cell.setCellValue(accountOffice);

				cell = row.getCell(6);
				String status = userOrganizationModel.getStatus().equals("logged") ? "Sử dụng" : "Không sử dụng";
				cell.setCellValue(status);

				cell = row.getCell(7);
				cell.setCellValue(userOrganizationModel.getMoreInfo().getPhone());

				cell = row.getCell(9);
				cell.setCellValue(orgName);

			}
			cell = row.getCell(9);
			int firstRow = 5;
			int lastRow = 5+listData.size()-1;
			System.out.println("Last row"+lastRow);
			CellRangeAddress cellRangeAddress = new CellRangeAddress(firstRow,lastRow,9,9);
			sheet.addMergedRegion(cellRangeAddress);

			CellStyle cellStyle = cell.getCellStyle();
			cellStyle.setAlignment(HorizontalAlignment.CENTER);
			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			cell.setCellStyle(cellStyle);
		}else {
			for(UserOrganizationModel userOrganizationModel : listData) {
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
				cell.setCellValue(userOrganizationModel.getMoreInfo().getFullName());

				cell = row.getCell(2);
				cell.setCellValue(userOrganizationModel.getMoreInfo().getUsername());

				cell = row.getCell(3);
				cell.setCellValue(userOrganizationModel.getMoreInfo().getEmail());

				cell = row.getCell(4);
				String positionName = userOrganizationModel.getPositionText(); 
				cell.setCellValue(positionName);

				cell = row.getCell(5);
				String accountOffice = userOrganizationModel.getAccountIOfficeText();
				cell.setCellValue(accountOffice);

				cell = row.getCell(6);
				String status = userOrganizationModel.getStatus().equals("logged") ? "Sử dụng" : "Không sử dụng";
				cell.setCellValue(status);

				cell = row.getCell(7);
				cell.setCellValue(userOrganizationModel.getMoreInfo().getPhone());

				cell = row.getCell(9);
				cell.setCellValue(userOrganizationModel.getOrgName());

				cell = row.getCell(10);
				cell.setCellValue(orgName);

			}
			
			megedDuplicates(sheet, 9);
			
			cell = row.getCell(10);
			int firstRow = 5;
			int lastRow = 5+listData.size()-1;
			CellRangeAddress cellRangeAddress = new CellRangeAddress(firstRow,lastRow,10,10);
			sheet.addMergedRegion(cellRangeAddress);

			CellStyle cellStyle = cell.getCellStyle();
			cellStyle.setAlignment(HorizontalAlignment.CENTER);
			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			cell.setCellStyle(cellStyle);

		}
	}

	public void setListData(List<UserOrganizationModel> listData) {
		this.listData = listData;
	}

	private static void megedDuplicates(Sheet sheet,int columIndex) {
		Map<String, CellRangeAddress> duplicateMap = new HashMap<>();

		for(int index = 5; index <= sheet.getLastRowNum(); index++) {
			Row row = sheet.getRow(index);
			if(row != null) {
				Cell cell = row.getCell(columIndex);
				if(cell != null) {
					String cellValue = cell.getStringCellValue();
			        
			        if(!duplicateMap.containsKey(cellValue)) {
			        	CellRangeAddress cellRangeAddress = new CellRangeAddress(row.getRowNum(),row.getRowNum(),cell.getColumnIndex(),cell.getColumnIndex());
			        	duplicateMap.put(cellValue, cellRangeAddress);
			        }else {
			        	CellRangeAddress cellRangeAddress = duplicateMap.get(cellValue);
			        	cellRangeAddress.setLastRow(row.getRowNum());
			        }
				}
			}
		}
		
		for(CellRangeAddress cellRangeAddress : duplicateMap.values()) {
			try {
				sheet.addMergedRegion(cellRangeAddress);
			} catch (Exception e) {
				System.out.println("Not duplicate");
			}
		}
		
	}

	//	private static boolean isRegionMerged(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
	//		for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
	//			CellRangeAddress mergedRegion = sheet.getMergedRegion(i);
	//			if (mergedRegion.isInRange(firstRow, firstCol) || mergedRegion.isInRange(lastRow, lastCol)) {
	//				return true;
	//			}
	//		}
	//		return false;
	//	}

	
	
}
























