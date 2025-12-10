package com.ngn.tdnv.doc.enumdoc;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.flowingcode.vaadin.addons.fontawesome.FontAwesome.Solid.Icon;
import com.ngn.utils.models.KeyValueModel;
import com.vaadin.flow.component.Component;

public class DocCategoryIcon {
	
	private KeyValueModel cateGory;
	public DocCategoryIcon(KeyValueModel cateGory) {
		this.cateGory = cateGory;
	}

	
	public Component iconAndTitleDoccateGory(boolean checkTaskIsAssignee) {
		Icon icon = null;
		if(cateGory != null) {
			switch(cateGory.getKey()) {
			case "CVDen": 
				icon = FontAwesome.Solid.MAIL_FORWARD.create();
				if(checkTaskIsAssignee) {
					icon.getStyle().setColor("rgb(23 126 8)");
					icon.setTooltipText(cateGory.getName() + " đã giao");
				}else {
					icon.getStyle().setColor("#d10000b8");
					icon.setTooltipText(cateGory.getName() + " chưa giao");
				}
				break;
			case "CVDi":
				icon = FontAwesome.Solid.REPLY.create();
				if(checkTaskIsAssignee) {
					icon.getStyle().setColor("rgb(23 126 8)");
					icon.setTooltipText(cateGory.getName() + " đã giao");
				}else {
					icon.getStyle().setColor("#d10000b8");
					icon.setTooltipText(cateGory.getName() + " chưa giao");
				}
				break;
			}
			icon.setSize("13px");
		}
		
		return icon;
	}
}
