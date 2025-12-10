package vn.com.ngn.views;

import java.util.List;
import java.util.Map;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import lombok.AllArgsConstructor;
import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.organization.ApiOrganizationModel;
import vn.com.ngn.api.organization.ApiOrganizationService;
import vn.com.ngn.page.user.model.BelongOrganizationsModel;
import vn.com.ngn.securitys.AuthenticatedUser;
import vn.com.ngn.utils.SessionUtil;
import vn.com.ngn.utils.components.VerticalLayoutTemplate;

@Route("login_first")
@AllArgsConstructor
@AnonymousAllowed
public class LoginByCode extends VerticalLayoutTemplate implements BeforeEnterObserver{
	private static final long serialVersionUID = 1L;
	
	private final AuthenticatedUser authenticatedUser;

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		
		Location location = event.getLocation();
		QueryParameters queryParameters = location.getQueryParameters();
		if(queryParameters.getParameters().containsKey("code")) {
			doAuthen(queryParameters.getParameters());
		}else {
			UI.getCurrent().getPage().setLocation("login");
		}
	}
	
	private void doAuthen(Map<String, List<String>> parameters) {
		String code = parameters.get("code").get(0);
		System.out.println("Code ne: "+code);
		if(isAuthentication(code)) {
			ApiOrganizationModel org = getOrg(parameters.get("org").get(0));
			if(org != null) {
				SessionUtil.setOrgId(org.getId());
				SessionUtil.setDetailOrg(new BelongOrganizationsModel(org));
			}
			UI.getCurrent().getPage().setLocation("org");
		}else {
//			UI.getCurrent().getPage().setLocation("login");
			System.out.println("Oang cho");
		}
	}
	
	private boolean isAuthentication(String code) {
		if(authenticatedUser.authenticateByCode(code)) {
			return true;
		}
		return false;
	}
	
	public ApiOrganizationModel getOrg(String idOrg) {
		try {
			ApiResultResponse<ApiOrganizationModel> data = ApiOrganizationService.getOneOrg(idOrg);
			return data.getResult();
		} catch (Exception e) {
			return null;
		}
	}

}
