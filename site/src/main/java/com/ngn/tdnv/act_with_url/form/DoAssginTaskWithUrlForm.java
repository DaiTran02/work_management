package com.ngn.tdnv.act_with_url.form;

import java.util.List;
import java.util.Map;

import com.ngn.api.doc.ApiDocModel;
import com.ngn.api.doc.ApiDocService;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.models.sign_in_org.SignInOrgModel;
import com.ngn.tdnv.doc.forms.create_task.TaskCreateForm;
import com.ngn.tdnv.doc.models.DocModel;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.NotificationTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class DoAssginTaskWithUrlForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private VerticalLayout vLayout = new VerticalLayout();
	private UserAuthenticationModel userAuthenticationModel = SessionUtil.getUser();
	private BelongOrganizationModel belongOrganizationModel = SessionUtil.getOrg();
	private SignInOrgModel signInOrgModel = SessionUtil.getDetailOrg();
	private Map<String, List<String>> parametters;
	public DoAssginTaskWithUrlForm(Map<String, List<String>> parametters) {
		this.parametters = parametters;
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(vLayout);
		checkParameters();
	}

	@Override
	public void configComponent() {
		
	}
	
	private void checkParameters() {
		if(parametters != null) {
			if(parametters.containsKey("func")) {
				String func = parametters.get("func").get(0);
				String idDoc = parametters.get("docid") == null ? "" : parametters.get("docid").get(0);
				String iOfficeId = "";
				DocModel docModel = null;
				if(idDoc.isEmpty()) {
					iOfficeId = parametters.get("iofficeid") == null ? "" : parametters.get("iofficeid").get(0);
					docModel = getADocByIofficeId(iOfficeId);
				}else {
					docModel = getADoc(idDoc);
				}
				
				if(docModel != null) {
					TaskCreateForm taskCreateForm = new TaskCreateForm(docModel.getId(), belongOrganizationModel, userAuthenticationModel, signInOrgModel, null);
					if(func.equals("task-assign")) {
						vLayout.add(taskCreateForm);
					}

					if(func.equals("view-task-assign")) {
						
					}
				}else {
					NotificationTemplate.error("Không tìm thấy văn bản trong hệ thống");
				}
				
			}
		}
	}


	private DocModel getADoc(String idDoc) {
		ApiResultResponse<ApiDocModel> dataDoc = ApiDocService.getAdoc(idDoc);
		if(dataDoc.isSuccess()) {
			DocModel oneDocModel = new DocModel(dataDoc.getResult());
			return oneDocModel;
		}
		return null;
	}
	
	private DocModel getADocByIofficeId(String iofficeId) {
		
		ApiResultResponse<ApiDocModel> dataDoc = ApiDocService.getAdocByOfficeId(iofficeId);
		if(dataDoc.isSuccess()) {
			DocModel docModel = new DocModel(dataDoc.getResult());
			return docModel;
		}
		
		return null;
	}

}
