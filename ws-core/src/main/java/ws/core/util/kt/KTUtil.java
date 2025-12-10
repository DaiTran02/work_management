package ws.core.util.kt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class KTUtil {
	public static void main(String[] args) {
		String excelInput="C:\\Users\\KHUETECH\\Documents\\trinh\\donhang\\Xuat_kho_full.xlsx";
		try {
			LinkedList<DonHang> dsDonHang=getDanhSachDonHang(excelInput);
			for (DonHang donHang : dsDonHang) {
				System.out.println(donHang.toString());
			}
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static LinkedList<SanPham> getDanhSachSanPham(String excelInput) throws IOException, InvalidFormatException{
		LinkedList<SanPham> dsSanPham=new LinkedList<>();
		
		XSSFWorkbook workbook = new XSSFWorkbook(new File(excelInput));
        XSSFSheet sheet = workbook.getSheet("DS_SanPham");
        
        Iterator<Row> rowIterator = sheet.iterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if(row.getRowNum()>=1) {
            	SanPham sanPham=new SanPham();
            	
            	DataFormatter dataFormatter=new DataFormatter();
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    
                    if(cell.getColumnIndex()==0) {
                    	sanPham.setNoiDung(dataFormatter.formatCellValue(cell));
                    }
                    
                    if(cell.getColumnIndex()==1) {
                    	sanPham.setSKU(dataFormatter.formatCellValue(cell));
                    }
                    
                    if(cell.getColumnIndex()==2) {
                    	sanPham.setTenSanPham(dataFormatter.formatCellValue(cell));
                    }
                    
                    if(cell.getColumnIndex()==3) {
                    	sanPham.setSoLuong(Integer.parseInt(dataFormatter.formatCellValue(cell)));
                    }
                }
                
                dsSanPham.add(sanPham);
            }
        }
        workbook.close();
		return dsSanPham;
	}
	
	private static SanPham findSanPhamTheoVi(LinkedList<SanPham> dsSanPham, List<String> vis) {
		for (String vi : vis) {
			for(SanPham sanPham: dsSanPham) {
				if(sanPham.getTenSanPham().contains(vi)) {
					return sanPham;
				}
			}
		}
		return null;
	}
	
	public static LinkedList<DonHang> getDanhSachDonHang(String excelInput) throws IOException, InvalidFormatException {
		LinkedList<SanPham> dsSanPham = getDanhSachSanPham(excelInput);
		LinkedList<DonHang> dsDonHang = new LinkedList<>();
		LinkedHashMap<String, List<String>> dsDonHangChuaTach=new LinkedHashMap<>();
		
		XSSFWorkbook workbook = new XSSFWorkbook(new File(excelInput));
        XSSFSheet sheet = workbook.getSheet("DS_DonHang");
        Iterator<Row> rowIterator = sheet.iterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if(row.getRowNum()>=8) {
            	String tenDoiTuong = null;
            	String diaChiBoPhan =null;
            	
            	DataFormatter dataFormatter=new DataFormatter();
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    
                    if(cell.getColumnIndex()==5) {
                    	tenDoiTuong=dataFormatter.formatCellValue(cell);
                    }
                    
                    if(cell.getColumnIndex()==6) {
                    	diaChiBoPhan=dataFormatter.formatCellValue(cell);
                    }
                }
                
				/* Nếu chưa có */
                if(dsDonHangChuaTach.containsKey(tenDoiTuong)==false) {
                	List<String> dsDiaChiBoPhan=new ArrayList<>();
                	dsDiaChiBoPhan.add(diaChiBoPhan);
                	
                	dsDonHangChuaTach.put(tenDoiTuong, dsDiaChiBoPhan);
                }
				/* Nếu đã có */
                else if(dsDonHangChuaTach.containsKey(tenDoiTuong)){
                	List<String> dsDiaChiBoPhan=dsDonHangChuaTach.get(tenDoiTuong);
                	dsDiaChiBoPhan.add(diaChiBoPhan);
                	
                	dsDonHangChuaTach.put(tenDoiTuong, dsDiaChiBoPhan);
                }
            }
        }
        workbook.close();
        
        int so=240;
        for (Map.Entry<String, List<String>> entry : dsDonHangChuaTach.entrySet()) {
			String tenDoiTuong = entry.getKey();
			List<String> dsDiaChiBoPhan = entry.getValue();
			
			String soChungTu=getSoChungTu(so);
			for (String diaChiBoPhan : dsDiaChiBoPhan) {
				/*Nếu là [Quà tặng không bán]*/
				if(diaChiBoPhan.startsWith("[Quà tặng không bán]")) {
					/* Vị chuối */
					if(diaChiBoPhan.contains("Chuối") || diaChiBoPhan.contains("chuối")) {
						SanPham sanPham=findSanPhamTheoVi(dsSanPham, Arrays.asList("Chuối", "chuối"));
						DonHang donHang=new DonHang();
						donHang.setSoChungTu(soChungTu);
						donHang.setTenDoiTuong(tenDoiTuong);
						donHang.setDiachiBoPhan(diaChiBoPhan);
						donHang.setLyDoXuat(sanPham.getNoiDung().replace("TENDOITUONG", tenDoiTuong));
						donHang.setMaHang(sanPham.getSKU());
						donHang.setTenHang(sanPham.getTenSanPham());
						donHang.setSoLuong(sanPham.getSoLuong());
						donHang.setDonTang(true);
						
						dsDonHang.add(donHang);
					}
					
					/* Vị ổi */
					if(diaChiBoPhan.contains("Ổi") || diaChiBoPhan.contains("ổi")) {
						SanPham sanPham=findSanPhamTheoVi(dsSanPham, Arrays.asList("Ổi", "ổi"));
						DonHang donHang=new DonHang();
						donHang.setSoChungTu(soChungTu);
						donHang.setTenDoiTuong(tenDoiTuong);
						donHang.setDiachiBoPhan(diaChiBoPhan);
						donHang.setLyDoXuat(sanPham.getNoiDung().replace("TENDOITUONG", tenDoiTuong));
						donHang.setMaHang(sanPham.getSKU());
						donHang.setTenHang(sanPham.getTenSanPham());
						donHang.setSoLuong(sanPham.getSoLuong());
						donHang.setDonTang(true);
						
						dsDonHang.add(donHang);
					}
					
					/* Vị dâu */
					if(diaChiBoPhan.contains("Dâu") || diaChiBoPhan.contains("dâu")) {
						SanPham sanPham=findSanPhamTheoVi(dsSanPham, Arrays.asList("Dâu", "dâu"));
						DonHang donHang=new DonHang();
						donHang.setSoChungTu(soChungTu);
						donHang.setTenDoiTuong(tenDoiTuong);
						donHang.setDiachiBoPhan(diaChiBoPhan);
						donHang.setLyDoXuat(sanPham.getNoiDung().replace("TENDOITUONG", tenDoiTuong));
						donHang.setMaHang(sanPham.getSKU());
						donHang.setTenHang(sanPham.getTenSanPham());
						donHang.setSoLuong(sanPham.getSoLuong());
						donHang.setDonTang(true);
						
						dsDonHang.add(donHang);
					}
				}else if(diaChiBoPhan.startsWith("Combo 3")) {
					for(SanPham sanPham:dsSanPham) {
						DonHang donHang=new DonHang();
						donHang.setSoChungTu(soChungTu);
						donHang.setTenDoiTuong(tenDoiTuong);
						donHang.setDiachiBoPhan(diaChiBoPhan);
						donHang.setLyDoXuat(sanPham.getNoiDung().replace("TENDOITUONG", tenDoiTuong));
						donHang.setMaHang(sanPham.getSKU());
						donHang.setTenHang(sanPham.getTenSanPham());
						donHang.setSoLuong(sanPham.getSoLuong());
						donHang.setDonTach(true);
						
						dsDonHang.add(donHang);
					}
				}else if(diaChiBoPhan.startsWith("Combo 6")) {
					for(SanPham sanPham:dsSanPham) {
						DonHang donHang=new DonHang();
						donHang.setSoChungTu(soChungTu);
						donHang.setTenDoiTuong(tenDoiTuong);
						donHang.setDiachiBoPhan(diaChiBoPhan);
						donHang.setLyDoXuat(sanPham.getNoiDung().replace("TENDOITUONG", tenDoiTuong));
						donHang.setMaHang(sanPham.getSKU());
						donHang.setTenHang(sanPham.getTenSanPham());
						donHang.setSoLuong(sanPham.getSoLuong()+1);
						donHang.setDonTach(true);
						
						dsDonHang.add(donHang);
					}
				}else {
					DonHang donHang=new DonHang();
					donHang.setSoChungTu(soChungTu);
					donHang.setTenDoiTuong(tenDoiTuong);
					donHang.setDiachiBoPhan(diaChiBoPhan);
					
					dsDonHang.add(donHang);
				}
			}
			
			/* Tăng số chứng từ */
			so++;
		}
        
		/* Ghi dữ liệu */
        workbook = new XSSFWorkbook(new File(excelInput));
        sheet = workbook.getSheet("Output");
        int rowNum=8;
        for(DonHang donHang:dsDonHang) {
        	Row row = sheet.createRow(rowNum++);
        	Cell cell=null;
        	
        	cell=row.createCell(3);
        	cell.setCellValue(donHang.getSoChungTu());
        	
        	cell=row.createCell(5);
        	cell.setCellValue(donHang.getTenDoiTuong());
        	
        	cell=row.createCell(6);
        	cell.setCellValue(donHang.getDiachiBoPhan());
        	
        	cell=row.createCell(12);
        	cell.setCellValue(donHang.getLyDoXuat());
        	
        	cell=row.createCell(15);
        	cell.setCellValue(donHang.getMaHang());
        	
        	cell=row.createCell(16);
        	cell.setCellValue(donHang.getTenHang());
        	
        	cell=row.createCell(24);
        	cell.setCellValue(donHang.getSoLuong());
        }
        
        try (FileOutputStream fileOut = new FileOutputStream("C:\\Users\\KHUETECH\\Documents\\trinh\\donhang\\Output.xlsx")) {
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return dsDonHang;
	}
	
	private static String getSoChungTu(int so) {
		return "XK00"+so+"/11";
	}
}
