package vn.com.ngn.page.report.forms;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.tuple.Pair;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.report.ApiOrganizationForReportModel;
import vn.com.ngn.api.report.ApiReportService;
import vn.com.ngn.api.report.ApiUserOrganizationModel;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.report.excels.ExportExcelUsersUsing;
import vn.com.ngn.page.report.models.OrganizationForReportModel;
import vn.com.ngn.page.report.models.UserOrganizationModel;
import vn.com.ngn.utils.components.ButtonDownLoadTemplate;
import vn.com.ngn.utils.components.DialogTemplate;
import vn.com.ngn.utils.components.NotificationTemplate;

public class ReportUsersUsingForm extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;

	private ChooseOrganizationForm chooseOrganizationForm;

	private Button btnChooseOrg = new Button("Chọn đơn vị lập báo cáo",FontAwesome.Solid.FOLDER_TREE.create());
	private Button btnCreate = new Button("Lập báo cáo",FontAwesome.Solid.FILE_ALT.create());

	private ButtonDownLoadTemplate btn = new ButtonDownLoadTemplate("Tải về",FontAwesome.Solid.DOWNLOAD.create());

	private TextField txtOrg = new TextField("Tên đơn vị báo cáo");
	private List<UserOrganizationModel> listUserOrganizationModels = new ArrayList<UserOrganizationModel>();

	private Checkbox cbChooseAllOrg = new Checkbox("Bao gồm các cơ quan và đơn vị trực thuộc");
	private ComboBox<Pair<String, String>> cmbStatus = new ComboBox<Pair<String,String>>("Trạng thái");
	private List<Pair<String, String>> listDataCmbStatus = new ArrayList<Pair<String,String>>();

	private DatePicker dateStartTime = new DatePicker("Từ ngày ");
	private DatePicker dateEndTime = new DatePicker("Đến ngày");


	private Grid<UserOrganizationModel> grid = new Grid<UserOrganizationModel>(UserOrganizationModel.class,false);

	private VerticalLayout vLayout = new VerticalLayout();


	private String orgId = null;
	public ReportUsersUsingForm() {
		buildLayout();
		configComponent();
		initDataCmbStatus();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.setPadding(false);
		vLayout.add(createLayoutGrid());
		this.add(createToolbar(),vLayout);
	}

	@Override
	public void configComponent() {
		btnChooseOrg.addClickListener(e->{
			openDialogChooseOrg();
		});

		btnCreate.addClickListener(e->{
			if(orgId == null) {
				NotificationTemplate.error("Vui lòng chọn đơn vị lập báo cáo");
			}else {
				loadData(orgId);
			}
		});



	}

	public void loadData(String orgId) {
		listUserOrganizationModels.clear();
		LocalDate localDateStartDay = dateStartTime.getValue();
		LocalDateTime localDateTimeStartDay = localDateStartDay.atTime(00,00);

		LocalDate localDateEndDay = dateEndTime.getValue();
		LocalDateTime localDateTimeEndDay = localDateEndDay.atTime(23,59);
		try {
			ApiResultResponse<ApiOrganizationForReportModel> data = ApiReportService.getListUsersUsing(orgId,cbChooseAllOrg.getValue(),cmbStatus.getValue().getKey(), 
					localDateTimeStartDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), 
					localDateTimeEndDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
					);


			if(cbChooseAllOrg.getValue() == true) {
				OrganizationForReportModel organizationForReportModel = new OrganizationForReportModel(data.getResult());
				initData(organizationForReportModel);
			}else {
				for(ApiUserOrganizationModel apiUserOrganizationModel : data.getResult().getUserOrganizationExpands()) {
					UserOrganizationModel userOrganizationModel = new UserOrganizationModel(apiUserOrganizationModel);
					listUserOrganizationModels.add(userOrganizationModel);
				}
			}

			vLayout.removeAll();
			vLayout.add(createLayoutGrid());
			grid.setItems(listUserOrganizationModels);

		} catch (Exception e) {
			e.printStackTrace();
		}

		ExportExcelUsersUsing createExcelReport = new ExportExcelUsersUsing(txtOrg.getValue(),cbChooseAllOrg.getValue());
		createExcelReport.setListData(listUserOrganizationModels);
		btn.getButton().setEnabled(true);
		btn.getButton().addClickListener(e->{
			try {
				btn.download(createExcelReport.createReport());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});


	}

	private void initData(OrganizationForReportModel organizationForReportModel) {
		List<UserOrganizationModel> listUserOrganizationModels = organizationForReportModel.getUserOrganizationExpands();
		for(UserOrganizationModel userOrganizationModel : listUserOrganizationModels) {
			userOrganizationModel.setOrgName(organizationForReportModel.getName());
			this.listUserOrganizationModels.add(userOrganizationModel);
		}

		List<OrganizationForReportModel> listOrganizationForReportModels = organizationForReportModel.getSubOrganizations() == (null) ? null : organizationForReportModel.getSubOrganizations();
		if(listOrganizationForReportModels == null || listOrganizationForReportModels.isEmpty()) {
			return;
		}else {
			for(OrganizationForReportModel organizationForReportModel2 : listOrganizationForReportModels) {
				initData(organizationForReportModel2);
			}
		}

	}

	private void initDataCmbStatus() {
		listDataCmbStatus.clear();
		Pair<String, String> item = Pair.of(null,"Tất cả");
		listDataCmbStatus.add(item);
		Pair<String, String> item2 = Pair.of("logged","Sử dụng");
		listDataCmbStatus.add(item2);
		Pair<String, String> item3 = Pair.of("notlogged","Không sử dụng");
		listDataCmbStatus.add(item3);

		cmbStatus.setItems(listDataCmbStatus);
		cmbStatus.setItemLabelGenerator(Pair::getRight);
		cmbStatus.setValue(listDataCmbStatus.get(0));
	}

	private Grid<UserOrganizationModel> createLayoutGrid(){
		grid = new Grid<UserOrganizationModel>(UserOrganizationModel.class,false);

		grid.addComponentColumn(model->{
			return new Span(model.getMoreInfo().getFullName());
		}).setHeader("Tên");

		grid.addComponentColumn(model->{
			return new Span(model.getMoreInfo().getUsername());
		}).setHeader("UserName");
		grid.addComponentColumn(model->{
			return new Span(model.getMoreInfo().getEmail());
		}).setHeader("Email");

		grid.addComponentColumn(model->{
			String possition = model.getPositionText();
			return new Span(possition);
		}).setHeader("Chức vụ trong đơn vị");
		grid.addComponentColumn(model->{
			String account = model.getAccountIOfficeText();
			return new Span(account);
		}).setHeader("Tài khoản kết nối");
		grid.addColumn(createStatusComponentRenderer()).setHeader("Tình trạng");
		grid.addColumn(model->{
			return model.getOrgName();
		}).setHeader("Đơn vị");
		
		return grid;
	}




	@SuppressWarnings("deprecation")
	private Component createToolbar() {
		VerticalLayout layoutGenera = new VerticalLayout();

		HorizontalLayout hLayout = new HorizontalLayout();

		btnChooseOrg.getStyle().setCursor("pointer").set("margin-top", "30px");;

		txtOrg.setReadOnly(true);
		txtOrg.setWidthFull();

		btnCreate.getStyle().setCursor("pointer");

		btn.getButton().addThemeVariants(ButtonVariant.LUMO_SUCCESS);
		btn.getButton().getStyle().setCursor("pointer");
		btn.getButton().setEnabled(false);

		dateStartTime.setLocale(new Locale("vi", "VN"));
		dateEndTime.setLocale(new Locale("vi", "VN"));

		LocalDate localEndStart = LocalDate.now();
		dateEndTime.setValue(localEndStart);

		LocalDate startDate =  localEndStart.minusMonths(1);
		dateStartTime.setValue(startDate);

		hLayout.setWidthFull();
		hLayout.add(btnChooseOrg,txtOrg,cmbStatus,dateStartTime,dateEndTime);
		hLayout.expand(txtOrg);

		HorizontalLayout hLayout2 = new HorizontalLayout();
		hLayout2.setWidthFull();
		cbChooseAllOrg.setWidthFull();
		hLayout2.add(cbChooseAllOrg,btnCreate,btn);
		hLayout2.expand(cbChooseAllOrg);

		layoutGenera.add(hLayout,hLayout2);
		layoutGenera.setWidthFull();

		return layoutGenera;
	}

	private void openDialogChooseOrg() {
		DialogTemplate dialog = new DialogTemplate("CHỌN ĐƠN VỊ",()->{

		});

		chooseOrganizationForm = new ChooseOrganizationForm(null, ()->{
			txtOrg.setValue(chooseOrganizationForm.getOrg().getName());
			orgId = chooseOrganizationForm.getOrg().getId();
			dialog.close();
		});

		dialog.add(chooseOrganizationForm);
		dialog.setSizeFull();
		dialog.getBtnSave().setVisible(false);
		dialog.getBtnSave().addClickListener(e->{
			dialog.close();
		});
		dialog.open();
	}

	private static final SerializableBiConsumer<Span, UserOrganizationModel> statusComponent = (span,user)->{
		boolean isActive = user.getStatus().equals("logged") ? true : false;
		String theme = String.format("badge %s", isActive ? "success" : "error");
		span.getElement().setAttribute("theme", theme);
		String status = user.getStatus().equals("logged") ? "Sử dụng" : "Không sử dụng";
		span.setText(status);
	};

	private static ComponentRenderer<Span, UserOrganizationModel> createStatusComponentRenderer(){
		return new ComponentRenderer<Span, UserOrganizationModel>(Span::new, statusComponent);
	}

















}
