package com.ngn.tdnv.task.forms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.flowingcode.vaadin.addons.fontawesome.FontAwesome.Solid.Icon;
import com.ngn.api.doc.ApiDocService;
import com.ngn.api.organization.ApiOrganizationModel;
import com.ngn.api.organization.ApiOrganizationService;
import com.ngn.api.organization.ApiUserGroupExpandModel;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tags.ApiTagFilterModel;
import com.ngn.api.tags.ApiTagModel;
import com.ngn.api.tags.ApiTagService;
import com.ngn.api.tasks.ApiFilterTaskModel;
import com.ngn.api.tasks.ApiTaskService;
import com.ngn.api.utils.ApiKeyValueModel;
import com.ngn.enums.DataOfEnum;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.tdnv.doc.enumdoc.DocCategoryEnum;
import com.ngn.utils.LocalDateUtil;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.DetailsTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePickerVariant;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexDirection;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.dom.Style.Display;

public class TaskFilterAssigneeForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private boolean isUser = SessionUtil.checkDataOf() == null ? true : SessionUtil.checkDataOf().getKey().equals(DataOfEnum.TOCANHAN.getKey());
	private VerticalLayoutTemplate vLayoutFilter = new VerticalLayoutTemplate();
	private DetailsTemplate detailAdvanced = new DetailsTemplate("Tìm kiếm nâng cao");
	private DateTimePicker dateStartDay = new DateTimePicker("Từ ngày");
	private Icon iconDate = FontAwesome.Solid.ARROW_RIGHT.create();
	private DateTimePicker dateEndDay = new DateTimePicker("Đến ngày");
	private TextField txtSeach = new TextField("Tìm kiếm");
	private ButtonTemplate btnSearch = new ButtonTemplate("Tìm kiếm",FontAwesome.Solid.SEARCH.create());
	private ComboBox<Pair<String, String>> cmbParentOrg = new ComboBox<Pair<String,String>>("Đơn vị giao");
	private ComboBox<Pair<String, String>> cmbUserOfOwner = new ComboBox<Pair<String,String>>("Cán bộ đơn vị giao");
	private ComboBox<Pair<String, String>> cmbSupportOrg = new ComboBox<Pair<String,String>>("Đơn vị phối hợp");
	private ComboBox<Pair<String, String>> cmbUserOfSupport = new ComboBox<Pair<String,String>>("Cán bộ đơn vị phối hợp");
	private ComboBox<Pair<String, String>> cmbAssigneeUser = new ComboBox<Pair<String,String>>("Cán bộ xử lý");
	private ComboBox<Pair<String, String>> cmbPriority = new ComboBox<Pair<String,String>>("Độ khẩn");
	private ComboBox<Pair<String, String>> cmbStatus = new ComboBox<Pair<String,String>>("Trạng thái");
	private ComboBox<Pair<String, String>> cmbTaskIsAssign = new ComboBox<Pair<String,String>>("Nhiệm vụ");
	private ComboBox<Pair<String, String>> cmbDocCategory = new ComboBox<Pair<String,String>>("Loại văn bản");
	private ComboBox<ApiTagModel> cmbTags = new ComboBox<ApiTagModel>("Tìm theo thẻ");
	
	private List<Pair<String,String>> listStatus = new ArrayList<Pair<String,String>>();
	private List<Pair<String, String>> listPriority = new ArrayList<Pair<String,String>>();
	
	private HorizontalLayout hLayoutSearch = new HorizontalLayout();
	
	private boolean isMobileLayout = false;
	private Boolean isKpi = null;
	
	private UserAuthenticationModel userAuthenticationModel = SessionUtil.getUser();

			
	private BelongOrganizationModel belongOrganizationModel;
	private Map<String, List<String>> parameters;
	public TaskFilterAssigneeForm(BelongOrganizationModel belongOrganizationModel,Map<String, List<String>> parameters) {
		this.belongOrganizationModel = belongOrganizationModel;
		this.parameters = parameters;
		checkMobileLayout();
		buildLayout();
		configComponent();
		loadData();
		checkParameter();
	}

	@Override
	public void buildLayout() {
		this.setWidthFull();
		this.add(vLayoutFilter);
		loadLayoutFilter();
//		this.add(createLayout());

	}

	@Override
	public void configComponent() {

		btnSearch.addClickListener(e->{
			fireEvent(new ClickEvent(this,false));
		});

		cmbPriority.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});

		txtSeach.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});

		dateStartDay.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});

		dateEndDay.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});

		cmbStatus.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});
		
		cmbParentOrg.addValueChangeListener(e->{
			loadCmbUserOfOwner();
		});
		
		cmbUserOfOwner.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});

		cmbSupportOrg.addValueChangeListener(e->{
			loadCmbUserOfSupport();
		});
		
		cmbUserOfSupport.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});
		
		cmbDocCategory.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});
		
		cmbTags.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});
		

	}

	public void loadData() {
		loadDateTime();
		loadCmbPriority();
		loadCmbStatus();
		loadCmbAssigneeUser();
		loadCmbAssign();
		loadCmbParentOrg();
		loadCmbUserOfOwner();
		loadCmbOrgSupport();
		loadCmbUserOfSupport();
		loadDocCategory();
		loadCmbTag();
	}
	
	private void checkParameter() {
		if(parameters != null) {
			if(parameters.containsKey("keysearch")) {
				txtSeach.setValue(parameters.get("keysearch").get(0));
			}
		}
		
		cmbStatus.getListDataView().getItems().filter(st->{
			String key = st.getKey() == null ? "nah" : st.getKey();
			return key.equals(parameters.get("status") == null ? "oh no" : parameters.get("status").get(0));
		}).findFirst().ifPresent(e->{
			cmbStatus.setValue(e);
		});;
	}
	
	private void checkMobileLayout() {
		try {
			UI.getCurrent().getPage().retrieveExtendedClientDetails(e->{
				if(e.getScreenWidth() < 768) {
					dateStartDay.setWidth("49%");
					dateEndDay.setWidth("49%");
					cmbAssigneeUser.setWidth("49%");
					cmbParentOrg.setWidth("49%");
					cmbUserOfOwner.setWidth("49%");
					cmbPriority.setWidth("49%");
					cmbStatus.setWidth("49%");
					cmbSupportOrg.setWidth("49%");
					cmbUserOfSupport.setWidth("49%");
					cmbTaskIsAssign.setWidth("49%");
					cmbTags.setWidth("49%");
					txtSeach.setWidth("70%");
					hLayoutSearch.setWidthFull();
					iconDate.setVisible(false);
					isMobileLayout = true;
				}
			});
		} catch (Exception e) {
		}
	}

	private void loadLayoutFilter() {
		vLayoutFilter.removeAll();
		
		dateStartDay.addThemeVariants(DateTimePickerVariant.LUMO_SMALL);
		dateStartDay.setWidth("180px");
		dateStartDay.setDatePickerI18n(LocalDateUtil.i18nVietNam());

		dateEndDay.addThemeVariants(DateTimePickerVariant.LUMO_SMALL);
		dateEndDay.setWidth("180px");
		dateEndDay.setDatePickerI18n(LocalDateUtil.i18nVietNam());

		txtSeach.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		txtSeach.setWidth("200px");
		txtSeach.setClearButtonVisible(true);
		txtSeach.setPlaceholder("Nhập từ khóa để tìm...");

		btnSearch.addThemeVariants(ButtonVariant.LUMO_SMALL);
		btnSearch.getStyle().setMarginTop("27px");	


		cmbPriority.addThemeVariants(ComboBoxVariant.LUMO_SMALL);

		cmbStatus.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbStatus.setWidth("150px");
		cmbStatus.getStyle().set("--vaadin-combo-box-overlay-width", "350px");

		cmbAssigneeUser.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbAssigneeUser.setWidth("150px");
		cmbAssigneeUser.getStyle().set("--vaadin-combo-box-overlay-width", "350px");
		
		cmbTaskIsAssign.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		
		cmbParentOrg.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbParentOrg.setWidth("200px");
		cmbParentOrg.getStyle().set("--vaadin-combo-box-overlay-width", "350px");
		
		cmbUserOfOwner.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbUserOfOwner.setWidth("150px");
		cmbUserOfOwner.getStyle().set("--vaadin-combo-box-overlay-width", "350px");
		
		cmbSupportOrg.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbSupportOrg.setWidth("200px");
		cmbSupportOrg.getStyle().set("--vaadin-combo-box-overlay-width", "350px");
		
		cmbUserOfSupport.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbUserOfSupport.setWidth("150px");
		cmbUserOfSupport.getStyle().set("--vaadin-combo-box-overlay-width", "350px");
		
		cmbDocCategory.addThemeVariants(ComboBoxVariant.LUMO_SMALL);

		cmbTags.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		
		iconDate.setSize("10px");
		iconDate.getStyle().setMarginTop("36px");
		
		HorizontalLayout hLayoutFilter = new HorizontalLayout();
		hLayoutFilter.setWidthFull();
		hLayoutFilter.add(dateStartDay,iconDate,dateEndDay,cmbStatus,txtSeach,cmbTags,btnSearch);
		
		if(isMobileLayout)
			hLayoutFilter.getStyle().setDisplay(Display.FLEX).setFlexWrap(com.vaadin.flow.dom.Style.FlexWrap.WRAP);
		
		detailAdvanced.setOpened(false);
		detailAdvanced.setWidthFull();
		
		FlexLayout flexLayout = new FlexLayout();
		flexLayout.add(cmbParentOrg,cmbUserOfOwner,cmbSupportOrg,cmbUserOfSupport,cmbAssigneeUser,cmbPriority,cmbTaskIsAssign,
				cmbDocCategory);
		flexLayout.setFlexWrap(FlexWrap.WRAP);
		flexLayout.setFlexDirection(FlexDirection.ROW);
		flexLayout.getStyle().set("gap", "5px");
		
		detailAdvanced.add(flexLayout);
		
		
		vLayoutFilter.setWidthFull();
		vLayoutFilter.add(hLayoutFilter,detailAdvanced);
	}
	
	private void loadDocCategory() {
		List<Pair<String, String>> listDocCate = new ArrayList<Pair<String,String>>();
		listDocCate.add(Pair.of(null,"Tất cả"));
		listDocCate.add(Pair.of(DocCategoryEnum.CVDEN.getKey(),DocCategoryEnum.CVDEN.getTitle()));
		listDocCate.add(Pair.of(DocCategoryEnum.CVDI.getKey(),DocCategoryEnum.CVDI.getTitle()));
		
		cmbDocCategory.setItems(listDocCate);
		cmbDocCategory.setItemLabelGenerator(Pair::getValue);
		cmbDocCategory.setValue(listDocCate.get(0));
	}
	
	private void loadCmbParentOrg() {
		List<Pair<String, String>> listParrent = new ArrayList<Pair<String,String>>();
		
		listParrent.add(Pair.of(null,"Tất cả"));
		listParrent.add(Pair.of(belongOrganizationModel.getOrganizationId(),belongOrganizationModel.getOrganizationName()+" (đơn vị đang sử dụng)"));
		
		ApiResultResponse<ApiOrganizationModel> data = ApiOrganizationService.getOneOrg(belongOrganizationModel.getOrganizationId());
		List<String> idParents = new ArrayList<String>();
		if(data.isSuccess()) {
			idParents.add(data.getResult().getParentId());
			for(String idSeccondParent : data.getResult().getParentIdSeconds()) {
				idParents.add(idSeccondParent);
			}
		}
		
		if(!(idParents.get(0) == null)) {
			for(String idOrg : idParents) {
				ApiOrganizationModel apiOrganizationModel = getDetailOrg(idOrg);
				listParrent.add(Pair.of(apiOrganizationModel.getId(),apiOrganizationModel.getName()));
			}
		}
		
		cmbParentOrg.setItems(listParrent);
		cmbParentOrg.setItemLabelGenerator(Pair::getValue);
		cmbParentOrg.setValue(listParrent.get(0));
	}
	
	private void loadCmbUserOfOwner() {
		List<Pair<String, String>> listUser = new ArrayList<Pair<String,String>>();
		listUser.add(Pair.of(null,"Tất cả"));
		if(cmbParentOrg.getValue() != null && cmbParentOrg.getValue().getKey() != null) {
			getListUserOfOrg(cmbParentOrg.getValue().getKey()).forEach(model->{
				listUser.add(Pair.of(model.getUserId(),model.getFullName()));
			});
		}
		
		cmbUserOfOwner.setItems(listUser);
		cmbUserOfOwner.setItemLabelGenerator(Pair::getValue);
		cmbUserOfOwner.setValue(listUser.get(0));
	}
	
	private ApiOrganizationModel getDetailOrg(String idOrg) {
		ApiResultResponse<ApiOrganizationModel> data = ApiOrganizationService.getOneOrg(idOrg);
		if(data.isSuccess()) {
			return data.getResult();
		}
		return null;
	}
	
	private void loadCmbOrgSupport() {
		List<Pair<String, String>> dataSupport = new ArrayList<Pair<String,String>>();
		dataSupport.add(Pair.of(null,"Tất cả"));
		
		dataSupport.add(Pair.of(belongOrganizationModel.getOrganizationId(),belongOrganizationModel.getOrganizationName() + " (đơn vị đang sử dụng) "));
		
		ApiResultResponse<ApiOrganizationModel> data = ApiOrganizationService.getOneOrg(belongOrganizationModel.getOrganizationId());
		if(data.getResult().getParentId() != null) {
			ApiResultResponse<List<ApiOrganizationModel>> listOrg = ApiOrganizationService.getListOrganization(data.getResult().getParentId());
			listOrg.getResult().stream().forEach(model->{
				dataSupport.add(Pair.of(model.getId(),model.getName()));
			});
		}
		
//		for(Pair<String, String> i : dataSupport) {
//			if( i.getKey() != null && i.getKey().equals(belongOrganizationModel.getOrganizationId())) {
//				dataSupport.remove(i);
//				break;
//			}
//		}
		cmbSupportOrg.setItems(dataSupport);
		cmbSupportOrg.setItemLabelGenerator(Pair::getRight);
		cmbSupportOrg.setValue(dataSupport.get(0));
	}
	
	private void loadCmbUserOfSupport() {
		List<Pair<String, String>> listUser = new ArrayList<Pair<String,String>>();
		listUser.add(Pair.of(null,"Tất cả"));
		
		if(cmbSupportOrg.getValue() != null && cmbSupportOrg.getValue().getKey() != null) {
			getListUserOfOrg(cmbSupportOrg.getValue().getKey()).forEach(model->{
				listUser.add(Pair.of(model.getUserId(),model.getFullName()));
			});
		}
		
		cmbUserOfSupport.setItems(listUser);
		cmbUserOfSupport.setItemLabelGenerator(Pair::getValue);
		cmbUserOfSupport.setValue(listUser.get(0));
	}


	private void loadCmbPriority() {
		listPriority.clear();
		listPriority.add(Pair.of(null,"Tất cả"));

		ApiResultResponse<List<ApiKeyValueModel>> data = ApiDocService.getPriority();
		if(data.isSuccess()) {
			data.getResult().stream().forEach(model->{
				listPriority.add(Pair.of(model.getKey(),model.getName()));
			});
		}

		cmbPriority.setItems(listPriority);
		cmbPriority.setItemLabelGenerator(Pair::getRight);
		cmbPriority.setValue(listPriority.get(0));
	}

	private void loadCmbStatus() {
		listStatus.clear();
		listStatus.add(Pair.of(null,"Tất cả"));
		ApiResultResponse<List<ApiKeyValueModel>> getStatus = ApiTaskService.getStatus();
		if(getStatus.isSuccess()) {
			getStatus.getResult().stream().forEach(model->{
				listStatus.add(Pair.of(model.getKey(),model.getName()));
			});
		}


		cmbStatus.setItems(listStatus);
		cmbStatus.setItemLabelGenerator(Pair::getRight);
		cmbStatus.setValue(listStatus.get(0));
	}
	
	private void loadCmbAssign() {
		List<Pair<String, String>> listAssign = new ArrayList<>();
		listAssign.add(Pair.of(null,"Tất cả"));
		listAssign.add(Pair.of("","Đã phân"));
		listAssign.add(Pair.of("","Chưa phân"));
		
		cmbTaskIsAssign.setItems(listAssign);
		cmbTaskIsAssign.setItemLabelGenerator(Pair::getValue);
		cmbTaskIsAssign.setValue(listAssign.get(0));
		
	}

	
	private void loadCmbAssigneeUser() {
		List<Pair<String, String>> listPairAssigneeUser = new ArrayList<Pair<String,String>>();
		listPairAssigneeUser.add(Pair.of(null,"Tất cả"));
		try {
			ApiResultResponse<List<ApiUserGroupExpandModel>> listAssigneeUser = ApiOrganizationService.getListUserOrganizationEx(belongOrganizationModel.getOrganizationId());
			if(listAssigneeUser.isSuccess()) {
				listAssigneeUser.getResult().stream().forEach(model->{
					listPairAssigneeUser.add(Pair.of(model.getUserId(),model.getMoreInfo().getFullName()));
				});
			}
			
		} catch (Exception e) {

		}
		
		cmbAssigneeUser.setItems(listPairAssigneeUser);
		cmbAssigneeUser.setItemLabelGenerator(Pair::getValue);
		cmbAssigneeUser.setValue(listPairAssigneeUser.get(0));
	}


	private void loadDateTime() {
		dateStartDay.setLocale(LocalDateUtil.localeVietNam());
		dateStartDay.setValue(LocalDateUtil.longToLocalDateTime(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getStartOfTheYear(SessionUtil.getYear()))));


		dateEndDay.setValue(LocalDateUtil.longToLocalDateTime(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getEndtOfTheYear(SessionUtil.getYear()))));
		dateEndDay.setLocale(LocalDateUtil.localeVietNam());
	}

	// Get list Org
	@SuppressWarnings("unused")
	private List<ApiOrganizationModel> getSubOrgs(String idOrg){
		ApiResultResponse<List<ApiOrganizationModel>> getOrgs = ApiOrganizationService.getListOrganization(idOrg);
		if(getOrgs.isSuccess()) {
			return getOrgs.getResult();
		}
		return Collections.emptyList();
	}
	
	// Get info of One Org
	@SuppressWarnings("unused")
	private ApiOrganizationModel getInfoOrg(String idOrg) {
		ApiResultResponse<ApiOrganizationModel> getOrg = ApiOrganizationService.getOneOrg(idOrg);
		if(getOrg.isSuccess()) {
			return getOrg.getResult();
		}
		
		return new ApiOrganizationModel();
	}
	
	private List<ApiUserGroupExpandModel> getListUserOfOrg(String idOrg) {
		ApiResultResponse<List<ApiUserGroupExpandModel>> data = ApiOrganizationService.getListUserOrganizationEx(idOrg);
		if(data.isSuccess()) {
			return data.getResult();
		}
		
		return Collections.emptyList();
	}
	
	//Tag
	private void loadCmbTag() {
		
		List<ApiTagModel> listTags = new ArrayList<ApiTagModel>();
		
		ApiTagFilterModel apiTagFilterModel = new ApiTagFilterModel();
		apiTagFilterModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
		apiTagFilterModel.setUserId(userAuthenticationModel.getId());
		apiTagFilterModel.setSkip(0);
		apiTagFilterModel.setLimit(0);
		apiTagFilterModel.setType("Task");
		apiTagFilterModel.setActive(true);
		
		ApiTagModel apiTagModel = new ApiTagModel();
		apiTagModel.setId(null);
		apiTagModel.setName("Tất cả");
		apiTagModel.setColor("white");
		
		listTags.add(apiTagModel);
		
		ApiResultResponse<List<ApiTagModel>> data = ApiTagService.getListTags(apiTagFilterModel);
		if(data.isSuccess()) {
			listTags.addAll(data.getResult());
		}
		
		cmbTags.setItems(listTags);
		cmbTags.setItemLabelGenerator(tag->tag.getName());
		cmbTags.setRenderer(createTagRenderer());
		cmbTags.setValue(listTags.get(0));
		cmbTags.getStyle().set("--vaadin-combo-box-overlay-width", "350px");
		cmbTags.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
	}
	
	private Renderer<ApiTagModel> createTagRenderer() {
	    StringBuilder tpl = new StringBuilder();
	    tpl.append("<div style=\"display: flex; align-items: center;\">");
	    tpl.append("  <div style=\"width: 12px; height: 12px; background-color: ${item.color}; border-radius: 50%; margin-right: 8px;\"></div>");
	    tpl.append("  <span>${item.name}</span>");
	    tpl.append("</div>");

	    return LitRenderer.<ApiTagModel>of(tpl.toString())
	            .withProperty("color", ApiTagModel::getColor)
	            .withProperty("name", ApiTagModel::getName);
	}
	
	public ApiFilterTaskModel getSearchData() {
		ApiFilterTaskModel apiFilterTaskModel = new ApiFilterTaskModel();

		apiFilterTaskModel.setFromDate(LocalDateUtil.localDateTimeToLong(dateStartDay.getValue()));
		apiFilterTaskModel.setToDate(LocalDateUtil.localDateTimeToLong(dateEndDay.getValue()));
		apiFilterTaskModel.setStatus(cmbStatus.getValue().getKey());
		apiFilterTaskModel.setPriority(cmbPriority.getValue().getKey());
		apiFilterTaskModel.setKeyword(txtSeach.getValue());
		
		// Assignee
		apiFilterTaskModel.setAssigneeOrganizationId(belongOrganizationModel.getOrganizationId());
		apiFilterTaskModel.setAssigneeOrganizationUserId(cmbAssigneeUser.getValue().getKey());
		
		// Owner
		apiFilterTaskModel.setOwnerOrganizationId(cmbParentOrg.getValue().getKey());
		if(cmbUserOfOwner.getValue() != null && cmbUserOfOwner.getValue().getKey() != null) {
			apiFilterTaskModel.setOwnerOrganizationUserId(cmbUserOfOwner.getValue().getKey());
		}
		
		// Support
		apiFilterTaskModel.setSupportOrganizationId(cmbSupportOrg.getValue().getKey());
		if(cmbUserOfSupport.getValue() != null && cmbUserOfSupport.getValue().getKey() != null) {
			apiFilterTaskModel.setSupportOrganizationUserId(cmbUserOfSupport.getValue().getKey());
		}
		
		apiFilterTaskModel.setDocCategory(cmbDocCategory.getValue().getKey());
		apiFilterTaskModel.setTagIds(cmbTags.getValue().getId());
		
		if(isUser){
			apiFilterTaskModel.setAssigneeOrganizationId(null);
			apiFilterTaskModel.setAssigneeOrganizationUserId(userAuthenticationModel.getId());
		}
		
		apiFilterTaskModel.setKpi(isKpi);
		
		return apiFilterTaskModel;
	}

	public ComboBox<Pair<String, String>> getCmbStatus() {
		return cmbStatus;
	}
	
	public void setCmbPriority(String keyPriority) {
		if(keyPriority == null) {
			cmbPriority.setValue(listPriority.get(0));
		}else {
			listPriority.forEach(md->{
				if(md.getKey() != null && md.getKey().equals(keyPriority)) {
					cmbPriority.setValue(md);
				}
			});
		}
	}
	
	public void setKpiFilter(Boolean kpi) {
		this.isKpi = kpi;
	}
	
	public void setCmbStatus(String key) {
		listStatus.forEach(md->{
			if(md.getKey() != null && key.equals(md.getKey())) {
				cmbStatus.setValue(md);
			}
		});
	}
}
