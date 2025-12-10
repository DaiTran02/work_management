package vn.com.ngn.api.user;

import java.util.List;

import lombok.Data;

@Data
public class ApiImportUsersLdapToSystemModel {
	private List<String> usernames;
}
