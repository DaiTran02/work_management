package com.ngn.tdnv.doc.forms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.doc.ApiDataSummaryModel;
import com.ngn.api.doc.ApiDocService;
import com.ngn.api.doc.ApiFilterListDocModel;
import com.ngn.api.organization.ApiOrganizationModel;
import com.ngn.api.organization.ApiOrganizationService;
import com.ngn.api.organization.ApiUserGroupExpandModel;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tags.ApiTagFilterModel;
import com.ngn.api.tags.ApiTagModel;
import com.ngn.api.tags.ApiTagService;
import com.ngn.api.utils.ApiKeyValueModel;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.utils.LocalDateUtil;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.DetailsTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePickerVariant;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexDirection;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.dom.Style.Display;

public class DocFilterForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private Logger log = LogManager.getLogger(DocFilterForm.class);

	private boolean isMobileLayout = false;
	
	private VerticalLayout vLayout = new VerticalLayout();
	private DateTimePicker startDate = new DateTimePicker("Từ ngày");
	private DateTimePicker endDate = new DateTimePicker("Đến ngày");
	private TextField txtSymbol = new TextField("Ký hiệu");
	private TextField txtNumber = new TextField("Số hiệu");
	private ComboBox<Pair<String, String>> cmbCategory = new ComboBox<Pair<String,String>>("Công văn");
	private ComboBox<Pair<String, String>> cmbStatus = new ComboBox<Pair<String,String>>("Trạng thái");
	private ComboBox<Pair<String, String>> cmbOwnerDoc = new ComboBox<Pair<String,String>>("Văn bản của đơn vị");
	private ComboBox<Pair<String, String>> cmbUserOfOrg = new ComboBox<Pair<String,String>>("Người soạn thảo");
	
	private DetailsTemplate detailsTemplate = new DetailsTemplate("Tìm kiếm nâng cao");

	private TextField txtSearch = new TextField("Tìm kiếm");
	private ButtonTemplate btnSearch = new ButtonTemplate("Tìm",FontAwesome.Solid.SEARCH.create());
	private Checkbox cbActive = new Checkbox("Hoạt động");
	private ButtonTemplate btnAddDoc = new ButtonTemplate("Thêm văn bản",FontAwesome.Solid.PLUS.create());
	private ApiDataSummaryModel apiDataSummaryModel = new ApiDataSummaryModel();
	
	private ComboBox<ApiTagModel> cmbTags = new ComboBox<ApiTagModel>("Tìm theo thẻ");

	
	private BelongOrganizationModel belongOrganizationModel;
	private UserAuthenticationModel userAuthenticationModel = SessionUtil.getUser();
	private Map<String, List<String>> parametters;
	private String docCategory;
	public DocFilterForm(BelongOrganizationModel belongOrganizationModel,Map<String, List<String>> parametters,String docCategory) {
		this.belongOrganizationModel = belongOrganizationModel;
		this.docCategory = docCategory;
		if(parametters != null) {
			this.parametters = parametters;

		}
		checkMobileLayout();
		buildLayout();
		initDateTime();
		initCategory();
		initStatus();
		initCmbOwnerDoc();
		initUserOfOrg();
		getFilter();
		checkPermissionDoc();
		loadCmbTag();
		loadData();
		configComponent();
		checkParametters();
	}

	@Override
	public void buildLayout() {
		this.setWidthFull();
		this.setPadding(false);
		this.setPadding(false);
		this.add(vLayout);

		//use this class in file layout.css
		//		this.addClassName("layout__genera--style");

	}

	@Override
	public void configComponent() {
		txtSearch.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});

		startDate.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});

		endDate.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});

		cmbCategory.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});

		cmbStatus.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});

		btnSearch.addClickListener(e->{
			fireEvent(new ClickEvent(this,false));
		});
		
		cmbOwnerDoc.addValueChangeListener(e->{
			initUserOfOrg();
			loadData();
			detailsTemplate.setOpened(true);
		});
		
		cmbUserOfOrg.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});
	}
	
	public void loadData() {
		ApiFilterListDocModel apiFilterListDocModel = getFilter();
		apiFilterListDocModel.setActive(true);
		ApiResultResponse<ApiDataSummaryModel> data = ApiDocService.getSummary(apiFilterListDocModel);
		if(data.isSuccess()) {
			apiDataSummaryModel = data.getResult();
			createLayoutFilter();
		}
	}
	
	private void loadCmbTag() {
		
		List<ApiTagModel> listTags = new ArrayList<ApiTagModel>();
		
		ApiTagFilterModel apiTagFilterModel = new ApiTagFilterModel();
		apiTagFilterModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
		apiTagFilterModel.setUserId(userAuthenticationModel.getId());
		apiTagFilterModel.setSkip(0);
		apiTagFilterModel.setLimit(0);
		apiTagFilterModel.setType("Doc");
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


	private void createLayoutFilter() {
		vLayout.removeAll();
		FlexLayout flexLayout = new FlexLayout();
		HorizontalLayout hLayoutRow1 = new HorizontalLayout();
		hLayoutRow1.setWidthFull();
		if(isMobileLayout) {
			hLayoutRow1.getStyle().setDisplay(Display.FLEX).setFlexWrap(com.vaadin.flow.dom.Style.FlexWrap.WRAP);
		}
		if(apiDataSummaryModel != null) {
			ButtonTemplate btnAll = new ButtonTemplate("Tất cả");
			btnAll.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnAll.addClickListener(e->cmbStatus.setValue(Pair.of(null,"Tất cả")));
			
			hLayoutRow1.add(btnAll);
			apiDataSummaryModel.getChild().forEach(model->{
				if(model != null && model.getKey() != null) {
					ButtonTemplate spStatus = new ButtonTemplate(model.getName());
					spStatus.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
					if(model.getKey().equals("chuagiaonhiemvu")) {
						spStatus.addThemeVariants(ButtonVariant.LUMO_ERROR);
						spStatus.setText(model.getName()+" ("+model.getCount()+")");
						if(isMobileLayout)
							spStatus.setText("Chưa giao ("+model.getCount()+")");
						spStatus.setIcon(FontAwesome.Solid.WINDOW_CLOSE.create());
					}else if(model.getKey().equals("vanbandahoanthanh")) {
						spStatus.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
						spStatus.setText(model.getName()+" ("+model.getCount()+")");
						spStatus.setIcon(FontAwesome.Solid.CHECK_CIRCLE.create());
					}else if(model.getKey().equals("dangthuchien")) {
						spStatus.setText(model.getName()+" ("+model.getCount()+")");
						if(isMobileLayout)
							spStatus.setText("Hoàn thành ("+model.getCount()+")");
						spStatus.setIcon(FontAwesome.Solid.FORWARD.create());
					}
					spStatus.addClickListener(e->{
						cmbStatus.setValue(Pair.of(model.getKey(),model.getName()));
					});
					hLayoutRow1.add(spStatus);
				}
			});
		}



		startDate.setWidth("200px");
		startDate.addThemeVariants(DateTimePickerVariant.LUMO_SMALL);
		startDate.setDatePickerI18n(LocalDateUtil.i18nVietNam());


		endDate.setWidth("200px");
		endDate.addThemeVariants(DateTimePickerVariant.LUMO_SMALL);
		endDate.setDatePickerI18n(LocalDateUtil.i18nVietNam());

		cmbCategory.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		
		cmbOwnerDoc.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbOwnerDoc.setWidth("300px");
		cmbOwnerDoc.getStyle().set("--vaadin-combo-box-overlay-width", "400px");
		
		cmbUserOfOrg.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		
		
		cmbStatus.addThemeVariants(ComboBoxVariant.LUMO_SMALL);

		
		txtSymbol.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		txtNumber.addThemeVariants(TextFieldVariant.LUMO_SMALL);

		txtSymbol.setPlaceholder("Nhập ký hiệu");
		txtSymbol.setClearButtonVisible(true);

		txtNumber.setPlaceholder("Nhập số hiệu");
		txtNumber.setClearButtonVisible(true);

		txtSearch.setPlaceholder("Nhập từ khóa để tìm...");
		txtSearch.setClearButtonVisible(true);
		txtSearch.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		txtSearch.setWidth("300px");


		btnSearch.getStyle().set("margin-top", "30px").setCursor("pointer");
		btnSearch.addThemeVariants(ButtonVariant.LUMO_SMALL);

		cbActive.getStyle().setMarginTop("30px");
		cbActive.setValue(true);

		btnAddDoc.addThemeVariants(ButtonVariant.LUMO_SMALL);
		btnAddDoc.getStyle().set("margin-top", "30px").setCursor("pointer");

		HorizontalLayout hLayoutRowFirst = new HorizontalLayout();
		hLayoutRowFirst.add(startDate,endDate,cmbTags,cmbCategory,txtSearch,cbActive,btnSearch,btnAddDoc);
		hLayoutRowFirst.getStyle().setFlexWrap(com.vaadin.flow.dom.Style.FlexWrap.WRAP);

		flexLayout.add(txtSymbol,txtNumber,cmbStatus,cmbOwnerDoc,cmbUserOfOrg);
		flexLayout.setFlexWrap(FlexWrap.WRAP);
		flexLayout.setFlexDirection(FlexDirection.ROW);
		flexLayout.getStyle().set("gap", "5px");
		
		if(isMobileLayout) {
			startDate.setWidth("99%");
			endDate.setWidth("99%");

			txtNumber.setWidth("49%");
			txtSearch.setWidth("49%");
			txtSymbol.setWidth("49%");

			cmbCategory.setWidth("49%");
			cmbStatus.setWidth("49%");
			cmbOwnerDoc.setWidth("49%");
			
			cmbUserOfOrg.setWidth("49%");
			
			hLayoutRowFirst.getStyle().setDisplay(Display.FLEX).setFlexWrap(com.vaadin.flow.dom.Style.FlexWrap.WRAP);
		}
		
		detailsTemplate.removeAll();
		detailsTemplate.add(flexLayout);
		detailsTemplate.setOpened(false);
		
		vLayout.add(hLayoutRow1,hLayoutRowFirst,detailsTemplate);
	}
	
	private void initUserOfOrg() {
		List<Pair<String, String>> listUser = new ArrayList<Pair<String,String>>();
		listUser.add(Pair.of(null,"Tất cả"));
		if(cmbUserOfOrg.getValue() != null) {
			if(cmbUserOfOrg.getValue().getKey() != null && !cmbUserOfOrg.getValue().getKey().equals("incChildOrgs") && 
					!cmbUserOfOrg.getValue().getKey().equals("incChildOrgsAndExcMyOrg")) {
				
				List<ApiUserGroupExpandModel> listData = getListUserOfOrg(cmbOwnerDoc.getValue().getKey());
				if(listData != null) {
					listData.forEach(model->{
						listUser.add(Pair.of(model.getUserId(),model.getFullName()));
					});
				}
			}
		}
		
		cmbUserOfOrg.setItems(listUser);
		cmbUserOfOrg.setItemLabelGenerator(Pair::getValue);
		cmbUserOfOrg.setValue(listUser.get(0));
		
	}

	private void checkPermissionDoc() {
//		if(PropsUtil.isPermission()) {
//			if(checkPermisstionUtil.checkPermissionGroupManager()) {
//				if(checkPermisstionUtil.checkUserIsInGroupOrg()) {
//					initCmbGroup(signInOrgModel.getGroup().getId());
//				}
//			}else {
//				if(checkPermisstionUtil.checkUserIsInGroupOrg()) {
//					initCmbGroup(signInOrgModel.getGroup().getId());
//					initCmbUserOfGroup(userAuthenticationModel.getId());
//				}
//			}

			//			if(checkPermisstionUtil.checkPermissionViewAllDoc()) {
			//				initCmbGroup(null);
			//			}
//		}
	}
	
	private void checkMobileLayout() {
		UI.getCurrent().getPage().retrieveExtendedClientDetails(e->{
			if(e.getScreenWidth() < 768) {
				isMobileLayout = true;
			}
		});
	}

	private void initDateTime() {
		startDate.setLocale(LocalDateUtil.localeVietNam());
		startDate.setValue(LocalDateUtil.longToLocalDateTime(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getStartOfTheYear(SessionUtil.getYear()))));


		endDate.setValue(LocalDateUtil.longToLocalDateTime(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getEndtOfTheYear(SessionUtil.getYear()))));
		endDate.setLocale(LocalDateUtil.localeVietNam());
	}

	@Async
	public void initCategory() {
		List<Pair<String, String>> listCategory = new ArrayList<Pair<String,String>>();
		listCategory.add(Pair.of(null,"Tất cả"));
		try {
			ApiResultResponse<List<ApiKeyValueModel>> data = ApiDocService.getKeyValueCategory();
			for(ApiKeyValueModel apiKeyValueModel :  data.getResult()) {
				listCategory.add(Pair.of(apiKeyValueModel.getKey(),apiKeyValueModel.getName()));
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		cmbCategory.setItems(listCategory);
		cmbCategory.setItemLabelGenerator(Pair::getRight);
		if(docCategory != null) {
			listCategory.forEach(model->{
				if(model.getKey() != null && model.getKey().equals(docCategory)) {
					cmbCategory.setValue(model);
					cmbCategory.setReadOnly(true);
				}
			});
		}else {
			cmbCategory.setValue(listCategory.get(0));
			cmbCategory.setReadOnly(false);
		}
	}

	@Async
	public void initStatus() {
		List<Pair<String, String>> listStatus = new ArrayList<Pair<String,String>>();
		listStatus.add(Pair.of(null,"Tất cả"));
		try {
			ApiResultResponse<List<ApiKeyValueModel>> data = ApiDocService.getKeyValueStatus();
			data.getResult().stream().forEach(model->{
				listStatus.add(Pair.of(model.getKey(),model.getName()));
			});
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		cmbStatus.setItems(listStatus);
		cmbStatus.setItemLabelGenerator(Pair::getRight);
		cmbStatus.setValue(listStatus.get(0));
	}
	
	private void initCmbOwnerDoc() {
		List<Pair<String, String>> listPairOwner = new ArrayList<Pair<String,String>>();
		ApiOrganizationModel currentOrg = getInfoOrg(belongOrganizationModel.getOrganizationId());
		if(currentOrg != null) {
			if(currentOrg.getLevel().getKey().equals("room")) {
				if(currentOrg.getParentId() != null) {
					ApiOrganizationModel parentOrg = getInfoOrg(currentOrg.getParentId());
					listPairOwner.add(Pair.of(parentOrg.getId(),parentOrg.getName()));
					listPairOwner.add(Pair.of("incChildOrgs","Tất cả ("+parentOrg.getName()+" và các đơn vị cấp dưới 1 cấp)"));
					listPairOwner.add(Pair.of("incChildOrgsAndExcMyOrg","Tất cả đơn vị cấp dưới (Không có "+parentOrg.getName()+")"));
					List<ApiOrganizationModel> listSubOrg = getSubOrgs(parentOrg.getId());
					if(listSubOrg != null) {
						listSubOrg.forEach(model->{
							listPairOwner.add(Pair.of(model.getId()," |-- "+model.getName()));
						});
					}
				}else {
					List<ApiOrganizationModel> listSubOrg = getSubOrgs(belongOrganizationModel.getOrganizationId());
					listPairOwner.add(Pair.of(currentOrg.getId(),currentOrg.getName()));
					listPairOwner.add(Pair.of("incChildOrgs","Tất cả (Đơn vị đang sử dụng và các đơn vị cấp dưới)"));
					listPairOwner.add(Pair.of("incChildOrgsAndExcMyOrg","Tất cả đơn vị cấp dưới (Không có đơn vị đang sử dụng)"));
					if(listSubOrg != null) {
						listSubOrg.forEach(model->{
							listPairOwner.add(Pair.of(model.getId()," |-- "+model.getName()));
						});
					}
//					Collections.reverse(listPairOwner);
				}
			}else {
				List<ApiOrganizationModel> listSubOrg = getSubOrgs(belongOrganizationModel.getOrganizationId());
				listPairOwner.add(Pair.of(currentOrg.getId(),currentOrg.getName()));
				listPairOwner.add(Pair.of("incChildOrgs","Tất cả (Đơn vị đang sử dụng và các đơn vị cấp dưới)"));
				listPairOwner.add(Pair.of("incChildOrgsAndExcMyOrg","Tất cả đơn vị cấp dưới (Không có đơn vị đang sử dụng)"));
				if(listSubOrg != null) {
					listSubOrg.forEach(model->{
						listPairOwner.add(Pair.of(model.getId()," |-- "+model.getName()));
					});
				}
//				Collections.reverse(listPairOwner);
			}
		}
	
		cmbOwnerDoc.setItems(listPairOwner);
		cmbOwnerDoc.setItemLabelGenerator(Pair::getRight);
		cmbOwnerDoc.setValue(listPairOwner.get(0));
		listPairOwner.forEach(model->{
			if(model.getKey().equals(belongOrganizationModel.getOrganizationId())) {
				cmbOwnerDoc.setValue(model);
			}
		});
	}
	
	// Get list Org
	private List<ApiOrganizationModel> getSubOrgs(String idOrg){
		ApiResultResponse<List<ApiOrganizationModel>> getOrgs = ApiOrganizationService.getListOrganization(idOrg);
		if(getOrgs.isSuccess()) {
			return getOrgs.getResult();
		}
		return Collections.emptyList();
	}
	
	// Get info of One Org
	private ApiOrganizationModel getInfoOrg(String idOrg) {
		ApiResultResponse<ApiOrganizationModel> getOrg = ApiOrganizationService.getOneOrg(idOrg);
		if(getOrg.isSuccess()) {
			return getOrg.getResult();
		}
		
		return null;
	}
	
	//Get list user of org
	private List<ApiUserGroupExpandModel> getListUserOfOrg(String idOrg){
		ApiResultResponse<List<ApiUserGroupExpandModel>> dataUser = ApiOrganizationService.getListUserOrganizationEx(idOrg);
		if(dataUser.isSuccess()) {
			return dataUser.getResult();
		}
		return null;
	}


	public TextField getTxtSearch() {
		return txtSearch;
	}

	public Button getBtnSearch() {
		return btnSearch;
	}

	public Button getBtnAddDoc() {
		return btnAddDoc;
	}

	public ApiFilterListDocModel getFilter() {
		ApiFilterListDocModel apiFilterListDocModel = new ApiFilterListDocModel();
		apiFilterListDocModel.setFromDate(LocalDateUtil.localDateTimeToLong(startDate.getValue()));
		apiFilterListDocModel.setToDate(LocalDateUtil.localDateTimeToLong(endDate.getValue()));
		apiFilterListDocModel.setCategory(cmbCategory.getValue().getKey());
		apiFilterListDocModel.setKeyword(txtSearch.getValue());
		apiFilterListDocModel.setStatus(cmbStatus.getValue().getKey());
		apiFilterListDocModel.setActive(cbActive.getValue());

		BelongOrganizationModel viewParentOrg = SessionUtil.getParentBelongOrgModel();
		ApiOrganizationModel getCurrentOrg = getInfoOrg(belongOrganizationModel.getOrganizationId());
		if(viewParentOrg != null && getCurrentOrg.getParentId() != null && !getCurrentOrg.getParentId().equals(viewParentOrg.getOrganizationId())) {
			ApiOrganizationModel getParent = getInfoOrg(getCurrentOrg.getParentId());
			BelongOrganizationModel parentOrg = new BelongOrganizationModel();
			parentOrg.setOrganizationId(getParent.getId());
			parentOrg.setOrganizationName(getParent.getName());
			viewParentOrg = parentOrg;
		}else {
			viewParentOrg = null;
		}
		
		
		if(viewParentOrg != null && getCurrentOrg.getLevel().getKey().equals("room")) {
			if(cmbOwnerDoc.getValue() != null) {
				if(cmbOwnerDoc.getValue().getKey().equals("incChildOrgs") || cmbOwnerDoc.getValue().getKey().equals("incChildOrgsAndExcMyOrg")) {
					apiFilterListDocModel.setOrganizationId(viewParentOrg.getOrganizationId());
					apiFilterListDocModel.setDataScopeType(cmbOwnerDoc.getValue().getKey());
				}else{
					apiFilterListDocModel.setOrganizationId(cmbOwnerDoc.getValue().getKey());
				}
			}
		}else {
			if(cmbOwnerDoc.getValue() != null) {
				if(cmbOwnerDoc.getValue().getKey().equals("incChildOrgs") || cmbOwnerDoc.getValue().getKey().equals("incChildOrgsAndExcMyOrg")) {
					apiFilterListDocModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
					apiFilterListDocModel.setDataScopeType(cmbOwnerDoc.getValue().getKey());
				}else{
					apiFilterListDocModel.setOrganizationId(cmbOwnerDoc.getValue().getKey());
				}
			}
		}
		
		if(cmbUserOfOrg.getValue() != null) {
			apiFilterListDocModel.setOrganizationUserId(cmbUserOfOrg.getValue().getKey());
		}
		
		if(!txtNumber.getValue().isBlank()) {
			apiFilterListDocModel.setNumber(txtNumber.getValue());
		}
		if(!txtSymbol.getValue().isBlank()) {
			apiFilterListDocModel.setSymbol(txtSymbol.getValue());
		}
		
		if(cmbTags.getValue() != null) {
			apiFilterListDocModel.setTagIds(cmbTags.getValue().getId());
		}
		
		return apiFilterListDocModel;
	}

	private void checkParametters() {
		if(parametters != null) {

			if(parametters.containsKey("docnumber")) {
				txtNumber.setValue(parametters.get("docnumber").get(0));
			}

			if(parametters.containsKey("docsymbol")) {
				txtSymbol.setValue(parametters.get("docsymbol").get(0));
			}
		}
	}


}

