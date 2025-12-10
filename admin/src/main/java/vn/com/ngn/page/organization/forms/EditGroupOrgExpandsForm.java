package vn.com.ngn.page.organization.forms;

import java.util.Collections;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.organization.ApiGroupOrganizationExpandsModel;
import vn.com.ngn.api.organization.ApiOrganizationService;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.organization.models.GroupOrganizationExpandsModel;

public class EditGroupOrgExpandsForm extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private TextField txtNameGroup = new TextField("Tên tổ giao việc");
	private TextField txtDescription = new TextField("Mô tả");
	private IntegerField txtStt = new IntegerField("Số thứ tự");
	
	private String groupId;
	private String parentId;
	private Runnable onRun;
	private int countData;
	private List<GroupOrganizationExpandsModel> listModel;
	public EditGroupOrgExpandsForm(String parentId,String groupId,int countData,Runnable onRun,List<GroupOrganizationExpandsModel> listModel) {
		this.parentId = parentId;
		this.onRun = onRun;
		this.countData = countData;
		this.listModel = listModel;
		buildLayout();
		configComponent();
		
		if(groupId!=null) {
			this.groupId = groupId;
			loadData();
		}
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(createLayout());
	}

	@Override
	public void configComponent() {
		txtStt.setValue(countData+1);
	}
	
	public void loadData() {
		try {
			ApiResultResponse<ApiGroupOrganizationExpandsModel> apiGroupOrganizationExpandsModel = ApiOrganizationService.getAGroup(parentId, groupId);
			txtNameGroup.setValue(apiGroupOrganizationExpandsModel.getResult().getName());
			txtDescription.setValue(apiGroupOrganizationExpandsModel.getResult().getDescription());
			txtStt.setValue(apiGroupOrganizationExpandsModel.getResult().getOrder());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private Component createLayout() {
		VerticalLayout layout = new VerticalLayout();
		
		txtNameGroup.setSizeFull();
		txtDescription.setSizeFull();
		txtStt.setSizeFull();
		txtStt.setValue(1);
		txtStt.setStepButtonsVisible(true);
		txtStt.setMin(0);
		
		HorizontalLayout hLayout = new HorizontalLayout();
		hLayout.add(txtNameGroup,txtDescription);
		hLayout.setSizeFull();
		
		layout.add(hLayout,txtStt);
		layout.setSizeFull();
		
		return layout;
	}
	
	public void doSave() {
		if(!invalid()) {
			return;
		}
		GroupOrganizationExpandsModel groupOrganizationExpandsModel = new GroupOrganizationExpandsModel();
		groupOrganizationExpandsModel.setName(txtNameGroup.getValue());
		groupOrganizationExpandsModel.setDescription(txtDescription.getValue());
		groupOrganizationExpandsModel.setUserIds(Collections.emptyList());
		groupOrganizationExpandsModel.setOrder(txtStt.getValue());
		
		System.out.println("System: "+groupOrganizationExpandsModel);
		if(groupId!=null) {
			doUpdateGroup(groupOrganizationExpandsModel);
		}else {
			doCreateGroup(groupOrganizationExpandsModel);
		}
		
	}
	
	private void doCreateGroup(GroupOrganizationExpandsModel groupOrganizationExpandsModel) {
		try {
			ApiGroupOrganizationExpandsModel apiGroupOrganizationExpandsModel = new ApiGroupOrganizationExpandsModel(groupOrganizationExpandsModel);
			ApiResultResponse<Object> createGroup = ApiOrganizationService.createGroup(parentId, apiGroupOrganizationExpandsModel);
			if(createGroup.getStatus()==200) {
				onRun.run();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void doUpdateGroup(GroupOrganizationExpandsModel groupOrganizationExpandsModel) {
		try {
			ApiGroupOrganizationExpandsModel apiGroupOrganizationExpandsModel = new ApiGroupOrganizationExpandsModel(groupOrganizationExpandsModel);
			ApiResultResponse<Object> updateGroup = ApiOrganizationService.updateGroup(parentId, groupId, apiGroupOrganizationExpandsModel);
			if(updateGroup.getStatus() == 200 || updateGroup.getStatus() == 201) {
				onRun.run();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean invalid() {
		if(txtNameGroup.getValue().isBlank()) {
			txtNameGroup.setErrorMessage("Không được để trống");
			txtNameGroup.setInvalid(true);
			txtNameGroup.focus();
			return false;
		}
		
		for(GroupOrganizationExpandsModel groupOrganizationExpandsModel : listModel) {
			if(groupOrganizationExpandsModel.getOrder() == txtStt.getValue()) {
				txtStt.setErrorMessage("Số thự tự bị trùng");
				txtStt.setInvalid(true);
				txtStt.focus();
				return false;
			}
		}
		
		return true;
	}

}
