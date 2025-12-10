package vn.com.ngn.page.setting.forms;

import java.io.IOException;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.organization_category.ApiOrganizationCategoryModel;
import vn.com.ngn.api.organization_category.ApiOrganizationCategoryService;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.utils.components.NotificationTemplate;

public class EditOrganizationCategoryForm extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;

	private TextField txtName = new TextField("Loại đơn vị");
	private TextField txtDsr = new TextField("Mô tả");
	private IntegerField txtStt = new IntegerField("Số thứ tự");
	private Checkbox cbActive = new Checkbox("Hoạt động");
	
	
	private String idOrgCategory;
	private int countData;
	public EditOrganizationCategoryForm(String idOrgCategory,int countData) {
		this.countData = countData;
		buildLayout();
		configComponent();
		if(idOrgCategory != null) {
			this.idOrgCategory = idOrgCategory;
			loadData();
		}
	}
	
	@Override
	public void buildLayout() {
		this.setSizeFull();
		
		txtName.setWidthFull();
		txtDsr.setWidthFull();
		txtStt.setWidthFull();
		txtStt.setValue(countData+1);
		txtStt.setStepButtonsVisible(true);
		
		cbActive.setValue(true);
		
		cbActive.getStyle().set("margin-left", "auto");
		
		this.add(txtName,txtDsr,txtStt,cbActive);
	}

	@Override
	public void configComponent() {
		
	}
	
	public void loadData() {
		try {
			ApiResultResponse<ApiOrganizationCategoryModel> data = ApiOrganizationCategoryService.getAOrg(idOrgCategory);
			ApiOrganizationCategoryModel  apiOrganizationCategoryModel = data.getResult();
			txtName.setValue(apiOrganizationCategoryModel.getName());
			txtDsr.setValue(apiOrganizationCategoryModel.getDescription());
			txtStt.setValue(apiOrganizationCategoryModel.getOrder());
			cbActive.setValue(apiOrganizationCategoryModel.isActive());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void saveOrgCate() {
		ApiOrganizationCategoryModel apiOrganizationCategoryModel = new ApiOrganizationCategoryModel();
		apiOrganizationCategoryModel.setName(txtName.getValue());
		apiOrganizationCategoryModel.setDescription(txtDsr.getValue());
		apiOrganizationCategoryModel.setActive(cbActive.getValue());
		apiOrganizationCategoryModel.setOrder(txtStt.getValue());
		
		if(idOrgCategory!=null) {
			doUpdateOrgCate(apiOrganizationCategoryModel);
		}else {
			doCreateOrgCate(apiOrganizationCategoryModel);
		}
	}
	
	private void doCreateOrgCate(ApiOrganizationCategoryModel apiOrganizationCategoryModel) {
		try {
			ApiResultResponse<Object> createOrg = ApiOrganizationCategoryService.createOrg(apiOrganizationCategoryModel);
			if(createOrg.isSuscces()) {
				NotificationTemplate.success("Thành công");
				fireEvent(new ClickEvent(this,false));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void doUpdateOrgCate(ApiOrganizationCategoryModel apiOrganizationCategoryModel) {
		try {
			ApiResultResponse<Object> updateOrg = ApiOrganizationCategoryService.updateOrgCate(idOrgCategory, apiOrganizationCategoryModel);
			if(updateOrg.isSuscces()) {
				NotificationTemplate.success("Cập nhật thành công");
				fireEvent(new ClickEvent(this,false));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Registration addChangeListener(ComponentEventListener<ClickEvent> listener) {
		return addListener(ClickEvent.class, listener);
	}

	public static class ClickEvent extends ComponentEvent<EditOrganizationCategoryForm> {
		private static final long serialVersionUID = 1L;

		public ClickEvent(EditOrganizationCategoryForm source, boolean fromClient) {
			super(source, fromClient);
		}
	}

}








