package ws.core.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum NotificationAction {
	van_ban_moi_duoc_dong_bo("van_ban_moi_duoc_dong_bo","Văn bản mới được đồng bộ"),
	nhiem_vu_moi_duoc_giao_xu_ly("nhiem_vu_moi_duoc_giao_xu_ly","Nhiệm vụ mới được giao (xử lý)"),
	nhiem_vu_moi_duoc_giao_phoi_hop("nhiem_vu_moi_duoc_giao_phoi_hop","Nhiệm vụ mới được giao (phối hợp)"),
	nhiem_vu_moi_duoc_giao_theo_doi("nhiem_vu_moi_duoc_giao_theo_doi","Nhiệm vụ mới được giao (theo dõi)"),
	nhiem_vu_bat_dau_thuc_hien("nhiem_vu_bat_dau_thuc_hien","Nhiệm vụ bắt đầu thực hiện"),
	nhiem_vu_hoan_thanh("nhiem_vu_hoan_thanh","Nhiệm vụ hoàn thành"),
	nhiem_vu_yeu_cau_xac_nhan_hoan_thanh("nhiem_vu_yeu_cau_xac_nhan_hoan_thanh","Yêu cầu xác nhận hoàn thành"),
	nhiem_vu_cho_xac_nhan_hoan_thanh("nhiem_vu_cho_xac_nhan_hoan_thanh","Nhiệm vụ chờ xác nhận hoàn thành"),
	nhiem_vu_da_xac_nhan_hoan_thanh("nhiem_vu_da_xac_nhan_hoan_thanh","Xác nhận đã hoàn thành nhiệm vụ"),
	nhiem_vu_bi_tu_choi_xac_nhan_hoan_thanh("nhiem_vu_bi_tu_choi_xac_nhan_hoan_thanh","Nhiệm vụ bị từ chối xác nhận hoàn thành"),
	nhiem_vu_thuc_hien_va_bao_cao_lai("nhiem_vu_thuc_hien_va_bao_cao_lai","Nhiệm vụ thực hiện và báo cáo lại"),
	
	nhiem_vu_duoc_trieu_hoi("nhiem_vu_duoc_trieu_hoi","Nhiệm vụ được triệu hồi"),
	nhiem_vu_duoc_hoan_thanh("nhiem_vu_duoc_hoan_thanh","Nhiệm vụ được hoàn thành"),
	
	nhiem_vu_duoc_danh_gia("nhiem_vu_duoc_danh_gia","Nhiệm vụ được đánh giá"),
	nhiem_vu_duoc_nhac_nho("nhiem_vu_duoc_nhac_nho","Nhiệm vụ được nhắc nhở"),
	nhiem_vu_duoc_cap_nhat("nhiem_vu_duoc_cap_nhat","Nhiệm vụ có cập nhật mới"),
	nhiem_vu_bi_tam_hoan("nhiem_vu_tam_hoan","Nhiệm vụ bị tạm hoãn"),
	nhiem_vu_tiep_tuc_thuc_hien("nhiem_vu_tiep_tuc","Nhiệm vụ tiếp tục thực hiện"),
	nhiem_vu_da_bi_xoa("nhiem_vu_da_bi_xoa","Nhiệm vụ vụ đã bị xóa"),
	nhiem_vu_bi_tu_choi_thuc_hien("nhiem_vu_bi_tu_choi_thuc_hien","Nhiệm vụ bị từ chối thực hiện"),
	nhiem_vu_duoc_yeu_cau_thuc_hien_lai("nhiem_vu_duoc_yeu_cau_thuc_hien_lai","Nhiệm vụ được yêu cầu thực hiện lại"),
	nhiem_vu_duoc_cap_nhat_tien_do("nhiem_vu_duoc_cap_nhat_tien_do","Nhiệm vụ được cập nhật tiến độ"),
	nhiem_vu_co_phan_hoi_trao_doi_moi("nhiem_vu_co_phan_hoi_trao_doi_moi","Nhiệm vụ có phản hồi trao đổi mới"),
	
	lich_moi_tham_gia_su_kien("lich_moi_tham_gia_su_kien","Mời tham gia sự kiện"),
	lich_cap_nhat_su_kien("lich_cap_nhat_su_kien","Cập nhật sự kiện"),
	lich_xac_nhan_tham_gia_su_kien("lich_xac_nhan_tham_gia_su_kien","Xác nhận tham gia sự kiên"),
	lich_uy_quyen_tham_gia_su_kien("lich_uy_quyen_tham_gia_su_kien","Ủy quyền tham gia sự kiên"),
	lich_huy_su_kien("lich_huy_su_kien","Hủy sự kiện"),
	lich_su_kien_sap_bat_dau("lich_su_kien_sap_bat_dau","Sự kiện sắp bắt đầu");
	
	private String action;
	private String title;
	
	NotificationAction(String action, String title){
		this.action=action;
		this.title=title;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
