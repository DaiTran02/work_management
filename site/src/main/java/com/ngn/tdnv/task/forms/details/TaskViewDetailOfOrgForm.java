package com.ngn.tdnv.task.forms.details;

import java.util.ArrayList;
import java.util.List;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.flowingcode.vaadin.addons.fontawesome.FontAwesome.Solid.Icon;
import com.ngn.interfaces.FormInterface;
import com.ngn.tdnv.task.models.TaskOutputModel;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBoxVariant;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style.AlignItems;
import com.vaadin.flow.dom.Style.Display;
import com.vaadin.flow.dom.Style.FlexDirection;

public class TaskViewDetailOfOrgForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private boolean isMobileLayout = false;
	private VerticalLayout vLayout = new VerticalLayout();
	
	private TaskOutputModel outputTaskModel;
	public TaskViewDetailOfOrgForm(TaskOutputModel outputTaskModel) {
		this.outputTaskModel = outputTaskModel;
		checkMobileLayout();
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.setWidthFull();
		this.getStyle().setPadding("0");
		vLayout.setWidthFull();
		vLayout.getStyle().setPadding("0");
		
		this.add(vLayout);
		createLayout();
		
	}

	@Override
	public void configComponent() {
		
	}
	
	private void checkMobileLayout() {
		UI.getCurrent().getPage().retrieveExtendedClientDetails(e->{
			if(e.getScreenWidth() < 768) {
				isMobileLayout = true;
			}
		});
	}
	
	private void createLayout() {
		vLayout.removeAll();
		HorizontalLayout hLayout1 = new HorizontalLayout();
		hLayout1.setWidthFull();
		
		VerticalLayout vLayoutOwner = new VerticalLayout();
		vLayoutOwner.setWidth("50%");
		vLayoutOwner.getStyle().setBorderRadius("10px").setBoxShadow("rgba(0, 0, 0, 0.16) 0px 1px 4px").setPadding("10px");
		
		HorizontalLayout hLayoutOwner = new HorizontalLayout();
		hLayoutOwner.setWidthFull();
		hLayoutOwner.setAlignItems(Alignment.CENTER);
		hLayoutOwner.getStyle().setBorderBottom("1px solid #eff2f4").setPaddingBottom("15px");
		Icon iconOwner = FontAwesome.Solid.USER_EDIT.create();
		iconOwner.setSize("14px");
		iconOwner.setColor("red");
		
		H4 headerOwner = new H4("ĐƠN VỊ CHỦ QUẢN");
		headerOwner.getStyle().setColor("red");
		hLayoutOwner.add(iconOwner,headerOwner);
		
		vLayoutOwner.add(hLayoutOwner);
		
		String widthOwner = "150px";
		
		vLayoutOwner.add(createLayoutKeyAndValue("Tên đơn vị giao: ", outputTaskModel.getOwner().getOrganizationName(), widthOwner, null),
				createLayoutKeyAndValue("Người chỉ đạo: ", outputTaskModel.getOwner().getOrganizationUserName().toString(), widthOwner, widthOwner),
				createLayoutKeyAndValue("Người giao nhiệm vụ: ", outputTaskModel.getAssistant().getOrganizationName() + " ("+outputTaskModel.getAssistant().getOrganizationUserName().toString()+")", widthOwner, widthOwner));
		
		
		VerticalLayout vLayoutAsignee = new VerticalLayout();
		vLayoutAsignee.setWidth("50%");
		vLayoutAsignee.getStyle().setBorderRadius("10px").setBoxShadow("rgba(0, 0, 0, 0.16) 0px 1px 4px").setPadding("10px");
		
		HorizontalLayout hLayoutAsignee = new HorizontalLayout();
		hLayoutAsignee.setWidthFull();
		hLayoutAsignee.setAlignItems(Alignment.CENTER);
		hLayoutAsignee.getStyle().setBorderBottom("1px solid #eff2f4").setPaddingBottom("15px");
		Icon iconAssignee = FontAwesome.Solid.USER_CHECK.create();
		iconAssignee.setSize("14px");
		iconAssignee.setColor("#00861d");
		
		H4 headerAssignee = new H4("ĐƠN VỊ XỬ LÝ");
		headerAssignee.getStyle().setColor("#00861d");
		
		hLayoutAsignee.add(iconAssignee,headerAssignee);
		
		vLayoutAsignee.add(hLayoutAsignee);
		vLayoutAsignee.add(createLayoutKeyAndValue("Tên đơn vị xử lý: ", outputTaskModel.getAssignee().getOrganizationName(), widthOwner, widthOwner),
				createLayoutKeyAndValue("Người được phân xử lý: ", outputTaskModel.getAssignee().getUserNameText(), widthOwner, widthOwner));
		
		
		
		VerticalLayout vLayoutSupportAndFollower = new VerticalLayout();
		vLayoutSupportAndFollower.setWidthFull();
		vLayoutSupportAndFollower.getStyle().setBorderRadius("10px").setBoxShadow("rgba(0, 0, 0, 0.16) 0px 1px 4px").setPadding("10px");
		
		HorizontalLayout hLayoutSupport = new HorizontalLayout();
		hLayoutSupport.setWidthFull();
		hLayoutSupport.setAlignItems(Alignment.CENTER);
		hLayoutSupport.getStyle().setBorderBottom("1px solid #eff2f4").setPaddingBottom("15px");
		Icon iconSupport = FontAwesome.Solid.USER_FRIENDS.create();
		iconSupport.setSize("14px");
		iconSupport.setColor("hsl(214deg 92.69% 38%)");
		
		H4 headerSupport = new H4("ĐƠN VỊ HỖ TRỢ VÀ THEO DÕI");
		headerSupport.getStyle().setColor("hsl(214deg 92.69% 38%)");
		
		hLayoutSupport.add(iconSupport,headerSupport);
		
		vLayoutSupportAndFollower.add(hLayoutSupport);
		
		String widthSupportAndFollower = "200px";
		if(isMobileLayout) {
			widthSupportAndFollower = "150px";
		}
		
		List<String> listSupport = new ArrayList<String>();
		if(outputTaskModel.getSupports().isEmpty()) {
			listSupport.add("Không có đơn vị hỗ trợ nào");
		}else {
			outputTaskModel.getSupports().stream().forEach(model->{
				if(model.getOrganizationUserId() != null) {
					listSupport.add(model.getOrganizationName()+" ("+model.getOrganizationUserName()+") ");
				}else {
					listSupport.add(model.getOrganizationName());
				}
			});
		}


		MultiSelectComboBox<String> multiSelectSupport = new MultiSelectComboBox<String>();
		multiSelectSupport.setReadOnly(true);
		multiSelectSupport.setItems(listSupport);
		multiSelectSupport.select(listSupport);
		multiSelectSupport.setWidth("100%");
		multiSelectSupport.addThemeVariants(MultiSelectComboBoxVariant.LUMO_SMALL);
		
		vLayoutSupportAndFollower.add(createLayoutKeyAndComponent("Đơn vị phối hợp ("+outputTaskModel.getSupports().size()+"):", multiSelectSupport, widthSupportAndFollower));
		
		List<String> listFollow = new ArrayList<String>();
		if(outputTaskModel.getFollowers() != null) {
			if(outputTaskModel.getFollowers().isEmpty()) {
				listFollow.add("Không có khối theo dõi nào");
			}else {
				outputTaskModel.getFollowers().stream().forEach(model->{
					if(model.getOrganizationUserId()!= null) {
						listFollow.add(model.getOrganizationName()+ " ("+model.getOrganizationUserName()+") ");
					}else {
						listFollow.add(model.getOrganizationName());
					}
				});
			}
		}else {
			listFollow.add("Không có khối theo dõi nào");
		}
		


		MultiSelectComboBox<String> multiSelectFollower = new MultiSelectComboBox<String>();
		multiSelectFollower.setReadOnly(true);
		multiSelectFollower.setItems(listFollow);
		multiSelectFollower.select(listFollow);
		multiSelectFollower.setWidth("85%");
		multiSelectFollower.addThemeVariants(MultiSelectComboBoxVariant.LUMO_SMALL);
		
		vLayoutSupportAndFollower.add(createLayoutKeyAndComponent("Đơn vị theo dõi ("+outputTaskModel.getFollowers().size()+"):", multiSelectFollower, widthSupportAndFollower));
		
		
		hLayout1.add(vLayoutOwner,vLayoutAsignee);
		
		vLayout.add(hLayout1,vLayoutSupportAndFollower);
		
		if(isMobileLayout) {
			vLayoutOwner.setWidthFull();
			vLayoutAsignee.setWidthFull();
			hLayout1.getStyle().setDisplay(Display.FLEX).setFlexDirection(FlexDirection.COLUMN);
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
	
	private Component createLayoutKeyAndComponent(String key,Component value, String widthHeader) {
		HorizontalLayout hlayout = new HorizontalLayout();

		Span spHeader = new Span(key);
		spHeader.setWidth(widthHeader);
		spHeader.getStyle().setFontWeight(600).setFlexShrink("0");

		value.getStyle().setWidth("100%");

		hlayout.setWidthFull();
		hlayout.add(spHeader,value);
		hlayout.getStyle().setBorderBottom("1px solid #c3c3c3").setPadding("5px").setAlignItems(AlignItems.CENTER).setPaddingTop("0");
		return hlayout;
	}

}
