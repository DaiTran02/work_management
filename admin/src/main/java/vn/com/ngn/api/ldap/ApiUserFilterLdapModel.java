package vn.com.ngn.api.ldap;

import lombok.Data;

@Data
public class ApiUserFilterLdapModel {
	private String username;
	private int skip;
	private int limit;
}
