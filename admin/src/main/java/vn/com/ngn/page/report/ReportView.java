package vn.com.ngn.page.report;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;
import vn.com.ngn.page.report.forms.ReportOrgForm;
import vn.com.ngn.page.report.forms.ReportUserSystemForm;
import vn.com.ngn.page.report.forms.ReportUsersUsingForm;
import vn.com.ngn.utils.CheckPermissionUtil;
import vn.com.ngn.utils.components.TabsTemplate;
import vn.com.ngn.views.MainLayout;

@Route(value = "report",layout = MainLayout.class)
@PermitAll
@PageTitle(value = "Báo cáo người dùng")
public class ReportView extends TabsTemplate{
	private static final long serialVersionUID = 1L;
	
	public ReportView() {
		this.setSizeFull();
		CheckPermissionUtil checkPermissionUtil = new CheckPermissionUtil();
		if(checkPermissionUtil.checkAdmin()) {
			addTab(new Span("Đơn vị"), new ReportOrgForm());
			addTab(new Span("Người dùng sử dụng trong đơn vị"), new ReportUsersUsingForm());
			addTab(new Span("Người dùng trong hệ thống"), new ReportUserSystemForm());
		}else {
			addTab(new Span("Người dùng sử dụng trong đơn vị"), new ReportUsersUsingForm());
		}
		
	}

}
