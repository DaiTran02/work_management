package com.ngn.tdnv.task.forms.details;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.media.ApiInputMediaModel;
import com.ngn.api.media.ApiMediaModel;
import com.ngn.api.media.ApiMediaService;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tasks.ApiOutputTaskModel;
import com.ngn.api.tasks.ApiTaskService;
import com.ngn.api.tasks.actions.ApiActionService;
import com.ngn.api.tasks.actions.ApiCommentModel;
import com.ngn.api.tasks.actions.ApiCreatorActionModel;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.tdnv.task.enums.StatusTaskEnum;
import com.ngn.tdnv.task.forms.broadcasts.TaskCommentBroadcaster;
import com.ngn.tdnv.task.models.TaskOutputModel;
import com.ngn.tdnv.task.models.TaskCommentModel;
import com.ngn.utils.LocalDateUtil;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.ActtachmentForm;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.DialogTemplate;
import com.ngn.utils.components.NotificationTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.ngn.utils.uploads.UploadModuleBasic;
import com.ngn.utils.uploads.UploadModuleDataModel;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBoxVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;

public class TaskCommentForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private UI ui = UI.getCurrent();

	private Registration taskCommentBroadcaster;
	private TaskOutputModel outputTaskModel;

	private HorizontalLayout hLayoutComment = new HorizontalLayout();
	private Avatar avatarComment = new Avatar();
	private TextField txtComment = new TextField();
	private ButtonTemplate btnAttachmentsComment = new ButtonTemplate(FontAwesome.Solid.PAPERCLIP.create());
	private ButtonTemplate btnSendComment = new ButtonTemplate(FontAwesome.Solid.PAPER_PLANE.create());
	private MultiSelectComboBox<UploadModuleDataModel> multiSelectCmbComment = new MultiSelectComboBox<UploadModuleDataModel>("Danh sách đính kèm");
	private List<UploadModuleDataModel> listDataFileComment = new ArrayList<UploadModuleDataModel>();

	private Avatar avatarReply = new Avatar();
	private TextField txtReply = new TextField();
	private ButtonTemplate btnAttachmentReply = new ButtonTemplate(FontAwesome.Solid.PAPERCLIP.create());
	private ButtonTemplate btnSendReply = new ButtonTemplate(FontAwesome.Solid.PAPER_PLANE.create());
	private MultiSelectComboBox<UploadModuleDataModel> multiSelectCmbReply = new MultiSelectComboBox<UploadModuleDataModel>("Danh sách đính kèm");
	private List<UploadModuleDataModel> listDataFileReply = new ArrayList<UploadModuleDataModel>();
	
	private Span spReason = new Span("*Nhiệm vụ đã hoàn thành không thể tiếp tục trao đổi");

	private VerticalLayout vLayoutDataComment = new VerticalLayout();

	private List<TaskCommentModel> listTaskComment = new ArrayList<TaskCommentModel>();
	private List<String> listIdAttachment = new ArrayList<String>();

	private String idTask;
	public TaskCommentForm(String idTask) {
		this.idTask = idTask;
		buildLayout();
		configComponent();
	}

	public TextField getTxtComment() {
		return txtComment;
	}



	@Override
	public void buildLayout() {
		this.setSizeFull();
		vLayoutDataComment.setWidthFull();
		
		spReason.getStyle().setFontWeight(600);
		spReason.setVisible(false);
		this.add(spReason,hLayoutComment,multiSelectCmbComment,vLayoutDataComment);
		loadLayoutFirstComment();

	}

	@Override
	public void configComponent() {
		btnAttachmentsComment.addClickListener(e->{
			openDialogUploadFile(false);
		});

		btnSendComment.addClickListener(e->{
			if(!txtComment.getValue().isBlank()) {
				listIdAttachment = new ArrayList<>();
				uploadFile(listDataFileComment);
				comment(txtComment.getValue(), listIdAttachment, false,null);
				txtComment.clear();
				multiSelectCmbComment.clear();
				multiSelectCmbComment.setVisible(false);
			}
		});

		btnAttachmentReply.addClickListener(e->{
			openDialogUploadFile(true);

		});

		txtComment.addValueChangeListener(e->{
			if(!txtComment.getValue().isEmpty()) {
				btnSendComment.addClickShortcut(Key.ENTER);
			}else {
				btnSendComment.addClickShortcut(Key.PAUSE);
			}
		});

		txtReply.addValueChangeListener(e->{
			if(!txtReply.getValue().isBlank()) {
				btnSendReply.addClickShortcut(Key.ENTER);
			}else {
				btnSendComment.addClickShortcut(Key.PAUSE);
				btnSendReply.addClickShortcut(Key.PAUSE);
			}
		});
	}


	public void loadData(TaskOutputModel taskOutputModel) {
		listTaskComment = new ArrayList<TaskCommentModel>();
		vLayoutDataComment.removeAll();
		try {
			if(idTask!=null) {
				ApiResultResponse<ApiOutputTaskModel> data = ApiTaskService.getAtask(idTask);
				
				outputTaskModel = new TaskOutputModel(data.getResult());
				
				data.getResult().getComments().stream().forEach(model->{
					TaskCommentModel taskCommentModel = new TaskCommentModel(model);
					taskCommentModel.setIdTask(idTask);
					listTaskComment.add(taskCommentModel);
				});


				listTaskComment.stream().forEach(model->{
					vLayoutDataComment.add(createLayoutMessed(model));
					if(!model.getReplies().isEmpty()) {
						VerticalLayout vLayoutDataReply = new VerticalLayout();
						vLayoutDataReply.getStyle().setMarginLeft("20px");
						vLayoutDataReply.setWidth("90%");
						vLayoutDataComment.add(vLayoutDataReply);
						model.getReplies().forEach(e->{
							vLayoutDataReply.add(createLayoutMessed(e));
						});
					}

				});


				if(outputTaskModel.getState().equals(StatusTaskEnum.DAHOANTHANH.getKey())) {
					btnSendReply.setEnabled(false);
					spReason.setVisible(true);
					txtComment.setReadOnly(true);
					btnAttachmentsComment.setEnabled(false);
					btnSendComment.setEnabled(false);
				}
			}
		} catch (Exception ex) {
			outputTaskModel = taskOutputModel;
			taskOutputModel.getComments().stream().forEach(model->{
				listTaskComment.add(model);
			});

			listTaskComment.stream().forEach(model->{
				vLayoutDataComment.add(createLayoutMessed(model));
				if(!model.getReplies().isEmpty()) {
					VerticalLayout vLayoutDataReply = new VerticalLayout();
					vLayoutDataReply.getStyle().setMarginLeft("20px");
					vLayoutDataReply.setWidth("90%");
					vLayoutDataComment.add(vLayoutDataReply);
					model.getReplies().forEach(e->{
						vLayoutDataReply.add(createLayoutMessed(e));
					});
				}

			});


			if(outputTaskModel.getState().equals(StatusTaskEnum.DAHOANTHANH.getKey())) {
				btnSendReply.setEnabled(false);
				spReason.setVisible(true);
				txtComment.setReadOnly(true);
				btnAttachmentsComment.setEnabled(false);
				btnSendComment.setEnabled(false);
			}
		}
	}


	@Override
	protected void onAttach(AttachEvent attachEvent) {
		taskCommentBroadcaster = TaskCommentBroadcaster.register(newComment ->{
			ui.access(()->{
				if(!newComment.isEmpty() && newComment.get(0).getIdTask().equals(idTask)) {
					vLayoutDataComment.removeAll();
					for(TaskCommentModel taskCommentModel : newComment) {
						vLayoutDataComment.add(createLayoutMessed(taskCommentModel));
						if(!taskCommentModel.getReplies().isEmpty()) {
							VerticalLayout vLayoutDataReply = new VerticalLayout();
							vLayoutDataReply.getStyle().setMarginLeft("20px");
							vLayoutDataReply.setWidth("90%");
							vLayoutDataComment.add(vLayoutDataReply);
							for(TaskCommentModel taskReply : taskCommentModel.getReplies()) {
								vLayoutDataReply.add(createLayoutMessed(taskReply));
							}
						}
					}
				}
			});
		});
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
		taskCommentBroadcaster.remove();
		taskCommentBroadcaster = null;
	}

	private void loadLayoutFirstComment() {
		hLayoutComment.removeAll();
		avatarComment.getStyle().setMargin("auto");

		txtComment.setWidthFull();
		txtComment.setPlaceholder("Nhập bình luận mới");

		btnSendComment.addThemeVariants(ButtonVariant.LUMO_TERTIARY);


		btnAttachmentsComment.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

		multiSelectCmbComment.setWidthFull();
		multiSelectCmbComment.addThemeVariants(MultiSelectComboBoxVariant.LUMO_SMALL);
		multiSelectCmbComment.setVisible(false);

		hLayoutComment.add(avatarComment,txtComment,btnAttachmentsComment,btnSendComment);
		hLayoutComment.setWidthFull();
		hLayoutComment.expand(txtComment);

	}


	private Component createLayoutReply(TaskCommentModel taskCommentModel,Runnable run) {
		VerticalLayout vLayoutReply = new VerticalLayout();
		HorizontalLayout hLayoutReply = new HorizontalLayout();
		avatarReply.getStyle().setMargin("auto");

		txtReply.setWidthFull();
		txtReply.setPlaceholder("Nhập câu trả lời bình luận");
		txtReply.focus();

		txtReply.addValueChangeListener(e->{
			if(!txtReply.getValue().isBlank()) {
				btnSendReply.addClickShortcut(Key.ENTER);
			}else {
				btnSendComment.addClickShortcut(Key.PAUSE);
				btnSendReply.addClickShortcut(Key.PAUSE);
			}
		});

		btnSendReply.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnSendReply.addClickListener(e->{
			listIdAttachment = new ArrayList<>();
			uploadFile(multiSelectCmbReply.getListDataView().getItems().toList());
			comment(txtReply.getValue(), listIdAttachment, true,taskCommentModel.getId());
			vLayoutReply.removeAll();
			btnSendComment.addClickShortcut(Key.ENTER);
			btnSendReply.addClickShortcut(Key.GO_HOME);
			run.run();
			vLayoutReply.setHeight("0");
		});

		btnAttachmentReply.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

		multiSelectCmbReply.addThemeVariants(MultiSelectComboBoxVariant.LUMO_SMALL);
		multiSelectCmbReply.setWidthFull();
		multiSelectCmbReply.setVisible(false);

		hLayoutReply.setWidthFull();
		hLayoutReply.expand(txtReply);
		hLayoutReply.add(avatarReply,txtReply,btnAttachmentReply,btnSendReply);

		vLayoutReply.setWidthFull();
		vLayoutReply.add(hLayoutReply,multiSelectCmbReply);

		return vLayoutReply;
	}

	private Component createLayoutMessed(TaskCommentModel taskCommentModel) {
		VerticalLayout vLayoutGeneral = new VerticalLayout();
		HorizontalLayout hlayout = new HorizontalLayout();
		Avatar avatar = new Avatar();
		avatar.getStyle().setMargin("auto");

		VerticalLayout vLayout = new VerticalLayout();
		String creatorText = taskCommentModel.getCreator() == null ? "Đang cập nhật" : taskCommentModel.getCreator().getOrganizationName() + " ("+taskCommentModel.getCreator().getOrganizationUserName()+")";
		Span spanName = new Span(creatorText);
		spanName.getStyle().setFontWeight(600);

		Span spanDrs = new Span(taskCommentModel.getMessage());
		spanDrs.getStyle().setMarginLeft("7px");


		HorizontalLayout hLayoutButton = new HorizontalLayout();
		ButtonTemplate btnReplay = new ButtonTemplate("Trả lời");
		btnReplay.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnReplay.setHeight("13px");
		if(outputTaskModel.getState().equals(StatusTaskEnum.DAHOANTHANH.getKey())) {
			btnReplay.setEnabled(false);
		}

		btnReplay.addClickListener(e->{
			Component componentReply = createLayoutReply(taskCommentModel, ()->{
				vLayoutGeneral.removeAll();
				btnSendReply = new ButtonTemplate(FontAwesome.Solid.PAPER_PLANE.create());
			});
			vLayoutGeneral.add(componentReply);
			btnSendComment.addClickShortcut(Key.ARROW_UP);
			btnSendReply.addClickShortcut(Key.ENTER);
		});


		Span spanDate = new Span(LocalDateUtil.dfDate.format(taskCommentModel.getCreatedTime()));


		ButtonTemplate btnAttachmentfile = new ButtonTemplate("Đính kèm ("+ taskCommentModel.getAttachments().size()+")");
		btnAttachmentfile.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnAttachmentfile.setHeight("13px");

		if(taskCommentModel.getAttachments().isEmpty()) {
			hLayoutButton.add(btnReplay,spanDate);
		}else {
			hLayoutButton.add(btnReplay,spanDate,btnAttachmentfile);
		}

		btnAttachmentfile.addClickListener(e->{
			openDialogViewAttachment(taskCommentModel.getAttachments());
		});

		vLayout.add(spanName,spanDrs,hLayoutButton);
		vLayout.setSpacing(false);
		vLayout.getStyle().setPadding("10px").setBorderRadius("20px").setBackground("rgb(240, 242, 245)");

		hlayout.add(avatar,vLayout);
		vLayoutGeneral.add(hlayout);

		return vLayoutGeneral;

	}

	private void comment(String message,List<String> listAttachments,boolean repLy,String idParent) {
		BelongOrganizationModel belongOrganizationModel = SessionUtil.getOrg();
		UserAuthenticationModel userAuthenticationModel = SessionUtil.getUser();
		ApiCommentModel apiCommentModel = new ApiCommentModel();
		apiCommentModel.setMessage(message);
		apiCommentModel.setAttachments(listAttachments);
		ApiCreatorActionModel apiCreatorActionModel = new ApiCreatorActionModel();
		apiCreatorActionModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
		apiCreatorActionModel.setOrganizationName(belongOrganizationModel.getOrganizationName());
		apiCreatorActionModel.setOrganizationUserId(userAuthenticationModel.getId());
		apiCreatorActionModel.setOrganizationUserName(userAuthenticationModel.getFullName());
		apiCommentModel.setCreator(apiCreatorActionModel);
		if(repLy) {
			doReplyComment(idParent,apiCommentModel);
		}else {
			doComment(apiCommentModel);
		}
	}

	private void doComment(ApiCommentModel apiCommentModel) {
		ApiResultResponse<Object> comment = ApiActionService.doComment(idTask, apiCommentModel);
		if(comment.isSuccess()) {
			ApiResultResponse<ApiOutputTaskModel> data = ApiTaskService.getAtask(idTask);
			if(data.isSuccess()) {
				List<TaskCommentModel> listTaskCommentModels = new ArrayList<TaskCommentModel>();
				data.getResult().getComments().forEach(model->{
					TaskCommentModel taskCommentModel = new TaskCommentModel(model);
					taskCommentModel.setIdTask(idTask);
					listTaskCommentModels.add(taskCommentModel);
				});
				
				TaskCommentBroadcaster.broadcast(listTaskCommentModels);
			}

		}else {
			NotificationTemplate.warning(comment.getMessage());
		}

	}

	private void doReplyComment(String idParent,ApiCommentModel apiCommentModel) {
		ApiResultResponse<Object> reply = ApiActionService.doReplyComment(idTask, idParent, apiCommentModel);
		if(reply.isSuccess()) {
			txtReply.clear();
			multiSelectCmbReply.clear();
			ApiResultResponse<ApiOutputTaskModel> data = ApiTaskService.getAtask(idTask);
			if(data.isSuccess()) {
				List<TaskCommentModel> listTaskCommentModels = new ArrayList<TaskCommentModel>();
				data.getResult().getComments().forEach(model->{
					TaskCommentModel taskCommentModel = new TaskCommentModel(model);
					taskCommentModel.setIdTask(idTask);
					listTaskCommentModels.add(taskCommentModel);
				});
				TaskCommentBroadcaster.broadcast(listTaskCommentModels);
			}
		}
	}

	private void openDialogUploadFile(boolean checkReply) {
		DialogTemplate dialogTemplate = new DialogTemplate("Thêm đính kèm");

		UploadModuleBasic uploadModuleBasic = new UploadModuleBasic();
		uploadModuleBasic.initUpload();

		dialogTemplate.getBtnSave().setText("Thêm");
		dialogTemplate.getBtnSave().addClickListener(e->{

			if(checkReply) {
				listDataFileReply = new ArrayList<>();
				listDataFileReply = uploadModuleBasic.getListFileUpload();
				loadMultiCmbReply();

			}else {
				listDataFileComment = new ArrayList<>();
				listDataFileComment = uploadModuleBasic.getListFileUpload();
				loadMultiCmbComment();
			}

			fireEvent(new ClickEvent(this,false));
			dialogTemplate.close();
		});
		dialogTemplate.add(uploadModuleBasic);
		dialogTemplate.setHeight("60%");
		dialogTemplate.setWidth("60%");
		dialogTemplate.open();
	}

	private void openDialogViewAttachment(List<String> listAttachment) {
		DialogTemplate dialogTemplate = new DialogTemplate("DANH SÁCH ĐÍNH KÈM");
		ActtachmentForm acttachmentForm = new ActtachmentForm(listAttachment, true);
		dialogTemplate.add(acttachmentForm);
		dialogTemplate.setWidth("60%");
		dialogTemplate.setHeightFull();
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.open();
	}

	private void loadMultiCmbComment() {
		multiSelectCmbComment.setItems(listDataFileComment);
		multiSelectCmbComment.setItemLabelGenerator(UploadModuleDataModel::getFileName);
		multiSelectCmbComment.setVisible(true);
		multiSelectCmbComment.select(listDataFileComment);
	}

	private void loadMultiCmbReply() {
		multiSelectCmbReply.setItems(listDataFileReply);
		multiSelectCmbReply.setItemLabelGenerator(UploadModuleDataModel::getFileName);
		multiSelectCmbReply.setVisible(true);
		multiSelectCmbReply.select(listDataFileReply);
	}

	private void uploadFile(List<UploadModuleDataModel> listFile) {
		BelongOrganizationModel belongOrganizationModel = SessionUtil.getOrg();
		UserAuthenticationModel userAuthenticationModel = SessionUtil.getUser();
		for(UploadModuleDataModel uploadModuleDataModel : listFile) {
			File file = new File(uploadModuleDataModel.getFileName());
			try {
				FileUtils.copyInputStreamToFile(uploadModuleDataModel.getInputStream(), file);
				ApiInputMediaModel apiInputMediaModel = new ApiInputMediaModel();
				apiInputMediaModel.setFile(new FileSystemResource(file));
				apiInputMediaModel.setDescription(uploadModuleDataModel.getDescription());
				apiInputMediaModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
				apiInputMediaModel.setOrganizationName(belongOrganizationModel.getOrganizationName());
				apiInputMediaModel.setOrganizationUserId(userAuthenticationModel.getId());
				apiInputMediaModel.setOrganizationUserName(userAuthenticationModel.getFullName());
				doCreateFile(apiInputMediaModel);
				FileUtils.delete(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void doCreateFile(ApiInputMediaModel apiModel) {
		try {
			ApiResultResponse<ApiMediaModel> data = ApiMediaService.createFile(apiModel);
			if(data.isSuccess()) {
				listIdAttachment.add(data.getResult().getId());

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
