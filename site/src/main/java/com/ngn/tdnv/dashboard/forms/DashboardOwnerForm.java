package com.ngn.tdnv.dashboard.forms;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.vaadin.addons.yuri0x7c1.bslayout.BsColumn;
import org.vaadin.addons.yuri0x7c1.bslayout.BsColumn.Size;
import org.vaadin.addons.yuri0x7c1.bslayout.BsLayout;
import org.vaadin.addons.yuri0x7c1.bslayout.BsRow;

import com.ngn.api.report.ApiFilterReportTaskKpiModel;
import com.ngn.api.report.ApiReportKpiModel;
import com.ngn.api.report.ApiReportService;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tasks.ApiFilterAchivementModel;
import com.ngn.api.tasks.ApiFilterTaskModel;
import com.ngn.api.tasks.ApiNameAndValueModel;
import com.ngn.api.tasks.ApiTaskService;
import com.ngn.api.tasks.ApiTaskSummaryModel;
import com.ngn.enums.DataOfEnum;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.models.sign_in_org.SignInOrgModel;
import com.ngn.tdnv.task.enums.StatusTaskEnum;
import com.ngn.tdnv.task.forms.TaskOverviewSummaryFrom;
import com.ngn.utils.CountMenuUtil;
import com.ngn.utils.LocalDateUtil;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style.Display;
import com.vaadin.flow.dom.Style.FlexDirection;

