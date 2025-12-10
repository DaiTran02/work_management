package vn.com.ngn.page.app_access.forms;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import vn.com.ngn.api.app_access.ApiAppAccessModel;
import vn.com.ngn.api.app_access.ApiAppAccessService;
import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.app_access.models.AppAccessModel;
import vn.com.ngn.utils.components.DialogTemplate;
import vn.com.ngn.utils.components.NotificationTemplate;

public class EditAppAccessForm extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;

	private boolean checkIdNotNull = false;
	
	private ControlOrgForm controlOrgForm;
	
	private Button btnChooseOrg = new Button("Chọn đơn vị muốn cấp quyền",FontAwesome.Solid.PLUS.create());
	
	private TextField txtName = new TextField("Tên đơn vị");
	private TextField txtDesrOrg = new TextField("Mô tả");
	
	private DatePicker dateStartTime = new DatePicker("Ngày bắt đầu được phép truy cập");
	private DatePicker dateEndTime = new DatePicker("Ngày kết thúc truy cập");
	
	private Checkbox cbActive = new Checkbox("Hoạt động");
	private String inputOrgId = "";
	
	private IpAddressForm ipAddressForm;
	
	private String orgId = null;
	
	private String accessId;
	private Runnable onRun;
	public EditAppAccessForm(String accessId,Runnable onRun) {
		this.onRun = onRun;
		ipAddressForm = new IpAddressForm(Collections.emptyList());
		buildLayout();
		configComponent();
		if(accessId != null) {
			checkIdNotNull = true;
			this.accessId = accessId;
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
		loadDataDateTime();
		
		btnChooseOrg.addClickListener(e->{
			openDiaLogChooseOrg();
		});
		
	}
	
	public void loadData() {
		try {
			ApiResultResponse<ApiAppAccessModel> getAccess = ApiAppAccessService.getOneAppAccess(accessId);
			AppAccessModel accessModel = new AppAccessModel(getAccess.getResult());
			
			btnChooseOrg.setEnabled(false);
			txtName.setValue(accessModel.getName());
			
			txtDesrOrg.setValue(accessModel.getDescription());
			
			Instant instantStartDay = Instant.ofEpochMilli(accessModel.getStartTime());
			dateStartTime.setValue(instantStartDay.atZone(ZoneId.systemDefault()).toLocalDate());
			
			Instant instantEndDay = Instant.ofEpochMilli(accessModel.getEndTime());
			dateEndTime.setValue(instantEndDay.atZone(ZoneId.systemDefault()).toLocalDate());
			
			cbActive.setValue(accessModel.isActive());
			inputOrgId = accessModel.getOrganizationId();
			ipAddressForm = new IpAddressForm(accessModel.getIpsAccess());
			
			this.removeAll();
			this.add(createLayout());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void loadDataDateTime() {
		LocalDate localDate = LocalDate.now();
		@SuppressWarnings("deprecation")
		Locale locale = new Locale("vi","VN");

		
		dateStartTime.setValue(localDate);
		dateStartTime.setMin(localDate);
		dateStartTime.setLocale(locale);
		
		dateEndTime.setValue(localDate.plusDays(1));
		dateEndTime.setMin(localDate);
		dateEndTime.setLocale(locale);
	}
	
	private Component createLayout() {
		VerticalLayout layout = new VerticalLayout();
		
		
		txtName.setSizeFull();
		txtName.setReadOnly(true);
		
		txtDesrOrg.setWidthFull();
		txtDesrOrg.setReadOnly(true);
		
		btnChooseOrg.getStyle().setCursor("pointer");
		layout.add(btnChooseOrg,txtName,txtDesrOrg);
		
		HorizontalLayout hLayoutDate = new HorizontalLayout();
		hLayoutDate.setWidthFull();
		
		dateStartTime.setWidthFull();
		dateEndTime.setWidthFull();
		hLayoutDate.add(dateStartTime,dateEndTime);
		
		layout.add(hLayoutDate,ipAddressForm,cbActive);
		
		
		layout.setSizeFull();
		return layout;
	}
	
	private void openDiaLogChooseOrg() {
		DialogTemplate dialog = new DialogTemplate("CHỌN ĐƠN VỊ CẤP QUYỀN TRUY CẬP",()->{

		});
		controlOrgForm = new ControlOrgForm(orgId,()->{
			txtName.setValue(controlOrgForm.getOrg().getName());
			txtDesrOrg.setValue(controlOrgForm.getOrg().getDescription());
			inputOrgId = controlOrgForm.getOrg().getId();
			dialog.close();
		});
		dialog.add(controlOrgForm);
		dialog.setWidth("90%");
		dialog.setHeight("90%");
		dialog.getBtnSave().addClickListener(e->{
			dialog.close();
		});
		dialog.open();
	}
	
	public void save() {
		AppAccessModel appAccessModel = new AppAccessModel();
		
		appAccessModel.setName(txtName.getValue());
		appAccessModel.setDescription(txtDesrOrg.getValue());
		appAccessModel.setActive(cbActive.getValue());
		appAccessModel.setOrganizationId(inputOrgId);
		
		LocalDate localDateStartDay = dateStartTime.getValue();
		LocalDateTime localDateTimeStartDay = localDateStartDay.atTime(00,00);
		appAccessModel.setStartTime(localDateTimeStartDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		
		LocalDate localDateEndDay = dateEndTime.getValue();
		LocalDateTime localDateTimeEndDay = localDateEndDay.atTime(23,59);
		appAccessModel.setEndTime(localDateTimeEndDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		
		List<String> listIp = new ArrayList<String>();
		
		for(TextField tx : ipAddressForm.getListData()) {
			listIp.add(tx.getValue());
		}

		appAccessModel.setIpsAccess(listIp);
		
		if(checkIdNotNull) {
			doUpdate(appAccessModel);
		}else {
			doCreate(appAccessModel);
		}
	}
	
	private void doCreate(AppAccessModel accessModel) {
		try {
			ApiAppAccessModel apiAppAccessModel = new ApiAppAccessModel(accessModel);
			ApiResultResponse<Object> create = ApiAppAccessService.createAppAccess(apiAppAccessModel);
			if(create.getStatus() == 200 || create.getStatus() == 201) {
				NotificationTemplate.success("Thành công");
				onRun.run();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void doUpdate(AppAccessModel appAccessModel) {
		try {
			ApiAppAccessModel apiAppAccessModel = new ApiAppAccessModel(appAccessModel);
			ApiResultResponse<Object> update = ApiAppAccessService.updateAppAccess(accessId, apiAppAccessModel);
			if(update.getStatus() == 200 || update.getStatus() == 201) {
				NotificationTemplate.success("Cập nhật thành công");
				onRun.run();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
