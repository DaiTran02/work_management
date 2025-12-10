package vn.com.ngn.page.organization.forms;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.organization.ApiCreateAndUpdateOrgModel;
import vn.com.ngn.api.organization.ApiKeyAndValueOrgModel;
import vn.com.ngn.api.organization.ApiOrganizationModel;
import vn.com.ngn.api.organization.ApiOrganizationService;
import vn.com.ngn.api.organization.ApiRoleOrganizationExpandsModel;
import vn.com.ngn.api.organization_category.ApiOrganizationCategoryFilterModel;
import vn.com.ngn.api.organization_category.ApiOrganizationCategoryModel;
import vn.com.ngn.api.organization_category.ApiOrganizationCategoryService;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.organization.models.CreateAndUpdateOrgModel;
import vn.com.ngn.page.organization.models.OrganizationModel;
import vn.com.ngn.page.organization.models.RoleOrganizationExpandsModel;
import vn.com.ngn.page.setting.forms.OrganizationCategoryForm;
import vn.com.ngn.utils.components.ButtonTemplate;
import vn.com.ngn.utils.components.DialogTemplate;
import vn.com.ngn.utils.components.NotificationTemplate;

public class EditOrgForm extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;

	private boolean checkNotNull = false;

	private OrganizationModel organizationModel;

	private TextField txtOrgName = new TextField("Tên đơn vị");
	private TextField txtSrc = new TextField("Mô tả");
	private Checkbox cbActive = new Checkbox("Hoạt động");
	private IntegerField txtStt = new IntegerField("Số thứ tự");
	private ComboBox<Pair<String, String>> cmbOrgCategory = new ComboBox<Pair<String,String>>("Loại đơn vị");
	private ButtonTemplate btnAddCategory = new ButtonTemplate(FontAwesome.Solid.PLUS.create());
	private TextField txtUnitCode = new TextField("Mã đơn vị quốc gia");
	private List<Pair<String, String>> listDataCmbOrgCate = new ArrayList<Pair<String,String>>();
	private List<ApiOrganizationModel> listDataOrg = new ArrayList<ApiOrganizationModel>();
	private ButtonTemplate btnAddRole = new ButtonTemplate("Thêm vai trò",FontAwesome.Solid.PLUS.create());
	private MultiSelectComboBox<Pair<String, RoleOrganizationExpandsModel>> multiRoleIsChoose = new MultiSelectComboBox<Pair<String,RoleOrganizationExpandsModel>>("Danh sách vai trò đã chọn");
	private List<Pair<String, RoleOrganizationExpandsModel>> listRoleIsChoose = new ArrayList<Pair<String,RoleOrganizationExpandsModel>>();
	private ComboBox<ApiKeyAndValueOrgModel> cmbLevel = new ComboBox<>("Cấp đơn vị");	
	private String id;
	private String parentId;
	private Runnable onRun;
	public EditOrgForm(String id,String parentId,Runnable onRun) {
		this.onRun = onRun;
		if(parentId!=null) {
			this.parentId = parentId;
		}
		buildLayout();
		configComponent();
		loadDataOfTypeOrg();
		initOrgLevel();
		if(id!=null) {
			this.id = id;
			checkNotNull = true;
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
		try {
			listDataOrg = new ArrayList<ApiOrganizationModel>();
			ApiResultResponse<List<ApiOrganizationModel>> getData = ApiOrganizationService.getListOrganization(0,10000,parentId,"",null);
			txtStt.setValue(getData.getTotal()+1);
			listDataOrg.addAll(getData.getResult());
		} catch (Exception e) {
			e.printStackTrace();
		}

		btnAddCategory.addClickListener(e->{
			openDialogViewOrgCategory();
		});
		
		btnAddRole.addClickListener(e->{
			openDialogAddRoleTemplate();
		});

	}

	public void loadData() {
		btnAddRole.setVisible(false);
		try {
			ApiResultResponse<ApiOrganizationModel> data = ApiOrganizationService.getOneOrg(id);
			organizationModel = new OrganizationModel(data.getResult());
		} catch (Exception e) {
			e.printStackTrace();
		}

		txtOrgName.setValue(organizationModel.getName());
		txtSrc.setValue(organizationModel.getDescription());
		txtStt.setValue(organizationModel.getOrder());
		txtUnitCode.setValue(organizationModel.getUnitCode());
		cbActive.setValue(organizationModel.isActive());
		if(listDataCmbOrgCate != null) {
			listDataCmbOrgCate.stream().forEach(model->{
				if(model.getKey() != null) {
					if(model.getKey().equals(organizationModel.getOrganizationCategoryId())) {
						cmbOrgCategory.setValue(model);
					}
				}
			});
		}
		if(organizationModel.getLevel() != null) {
			cmbLevel.setValue(organizationModel.getLevel());
		}
	}

	private Component createLayout() {
		VerticalLayout layout = new VerticalLayout();

		txtOrgName.setSizeFull();
		txtSrc.setSizeFull();
		txtStt.setSizeFull();
		txtStt.setStepButtonsVisible(true);
		txtStt.setValue(0);
		txtStt.setMin(0);
		cbActive.setValue(true);

		cbActive.setSizeFull();
		txtUnitCode.setSizeFull();

		HorizontalLayout hLayout1 = new HorizontalLayout();
		hLayout1.add(txtOrgName,txtSrc);
		hLayout1.setWidthFull();

		HorizontalLayout hLayout2 = new HorizontalLayout();
		hLayout2.add(txtStt,txtUnitCode);
		hLayout2.setWidthFull();

		cmbOrgCategory.setWidthFull();

		HorizontalLayout hLayoutCategory = new HorizontalLayout();
		hLayoutCategory.setWidthFull();
		btnAddCategory.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnAddCategory.setTooltipText("Thêm nhanh loại đơn vị");
		btnAddCategory.getStyle().set("margin-top", "30px");
		hLayoutCategory.add(cmbOrgCategory,btnAddCategory);
		
		HorizontalLayout hButton = new HorizontalLayout();
		hButton.add(cbActive,btnAddRole);
		hButton.setWidthFull();
		btnAddRole.getStyle().set("margin-left", "auto");
		btnAddRole.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		
		multiRoleIsChoose.setHelperText("Chức năng này giúp thêm những vai trò này cho đơn vị, khi bạn tạo một đơn vị mới.");
		multiRoleIsChoose.setWidthFull();
		multiRoleIsChoose.setVisible(false);
		
		cmbLevel.setWidthFull();
		
		layout.add(hLayout1,hLayout2,hLayoutCategory,cmbLevel,hButton,multiRoleIsChoose);

		return layout;
	}

	private void loadDataOfTypeOrg() {
		listDataCmbOrgCate = new ArrayList<Pair<String,String>>();
		ApiOrganizationCategoryFilterModel apiOrganizationCategoryFilterModel = new ApiOrganizationCategoryFilterModel();
		apiOrganizationCategoryFilterModel.setSkip(0);
		apiOrganizationCategoryFilterModel.setLimit(Integer.MAX_VALUE);
		apiOrganizationCategoryFilterModel.setActive(true);
		listDataCmbOrgCate.add(Pair.of(null,""));
		try {
			ApiResultResponse<List<ApiOrganizationCategoryModel>> data = ApiOrganizationCategoryService.getListOrganizationCategory(apiOrganizationCategoryFilterModel);
			data.getResult().stream().forEach(model->{
				listDataCmbOrgCate.add(Pair.of(model.getId(),model.getName()));
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		cmbOrgCategory.setItems(listDataCmbOrgCate);
		cmbOrgCategory.setItemLabelGenerator(Pair::getRight);
		if(!listDataCmbOrgCate.isEmpty()) {
			cmbOrgCategory.setValue(listDataCmbOrgCate.get(0));
		}
	}
	
	private void initOrgLevel() {
		List<ApiKeyAndValueOrgModel> list = new ArrayList<>();
		try {
			ApiResultResponse<List<ApiKeyAndValueOrgModel>> listData = ApiOrganizationService.getLevels();
			if(listData.isSuscces()) {
				list.addAll(listData.getResult());
			}
		} catch (Exception e) {
		}
		
		cmbLevel.setItems(list);
		cmbLevel.setItemLabelGenerator(ApiKeyAndValueOrgModel::getName);
		cmbLevel.setValue(list.get(0));
	}

	public void saveOrg() {
		if(invalid()==false) {
			return;
		}
		CreateAndUpdateOrgModel createAndUpdateOrgModel = new CreateAndUpdateOrgModel();
		createAndUpdateOrgModel.setName(txtOrgName.getValue());
		createAndUpdateOrgModel.setDescription(txtSrc.getValue());
		createAndUpdateOrgModel.setActive(cbActive.getValue());
		createAndUpdateOrgModel.setUnitCode(txtUnitCode.getValue());
		createAndUpdateOrgModel.setParentId(parentId);
		createAndUpdateOrgModel.setOrder(txtStt.getValue());
		createAndUpdateOrgModel.setOrganizationLevel(cmbLevel.getValue().getKey());
		if(cmbOrgCategory.getValue() == null) {
			createAndUpdateOrgModel.setOrganizationCategoryId(null);
		}else {
			createAndUpdateOrgModel.setOrganizationCategoryId(cmbOrgCategory.getValue().getKey());
		}
		if(checkNotNull==true) {
			doUpdateOrg(createAndUpdateOrgModel);
		}else {
			doCreateOrg(createAndUpdateOrgModel);
		}

	}

	private void doCreateOrg(CreateAndUpdateOrgModel createAndUpdateOrgModel) {
		ApiCreateAndUpdateOrgModel createOrgModel = new ApiCreateAndUpdateOrgModel(createAndUpdateOrgModel);
		try {
			ApiResultResponse<ApiOrganizationModel> createOrg = ApiOrganizationService.createNewOrg(createOrgModel);
			if(createOrg.getStatus()==201) {
				createRole(createOrg.getResult().getId());
				onRun.run();
			}else {
				NotificationTemplate.error(createOrg.getMessage());
			}
		} catch (Exception e) {
			txtUnitCode.setErrorMessage("Mã đơn vị đã sử dụng vui lòng nhập mã khác");
			txtUnitCode.setInvalid(true);
			txtUnitCode.focus();
			e.printStackTrace();
		}
	}

	private void doUpdateOrg(CreateAndUpdateOrgModel createAndUpdateOrgModel) {
		ApiCreateAndUpdateOrgModel updateOrgModel = new ApiCreateAndUpdateOrgModel(createAndUpdateOrgModel);
		try {
			ApiResultResponse<Object> updateOrg = ApiOrganizationService.updateOrg(id, updateOrgModel);
			if(updateOrg.getStatus() == 200) {
				onRun.run();
			}else {
				NotificationTemplate.error(updateOrg.getMessage());
			}
		} catch (Exception e) {
			txtUnitCode.setErrorMessage("Mã đơn vị đã sử dụng vui lòng nhập mã khác");
			txtUnitCode.setInvalid(true);
			txtUnitCode.focus();
			e.printStackTrace();
		}

	}
	
	private void createRole(String idOrg) {
		listRoleIsChoose.stream().forEach(model->{
			doCreateRole(idOrg,model.getValue());
		});
	}

	private void doCreateRole(String idOrg,RoleOrganizationExpandsModel roleOrganizationExpandsModel) {
		try {
			ApiRoleOrganizationExpandsModel apiRoleOrganizationExpandsModel = new ApiRoleOrganizationExpandsModel(roleOrganizationExpandsModel);
			apiRoleOrganizationExpandsModel.setRoleTemplateId(roleOrganizationExpandsModel.getId());
			ApiResultResponse<Object> createRole = ApiOrganizationService.createRole(idOrg, apiRoleOrganizationExpandsModel);
			if(createRole.getStatus() == 200) {

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void openDialogViewOrgCategory() {
		DialogTemplate dialogTemplate = new DialogTemplate("QUẢN LÝ LOẠI ĐƠN VỊ",()->{

		});

		OrganizationCategoryForm organizationCategoryForm = new OrganizationCategoryForm();
		organizationCategoryForm.addChangeListener(e->{
			loadDataOfTypeOrg();
		});
		dialogTemplate.add(organizationCategoryForm);
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setSizeFull();
		dialogTemplate.open();
	}
	
	private void loadDataForMultilRole() {
		if(!listRoleIsChoose.isEmpty()) {
			multiRoleIsChoose.setVisible(true);
			multiRoleIsChoose.setReadOnly(true);
		}
		multiRoleIsChoose.setItems(listRoleIsChoose);
		multiRoleIsChoose.setItemLabelGenerator(Pair::getKey);
		multiRoleIsChoose.setValue(listRoleIsChoose);
	}
	
	private void openDialogAddRoleTemplate() {
		DialogTemplate dialog = new DialogTemplate("THÊM VAI TRÒ TỪ VAI TRÒ MẪU",()->{
			
		});
		
		List<RoleOrganizationExpandsModel> listRoles = new ArrayList<RoleOrganizationExpandsModel>();
		listRoleIsChoose.stream().forEach(model->{
			listRoles.add(model.getValue());
		});
		
		RoleTemplateForm roleTemplateForm = new RoleTemplateForm(parentId,listRoles);
		
		dialog.add(roleTemplateForm);
		dialog.getBtnSave().addClickListener(e->{
			listRoleIsChoose.clear();
			roleTemplateForm.getListRoleIsChoose().stream().forEach(model->{
				listRoleIsChoose.add(Pair.of(model.getName(),model));
				loadDataForMultilRole();
			});
			
			dialog.close();
		});
		dialog.setWidth("90%");
		dialog.setHeight("90%");
		dialog.open();
	}

	private boolean invalid() {
		if(txtOrgName.getValue().isEmpty()) {
			txtOrgName.setErrorMessage("Không được để trống");
			txtOrgName.setInvalid(true);
			txtOrgName.focus();
			return false;
		}
		
		if(txtUnitCode.getValue().isBlank()) {
			txtUnitCode.setErrorMessage("Không được để trống");
			txtUnitCode.setInvalid(true);
			txtUnitCode.focus();
			return false;
		}
		
		return true;
	}

}
