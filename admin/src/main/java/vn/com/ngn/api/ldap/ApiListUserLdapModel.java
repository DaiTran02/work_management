package vn.com.ngn.api.ldap;

import lombok.Data;

@Data
public class ApiListUserLdapModel {
    private String username;
    private String fullName;
    private Object mail;
    private Object mobile;
    private String principalName;
    
    public String getMailText() {
    	return mail == null ? "Chưa cập nhật" : mail.toString();
    }
    
    public String getMobileText() {
    	return mobile == null ? "Chưa cập nhật" : mobile.toString();
    }
}
