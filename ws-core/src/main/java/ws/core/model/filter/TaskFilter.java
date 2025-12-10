package ws.core.model.filter;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import ws.core.enums.TaskSource;
import ws.core.enums.TaskStatus;
import ws.core.model.embeded.TaskDocInfo;
import ws.core.model.filter.embeded.TaskAssigneeFilter;
import ws.core.model.filter.embeded.TaskAssistantFilter;
import ws.core.model.filter.embeded.TaskFollowerFilter;
import ws.core.model.filter.embeded.TaskOwnerFilter;
import ws.core.model.filter.embeded.TaskSupportFilter;
import ws.core.model.filter.embeded.TaskUserRefFilter;

@Data
public class TaskFilter {
	/**
	 * Danh sách id nhiệm vụ
	 */
	private List<String> ids=null;
	
	/**
	 * Ngày tạo từ
	 */
	private long fromDate=0;
	
	/**
	 * Ngày tạo đến
	 */
	private long toDate=0;
	
	/**
	 * Ngày hoàn thành từ
	 */
	private long completedFromDate=0;
	
	/**
	 * Ngày hoàn thành đến
	 */
	private long completedToDate=0;
	
	/**
	 * Danh sách id nhiệm vụ cha
	 */
	private List<String> parentIds=new ArrayList<String>();
	
	/**
	 * Danh sách id văn bản đã giao nhiệm vụ
	 */
	private List<String> docIds=new ArrayList<String>();
	
	/**
	 * Nhiệm vụ root or child
	 * true: chỉ lấy nhiệm vụ root, không lấy nhiệm vụ child
	 * false: chỉ lấy nhiệm vụ child, không lấy nhiệm vụ root
	 */
	private Boolean taskRoot=null;
	
	/**
	 * Tìm theo văn bản
	 * Số hiệu
	 * Ký hiệu
	 * Trích yếu
	 * Loại văn bản
	 */
	private TaskDocInfo docInfo=null;
	
	/**
	 * Tìm Đơn vị/Cán bộ chỉ đạo giao nhiệm vụ
	 */
	private List<TaskOwnerFilter> findOwnerFilters=null;
	
	/**
	 * Tìm Đơn vị/Cán bộ giao thay nhiệm vụ
	 */
	private List<TaskAssistantFilter> findAssistantFilters=null;
	
	/**
	 * Tìm Đơn vị/Cán bộ được giao chủ trì xử lý
	 */
	private List<TaskAssigneeFilter> findAssigneeFilters=null;
	
	/**
	 * Tìm Đơn vị/Cán bộ được giao hỗ trợ phối hợp xử lý
	 */
	private List<TaskSupportFilter> findSupportFilters=null;
	
	/**
	 * Tìm Đơn vị/Cán bộ theo dõi nhiệm vụ
	 */
	private List<TaskFollowerFilter> findFollowerFilters=null;
	
	/**
	 * Tìm Đơn vị/Cán bộ liên quan đến nhiệm vụ (dùng cho trường hợp kiểm tra user A của đơn vị B có liên quan đến nhiệm vụ nào không?)
	 */
	private TaskUserRefFilter findUserRefFilter;
	
	/**
	 * Tìm theo trạng thái nhiệm vụ
	 */
	private TaskStatus status=null;
	
	/**
	 * Tìm theo độ ưu tiên
	 */
	private String priority=null;
	
	/**
	 * Tìm theo từ khóa
	 */
	private String keySearch=null;
	
	/**
	 * Tìm nhiệm vụ có hạn xử lý
	 */
	private Boolean hasEndTime=null;
	
	/**
	 * Tìm nhiệm vụ có yêu cầu xác nhận hoàn thành
	 */
	private Boolean requiredConfirm=null;
	
	/**
	 * Tìm nhiệm vụ được đánh giá bao nhiêu sao
	 */
	private List<Integer> ratingStar=null;
	
	/**
	 * Tìm nhiệm vụ Đơn vị được giao đã gán người xử lý chưa
	 */
	private Boolean hasAssignUserAssignee=null;
	
	/**
	 * Tìm nhiệm vụ Đơn vị phối hợp đã gán người hỗ trợ chưa
	 */
	private Boolean hasAssignUserSupport=null;
	
	/**
	 * Tìm nhiệm vụ theo nguồn giao
	 * Từ văn bản
	 * Tự phát
	 */
	private TaskSource taskSource=null; 
	
	/**
	 * Phân trang
	 */
	private SkipLimitFilter skipLimitFilter=null;
	
	/**
	 * Sắp xếp theo yêu cầu
	 */
	private OrderByFilter orderByFilter=null;
	
	/**
	 * Tìm theo cán bộ được giao nhiệm vụ
	 * */
	private String idUserAssignee;
	
	/**
	 * Tìm theo Kpi
	 * */
	private Boolean kpi;
	
}
