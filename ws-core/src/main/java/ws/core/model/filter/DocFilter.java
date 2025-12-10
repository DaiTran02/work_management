package ws.core.model.filter;

import java.util.List;

import lombok.Data;
import ws.core.enums.DocCategory;
import ws.core.enums.DocStatus;
import ws.core.model.filter.embeded.DocOwnerFilter;
import ws.core.model.filter.embeded.DocUserRefFilter;

@Data
public class DocFilter {
	/**
	 * Danh sách id văn bản
	 */
	private List<String> ids=null;
	
	/**
	 * Tìm ngày ký từ ngày
	 */
	private long fromRegDate=0;
	
	/**
	 * Tìm ngày ký đến ngày
	 */
	private long toRegDate=0;
	
	/**
	 * Tìm từ khóa liên quan
	 */
	private String keySearch=null;
	
	/**
	 * Tìm số hiệu
	 */
	private String number=null;
	
	/**
	 * Tìm ký hiệu
	 */
	private String symbol=null;
	
	/**
	 * Tìm hoạt động?
	 */
	private Boolean active=null;
	
	/**
	 * Tìm đã bỏ thùng rác?
	 */
	private Boolean trash=null;
	
	/**
	 * Tìm người ký
	 */
	private String signerName=null;
	
	/**
	 * Tìm Người dùng/Đơn vị quản lý văn bản đó
	 */
	private DocOwnerFilter ownerFilter=null;
	
	/**
	 * Tìm Người dùng/Đơn vị có liên quan bất kỳ văn bản nào không?
	 */
	private DocUserRefFilter userRefFilter=null;
	
	/**
	 * Tìm theo trạng thái
	 */
	private DocStatus status=null;
	
	/**
	 * Tìm loại văn bản
	 */
	private DocCategory category=null;
	
	/**
	 * Tìm phân loại chỉ đạo văn bản
	 */
	private String classifyTaskId=null;
	
	/**
	 * Tìm người kết luận văn bản
	 */
	private String leaderApproveTaskId=null;
	
	/**
	 * Lấy dữ liệu từ skip với limit
	 */
	private SkipLimitFilter skipLimitFilter=null;
	
	/**
	 * Sắp xếp theo đối tượng
	 */
	private OrderByFilter orderByFilter=null;
}
