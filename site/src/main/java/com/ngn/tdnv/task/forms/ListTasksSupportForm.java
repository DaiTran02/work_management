package com.ngn.tdnv.task.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tasks.ApiFilterTaskModel;
import com.ngn.api.tasks.ApiOutputTaskModel;
import com.ngn.api.tasks.ApiTaskService;
import com.ngn.api.tasks.ApiTaskSummaryModel;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.tdnv.task.forms.components.ChipComponentStatus;
import com.ngn.tdnv.task.models.TaskOutputModel;
import com.ngn.utils.components.DetailsTemplate;
import com.ngn.utils.components.PaginationForm;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ListTasksSupportForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private Span spTitle = new Span("*Nhiệm vụ hỗ trợ là những nhiệm vụ mà đơn vị hỗ trợ thực hiện nhiệm vụ cho các đơn vị xử lý nhiệm vụ.");
	private TaskFilterSupportForm taskFilterForm;
	private PaginationForm paginationForm;
	private VerticalLayout vLayoutFilter = new VerticalLayout();

	private TaskListForm taskListForm;
	private List<TaskOutputModel> listModel = new ArrayList<TaskOutputModel>();

	private DetailsTemplate detailsOverview = new DetailsTemplate("Tổng quan (Click vào các trạng thái ở bên dưới để tìm nhanh các nhiệm vụ theo trạng thái)", FontAwesome.Solid.EYE.create());
	private TaskOverviewSummaryFrom taskOverviewSummaryFrom = new TaskOverviewSummaryFrom();

	private BelongOrganizationModel belongOrganizationModel;
	private Map<String, List<String>> parameters;
	public ListTasksSupportForm(BelongOrganizationModel belongOrganizationModel,Map<String, List<String>> parametters) {
		this.belongOrganizationModel = belongOrganizationModel;
		this.parameters = parametters;
		taskListForm = new TaskListForm(false,false,true,false,parametters);
		buildLayout();
		configComponent();
		loadData();
		checkMobileLayout();
	}


	@Override
	public void buildLayout() {
		this.setSizeFull();
		spTitle.getStyle().setFontWeight(600);
		
		HorizontalLayout hLayoutTitleTop = new HorizontalLayout();
		hLayoutTitleTop.setWidthFull();
		
		ChipComponentStatus all = new ChipComponentStatus("Tất cả", " rgb(175 175 175)");
		all.getStyle().setMarginLeft("auto");
		
		ChipComponentStatus chipComponentStatus = new ChipComponentStatus("Nhiệm vụ đánh giá KPI", "#376854");
		
		ChipComponentStatus chipComponentStatus2 = new ChipComponentStatus("Nhiệm vụ hỏa tốc thực hiện", "red");
		
		ChipComponentStatus chipComponentStatus3 = new ChipComponentStatus("Nhiệm vụ khẩn", "#a9911d");
		
		chipComponentStatus.addClickListener(e->{
			taskFilterForm.setKpiFilter(true);
			loadData();
		});
		chipComponentStatus2.addClickListener(e->taskFilterForm.setCmbPriority("hoatoc"));
		chipComponentStatus3.addClickListener(e->taskFilterForm.setCmbPriority("khan"));
		all.addClickListener(e->{
			taskFilterForm.setKpiFilter(null);
			taskFilterForm.setCmbPriority(null);
			loadData();
		});
		
		hLayoutTitleTop.add(spTitle,all,chipComponentStatus,chipComponentStatus2,chipComponentStatus3);
		
		this.add(hLayoutTitleTop,new Hr());
		taskFilterForm = new TaskFilterSupportForm(belongOrganizationModel,parameters);
		paginationForm = new PaginationForm(()->{
			if(paginationForm != null) {
				loadData();
			}
		});
		vLayoutFilter.add(taskFilterForm,paginationForm);
		vLayoutFilter.setWidthFull();

		this.add(detailsOverview,vLayoutFilter,taskListForm);
		createLayoutEvent();
		try {
			UI.getCurrent().getPage().retrieveExtendedClientDetails(e->{
				if(e.getScreenWidth() < 768) {
					all.setVisible(false);
					chipComponentStatus.setVisible(false);
					chipComponentStatus2.setVisible(false);
					chipComponentStatus3.setVisible(false);
				}
			});
		} catch (Exception e) {
		}

	}

	@Override
	public void configComponent() {
		taskFilterForm.addChangeListener(e->loadData());

	}
	
	private void checkMobileLayout() {
		try {
			UI.getCurrent().getPage().retrieveExtendedClientDetails(e->{
				if(e.getScreenWidth() < 768) {
					detailsOverview.setSummaryText("Tổng quan");
					detailsOverview.setOpened(false);
					spTitle.setVisible(false);
					paginationForm.setLayoutMobile();
				}
			});
		} catch (Exception e) {
		}
	}

	public void loadData() {
		listModel = new ArrayList<TaskOutputModel>();
		ApiResultResponse<List<ApiOutputTaskModel>> data = ApiTaskService.getListSupport(getSearch());
		if(data.isSuccess()) {
			listModel = data.getResult().stream().map(TaskOutputModel::new).toList();
			paginationForm.setItemCount(data.getTotal());
		}
		taskListForm.setData(listModel);
	}

	private void createLayoutEvent() {
		try {
			ApiResultResponse<List<ApiTaskSummaryModel>> dataSummary = ApiTaskService.getSummaryTaskSupport(getSearch());
			if(dataSummary.isSuccess()) {
				
				taskOverviewSummaryFrom.loadData(dataSummary.getResult());
				

				taskOverviewSummaryFrom.getListButtons().forEach(button->{
					
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
						taskFilterForm.setCmbStatus(button.getId().get());
					});
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		detailsOverview.removeAll();
		detailsOverview.setOpened(true);
		detailsOverview.addClassName("detail-filter_background");
		detailsOverview.add(taskOverviewSummaryFrom);

	}

	private ApiFilterTaskModel getSearch() {
		ApiFilterTaskModel filterTaskModel = taskFilterForm.getSearchData();

		filterTaskModel.setSkip(paginationForm.getSkip());
		filterTaskModel.setLimit(paginationForm.getLimit());
		filterTaskModel.setSupportOrganizationId(belongOrganizationModel.getOrganizationId());

		return filterTaskModel;
	}

}

