package vn.com.ngn.page.setting.forms;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Background;
import com.vaadin.flow.theme.lumo.LumoUtility.BorderRadius;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Overflow;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;

import vn.com.ngn.api.auth.ApiAuthService;
import vn.com.ngn.api.auth.ApiSignInOrgModel;
import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.organization.ApiOrganizationModel;
import vn.com.ngn.api.organization.ApiOrganizationService;
import vn.com.ngn.page.user.UserView;
import vn.com.ngn.page.user.model.BelongOrganizationsModel;
import vn.com.ngn.utils.CheckPermissionUtil;
import vn.com.ngn.utils.SessionUtil;
import vn.com.ngn.utils.components.NotificationTemplate;

public class CardOrgForm extends ListItem{
	private static final long serialVersionUID = 1L;
	private BelongOrganizationsModel belongOrganizationModel;
	private boolean checkChangeOrg = false;
	public CardOrgForm(BelongOrganizationsModel belongOrganizationModel,boolean checkChangeOrg) {
		this.belongOrganizationModel = belongOrganizationModel;
		this.checkChangeOrg = checkChangeOrg;
		createLayout();
		
	}

	private void createLayout() {
		addClassNames(Background.CONTRAST_5, Display.FLEX, FlexDirection.COLUMN, AlignItems.START, Padding.MEDIUM,
				BorderRadius.LARGE);
		this.setWidth("250px");
		VerticalLayout vLayout = new VerticalLayout();



		Div div = new Div();
		div.setHeight("150px");
		div.addClassNames(Background.CONTRAST, Display.FLEX, AlignItems.CENTER, JustifyContent.CENTER,
				Margin.Bottom.MEDIUM, Overflow.HIDDEN, BorderRadius.MEDIUM, Width.FULL);

		Image image = new Image();
		image.setWidth("100%");
		image.setSrc("https://quanlyvanban.hanoi.gov.vn/qlvbdh/login/img/bg-login.svg");
		image.setAlt("Hinh nen");
		div.add(image);

		Avatar avatar = new Avatar(belongOrganizationModel.getOrganizationName());
		avatar.setWidth("70px");
		avatar.setHeight("70px");
		avatar.getStyle().setBackground("white");

		Button btnCheck = new Button(FontAwesome.Solid.CIRCLE_CHECK.create());
		btnCheck.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnCheck.setTooltipText("Bạn đang ở trong đơn vị này");
		btnCheck.getStyle().set("margin-left", "-32px").set("margin-top", "36px");

		HorizontalLayout hLayoutAvatar = new HorizontalLayout();
		if(checkChangeOrg) {
			if(SessionUtil.getOrgId().equals(belongOrganizationModel.getOrganizationId())) {
				hLayoutAvatar.add(avatar,btnCheck);
			}else {
				hLayoutAvatar.add(avatar);
			}

		}else {
			hLayoutAvatar.add(avatar);
		}
		hLayoutAvatar.getStyle().setMargin("auto").set("margin-bottom", "0").set("margin-top", "35p");




		Span header = new Span();
		header.addClassNames(FontSize.XLARGE, FontWeight.SEMIBOLD, AlignItems.CENTER);
		header.setText(belongOrganizationModel.getOrganizationName());
		header.getStyle().setMargin("auto");



		ApiOrganizationModel signInOrgModel = getOrg(belongOrganizationModel.getOrganizationId());
		System.out.println("Org ne: "+belongOrganizationModel.getOrganizationId());
		
		if(signInOrgModel == null) {
			Span spanDsr = new Span();
			spanDsr.addClassNames(FontSize.SMALL, TextColor.SECONDARY, AlignItems.CENTER);
			spanDsr.setText("Tài khoản không có quyền quản trị hệ thống");
			vLayout.add(header,createLayoutKeyValue("Đơn vị: ", belongOrganizationModel.getOrganizationName(),null),createLayoutKeyValue("Mô tả: ", "Tài khoản không có quyền quản trị hệ thống", null));
			this.addClickListener(e->{
				ShowErrorPermissionDialog showErrorPermissionDialog = new ShowErrorPermissionDialog();
				showErrorPermissionDialog.open();
				showErrorPermissionDialog.getBtnClose().addClickListener(ev->{

				});
			});
		}else {
			Span spanDsr = new Span();
			spanDsr.addClassNames(FontSize.SMALL, TextColor.SECONDARY, AlignItems.CENTER);
			spanDsr.setText("Mô tả: "+signInOrgModel.getDescription() == null ? "Đang cập nhật" : signInOrgModel.getDescription());


			this.addClickListener(e->{
				if(SessionUtil.getOrgId() != null) {
					if(SessionUtil.getOrgId().equals(belongOrganizationModel.getOrganizationId())) {
						NotificationTemplate.error("Hiện đang trong đơn vị");
					}else {
						
						String oldOrgId = SessionUtil.getOrgId();
						BelongOrganizationsModel oldBelongOrganizationsModel = SessionUtil.getDetailOrg();
						ApiSignInOrgModel oldApiSignInOrgModel = SessionUtil.getSignInOrg();
						
						SessionUtil.setOrgId(signInOrgModel.getId());
						SessionUtil.setDetailOrg(belongOrganizationModel);
						SessionUtil.setSignInOrg(getSignInOrgModel(signInOrgModel.getId()));
						CheckPermissionUtil checkPermissionUtil = new CheckPermissionUtil();
						if(checkPermissionUtil.checkPermissionManagerOrg() == false) {
							ShowErrorPermissionDialog showErrorPermissionDialog = new ShowErrorPermissionDialog();
							showErrorPermissionDialog.open();
							showErrorPermissionDialog.getBtnClose().addClickListener(ev->{
								SessionUtil.setOrgId(oldOrgId);
								SessionUtil.setDetailOrg(oldBelongOrganizationsModel);
								SessionUtil.setSignInOrg(oldApiSignInOrgModel);
							});
							
						}else {
							
							UI.getCurrent().getPage().reload();
						}
					}
				}else {
					SessionUtil.setOrgId(signInOrgModel.getId());
					SessionUtil.setDetailOrg(belongOrganizationModel);
					SessionUtil.setSignInOrg(getSignInOrgModel(signInOrgModel.getId()));
					CheckPermissionUtil checkPermissionUtil = new CheckPermissionUtil();
					if(checkPermissionUtil.checkPermissionManagerOrg() == false) {
						ShowErrorPermissionDialog showErrorPermissionDialog = new ShowErrorPermissionDialog();
						showErrorPermissionDialog.open();
						showErrorPermissionDialog.getBtnClose().addClickListener(ev->{
							SessionUtil.setOrgId(null);
							SessionUtil.setDetailOrg(null);
							SessionUtil.setSignInOrg(null);
						});
					}else {
						UI.getCurrent().navigate(UserView.class);
						fireEvent(new ClickEvent(this,false));
					}
				}
			});

			vLayout.add(header,createLayoutKeyValue("Đơn vị: ", signInOrgModel.getName(),null),createLayoutKeyValue("Mô tả: ", signInOrgModel.getDescription(), null));
		}


		add(div,hLayoutAvatar,vLayout);
	}

