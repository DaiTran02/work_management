package vn.com.ngn.page.user.form;

import java.io.IOException;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.organization.ApiGroupOrganizationExpandsModel;
import vn.com.ngn.api.organization.ApiOrganizationModel;
import vn.com.ngn.api.organization.ApiOrganizationService;
import vn.com.ngn.api.organization.ApiRoleOrganizationExpandsModel;
import vn.com.ngn.api.user.ApiUserModel;
import vn.com.ngn.api.user.ApiUserService;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.user.model.UserModel;
import vn.com.ngn.utils.LocalDateUtils;
import vn.com.ngn.utils.components.ButtonTemplate;
import vn.com.ngn.utils.components.VerticalLayoutTemplate;

public class DetailFirstReviewForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private UserModel userModel = new UserModel();

	private String idUser;
	public DetailFirstReviewForm(String idUser) {
		this.idUser = idUser;
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setWidthFull();
		

	}

	@Override
	public void configComponent() {

	}

	public void loadData() {
		try {
			ApiResultResponse<ApiUserModel> data = ApiUserService.getaUser(idUser);
			userModel = new UserModel(data.getResult());
			System.out.println(userModel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.removeAll();
		this.add(createLayout());
	}

	private Component createLayout() {
		VerticalLayout vLayout = new VerticalLayout();
		H3 header = new H3("Thông tin đơn vị mà người dùng đã tự chọn");
		header.getStyle().setMargin("auto");
		vLayout.add(header,new Hr());
		ApiOrganizationModel organizationModel = getOrg(userModel.getFirstReview() == null ? "" : userModel.getFirstReview().getChoiceOrganizationId());

		ApiGroupOrganizationExpandsModel groupOrganizationExpandsModel = null;

		ApiRoleOrganizationExpandsModel roleOrganizationExpandsModel = null;

		if(userModel.getFirstReview().getChoiceOrganizationGroupId() != null) {
			groupOrganizationExpandsModel = getGroup(userModel.getFirstReview().getChoiceOrganizationId(), userModel.getFirstReview().getChoiceOrganizationGroupId().toString());
		}

		if(userModel.getFirstReview().getChoiceOrganizationRoleId() != null) {
			roleOrganizationExpandsModel = getRole(userModel.getFirstReview().getChoiceOrganizationId(), userModel.getFirstReview().getChoiceOrganizationRoleId().toString());
		}

		vLayout.add(createLayoutKeyValue("Đơn vị đã chọn: ", organizationModel.getName(),organizationModel.getDescription(), null),
				
				createLayoutKeyValue("Nhóm đã chọn:", groupOrganizationExpandsModel == null ? "Không có" : groupOrganizationExpandsModel.getName(),
						groupOrganizationExpandsModel == null ? "": groupOrganizationExpandsModel.getDescription(), null),
				
				createLayoutKeyValue("Vai trò đã chọn:", roleOrganizationExpandsModel == null ? "Không có" : roleOrganizationExpandsModel.getName(),
						roleOrganizationExpandsModel == null ? "" : roleOrganizationExpandsModel.getDescription(),null),
				createLayoutKeyValue("Ngày thực hiện:", LocalDateUtils.dfDateTime.format(userModel.getFirstReview().getCreatedTime()),"Ngày người dùng đăng nhập lần đầu", null));

		
		
		
		ButtonTemplate btnSubmit = new ButtonTemplate("Duyệt",FontAwesome.Solid.CHECK_TO_SLOT.create());
		btnSubmit.getStyle().set("margin-left", "auto");
		btnSubmit.addClickListener(e->{
			setReviewUser();
		});
		
		if(userModel.getFirstReview().isReviewed()) {
			btnSubmit.setIcon(FontAwesome.Regular.CHECK_CIRCLE.create());
			btnSubmit.setText("Đã duyệt");
			btnSubmit.setEnabled(false);
			vLayout.add(createLayoutKeyValue("Ngày duyệt: ", LocalDateUtils.dfDateTime.format(userModel.getFirstReview().getReviewedTime()), "Ngày quản trị duyệt", null));
		}
		
		vLayout.add(btnSubmit);

		vLayout.getStyle().setBoxShadow("rgba(99, 99, 99, 0.2) 0px 2px 8px 0px").set("border-radius", "10px");

		return vLayout;
	}

	private Component createLayoutKeyValue(String name,String value,String dsr,String style) {
		HorizontalLayout hLayoutKeyValue = new HorizontalLayout();
		Span spanName = new Span(name);

		spanName.getStyle().set("font-weight", "600").setWidth("135px");

		VerticalLayout vLayoutValue = new VerticalLayout();
		
		
		Span spanValue = new Span(value);
		if(style != null) {
			spanValue.getStyle().setColor(style);
		}
		
		Span spdsr = new Span(dsr);
		spdsr.getStyle().set("font-style", "italic").set("font-size", "12px");
		
		vLayoutValue.add(spanValue,spdsr);
		vLayoutValue.setSpacing(false);
		vLayoutValue.setPadding(false);
		
		hLayoutKeyValue.add(spanName,vLayoutValue);
		hLayoutKeyValue.setWidthFull();
		hLayoutKeyValue.getStyle().set("border-bottom", "1px solid #9fa0c5b3").set("padding-bottom", "10px");
		
		return hLayoutKeyValue;
	}

	private ApiOrganizationModel getOrg(String idOrg) {
		try {
			ApiResultResponse<ApiOrganizationModel> data = ApiOrganizationService.getOneOrg(idOrg);
			if(data.isSuscces()) {
				return data.getResult();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private ApiGroupOrganizationExpandsModel getGroup(String idOrg,String idGroup) {
		try {
			ApiResultResponse<ApiGroupOrganizationExpandsModel> data = ApiOrganizationService.getAGroup(idOrg, idGroup);
			if(data.isSuscces()) {
				return data.getResult();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;

	}

	private ApiRoleOrganizationExpandsModel getRole(String idOrg,String idRole) {
		try {
			ApiResultResponse<ApiRoleOrganizationExpandsModel> data = ApiOrganizationService.getOneRole(idOrg, idRole);
			if(data.isSuscces()) {
				return data.getResult();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void setReviewUser() {
		try {
			ApiResultResponse<Object> data = ApiUserService.setReviewOfFirstLogin(idUser);
			if(data.isSuscces()) {
				loadData();
				fireEvent(new ClickEvent(this, false));
			}
		} catch (Exception e) {
		}
	}

}
