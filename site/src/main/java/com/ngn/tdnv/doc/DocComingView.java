package com.ngn.tdnv.doc;

import java.util.List;
import java.util.Map;

import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.models.sign_in_org.SignInOrgModel;
import com.ngn.tdnv.doc.forms.DocListLazyLoadForm;
import com.ngn.utils.SessionUtil;
import com.ngn.views.MainLayout;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

@PageTitle(value = "Công văn đến")
@Route(value = "doc_incoming",layout = MainLayout.class)
@PermitAll
@JavaScript("./themes/site/js/code/highcharts.js")
@JavaScript("./themes/site/js/code/modules/treemap.js")
@JavaScript("./themes/site/js/code/modules/treegraph.js")
@JavaScript("./themes/site/js/code/modules/accessibility.js")
@JavaScript("./themes/site/js/code/modules/exporting.js")
@JavaScript("./themes/site/js/code/modules/sankey.js")
@JavaScript("./themes/site/js/code/modules/organization.js")
public class DocComingView extends VerticalLayout implements HasUrlParameter<String>{
	private static final long serialVersionUID = 1L;

	public DocComingView() {
		this.setSizeFull();
	}
	
	@Override
	public void setParameter(BeforeEvent event,@OptionalParameter String parameter) {
		Location location = event.getLocation();
		QueryParameters queryParameters = location.getQueryParameters();
		Map<String, List<String>> parameters = queryParameters.getParameters();
		UserAuthenticationModel userAuthenticationModel = SessionUtil.getUser();
		BelongOrganizationModel belongOrganizationModel = SessionUtil.getOrg();
		SignInOrgModel signInOrgModel = SessionUtil.getDetailOrg();
//		DocListForm listDocForm = new DocListForm(userAuthenticationModel, belongOrganizationModel, signInOrgModel,false,null,parameters,"CVDen");
		DocListLazyLoadForm listDocForm = new DocListLazyLoadForm(userAuthenticationModel, belongOrganizationModel, signInOrgModel,false,null,parameters,"CVDen");
		this.removeAll();
		this.add(listDocForm);
	}

}
