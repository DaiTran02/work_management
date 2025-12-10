package com.ngn.tdnv.tag.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.vaadin.addons.tatu.ColorPicker;
import org.vaadin.addons.tatu.ColorPicker.ColorPreset;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tags.ApiTagCreatetorModel;
import com.ngn.api.tags.ApiTagModel;
import com.ngn.api.tags.ApiTagService;
import com.ngn.api.utils.ApiKeyValueModel;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Style.AlignItems;

public class EditTagForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private BelongOrganizationModel belongOrganizationModel = SessionUtil.getOrg();
	private UserAuthenticationModel userAuthenticationModel = SessionUtil.getUser();
	
	private TextField txtName = new TextField("Tên");
	private Checkbox cbActive = new Checkbox("Hoạt động");
	private ColorPicker colorPicker = new ColorPicker();
	private RadioButtonGroup<Pair<String, String>> radioGroup = new RadioButtonGroup<Pair<String,String>>();
	private ButtonTemplate btnTag = new ButtonTemplate(FontAwesome.Solid.TAG.create());
	
	private String idTag;
	private String type;
	public EditTagForm(String idTag,String type) {
		this.type = type;
		buildLayout();
		configComponent();
		loadRadioGroup();
		if(idTag != null) {
			this.idTag = idTag;
			loadData();
		}
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		HorizontalLayout hlayout = new HorizontalLayout();
		hlayout.add(txtName,cbActive);
		hlayout.setWidthFull();
		
		txtName.setWidthFull();
		cbActive.setWidth("130px");
		cbActive.getStyle().setMarginTop("25px");
		cbActive.setValue(true);
		
		hlayout.getStyle().setAlignItems(AlignItems.CENTER);
		
		HorizontalLayout hLayoutColor = new HorizontalLayout();
		hLayoutColor.add(colorPicker,btnTag);
		
		try {
			colorPicker.setPresets(Arrays.asList(new ColorPreset("#00681c", "Màu 1"),
	                new ColorPreset("#680000", "Màu 2"),
	                new ColorPreset("#000b68","Màu 3"),
	                new ColorPreset("#eb0406", "Màu 4"),
	                new ColorPreset("#4448ff", "Màu 5")));
		}catch(Exception e) {
			
		}
		
		btnTag.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnTag.getStyle().setFontSize("30px").setColor("#680000");
		colorPicker.setValue("#680000");
		
		colorPicker.addValueChangeListener(e->{
			btnTag.getStyle().setColor(e.getValue());
		});
		
		hLayoutColor.add(colorPicker,btnTag,radioGroup);
		hLayoutColor.getStyle().setAlignItems(AlignItems.CENTER);
		
		this.add(hlayout,hLayoutColor);
	}

	@Override
	public void configComponent() {
		
	}
	
	private void loadRadioGroup() {
		List<Pair<String, String>> listData = new ArrayList<Pair<String,String>>();
		ApiResultResponse<List<ApiKeyValueModel>> data = ApiTagService.getTypeFilter();
		if(data.isSuccess()) {
			data.getResult().forEach(model->{
				listData.add(Pair.of(model.getKey(),model.getName()));
			});
		}
		
		radioGroup.setItems(listData);
		radioGroup.setValue(listData.get(0));
		radioGroup.setItemLabelGenerator(Pair::getValue);
		
		if(this.type != null) {
			if(this.type.equals("Doc")) {
				radioGroup.setValue(listData.get(0));
			}else {
				radioGroup.setValue(listData.get(1));
			}
		}
		
	}
	
	private void loadData() {
		ApiResultResponse<ApiTagModel> data = ApiTagService.getTag(idTag);
		if(data.isSuccess()) {
			txtName.setValue(data.getResult().getName());
			colorPicker.setValue(data.getResult().getColor());
			cbActive.setValue(data.getResult().isActive());
		}
	}
	
	public void save() {
		ApiTagModel apiTagModel = new ApiTagModel();
		
		apiTagModel.setColor(colorPicker.getValue());
		apiTagModel.setActive(cbActive.getValue());
		apiTagModel.setName(txtName.getValue());
		apiTagModel.setType(radioGroup.getValue().getKey());
		
		ApiTagCreatetorModel apiTagCreatetorModel = new ApiTagCreatetorModel();
		apiTagCreatetorModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
		apiTagCreatetorModel.setOrganizationName(belongOrganizationModel.getOrganizationName());
		apiTagCreatetorModel.setOrganizationUserId(userAuthenticationModel.getId());
		apiTagCreatetorModel.setOrganizationUserName(userAuthenticationModel.getFullName());
		
		apiTagModel.setCreator(apiTagCreatetorModel);
		
		if(idTag != null) {
			doUpdateTag(apiTagModel);
		}else {
			doCreateTag(apiTagModel);
		}
	}
	
	private void doCreateTag(ApiTagModel apiTagModel) {
		ApiResultResponse<Object> create = ApiTagService.createTag(apiTagModel);
		if(create.isSuccess()) {
			fireEvent(new ClickEvent(this, false));
		}
	}
	
	private void doUpdateTag(ApiTagModel apiTagModel) {
		ApiResultResponse<Object> update = ApiTagService.updateTag(idTag, apiTagModel);
		if(update.isSuccess()) {
			fireEvent(new ClickEvent(this, false));
		}
	}
	
	

}
