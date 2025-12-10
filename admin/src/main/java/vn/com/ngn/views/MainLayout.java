package vn.com.ngn.views;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.flowingcode.vaadin.addons.fontawesome.FontAwesome.Solid.Icon;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;

import vn.com.ngn.api.auth.ApiAuthModel;
import vn.com.ngn.page.app_access.AppAccessView;
import vn.com.ngn.page.appmobile.AppMobileView;
import vn.com.ngn.page.home.HomeView;
import vn.com.ngn.page.organization.OrgView;
import vn.com.ngn.page.report.ReportView;
import vn.com.ngn.page.setting.SettingView;
import vn.com.ngn.page.setting.forms.ChangeOrgDialog;
import vn.com.ngn.page.setting.forms.ControlRoleTemplateForm;
import vn.com.ngn.page.user.UserView;
import vn.com.ngn.page.user.model.BelongOrganizationsModel;
import vn.com.ngn.securitys.AuthenticatedUser;
import vn.com.ngn.utils.PropUtils;
import vn.com.ngn.utils.SessionUtil;
import vn.com.ngn.utils.components.ButtonTemplate;
import vn.com.ngn.utils.components.DialogTemplate;

public class MainLayout extends AppLayout {
    private static final long serialVersionUID = 1L;
	private H2 viewTitle;
	private Image headerLogo = new Image(PropUtils.getUrlLogoForMainLayout(), "LOGO");
	private ButtonTemplate btnChangeOrg = new ButtonTemplate();
	private BelongOrganizationsModel belongOrganizationsModel = SessionUtil.getDetailOrg();

	private final AuthenticatedUser authenticatedUser;
    public MainLayout(AuthenticatedUser authenticatedUser) {
    	this.authenticatedUser = authenticatedUser;
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
        configComponent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        
        viewTitle = new H2();
        viewTitle.getStyle().set("color", "white");
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        
        if(belongOrganizationsModel != null) {
        	btnChangeOrg = new ButtonTemplate(belongOrganizationsModel.getOrganizationName(),FontAwesome.Solid.HOME.create());
            //The class in file mainLayout.css
            btnChangeOrg.addClassNames("btn_move-org");
            btnChangeOrg.setTooltipText("Chuyển đơn vị quản trị");
        }
        
        if(SessionUtil.getOrgId().equals(SessionUtil.getUser().getId())) {
        	btnChangeOrg.setVisible(false);
        }
        
        ButtonTemplate btnHow = new ButtonTemplate(FontAwesome.Solid.QUESTION_CIRCLE.create());
        btnHow.getStyle().set("background", "#ffffff").set("border-radius", "100px").set("color", "#113f67");
        btnHow.addClickListener(e->{
        	btnHow.setOpenNewPage(PropUtils.getUrlUserManual());
        });
        
        HorizontalLayout hLayoutHeader = new HorizontalLayout();
        hLayoutHeader.add(btnChangeOrg,btnHow,btnHow.getAnchor());
        hLayoutHeader.getStyle().set("margin-left", "auto").set("margin-right", "10px");

        addToNavbar(true, toggle, viewTitle,hLayoutHeader);
    }

    private void addDrawerContent() {
        H1 appName = new H1(PropUtils.getMainTitlteForMainLayout());
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        appName.getStyle().setMargin("auto").set("font-size", "21px").setColor("#101028");
        
        headerLogo.setWidth("60px");
        headerLogo.setHeight("60px");
        headerLogo.getStyle().setMargin("auto");
        
        VerticalLayout header = new VerticalLayout();
        
        header.add(headerLogo,appName);
        header.getStyle().set("display", "flex");
        

        Scroller scroller = new Scroller(getTabs());

        addToDrawer(header, scroller, createFooter());
    }
    
    private void configComponent() {
    	btnChangeOrg.addClickListener(e->{
    		ChangeOrgDialog changeOrgDialog = new ChangeOrgDialog(SessionUtil.getListOrg(),true);
    		changeOrgDialog.getFooter().removeAll();
			if(changeOrgDialog.getListOrg().size() == 1) {

			}else {
				changeOrgDialog.open();
			}
    	});
    	
    	this.getChildren().forEach(model->{
    		System.out.println(model);
    	});
    	
    }

//    private SideNav createNavigation() {
//        SideNav nav = new SideNav();
//
////        nav.addItem(new SideNavItem("Hello World", HelloWorldView.class, LineAwesomeIcon.GLOBE_SOLID.create()));
//        nav.addItem(new SideNavItem("About", HomeView.class, LineAwesomeIcon.FILE.create()));
//
//        return nav;
//    }
    
