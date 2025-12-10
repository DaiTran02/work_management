package com.ngn.tdnv.task.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.addons.yuri0x7c1.bslayout.BsLayout;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tasks.ApiFilterTaskModel;
import com.ngn.api.tasks.ApiOutputTaskModel;
import com.ngn.api.tasks.ApiTaskService;
import com.ngn.api.tasks.ApiTaskSummaryModel;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.UserAuthenticationModel;
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

public class ListTasksAssigneeForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private Logger logger = LogManager.getFormatterLogger(this);

	private Span spTitle = new Span("*Nhiệm vụ mà đơn vị của bạn được chỉ định để xử lý, có thể được hỗ trợ từ những đơn vị khác (nếu có).");
	
	private VerticalLayout vLayoutActionFilter = new VerticalLayout();

	private List<TaskOutputModel> listModel = new ArrayList<TaskOutputModel>();

	private DetailsTemplate detailsOverview = new DetailsTemplate("Tổng quan (Click vào các trạng thái ở bên dưới để tìm nhanh các nhiệm vụ theo trạng thái)", FontAwesome.Solid.EYE.create());
	private BsLayout hLayoutDetail = new BsLayout();

	private TaskOverviewSummaryFrom taskOverviewSummaryFrom = new TaskOverviewSummaryFrom();

	private TaskFilterAssigneeForm taskFilterForm;
	private PaginationForm paginationForm = new PaginationForm(()->{});
	private TaskListForm taskListForm;

	private BelongOrganizationModel belongOrganizationModel;
	private UserAuthenticationModel userAuthenticationModel;
	private Map<String, List<String>> parameters;
	public ListTasksAssigneeForm(BelongOrganizationModel belongOrganizationModel,UserAuthenticationModel userAuthenticationModel,Map<String, List<String>> parametters) {
		this.belongOrganizationModel = belongOrganizationModel;
		this.userAuthenticationModel = userAuthenticationModel;
		this.parameters = parametters;
		taskListForm = new TaskListForm(false,true,false,false,parametters);
		buildLayout();
		configComponent();
		loadData();
		checkMobileLayout();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		System.out.println(userAuthenticationModel.getId());
		
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
		
		taskFilterForm = new TaskFilterAssigneeForm(belongOrganizationModel,parameters);

		paginationForm = new PaginationForm(()->{
			loadData();
		});

		vLayoutActionFilter.add(taskFilterForm,paginationForm);
		//		vLayoutActionFilter.addClassName("layout__task");
		vLayoutActionFilter.setWidthFull();

		//		taskListForm.getStyle().setMarginTop("188px");

		this.add(detailsOverview,vLayoutActionFilter,taskListForm);
		createLayoutEvent();
		
		try {
			UI.getCurrent().getPage().retrieveExtendedClientDetails(e->{
				if(e.getScreenWidth() < 768) {
					detailsOverview.setSummaryText("Tổng quan");
					detailsOverview.setOpened(false);
					spTitle.setVisible(false);
					paginationForm.setLayoutMobile();
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
		taskFilterForm.addChangeListener(e->{
			loadData();
			createLayoutEvent();
		});

		taskListForm.addChangeListener(e->{
			loadData();
			createLayoutEvent();
		});

	}

	public void loadData() {
		listModel = new ArrayList<TaskOutputModel>();
		try {
			ApiResultResponse<List<ApiOutputTaskModel>> data = ApiTaskService.getListAssignee(getParam());
			paginationForm.setItemCount(data.getTotal());
			listModel = data.getResult().stream().map(TaskOutputModel::new).toList();
			taskListForm.setData(listModel);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
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

	private void createLayoutEvent() {
		try {
			ApiResultResponse<List<ApiTaskSummaryModel>> dataSummary = ApiTaskService.getSummaryTaskAssignee(getParam());
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

		hLayoutDetail.setWidthFull();

		detailsOverview.removeAll();
		detailsOverview.setOpened(true);
		//The className in file dashboard.css
		detailsOverview.addClassName("detail-filter_background");
		detailsOverview.add(taskOverviewSummaryFrom);

	}

	public ApiFilterTaskModel getParam() {
		ApiFilterTaskModel apiFilterTaskModel = taskFilterForm.getSearchData();
		apiFilterTaskModel.setLimit(paginationForm.getLimit());
		apiFilterTaskModel.setSkip(paginationForm.getSkip());
		
		return apiFilterTaskModel;
	}

}
