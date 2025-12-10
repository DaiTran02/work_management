package com.ngn.tdnv.doc.forms.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ngn.api.organization.ApiFilterOrgModel;
import com.ngn.api.organization.ApiOrganizationModel;
import com.ngn.api.organization.ApiOrganizationService;
import com.ngn.api.organization.ApiUserOrganizationExModel;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.DetailsTemplate;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.shared.Registration;

public class PopoverUserOfOwner extends Popover{
	private static final long serialVersionUID = 1L;
	private String currentOrgId = SessionUtil.getOrgId();
	
	private List<ApiOrganizationModel> listData = new ArrayList<ApiOrganizationModel>();
	private VerticalLayout vLayout = new VerticalLayout();
	private Map<ApiOrganizationModel, ApiUserOrganizationExModel> mapOwnerSelected = new HashMap<ApiOrganizationModel, ApiUserOrganizationExModel>();
	
	public PopoverUserOfOwner() {
		super();
		this.add(vLayout);
		vLayout.setWidth("600px");
		vLayout.setHeight("400px");
	}
	
	public void loadData(String searchName) {
		listData.clear();
		mapOwnerSelected.clear();
		
		ApiOrganizationModel apiOrganizationModel = getOrg(currentOrgId);
		if(apiOrganizationModel != null) {
			if(apiOrganizationModel.getLevel().getKey().equals("room")) {
				ApiOrganizationModel parentOrg = getOrg(apiOrganizationModel.getParentId());
				listData.add(parentOrg);
				List<ApiOrganizationModel> listSubOrg = getListSubOrg(parentOrg.getId());
				if(listSubOrg != null) {
					listData.addAll(listSubOrg);
				}
			}else {
				List<ApiOrganizationModel> listSubOrg = getListSubOrg(currentOrgId);
				ApiOrganizationModel currentOrg = getOrg(currentOrgId);
				listSubOrg.add(currentOrg);
				if(listSubOrg != null) {
					listData.addAll(listSubOrg);
				}
			}
		}
		searchData(searchName);
	}
	
	public void doFocus() {
		loadData("");
		List<ApiOrganizationModel> listOrgFind = listData;
		
		listOrgFind.forEach(model->{
			DetailsTemplate detailsTemplate = new DetailsTemplate(model.getName());
			Grid<ApiUserOrganizationExModel> gridUser = new Grid<ApiUserOrganizationExModel>(ApiUserOrganizationExModel.class,false);
			detailsTemplate.add(gridUser);
			
			gridUser.addColumn(ApiUserOrganizationExModel::getFullName);
			gridUser.addComponentColumn(user->{
				ButtonTemplate btnChoose = new ButtonTemplate("Chọn");
				btnChoose.addThemeVariants(ButtonVariant.LUMO_SMALL,ButtonVariant.LUMO_SUCCESS);
				btnChoose.addClickListener(e->{
					mapOwnerSelected.put(model, user);
					fireEvent(new ClickEvent(this, false));
				});
				
				return btnChoose;
			}).setWidth("140px").setFlexGrow(0);
			
			gridUser.setHeight("300px");
			gridUser.addThemeVariants(GridVariant.LUMO_NO_BORDER);
			
			if(model.getUserOrganizationExpands() != null) {
				gridUser.setItems(model.getUserOrganizationExpands());
			}
			
			detailsTemplate.setOpened(false);
			
			vLayout.add(detailsTemplate);
		});
		this.open();
	}
	
	public void searchData(String nameOrg) {
		vLayout.removeAll();
		List<ApiOrganizationModel> listOrgFind = new ArrayList<ApiOrganizationModel>();
		if(nameOrg.isEmpty()) {
			listOrgFind = listData;
		}else {
			listOrgFind = listData.stream().filter(org->org.getName().toLowerCase().contains(nameOrg.toLowerCase())).toList();
		}
		 
		
		listOrgFind.forEach(model->{
			DetailsTemplate detailsTemplate = new DetailsTemplate(model.getName());
			Grid<ApiUserOrganizationExModel> gridUser = new Grid<ApiUserOrganizationExModel>(ApiUserOrganizationExModel.class,false);
			detailsTemplate.add(gridUser);
			
			gridUser.addColumn(ApiUserOrganizationExModel::getFullName);
			gridUser.addComponentColumn(user->{
				ButtonTemplate btnChoose = new ButtonTemplate("Chọn");
				btnChoose.addThemeVariants(ButtonVariant.LUMO_SMALL,ButtonVariant.LUMO_SUCCESS);
				btnChoose.addClickListener(e->{
					mapOwnerSelected.put(model, user);
					fireEvent(new ClickEvent(this, false));
				});
				
				return btnChoose;
			}).setWidth("140px").setFlexGrow(0);
			
			gridUser.setHeight("300px");
			gridUser.addThemeVariants(GridVariant.LUMO_NO_BORDER);
			
			if(model.getUserOrganizationExpands() != null) {
				gridUser.setItems(model.getUserOrganizationExpands());
			}
			
			detailsTemplate.setOpened(false);
			
			vLayout.add(detailsTemplate);
		});
		this.open();
	}
	
	
	private List<ApiOrganizationModel> getListSubOrg(String idOrg){
		ApiFilterOrgModel apiFilterOrgModel = new ApiFilterOrgModel();
		apiFilterOrgModel.setLimit(0);
		apiFilterOrgModel.setSkip(0);
		apiFilterOrgModel.setParentId(idOrg);
		ApiResultResponse<List<ApiOrganizationModel>> data = ApiOrganizationService.getListOrg(apiFilterOrgModel);
		if(data.isSuccess()) {
			return data.getResult();
		}
		return null;
	}
	
	private ApiOrganizationModel getOrg(String idOrg) {
		ApiResultResponse<ApiOrganizationModel> data = ApiOrganizationService.getOneOrg(idOrg);
		if(data.isSuccess()) {
			return data.getResult();
		}
		return null;
	}

	public Map<ApiOrganizationModel, ApiUserOrganizationExModel> getMapOwnerSelected() {
		return mapOwnerSelected;
	}
	
	public Registration addChangeListener(ComponentEventListener<ClickEvent> listener) {
		return addListener(ClickEvent.class, listener);
	}

	public static class ClickEvent extends ComponentEvent<PopoverUserOfOwner> {
		private static final long serialVersionUID = 1L;

		public ClickEvent(PopoverUserOfOwner source, boolean fromClient) {
			super(source, fromClient);
		}
	}
}
