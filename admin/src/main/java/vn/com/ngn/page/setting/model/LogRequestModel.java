package vn.com.ngn.page.setting.model;

import lombok.Data;
import vn.com.ngn.api.log_request.ApiLogRequestModel;

@Data
public class LogRequestModel {
    private String id;
    private long createdTime;
    private String method;
    private String protocol;
    private String query;
    private Object userAgent;
    private String addRemote;
    private String requestURL;
    
    public LogRequestModel() {
    	
    }
    
    public LogRequestModel(ApiLogRequestModel apiLogRequestModel) {
    	this.id = apiLogRequestModel.getId();
    	this.createdTime = apiLogRequestModel.getCreatedTime();
    	this.method = apiLogRequestModel.getMethod();
    	this.protocol = apiLogRequestModel.getProtocol();
    	this.query = apiLogRequestModel.getQuery();
    	this.userAgent = apiLogRequestModel.getUserAgent();
    	this.addRemote = apiLogRequestModel.getAddRemote();
    	this.requestURL = apiLogRequestModel.getRequestURL();
    }

}
