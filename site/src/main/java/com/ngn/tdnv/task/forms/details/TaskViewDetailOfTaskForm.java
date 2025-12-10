package com.ngn.tdnv.task.forms.details;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.flowingcode.vaadin.addons.fontawesome.FontAwesome.Solid.Icon;
import com.ngn.api.doc.ApiDocService;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.utils.ApiKeyValueModel;
import com.ngn.interfaces.FormInterface;
import com.ngn.tdnv.task.enums.StatusTaskEnum;
import com.ngn.tdnv.task.models.TaskOutputModel;
import com.ngn.tdnv.task.models.TaskProcessModel;
import com.ngn.utils.components.ActtachmentForm;
import com.ngn.utils.components.MenuBarTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style.Display;
import com.vaadin.flow.dom.Style.FlexDirection;
import com.vaadin.flow.dom.Style.FlexWrap;

public class TaskViewDetailOfTaskForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private boolean isLayoutMobile = false;
	private VerticalLayout vLayout = new VerticalLayout();
	private VerticalLayout vLayoutProcess = new VerticalLayout();
	private HorizontalLayout hLayoutAttachmentAndDiscuss = new HorizontalLayout();
	private MenuBarTemplate menuBarBell = new MenuBarTemplate();
	private MenuBarTemplate menuBarRate = new MenuBarTemplate();
	private MenuBarTemplate menuBarReverse = new MenuBarTemplate();
	private MenuBarTemplate menuBarRedo = new MenuBarTemplate();
	private MenuBarTemplate menuBarPedding = new MenuBarTemplate();
	private MenuBarTemplate menuBarRefuse = new MenuBarTemplate();
	private TaskCommentForm taskCommentForm = new TaskCommentForm(null);

	private List<Pair<String, String>> listPriority = new ArrayList<Pair<String,String>>();
	private TaskOutputModel outputTaskModel;
	private boolean isSupport;
	public TaskViewDetailOfTaskForm(TaskOutputModel outputTaskModel,boolean isOwner,boolean isAssignee,boolean isSupport,boolean isFollow) {
		this.outputTaskModel = outputTaskModel;
		this.isSupport = isSupport;
		checkLayoutMobile();
		loadData();
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.add(vLayout,vLayoutProcess,hLayoutAttachmentAndDiscuss);
		vLayout.getStyle().setBoxShadow("rgba(0, 0, 0, 0.16) 0px 1px 4px").setPadding("10px").setBorderRadius("10px");
		createLayout();
	}

	@Override
	public void configComponent() {

	}
	
	private void checkLayoutMobile() {
		UI.getCurrent().getPage().retrieveExtendedClientDetails(e->{
			if(e.getScreenWidth() < 768) {
				isLayoutMobile = true;
			}
		});
	}

	private void loadData() {
		listPriority = new ArrayList<Pair<String,String>>();
		try {
			ApiResultResponse<List<ApiKeyValueModel>> dataOfPriority = ApiDocService.getPriority();
			if(dataOfPriority.isSuccess()) {
				dataOfPriority.getResult().stream().forEach(model->{
					listPriority.add(Pair.of(model.getKey(),model.getName()));
				});
			}	
		} catch (Exception e) {
			listPriority.add(Pair.of("thuong","Thường"));
			listPriority.add(Pair.of("khan","Khẩn"));
			listPriority.add(Pair.of("hoatoc","Hỏa tốc"));
		}
	}

	private void createLayout() {
		vLayout.removeAll();
		String width = "150px";
		
		
		HorizontalLayout hLayoutHeader = new HorizontalLayout();
		hLayoutHeader.setWidthFull();
		hLayoutHeader.setAlignItems(Alignment.CENTER);
		hLayoutHeader.getStyle().setBorderBottom("1px solid #eff2f4").setPaddingBottom("15px");
		
		Icon icon = FontAwesome.Solid.FILE_ALT.create();
		icon.setSize("14px");
		
		H4 header = new H4("THÔNG TIN NHIỆM VỤ");
		hLayoutHeader.add(icon,header);

		vLayout.add(hLayoutHeader);

		vLayout.add(createLayoutKeyAndValue("Tiêu đề: ", outputTaskModel.getTitle(), 
						width, StatusTaskEnum.toCheckQuaHanVaSapQuaHan(outputTaskModel.getStatus().toString(), outputTaskModel.checkOverDue())),
				
				createLayoutKeyAndValue("Nội dung: ", outputTaskModel.getDescription(), 
						width, StatusTaskEnum.toCheckQuaHanVaSapQuaHan(outputTaskModel.getStatus().toString(), outputTaskModel.checkOverDue())),
				
				createLayoutKeyAndValue("Ngày giao: ", outputTaskModel.getCreateTimeText(), width, width),
				
				createLayoutKeyAndValue("Hạn xử lý: ", outputTaskModel.getEndTimeText(), width, width),
				
				createLayoutKeyAndValue("Thời gian còn lại: ", outputTaskModel.getCalculateTimeRemainText(), 
						width, StatusTaskEnum.toCheckQuaHanVaSapQuaHan(outputTaskModel.getStatus().toString(), outputTaskModel.checkOverDue())),
				
				createLayoutKeyAndValue("Thời gian đã giao: ", outputTaskModel.getCalculateCreateTimeText(), width, width),
				createLayoutKeyAndValue("Ngày thực hiện", outputTaskModel.getStartTimeText(), width, width),
				createLayoutKeyAndValue("Thời gian đã thực hiện: ", outputTaskModel.getCalculateStartTimeText(), width, width));


		HorizontalLayout hLayoutButton = new HorizontalLayout();
		Button btnPriority = new Button();

		for(Pair<String, String> p : listPriority) {
			if(p.getKey().equals(outputTaskModel.getPriority())) {
				btnPriority = new Button("Độ khẩn: "+p.getRight(),FontAwesome.Solid.FLAG.create());
				switch(outputTaskModel.getPriority()) {
				case "thuong":
					break;
				case "khan":
					btnPriority.getStyle().setColor("#c6a100");
					break;
				case "hoatoc":
					btnPriority.addThemeVariants(ButtonVariant.LUMO_ERROR);
					break;
				}
			}
		}

		btnPriority.addThemeVariants(ButtonVariant.LUMO_SMALL);

		menuBarBell.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);
		menuBarBell.removeAll();
		MenuItem menuItemBell = menuBarBell.addItem(createLayoutIconContent(FontAwesome.Solid.BELL.create(), "Nhắc nhở ("+outputTaskModel.getReminds().size()+")", "rgb(255 221 221 / 68%)", "rgb(104 18 18)"));
		SubMenu subMenuBell = menuItemBell.getSubMenu();
		if(!outputTaskModel.getReminds().isEmpty()) {
			TaskViewRemindForm taskViewRemindForm = new TaskViewRemindForm(outputTaskModel.getReminds());
			subMenuBell.add(taskViewRemindForm);
		}

		menuBarRate.removeAll();
		menuBarRate.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);
		String stars = outputTaskModel.getRating() == null ? "Đánh giá (Chưa đánh giá)" : "Đánh giá ("+outputTaskModel.getRating().getStar()+" sao)";
		MenuItem menuRateItem = menuBarRate.addItem(createLayoutIconContent(FontAwesome.Solid.STAR.create(), stars, "rgb(239 245 222 / 68%)", "rgb(102 105 2"));
		SubMenu subRate = menuRateItem.getSubMenu();
		if(outputTaskModel.getRating() != null) {
			TaskViewRatingForm taskViewRatingForm = new TaskViewRatingForm(outputTaskModel.getRating());
			subRate.add(taskViewRatingForm);
		}

		menuBarReverse.removeAll();
		menuBarReverse.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);
		menuBarReverse.setVisible(false);
		MenuItem menuItemReverse = menuBarReverse.addItem(createLayoutIconContent(FontAwesome.Solid.ARROW_ROTATE_BACK.create(), "Nhiệm vụ triệu hồi", "rgb(255 221 240 / 68%)", "rgb(104 18 93)"));
		SubMenu subMenuReverse = menuItemReverse.getSubMenu();
		if(outputTaskModel.getReverse() != null) {
			TaskViewReverseForm taskViewReverseForm = new TaskViewReverseForm(outputTaskModel.getReverse());
			subMenuReverse.add(taskViewReverseForm);
			menuBarReverse.setVisible(true);
		}

		menuBarRedo.removeAll();
		menuBarRedo.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);
		menuBarRedo.setVisible(false);
		MenuItem menuItemRedo = menuBarRedo.addItem(createLayoutIconContent(FontAwesome.Solid.REPEAT.create(), "Nhiệm vụ thực hiện lại", "rgb(221 252 255 / 68%)", "rgb(12 60 84)"));
		SubMenu subMenuRedo = menuItemRedo.getSubMenu();
		if(outputTaskModel.getRedo() != null) {
			TaskViewRedoForm taskViewRedoForm = new TaskViewRedoForm(outputTaskModel.getRedo());
			subMenuRedo.add(taskViewRedoForm);
			menuBarRedo.setVisible(true);
		}

		menuBarPedding.removeAll();
		menuBarPedding.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);
		menuBarPedding.setVisible(false);
		MenuItem menuItemPedding = menuBarPedding.addItem(createLayoutIconContent(FontAwesome.Solid.STOP.create(), "Nhiệm vụ đang tạm hoãn", "rgb(221 252 255 / 68%)", "rgb(12 60 84)"));
		SubMenu subMenuPendding = menuItemPedding.getSubMenu();
		if(outputTaskModel.getState().equals(StatusTaskEnum.TAMHOAN.getKey())) {
			menuBarPedding.setVisible(true);
			TaskViewPenddingForm taskViewPenddingForm = new TaskViewPenddingForm(outputTaskModel.getPending());
			subMenuPendding.add(taskViewPenddingForm);
		}
		
		menuBarRefuse.removeAll();
		menuBarRefuse.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);
		menuBarRefuse.setVisible(false);
		MenuItem menuItemRefuse = menuBarRefuse.addItem(createLayoutIconContent(FontAwesome.Solid.X.create(), "Nhiệm vụ từ chối thực hiện", "rgb(255 221 221 / 68%)", "rgb(104 18 18)"));
		SubMenu subMenuRefuse = menuItemRefuse.getSubMenu();
		if(outputTaskModel.getState().equals(StatusTaskEnum.TUCHOITHUCHIEN.getKey())) {
			menuBarRefuse.setVisible(true);
			menuBarRate.setVisible(false);
			TaskViewRefuseForm taskViewRefuse = new TaskViewRefuseForm(outputTaskModel.getRefuse());
			subMenuRefuse.add(taskViewRefuse);
		}
		
		hLayoutButton.setWidthFull();
		hLayoutButton.getStyle().setMarginTop("10px");
		hLayoutButton.add(btnPriority,menuBarBell,menuBarRate,menuBarReverse,menuBarRedo,menuBarPedding,menuBarRefuse);
		if(isLayoutMobile) {
			hLayoutButton.getStyle().setDisplay(Display.FLEX).setFlexWrap(FlexWrap.WRAP);
		}


		vLayout.add(hLayoutButton);

		createLayoutProcessTask();
		createLayoutAttachmentAndDiscuss(outputTaskModel.getAttachments());
	}

	private void createLayoutProcessTask() {
		vLayoutProcess.removeAll();
		
		HorizontalLayout hLayoutHeaderProccess = new HorizontalLayout();
		hLayoutHeaderProccess.setWidthFull();
		hLayoutHeaderProccess.setAlignItems(Alignment.CENTER);
		hLayoutHeaderProccess.getStyle().setBorderBottom("1px solid #eff2f4").setPaddingBottom("15px");
		
		Icon icon = FontAwesome.Solid.BARS_PROGRESS.create();
		icon.setSize("14px");
		
		H4 header = new H4("TIẾN ĐỘ NHIỆM VỤ");
		hLayoutHeaderProccess.add(icon,header);
		
		vLayoutProcess.add(hLayoutHeaderProccess);

		TaskProcessModel taskProcessModel = outputTaskModel.getProcesses().isEmpty() ? new TaskProcessModel() : outputTaskModel.getProcesses().get(0);
		TaskProgressViewForm taskProgressViewForm = new TaskProgressViewForm(outputTaskModel.getId(), taskProcessModel, isSupport, outputTaskModel.getState().toString(),true);
		vLayoutProcess.add(taskProgressViewForm);
		vLayoutProcess.getStyle().setBoxShadow("rgba(0, 0, 0, 0.16) 0px 1px 4px").setPadding("10px").setBorderRadius("10px");
	}


	private void createLayoutAttachmentAndDiscuss(List<Object> listIdAttachment) {
		hLayoutAttachmentAndDiscuss.removeAll();
		hLayoutAttachmentAndDiscuss.setWidthFull();
		List<String> listAttachment = new ArrayList<String>();
		for(Object object : listIdAttachment) {
			listAttachment.add(object.toString());
		}

		VerticalLayout vLayoutattachment = new VerticalLayout();
		ActtachmentForm acttachmentForm = new ActtachmentForm(listAttachment, true);
		acttachmentForm.setSizeFull();
		acttachmentForm.setSpanTitle("");
		
		HorizontalLayout hLayoutHeaderAttachment = new HorizontalLayout();
		hLayoutHeaderAttachment.setWidthFull();
		hLayoutHeaderAttachment.setAlignItems(Alignment.CENTER);
		hLayoutHeaderAttachment.getStyle().setBorderBottom("1px solid #eff2f4").setPaddingBottom("15px");
		
		Icon iconAttachment = FontAwesome.Solid.PAPERCLIP.create();
		iconAttachment.setSize("14px");
		
		H4 headerAttachment = new H4("DANH SÁCH ĐÍNH KÈM TỪ ĐƠN VỊ GIAO");
		hLayoutHeaderAttachment.add(iconAttachment,headerAttachment);

		vLayoutattachment.add(hLayoutHeaderAttachment);

		vLayoutattachment.setWidth("50%");
		vLayoutattachment.getStyle().setBoxShadow("rgba(0, 0, 0, 0.16) 0px 1px 4px").setPadding("10px").setBorderRadius("10px");
		vLayoutattachment.add(acttachmentForm);

		VerticalLayout vLayoutTaskComment = new VerticalLayout();
		taskCommentForm = new TaskCommentForm(outputTaskModel.getId());
		taskCommentForm.loadData(outputTaskModel);
		taskCommentForm.setSizeFull();
		taskCommentForm.getTxtComment().setAutofocus(false);
		
		HorizontalLayout hLayoutHeaderComment = new HorizontalLayout();
		hLayoutHeaderComment.setWidthFull();
		hLayoutHeaderComment.setAlignItems(Alignment.CENTER);
		hLayoutHeaderComment.getStyle().setBorderBottom("1px solid #eff2f4").setPaddingBottom("15px");
		
		Icon icon = FontAwesome.Solid.COMMENT_ALT.create();
		icon.setSize("14px");
		
		H4 header = new H4("TRAO ĐỔI THÔNG TIN");
		hLayoutHeaderComment.add(icon,header);
		vLayoutTaskComment.add(hLayoutHeaderComment);

		vLayoutTaskComment.setWidth("50%");
		vLayoutTaskComment.add(taskCommentForm);
		vLayoutTaskComment.getStyle().setBoxShadow("rgba(0, 0, 0, 0.16) 0px 1px 4px").setPadding("10px").setBorderRadius("10px");


		hLayoutAttachmentAndDiscuss.add(vLayoutattachment,vLayoutTaskComment);
		if(isLayoutMobile) {
			vLayoutattachment.setWidthFull();
			vLayoutTaskComment.setWidthFull();
			hLayoutAttachmentAndDiscuss.removeAll();
			hLayoutAttachmentAndDiscuss.add(vLayoutTaskComment,vLayoutattachment);
			hLayoutAttachmentAndDiscuss.getStyle().setDisplay(Display.FLEX).setFlexDirection(FlexDirection.COLUMN);
		}
	}


	private Component createLayoutKeyAndValue(String key,String value,String widthHeader,String style) {
		HorizontalLayout hlayout = new HorizontalLayout();

		Span spHeader = new Span(key);
		spHeader.setWidth(widthHeader);
		spHeader.getStyle().setFontWeight(600).setFlexShrink("0");

		Span spValue = new Span(value);
		if(style != null) {
			spValue.getStyle().setColor(style);
		}

		hlayout.setWidthFull();
		hlayout.add(spHeader,spValue);
		hlayout.getStyle().setBorderBottom("1px solid #c3c3c3").setPadding("5px");
		return hlayout;
	}

	private Span createLayoutIconContent(Icon icon,String content,String background,String color) {
		Span sp = new Span();
		sp.add(icon,new Text(content));
		sp.getStyle().setBackground(background).setColor(color);
		icon.setSize("15px");
		sp.getStyle().setDisplay(com.vaadin.flow.dom.Style.Display.FLEX)
		.setFlexDirection(com.vaadin.flow.dom.Style.FlexDirection.ROW).setAlignItems(com.vaadin.flow.dom.Style.AlignItems.CENTER)
		.set("gap", "10px").setFontSize("13px").setPadding("6px").setBorderRadius("5px").setFontWeight(600).setHeight("16px").setCursor("pointer");

		return sp;
	}
	

	//Xem danh sách đính kèm
	//	private void openDialogViewAttachment(List<Object> listIdAttachment) {
	//		DialogTemplate dialogTemplate = new DialogTemplate("DANH SÁCH ĐÍNH KÈM");
	//		List<String> listAttachment = new ArrayList<String>();
	//		for(Object object : listIdAttachment) {
	//			listAttachment.add(object.toString());
	//		}
	//		ActtachmentForm acttachmentForm = new ActtachmentForm(listAttachment, true);
	//		dialogTemplate.add(acttachmentForm);
	//		dialogTemplate.setWidth("60%");
	//		dialogTemplate.setHeightFull();
	//		dialogTemplate.getFooter().removeAll();
	//		dialogTemplate.setLayoutMobile();
	//		dialogTemplate.open();
	//	}

}
