package com.ngn.tdnv.personal_record.form;

import java.util.ArrayList;
import java.util.List;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.personal_record.ApiPersonalDetailModel;
import com.ngn.api.personal_record.ApiPersonalFilter;
import com.ngn.api.personal_record.ApiPersonalRecordModel;
import com.ngn.api.personal_record.ApiPersonalService;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tasks.ApiCreatorModel;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.tdnv.personal_record.model.DataChooseModel;
import com.ngn.utils.LocalDateUtil;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.ConfirmDialogTemplate;
import com.ngn.utils.components.DialogTemplate;
import com.ngn.utils.components.NotificationTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayoutVariant;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;

public class ListPersonalForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private UserAuthenticationModel currentUser = SessionUtil.getUser();
	private BelongOrganizationModel belongOrganizationModel = SessionUtil.getOrg();

	private SplitLayout splitLayout = new SplitLayout();
	private VerticalLayout vLayoutLeft = new VerticalLayout();
	private TextField txtSearch = new TextField();
	private ButtonTemplate btnSearch = new ButtonTemplate(FontAwesome.Solid.SEARCH.create());

	private VerticalLayout vLayoutRight = new VerticalLayout();
	private TextField txtTitle = new TextField("Tên hồ sơ");
	private TextArea txtDesr = new TextArea("Mô tả");

	private ButtonTemplate btnAddDoc = new ButtonTemplate("Thêm văn bản",FontAwesome.Solid.PLUS.create());
	private ButtonTemplate btnAddTask = new ButtonTemplate("Thêm nhiệm vụ",FontAwesome.Solid.PLUS.create());


	private ButtonTemplate btnAddPersonal = new ButtonTemplate("Thêm mới",FontAwesome.Solid.PLUS.create());
	private ButtonTemplate btnCreate = new ButtonTemplate("Tạo hồ sơ",FontAwesome.Solid.SAVE.create());
	private ButtonTemplate btnUpdate = new ButtonTemplate("Cập nhật",FontAwesome.Solid.SAVE.create());
	private ButtonTemplate btnTransferUser = new ButtonTemplate("Chuyển giao",FontAwesome.Solid.SHARE.create());

	private ApiPersonalRecordModel apiInputPersonal = new ApiPersonalRecordModel();

	private ApiPersonalDetailModel apiPersonalDetailModel = null;

	private TreeGrid<ApiPersonalRecordModel> grid = new TreeGrid<ApiPersonalRecordModel>(ApiPersonalRecordModel.class);
	private List<ApiPersonalRecordModel> listData = new ArrayList<ApiPersonalRecordModel>();

	private Grid<DataChooseModel> gridData = new Grid<DataChooseModel>(DataChooseModel.class,false);
	private List<DataChooseModel> listDataChoose = new ArrayList<DataChooseModel>();

	public ListPersonalForm() {
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		splitLayout = new SplitLayout(vLayoutLeft,vLayoutRight);
		splitLayout.addThemeVariants(SplitLayoutVariant.LUMO_SMALL);
		splitLayout.setSizeFull();

		vLayoutLeft.setWidth("30%");
		HorizontalLayout hLayoutFilter = new HorizontalLayout();
		hLayoutFilter.setWidthFull();
		hLayoutFilter.add(txtSearch,btnSearch);
		hLayoutFilter.expand(txtSearch);

		txtSearch.setPlaceholder("Nhập từ khóa để tìm");

		vLayoutLeft.add(hLayoutFilter);

		vLayoutRight.setWidth("70%");
		setUpdateFormInput();

		vLayoutLeft.add(createGrid());

		this.setSizeFull();
		this.add(splitLayout);
	}

	@Override
	public void configComponent() {
		txtSearch.addValueChangeListener(e->loadData());
		btnSearch.addClickListener(e->loadData());
		btnCreate.addClickListener(e->save());
		btnUpdate.addClickListener(e->save());
		btnAddPersonal.addClickListener(e->reset());
		btnAddDoc.addClickListener(e->openDialogDoc());
		btnAddTask.addClickListener(e->openDialogTask());
		btnTransferUser.addClickListener(e->openDialogTransfer());
	}

	public void loadData() {
		listData = new ArrayList<ApiPersonalRecordModel>();
		listData.addAll(ApiPersonalService.getAllpersonalByFilter(getSearch()).getResult());
		grid.setItems(listData,model->getChilds(model));
	}

	private List<ApiPersonalRecordModel> getChilds(ApiPersonalRecordModel father){
		return new ArrayList<ApiPersonalRecordModel>();
	}

	private void initData(String id) {
		apiPersonalDetailModel = new ApiPersonalDetailModel();
		listDataChoose = new ArrayList<DataChooseModel>();
		ApiResultResponse<ApiPersonalDetailModel> data = ApiPersonalService.getDetailPersonal(id);
		if(data.isSuccess()) {
			apiPersonalDetailModel = data.getResult();
			txtTitle.setValue(apiPersonalDetailModel.getTitle());
			txtDesr.setValue(apiPersonalDetailModel.getDescription());
			
			apiPersonalDetailModel.getDocs().forEach(model->{
				DataChooseModel dataChooseModel = new DataChooseModel(model);
				listDataChoose.add(dataChooseModel);
			});
			
			apiPersonalDetailModel.getTasks().forEach(model->{
				DataChooseModel dataChooseModel = new DataChooseModel(model);
				listDataChoose.add(dataChooseModel);
			});
			
			setUpdateFormInput();
			loadDataChoose();
		}

	}

	private Component createGrid() {
		grid = new TreeGrid<ApiPersonalRecordModel>();

		grid.addColumn(ApiPersonalRecordModel::getTitle).setHeader("Tên hồ sơ");
		grid.addComponentColumn(model->{
			HorizontalLayout hLayout = new HorizontalLayout();

			ButtonTemplate btnForward = new ButtonTemplate(FontAwesome.Solid.SHARE.create());
			btnForward.addThemeVariants(ButtonVariant.LUMO_TERTIARY,ButtonVariant.LUMO_SUCCESS);
			btnForward.addClickListener(e->openDialogTransfer());
			
			
			
			ButtonTemplate btnEdit = new ButtonTemplate(FontAwesome.Solid.EDIT.create());
			btnEdit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnEdit.addClickListener(e->{
				initData(model.getId());
			});

			ButtonTemplate btnDelete = new ButtonTemplate(FontAwesome.Solid.TRASH.create());
			btnDelete.addThemeVariants(ButtonVariant.LUMO_TERTIARY,ButtonVariant.LUMO_ERROR);
			btnDelete.addClickListener(e->delete(model.getId()));

			hLayout.add(btnForward,btnEdit,btnDelete);

			return hLayout;
		}).setHeader("Thao tác").setWidth("160px").setFlexGrow(0);
		grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		grid.addItemClickListener(e->{
			initData(e.getItem().getId());
		});

		return grid;
	}

	private void loadDataChoose() {
		gridData.setItems(listDataChoose);
	}

	private Component createGridDataChoose() {
		gridData = new Grid<DataChooseModel>(DataChooseModel.class,false);

		gridData.addColumn(DataChooseModel::getName).setHeader("Tiêu đề").setWidth("400px").setFlexGrow(0).setResizable(true);
		gridData.addColumn(DataChooseModel::getType).setHeader("Loại thông tin");
		gridData.addColumn(DataChooseModel::getCreateTime).setHeader("Ngày tạo");
		gridData.addComponentColumn(model->{
			ButtonTemplate btnDelete = new ButtonTemplate(FontAwesome.Solid.TRASH.create());
			btnDelete.addThemeVariants(ButtonVariant.LUMO_TERTIARY,ButtonVariant.LUMO_ERROR);
			btnDelete.addClickListener(e->{
				listDataChoose.removeIf(item->item.getId().equals(model.getId()));
				loadDataChoose();
			});
			
			
			return btnDelete;
		}).setHeader("Thao tác").setWidth("100px").setFlexGrow(0);
		
		gridData.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

		return gridData;
	}

	private void delete(String id) {
		ConfirmDialogTemplate confirmDialogTemplate = new ConfirmDialogTemplate("Xóa hồ sơ");
		confirmDialogTemplate.setText("Xác nhận xóa hồ sơ");
		confirmDialogTemplate.open();
		confirmDialogTemplate.addConfirmListener(e->{
			ApiResultResponse<Object> dele = ApiPersonalService.deletePersonal(id);
			if(dele.isSuccess()) {
				NotificationTemplate.success("Xóa thành công");
				loadData();
			}
		});

	}


	private void setUpdateFormInput() {
		vLayoutRight.removeAll();
		vLayoutRight.getStyle().setPadding("20px");

		txtTitle.setWidthFull();
		txtDesr.setWidthFull();
		txtDesr.setHeight("200px");

		H4 header = new H4("Tạo và cập nhật hồ sơ");
		header.getStyle().setMargin("0 auto");

		HorizontalLayout hLayoutEvent = new HorizontalLayout();
		hLayoutEvent.setWidthFull();
		hLayoutEvent.add(btnAddDoc,btnAddTask,apiPersonalDetailModel == null ? btnCreate : btnUpdate,btnTransferUser);
		
		btnTransferUser.setEnabled(apiPersonalDetailModel == null ? false : true);
		
		btnUpdate.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

		btnAddPersonal.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

		vLayoutRight.add(header,btnAddPersonal,txtTitle,txtDesr,hLayoutEvent,createGridDataChoose());
	}

	private void save() {
		if(isValid() == false)
			return;

		apiInputPersonal = new ApiPersonalRecordModel();
		apiInputPersonal.setTitle(txtTitle.getValue());
		apiInputPersonal.setDescription(txtDesr.getValue());
		apiInputPersonal.setUserId(currentUser.getId());
		
		List<String> listDocs = new ArrayList<String>();
		
		List<String> listTask = new ArrayList<String>();
		
		listDataChoose.forEach(model->{
			if(model.getTypeId().equals("Doc")) {
				listDocs.add(model.getId());
			}
			
			if(model.getTypeId().equals("Task")) {
				listTask.add(model.getId());
			}
			
		});
		apiInputPersonal.setDocs(listDocs);
		apiInputPersonal.setTasks(listTask);
		
		apiInputPersonal.setCreator(getCreator());
		
		System.out.println("Check data: "+apiInputPersonal );
		
		if(apiPersonalDetailModel == null) {
			doCreate();
			System.out.println("check 1");
		}else {
			System.out.println("Check 2");
			doUpdate();
		}
	}

	private void doCreate() {
		ApiResultResponse<Object> create = ApiPersonalService.createPersonal(apiInputPersonal);
		if(create.isSuccess()) {
			NotificationTemplate.success("Thành công");
			loadData();
		}
	}

	private void doUpdate() {
		ApiResultResponse<Object> update = ApiPersonalService.updatePersonal(apiPersonalDetailModel.getId(), apiInputPersonal);
		if(update.isSuccess()) {
			NotificationTemplate.success("Thành công");
			loadData();
		}
	}

	private void reset() {
		txtTitle.clear();
		txtDesr.clear();
		apiInputPersonal = new ApiPersonalRecordModel();
		apiPersonalDetailModel = null;
		setUpdateFormInput();
	}

	private boolean isValid() {
		if(txtTitle.getValue().isEmpty()) {
			txtTitle.setErrorMessage("Không được để trống");
			txtTitle.setInvalid(true);
			txtTitle.focus();
			return false;
		}
		return true;
	}

	private ApiCreatorModel getCreator() {
		ApiCreatorModel apiCreatorModel = new ApiCreatorModel();
		apiCreatorModel.setOrganizationUserId(currentUser.getId());
		apiCreatorModel.setOrganizationUserName(currentUser.getFullName());
		apiCreatorModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
		apiCreatorModel.setOrganizationName(belongOrganizationModel.getOrganizationName());
		return apiCreatorModel;
	}

	private ApiPersonalFilter getSearch() {
		ApiPersonalFilter apiPersonalFilter = new ApiPersonalFilter();

		apiPersonalFilter.setUserId(currentUser.getId());
		apiPersonalFilter.setKeySearch(txtSearch.getValue());

		return apiPersonalFilter;
	}
	
	private void openDialogDoc() {
		DialogTemplate dialogTemplate = new DialogTemplate("Chọn văn bản");

		ChooseDocForm chooseDocForm = new ChooseDocForm();

		dialogTemplate.getBtnSave().addClickListener(e->{
			chooseDocForm.getListDocChoose().forEach(model->{
				DataChooseModel dataChooseModel = new DataChooseModel();
				dataChooseModel.setId(model.getId());
				dataChooseModel.setName(model.getSummary());
				dataChooseModel.setType("Văn bản");
				dataChooseModel.setTypeId("Doc");
				dataChooseModel.setCreateTime(LocalDateUtil.dfDate.format(model.getCreatedTime()));
				listDataChoose.add(dataChooseModel);
			});
			loadDataChoose();
			dialogTemplate.close();
		});
		dialogTemplate.add(chooseDocForm);

		dialogTemplate.getBtnSave().setText("Xác nhận chọn văn bản");
		dialogTemplate.setWidth("90%");
		dialogTemplate.setHeightFull();

		dialogTemplate.open();
	}
	
	private void openDialogTask() {
		DialogTemplate dialogTemplate = new DialogTemplate("Chọn nhiệm vụ");
		ChooseTaskForm chooseTaskForm = new ChooseTaskForm();
		dialogTemplate.getBtnSave().addClickListener(e->{
			chooseTaskForm.getListTaskChoose().forEach(model->{
				DataChooseModel dataChooseModel = new DataChooseModel(model);
				listDataChoose.add(dataChooseModel);
			});
			
			loadDataChoose();
			dialogTemplate.close();
		});
		
		
		dialogTemplate.add(chooseTaskForm);
		dialogTemplate.getBtnSave().setText("Xác nhận chọn nhiệm vụ");
		dialogTemplate.setHeightFull();
		dialogTemplate.setWidth("90%");
		dialogTemplate.open();
	}
	
	private void openDialogTransfer() {
		DialogTemplate dialogTemplate = new DialogTemplate("Chuyển giao hồ sơ");
		ChooseUserTransferForm chooseUserTransferForm = new ChooseUserTransferForm();
		
		dialogTemplate.getBtnSave().addClickListener(e->{
			doTransferUser(chooseUserTransferForm.getUserIsChoose().getUserId());
			dialogTemplate.close();
		});
		
		dialogTemplate.add(chooseUserTransferForm);
		dialogTemplate.getBtnSave().setText("Xác nhận chọn");
		dialogTemplate.setHeightFull();
		dialogTemplate.setWidth("60%");
		dialogTemplate.open();
	}
	
	private void doTransferUser(String userId) {
		ApiResultResponse<Object> transfer = ApiPersonalService.transferPersonal(apiPersonalDetailModel.getId(), userId);
		if(transfer.isSuccess()) {
			NotificationTemplate.success("Đã chuyển giao thành công");
			loadData();
			reset();
		}
	}

}
