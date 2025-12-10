package vn.com.ngn.page.report.forms;

import java.util.List;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.report.ApiFilterReportOrgSystemModel;
import vn.com.ngn.api.report.ApiReportService;
import vn.com.ngn.api.report.ApiUserSystemModel;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.report.excels.ExportExcelUserSystem;
import vn.com.ngn.utils.LocalDateUtils;
import vn.com.ngn.utils.components.ButtonDownLoadTemplate;

public class ReportUserSystemForm extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;

	private Button btnCreate = new Button("Lập báo cáo",FontAwesome.Solid.FILE_ALT.create());

	private ButtonDownLoadTemplate btnDownLoad = new ButtonDownLoadTemplate("Tải về",FontAwesome.Solid.FILE_EXPORT.create());
	private Checkbox cbUsed = new Checkbox("Sử dụng");
	private Checkbox cbActive = new Checkbox("Hoạt động");

	private TextField txtOrg = new TextField();
	
	private Grid<ApiUserSystemModel> grid = new Grid<ApiUserSystemModel>(ApiUserSystemModel.class,false);

	public ReportUserSystemForm() {
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.setPadding(false);
		this.add(createToolbar(),createGrid());
	}

	@Override
	public void configComponent() {

		btnCreate.addClickListener(e->{
			loadData();
		});
		
		cbActive.addClickListener(e->{
			loadData();
		});
		
		cbUsed.addClickListener(e->{
			loadData();
		});

	}

	public void loadData() {
		ApiResultResponse<List<ApiUserSystemModel>> getListData = null;
		try {
			getListData = ApiReportService.getListUsersSystem(getSearch());
			initLayout(getListData.getResult());

			ExportExcelUserSystem createExcelReport = new ExportExcelUserSystem("",false,true);
			createExcelReport.setListData(getListData.getResult());
			btnDownLoad.setEnabled(true);
			btnDownLoad.getButton().addClickListener(e->{
				try {
					btnDownLoad.download(createExcelReport.createReport());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	
	}

	private void initLayout(List<ApiUserSystemModel> listUserOrganizationModels) {
		grid.setItems(listUserOrganizationModels);
	}
	
	private Component createGrid() {
		grid = new Grid<ApiUserSystemModel>(ApiUserSystemModel.class,false);
		
		grid.addColumn(ApiUserSystemModel::getUsername).setHeader("Tên tài khoản");
		grid.addColumn(ApiUserSystemModel::getFullName).setHeader("Tên đầy đủ");
		grid.addColumn(ApiUserSystemModel::getEmail).setHeader("Email");
		grid.addColumn(model->{
			return LocalDateUtils.dfDate.format(model.getCreatedTime());
		}).setHeader("Ngày tạo tài khoản");
		
		grid.addColumn(model->{
			return model.getUpdatedTime() == 0 ? "Chưa cập nhật" : LocalDateUtils.dfDate.format(model.getUpdatedTime());
		}).setHeader("Ngày cập nhật");
		
		grid.addColumn(model->{
			return model.getActiveText();
		}).setHeader("Trạng thái");
		
		return grid;
	}

	private Component createToolbar() {
		HorizontalLayout hLayout = new HorizontalLayout();


		btnCreate.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnCreate.getStyle().setCursor("pointer");

		btnDownLoad.getButton().addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnDownLoad.getButton().addThemeVariants(ButtonVariant.LUMO_SUCCESS);
		btnDownLoad.getStyle().setCursor("pointer");
		btnDownLoad.setEnabled(false);

		txtOrg.setWidthFull();
		txtOrg.setReadOnly(true);

		cbActive.setValue(true);
		cbActive.getStyle().set("margin-top", "7px");

		cbUsed.setValue(true);
		cbUsed.getStyle().set("margin-top", "7px");

		hLayout.setWidthFull();
		hLayout.add(cbUsed,cbActive,btnCreate,btnDownLoad);
		hLayout.expand(txtOrg);
		hLayout.setPadding(false);

		return hLayout;
	}

	private ApiFilterReportOrgSystemModel getSearch() {
		ApiFilterReportOrgSystemModel apiFilterReportOrgSystemModel = new ApiFilterReportOrgSystemModel();
		apiFilterReportOrgSystemModel.setSkip(0);
		apiFilterReportOrgSystemModel.setLimit(0);
		if(cbActive.getValue() == true)
			apiFilterReportOrgSystemModel.setActive(String.valueOf(cbActive.getValue()));
		
		if(cbUsed.getValue() == true)
			apiFilterReportOrgSystemModel.setUsed(String.valueOf(cbUsed.getValue()));
		

		return apiFilterReportOrgSystemModel;
	}
	


}
