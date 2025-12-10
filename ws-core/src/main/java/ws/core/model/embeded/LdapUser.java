package ws.core.model.embeded;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
public class LdapUser implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Username (AD)
	 */
	@Field(value="username")
	private String username;
	
	/**
	 * Họ tên
	 */
	@Field(value="fullName")
	private String fullName;
	
	/**
	 * Email
	 */
	@Field(value="mail")
	private String mail;
	
	/**
	 * Email domain
	 */
	@Field(value="userPrincipalName")
	private String userPrincipalName;
	
	/**
	 * Số điện thoại
	 */
	@Field(value="mobile")
	private String mobile;
	
	public boolean isValid() {
		if(StringUtils.isEmpty(username)) {
			return false;
		}
		
		if(StringUtils.isEmpty(fullName)) {
			this.fullName=username;
		}
		
		if(fullName==null) {
			fullName=username;
		}
		
		if(userPrincipalName==null) {
			userPrincipalName=username;
		}
		
		return true;
	}
}
