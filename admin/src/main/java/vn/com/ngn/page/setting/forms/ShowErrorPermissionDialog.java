package vn.com.ngn.page.setting.forms;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.utils.components.DialogTemplate;

public class ShowErrorPermissionDialog extends DialogTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	public ShowErrorPermissionDialog() {
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.setHeaderTitle("Lỗi!!! Thông báo");
		this.setWidth("40%");
		this.add(createLayout());
		this.setCloseOnOutsideClick(false);
		this.setCloseOnEsc(false);
		this.getFooter().removeAll();
		this.open();
	}
	
	public void configComponent() {
		this.getBtnClose().addClickListener(e->this.close());
	}
	
	private VerticalLayout createLayout() {
		VerticalLayout vLayout = new VerticalLayout();
		
		Html html = new Html(""
				+ "<div style='display:flex,flex-direction: column'>"
				+ "<h3>Bạn không có quyền quản trị trong đơn vị này, vui lòng chọn đơn vị khác hoặc liên hệ với quản trị viên</h3>"
				+ "<hr>"
//				+ "<img loading='lazy' src='https://media1.tenor.com/images/d6a87bc63b7c4e449d8b3836f4aea8b4/tenor.gif?itemid=10889198'/>"
				+ "<p>Vui lòng liên hệ quản trị viên để thêm quyền quản lý đơn vị</p>"
//				+ "<img style='width:200px; ,margin: auto;' src='./images/warning.png'/>"
				+ "</div>"
				+ "");
		
		vLayout.setSizeFull();
		vLayout.add(html);
		return vLayout;
	}

}
