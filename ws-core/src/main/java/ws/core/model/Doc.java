package ws.core.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.EnumUtils;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import ws.core.enums.DocCategory;
import ws.core.enums.DocSecurity;
import ws.core.enums.DocSource;
import ws.core.enums.DocStatus;
import ws.core.model.embeded.DocExternal;
import ws.core.model.embeded.DocOwner;
import ws.core.model.embeded.DocReceiver;
import ws.core.model.embeded.DocResultConfirm;

/**
 * The Class Doc.
 */
@Data
@Document(collection = "doc")
public class Doc {
	
	/** The id. */
	@Id
	@Field(value = "_id")
	private ObjectId id;
	
	/** The created time. */
	@Indexed
	@Field(value = "createdTime")
	private Date createdTime;
	
	/** The updated time. */
	@Indexed
	@Field(value = "updatedTime")
	private Date updatedTime;
	
	/** Số văn bản. */
	@Indexed
	@Field(value = "number")
	private String number;
	
	/** Ký hiệu văn bản. */
	@Indexed
	@Field(value = "symbol")
	private String symbol;
	
	/** Độ mật văn bản. */
	@Indexed
	@Field(value = "security")
	private String security;
	
	/** Ngày ký văn bản. */
	@Indexed
	@Field(value = "regDate")
	private Date regDate;
	
	/** Loại văn bản. */
	@Indexed
	@Field(value = "type")
	private String type;
	
	/** Họ tên người ký văn bản. */
	@Indexed
	@Field(value = "signerName")
	private String signerName;
	
	/** Chức vụ người ký văn bản. */
	@Indexed
	@Field(value = "signerPosition")
	private String signerPosition;
	
	/** Số bản. */
	@Indexed
	@Field(value = "copies")
	private int copies;
	
	/** Số trang. */
	@Indexed
	@Field(value = "pages")
	private int pages;
	
	/** Đơn vị nhận. */
	@Indexed
	@Field(value = "orgReceiveName")
	private String orgReceiveName;
	
	/** Đơn vị phát hành. */
	@Indexed
	@Field(value = "orgCreateName")
	private String orgCreateName;
	
	/** Trích yếu. */
	@Indexed
	@Field(value = "summary")
	private String summary;
	
	/** Văn bản đang sử dụng?. */
	@Indexed
	@Field(value = "active")
	private boolean active;
	
	/** Văn bản đã xóa?. */
	@Indexed
	@Field(value = "trash")
	private boolean trash;
	
	/** Thông tin đơn vị, tổ giao việc và người soạn thảo trên hệ thống giao nhiệm vụ. */
	@Indexed
	@Field(value = "owner")
	private DocOwner owner;
	
	@Indexed
	@Field(value = "receivers")
	private List<DocReceiver> receivers=new ArrayList<>();
	
	/** Loại văn bản. */
	@Indexed
	@Field(value = "category")
	public String category;
	
	/** Thông tin mở rộng bên ngoài. */
	@Indexed
	@Field(value = "external")
	private DocExternal external;
	
	/** Id tài khoản tác động. */
	@Indexed
	@Field(value = "creatorId")
	public String creatorId;
	
	/** Tên tài khoản tác động. */
	@Field(value="creatorName")
	public String creatorName;
	
	/** Số nhiệm vụ đã giao trực tiếp từ văn bản. */
	@Indexed
	@Field(value = "countTask")
	private int countTask;
	
	/** Danh sách đính kèm. */
	@Indexed
	@Field(value = "attachments")
	private LinkedList<String> attachments=new LinkedList<>();
	
	/** Id loại chỉ đạo (trường mở rộng). */
	@Indexed
	@Field(value = "classifyTaskId")
	private String classifyTaskId=null;
	
	/** Id người kết luận (trường mở rộng). */
	@Indexed
	@Field(value = "leaderApproveTaskId")
	private String leaderApproveTaskId=null;
	
	/** iOfficeId id bên VNPT. */
	@Indexed
	@Field(value = "iOfficeId")
	private String iOfficeId;
	
	@Indexed
	@Field(value = "source")
	private String source;
	
	@Indexed
	@Field(value = "status")
	private DocStatus status;
	
	@Indexed
	@Field(value = "resultConfirm")
	private DocResultConfirm resultConfirm;
	
	/**
	 * Instantiates a new doc.
	 */
	public Doc() {
		this.id=ObjectId.get();
		this.copies=0;
		this.pages=0;
		this.countTask=0;
		this.status=DocStatus.chuagiaonhiemvu;
	}
	
	/**
	 * Gets the object id.
	 *
	 * @return the object id
	 */
	public ObjectId getObjectId() {
		return id;
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id.toHexString();
	}
	
	/**
	 * Gets the created time long.
	 *
	 * @return the created time long
	 */
	public long getCreatedTimeLong() {
		if(getCreatedTime()!=null) {
			return getCreatedTime().getTime();
		}
		return 0;
	}
	
	/**
	 * Gets the updated time long.
	 *
	 * @return the updated time long
	 */
	public long getUpdatedTimeLong() {
		if(getUpdatedTime()!=null) {
			return getUpdatedTime().getTime();
		}
		return 0;
	}
	
	/**
	 * Gets the reg date long.
	 *
	 * @return the reg date long
	 */
	public long getRegDateLong() {
		if(getRegDate()!=null) {
			return getRegDate().getTime();
		}
		return 0;
	}
	
	/**
	 * Gets the category.
	 *
	 * @return the category
	 */
	public DocCategory getCategory() {
		if(category!=null && EnumUtils.isValidEnum(DocCategory.class, category)) {
			return EnumUtils.getEnumIgnoreCase(DocCategory.class, category);
		}
		return null;
	}
	
	/**
	 * Gets the security.
	 *
	 * @return the security
	 */
	public DocSecurity getSecurity() {
		if(security!=null && EnumUtils.isValidEnum(DocSecurity.class, security)) {
			return EnumUtils.getEnumIgnoreCase(DocSecurity.class, security);
		}
		return null;
	}
	
	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public DocStatus getStatus() {
		if(status==DocStatus.vanbandahoanthanh)
			return DocStatus.vanbandahoanthanh;
		else {
			if(countTask>0) {
				return DocStatus.dangthuchien;
			}
			return DocStatus.chuagiaonhiemvu;
		}
	}
	
	public boolean isAPIPartner() {
		return (getIOfficeId()!=null && getSource()!=null && DocSource.APIPartner.getKey().equals(getSource()));
	}
}
