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
import com.ngn.api.tasks.ApiTaskSourceModel;
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

public class TaskFilterOwnerForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private boolean isUser = SessionUtil.checkDataOf() == null ? true : SessionUtil.checkDataOf().getKey().equals(DataOfEnum.TOCANHAN.getKey());
	private VerticalLayoutTemplate vLayoutFilter = new VerticalLayoutTemplate();
	private DateTimePicker dateStartDay = new DateTimePicker("Từ ngày");
	private Icon iconDate = FontAwesome.Solid.ARROW_RIGHT.create();
	private DateTimePicker dateEndDay = new DateTimePicker("Đến ngày");
	private TextField txtSeach = new TextField("Tìm kiếm");
	private ComboBox<Pair<Boolean, String>> cmbDataType = new ComboBox<Pair<Boolean,String>>("Phân loại nhiệm vụ");
	private ButtonTemplate btnSearch = new ButtonTemplate("Tìm kiếm",FontAwesome.Solid.SEARCH.create());
	private List<Pair<String,String>> listStatus = new ArrayList<Pair<String,String>>();
	private List<Pair<String, String>> listPriority = new ArrayList<Pair<String,String>>();

	private DetailsTemplate detailFilterAdvance = new DetailsTemplate("Tìm kiếm nâng cao");
	private TextField txtSymbol = new TextField("Ký hiệu ( VB )");
	private TextField txtNumber = new TextField("Số hiệu ( VB )");
	private ComboBox<Pair<String, String>> cmbOwner = new ComboBox<Pair<String,String>>("Đơn vị giao nhiệm vụ");
	private ComboBox<Pair<String, String>> cmbUserOfOwner = new ComboBox<Pair<String,String>>("Cán bộ của đơn vị giao");
	private ComboBox<Pair<String, String>> cmbAssignee = new ComboBox<Pair<String,String>>("Đơn vị xử lý");
	private ComboBox<Pair<String, String>> cmbUserOfAssignee = new ComboBox<Pair<String,String>>("Cán bộ của đơn vị được giao");
	private ComboBox<Pair<String, String>> cmbSupport = new ComboBox<Pair<String,String>>("Đơn vị hỗ trợ");
	private ComboBox<Pair<String, String>> cmbUserOfSupport = new ComboBox<Pair<String,String>>("Cán bộ của đơn vị hỗ trợ");
	private ComboBox<Pair<String, String>> cmbFollow = new ComboBox<Pair<String,String>>("Đơn vị theo dõi");
	private ComboBox<Pair<String, String>> cmbUserFollow = new ComboBox<Pair<String,String>>("Cán bộ của đơn vị theo dõi");
	private ComboBox<Pair<String, String>> cmbPriority = new ComboBox<Pair<String,String>>("Độ khẩn");
	private ComboBox<Pair<String, String>> cmbStatus = new ComboBox<Pair<String,String>>("Trạng thái");
	private ComboBox<Pair<String, String>> cmbOrgOrUser = new ComboBox<Pair<String,String>>("Nhiệm vụ");
	private ComboBox<Pair<String, String>> cmbSource = new ComboBox<Pair<String,String>>("Giao từ");
	private ComboBox<Pair<String, String>> cmbDocCategory = new ComboBox<Pair<String,String>>("Công văn");
	private ComboBox<ApiTagModel> cmbTags = new ComboBox<ApiTagModel>("Tìm theo thẻ");
	
	private boolean isMobileLayout = false;
	private Boolean isKpi = null;

	private UserAuthenticationModel userAuthenticationModel = SessionUtil.getUser();

	private BelongOrganizationModel belongOrganizationModel;
	private Map<String, List<String>> parametters;
	public TaskFilterOwnerForm(BelongOrganizationModel belongOrganizationModel,Map<String, List<String>> parametters) {
		this.belongOrganizationModel = belongOrganizationModel;
		this.parametters = parametters;
		checkLayoutMobile();
		buildLayout();
		configComponent();
		loadData();
		checkParametter();
	}

	@Override
	public void buildLayout() {
		this.setWidthFull();
		this.add(vLayoutFilter);
		loadLayout();
//		this.add(createLayout());

	}

	@Override
	public void configComponent() {
		
		cmbOwner.addValueChangeListener(e->{
			loadCmbUserOfOwner();
		});
		
		cmbUserOfOwner.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});
		
		cmbAssignee.addValueChangeListener(e->{
			loadCmbUserOfAssignee();
		});
		
		cmbUserOfAssignee.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});

		cmbSupport.addValueChangeListener(e->{
			loadCmbUserOfSupport();
		});
		
		cmbUserOfSupport.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});

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

		cmbFollow.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});

		cmbSource.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});

		cmbDocCategory.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});

		
		cmbTags.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});
		
		cmbDataType.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});
		
	}

	public void loadData() {
		loadDateTime();
		loadCmbOwner();
		loadCmbUserOfOwner();
		loadCmdAssignee();
		loadCmbUserOfAssignee();
		loadCmbPriority();
		loadCmbStatus();
		loadCmbSupport();
		loadCmbUserOfSupport();
		loadCmbFollow();
		loadCmbUserFollow();
		loadCmbOrgOrUser();
		loadCmbSource();
		loadDocCategory();
		loadCmbTag();
		loadCmbDataType();
	}

	private void checkLayoutMobile() {
		try {
			UI.getCurrent().getPage().retrieveExtendedClientDetails(e->{
				if(e.getScreenWidth() < 768) {
					dateStartDay.setWidth("49%");
					dateEndDay.setWidth("49%");
					iconDate.setVisible(false);
					txtNumber.setWidth("49%");
					txtSymbol.setWidth("49%");
					cmbOwner.setWidth("49%");
					cmbUserOfOwner.setWidth("49%");
					cmbAssignee.setWidth("49%");
					cmbUserOfAssignee.setWidth("49%");
					cmbSupport.setWidth("49%");
					cmbUserOfSupport.setWidth("49%");
					cmbFollow.setWidth("49%");
					cmbOrgOrUser.setWidth("49%");
					cmbSource.setWidth("49%");
					cmbUserFollow.setWidth("49%");
					cmbPriority.setWidth("49%");
					cmbStatus.setWidth("49%");
					cmbDocCategory.setWidth("49%");
					cmbTags.setWidth("49%");
					txtSeach.setWidth("99%");
					isMobileLayout = true;
				}
			});
		} catch (Exception e) {
		}
	}

	private void checkParametter() {
		if(parametters.containsKey("keysearch")) {
			txtSeach.setValue(parametters.get("keysearch").get(0));
		}

		if(parametters.containsKey("docnumber")) {
			txtNumber.setValue(parametters.get("docnumber").get(0));
		}

		if(parametters.containsKey("docsymbol")) {
			txtSymbol.setValue(parametters.get("docsymbol").get(0));
		}
		
		cmbStatus.getListDataView().getItems().filter(st->{
			String key = st.getKey() == null ? "nah" : st.getKey();
			return key.equals(parametters.get("status") == null ? "Oh no" : parametters.get("status").get(0));
		}).findFirst().ifPresent(e->{
			cmbStatus.setValue(e);
		});;
		
	}

	private void loadLayout() {
		vLayoutFilter.removeAll();
		
		dateStartDay.addThemeVariants(DateTimePickerVariant.LUMO_SMALL);
		dateStartDay.setWidth("180px");
		dateStartDay.setDatePickerI18n(LocalDateUtil.i18nVietNam());

		dateEndDay.addThemeVariants(DateTimePickerVariant.LUMO_SMALL);
		dateEndDay.setWidth("180px");
		dateEndDay.setDatePickerI18n(LocalDateUtil.i18nVietNam());

		txtSeach.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		txtSeach.setWidth("250px");
		txtSeach.setClearButtonVisible(true);
		txtSeach.setPlaceholder("Nhập từ khóa để tìm...");
		//		txtSeach.getStyle().setMarginTop("25px");

		btnSearch.addThemeVariants(ButtonVariant.LUMO_SMALL);
		btnSearch.getStyle().setMarginTop("28px");
		

		cmbPriority.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbPriority.setWidth("100px");

		cmbStatus.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbStatus.setWidth("150px");
		cmbStatus.getStyle().set("--vaadin-combo-box-overlay-width", "350px");
		
		cmbOwner.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbOwner.setWidth("200px");
		cmbOwner.getStyle().set("--vaadin-combo-box-overlay-width", "350px");
		
		cmbUserOfOwner.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbUserOfOwner.setWidth("150px");
		cmbUserOfOwner.getStyle().set("--vaadin-combo-box-overlay-width", "350px");
		
		cmbAssignee.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbAssignee.setWidth("200px");
		cmbAssignee.getStyle().set("--vaadin-combo-box-overlay-width", "350px");
		
		cmbUserOfAssignee.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbUserOfAssignee.setWidth("150px");
		cmbUserOfAssignee.getStyle().set("--vaadin-combo-box-overlay-width", "350px");
		
		cmbSupport.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbSupport.setWidth("200px");
		cmbSupport.getStyle().set("--vaadin-combo-box-overlay-width", "350px");
		
		cmbUserOfSupport.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbUserOfSupport.setWidth("150px");
		cmbUserOfSupport.getStyle().set("--vaadin-combo-box-overlay-width", "350px");
		
		cmbFollow.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbFollow.setWidth("200px");
		cmbFollow.getStyle().set("--vaadin-combo-box-overlay-width", "350px");
		
		cmbUserFollow.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbUserFollow.setWidth("150px");
		cmbUserFollow.getStyle().set("--vaadin-combo-box-overlay-width", "350px");
		
		cmbOrgOrUser.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbOrgOrUser.setWidth("120px");
		
		txtSymbol.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		txtSymbol.setWidth("100px");
		
		txtNumber.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		txtNumber.setWidth("100px");
		
		cmbSource.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbSource.setWidth("120px");
		
		cmbDocCategory.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbDocCategory.setWidth("120px");
		
		cmbTags.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		
		cmbDataType.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbDataType.getStyle().set("--vaadin-combo-box-overlay-width", "350px");
		

		txtSymbol.setPlaceholder("Nhập ký hiệu");
		txtNumber.setPlaceholder("Nhập số hiệu");
		

		iconDate.setSize("10px");
		iconDate.getStyle().setMarginTop("36px");
		
		HorizontalLayout hLayout = new HorizontalLayout();
		hLayout.setWidthFull();
		hLayout.add(dateStartDay,iconDate,dateEndDay,txtSeach,cmbStatus,cmbTags,cmbDataType,btnSearch);
		
		if(isMobileLayout) {
			dateStartDay.setWidthFull();
			dateEndDay.setWidthFull();
			hLayout.getStyle().setDisplay(Display.FLEX).setFlexWrap(com.vaadin.flow.dom.Style.FlexWrap.WRAP);
		}
		
		detailFilterAdvance.setWidthFull();
		detailFilterAdvance.setOpened(false);
		
		FlexLayout flexLayout = new FlexLayout();
		flexLayout.add(cmbOwner,cmbUserOfOwner,cmbAssignee,cmbUserOfAssignee,cmbSupport,cmbUserOfSupport,cmbFollow,cmbUserFollow,txtSymbol,txtNumber,cmbDocCategory,
				cmbPriority,cmbOrgOrUser,cmbSource);
		flexLayout.setFlexWrap(FlexWrap.WRAP);
		flexLayout.setFlexDirection(FlexDirection.ROW);
		flexLayout.getStyle().set("gap", "5px");
		detailFilterAdvance.add(flexLayout);
		
		vLayoutFilter.setWidthFull();
		vLayoutFilter.add(hLayout,detailFilterAdvance);
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
	
	private void loadCmbDataType() {
		List<Pair<Boolean, String>> data = new ArrayList<Pair<Boolean,String>>();
		data.add(Pair.of(null,"Tất cả"));
		data.add(Pair.of(true,"Nhiệm vụ tôi chỉ đạo"));
		data.add(Pair.of(false,"Nhiệm vụ giao thay"));
		
		cmbDataType.setItems(data);
		cmbDataType.setItemLabelGenerator(Pair::getValue);
		cmbDataType.setValue(data.get(0));
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

	private void loadCmbOrgOrUser() {
		List<Pair<String, String>> listOrgOrUser = new ArrayList<Pair<String,String>>();
		listOrgOrUser.add(Pair.of(null,"Tất cả"));
		listOrgOrUser.add(Pair.of("org","Đã phân"));
		listOrgOrUser.add(Pair.of("user","Chưa phân"));
		cmbOrgOrUser.setItems(listOrgOrUser);
		cmbOrgOrUser.setItemLabelGenerator(Pair::getValue);
		cmbOrgOrUser.setValue(listOrgOrUser.get(0));
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

	private void loadCmbSource() {
		List<Pair<String, String>> listSource = new ArrayList<Pair<String,String>>();
		listSource.add(Pair.of(null,"Tất cả"));
		ApiResultResponse<List<ApiTaskSourceModel>> getSource = ApiTaskService.getScource();
		if(getSource.isSuccess()) {
			getSource.getResult().forEach(model->{
				listSource.add(Pair.of(model.getKey(),model.getName()));
			});
		}

		cmbSource.setItems(listSource);
		cmbSource.setItemLabelGenerator(Pair::getRight);
		cmbSource.setValue(listSource.get(0));

	}

	private void loadCmbOwner() {
		List<Pair<String, String>> listPairOwner = new ArrayList<Pair<String,String>>();
		listPairOwner.add(Pair.of(belongOrganizationModel.getOrganizationId(),belongOrganizationModel.getOrganizationName()));
		listPairOwner.add(Pair.of("incChildOrgs","Tất cả ( Đơn vị đang sử dụng và các đơn vị cấp dưới )"));
		listPairOwner.add(Pair.of("incChildOrgsAndExcMyOrg","Tất cả đơn vị cấp dưới ( Không có đơn vị đang sử dụng )"));

		List<ApiOrganizationModel> listOrg = getSubOrgs(belongOrganizationModel.getOrganizationId());
		listOrg.forEach(model->{
			listPairOwner.add(Pair.of(model.getId()," -- "+model.getName()));
		});

		cmbOwner.setItems(listPairOwner);
		cmbOwner.setItemLabelGenerator(Pair::getRight);
		cmbOwner.setValue(listPairOwner.get(0));
	}
	
	private void loadCmbUserOfOwner() {
		List<Pair<String, String>> listUser = new ArrayList<Pair<String,String>>();
		listUser.add(Pair.of(null,"Tất cả"));
		
		if(cmbOwner.getValue().getKey() != null) {
			getListUserOfOrg(cmbOwner.getValue().getKey()).forEach(model->{
				listUser.add(Pair.of(model.getUserId(),model.getFullName()));
			});
		}
		
		cmbUserOfOwner.setItems(listUser);
		cmbUserOfOwner.setItemLabelGenerator(Pair::getValue);
		cmbUserOfOwner.setValue(listUser.get(0));
	}
	
	private void loadCmdAssignee() {
		List<Pair<String, String>> listAssignee = new ArrayList<Pair<String,String>>();
		listAssignee.add(Pair.of(null,"Tất cả"));
		ApiOrganizationModel infoCurrentOrg = getInfoOrg(belongOrganizationModel.getOrganizationId());
		// Các đơn vị ngang cấp
		List<ApiOrganizationModel> listOrgSame = getSubOrgs(infoCurrentOrg.getParentId());
		listOrgSame.forEach(model->{
			if(model.getId().equals(belongOrganizationModel.getOrganizationId())) {
				listAssignee.add(Pair.of(model.getId(),model.getName()));
			}else {
				listAssignee.add(Pair.of(model.getId(),model.getName() + " ( Đơn vị cùng cấp )"));
			}
			
		});


		// Các đơn vị cấp dưới của đơn vị hiện tại
		List<ApiOrganizationModel> listOrg = getSubOrgs(belongOrganizationModel.getOrganizationId());

		listOrg.stream().forEach(model->{
			listAssignee.add(Pair.of(model.getId(), " -- "+model.getName()));
		});

		cmbAssignee.setItems(listAssignee);
		cmbAssignee.setItemLabelGenerator(Pair::getRight);
		if(!listAssignee.isEmpty()) {
			cmbAssignee.setValue(listAssignee.get(0));
		}
	}
	
	private void loadCmbUserOfAssignee() {
		List<Pair<String, String>> listData = new ArrayList<Pair<String,String>>();
		listData.add(Pair.of(null,"Tất cả"));
		if(cmbAssignee.getValue().getKey() != null) {
			getListUserOfOrg(cmbAssignee.getValue().getKey()).forEach(model->{
				listData.add(Pair.of(model.getUserId(),model.getFullName()));
			});
		}
		
		cmbUserOfAssignee.setItems(listData);
		cmbUserOfAssignee.setItemLabelGenerator(Pair::getValue);
		cmbUserOfAssignee.setValue(listData.get(0));
	}

	private void loadCmbSupport() {
		List<Pair<String, String>> listPairSupport = new ArrayList<Pair<String,String>>();
		listPairSupport.add(Pair.of(null,"Tất cả"));
		ApiOrganizationModel infoCurrentOrg = getInfoOrg(belongOrganizationModel.getOrganizationId());
		// Các đơn vị ngang cấp
		List<ApiOrganizationModel> listOrgSame = getSubOrgs(infoCurrentOrg.getParentId());
		listOrgSame.forEach(model->{
			if(model.getId().equals(belongOrganizationModel.getOrganizationId())) {
				listPairSupport.add(Pair.of(model.getId(),model.getName()));
			}else {
				listPairSupport.add(Pair.of(model.getId(),model.getName() + " ( Đơn vị cùng cấp )"));
			}
		});

		
		// Các đơn vị cấp dưới của đơn vị hiện tại
		List<ApiOrganizationModel> listOrg = getSubOrgs(belongOrganizationModel.getOrganizationId());

		listOrg.stream().forEach(model->{
			listPairSupport.add(Pair.of(model.getId()," -- "+model.getName()));
		});

		cmbSupport.setItems(listPairSupport);
		cmbSupport.setItemLabelGenerator(Pair::getValue);
		cmbSupport.setValue(listPairSupport.get(0));
	}
	
	private void loadCmbUserOfSupport() {
		List<Pair<String, String>> listData = new ArrayList<Pair<String,String>>();
		listData.add(Pair.of(null,"Tất cả"));
		if(cmbSupport.getValue().getKey() != null) {
			getListUserOfOrg(cmbSupport.getValue().getKey()).forEach(model->{
				listData.add(Pair.of(model.getUserId(),model.getFullName()));
			});
		}
		
		cmbUserOfSupport.setItems(listData);
		cmbUserOfSupport.setItemLabelGenerator(Pair::getValue);
		cmbUserOfSupport.setValue(listData.get(0));
	}

	private void loadCmbFollow() {
		List<Pair<String, String>> listPairFollow = new ArrayList<Pair<String,String>>();
		listPairFollow.add(Pair.of(null,"Tất cả"));
		listPairFollow.add(Pair.of(belongOrganizationModel.getOrganizationId(),belongOrganizationModel.getOrganizationName()));
		
		List<ApiOrganizationModel> listOrgs = getSubOrgs(belongOrganizationModel.getOrganizationId());
		listOrgs.forEach(model->{
			listPairFollow.add(Pair.of(model.getId()," -- "+model.getName()));
		});
		
//		ApiResultResponse<List<ApiGroupExpandModel>> listGroup = ApiOrganizationService.getListGroup(belongOrganizationModel.getOrganizationId(),"");
//		if(listGroup.isSuccess()) {
//			listGroup.getResult().stream().forEach(model->{
//				listPairFollow.add(Pair.of(model.getGroupId(),model.getName()));
//			});
//		}

		cmbFollow.setItems(listPairFollow);
		cmbFollow.setItemLabelGenerator(Pair::getValue);
		cmbFollow.setValue(listPairFollow.get(0));
	}

	private void loadCmbUserFollow() {
		List<Pair<String, String>> listPairUserFollow = new ArrayList<Pair<String,String>>();
		listPairUserFollow.add(Pair.of(null,"Tất cả"));
		try {
			if(cmbFollow.getValue().getKey() != null) {
				ApiResultResponse<List<ApiUserGroupExpandModel>> listUserOfGroup = ApiOrganizationService.getListUserGroup(belongOrganizationModel.getOrganizationId(), cmbFollow.getValue().getKey());
				if(listUserOfGroup.isSuccess()) {
					listUserOfGroup.getResult().stream().forEach(model->{
						listPairUserFollow.add(Pair.of(model.getUserId(),model.getUserName()));
					});
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		cmbUserFollow.setItems(listPairUserFollow);
		cmbUserFollow.setItemLabelGenerator(Pair::getValue);
		cmbUserFollow.setValue(listPairUserFollow.get(0));
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
		
		return new ApiOrganizationModel();
	}
	
	//Get user of onwer
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

	private void loadDateTime() {
		dateStartDay.setLocale(LocalDateUtil.localeVietNam());
		dateStartDay.setValue(LocalDateUtil.longToLocalDateTime(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getStartOfTheYear(SessionUtil.getYear()))));


		dateEndDay.setValue(LocalDateUtil.longToLocalDateTime(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getEndtOfTheYear(SessionUtil.getYear()))));
		dateEndDay.setLocale(LocalDateUtil.localeVietNam());

	}

	public ApiFilterTaskModel getSearchData() {
		ApiFilterTaskModel apiFilterTaskModel = new ApiFilterTaskModel();

		apiFilterTaskModel.setFromDate(LocalDateUtil.localDateTimeToLong(dateStartDay.getValue()));
		apiFilterTaskModel.setToDate(LocalDateUtil.localDateTimeToLong(dateEndDay.getValue()));
		apiFilterTaskModel.setStatus(cmbStatus.getValue().getKey());
		
		//Owner
		BelongOrganizationModel viewParentOrg = SessionUtil.getParentBelongOrgModel();
		ApiOrganizationModel getCurrentOrg = getInfoOrg(belongOrganizationModel.getOrganizationId());
		if(viewParentOrg != null && getCurrentOrg.getParentId() != null  &&  !getCurrentOrg.getParentId().equals(viewParentOrg.getOrganizationId())) {
			ApiOrganizationModel getParent = getInfoOrg(getCurrentOrg.getParentId());
			BelongOrganizationModel parentOrg = new BelongOrganizationModel();
			parentOrg.setOrganizationId(getParent.getId());
			parentOrg.setOrganizationName(getParent.getName());
			viewParentOrg = parentOrg;
		}else {
			viewParentOrg = null;
		}
		
		if(isUser) {
			apiFilterTaskModel.setOwnerOrganizationId(null);
			apiFilterTaskModel.setOwnerOrganizationUserId(userAuthenticationModel.getId());
		}
		
		if(viewParentOrg != null && getCurrentOrg.getLevel().getKey().equals("room")) {
			if(cmbOwner.getValue() != null && cmbOwner.getValue().getKey() != null) {
				if(cmbOwner.getValue().getKey().equals("incChildOrgs") || cmbOwner.getValue().getKey().equals("incChildOrgsAndExcMyOrg")) {
					apiFilterTaskModel.setOwnerOrganizationId(viewParentOrg.getOrganizationId());
					apiFilterTaskModel.setDataScopeType(cmbOwner.getValue().getKey());
				}else {
					apiFilterTaskModel.setOwnerOrganizationId(cmbOwner.getValue().getKey());
				}
			}
		}else {
			if(cmbOwner.getValue().getKey().equals("incChildOrgs") || cmbOwner.getValue().getKey().equals("incChildOrgsAndExcMyOrg")) {
				apiFilterTaskModel.setOwnerOrganizationId(belongOrganizationModel.getOrganizationId());
				apiFilterTaskModel.setDataScopeType(cmbOwner.getValue().getKey());
			}else {
				apiFilterTaskModel.setOwnerOrganizationId(cmbOwner.getValue().getKey());
			}
		}
		
		
		if(cmbUserOfOwner.getValue() != null && cmbUserOfOwner.getValue().getKey() != null) {
			apiFilterTaskModel.setOwnerOrganizationUserId(cmbUserOfOwner.getValue().getKey());
		}
		
		//Assignee
		apiFilterTaskModel.setAssigneeOrganizationId(cmbAssignee.getValue().getKey());
		if(cmbUserOfAssignee.getValue() != null && cmbUserOfAssignee.getValue().getKey() != null) {
			apiFilterTaskModel.setAssigneeOrganizationUserId(cmbUserOfAssignee.getValue().getKey());
		}
		
		//Support
		apiFilterTaskModel.setSupportOrganizationId(cmbSupport.getValue().getKey());
		if(cmbUserOfSupport.getValue() != null && cmbUserOfSupport.getValue().getKey() != null) {
			apiFilterTaskModel.setSupportOrganizationUserId(cmbUserOfSupport.getValue().getKey());
		}
		
		//Follower
		apiFilterTaskModel.setFollowerOrganizationId(cmbFollow.getValue().getKey());
		if(cmbUserFollow.getValue() != null && cmbUserFollow.getValue().getKey() != null) {
			apiFilterTaskModel.setFollowerOrganizationUserId(cmbUserFollow.getValue().getKey());
		}
		
		apiFilterTaskModel.setPriority(cmbPriority.getValue().getKey());
		
//		apiFilterTaskModel.setFollowerOrganizationGroupId(cmbFollow.getValue().getKey());
//		apiFilterTaskModel.setFollowerOrganizationUserId(cmbUserFollow.getValue().getKey());
		apiFilterTaskModel.setKeyword(txtSeach.getValue());
		apiFilterTaskModel.setSource(cmbSource.getValue().getKey());
		apiFilterTaskModel.setDocCategory(cmbDocCategory.getValue().getKey());
		if(!txtSymbol.getValue().isEmpty()) {
			apiFilterTaskModel.setDocSymbol(txtSymbol.getValue());
		}

		if(!txtNumber.getValue().isEmpty()) {
			apiFilterTaskModel.setDocNumber(txtNumber.getValue());
		}
		
		apiFilterTaskModel.setOnlyOwner(cmbDataType.getValue().getKey());
		System.out.println(apiFilterTaskModel.getOnlyOwner());
		
		apiFilterTaskModel.setTagIds(cmbTags.getValue().getId());
		apiFilterTaskModel.setKpi(isKpi);
		
		return apiFilterTaskModel;
	}

	public ComboBox<Pair<String, String>> getCmbAssignee() {
		return cmbAssignee;
	}

	public ComboBox<Pair<String, String>> getCmbFollow() {
		return cmbFollow;
	}

	public ComboBox<Pair<String, String>> getCmbPriority() {
		return cmbPriority;
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

	public ComboBox<Pair<String, String>> getCmbStatus() {
		return cmbStatus;
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
