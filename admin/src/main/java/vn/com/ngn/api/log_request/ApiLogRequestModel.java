package vn.com.ngn.api.log_request;

import lombok.Data;

@Data
public class ApiLogRequestModel {
    private String id;
    private long createdTime;
    private String method;
    private String protocol;
    private String query;
    private Object userAgent;
    private String addRemote;
    private String requestURL;
}
