package ws.core.model.request;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.enums.DocCategory;
import ws.core.enums.DocSecurity;
import ws.core.model.request.embeded.ReqDocOwnerPartner;
import ws.core.validation.ValidDatetimeLong;
import ws.core.validation.ValidEnum;
import ws.core.validation.ValidListObjectId;
import ws.core.validation.ValidObjectId;
import ws.core.validation.ValidStringExtra;
import ws.core.validation.ValidStringMedium;

@Data
public class ReqDocCreatePartner {
	@NotNull(message = "number không được trống")
	@ValidStringMedium(message = "number không được chứa các ký tự đặc biệt")
	private String number;
	
	@NotNull(message = "symbol không được trống")
	@ValidStringMedium(message = "symbol không được chứa các ký tự đặc biệt")
	private String symbol;
	
	@NotNull(message = "security không được trống")
	@ValidStringMedium(message = "security không được chứa các ký tự đặc biệt")
	private String security;
	
	@NotNull(message = "regDate không được trống")
	@ValidDatetimeLong(message = "regDate không hợp lệ")
	private long regDate;
	
	@NotNull(message = "type không được trống")
	@ValidStringMedium(message = "type không được chứa các ký tự đặc biệt")
	private String type;
	
	@NotEmpty(message = "signerName không được trống")
	@ValidStringMedium(message = "signerName không được chứa các ký tự đặc biệt")
	private String signerName;
	
	@NotNull(message = "signerPosition không được trống")
	@ValidStringMedium(message = "signerPosition không được chứa các ký tự đặc biệt")
	private String signerPosition;
	
	@ValidStringMedium(message = "orgReceiveName không được chứa các ký tự đặc biệt")
	private String orgReceiveName;
	
	@ValidStringMedium(message = "orgCreateName không được chứa các ký tự đặc biệt")
	private String orgCreateName;
	
	@NotEmpty(message = "summary không được trống")
	@ValidStringExtra(message = "summary không được chứa các ký tự đặc biệt")
	private String summary;
	
	@NotEmpty(message = "category không được trống")
	@ValidEnum(enumClass = DocCategory.class, message = "category không hợp lệ")
	private String category;
	
	@ValidListObjectId(message = "attachments có item không hợp lệ")
	private LinkedList<String> attachments=new LinkedList<String>();
	
	private int copies;
	
	private int pages;
	
	private boolean active;
	
	@Valid
	@NotNull(message = "owner không được trống")
	private ReqDocOwnerPartner owner;
	
	private List<String> receivers=new ArrayList<>();
	
	@ValidObjectId(message = "classifyTaskId không hợp lệ")
	private String classifyTaskId;
	
	@ValidObjectId(message = "leaderApproveTaskId không hợp lệ")
	private String leaderApproveTaskId;
	
	@NotNull(message = "iOfficeId không được trống nha")
	@ValidStringMedium(message = "iOfficeId không hợp lệ")
	public String iOfficeId;
	
	public ReqDocCreatePartner() {
		copies=1;
		pages=1;
		active=true;
	}
	
	public long getRegDate() {
		long THRESHOLD = 946684800000L;
		if (regDate < THRESHOLD) {
            // Giả định là seconds, chuyển đổi thành milliseconds
			regDate *= 1000;
        }
        return regDate;
	}
	
	public String getSecurity() {
		if(security!=null) {
			for(DocSecurity docSecurity:DocSecurity.values()) {
				if(docSecurity.getKey().toUpperCase().equals(security.toUpperCase())) {
					return docSecurity.getKey();
				}
			}
			return security;
		}
		return DocSecurity.Thuong.getKey();
	}
}
