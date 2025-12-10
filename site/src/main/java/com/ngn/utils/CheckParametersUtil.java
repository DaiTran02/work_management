package com.ngn.utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ngn.models.BelongOrganizationModel;
import com.ngn.setting.ChooseOrgDialog;
import com.ngn.utils.components.NotificationTemplate;
import com.vaadin.flow.component.UI;

public class CheckParametersUtil {
	
	private Map<String, List<String>> mapParameters;
	public CheckParametersUtil(Map<String, List<String>> mapParameters) {
		this.mapParameters = mapParameters;
	}
	
	public void checkOrgSaveInSession(Map<String, List<String>> parameters) {
		if(SessionUtil.getOrgId()!=null) {
			if(parameters.containsKey("org")) {
				String idOrg = parameters.get("org").get(0);
				
				if(idOrg.isEmpty() || idOrg == null) {
					System.out.println("Ko tim thay org");
					UI.getCurrent().getPage().setLocation("authen_fail");
				}
				
				
				List<BelongOrganizationModel> listBelongOrganizationModels = SessionUtil.getBelongOrg();
				List<BelongOrganizationModel> listWillSaveToSession = new ArrayList<BelongOrganizationModel>();
				for(BelongOrganizationModel belongOrganizationModel : listBelongOrganizationModels) {
					if(belongOrganizationModel.getOrganizationId() == idOrg) {
						listWillSaveToSession.add(belongOrganizationModel);
					}
				}
				
				if(!listWillSaveToSession.isEmpty()) {
					@SuppressWarnings("unused")
					ChooseOrgDialog chooseOrgDialog = new ChooseOrgDialog(listWillSaveToSession,false,parameters,false);
					handleParam();
				}else {
					NotificationTemplate.error("Không tìm thấy đơn vị chỉ định");
				}
				
				
			}else {
				@SuppressWarnings("unused")
				ChooseOrgDialog chooseOrgDialog = new ChooseOrgDialog(SessionUtil.getBelongOrg(),false,parameters,false);
				handleParam();
			}
		}
		else {
			if(parameters.containsKey("org")) {
				String idOrg = parameters.get("org").get(0);
				
				if(idOrg.isEmpty() || idOrg == null) {
					System.out.println("Ko tim thay org");
					UI.getCurrent().getPage().setLocation("authen_fail");
				}
				
				List<BelongOrganizationModel> listBelongOrganizationModels = SessionUtil.getBelongOrg();
				List<BelongOrganizationModel> listWillSaveToSession = new ArrayList<BelongOrganizationModel>();
				for(BelongOrganizationModel belongOrganizationModel : listBelongOrganizationModels) {
					if(belongOrganizationModel.getOrganizationId() == idOrg) {
						listWillSaveToSession.add(belongOrganizationModel);
					}
				}
				if(!listWillSaveToSession.isEmpty()) {
					@SuppressWarnings("unused")
					ChooseOrgDialog chooseOrgDialog = new ChooseOrgDialog(listWillSaveToSession,false,parameters,false);
					handleParam();
				}else {
					NotificationTemplate.error("Không tìm thấy đơn vị chỉ định");
				}
			}else {
				ChooseOrgDialog chooseOrgDialog = new ChooseOrgDialog(SessionUtil.getBelongOrg(),false,parameters,false);
				chooseOrgDialog.getFooter().removeAll();
				chooseOrgDialog.open();
				chooseOrgDialog.getBtnClose().addClickListener(e->{
					chooseOrgDialog.close();
				});
				chooseOrgDialog.setCloseOnOutsideClick(false);
			}
		}
	}
	