public class DashboardOwnerForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private boolean isUser = SessionUtil.checkDataOf().getKey().equals(DataOfEnum.TOCANHAN.getKey());
	private UserAuthenticationModel user = SessionUtil.getUser();
	private boolean isMobileLayout = false;
	private StringBuilder stringNavigate = new StringBuilder();
	private VerticalLayout vLayout = new VerticalLayout();
	private ComboBox<Pair<String, String>> cmbTypeOfChart = new ComboBox<Pair<String, String>>();

	private BelongOrganizationModel belongOrganization = SessionUtil.getOrg();
	private HorizontalLayout hLayoutChartAndAchiment = new HorizontalLayout();
	private SignInOrgModel signInOrgModel = SessionUtil.getDetailOrg();
	private List<Pair<String, Integer>> listDataOfChart = new ArrayList<Pair<String,Integer>>();

	private int totalChartKpi = 0;
	private CountMenuUtil countMenuUtil = new CountMenuUtil(belongOrganization, signInOrgModel);
	private TaskOverviewSummaryFrom taskOverviewSummaryFrom = new TaskOverviewSummaryFrom();

	private BsLayout bsLayoutCharSummary = new BsLayout();

	public DashboardOwnerForm() {
		checkMobileLayout();
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();

		this.getStyle().set("animation", "fadeIn 0.5s ease-out forwards");

		this.add(vLayout);

		loadCmbTypeOfChart();
		vLayout.setSizeFull();
		createLayout();

	}

	@Override
	public void configComponent() {
		cmbTypeOfChart.addValueChangeListener(e->{
			createLayoutEvent();
			loadLayoutStatusAndAchiment();
		});
	}

	public void loadData() {
	}

	private void checkMobileLayout() {
		UI.getCurrent().getPage().retrieveExtendedClientDetails(e->{
			if(e.getScreenWidth() < 768) {
				isMobileLayout = true;
			}
		});
	}

	public void createLayout() {
		vLayout.removeAll();
		H1 header = new H1("THỐNG KÊ NHIỆM VỤ ĐÃ GIAO");
		header.getStyle()
		.set("animation", "fadeInUp 0.5s ease-out forwards")
		.set("animation-delay", "0.1s")
		.set("opacity", "0");

		HorizontalLayout hLayoutTitle = new HorizontalLayout();

		HorizontalLayout hLayoutCount = new HorizontalLayout();

		H1 countTask = new H1(countMenuUtil.countTaskOwner()+" ");


		H3 titleCountTask = new H3("Nhiệm vụ");
		hLayoutCount.add(countTask,titleCountTask);
		hLayoutCount.setWidth("300px");

		Span spanTime = new Span("Từ ngày "+LocalDateUtil.dfDate.format(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getStartOfTheYear(SessionUtil.getYear())))
		+" -> Đến ngày "+LocalDateUtil.dfDate.format(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getEndtOfTheYear(SessionUtil.getYear()))));
		spanTime.getStyle().setMarginLeft("auto");
		spanTime.getStyle()
		.set("animation", "fadeInUp 0.5s ease-out forwards")
		.set("animation-delay", "0.3s")
		.set("opacity", "0");

		VerticalLayout vLayoutTimeAndTypeChart = new VerticalLayout();
		vLayoutTimeAndTypeChart.add(cmbTypeOfChart,spanTime);
		vLayoutTimeAndTypeChart.getStyle().setMarginLeft("auto");

		cmbTypeOfChart.getStyle().setMarginLeft("auto");
		cmbTypeOfChart.setWidth("265px");

		//These className in file dashboard.css
		countTask.addClassName("count-task-h1");

		hLayoutCount.addClassName("layout-count-task");
		hLayoutCount.getStyle()
		.set("animation", "fadeInUp 0.5s ease-out forwards")
		.set("animation-delay", "0.2s")
		.set("opacity", "0");


		hLayoutTitle.setWidthFull();
		hLayoutTitle.add(hLayoutCount,vLayoutTimeAndTypeChart);

		loadLayoutStatusAndAchiment();
		hLayoutChartAndAchiment.setWidthFull();

		hLayoutChartAndAchiment.getStyle().set("gap", "25px");
		if(isMobileLayout) {
			hLayoutChartAndAchiment.getStyle().setDisplay(Display.FLEX).setFlexDirection(FlexDirection.COLUMN).set("gap", "0");
		}

		vLayout.add(header,hLayoutTitle,taskOverviewSummaryFrom,hLayoutChartAndAchiment,bsLayoutCharSummary);
		createLayoutEvent();

	}

	private void loadLayoutStatusAndAchiment() {
		loadDataStatus();
		hLayoutChartAndAchiment.removeAll();
		String id = System.currentTimeMillis()+Math.random() * 49 +1+"";
		String idAchiment = System.currentTimeMillis()+Math.random() * 49 +1+"";

		List<ApiNameAndValueModel> listAchiment = getDataAchiment();
		String dataAchivement = "";
		String dataCircleAchiment = "";
		for(ApiNameAndValueModel model : listAchiment) {
			dataAchivement += "{"
					+ "name:'"+model.getName()+"',"
					+ "data:["+model.getValue()+"]"
					+ "},";

			dataCircleAchiment += "{"
					+ "name:'"+model.getName()+"',"
					+ "y:"+model.getValue()+","
					+ "z:"+model.getValue()+","
					+ "},";

		}


		if(cmbTypeOfChart.getValue().getKey().equals("collumn")) {
			hLayoutChartAndAchiment.add(createChartKpiLayout(id,""),createLayoutCollumnChartAchivement(idAchiment, "Thành tích", dataAchivement));
		}else {
			hLayoutChartAndAchiment.add(createChartCircleKpiLayout(id,totalChartKpi),createLayoutCircleChartAchivement(idAchiment, "Thành tích", dataCircleAchiment));
		}
	}

	private void loadCmbTypeOfChart() {
		List<Pair<String, String>> listTypeOfChart = new ArrayList<Pair<String,String>>();
		listTypeOfChart.add(Pair.of("collumn","Biểu đồ cột"));
		listTypeOfChart.add(Pair.of("circle","Biểu đồ tròn"));

		cmbTypeOfChart.setItems(listTypeOfChart);
		cmbTypeOfChart.setItemLabelGenerator(Pair::getValue);
		cmbTypeOfChart.setValue(listTypeOfChart.get(0));

	}

	private void createLayoutEvent() {

		try {
			ApiResultResponse<List<ApiTaskSummaryModel>> dataSummary = ApiTaskService.getSummaryTasksOwner(getSearch());
			if(dataSummary.isSuccess()) {
				taskOverviewSummaryFrom.loadData(dataSummary.getResult());
				loadChartSummary(dataSummary.getResult());

				// To navigate
				stringNavigate = new StringBuilder();
				stringNavigate.append("task_ower?status=");
				buildEventButton();


			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void buildEventButton() {
		for(ButtonTemplate button : taskOverviewSummaryFrom.getListButtons()) {

			button.addClassName("summary-button");
			button.getStyle()
			.set("transition", "all 0.3s ease")
			.set("transform-origin", "center");

			// Thêm hiệu ứng khi hover
			button.getElement().addEventListener("mouseenter", e -> {
				button.getStyle()
				.set("transform", "scale(1.05)")
				.set("box-shadow", "0 5px 15px rgba(0,0,0,0.1)");
			});

			button.getElement().addEventListener("mouseleave", e -> {
				button.getStyle()
				.set("transform", "scale(1)")
				.set("box-shadow", "none");
			});
			
			
			button.addClickListener(e->{
				stringNavigate.append(StatusTaskEnum.toGetKey(button.getId().get()));
				toNavigateTask();
			});

		}
	}

	private void toNavigateTask() {
		if(!stringNavigate.isEmpty()) {
			UI.getCurrent().getPage().setLocation(stringNavigate.toString());
		}
	}

	// Data status
	private void loadDataStatus() {
		listDataOfChart = new ArrayList<Pair<String,Integer>>();
		ApiResultResponse<List<ApiTaskSummaryModel>> dataSummary = ApiTaskService.getSummaryTasksOwner(getSearch());
		dataSummary.getResult().stream().forEach(model->{
			listDataOfChart.add(Pair.of(model.getCount()+" "+model.getName(),model.getCount()));
		});
	}

//	private Component createLayoutChartCollumnStatus(String id) {
//		Div div = new Div();
//		Html htmlChart = new Html("<script>"
//				+ "Highcharts.chart('"+id+"', { "
//				+ "        chart: { "
//				+ "            animation: { "
//				+ "                duration: 500 "
//				+ "            }, "
//				+ "            marginRight: 50 "
//				+ "        }, "
//				+ "        title: { "
//				+ "            text: 'ĐỒ THỊ BIỂU DIỄN', "
//				+ "            align: 'center' "
//				+ "        }, "
//				+ "    credits: {"
//				+ "        enabled: false"
//				+ "    },"
//				+ "        subtitle: { "
//				+ "            useHTML: true, "
//				+ "            text: '', "
//				+ "            floating: true, "
//				+ "            align: 'right', "
//				+ "            verticalAlign: 'middle', "
//				+ "            y: -80, "
//				+ "            x: -100 "
//				+ "        }, "
//				+ " "
//				+ "        legend: { "
//				+ "            enabled: false "
//				+ "        }, "
//				+ "        xAxis: { "
//				+ "            type: 'category' "
//				+ "        }, "
//				+ "        yAxis: { "
//				+ "            opposite: true, "
//				+ "            tickPixelInterval: 150, "
//				+ "            title: { "
//				+ "                text: null "
//				+ "            } "
//				+ "        }, "
//				+ "        plotOptions: { "
//				+ "            series: { "
//				+ "                animation: false, "
//				+ "                groupPadding: 0, "
//				+ "                pointPadding: 0.1, "
//				+ "                borderWidth: 0, "
//				+ "                colorByPoint: true, "
//				+ "                dataSorting: { "
//				+ "                    enabled: true, "
//				+ "                    matchByName: true "
//				+ "                }, "
//				+ "                type: 'bar', "
//				+ "                dataLabels: { "
//				+ "                    enabled: true "
//				+ "                } "
//				+ "            } "
//				+ "        }, "
//				+ "        series: [ "
//				+ "            { "
//				+ "                type: 'bar', "
//				+ "                name: 2024, "
//				+ "                data: ["+convertListPairDataToString(listDataOfChart)+"] "
//				+ "            } "
//				+ "        ], "
//				+ "        responsive: { "
//				+ "            rules: [{ "
//				+ "                condition: { "
//				+ "                    maxWidth: 550 "
//				+ "                }, "
//				+ "                chartOptions: { "
//				+ "                    xAxis: { "
//				+ "                        visible: false "
//				+ "                    }, "
//				+ "                    subtitle: { "
//				+ "                        x: 0 "
//				+ "                    }, "
//				+ "                    plotOptions: { "
//				+ "                        series: { "
//				+ "                            dataLabels: [{ "
//				+ "                                enabled: true, "
//				+ "                                y: 8 "
//				+ "                            }, { "
//				+ "                                enabled: true, "
//				+ "                                format: '{point.name}', "
//				+ "                                y: -8, "
//				+ "                                style: { "
//				+ "                                    fontWeight: 'normal', "
//				+ "                                    opacity: 0.7 "
//				+ "                                } "
//				+ "                            }] "
//				+ "                        } "
//				+ "                    } "
//				+ "                } "
//				+ "            }] "
//				+ "        } "
//				+ "    });"
//
//				+ "</script>");
//
//		div.setId(id);
//		div.add(htmlChart);
//
//		div.addClassName("dashboard--boxshadow");
//
//		div.setWidth("69%");
//		if(isMobileLayout) {
//			div.setWidthFull();
//		}
//		div.setHeight("200px");
//		div.getStyle().setMargin("15px 0");
//
//		return div;
//	}

//	private Component createLayoutCircleChartStatus(String id) {
//		Div divChart = new Div();
//
//		divChart.setId(id);
//
//		Html html = new Html("<script>"
//				+ "Highcharts.chart('"+id+"', {"
//				+ "    chart: {"
//				+ "        type: 'pie',"
//				+ "    credits: {"
//				+ "        enabled: false"
//				+ "    },"
//				+ "        custom: {},"
//				+ "    credits: {"
//				+ "        enabled: false"
//				+ "    },"
//				+ "        events: {"
//				+ "            render() {"
//				+ "                const chart = this,"
//				+ "                    series = chart.series[0];"
//				+ "                let customLabel = chart.options.chart.custom.label;"
//				+ "                if (!customLabel) {"
//				+ "                    customLabel = chart.options.chart.custom.label ="
//				+ "                        chart.renderer.label("
//				+ "                            'Nhiệm vụ<br/>' +"
//				+ "                            '<strong>"+countMenuUtil.countTaskOwner()+"</strong>'"
//				+ "                        )"
//				+ "                            .css({"
//				+ "                                color: '#000',"
//				+ "                                textAnchor: 'middle'"
//				+ "                            })"
//				+ "                            .add();"
//				+ "                }"
//				+ "                const x = series.center[0] + chart.plotLeft,"
//				+ "                    y = series.center[1] + chart.plotTop -"
//				+ "                    (customLabel.attr('height') / 2);"
//				+ "                customLabel.attr({"
//				+ "                    x,"
//				+ "                    y"
//				+ "                });"
//				+ "                customLabel.css({"
//				+ "                    fontSize: `${series.center[2] / 12}px`"
//				+ "                });"
//				+ "            }"
//				+ "        }"
//				+ "    },"
//				+ "    accessibility: {"
//				+ "        point: {"
//				+ "            valueSuffix: '%'"
//				+ "        }"
//				+ "    },"
//				+ "    title: {"
//				+ "        text: 'Biểu đồ'"
//				+ "    },"
//				+ "    subtitle: {},"
//				+ "    tooltip: {"
//				+ "        pointFormat: '{series.name}: <b>{point.percentage:.0f}%</b>'"
//				+ "    },"
//				+ "    legend: {"
//				+ "        enabled: false"
//				+ "    },"
//				+ "    plotOptions: {"
//				+ "        series: {"
//				+ "            allowPointSelect: true,"
//				+ "            cursor: 'pointer',"
//				+ "            borderRadius: 8,"
//				+ "            dataLabels: [{"
//				+ "                enabled: true,"
//				+ "                distance: 20,"
//				+ "                format: '{point.name}'"
//				+ "            }, {"
//				+ "                enabled: true,"
//				+ "                distance: -15,"
//				+ "                format: '{point.percentage:.0f}%',"
//				+ "                style: {"
//				+ "                    fontSize: '0.9em'"
//				+ "                }"
//				+ "            }],"
//				+ "            showInLegend: true"
//				+ "        }"
//				+ "    },"
//				+ "    series: [{"
//				+ "        name: 'Chiếm: ',"
//				+ "        colorByPoint: true,"
//				+ "        innerSize: '75%',"
//				+ "        data: ["+convertListPairDataToString(listDataOfChart)+"]"
//				+ "    }]"
//				+ "});"
//				+ "</script>");
//
//		divChart.add(html);
//		divChart.addClassName("dashboard--boxshadow");
//		divChart.setWidth("69%");
//		divChart.getStyle().setMargin("15px 0");
//
//		return divChart;
//	}


	// Summary
	private int idChart = 0;
	private void loadChartSummary(List<ApiTaskSummaryModel> listData) {
		bsLayoutCharSummary.removeAll();
		bsLayoutCharSummary.setWidthFull();
		bsLayoutCharSummary.getStyle().setPaddingLeft("0").setPaddingRight("0");

		BsRow row = bsLayoutCharSummary.addRow();
		listData.forEach(model->{
			String id = "id"+idChart;

			//Chart dang thuc hien
			if(model.getKey().equals("dangthuchien")) {
				String dataString = "";
				String dataStringCollumn = "";
				for(ApiTaskSummaryModel md : model.getChild()) {
					dataString += "{"
							+ "name:'"+md.getName()+"',"
							+ "y:"+md.getCount()+""
							+ "},";

					dataStringCollumn += "{"
							+ "name:'"+md.getName()+"',"
							+"data:["+md.getCount()+"]"
							+ "},";
				}
				BsColumn bsColumn = cmbTypeOfChart.getValue().getKey().equals("collumn") ? row.addColumn(new BsColumn(createLayoutChartCollumnSummary(id, "Đang thực hiện",dataStringCollumn))) 
						: row.addColumn(new BsColumn(createCircleChartSummary(id,"Đang thực hiện",dataString)));
				bsColumn.addSize(Size.MD,3);
			}

			//Chart cho xac nhan
			if(model.getKey().equals("choxacnhan")) {
				String dataString = "";
				String dataStringCollumn = "";
				for(ApiTaskSummaryModel md : model.getChild()) {
					dataString += "{"
							+ "name:'"+md.getName()+"',"
							+ "y:"+md.getCount()+""
							+ "},";

					dataStringCollumn += "{"
							+ "name:'"+md.getName()+"',"
							+"data:["+md.getCount()+"]"
							+ "},";
				}
				BsColumn bsColumn = cmbTypeOfChart.getValue().getKey().equals("collumn") ? row.addColumn(new BsColumn(createLayoutChartCollumnSummary(id, "Chờ xác nhận", dataStringCollumn)))
						: row.addColumn(new BsColumn(createCircleChartSummary(id,"Chờ xác nhận",dataString)));
				bsColumn.addSize(Size.MD,3);
			}

			//Chart da hoan thanh
			if(model.getKey().equals("dahoanthanh")) {
				String dataString = "";
				String dataStringCollumn = "";
				for(ApiTaskSummaryModel md : model.getChild()) {
					dataString += "{"
							+ "name:'"+md.getName()+"',"
							+ "y:"+md.getCount()+""
							+ "},";

					dataStringCollumn += "{"
							+ "name:'"+md.getName()+"',"
							+"data:["+md.getCount()+"]"
							+ "},";
				}
				BsColumn bsColumn = cmbTypeOfChart.getValue().getKey().equals("collumn") ? row.addColumn(new BsColumn(createLayoutChartCollumnSummary(id, "Đã hoàn thành", dataStringCollumn)))
						: row.addColumn(new BsColumn(createCircleChartSummary(id,"Đã hoàn thành",dataString)));
				bsColumn.addSize(Size.MD,3);
			}

			//Chart khac
			if(model.getKey().equals("khac")) {
				String dataString = "";
				String dataStringCollumn = "";
				for(ApiTaskSummaryModel md : model.getChild()) {
					dataString += "{"
							+ "name:'"+md.getName()+"',"
							+ "y:"+md.getCount()+""
							+ "},";

					dataStringCollumn += "{"
							+ "name:'"+md.getName()+"',"
							+"data:["+md.getCount()+"]"
							+ "},";
				}

				BsColumn bsColumn = cmbTypeOfChart.getValue().getKey().equals("collumn") ? row.addColumn(new BsColumn(createLayoutChartCollumnSummary(id, "Khác", dataStringCollumn)))
						: row.addColumn(new BsColumn(createCircleChartSummary(id,"Khác",dataString)));
				bsColumn.addSize(Size.MD,3);
			}

			idChart++;
		});
	}


	private Div createCircleChartSummary(String id,String nameChart,String data) {
		Div divChart = new Div();
		divChart.setId(id);
		Html html = new Html("<script>"
				+ "Highcharts.chart('"+id
				+"', { "
				+ "    chart: { "
				+ "        type: 'pie' "
				+ "    }, "
				+ "    title: { "
				+ "        text: '"+nameChart+"' "
				+ "    }, "
				+ "    credits: {"
				+ "        enabled: false"
				+ "    },"
				+ "    tooltip: { "
				+ "        valueSuffix: '%' "
				+ "    }, "
				+ "    plotOptions: { "
				+ "        series: { "
				+ "            allowPointSelect: true, "
				+ "            cursor: 'pointer', "
				+ "            dataLabels: [{ "
				+ "                enabled: true, "
				+ "                distance: 20 "
				+ "            }, { "
				+ "                enabled: true, "
				+ "                distance: -40, "
				+ "                format: '{point.percentage:.1f}%', "
				+ "                style: { "
				+ "                    fontSize: '12px', "
				+ "                    textOutline: 'none', "
				+ "                    opacity: 0.7 "
				+ "                }, "
				+ "                filter: { "
				+ "                    operator: '>', "
				+ "                    property: 'percentage', "
				+ "                    value: 10 "
				+ "                } "
				+ "            }] "
				+ "        } "
				+ "    }, "
				+ "    series: [ "
				+ "        { "
				+ "            name: 'Số lượng ', "
				+ "            colorByPoint: true, "
				+ "            data: [ "+data+""
				+ "            ]"
				+ "        } "
				+ "    ]"
				+ "}); "
				+ ""
				+ "</script>");


		divChart.add(html);
		divChart.getStyle().setBoxShadow("rgba(99, 99, 99, 0.2) 0px 2px 8px 0px");
		return divChart;
	}

	private Div createLayoutChartCollumnSummary(String id,String name,String data) {
		Div divChart = new Div();
		divChart.setId(id);
		Html html = new Html("<script>"
				+ "Highcharts.chart('"+id+"', { "
				+ "    chart: { "
				+ "        type: 'column' "
				+ "    }, "
				+ "    title: { "
				+ "        text: '"+name+"' "
				+ "    }, "
				+ "    subtitle: {}, "
				+ "    credits: {"
				+ "        enabled: false"
				+ "    },"
				+ "    xAxis: { "
				+ "        categories: ['"+name+"'], "
				+ "        crosshair: true, "
				+ "        accessibility: { "
				+ "            description: 'Countries' "
				+ "        } "
				+ "    }, "
				+ "    yAxis: { "
				+ "        min: 0, "
				+ "        title: { "
				+ "            text: 'Nhiệm vụ' "
				+ "        } "
				+ "    }, "
				+ "    tooltip: { "
				+ "        valueSuffix: ' Nhiệm vụ' "
				+ "    }, "
				+ "    plotOptions: { "
				+ "        column: { "
				+ "            pointPadding: 0.2, "
				+ "            borderWidth: 0 "
				+ "        } "
				+ "    }, "
				+ "    series: [ "
				+ 		data
				+ "    ] "
				+ "});"
				+ "</script>");


		divChart.add(html);
		divChart.getStyle().setBoxShadow("rgba(99, 99, 99, 0.2) 0px 2px 8px 0px");
		return divChart;
	}

	// Achivement

	private Component createLayoutCircleChartAchivement(String id,String name,String data) {
		Div div = new Div();
		div.setId(id);

		Html html = new Html("<script>"
				+ "Highcharts.chart('"+id+"', {"
				+ "    chart: {"
				+ "        type: 'variablepie'"
				+ "    },"
				+ "    credits: {"
				+ "        enabled: false"
				+ "    },"
				+ "    title: {"
				+ "        text: 'Thành tích'"
				+ "    },"
				+ "    tooltip: {"
				+ "        headerFormat: '',"
				+ "        pointFormat: '<span style=\"color:{point.color}\">\\u25CF</span> <b> ' +"
				+ "            '{point.name}</b><br/>' +"
				+ "            '"+name+": <b>{point.y}</b><br/>' +"
				+ "            'Tổng nhiệm vụ: <b>"+countMenuUtil.countTaskOwner()+"</b><br/>'"
				+ "    },"
				+ "    series: [{"
				+ "        minPointSize: 10,"
				+ "        innerSize: '20%',"
				+ "        zMin: 0,"
				+ "        name: 'countries',"
				+ "        borderRadius: 5,"
				+ "        data: ["+data+"],"
				+ "        colors: ["
				+ "            '#4caefe',"
				+ "            '#3dc3e8',"
				+ "            '#2dd9db',"
				+ "            '#1feeaf',"
				+ "            '#0ff3a0',"
				+ "            '#00e887',"
				+ "            '#23e274'"
				+ "        ]"
				+ "    }]"
				+ "});"
				+ ""
				+ "</script>");

		div.add(html);
		div.setWidth("30%");
		div.getStyle().setMargin("15px 0");
		div.addClassName("dashboard--boxshadow");

		return div;
	}

	private Component createLayoutCollumnChartAchivement(String id,String name,String data) {
		Div div = new Div();

		div.setId(id);

		Html html = new Html("<script>"
				+ "Highcharts.chart('"+id+"', { "
				+ "    chart: { "
				+ "        type: 'column' "
				+ "    }, "
				+ "    title: { "
				+ "        text: '"+name+"' "
				+ "    }, "
				+ "    subtitle: {}, "
				+ "    credits: {"
				+ "        enabled: false"
				+ "    },"
				+ "    xAxis: { "
				+ "        categories: ['"+name+"'], "
				+ "        crosshair: true, "
				+ "        accessibility: { "
				+ "            description: 'Countries' "
				+ "        } "
				+ "    }, "
				+ "    yAxis: { "
				+ "        min: 0, "
				+ "        title: { "
				+ "            text: 'Nhiệm vụ' "
				+ "        } "
				+ "    }, "
				+ "    tooltip: { "
				+ "        valueSuffix: ' Nhiệm vụ' "
				+ "    }, "
				+ "    plotOptions: { "
				+ "        column: { "
				+ "            pointPadding: 0.2, "
				+ "            borderWidth: 0 "
				+ "        } "
				+ "    }, "
				+ "    series: [ "
				+ 		data
				+ "    ] "
				+ "});"
				+ "</script>");

		div.add(html);

		div.addClassName("dashboard--boxshadow");
		div.setWidth("30%");
		div.getStyle().setMargin("15px 0");
		div.setHeight("200px");

		return div;
	}

	private List<ApiNameAndValueModel> getDataAchiment(){
		List<ApiNameAndValueModel> listData = new ArrayList<ApiNameAndValueModel>();
		ApiFilterAchivementModel apiFilterAchivementModel = new ApiFilterAchivementModel();
		apiFilterAchivementModel.setSkip(0);
		apiFilterAchivementModel.setLimit(0);
		apiFilterAchivementModel.setFromDate(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getStartOfTheYear(SessionUtil.getYear())));
		apiFilterAchivementModel.setToDate(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getEndtOfTheYear(SessionUtil.getYear())));
		apiFilterAchivementModel.setOwnerOrganizationId(belongOrganization.getOrganizationId());
		if(isUser) {
			apiFilterAchivementModel.setOwnerOrganizationId(null);
			apiFilterAchivementModel.setOwnerOrganizationUserId(user.getId());
		}

		ApiResultResponse<List<ApiNameAndValueModel>> listAchivement = ApiTaskService.getListAchivementTaskOwner(apiFilterAchivementModel);
		if(listAchivement.isSuccess()) {
			listData.addAll(listAchivement.getResult());
		}
		return listData;
	}

//	private String convertListPairDataToString(List<Pair<String, Integer>> listData) {
//		String string = "";
//		for(Pair<String, Integer> p : listData) {
//			string += "['"+p.getKey()+"',"+p.getValue() +"],";
//		}
//		return string;
//	}

	private ApiFilterTaskModel getSearch() {
		ApiFilterTaskModel apiFilterTaskModel = new ApiFilterTaskModel();
		apiFilterTaskModel.setFromDate(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getStartOfTheYear(SessionUtil.getYear())));
		apiFilterTaskModel.setToDate(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getEndtOfTheYear(SessionUtil.getYear())));
		apiFilterTaskModel.setLimit(0);
		apiFilterTaskModel.setSkip(0);
		apiFilterTaskModel.setOwnerOrganizationId(belongOrganization.getOrganizationId());
		if(isUser) {
			apiFilterTaskModel.setOwnerOrganizationId(null);
			apiFilterTaskModel.setOwnerOrganizationUserId(user.getId());
		}

		return apiFilterTaskModel;
	}
	
	private ApiFilterReportTaskKpiModel getSearchKpi() {
		ApiFilterReportTaskKpiModel apiFilterTaskModel = new ApiFilterReportTaskKpiModel();
		apiFilterTaskModel.setFromDate(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getStartOfTheYear(SessionUtil.getYear())));
		apiFilterTaskModel.setToDate(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getEndtOfTheYear(SessionUtil.getYear())));
		apiFilterTaskModel.setLimit(0);
		apiFilterTaskModel.setSkip(0);
		apiFilterTaskModel.setOwnerOrganizationId(belongOrganization.getOrganizationId());
		if(isUser) {
			apiFilterTaskModel.setOwnerOrganizationId(null);
			apiFilterTaskModel.setOwnerOrganizationUserId(user.getId());
		}
		return apiFilterTaskModel;
	}
	
	private String loadDataKpi() {
		String result = "[";
		ApiResultResponse<ApiReportKpiModel> dataChartKpi = ApiReportService.getReportKpi(getSearchKpi());
		
		if(dataChartKpi.isSuccess()) {
			ApiReportKpiModel data = dataChartKpi.getResult();
			totalChartKpi = data.getListTasks().size();
			
			if(cmbTypeOfChart.getValue().getKey().equals("collumn")) {
				result += "{"
						+ "name:'Tổng',"
						+ "y:"+data.getListTasks().size()
					+ "},";
			}
			
			result += "{"
						+ "name:'Đã hoàn thành',"
						+ "y:"+data.getTaskCompleted()
					+ "},"
					+ "{"
						+ "name:'Trong hạn',"
						+ "y:"+data.getTaskCompletedButThroughExpired()
					+ "},"
					+ "{"
						+ "name:'Quá hạn',"
						+ "y:"+data.getTaskCompletedButNotThroughExpired()
					+ "},"
					+ "{"
						+ "name:'Chưa hoàn thành',"
						+ "y:"+data.getTaskNotCompleted()
					+ "},"
					+ "{"
					+ "name:'Đánh giá trên 3 sao',"
					+ "y:"+data.getTaskIsRatedHigherThanThreeStars()
					+ "}";
		}
		result += "]";
		
		return result;
	}
	
	private Component createChartKpiLayout(String id,String data) {
		Div div = new Div();
		div.setId(id);
		
		Html html = new Html("<script>"
				+ "Highcharts.chart('"+id+"', { "
				+ "    chart: { "
				+ "        type: 'column' "
				+ "    }, "
				+ "    credits: {"
				+ "        enabled: false"
				+ "    },"
				+ "    title: { "
				+ "        text: 'Nhiệm vụ đã giao được tính KPI' "
				+ "    }, "
				+ "    accessibility: { "
				+ "        announceNewData: { "
				+ "            enabled: true "
				+ "        } "
				+ "    }, "
				+ "    xAxis: { "
				+ "        type: 'category' "
				+ "    }, "
				+ "    yAxis: { "
				+ "        title: { "
				+ "            text: 'Nhiệm vụ' "
				+ "        } "
				+ " "
				+ "    }, "
				+ "    legend: { "
				+ "        enabled: false "
				+ "    }, "
				+ "    plotOptions: { "
				+ "        series: { "
				+ "            borderWidth: 0, "
				+ "            dataLabels: { "
				+ "                enabled: true, "
				+ "                format: '{point.y}' "
				+ "            } "
				+ "        } "
				+ "    }, "
				+ " "
				+ "    tooltip: { "
				+ "        headerFormat: '<span style=\"font-size:11px\">{series.name}</span><br>', "
				+ "        pointFormat: '<span style=\"color:{point.color}\">{point.name}</span>: ' + "
				+ "            '<b>{point.y:.2f}</b> nhiệm vụ<br/>' "
				+ "    }, "
				+ " "
				+ "    series: [ "
				+ "        { "
				+ "            name: 'KPI', "
				+ "            colorByPoint: true, "
				+ "            data: "+loadDataKpi()
				+ "        } "
				+ "    ], "
				+ "    drilldown: { "
				+ "        breadcrumbs: { "
				+ "            position: { "
				+ "                align: 'right' "
				+ "            } "
				+ "        }, "
				+ "    } "
				+ "});"
				+ "</script>");
		
		div.add(html);
		
		div.addClassName("dashboard--boxshadow");

		div.setWidth("69%");
		if(isMobileLayout) {
			div.setWidthFull();
		}
		div.setHeight("200px");
		div.getStyle().setMargin("15px 0");
		return div;
	}
	
	private Component createChartCircleKpiLayout(String id,int total) {
		Div div = new Div();
		div.setId(id);
		
		Html html = new Html("<script>"
				+ "Highcharts.chart('"+id+"', {"
				+ "    chart: {"
				+ "        type: 'pie',"
				+ "        custom: {},"
				+ "        events: {"
				+ "            render() {"
				+ "                const chart = this,"
				+ "                    series = chart.series[0];"
				+ "                let customLabel = chart.options.chart.custom.label;"
				+ "                if (!customLabel) {"
				+ "                    customLabel = chart.options.chart.custom.label ="
				+ "                        chart.renderer.label("
				+ "                            'Total<br/>' +"
				+ "                            '<strong>"+total+"</strong>'"
				+ "                        )"
				+ "                            .css({"
				+ "                                color:"
				+ "                                    'var(--highcharts-neutral-color-100, #000)',"
				+ "                                textAnchor: 'middle'"
				+ "                            })"
				+ "                            .add();"
				+ "                }"
				+ "                const x = series.center[0] + chart.plotLeft,"
				+ "                    y = series.center[1] + chart.plotTop -"
				+ "                    (customLabel.attr('height') / 2);"
				+ "                customLabel.attr({"
				+ "                    x,"
				+ "                    y"
				+ "                });"
				+ "                customLabel.css({"
				+ "                    fontSize: `${series.center[2] / 12}px`"
				+ "                });"
				+ "            }"
				+ "        }"
				+ "    },"
				+ "    accessibility: {"
				+ "        point: {"
				+ "            valueSuffix: '%'"
				+ "        }"
				+ "    },"
				+ "    title: {"
				+ "        text: 'Nhiệm vụ đã giao được tính KPI'"
				+ "    },"
				+ "    tooltip: {"
				+ "        pointFormat: '{series.name}: <b>{point.y}</b> nhiệm vụ'"
				+ "    },"
				+ "    legend: {"
				+ "        enabled: false"
				+ "    },"
				+ "    plotOptions: {"
				+ "        series: {"
				+ "            allowPointSelect: true,"
				+ "            cursor: 'pointer',"
				+ "            borderRadius: 8,"
				+ "            dataLabels: [{"
				+ "                enabled: true,"
				+ "                distance: 20,"
				+ "                format: '{point.name}'"
				+ "            }, {"
				+ "                enabled: true,"
				+ "                distance: -15,"
				+ "                format: '{point.y} nhiệm vụ',"
				+ "                style: {"
				+ "                    fontSize: '0.9em'"
				+ "                }"
				+ "            }],"
				+ "            showInLegend: true"
				+ "        }"
				+ "    },"
				+ "    series: [{"
				+ "        name: 'KPI',"
				+ "        colorByPoint: true,"
				+ "        innerSize: '75%',"
				+ "        data: "+loadDataKpi()
				+ "    }]"
				+ "});"
				+ ""
				+ "</script>");
		
		div.add(html);
		div.addClassName("dashboard--boxshadow");
		div.setWidth("69%");
		div.getStyle().setMargin("15px 0");
		
		return div;
	}


}
