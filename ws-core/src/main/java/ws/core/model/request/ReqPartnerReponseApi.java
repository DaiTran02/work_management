package ws.core.model.request;

import lombok.Data;

@Data
public class ReqPartnerReponseApi <T>{
	private int errorCode;
	private Object message;
	private T data;
	private boolean isError;
	
	public boolean isSuccess() {
		return errorCode == 200 ? true : false;
	}
}
