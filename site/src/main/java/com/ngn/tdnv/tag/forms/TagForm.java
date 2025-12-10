package com.ngn.tdnv.tag.forms;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tags.ApiInputAddclassModel;
import com.ngn.api.tags.ApiTagCreatetorModel;
import com.ngn.api.tags.ApiTagFilterModel;
import com.ngn.api.tags.ApiTagModel;
import com.ngn.api.tags.ApiTagService;
import com.ngn.api.utils.ApiKeyValueModel;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.ConfirmDialogTemplate;
import com.ngn.utils.components.DialogTemplate;
import com.ngn.utils.components.NotificationTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Style.AlignItems;
import com.vaadin.flow.dom.Style.FlexWrap;

public class TagForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private TextField txtSearch = new TextField();
	private Checkbox cbActive = new Checkbox("Hoạt động");
	private ButtonTemplate btnSearch = new ButtonTemplate("Tìm",FontAwesome.Solid.SEARCH.create());
	private ButtonTemplate btnAddNew = new ButtonTemplate("Thêm thẻ mới",FontAwesome.Solid.PLUS.create());
	private ComboBox<Pair<String, String>> cmbType = new ComboBox<Pair<String,String>>();
	private List<Pair<String, String>> listType = new ArrayList<Pair<String,String>>();


	private Grid<ApiTagModel> grid = new Grid<ApiTagModel>(ApiTagModel.class,false);
	private List<ApiTagModel> listModel = new ArrayList<ApiTagModel>();
	private BelongOrganizationModel belongOrganizationModel = SessionUtil.getOrg();
	private UserAuthenticationModel userAuthenticationModel = SessionUtil.getUser();
	
	private List<ButtonTemplate> listButtons = new ArrayList<ButtonTemplate>();

	private boolean isChooseTag;
	private ApiInputAddclassModel apiInputAddclassModel;
	public TagForm(boolean isChooseTag,ApiInputAddclassModel apiInputAddclassModel) {
		this.isChooseTag = isChooseTag;
		this.apiInputAddclassModel = apiInputAddclassModel;
		loadCmbType();
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(createLayoutFilter(),createGrid());
	}

	@Override
	public void configComponent() {
		btnSearch.addClickListener(e->loadData());
		txtSearch.addValueChangeListener(e->loadData());
		cbActive.addClickListener(e->loadData());
		cmbType.addValueChangeListener(e->loadData());
		
		btnAddNew.addClickListener(e->openDialogAddNewTag());
	}

	private void loadData() {
		listModel = new ArrayList<ApiTagModel>();
		ApiResultResponse<List<ApiTagModel>> data = ApiTagService.getListTags(getSearch());

		if(data.isSuccess()) {
			listModel.addAll(data.getResult());
		}
		grid.setItems(listModel);
	}

	private Component createGrid() {
		grid = new Grid<ApiTagModel>(ApiTagModel.class,false);

		grid.addColumn(ApiTagModel::getName).setHeader("Tên");

		grid.addComponentColumn(model->{
			ButtonTemplate btn = new ButtonTemplate(FontAwesome.Solid.TAG.create());

			btn.getStyle().setColor(model.getColor());
			btn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btn.getStyle().setFontSize("25px");

			return btn;
		}).setHeader("Màu");

		grid.addColumn(model->{
			if(model.getType() == null) {
				return "Đang cập nhật";
			}else {
				return listType.stream().filter(check->{
					if(check.getKey() != null) {
						if(check.getKey().equals(model.getType())) {
							return true;
						}
					}
					return false;
				}).findFirst().get().getValue();
			}
		}).setHeader("Loại");

		grid.addColumn(model->{
			if(model.isActive()) {
				return "Hoạt động";
			}else {
				return "Không hoạt động";
			}
		}).setHeader("Trạng thái");

		grid.addComponentColumn(model->{
			HorizontalLayout hLayoutButtons = new HorizontalLayout();

			ButtonTemplate btnEdit = new ButtonTemplate("Sửa",FontAwesome.Solid.EDIT.create());
			btnEdit.addClickListener(e->openDialogUpdateTag(model.getId()));

			ButtonTemplate btnDeleted = new ButtonTemplate("Xóa",FontAwesome.Solid.TRASH.create());
			btnDeleted.addThemeVariants(ButtonVariant.LUMO_ERROR);
			btnDeleted.addClickListener(e->openConfirmDelete(model.getId()));

			hLayoutButtons.add(btnEdit,btnDeleted);
			
			if(isChooseTag) {
				btnDeleted.setVisible(false);
			}
			
			return hLayoutButtons;
		}).setHeader("Thao tác").setWidth("170px").setFlexGrow(0);
		
		if(isChooseTag) {
			grid.addComponentColumn(model->{
				HorizontalLayout hLayout = new HorizontalLayout();
				
				ButtonTemplate btnRemove = new ButtonTemplate("Bỏ chọn");
				btnRemove.addThemeVariants(ButtonVariant.LUMO_ERROR);
				btnRemove.setVisible(false);
				btnRemove.addClickListener(e->removeAddclass(model));
				
				ButtonTemplate btnChoose = new ButtonTemplate("Chọn");
				btnChoose.setId(model.getId());
				btnChoose.addClickListener(e->{
					ApiTagModel tag = getTag(apiInputAddclassModel.getTagId());
					if(tag != null) {
						removeAddclass(tag);
					}
					addClass(model.getId());
				});
				btnChoose.setVisible(true);
				hLayout.add(btnChoose);
				
				for(String id : model.getClassIds()) {
					for(String j : apiInputAddclassModel.getClassIds()) {
						if(id.equals(j)) {
							btnChoose.setVisible(false);
							btnRemove.setVisible(true);
							hLayout.replace(btnChoose, btnRemove);
						}
					}
				}
				
				listButtons.add(btnChoose);
				
				return hLayout;
			}).setWidth("90px").setFlexGrow(0);
		}

		return grid;
	}

	private Component createLayoutFilter() {
		HorizontalLayout hLayoutFilter = new HorizontalLayout();

		txtSearch.setPlaceholder("Nhập từ khóa để tìm...");
		txtSearch.setClearButtonVisible(true);

		cbActive.setValue(true);

		hLayoutFilter.setWidthFull();
		hLayoutFilter.add(txtSearch,cbActive,cmbType,btnSearch,btnAddNew);
		hLayoutFilter.expand(txtSearch);
		hLayoutFilter.getStyle().setFlexWrap(FlexWrap.WRAP);

		hLayoutFilter.getStyle().setAlignItems(AlignItems.CENTER);

		return hLayoutFilter;
	}

	private ApiTagFilterModel getSearch() {
		ApiTagFilterModel apiTagFilterModel = new ApiTagFilterModel();

		apiTagFilterModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
		apiTagFilterModel.setUserId(userAuthenticationModel.getId());
		apiTagFilterModel.setSkip(0);
		apiTagFilterModel.setLimit(0);
		apiTagFilterModel.setType(cmbType.getValue().getKey());

		if(!txtSearch.getValue().isEmpty()) {
			apiTagFilterModel.setKeyword(txtSearch.getValue());
		}

		apiTagFilterModel.setActive(cbActive.getValue());

		return apiTagFilterModel;
	}

	private void loadCmbType() {
		listType = new ArrayList<Pair<String,String>>();
		ApiResultResponse<List<ApiKeyValueModel>> dataType = ApiTagService.getTypeFilter();

		listType.add(Pair.of(null,"Tất cả"));
		if(dataType.isSuccess()) {
			dataType.getResult().forEach(model->{
				listType.add(Pair.of(model.getKey(),model.getName()));
			});
		}

		cmbType.setItems(listType);
		cmbType.setValue(listType.get(0));
		cmbType.setItemLabelGenerator(Pair::getValue);
		if(isChooseTag) {
			cmbType.setValue(listType.stream().filter(tag-> tag.getKey() != null && tag.getKey().equals(apiInputAddclassModel.getType())).findFirst().get());
		}

	}

	private void openConfirmDelete(String idTag) {
		ConfirmDialogTemplate confirmDialogTemplate = new ConfirmDialogTemplate("Xóa thông tin");
		confirmDialogTemplate.setText("Xác nhận xóa thông tin phân loại này");

		confirmDialogTemplate.addConfirmListener(e->doDeleted(idTag));

		confirmDialogTemplate.open();

	}

	private void doDeleted(String idTag) {
		ApiResultResponse<Object> delete = ApiTagService.deleteTag(idTag);
		if(delete.isSuccess()) {
			loadData();
			NotificationTemplate.success("Đã xóa thành công");
		}
	}

	private void openDialogAddNewTag() {
		DialogTemplate dialogTemplate = new DialogTemplate("Thêm thẻ mới");
		
		String type = null;
		
		if(this.apiInputAddclassModel != null) {
			type = apiInputAddclassModel.getType();
		}
		
		EditTagForm EditTagForm = new EditTagForm(null,type);

		EditTagForm.addChangeListener(e->{

			NotificationTemplate.success("Thành công");
			loadData();

			dialogTemplate.close();
		});

		dialogTemplate.getBtnSave().addClickListener(e->{
			EditTagForm.save();
		});

		dialogTemplate.add(EditTagForm);
		dialogTemplate.setWidth("40%");

		dialogTemplate.open();
	}

	private void openDialogUpdateTag(String idTag) {
		DialogTemplate dialogTemplate = new DialogTemplate("Chỉnh sửa");

		EditTagForm EditTagForm = new EditTagForm(idTag,null);

		EditTagForm.addChangeListener(e->{

			NotificationTemplate.success("Thành công");
			loadData();

			dialogTemplate.close();
		});

		dialogTemplate.getBtnSave().addClickListener(e->{
			EditTagForm.save();
		});

		dialogTemplate.add(EditTagForm);
		dialogTemplate.setWidth("40%");

		dialogTemplate.open();
	}
	
	private void addClass(String idTag) {
		ApiInputAddclassModel input = new ApiInputAddclassModel();
		
		input.setType(apiInputAddclassModel.getType());
		input.setTagId(idTag);
		List<String> listAddclass = new ArrayList<String>();
		listAddclass.addAll(this.apiInputAddclassModel.getClassIds());
		input.setClassIds(listAddclass);
		
		ApiTagCreatetorModel apiTagCreatetorModel = new ApiTagCreatetorModel();
		apiTagCreatetorModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
		apiTagCreatetorModel.setOrganizationName(belongOrganizationModel.getOrganizationName());
		apiTagCreatetorModel.setOrganizationUserId(userAuthenticationModel.getId());
		apiTagCreatetorModel.setOrganizationUserName(userAuthenticationModel.getFullName());
		
		input.setCreator(apiTagCreatetorModel);
		
		ApiResultResponse<ApiTagModel> data = ApiTagService.addClass(input);
		if(data.isSuccess()) {
			apiInputAddclassModel.setTagId(data.getResult().getId());
			loadData();
			fireEvent(new ClickEvent(this, false));
		}
	}
	
	private ApiTagModel getTag(String idTag) {
		ApiResultResponse<ApiTagModel> data = ApiTagService.getTag(idTag);
		if(data.isSuccess()) {
			return data.getResult();
		}
		return null;
	}
	
	private void removeAddclass(ApiTagModel apiTagModel) {
		ApiInputAddclassModel input = new ApiInputAddclassModel(apiTagModel);
		input.setType(apiInputAddclassModel.getType());
		input.getClassIds().removeIf(id->{
			for(String i : apiInputAddclassModel.getClassIds()) {
				if(id.equals(i)) {
					return false;
				}
			}
			return true;
		});
		
		ApiResultResponse<ApiTagModel> data = ApiTagService.removeClass(input);
		if(data.isSuccess()) {
			loadData();
			fireEvent(new ClickEvent(this, false));
		}
		
	}

}
