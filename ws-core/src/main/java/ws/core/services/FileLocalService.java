package ws.core.services;

public interface FileLocalService {
	/**
	 * Lấy địa chỉ thư mục lưu đính kèm
	 * @return
	 */
	public String getPathAttachments();
	
	/**
	 * Lấy địa chỉ thư mục lưu báo cáo
	 * @return
	 */
	public String getPathExports();
	
	/**
	 * Lấy địa chỉ thư mục lưu báo cáo công cộng
	 * @return
	 */
	public String getPathExportsPublic();
	
	/**
	 * Lấy địa chỉ thư mục lưu mẫu
	 * @return
	 */
	public String getPathTemplates();
	
	/**
	 * Lấy nội dung tệp bằng địa chỉ đường dẫn
	 * @param filePath
	 * @return
	 */
	public byte[] getFile(String filePath);
	
	/**
	 * Xóa tệp bằng địa chỉ đường dẫn
	 * @param filePath
	 */
	public void deleteFile(String filePath);
}