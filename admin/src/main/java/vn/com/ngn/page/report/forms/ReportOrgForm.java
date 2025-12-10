package vn.com.ngn.page.report.forms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.report.ApiOrganizationForReportModel;
import vn.com.ngn.api.report.ApiOrganizationReportFilterModel;
import vn.com.ngn.api.report.ApiReportService;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.report.excels.ExportExcelOrg;
import vn.com.ngn.utils.components.ButtonDownLoadTemplate;
import vn.com.ngn.utils.components.DialogTemplate;
import vn.com.ngn.utils.components.VerticalLayoutTemplate;

public class ReportOrgForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private ChooseOrganizationForm chooseOrganizationForm;

	private Button btnChooseOrg = new Button("Chọn đơn vị lập báo cáo",FontAwesome.Solid.FOLDER_TREE.create());
	private Button btnCreate = new Button("Lập báo cáo",FontAwesome.Solid.FILE_ALT.create());

	private ButtonDownLoadTemplate btn = new ButtonDownLoadTemplate("Tải về",FontAwesome.Solid.DOWNLOAD.create());

	private TextField txtOrg = new TextField("Tên đơn vị báo cáo");
	
	
	private TreeGrid<ApiOrganizationForReportModel> treeGrid = new TreeGrid<ApiOrganizationForReportModel>();
	private List<ApiOrganizationForReportModel> listModel = new ArrayList<ApiOrganizationForReportModel>();

	private Checkbox cbChooseAllOrg = new Checkbox("Bao gồm các cơ quan và đơn vị trực thuộc (Không chọn thì hệ thống sẽ lấy mặc định một cấp từ đơn vị hiện tại");
	private ComboBox<Pair<String, String>> cmbStatus = new ComboBox<Pair<String,String>>("Trạng thái");
	private List<Pair<String, String>> listDataCmbStatus = new ArrayList<Pair<String,String>>();
	
	private String orgId = null;
	
	public ReportOrgForm() {
		buildLayout();
		configComponent();
		initDataCmbStatus();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(createToolbar(),treeGrid);
	}

	@Override
	public void configComponent() {
		btnChooseOrg.addClickListener(e->openDialogChooseOrg());
		btnCreate.addClickListener(e->{
			loadData();
		});
	}
	
	
	private void loadData() {
		listModel = new ArrayList<ApiOrganizationForReportModel>();
		ApiOrganizationReportFilterModel apiOrganizationReportFilterModel = new ApiOrganizationReportFilterModel();
		apiOrganizationReportFilterModel.setParentId(orgId);
		try {
			ApiResultResponse<List<ApiOrganizationForReportModel>> data = ApiReportService.getListOrganization(apiOrganizationReportFilterModel);
			if(data.isSuscces()) {
				
				if(cbChooseAllOrg.getValue() == false) {
					data.getResult().forEach(model->{
						if(model.getSubOrganizations() != null && !model.getSubOrganizations().isEmpty()) {
							model.getSubOrganizations().forEach(sub->{
								sub.setSubOrganizations(Collections.emptyList());
							});
						}
						listModel.add(model);
					});
				}else {
					listModel.addAll(data.getResult());
				}
			}
		} catch (Exception e) {
		}
		
		treeGrid.removeAllColumns();
		createTreeGrid();
		
		
		ExportExcelOrg createExcel = new ExportExcelOrg();
		createExcel.setListData(listModel);
		btn.getButton().setEnabled(true);
		btn.getButton().addClickListener(e->{
			try {
				btn.download(createExcel.createReport());
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		});
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
	
	private void createTreeGrid() {
		treeGrid.setItems(listModel, ApiOrganizationForReportModel::getSubOrganizations);
		treeGrid.addHierarchyColumn(ApiOrganizationForReportModel::getName).setHeader("Tên");
		treeGrid.addColumn(ApiOrganizationForReportModel::getDescription).setHeader("Mô tả");
		treeGrid.addColumn(model->{
			return model.getUnitCodeText();
		}).setHeader("Mã đơn vị");
		treeGrid.addColumn(ApiOrganizationForReportModel::getCountSubOrganization).setHeader("Đơn vị trực thuộc");
		treeGrid.addComponentColumn(model->{
			return new Span("Người dùng ("+model.getCountUser()+")");
		}).setHeader("Người dùng");
	}
	
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

		hLayout.setWidthFull();
		hLayout.add(btnChooseOrg,txtOrg,cmbStatus);
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

}