    private Tabs getTabs() {
    	Tabs tabs = new Tabs();
    	if(SessionUtil.getUser().getUsername().equals("administrator")) {
        	tabs.add(createTab(FontAwesome.Solid.HOME_LG.create(), "Tổng quan", HomeView.class));
        	tabs.add(createTab(FontAwesome.Solid.USER.create(), "Quản lý người dùng",UserView.class));
        	tabs.add(createTab(FontAwesome.Solid.SITEMAP.create(), "Quản lý đơn vị",OrgView.class));
        	tabs.add(createTab(FontAwesome.Solid.REORDER.create(), "Quản lý vai trò mẫu",ControlRoleTemplateForm.class));
        	if(PropUtils.isAllowAppAccess()) {
        		tabs.add(createTab(FontAwesome.Solid.KEY.create(), "Quản lý quyền truy cập", AppAccessView.class));
        	}
        	if(PropUtils.isAllowAppMobile()) {
        		tabs.add(createTab(FontAwesome.Solid.MOBILE_ANDROID.create(), "Quản lý ứng dụng di động",AppMobileView.class));
        	}
        	tabs.add(createTab(FontAwesome.Solid.FILE_EXPORT.create(), "Báo cáo, thống kê",ReportView.class));
    	}else {
        	tabs.add(createTab(FontAwesome.Solid.USER.create(), "Quản lý người dùng",UserView.class));
        	tabs.add(createTab(FontAwesome.Solid.SITEMAP.create(), "Quản lý đơn vị",OrgView.class));
        	tabs.add(createTab(FontAwesome.Solid.FILE_EXPORT.create(), "Báo cáo, thống kê",ReportView.class));
    	}

    	tabs.setOrientation(Tabs.Orientation.VERTICAL);
//    	tabs.getStyle().set("background", "#e7eaf6").set("border-radius", "10px");
    	tabs.setWidthFull();
    	
    	return tabs;
    }
    
    private Tab createTab(Icon viewIcon,String viewName,Class<? extends Component> navigationTarget) {
    	viewIcon.getStyle().set("box-sizing", "border-box")
		.set("margin-inline-end", "var(--lumo-space-m)")
		.set("margin-inline-start", "var(--lumo-space-xs)");
//		.set("padding", "var(--lumo-space-xs)");
    	
    	RouterLink link = new RouterLink();
    	link.setRoute(navigationTarget);
    	link.add(viewIcon, new Span(viewName));
		link.setTabIndex(-1);
		
		return new Tab(link);
    	
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        ApiAuthModel apiAuthModel = authenticatedUser.get().get();
        System.out.println(apiAuthModel.getFullName());
        
        MenuBar userMenu = new MenuBar();
		userMenu.setThemeName("tertiary-inline contrast");
		userMenu.setWidthFull();
		
		Button btnUser = new Button(apiAuthModel.getFullName(),FontAwesome.Solid.USER.create());
		btnUser.setWidth("100%");

		MenuItem layoutMenu = userMenu.addItem("");
		layoutMenu.getStyle().setWidth("100%");
		
		Button btnSetting = new Button("Cài đặt",FontAwesome.Solid.GEAR.create());
		btnSetting.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnSetting.getStyle().set("margin-right", "auto");
		
		
		Button btnLogout = new Button("Đăng xuất",FontAwesome.Solid.SIGN_OUT.create());
		btnLogout.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnLogout.getStyle().set("margin-right", "auto");
		
		Div div = new Div(btnUser);
		div.getElement().getStyle().set("display", "flex");
		div.getElement().getStyle().set("align-items", "center");
		div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
		div.setWidth("200px");
		layoutMenu.add(div);
		layoutMenu.getSubMenu().addItem(btnSetting,e->{
			openDialogSetting();
		});
		layoutMenu.getSubMenu().addItem(btnLogout,e->{
			authenticatedUser.logout();
		});
		
		layout.getStyle().set("margin-top", "auto");
		layout.setWidthFull();
		layout.add(userMenu);
        
        return layout;
    }
    
    private void openDialogSetting() {
    	DialogTemplate dialogTemplate = new DialogTemplate("CÀI ĐẶT",()->{
    		
    	});
    	
    	SettingView settingView = new SettingView();
    	
    	dialogTemplate.add(settingView);
    	
    	dialogTemplate.getBtnSave().addClickListener(e->{
    		dialogTemplate.close();
    	});
    	
    	dialogTemplate.getFooter().removeAll();
    	
    	dialogTemplate.setWidth("30%");
    	dialogTemplate.setHeight("auto");
    	
    	dialogTemplate.open();
    	
    }
    

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
