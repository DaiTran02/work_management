package vn.com.ngn.page.report.excels;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.impl.common.XPath.XPathCompileException;

import com.vaadin.flow.server.StreamResource;

import vn.com.ngn.api.report.ApiOrganizationForReportModel;
import vn.com.ngn.utils.ExportToExcel;

public class ExportExcelOrg extends ExportToExcel{
	private List<ApiOrganizationForReportModel> listData = new ArrayList<ApiOrganizationForReportModel>();
	XSSFSheet sheet = null;
	XSSFRow rowTemplate = null;
	int index = 3;
	int startRow = index;
	int stt = 1;
	int sizeOfData = 0;
	public StreamResource createReport() throws Exception{
		FileInputStream fileInputStream = new FileInputStream("./template/thong-ke-don-vi.xlsx");
		workbook = new XSSFWorkbook(fileInputStream);

		createSheet();
		row = sheet.getRow(1);
		cell = row.getCell(0);
		cell.setCellValue("Tổng số đơn vị: "+sizeOfData);
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		workbook.write(byteOutputStream);

		workbook.close();
		return getStreamResource("Thống kê đơn vị.xlsx", byteOutputStream.toByteArray());
	}

	public void createSheet() throws XPathCompileException{
		sheet = workbook.getSheetAt(0);

		
		sizeOfData += listData.size();


		rowTemplate = sheet.getRow(index);

	

		doGetChildOrg(listData,"");
	}

	private void doGetChildOrg(List<ApiOrganizationForReportModel> subOrg,String nameParent) {
		try {
			for(ApiOrganizationForReportModel organizationForReportModel : subOrg) {
				if(index == startRow) {
					row = rowTemplate;
					index++;
				}else {
					row = sheet.createRow(index++);
					row.copyRowFrom(rowTemplate, cellCopyPolicy);
				}
				sizeOfData += organizationForReportModel.getCountSubOrganization();

				cell = row.getCell(0);
				cell.setCellValue(stt++);

				cell = row.getCell(1);
				cell.setCellValue(organizationForReportModel.getName());

				cell = row.getCell(2);
				cell.setCellValue(organizationForReportModel.getUnitCodeText());

				cell = row.getCell(3);
				cell.setCellValue(organizationForReportModel.getDescription());

				cell = row.getCell(4);
				cell.setCellValue(organizationForReportModel.getLevel().getName());

				cell = row.getCell(5);
				cell.setCellValue("Đang cập nhật");

				cell = row.getCell(6);
				cell.setCellValue(checkActive(organizationForReportModel.isActive()));

				cell = row.getCell(7);
				cell.setCellValue("0");
				
				cell = row.getCell(8);
				cell.setCellValue("0");
				
				cell = row.getCell(9);
				cell.setCellValue(organizationForReportModel.getCreateTimeText());
				
				cell = row.getCell(10);
				cell.setCellValue(organizationForReportModel.getUpdateTimeText());
				
				cell = row.getCell(11);
				cell.setCellValue(nameParent);

				if(organizationForReportModel.getSubOrganizations() != null && !organizationForReportModel.getSubOrganizations().isEmpty()) {
					doGetChildOrg(organizationForReportModel.getSubOrganizations(),organizationForReportModel.getName());
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String checkActive(boolean check) {
		if(check)
			return "Hoạt động";
		return "Không hoạt động";
	}

	public void setListData(List<ApiOrganizationForReportModel> listData) {
		this.listData = listData;
	}
}
