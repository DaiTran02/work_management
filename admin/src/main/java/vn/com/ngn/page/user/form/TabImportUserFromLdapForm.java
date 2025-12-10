package vn.com.ngn.page.user.form;

import com.vaadin.flow.component.tabs.TabSheet;

import vn.com.ngn.page.user.form.details.ImportUserFromLdapToOrgForm;
import vn.com.ngn.page.user.form.details.ImportUserFromLdapToSystemForm;
import vn.com.ngn.utils.CheckPermissionUtil;

public class TabImportUserFromLdapForm extends TabSheet{
	private static final long serialVersionUID = 1L;
	private CheckPermissionUtil checkPermissionUtil = new CheckPermissionUtil();
	
	public TabImportUserFromLdapForm() {
		this.setSizeFull();
		ImportUserFromLdapToSystemForm importUserFormLdapToSystemForm = new ImportUserFromLdapToSystemForm();
		this.add("Thêm vào hệ thống", importUserFormLdapToSystemForm);
		
		if(!checkPermissionUtil.checkAdmin()) {
			this.remove(importUserFormLdapToSystemForm);
		}
		
		ImportUserFromLdapToOrgForm importUserFromLdapToOrgForm = new ImportUserFromLdapToOrgForm();
		this.add("Thêm vào hệ thống và đơn vị", importUserFromLdapToOrgForm);
	}

}
