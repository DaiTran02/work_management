package com.ngn.tdnv.doc.forms;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.doc.ApiDocService;
import com.ngn.api.doc.ApiFilterSummaryDocModel;
import com.ngn.api.doc.ApiFilterTaskOfDocModel;
import com.ngn.api.organization.ApiOrganizationModel;
import com.ngn.api.organization.ApiOrganizationService;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tasks.ApiOutputTaskModel;
import com.ngn.api.tasks.ApiTaskService;
import com.ngn.api.tasks.ApiTaskSummaryModel;
import com.ngn.api.utils.ApiKeyValueModel;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.models.sign_in_org.SignInOrgModel;
import com.ngn.tdnv.doc.forms.create_task.TaskCreateFormV2;
import com.ngn.tdnv.doc.models.DocModel;
import com.ngn.tdnv.task.enums.StatusTaskEnum;
import com.ngn.tdnv.task.forms.TaskListForm;
import com.ngn.tdnv.task.forms.TaskOverviewSummaryFrom;
import com.ngn.tdnv.task.models.TaskOutputModel;
import com.ngn.utils.LocalDateUtil;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.DialogTemplate;
import com.ngn.utils.components.PaginationForm;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePickerVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.dom.Style.Display;
import com.vaadin.flow.dom.Style.FlexWrap;