	public void handleParam() {
		StringBuilder url = new StringBuilder();
		if(mapParameters != null) {
			if(mapParameters.containsKey("nav")) {
				String valueNav = mapParameters.get("nav").get(0);
				//Van ban di
				if(valueNav.equals("doc_away")) {
					url.append("/doc_away?");
					if(mapParameters.containsKey("docnum")) {
						url.append("docnumber="+mapParameters.get("docnum").get(0));
					}
					if(mapParameters.containsKey("docsyb")) {
						url.append("&docsymbol="+mapParameters.get("docsyb").get(0));
					}
					if(mapParameters.containsKey("func")) {
						url.append("&func="+mapParameters.get("func").get(0));
					}
					if(mapParameters.containsKey("docid")) {
						String value = mapParameters.get("docid") == null ? "" : mapParameters.get("docid").get(0);
						url.append("&docid="+value);
					}
					if(mapParameters.containsKey("iofficeid")) {
						url.append("&iofficeid="+mapParameters.get("iofficeid").get(0));
					}
				}
				else if(valueNav.equals("doc_incoming")) {
					url.append("/doc_incoming?");
					if(mapParameters.containsKey("docnum")) {
						url.append("docnumber="+mapParameters.get("docnum").get(0));
					}
					if(mapParameters.containsKey("docsyb")) {
						url.append("&docsymbol="+mapParameters.get("docsyb").get(0));
					}
					if(mapParameters.containsKey("func")) {
						url.append("&func="+mapParameters.get("func").get(0));
					}
					if(mapParameters.containsKey("docid")) {
						String value = mapParameters.get("docid") == null ? "" : mapParameters.get("docid").get(0);
						url.append("&docid="+value);
					}
					if(mapParameters.containsKey("iofficeid")) {
						url.append("&iofficeid="+mapParameters.get("iofficeid").get(0));
					}
				}
				
				
				//Da giao
				else if(valueNav.equals("task_ower")) {
					url.append("/task_ower?");
					if(mapParameters.containsKey("docnum")) {
						url.append("docnumber="+mapParameters.get("docnum").get(0));
					}
					if(mapParameters.containsKey("docsyb")) {
						url.append("&docsymbol="+mapParameters.get("docsyb").get(0));
					}

					if(mapParameters.containsKey("keysearch")) {
						url.append("&keysearch="+mapParameters.get("keysearch").get(0));
					}

					if(mapParameters.containsKey("detail")) {
						url.append("&detail="+mapParameters.get("detail").get(0));
					}


				}
				//Duoc giao
				else if(valueNav.equals("task_assignee")) {
					url.append("/task?");
					if(mapParameters.containsKey("keysearch")) {
						url.append("&keysearch="+mapParameters.get("keysearch").get(0));
					}

					if(mapParameters.containsKey("detail")) {
						url.append("&detail="+mapParameters.get("detail").get(0));
					}
				}
				//Ho tro
				else if(valueNav.equals("task_support")) {
					url.append("/task_support?");
					if(mapParameters.containsKey("keysearch")) {
						url.append("&keysearch="+mapParameters.get("keysearch").get(0));
					}

					if(mapParameters.containsKey("detail")) {
						url.append("&detail="+mapParameters.get("detail").get(0));
					}
				}
				//Theo doi
				else if(valueNav.equals("task_follower")) {
					url.append("/task_follower?");
					if(mapParameters.containsKey("keysearch")) {
						url.append("&keysearch="+mapParameters.get("keysearch").get(0));
					}

					if(mapParameters.containsKey("detail")) {
						url.append("&detail="+mapParameters.get("detail").get(0));
					}
				}
				
				else if(valueNav.equals("dashboard")) {
					url.append("/dashboard?");
				}
				
				else if(valueNav.equals("report")) {
					url.append("/report");
				}
				if(mapParameters.containsKey("year")) {
					setYear(mapParameters.get("year").get(0));
				}
			}else {
				url.append("/dashboard?");
			}
		}else {
			url.append("/dashboard?");
		}
		UI.getCurrent().getPage().setLocation(url.toString());
	}
	
	private void setYear(String year) {
		int yearParam = Integer.parseInt(year);
		for(int i = 2019 ; i <= LocalDate.now().getYear();i++) {
			if(yearParam == i) {
				SessionUtil.setYear(i);
				break;
			}
		}
	}
	
	

}