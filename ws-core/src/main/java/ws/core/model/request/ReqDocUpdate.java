package ws.core.model.request;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.enums.DocCategory;
import ws.core.enums.DocSecurity;
import ws.core.model.request.embeded.ReqDocReceiver;
import ws.core.validation.ValidDatetimeLong;
import ws.core.validation.ValidEnum;
import ws.core.validation.ValidListObjectId;
import ws.core.validation.ValidObjectId;
import ws.core.validation.ValidStringExtra;
import ws.core.validation.ValidStringMedium;

@SuppressWarnings("serial")
@Data
public class ReqDocUpdate implements Serializable {
	@NotNull(message = "number không được trống")
	@ValidStringMedium(message = "number không được chứa các ký tự đặc biệt")
	public String number;
	
	@NotNull(message = "symbol không được trống")
	@ValidStringMedium(message = "symbol không được chứa các ký tự đặc biệt")
	public String symbol;
	
	@NotNull(message = "security không được trống")
	@ValidEnum(enumClass = DocSecurity.class, message = "security không hợp lệ")
	public String security;
	
	@NotNull(message = "regDate không được trống")
	@ValidDatetimeLong(message = "regDate không hợp lệ")
	public long regDate;
	
	@NotNull(message = "type không được trống")
	@ValidStringMedium(message = "type không được chứa các ký tự đặc biệt")
	public String type;
	
	@NotNull(message = "signerName không được trống")
	@ValidStringMedium(message = "signerName không được chứa các ký tự đặc biệt")
	public String signerName;
	
	@NotNull(message = "signerPosition không được trống")
	@ValidStringMedium(message = "signerPosition không được chứa các ký tự đặc biệt")
	public String signerPosition;
	
	@ValidStringMedium(message = "orgReceiveName không được chứa các ký tự đặc biệt")
	public String orgReceiveName;
	
	@ValidStringMedium(message = "orgCreateName không được chứa các ký tự đặc biệt")
	public String orgCreateName;
	
	@NotNull(message = "summary không được trống")
	@ValidStringExtra(message = "summary không được chứa các ký tự đặc biệt")
	public String summary;
	
	@NotNull(message = "category không được trống")
	@ValidEnum(enumClass = DocCategory.class, message = "category không hợp lệ")
	public String category;
	
	@ValidListObjectId(message = "attachments có item không hợp lệ")
	public LinkedList<String> attachments=new LinkedList<String>();
	
	public int copies;
	
	public int pages;
	
	public boolean active;
	
	public List<ReqDocReceiver> receivers=new ArrayList<ReqDocReceiver>();
	
	@ValidObjectId(message = "classifyTaskId không hợp lệ")
	public String classifyTaskId;
	
	@ValidObjectId(message = "leaderApproveTaskId không hợp lệ")
	public String leaderApproveTaskId;
	
	public ReqDocUpdate() {
		copies=1;
		pages=1;
		active=true;
	}
}