public class ListTasksOfDocForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private boolean isUiMobile = false;
	private UserAuthenticationModel userAuthenticationModel = SessionUtil.getUser();
	private SignInOrgModel signInOrgModel = SessionUtil.getDetailOrg();

	private DateTimePicker dateStartDay = new DateTimePicker("Từ ngày");
	private DateTimePicker dateEndDay = new DateTimePicker("Đến ngày");
	
	private VerticalLayout vLayout = new VerticalLayout();
	private TaskListForm taskListForm = new TaskListForm(true,false,false,false,null);
	private DocModel docModel;
	private ApiFilterSummaryDocModel apiFilterSummaryDocModel;
	private PaginationForm paginationForm;
	private ComboBox<Pair<String, String>> cmbStatus = new ComboBox<Pair<String,String>>("Trạng thái");
	private List<TaskOutputModel> listModel = new ArrayList<TaskOutputModel>();
	private TextField txtSearch = new TextField("Tìm kiếm");
	private ButtonTemplate btnSearch = new ButtonTemplate("Tìm",FontAwesome.Solid.SEARCH.create());
	private ComboBox<Pair<String, String>> cmbAssignee = new ComboBox<Pair<String,String>>("Đơn vị xử lý");
	private BelongOrganizationModel belongOrganizationModel = SessionUtil.getOrg();
	private TaskOverviewSummaryFrom taskOverviewSummaryFrom = new TaskOverviewSummaryFrom();
	private ButtonTemplate btnCreateTask = new ButtonTemplate("Giao nhiệm vụ mới",FontAwesome.Solid.PLUS.create());
	public ListTasksOfDocForm(DocModel docModel,ApiFilterSummaryDocModel apiFilterSummaryDocModel) {
		this.docModel = docModel;
		this.apiFilterSummaryDocModel = apiFilterSummaryDocModel;
		loadDateTime();
		checkUiMobile();
		loadCmdAssignee();
		buildLayout();
		configComponent();
		loadCmbStatus();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		if(!docModel.getOwner().getOrganizationId().equals(belongOrganizationModel.getOrganizationId())) {
			Span spanWarningOwner = new Span("Cảnh báo: Văn bản này không thuộc quyền quản lý của đơn vị đang sử dụng, bạn sẽ không thấy văn bản này ở danh sách văn bản của bạn sau khi sử dụng xong, "
					+ "nhiệm vụ vẫn sẽ được giao dựa vào thông tin của văn bản và thuộc quyền quản lý của đơn vị này.");
			
			spanWarningOwner.getStyle().setBackground("#ffe4e487").setBorderLeft("5px solid #ff6f6f").setPadding("5px").setBorderRadius("10px").setColor("#9f3030").setFontWeight(500);
			this.add(spanWarningOwner);
		}
		paginationForm = new PaginationForm(()->{
			if(paginationForm != null) {
				loadData();
			}
		});
		this.add(vLayout);
		createLayout();
	}

	@Override
	public void configComponent() {
		dateStartDay.addValueChangeListener(e->loadData());
		dateEndDay.addValueChangeListener(e->loadData());
		cmbStatus.addValueChangeListener(e->loadData());
		txtSearch.addValueChangeListener(e->loadData());
		btnSearch.addClickListener(e->loadData());
		cmbAssignee.addValueChangeListener(e->{
			loadData();
			createSummaryLayout();
		});
		taskListForm.addChangeListener(e->{
			loadData();
			fireEvent(new ClickEvent(this, false));
		});
		
		btnCreateTask.addClickListener(e->openDialogCreateTask2(docModel));
		
	}
	
	private void loadDateTime() {
		dateStartDay.setLocale(LocalDateUtil.localeVietNam());
		dateStartDay.setValue(LocalDateUtil.longToLocalDateTime(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getStartOfTheYear(SessionUtil.getYear()))));


		dateEndDay.setValue(LocalDateUtil.longToLocalDateTime(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getEndtOfTheYear(SessionUtil.getYear()))));
		dateEndDay.setLocale(LocalDateUtil.localeVietNam());

	}
	
	private void loadData() {
		listModel = new ArrayList<TaskOutputModel>();
		ApiResultResponse<List<ApiOutputTaskModel>> data = ApiDocService.getListTaskOfDoc(getSearch());
		if(data.isSuccess()) {
			listModel = data.getResult().stream().map(TaskOutputModel::new).toList();
			paginationForm.setItemCount(data.getTotal());
		}
		
		taskListForm.setData(listModel);
	}
	
	private ApiFilterTaskOfDocModel getSearch() {
		ApiFilterTaskOfDocModel apiFilterTaskOfDocModel = new ApiFilterTaskOfDocModel();
		apiFilterTaskOfDocModel.setDocId(docModel.getId());
		apiFilterTaskOfDocModel.setLimit(paginationForm.getLimit());
		apiFilterTaskOfDocModel.setSkip(paginationForm.getSkip());
		apiFilterTaskOfDocModel.setStatus(cmbStatus.getValue().getKey());
		apiFilterTaskOfDocModel.setKeyword(txtSearch.getValue());
		apiFilterTaskOfDocModel.setAssigneeOrganizationId(cmbAssignee == null ? null : cmbAssignee.getValue().getKey());
		apiFilterTaskOfDocModel.setFromDate(LocalDateUtil.localDateTimeToLong(dateStartDay.getValue()));
		apiFilterTaskOfDocModel.setToDate(LocalDateUtil.localDateTimeToLong(dateEndDay.getValue()));
		
		return apiFilterTaskOfDocModel;
	}
	
	private ApiFilterSummaryDocModel getFilterSummary() {
		ApiFilterSummaryDocModel filter = apiFilterSummaryDocModel;
		
		filter.setAssigneeOrganizationId(cmbAssignee == null ? null : cmbAssignee.getValue().getKey());
		
		return filter;
	}

	private void createLayout() {
		vLayout.removeAll();
		vLayout.setWidthFull();
		taskListForm.getStyle().setPadding("0");

		H3 header = new H3("Tổng số nhiệm vụ đã giao từ văn bản: "+docModel.getCountTask());
		Span spTitle = new Span("*Click vào các trạng thái bên dưới để tìm nhiệm vụ theo trang thái nhanh hơn");
		
		
//		cmbStatus.setWidth("400px");
		cmbStatus.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbStatus.getStyle().set("--vaadin-combo-box-overlay-width", "350px");
		
//		cmbAssignee.setWidth("600px");
		
		txtSearch.setPlaceholder("Nhập từ khóa để tìm...");
		txtSearch.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		
		cmbAssignee.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbAssignee.getStyle().set("--vaadin-combo-box-overlay-width", "350px");
		
		
		
		btnSearch.getStyle().setMarginTop("30px");
		btnSearch.addThemeVariants(ButtonVariant.LUMO_SMALL);
		
		btnCreateTask.getStyle().setMarginTop("30px");
		btnCreateTask.addThemeVariants(ButtonVariant.LUMO_SMALL);
		
		if(docModel.getResultConfirm() != null) {
			btnCreateTask.setEnabled(false);
		}
		
		HorizontalLayout hLayoutSearch = new HorizontalLayout();
		hLayoutSearch.setWidthFull();
		hLayoutSearch.add(dateStartDay,dateEndDay,txtSearch,cmbAssignee,cmbStatus,btnSearch,btnCreateTask);
		hLayoutSearch.expand(txtSearch);
		
		dateStartDay.addThemeVariants(DateTimePickerVariant.LUMO_SMALL);
		dateStartDay.setDatePickerI18n(LocalDateUtil.i18nVietNam());

		dateEndDay.addThemeVariants(DateTimePickerVariant.LUMO_SMALL);
		dateEndDay.setDatePickerI18n(LocalDateUtil.i18nVietNam());
		
		hLayoutSearch.getStyle().setDisplay(Display.FLEX).setFlexWrap(FlexWrap.WRAP);
		if(isUiMobile) {
			txtSearch.setWidth("48%");
			cmbAssignee.setWidth("48%");
			cmbStatus.setWidth("48%");
			btnSearch.setWidth("48%");
		}
		
		vLayout.add(header,spTitle,new Hr(),taskOverviewSummaryFrom,hLayoutSearch,paginationForm,taskListForm);
		createSummaryLayout();
	}
	
	private void checkUiMobile() {
		try {
			UI.getCurrent().getPage().retrieveExtendedClientDetails(e->{
				if(e.getScreenWidth() < 768) {
					isUiMobile = true;
				}
			});
		} catch (Exception e) {
		}
	}
	
	private void loadCmbStatus() {
		List<Pair<String,String>> listStatus = new ArrayList<Pair<String,String>>();
		listStatus.add(Pair.of(null,"Tất cả"));
		ApiResultResponse<List<ApiKeyValueModel>> getStatus = ApiTaskService.getStatus();
		if(getStatus.isSuccess()) {
			getStatus.getResult().stream().forEach(model->{
				listStatus.add(Pair.of(model.getKey(),model.getName()));
			});
		}


		cmbStatus.setItems(listStatus);
		cmbStatus.setItemLabelGenerator(Pair::getRight);
		cmbStatus.setValue(listStatus.get(0));
	}

	private void createSummaryLayout() {
		ApiResultResponse<List<ApiTaskSummaryModel>> getSummaryTaskDoc = ApiDocService.getSummaryTaskOfDoc(getFilterSummary());
		if(getSummaryTaskDoc.isSuccess()) {
			taskOverviewSummaryFrom.loadData(getSummaryTaskDoc.getResult());
			taskOverviewSummaryFrom.getListButtons().forEach(button->{

				//Dang thuc hien
				if(button.getId().get().equals(StatusTaskEnum.DANGTHUCHIEN.getKey())) {
					button.addClickListener(e->{
						cmbStatus.getListDataView().getItems().forEach(md->{
							if(md.getKey()!=null) {
								if(md.getKey().equals(StatusTaskEnum.DANGTHUCHIEN.getKey())) {
									cmbStatus.setValue(md);
									loadData();
								}
							}
						});
						
						
						
					});
				}

				if(button.getId().get().equals(StatusTaskEnum.DANGTHUCHIEN_TRONGHAN.getKey())) {
					button.addClickListener(e->{
						cmbStatus.getListDataView().getItems().forEach(md->{
							if(md.getKey()!=null) {
								if(md.getKey().equals(StatusTaskEnum.DANGTHUCHIEN_TRONGHAN.getKey())) {
									cmbStatus.setValue(md);
									loadData();
								}
							}
						});
					});
				}

				if(button.getId().get().equals(StatusTaskEnum.DANGTHUCHIEN_QUAHAN.getKey())) {
					button.addClickListener(e->{
						cmbStatus.getListDataView().getItems().forEach(md->{
							if(md.getKey()!=null) {
								if(md.getKey().equals(StatusTaskEnum.DANGTHUCHIEN_QUAHAN.getKey())) {
									cmbStatus.setValue(md);
									loadData();
								}
							}
						});
					});
				}
				if(button.getId().get().equals(StatusTaskEnum.DANGTHUCHIEN_SAPQUAHAN.getKey())) {
					button.addClickListener(e->{
						cmbStatus.getListDataView().getItems().forEach(md->{
							if(md.getKey()!=null) {
								if(md.getKey().equals(StatusTaskEnum.DANGTHUCHIEN_SAPQUAHAN.getKey())) {
									cmbStatus.setValue(md);
									loadData();
								}
							}
						});
					});
				}
				if(button.getId().get().equals(StatusTaskEnum.DANGTHUCHIEN_KHONGHAN.getKey())) {
					button.addClickListener(e->{
						cmbStatus.getListDataView().getItems().forEach(md->{
							if(md.getKey()!=null) {
								if(md.getKey().equals(StatusTaskEnum.DANGTHUCHIEN_KHONGHAN.getKey())) {
									cmbStatus.setValue(md);
									loadData();
								}
							}
						});
					});
				}

				// Cho xac nhan
				if(button.getId().get().equals(StatusTaskEnum.CHOXACNHAN.getKey())) {
					button.addClickListener(e->{
						cmbStatus.getListDataView().getItems().forEach(md->{
							if(md.getKey()!=null) {
								if(md.getKey().equals(StatusTaskEnum.CHOXACNHAN.getKey())) {
									cmbStatus.setValue(md);
									loadData();
								}
							}
						});
					});
				}

				if(button.getId().get().equals(StatusTaskEnum.CHOXACNHAN_KHONGHAN.getKey())) {
					button.addClickListener(e->{
						cmbStatus.getListDataView().getItems().forEach(md->{
							if(md.getKey()!=null) {
								if(md.getKey().equals(StatusTaskEnum.CHOXACNHAN_KHONGHAN.getKey())) {
									cmbStatus.setValue(md);
									loadData();
								}
							}
						});
					});
				}

				if(button.getId().get().equals(StatusTaskEnum.CHOXACNHAN_QUAHAN.getKey())) {
					button.addClickListener(e->{
						cmbStatus.getListDataView().getItems().forEach(md->{
							if(md.getKey()!=null) {
								if(md.getKey().equals(StatusTaskEnum.CHOXACNHAN_QUAHAN.getKey())) {
									cmbStatus.setValue(md);
									loadData();
								}
							}
						});
					});
				}

				if(button.getId().get().equals(StatusTaskEnum.CHOXACNHAN_TRONGHAN.getKey())) {
					button.addClickListener(e->{
						cmbStatus.getListDataView().getItems().forEach(md->{
							if(md.getKey()!=null) {
								if(md.getKey().equals(StatusTaskEnum.CHOXACNHAN_TRONGHAN.getKey())) {
									cmbStatus.setValue(md);
									loadData();
								}
							}
						});
					});
				}

				//Da hoan thanh
				if(button.getId().get().equals(StatusTaskEnum.DAHOANTHANH.getKey())) {
					button.addClickListener(e->{
						cmbStatus.getListDataView().getItems().forEach(md->{
							if(md.getKey()!=null) {
								if(md.getKey().equals(StatusTaskEnum.DAHOANTHANH.getKey())) {
									cmbStatus.setValue(md);
									loadData();
								}
							}
						});
					});
				}

				if(button.getId().get().equals(StatusTaskEnum.DAHOANTHANH_KHONGHAN.getKey())) {
					button.addClickListener(e->{
						cmbStatus.getListDataView().getItems().forEach(md->{
							if(md.getKey()!=null) {
								if(md.getKey().equals(StatusTaskEnum.DAHOANTHANH_KHONGHAN.getKey())) {
									cmbStatus.setValue(md);
									loadData();
								}
							}
						});
					});
				}

				if(button.getId().get().equals(StatusTaskEnum.DAHOANTHANH_QUAHAN.getKey())) {
					button.addClickListener(e->{
						cmbStatus.getListDataView().getItems().forEach(md->{
							if(md.getKey()!=null) {
								if(md.getKey().equals(StatusTaskEnum.DAHOANTHANH_QUAHAN.getKey())) {
									cmbStatus.setValue(md);
									loadData();
								}
							}
						});
					});
				}

				if(button.getId().get().equals(StatusTaskEnum.DAHOANTHANH_TRONGHAN.getKey())) {
					button.addClickListener(e->{
						cmbStatus.getListDataView().getItems().forEach(md->{
							if(md.getKey()!=null) {
								if(md.getKey().equals(StatusTaskEnum.DAHOANTHANH_TRONGHAN.getKey())) {
									cmbStatus.setValue(md);
									loadData();
								}
							}
						});
					});
				}

				//Khac

				if(button.getId().get().equals(StatusTaskEnum.CHUATHUCHIEN.getKey())) {
					button.addClickListener(e->{
						cmbStatus.getListDataView().getItems().forEach(md->{
							if(md.getKey()!=null) {
								if(md.getKey().equals(StatusTaskEnum.CHUATHUCHIEN.getKey())) {
									cmbStatus.setValue(md);
									loadData();
								}
							}
						});
					});
				}

				if(button.getId().get().equals(StatusTaskEnum.TAMHOAN.getKey())) {
					button.addClickListener(e->{
						cmbStatus.getListDataView().getItems().forEach(md->{
							if(md.getKey()!=null) {
								if(md.getKey().equals(StatusTaskEnum.TAMHOAN.getKey())) {
									cmbStatus.setValue(md);
									loadData();
								}
							}
						});
					});
				}

				if(button.getId().get().equals(StatusTaskEnum.TUCHOITHUCHIEN.getKey())) {
					button.addClickListener(e->{
						cmbStatus.getListDataView().getItems().forEach(md->{
							if(md.getKey()!=null) {
								if(md.getKey().equals(StatusTaskEnum.TUCHOITHUCHIEN.getKey())) {
									cmbStatus.setValue(md);
									loadData();
								}
							}
						});
					});
				}

				if(button.getId().get().equals(StatusTaskEnum.THUCHIENLAI.getKey())) {
					button.addClickListener(e->{
						cmbStatus.getListDataView().getItems().forEach(md->{
							if(md.getKey()!=null) {
								if(md.getKey().equals(StatusTaskEnum.THUCHIENLAI.getKey())) {
									cmbStatus.setValue(md);
									loadData();
								}
							}
						});
					});
				}

			});
		}

	}
	
	private void loadCmdAssignee() {
		List<Pair<String, String>> listAssignee = new ArrayList<Pair<String,String>>();
		listAssignee.add(Pair.of(null,"Tất cả"));

		ApiResultResponse<List<ApiOrganizationModel>> listOrg = ApiOrganizationService.getListOrganization(belongOrganizationModel.getOrganizationId());
		if(listOrg.isSuccess()) {
			
			listOrg.getResult().stream().forEach(model->{
				listAssignee.add(Pair.of(model.getId(),model.getName()));
			});
		}

		cmbAssignee.setItems(listAssignee);
		cmbAssignee.setItemLabelGenerator(Pair::getRight);
		if(!listAssignee.isEmpty()) {
			cmbAssignee.setValue(listAssignee.get(0));
		}

	}
	
	private void openDialogCreateTask2(DocModel docModel) {
		DialogTemplate dialogTemplate = new DialogTemplate("Giao nhiệm vụ mới");
		//		TaskCreateForm createTaskForm = new TaskCreateForm(docModel.getId(),belongOrganizationModel, userAuthenticationModel,signInOrgModel,null);
		TaskCreateFormV2 createTaskForm = new TaskCreateFormV2(docModel.getId(),belongOrganizationModel, userAuthenticationModel,signInOrgModel,null);
		dialogTemplate.add(createTaskForm);

		createTaskForm.addChangeListener(e->{
			loadData();
			refreshMainLayout();
			dialogTemplate.close();
		});

		dialogTemplate.setHeightFull();
		if(docModel.getAttachments().isEmpty()) {
			dialogTemplate.setWidth("60%");
		}else {
			dialogTemplate.setWidth("100%");
		}
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setLayoutMobile();

		dialogTemplate.open();
	}


}





















