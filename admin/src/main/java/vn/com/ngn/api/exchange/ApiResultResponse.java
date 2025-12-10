package vn.com.ngn.api.exchange;

import lombok.Data;

@Data
public class ApiResultResponse <T>{
	private Integer status;
	private String message;
	private Integer total;
	private T result = null;
	
	public boolean isSuscces() {
		if(status == 200 || status == 201) {
			return true;
		}
		return false;
	}

}
