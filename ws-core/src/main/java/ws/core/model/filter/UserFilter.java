package ws.core.model.filter;

import java.util.List;

import lombok.Data;

/**
 * The Class UserFilter.
 */
@Data
public class UserFilter {
	
	/** Bao gồm các người dùng (username) */
	private List<String> userNames=null;
	
	/** Tình trạng kích hoạt */
	private Boolean active=null;
	
	/** Tình trạng nghỉ hưu */
	private Boolean archive=null;
	
	/** Bao gồm những tổ chức (id) */
	private List<String> includeOrganizationIds=null;
	
	/** Loại trừ những tổ chức (id) */
	private List<String> excludeOrganizationIds=null;
	
	/** Bao gồm các người dùng (id) */
	private List<String> includeUserIds=null;
	
	/** Loại trừ các người dùng (id) */
	private List<String> excludeUserIds=null;
	
	/** Đã được sử dụng cho tổ chức? */
	private Boolean hasUsed=null;
	
	/** Tour giới thiệu khi lần đầu sử dụng */
	private FirstReviewFilter firstReviewFilter=null;
	
	/** Nguồn tạo người dùng */
	private String provider=null;
	
	/** Từ khóa tìm kiếm */
	private String keySearch=null;
	
	/** The searching type filter. */
	private SearchingTypeFilter searchingTypeFilter=null;
	
	/** The skip limit filter. */
	private SkipLimitFilter skipLimitFilter=null;
	
	/** The order by filter. */
	private OrderByFilter orderByFilter=null;
}
