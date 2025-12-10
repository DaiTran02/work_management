package ws.core.model.response.util;

import org.bson.Document;
import org.springframework.stereotype.Component;

import ws.core.model.embeded.LdapUser;

@Component
public class LdapUserUtil {
	
	public Document toAdminResponse(LdapUser ldapUser) {
		Document document=new Document();
		document.put("username", ldapUser.getUsername());
		document.put("fullName", ldapUser.getFullName());
		document.put("mail", ldapUser.getMail());
		document.put("mobile", ldapUser.getMobile());
		document.put("principalName", ldapUser.getUserPrincipalName());
		return document;
	}
}
