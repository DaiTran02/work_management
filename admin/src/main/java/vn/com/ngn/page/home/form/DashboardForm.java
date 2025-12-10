package vn.com.ngn.page.home.form;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.theme.lumo.LumoUtility;

import vn.com.ngn.api.dashboard.ApiDashBoardOrgModel;
import vn.com.ngn.api.dashboard.ApiDashBoardService;
import vn.com.ngn.api.dashboard.ApiDashBoardUserModel;
import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.log_request.ApiLogRequestModel;
import vn.com.ngn.api.log_request.ApiLogRequestService;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.setting.model.LogRequestModel;
import vn.com.ngn.utils.LocalDateUtils;
import vn.com.ngn.utils.components.VerticalLayoutTemplate;

public class DashboardForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private final ApiDashBoardService apiDashBoardService;
	private long idChart = System.currentTimeMillis();
	private HorizontalLayout hLayout = new HorizontalLayout();
	
	public DashboardForm(ApiDashBoardService apiDashBoardService) {
		this.apiDashBoardService = apiDashBoardService;
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setWidthFull();
	}

	@Override
	public void configComponent() {

	}

	private void loadData() {
		
		List<Pair<String, String>> listPairUser = new ArrayList<Pair<String,String>>();
		List<Pair<String, String>> listPaidOrg = new ArrayList<Pair<String,String>>();
		String dataChartUser = "[";
		String dataChartOrg = "[";
		try {
			ApiResultResponse<List<ApiDashBoardUserModel>> getInfoUserDashBoard = apiDashBoardService.listUserDashBoard(null);
			for(ApiDashBoardUserModel model : getInfoUserDashBoard.getResult()) {
				listPairUser.add(Pair.of(model.getTitle(),model.getDisplay()));
				dataChartUser += "{"
						+ "name:'"+model.getTitle()+"',"
						+ "y:"+model.getValue()
						+ "},";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		dataChartUser += "]";
		
		try {
			ApiResultResponse<List<ApiDashBoardOrgModel>> getInfoOrgDashBoard = apiDashBoardService.listOrgDashBoard(null);
			for(ApiDashBoardOrgModel model : getInfoOrgDashBoard.getResult()) {
				listPaidOrg.add(Pair.of(model.getTitle(),model.getDisplay()));
				dataChartOrg += "{"
						+ "name:'"+model.getTitle()+"',"
						+ "y:"+model.getValue()
						+ "},";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		dataChartOrg += "]";
		
		
		System.out.println("data User:" + dataChartUser);
		System.out.println("data Org: "+ dataChartOrg);
		
		this.removeAll();
		this.add(createLayoutInfoUserAndOrg(listPairUser,listPaidOrg));
		this.add(hLayout);
		hLayout.setWidthFull();
		hLayout.add(createLayoutChartUser(dataChartUser, idChart),createLayoutChartOrg(dataChartOrg, idChart));
		this.add(createLayoutGridHistory());
	}
	
	private Component createLayoutInfoUserAndOrg(List<Pair<String, String>> listInfoUser,List<Pair<String, String>> listInfoOrg) {
		HorizontalLayout hLayout = new HorizontalLayout();
		
		VerticalLayout vLayoutUser = new VerticalLayout();
		vLayoutUser.setWidth("50%");
		vLayoutUser.getStyle().setBoxShadow("rgba(0, 0, 0, 0.16) 0px 1px 4px").set("padding", "20px");
		
		H5 headerUser = new H5("Tổng quan về người dùng");
		headerUser.getStyle().setMargin("auto");
		vLayoutUser.add(headerUser);
		
		for(Pair<String, String> model : listInfoUser) {
			vLayoutUser.add(createKeyValue(model.getKey()+":", model.getValue()+" tài khoản","315px"));
		}
		
		VerticalLayout vLayoutOrg = new VerticalLayout();
		vLayoutOrg.setWidth("50%");
		
		
		H5 headerOrg = new H5("Tổng quan về đơn vị");
		headerOrg.getStyle().setMargin("auto");
		vLayoutOrg.add(headerOrg);
		vLayoutOrg.getStyle().setBoxShadow("rgba(0, 0, 0, 0.16) 0px 1px 4px");
		
		for(Pair<String, String> model : listInfoOrg) {
			vLayoutOrg.add(createKeyValue(model.getKey()+":", model.getValue(), "205px"));
		}
		
		vLayoutOrg.getStyle().setPadding("20px");
		
		hLayout.add(vLayoutUser,vLayoutOrg);
		hLayout.setWidthFull();
		
		
		return hLayout;
	}

	private Component createLayoutChartUser(String dataUser,long idChart) {
		
		Div div = new Div();
		String idChartUser =  "container"+idChart;
		Html html = new Html("<script>"
				+ "Highcharts.chart('"+idChartUser+"', {"
				+ "    chart: {"
				+ "        type: 'pie'"
				+ "    },"
				+ "    title: {"
				+ "        text: 'Biểu đồ người dùng'"
				+ "    },"
				+ "    tooltip: {"
				+ "        valueSuffix: ''"
				+ "    },"
			    + "    credits: {"
			    + "        enabled: false"
			    + "    },"
				+ "    plotOptions: {"
				+ "        series: {"
				+ "            allowPointSelect: true,"
				+ "            cursor: 'pointer',"
				+ "            dataLabels: [{"
				+ "                enabled: true,"
				+ "                distance: 20"
				+ "            }, {"
				+ "                enabled: true,"
				+ "                distance: -40,"
				+ "                format: '{point.percentage:.1f}%',"
				+ "                style: {"
				+ "                    fontSize: '1.2em',"
				+ "                    textOutline: 'none',"
				+ "                    opacity: 0.7"
				+ "                },"
				+ "                filter: {"
				+ "                    operator: '>',"
				+ "                    property: 'Tài khoản',"
				+ "                    value: 10"
				+ "                }"
				+ "            }]"
				+ "        }"
				+ "    },"
				+ "    series: ["
				+ "        {"
				+ "            name: 'Tài khoản',"
				+ "            colorByPoint: true,"
				+ "            data:" +dataUser
				+ "        }"
				+ "    ]"
				+ "});"
				+ ""
				+ "</script>");

		div.add(html);
		div.setId(idChartUser);
		div.setHeight("300px");
		div.setWidth("50%");
		div.getStyle().setBoxShadow("rgba(0, 0, 0, 0.16) 0px 1px 4px");
		
		
	

		return div;
	}
	
	private Component createLayoutChartOrg(String dataChartOrg,long idChart) {
		Div divChartOrg = new Div();
		String idChartOrg = "container_org"+idChart;
		Html htmlChartOrg = new Html("<script>"
		        + "// Build the chart\r\n"
		        + "Highcharts.chart('" + idChartOrg + "', {\r\n"
		        + "    chart: {\r\n"
		        + "        plotBackgroundColor: null,\r\n"
		        + "        plotBorderWidth: null,\r\n"
		        + "        plotShadow: false,\r\n"
		        + "        type: 'pie'\r\n"
		        + "    },\r\n"
		        + "    title: {\r\n"
		        + "        text: 'Biểu đồ đơn vị'\r\n"
		        + "    },\r\n"
		        + "    tooltip: {\r\n"
		        + "        pointFormat: '{series.name}: <b>{point.percentage:.1f}</b>'\r\n"
		        + "    },\r\n"
		        + "    accessibility: {\r\n"
		        + "        point: {\r\n"
		        + "            valueSuffix: '%'\r\n"
		        + "        }\r\n"
		        + "    },\r\n"
		        + "    plotOptions: {\r\n"
		        + "        pie: {\r\n"
		        + "            allowPointSelect: true,\r\n"
		        + "            cursor: 'pointer',\r\n"
		        + "            dataLabels: {\r\n"
		        + "                enabled: true,\r\n"
		        + "                format: '<span style=\"font-size: 1.2em\"><b>{point.name}</b>' +\r\n"
		        + "                    '</span><br>' +\r\n"
		        + "                    '<span style=\"opacity: 0.6\">{point.percentage:.1f} ' +\r\n"
		        + "                    '%</span>',\r\n"
		        + "                connectorColor: 'rgba(128,128,128,0.5)'\r\n"
		        + "            }\r\n"
		        + "        }\r\n"
		        + "    },\r\n"
		        + "    series: [{\r\n"
		        + "        name: 'Đơn vị',\r\n"
		        + "        data:" + dataChartOrg + "\r\n"
		        + "    }]\r\n"
		        + "});\r\n"
		        + "</script>");

		
		divChartOrg.setHeight("300px");
		divChartOrg.setWidth("50%");
		divChartOrg.getStyle().setBoxShadow("rgba(0, 0, 0, 0.16) 0px 1px 4px");
		divChartOrg.setId(idChartOrg);
		divChartOrg.add(htmlChartOrg);
		
		return divChartOrg;
	}
	
	private Component createLayoutGridHistory() {
		
		VerticalLayout vLayout = new VerticalLayout();
		
		Grid<LogRequestModel> grid = new Grid<LogRequestModel>(LogRequestModel.class,false);
		List<LogRequestModel> listModel = new ArrayList<LogRequestModel>();

		try {
			ApiResultResponse<List<ApiLogRequestModel>> data = ApiLogRequestService.getListLogRequest(0,10,0,0,"");
			listModel = data.getResult().stream().map(LogRequestModel::new).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		grid.setItems(listModel);
		
		grid.addColumn(createStatusComponentRenderer()).setHeader("Phương pháp").setWidth("100px").setFlexGrow(0);
		grid.addComponentColumn(model->{
			return new Span(LocalDateUtils.dfDateTime.format(model.getCreatedTime()));
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
		
		
		H5 header = new H5("Top 10 request mới nhất");
		
		header.getStyle().setMargin("auto");
		
		vLayout.add(header,grid);
		vLayout.setWidthFull();
		vLayout.getStyle().setBoxShadow("rgba(0, 0, 0, 0.16) 0px 1px 4px");
		
		return vLayout;
	}
	
	
	private HorizontalLayout createKeyValue(String name, String value,String width) {
		HorizontalLayout hLayout = new HorizontalLayout();
		Span spName = new Span(name);
		spName.getStyle().set("min-width", "200px").setWidth(width);
		
		Span spValue = new Span(value);
		spValue.getStyle().set("font-weight", "600");
		
		hLayout.add(spName,spValue);
		hLayout.setWidthFull();
		hLayout.getStyle().set("border-bottom", "1px solid #d7d7d7");
		
		return hLayout;
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
