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
import com.ngn.models.sign_in_org.SignInOrgModel;
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

public class ListTasksFollowerForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	Span spanTitle = new Span("*Nhiệm vụ theo dõi là những nhiệm vụ mà đơn vị có thể theo dõi tiến độ các nhiệm vụ từ đơn vị khác.");
	
	private TaskFilterFollowerForm taskFilterForm;
	private PaginationForm paginationForm;
	private VerticalLayout vLayoutFilter = new VerticalLayout();
	private TaskOverviewSummaryFrom taskOverviewSummaryFrom = new TaskOverviewSummaryFrom();

	private TaskListForm taskListForm;
	private List<TaskOutputModel> listModel = new ArrayList<TaskOutputModel>();

	private DetailsTemplate detailsOverview = new DetailsTemplate("Tổng quan (Click vào các trạng thái ở bên dưới để tìm nhanh các nhiệm vụ theo trạng thái)", FontAwesome.Solid.EYE.create());

//	private SignInOrgModel signInOrgModel;
	private BelongOrganizationModel belongOrganizationModel;
	private Map<String, List<String>> parametters;
	public ListTasksFollowerForm(BelongOrganizationModel belongOrganizationModel,SignInOrgModel signInOrgModel,Map<String, List<String>> parametters) {
		this.belongOrganizationModel = belongOrganizationModel;
//		this.signInOrgModel = signInOrgModel;
		this.parametters = parametters;
		taskListForm = new TaskListForm(false,false,false,true,parametters);
		buildLayout();
		configComponent();
		loadData();
		checkLayoutMobile();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		
		spanTitle.getStyle().setFontWeight(600);
		
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
		
		
		hLayoutTitleTop.add(spanTitle,all,chipComponentStatus,chipComponentStatus2,chipComponentStatus3);
		
		this.add(hLayoutTitleTop,new Hr());
		taskFilterForm = new TaskFilterFollowerForm(belongOrganizationModel,parametters);
		paginationForm = new PaginationForm(()->{
			if(paginationForm != null) {
				loadData();
			}
		});

		vLayoutFilter.add(taskFilterForm,paginationForm);
		vLayoutFilter.setWidthFull();
		this.setSpacing(false);
		this.add(detailsOverview,vLayoutFilter,taskListForm);
		createLayoutEvent();
		
		UI.getCurrent().getPage().retrieveExtendedClientDetails(e->{
			if(e.getScreenWidth() < 768) {
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
		});
	}

	public void loadData() {
		listModel = new ArrayList<TaskOutputModel>();
		ApiResultResponse<List<ApiOutputTaskModel>> data = ApiTaskService.getListFollower(getSearch());
		if(data.isSuccess()) {
			listModel = data.getResult().stream().map(TaskOutputModel::new).toList();
			paginationForm.setItemCount(data.getTotal());
		}
		taskListForm.setData(listModel);
	}
	
	private void checkLayoutMobile() {
		UI.getCurrent().getPage().retrieveExtendedClientDetails(e->{
			if(e.getScreenWidth() < 768) {
				spanTitle.setVisible(false);
				paginationForm.setLayoutMobile();
				detailsOverview.setSummaryText("Tổng quan");
				detailsOverview.setOpened(false);
			}
		});

	}

	private void createLayoutEvent() {
		try {
			ApiResultResponse<List<ApiTaskSummaryModel>> dataSummary = ApiTaskService.getSummaryTasksFollower(getSearch());
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
		ApiFilterTaskModel apiFilterTaskModel = taskFilterForm.getSearchData();
		apiFilterTaskModel.setLimit(paginationForm.getLimit());
		apiFilterTaskModel.setSkip(paginationForm.getSkip());
//		apiFilterTaskModel.setOwnerOrganizationId(belongOrganizationModel.getOrganizationId());
//		apiFilterTaskModel.setFollowerOrganizationGroupId(signInOrgModel.getGroup().getId());

		return apiFilterTaskModel;

	}

}
