package com.ngn.api.result;

import lombok.Data;

@Data
public class ApiResultResponse<T> {
	private Integer status;
	private String message;
	private Integer total;
	private T result = null;
	
	public boolean isSuccess() {
		return (status == 200 || status == 201);
	}
}
