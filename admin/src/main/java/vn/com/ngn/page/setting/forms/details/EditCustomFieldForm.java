package vn.com.ngn.page.setting.forms.details;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.vaadin.addons.tatu.ColorPicker;
import org.vaadin.addons.tatu.ColorPicker.ColorPreset;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexDirection;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;
import com.vaadin.flow.component.textfield.TextField;

import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.setting.model.ComponentModel;
import vn.com.ngn.utils.components.ButtonTemplate;
import vn.com.ngn.utils.components.DialogTemplate;
import vn.com.ngn.utils.components.VerticalLayoutTemplate;

public class EditCustomFieldForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private VerticalLayout vLayout = new VerticalLayout();
	private ButtonTemplate btnChooseComponent = new ButtonTemplate("Chọn thành phần muốn sử dụng",FontAwesome.Solid.HAND_POINTER.create());
	
	private List<ComponentModel> listComponentIsChoose = new ArrayList<ComponentModel>();
	
	public EditCustomFieldForm() {
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		
		btnChooseComponent.getStyle().set("margin-left", "auto");
		
		vLayout.setSizeFull();
		this.add(btnChooseComponent,vLayout);
		
		
	}

	@Override
	public void configComponent() {
		btnChooseComponent.addClickListener(e->openDialogListComponents());
	}
	
	private void loadData() {
		vLayout.removeAll();
		listComponentIsChoose.stream().forEach(model->{
			createCustomComponent(model);
		});
	}
	
	private void openDialogListComponents() {
		DialogTemplate dialogTemplate = new DialogTemplate("Danh sách thành phần giao diện",()->{});
		ListComponentsForm listComponentsForm = new ListComponentsForm();
		dialogTemplate.add(listComponentsForm);
		dialogTemplate.setWidth("40%");
		dialogTemplate.setHeightFull();
		dialogTemplate.getBtnSave().setText("Chọn");
		dialogTemplate.getBtnSave().addClickListener(e->{
			listComponentIsChoose.clear();
			listComponentIsChoose.addAll(listComponentsForm.getlistComponent());
			loadData();
			dialogTemplate.close();
		});
		dialogTemplate.open();
	}
	
	private void createCustomComponent(ComponentModel componentModel) {
		
		switch (componentModel.getKey()) {
		case "button": 
			vLayout.add(createLayoutButton());
			break;
			
		case "textfield":
			vLayout.add(createLayoutTextField());
			break;
			
		case "select":
			vLayout.add(createLayoutSelect());
			break;
		}
		
		
	}
	
	
	private Component createLayoutButton() {
		VerticalLayout vLayout = new VerticalLayout();
		
		FlexLayout hLayoutAttitute = new FlexLayout();
		hLayoutAttitute.setWidthFull();
		
		TextField txtNameButton = new TextField("Nhập tên nút bấm");
		TextField txtWidthButton = new TextField("Chiều dài");
		ColorPicker colorPicker = new ColorPicker();
		colorPicker.setLabel("Màu nền");
		try {
			colorPicker
	        .setPresets(Arrays.asList(new ColorPreset("rgb(210, 240, 199)", "Màu nền 1"),
	                new ColorPreset("rgb(240, 199, 199)", "Màu nền 2"),
	                new ColorPreset("rgb(177, 213, 251)","Màu nền 3")));
		}catch(Exception e) {
			
		}
		
		ColorPicker colorText = new ColorPicker();
		colorText.setLabel("Màu chữ");
		colorText
        .setPresets(Arrays.asList(new ColorPreset("#00681c", "Màu chữ 1"),
                new ColorPreset("#680000", "Màu chữ 2"),
                new ColorPreset("#000b68","Màu chữ 3")));
		 
		hLayoutAttitute.add(txtNameButton,txtWidthButton,colorPicker,colorText);
		hLayoutAttitute.setFlexWrap(FlexWrap.WRAP);
		hLayoutAttitute.setFlexDirection(FlexDirection.ROW);
		hLayoutAttitute.getStyle().set("gap", "3px");
		
		
		Button btnEx = new Button();
		
		colorPicker.addValueChangeListener(e->{
			btnEx.getStyle().setBackground(e.getValue());
		});
		
		colorText.addValueChangeListener(e->{
			btnEx.getStyle().setColor(e.getValue());
		});
		
		txtNameButton.addValueChangeListener(e->{
			btnEx.setText(txtNameButton.getValue());
		});
		
		txtWidthButton.addValueChangeListener(e->{
			btnEx.setWidth(txtWidthButton.getValue());
		});
		
		vLayout.add(createLayoutGeneral("Nút bấm"),hLayoutAttitute,new Hr(),btnEx);
		btnEx.getStyle().setMargin("0 auto");
		
		//The class name in file custom_component.css
		vLayout.addClassName("layout-component_edit");
		
		return vLayout;
	}
	
	private Component createLayoutTextField() {
		VerticalLayout vLayoutTextField = new VerticalLayout();
		vLayoutTextField.add(createLayoutGeneral("Ô nhập"));
		
		TextField txtEx = new TextField();

		FlexLayout hLayoutProperties = new FlexLayout();
		hLayoutProperties.setWidthFull();
		
		TextField txtKey = new TextField("Key (Từ khóa)*");
		
		TextField txtName = new TextField("Tên*");
		
		TextField txtPlaceholder = new TextField("Placeholder");
		
		TextField txtHelper = new TextField("Mô tả");
		
		TextField txtWidth = new TextField("Kích thước");
		txtWidth.setPlaceholder("px");
		
		txtPlaceholder.addValueChangeListener(e->{
			txtEx.setPlaceholder(txtPlaceholder.getValue());
		});
		
		txtHelper.addValueChangeListener(e->{
			txtEx.setHelperText(txtHelper.getValue());
		});
		
		txtWidth.addValueChangeListener(e->{
			txtEx.setWidth(txtWidth.getValue());
		});
		
		txtName.addValueChangeListener(e->{
			txtEx.setLabel(txtName.getValue());
		});
		
		hLayoutProperties.add(txtKey,txtName,txtPlaceholder,txtHelper,txtWidth);
		hLayoutProperties.setFlexWrap(FlexWrap.WRAP);
		hLayoutProperties.setFlexDirection(FlexDirection.ROW);
		hLayoutProperties.getStyle().set("gap", "3px");
		
		
		vLayoutTextField.add(hLayoutProperties,new Hr(),txtEx);
		txtEx.getStyle().setMargin("0 auto");
		
		vLayoutTextField.setWidthFull();
		//The class name in file custom_component.css
		vLayoutTextField.addClassName("layout-component_edit");
		
		return vLayoutTextField;
	}
	
	
	private List<Pair<String, String>> dataOfSelect = new ArrayList<Pair<String,String>>();
	private Component createLayoutSelect() {
		VerticalLayout vLayoutSelect = new VerticalLayout();
		vLayoutSelect.add(createLayoutGeneral("Thanh lựa chọn"));
		
		HorizontalLayout hLayoutMulti = new HorizontalLayout();
		MultiSelectComboBox<Pair<String, String>> multiIsSelect = new MultiSelectComboBox<Pair<String,String>>("Dữ liệu đã thêm");
		multiIsSelect.setWidthFull();
		
		multiIsSelect.setItems(dataOfSelect);
		multiIsSelect.setItemLabelGenerator(Pair::getValue);
		multiIsSelect.select(dataOfSelect);
		
		ButtonTemplate btnAddData = new ButtonTemplate("Thêm dữ liệu",FontAwesome.Solid.PLUS.create());
		btnAddData.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnAddData.getStyle().set("margin-top", "30px");
		
		hLayoutMulti.setWidthFull();
		hLayoutMulti.add(multiIsSelect,btnAddData);
		
		btnAddData.addClickListener(e->openDialogAddDataForSelect());
		
		FlexLayout layoutProperties = new FlexLayout();
		
		
		Select<Pair<String, String>> selectEx = new Select<Pair<String,String>>();
		
		vLayoutSelect.add(hLayoutMulti,layoutProperties, new Hr(),selectEx);
		
		//The class name in file custom_component.css
		vLayoutSelect.addClassName("layout-component_edit");
		return vLayoutSelect;
	}
	
	private Component createLayoutGeneral(String nameComponent) {
		VerticalLayout vLayoutGeneral = new VerticalLayout();
		
		H3 deader = new H3(nameComponent);
		
		
		vLayoutGeneral.add(deader,new Hr());
		
		HorizontalLayout hLayoutGeneral = new HorizontalLayout();
		
		ComboBox<Pair<String, String>> cmbNav = new ComboBox<Pair<String,String>>("Nơi áp dụng");
		cmbNav.setItems(getDataNav());
		cmbNav.setItemLabelGenerator(Pair::getValue);
		
		ComboBox<Pair<String, String>> cmbFunction = new ComboBox<Pair<String,String>>("Chức năng sử dụng");
		cmbFunction.setItems(getDataFunction());
		cmbFunction.setItemLabelGenerator(Pair::getValue);
		cmbFunction.setWidth("220px");
		
		hLayoutGeneral.add(cmbNav,cmbFunction);
		hLayoutGeneral.setPadding(false);
		
		vLayoutGeneral.add(hLayoutGeneral);
		vLayoutGeneral.setWidthFull();
		vLayoutGeneral.setPadding(false);
		
		return vLayoutGeneral;
	}
	
	private List<Pair<String, String>> getDataNav (){
		List<Pair<String, String>> listData = new ArrayList<Pair<String,String>>();
		listData.add(Pair.of("doc","Văn bản"));
		listData.add(Pair.of("taskowner","Nhiệm vụ đã giao"));
		listData.add(Pair.of("taskassignee","Nhiệm vụ được giao"));
		listData.add(Pair.of("tasksupport","Nhiệm vụ phối hợp"));
		listData.add(Pair.of("taskfollower","Theo dõi nhiệm vụ"));
		return listData;
	}
	
	
	private List<Pair<String, String>> getDataFunction(){
		List<Pair<String, String>> listData = new ArrayList<Pair<String,String>>();
		
		listData.add(Pair.of("filter","Chức năng tìm kiếm"));
		listData.add(Pair.of("detail","Thêm vào trường nhập dữ liệu"));
		
		return listData;
	}
	
	private void openDialogAddDataForSelect() {
		DialogTemplate dialogTemplate = new DialogTemplate("Thêm dữ liệu cho lưa chọn",()->{});
		
		AddDataForSelectForm addDataForSelectForm = new AddDataForSelectForm();
		dialogTemplate.add(addDataForSelectForm);
		dialogTemplate.setWidth("40%");
		dialogTemplate.setHeightFull();
		dialogTemplate.getBtnSave().setText("Thêm");
		dialogTemplate.getBtnSave().addClickListener(e->{
			dataOfSelect.clear();
			dataOfSelect.addAll(addDataForSelectForm.getData());
			dialogTemplate.close();
		});
		dialogTemplate.open();
	}
	
	

}
