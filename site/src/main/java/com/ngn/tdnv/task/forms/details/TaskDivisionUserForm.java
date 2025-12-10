package com.ngn.tdnv.task.forms.details;

import java.util.ArrayList;
import java.util.List;

import com.ngn.api.organization.ApiOrganizationService;
import com.ngn.api.organization.ApiUserGroupExpandModel;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tasks.actions.ApiActionService;
import com.ngn.api.tasks.actions.ApiDoAssignModel;
import com.ngn.api.tasks.actions.ApiDoAssignModel.Creator;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.sign_in_org.SignInOrgModel;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.NotificationTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;

public class TaskDivisionUserForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private Grid<ApiUserGroupExpandModel> grid = new Grid<ApiUserGroupExpandModel>(ApiUserGroupExpandModel.class,false);
	private List<ApiUserGroupExpandModel> listModel = new ArrayList<ApiUserGroupExpandModel>();
	private ApiUserGroupExpandModel apiUserChoosen = new ApiUserGroupExpandModel();
	private List<Checkbox> listCheckboxs = new ArrayList<Checkbox>();

	private BelongOrganizationModel belongOrganizationModel = SessionUtil.getOrg();
	private SignInOrgModel signInOrgModel = SessionUtil.getDetailOrg();

	private String idTask;
	private boolean checkAssignee;
	private ApiUserGroupExpandModel apiUserAssignee;
	private ApiUserGroupExpandModel apiUserSupport;
	/**
	 * Form này dùng cho việc phân người xử lý và phân người hỗ trợ
	 * @param idTask
	 * @param checkAssignee
	 * @param apiUserAssignee
	 * @param apiUserSupport
	 */
	public TaskDivisionUserForm(String idTask,boolean checkAssignee,ApiUserGroupExpandModel apiUserAssignee,ApiUserGroupExpandModel apiUserSupport) {
		this.idTask = idTask;
		this.checkAssignee = checkAssignee;
		if(apiUserAssignee != null) {
			this.apiUserAssignee = apiUserAssignee;
		}
		if(apiUserSupport != null) {
			this.apiUserSupport = apiUserSupport;
		}
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(createGridLayout());
	}

	@Override
	public void configComponent() {

	}

	private void loadData() {
		listModel = new ArrayList<ApiUserGroupExpandModel>();
		ApiResultResponse<List<ApiUserGroupExpandModel>> data = ApiOrganizationService.getListUserOrganizationEx(belongOrganizationModel.getOrganizationId());
		if(data.isSuccess()) {
			listModel.addAll(data.getResult());
		}


		grid.setItems(listModel);
	}

	private Component createGridLayout() {
		grid = new Grid<ApiUserGroupExpandModel>(ApiUserGroupExpandModel.class,false);

		grid.addComponentColumn(model->{
			Checkbox checkbox = new Checkbox();
			checkbox.addClickListener(e->{
				if(checkbox.getValue() == true) {
					listCheckboxs.forEach(md->{
						md.setValue(false);
					});
					checkbox.setValue(true);
					apiUserChoosen = model;
				}
			});

			if(apiUserAssignee!=null) {
				if(apiUserAssignee.getUserId() != null) {
					if(apiUserAssignee.getUserId().equals(model.getUserId())) {
						checkbox.setValue(true);
					}
				}
			}
			
			if(apiUserSupport!=null) {
				if(apiUserSupport.getUserId() != null) {
					if(apiUserSupport.getUserId().equals(model.getUserId())) {
						checkbox.setValue(true);
					}
				}
			}

			listCheckboxs.add(checkbox);

			return checkbox;
		}).setWidth("50px").setFlexGrow(0);

		grid.addComponentColumn(model->{
			Span span = new Span(model.getMoreInfo().getFullName());

			return span;
		}).setHeader("Tên cán bộ");

		grid.addComponentColumn(model->{
			Span span = new Span(model.getPositionName());
			return span;
		}).setHeader("Chức vụ");


		return grid;
	}

	public void save() {
		ApiDoAssignModel apiDoAssignModel = new ApiDoAssignModel();
		apiDoAssignModel.setOrganizationUserId(apiUserChoosen.getUserId());
		apiDoAssignModel.setOrganizationUserName(apiUserChoosen.getFullName());

		Creator creator = apiDoAssignModel.new Creator();
		creator.setOrganizationId(belongOrganizationModel.getOrganizationId());
		creator.setOrganizationName(belongOrganizationModel.getOrganizationName());
		creator.setOrganizationUserId(signInOrgModel.getUserExpand().getUserId());
		creator.setOrganizationUserName(signInOrgModel.getUserExpand().getFullName());

		apiDoAssignModel.setCreator(creator);

		if(checkAssignee) {
			doAssignUserAssignee(apiDoAssignModel);
		}else {
			doAssignUserSupport(apiDoAssignModel);
		}

	}

	private void doAssignUserAssignee(ApiDoAssignModel apiDoAssignModel) {
		ApiResultResponse<Object> doAssignUserAssignee = ApiActionService.doAssignUserAssignee(idTask, belongOrganizationModel.getOrganizationId(), apiDoAssignModel);
		if(doAssignUserAssignee.isSuccess()) {
			NotificationTemplate.success("Phân xử lý thành công");
			fireEvent(new ClickEvent(this,false));
		}
	}

	private void doAssignUserSupport(ApiDoAssignModel apiDoAssignModel) {
		ApiResultResponse<Object> doAssignUserSupport = ApiActionService.doAssignUserSupport(idTask, belongOrganizationModel.getOrganizationId(), apiDoAssignModel);
		if(doAssignUserSupport.isSuccess()) {
			NotificationTemplate.success("Phân hỗ trợ thành công");
			fireEvent(new ClickEvent(this,false));
		}
	}

}
