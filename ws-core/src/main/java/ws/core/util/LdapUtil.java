package ws.core.util;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang3.StringUtils;
/**
 * This class can auth throught ldap or active directory
 * require application.properties attributes below
 
	# Active Directory
	auth.ldap.url=ldap://192.168.1.141:389
	auth.ldap.user.dn=ngn\\$username
	auth.ldap.seach.base=dc=ngn,dc=local
	auth.ldap.query.filter=sAMAccountName=$username
	auth.ldap.result.attribute=mail
	
	# OpenLdap
	auth.ldap.url=ldap://192.168.10.36:389
	auth.ldap.user.dn=uid=$username,ou=people,dc=ngn,dc=local
	auth.ldap.seach.base=dc=ngn,dc=local
	auth.ldap.query.filter=uid=$username
	auth.ldap.result.attribute=mail
 */


public class LdapUtil {
	
	public static void main(String args[]) {
		String ldapUrl = "ldap://10.1.1.180:389";
		String username = "quachanhdung2_vpubtp";
		String password = "123456";
		String userdn = "uid=$username,ou=people,dc=hanoi,dc=gov,dc=vn";
		String searchBase = "dc=hanoi,dc=gov,dc=vn";
		String filter = "uid=$username";
		String resultAttribute="distinguishedName,mail,cn,userPrincipalName,sAMAccountName";
		
		Map<String, Object> map = authByLdap(ldapUrl, username, password, userdn, searchBase, filter, resultAttribute);
		if(map!=null) {
			System.out.println("success");
			System.out.println("data: "+map.toString());
		}else {
			System.out.println("failed"); 
		}
	}
	
	
	private static Map<String, Object> authByLdap(String ldapUrl, String username, String password, String userDn,
			String searchBase, String queryFilter, String resultAttribute) {

		// Parse username
		// username@domain
		if (username.contains("@"))
			username = username.substring(0, username.indexOf("@"));
		// domain\\username
		if (username.contains("\\"))
			username = username.substring(username.indexOf("\\") + 1);

		// Format username
		userDn = StringUtils.replace(userDn, "$username", username);

		// Format query filter
		queryFilter = StringUtils.replace(queryFilter, "$username", username);
		
		System.out.println("ldapUrl: "+ldapUrl);
		System.out.println("userDn: "+userDn);
		System.out.println("queryFilter: "+queryFilter);
		System.out.println("searchBase: "+searchBase);
		System.out.println("resultAttribute: "+resultAttribute);
		
		// "*" means all non-operational attributes, and "+" means all operational
		// attributes.
		// The password-control attributes are operational attributes, which aren't
		// returned unless you specifically ask for them.
		String[] returningAttributes = StringUtils.split(resultAttribute, ",");

		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapUrl);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, userDn);
		env.put(Context.SECURITY_CREDENTIALS, password);

		SearchControls searchControls = new SearchControls();
		searchControls.setReturningAttributes(returningAttributes);
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

		Map<String, Object> map = new HashMap<String, Object>();

		DirContext ctx = null;

		try {

			ctx = new InitialDirContext(env);

			NamingEnumeration<SearchResult> results = ctx.search(searchBase, queryFilter, searchControls);			

			while (results.hasMoreElements()) {

				SearchResult result = results.next();

				Attributes attributes = result.getAttributes();

				for (NamingEnumeration<? extends Attribute> ae = attributes.getAll(); ae.hasMoreElements();) {

					Attribute attribute = ae.next();

					map.put(attribute.getID(), attribute.get());
				}
			}
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
					return null;
				}
			}
		}

		return map;
	}

}
