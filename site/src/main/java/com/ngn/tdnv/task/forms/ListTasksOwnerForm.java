package com.ngn.tdnv.task.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.vaadin.addons.yuri0x7c1.bslayout.BsLayout;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tasks.ApiFilterTaskModel;
import com.ngn.api.tasks.ApiOutputTaskModel;
import com.ngn.api.tasks.ApiTaskService;
import com.ngn.api.tasks.ApiTaskSummaryModel;
import com.ngn.enums.DataOfEnum;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.tdnv.task.forms.components.ChipComponentStatus;
import com.ngn.tdnv.task.models.TaskOutputModel;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.DetailsTemplate;
import com.ngn.utils.components.PaginationForm;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style.AlignItems;

public class ListTasksOwnerForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private Logger logger = LogManager.getFormatterLogger(this);
	private boolean isUser = SessionUtil.checkDataOf() == null ? true : SessionUtil.checkDataOf().getKey().equals(DataOfEnum.TOCANHAN.getKey());

	private Span spTitle = new Span(isUser == true ? "*Nhiệm vụ cá nhân đã thực hiện giao nhiệm vụ (bao gồm những nhiệm vụ giao thay)." : 
		"*Nhiệm vụ mà đơn vị đã giao cho các đơn vị khác (bao gồm các nhiệm vụ mà đơn vị giao cho cá nhân trong đơn vị hay phân nhiệm vụ từ nhiệm vụ khác).");
	
	
	
	private TaskOverviewSummaryFrom taskOverviewSummaryFrom = new TaskOverviewSummaryFrom();

	private TaskFilterOwnerForm taskFilterForm;
	private PaginationForm paginationForm;
	private VerticalLayout vLayoutFilter = new VerticalLayout();

	private TaskListForm taskListForm;
//	private LazyLoadListTaskForm lazyLoadListTaskForm;
	
	private List<TaskOutputModel> listModel = new ArrayList<TaskOutputModel>();

	private DetailsTemplate detailsOverview = new DetailsTemplate("Tổng quan (Click vào các trạng thái ở bên dưới để tìm nhanh các nhiệm vụ theo trạng thái)", FontAwesome.Solid.EYE.create());
	private BsLayout hLayoutDetail = new BsLayout();


	private BelongOrganizationModel belongOrganizationModel;
	@SuppressWarnings("unused")
	private UserAuthenticationModel userAuthenticationModel = SessionUtil.getUser();
	private Map<String, List<String>> parametters;
	public ListTasksOwnerForm(BelongOrganizationModel belongOrganizationModel,UserAuthenticationModel userAuthenticationModel,Map<String, List<String>> parametters) {
		this.belongOrganizationModel = belongOrganizationModel;
		this.userAuthenticationModel = userAuthenticationModel;
		this.parametters = parametters;
		taskListForm = new TaskListForm(true,false,false,false,parametters);
//		lazyLoadListTaskForm = new LazyLoadListTaskForm(true,false,false,false,parametters);
		buildLayout();
		configComponent();
		loadData();
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
		hLayoutTitleTop.getStyle().setAlignItems(AlignItems.CENTER);
		
		this.add(hLayoutTitleTop,new Hr());
		taskFilterForm = new TaskFilterOwnerForm(belongOrganizationModel,parametters);
		paginationForm = new PaginationForm(()->{
			if(paginationForm != null) {
				loadData();
			}
		});

		vLayoutFilter.add(taskFilterForm);
		vLayoutFilter.setWidthFull();

		this.add(detailsOverview,vLayoutFilter,paginationForm,taskListForm);
		this.setSpacing(false);
		createLayoutEvent();

		UI.getCurrent().getPage().retrieveExtendedClientDetails(e->{
			if(e.getScreenWidth() < 768) {
				spTitle.setVisible(false);
				paginationForm.setLayoutMobile();
				detailsOverview.setSummaryText("Tổng quan");
				detailsOverview.setOpened(false);
				all.setVisible(false);
				chipComponentStatus.setVisible(false);
				chipComponentStatus2.setVisible(false);
				chipComponentStatus3.setVisible(false);
			}
		});

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
		
//		lazyLoadListTaskForm.addChangeListener(e->{
//			loadData();
//			createLayoutEvent();
//		});
	}

	@Async
	public void loadData() {
		listModel = new ArrayList<TaskOutputModel>();
		ApiResultResponse<List<ApiOutputTaskModel>> data = ApiTaskService.getListTaskOwner(getSearch());

		if(data.isSuccess()) {
			listModel = data.getResult()
					.parallelStream()
					.map(TaskOutputModel::new)
					.collect(Collectors.toList());

			paginationForm.setItemCount(data.getTotal());

		}else {
			logger.error(data);
		}
		taskListForm.setData(listModel);
//		lazyLoadListTaskForm.setData(listModel);
	}



	private void createLayoutEvent() {
		try {
			ApiResultResponse<List<ApiTaskSummaryModel>> dataSummary = ApiTaskService.getSummaryTasksOwner(getSearch());
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
		detailsOverview.addClassName("detail-filter_background");
		detailsOverview.add(taskOverviewSummaryFrom);

	}

	private ApiFilterTaskModel getSearch() {
		ApiFilterTaskModel apiFilterTaskModel = taskFilterForm.getSearchData();
		apiFilterTaskModel.setLimit(paginationForm.getLimit());
		apiFilterTaskModel.setSkip(paginationForm.getSkip());
		return apiFilterTaskModel;

	}

}






















