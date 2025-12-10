package vn.com.ngn.page.setting.forms;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.theme.lumo.LumoUtility;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.log_request.ApiLogRequestModel;
import vn.com.ngn.api.log_request.ApiLogRequestService;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.setting.model.LogRequestModel;
import vn.com.ngn.utils.components.PaginationForm;

public class ListLogRequestForm extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private Grid<LogRequestModel> grid = new Grid<LogRequestModel>(LogRequestModel.class,false);
	private List<LogRequestModel> listModel = new ArrayList<LogRequestModel>();
	
	private SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	
	private TextField txtSearch = new TextField("Tìm kiếm");
	private Button btnSearch = new Button(FontAwesome.Solid.SEARCH.create());
	private DatePicker fromDate = new DatePicker("Từ ngày");
	private DatePicker toDate = new DatePicker("Đến ngày");
	
	private PaginationForm paginationForm;
	
	public ListLogRequestForm() {
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		
		paginationForm = new PaginationForm(()->{
			if(paginationForm!=null) {
				loadData();
			}
		});
		
		this.add(createToolbar(),paginationForm,createGrid());
		
	}

	@Override
	public void configComponent() {
		btnSearch.addClickListener(e->{
			loadData();
		});
		btnSearch.addClickShortcut(Key.ENTER);
		
		txtSearch.addValueChangeListener(e->{
			loadData();
		});
		
		
		
	}
	
	public void loadData() {
		listModel.clear();
		LocalDate localDateStartDay = fromDate.getValue();
		LocalDateTime localDateTimeStartDay = localDateStartDay.atTime(00,00);
		
		LocalDate localDateEndDay = toDate.getValue();
		LocalDateTime localDateTimeEndDay = localDateEndDay.atTime(23,59);
		try {
			ApiResultResponse<List<ApiLogRequestModel>> data = ApiLogRequestService.getListLogRequest(paginationForm.getSkip(), paginationForm.getLimit(),
					localDateTimeStartDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
					localDateTimeEndDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),txtSearch.getValue());
			
			paginationForm.setItemCount(data.getTotal());
			
			listModel = data.getResult().stream().map(LogRequestModel::new).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		grid.setItems(listModel);
		
	}
	
	private Component createGrid() {
		grid = new Grid<LogRequestModel>(LogRequestModel.class,false);
		
		grid.addColumn(createStatusComponentRenderer()).setHeader("Phương pháp").setWidth("100px").setFlexGrow(0);
		grid.addComponentColumn(model->{
			return new Span(df.format(model.getCreatedTime()));
		}).setHeader("Thời gian").setWidth("190px").setFlexGrow(0);
		grid.addColumn(LogRequestModel::getProtocol).setHeader("Protocol").setWidth("100px").setFlexGrow(0);
		grid.addColumn(LogRequestModel::getQuery).setHeader("Truy vấn").setResizable(true);
		grid.addColumn(LogRequestModel::getAddRemote).setHeader("Địa chỉ IP").setWidth("120px").setFlexGrow(0);
		grid.addColumn(LogRequestModel::getRequestURL).setHeader("Đường dẫn");
		
		grid.setWidthFull();
		grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
		grid.setAllRowsVisible(true);
		grid.addThemeVariants(GridVariant.LUMO_COMPACT);
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
		grid.addThemeVariants(GridVariant.MATERIAL_COLUMN_DIVIDERS);
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);
		
		return grid;
	}
	
	@SuppressWarnings("deprecation")
	private Component createToolbar() {
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		
		fromDate.setLocale(new Locale("vi", "VN"));
		toDate.setLocale(new Locale("vi", "VN"));
		
		LocalDate localEndStart = LocalDate.now();
		toDate.setValue(localEndStart);
		
		LocalDate startDate =  localEndStart.minusMonths(1);
		fromDate.setValue(startDate);

		
		txtSearch.setPlaceholder("Nhập từ khóa để tìm.....");
		
		btnSearch.getStyle().set("margin-top", "30px");
		horizontalLayout.add(txtSearch,fromDate,toDate,btnSearch);
		horizontalLayout.expand(txtSearch);
		horizontalLayout.setWidthFull();
		
		return horizontalLayout;
	}
	
	
	private static final SerializableBiConsumer<Span, LogRequestModel> statusComponent = (span,logRequest)->{
		
		if(logRequest.getMethod().equals("GET")) {
			span.getElement().getThemeList().add("badge success");
		}else if(logRequest.getMethod().equals("POST")) {
			span.getElement().getThemeList().add("badge contrast");
		}else if(logRequest.getMethod().equals("PUT")) {
			span.getElement().getThemeList().add("badge");
		}else if(logRequest.getMethod().equals("DELETE")) {
			span.getElement().getThemeList().add("badge error");
		}
		
		
		span.setText(logRequest.getMethod());
	};
	
	private static ComponentRenderer<Span, LogRequestModel> createStatusComponentRenderer(){
		return new ComponentRenderer<Span, LogRequestModel>(Span::new, statusComponent);
	}


}



















