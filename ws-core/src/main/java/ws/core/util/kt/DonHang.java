package ws.core.util.kt;

import lombok.Data;

@Data
public class DonHang {
	private String SoChungTu;
	private int MaDoiTuong;
	private String TenDoiTuong;
	private String DiachiBoPhan;
	private String LyDoXuat;
	private String MaHang;
	private String TenHang;
	private int SoLuong;
	
	private boolean donTang=false;
	private boolean donTach=false;
}