	private Component createLayoutKeyValue(String header,String content,Span spValue) {
		HorizontalLayout hLayout = new HorizontalLayout();

		Span spanHeader = new Span(header);
		spanHeader.getStyle().set("font-weight", "600");

		Span spanContent = new Span(content);
		if(content == null) {
			spanContent = spValue;
		}

		hLayout.add(spanHeader,spanContent);


		return hLayout;
	}


	private ApiOrganizationModel getOrg(String id) {
		try {
			ApiResultResponse<ApiOrganizationModel> getData = ApiOrganizationService.getOneOrg(id);

			return getData.getResult();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private ApiSignInOrgModel getSignInOrgModel(String idOrg) {
		try {
			ApiResultResponse<ApiSignInOrgModel> signOrg = ApiAuthService.signInOrg(idOrg);
			if(signOrg.isSuscces()) {
				return signOrg.getResult();
			}
		} catch (Exception e) {
		}
		return null;
	}

	public Registration addChangeListener(ComponentEventListener<ClickEvent> listener) {
		return addListener(ClickEvent.class, listener);
	}

	public static class ClickEvent extends ComponentEvent<CardOrgForm> {
		private static final long serialVersionUID = 1L;

		public ClickEvent(CardOrgForm source, boolean fromClient) {
			super(source, fromClient);
		}
	}
}
