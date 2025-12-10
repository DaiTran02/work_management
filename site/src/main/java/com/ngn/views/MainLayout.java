package com.ngn.views;

import java.time.LocalDate;
import java.util.ArrayList;

import org.apache.commons.lang3.tuple.Pair;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.authen_to_admin.AuthenToAdminService;
import com.ngn.api.authentication.ApiAuthenticationModel;
import com.ngn.api.notification.ApiNotificationService;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.user.ApiUpdateUserService;
import com.ngn.enums.DataOfEnum;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.secutity.AuthenticatedUser;
import com.ngn.setting.ChangePasswordView;
import com.ngn.setting.ChooseOrgDialog;
import com.ngn.setting.InfomationView;
import com.ngn.setting.leader_classify.form.ControlLeaderAndClassifyForm;
import com.ngn.tdnv.calendar.views.CalendarView;
import com.ngn.tdnv.dashboard.DashboardView;
import com.ngn.tdnv.doc.DocComingView;
import com.ngn.tdnv.doc.DocView;
import com.ngn.tdnv.personal_record.view.PersonalTransferred;
import com.ngn.tdnv.personal_record.view.PersonalView;
import com.ngn.tdnv.personal_record.view.PersonalWasTransferView;
import com.ngn.tdnv.process_doc_and_schedule.ProcessedDocView;
import com.ngn.tdnv.report.ReportView;
import com.ngn.tdnv.tag.TagView;
import com.ngn.tdnv.task.views.TaskAsignView;
import com.ngn.tdnv.task.views.TaskAssignedInsteadView;
import com.ngn.tdnv.task.views.TaskAssigneeView;
import com.ngn.tdnv.task.views.TaskFollowerView;
import com.ngn.tdnv.task.views.TaskOwnerView;
import com.ngn.tdnv.task.views.TaskSupportView;
import com.ngn.utils.CheckPermisstionUtil;
import com.ngn.utils.CountMenuUtil;
import com.ngn.utils.PropsUtil;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.DialogTemplate;
import com.ngn.utils.components.MenuBarTemplate;
import com.ngn.utils.components.OnboardingView;
import com.ngn.utils.components.QuestionHowToUseApp;
import com.ngn.utils.notifications.TabNotificationForm;
import com.vaadin.componentfactory.onboarding.OnboardingStep;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.LumoUtility;



public class MainLayout extends AppLayout {
	private static final long serialVersionUID = 1L;

	private boolean isUiMobile = false;
	
	private RadioButtonGroup<DataOfEnum> chooseDataUserOrOrg = new RadioButtonGroup<DataOfEnum>();
	
	private MenuBarTemplate menuBell = new MenuBarTemplate();
	private MenuBarTemplate menuBookMark = new MenuBarTemplate();

	private H2 viewTitle;
	//	private ButtonTemplate btnNotification = new ButtonTemplate(FontAwesome.Solid.BELL.create());
	//  Check ne
	private ButtonTemplate btnOrgOfUser;
	private ButtonTemplate btnHelp = new ButtonTemplate(FontAwesome.Solid.QUESTION_CIRCLE.create());
	private ComboBox<Pair<Integer, String>> cmbDataOfYear = new ComboBox<Pair<Integer,String>>("Năm dữ liệu");
	private VerticalLayout vContentLayout = new VerticalLayout();
	private VerticalLayout vLayoutNav = new VerticalLayout();
	private UI ui = UI.getCurrent();
	
	private Image imgLogo;

	private OnboardingView onboardingView = new OnboardingView();
	private CheckPermisstionUtil checkPermisstionUtil = new CheckPermisstionUtil();

	private ApiAuthenticationModel uModel;
	private ApiNotificationService apiNotificationService;

	private final AuthenticatedUser authenticatedUser;
	@SuppressWarnings("unused")
	private AccessAnnotationChecker accessAnnotationChecker;
	public MainLayout(AuthenticatedUser authenticatedUser,AccessAnnotationChecker accessAnnotationChecker,ApiNotificationService apiNotificationService) {
		this.authenticatedUser = authenticatedUser;
		this.accessAnnotationChecker = accessAnnotationChecker;
		this.apiNotificationService = apiNotificationService;
		initChooseDataUserOrOrg();
		checkUiMobile();
		initDataOfYear();
		builLayout();
		loadData();
		setPrimarySection(Section.DRAWER);
		addToDrawer(vContentLayout);
		addHeaderContent();
		configComponent();
		createNavigation();
		addDrawerContent();
		if(authenticatedUser.get().get().isGuideWebUI() == false && isUiMobile == false) {
			openDialogHowToUse();
		}
	}

	private void builLayout() {
		imgLogo = new Image(PropsUtil.getUrlLoginForMainLayout(),"HN");
	}

	private void configComponent() {
		btnOrgOfUser.addClickListener(e->{
			//This class in package setting
			ChooseOrgDialog chooseOrgDialog = new ChooseOrgDialog(SessionUtil.getBelongOrg(),true,null,false);
			chooseOrgDialog.getFooter().removeAll();
			if(chooseOrgDialog.getListOrg().size() == 1) {

			}else {
				chooseOrgDialog.open();
			}
			
			if(SessionUtil.isPermissionChooseOrg() == true) {
				chooseOrgDialog.open();
			}

		});


		cmbDataOfYear.addValueChangeListener(e->{
			SessionUtil.setYear(cmbDataOfYear.getValue().getKey());
			UI.getCurrent().getPage().reload();
		});

		btnHelp.addClickListener(e->{
			btnHelp.setOpenNewPage(PropsUtil.urlHowToUse());
		});

		chooseDataUserOrOrg.addValueChangeListener(e->{
			SessionUtil.setDataOf(e.getValue());
			UI.getCurrent().getPage().reload();
		});

		onboardingView.createStep(btnHelp, "Tài liệu", "Mục tài liệu về các cách sử dụng hay các tài liệu liên quan khác");
		onboardingView.createStep(btnOrgOfUser, "Đơn vị sử dụng", "Đây là thông tin về đơn vị sử dụng, bạn có thể chuyển đơn vị đơn vị sử dụng nếu tài khoản"
				+ " của bạn sử dụng từ hai đơn vị trở lên");
		onboardingView.createStep(cmbDataOfYear, "Chọn năm hiện thị dữ liệu", "Mỗi năm thì sẽ có một dữ liệu khác nhau, và dữ liệu sẽ hiện thị mặc định là theo năm hiện tại");

	}

	private void loadData() {
		uModel = authenticatedUser.get().get();

	}

	private void addHeaderContent() {
		DrawerToggle toggle = new DrawerToggle();
		toggle.setAriaLabel("Menu toggle");
		TabNotificationForm tabNotificationForm = new TabNotificationForm(apiNotificationService);
		ButtonTemplate btnBell = new ButtonTemplate(FontAwesome.Solid.BELL.create());
		btnBell.addThemeVariants(ButtonVariant.LUMO_TERTIARY,ButtonVariant.LUMO_SMALL);
		btnBell.getStyle().setColor("#f5f5f5").setCursor("pointer");
		btnBell.addClickListener(e->{
			tabNotificationForm.updateData();
		});

		
		Div divBell = new Div();
		divBell.addClassNames("bell");
		
		Div divBellLayer1 = new Div();
		divBellLayer1.add(FontAwesome.Solid.BELL.create());
		
		Div divBellLayer2 = new Div();
		divBellLayer2.add(FontAwesome.Solid.BELL.create());
		
		Div divBellLayer3 = new Div();
		divBellLayer3.add(FontAwesome.Solid.BELL.create());
		
		divBellLayer1.addClassName("layer-1");
		divBellLayer2.addClassName("layer-2");
		divBellLayer3.addClassName("layer-3");
		
		divBell.getStyle().setFontSize("13px");
		
		divBell.add(divBellLayer1,divBellLayer2,divBellLayer3);
		

		MenuItem menuItemBell = menuBell.addItem(divBell);
		SubMenu subMenuBell = menuItemBell.getSubMenu();

		subMenuBell.add(tabNotificationForm);

		menuBell.addThemeVariants(MenuBarVariant.LUMO_TERTIARY,MenuBarVariant.LUMO_SMALL);
		menuBell.getStyle().setMarginLeft("auto").setCursor("pointer");

		menuItemBell.getStyle().setColor("white").setWidth("50px").setCursor("pointer");

		menuBookMark.setVisible(false);
		ButtonTemplate btnBookMark = new ButtonTemplate(FontAwesome.Solid.ALIGN_JUSTIFY.create());
		btnBookMark.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnBookMark.addThemeVariants(ButtonVariant.LUMO_SMALL);
		btnBookMark.getStyle().setColor("#f5f5f5").setBackground("#5585b5");

		MenuItem menuItemBookmark = menuBookMark.addItem(btnBookMark);
		menuItemBookmark.getStyle().setColor("white").setBackground("#5585b5");

		@SuppressWarnings("unused")
		SubMenu subMenuBookMark = menuItemBookmark.getSubMenu();

		if(PropsUtil.isAllowsBookmark() == true) {
			menuBookMark.setVisible(true);
		}


		viewTitle = new H2();
		viewTitle.getStyle().setColor("#fcfefe");
		viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

		//		btnNotification.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		//The className in main-layout.css
		//		btnNotification.addClassNames("btn_bell");
		//		btnNotification.setTooltipText("Thông báo");
		
		BelongOrganizationModel changeOrg = SessionUtil.getOrg();
		
		String nameCurrentOrg = changeOrg == null ? "Đang cập nhật" : changeOrg.getOrganizationName();

		if( SessionUtil.getBelongOrg() != null && SessionUtil.getBelongOrg().size() > 1) {
			btnOrgOfUser = new ButtonTemplate("Đổi đơn vị sử dụng ("+nameCurrentOrg+")",FontAwesome.Solid.RETWEET.create());
		}else {
			BelongOrganizationModel currentOrg = SessionUtil.getOrg();
			String currentNameOrg = "";
			if(currentOrg == null) {
				currentNameOrg = "Đang cập nhật";
			}else {
				currentNameOrg = currentOrg.getOrganizationName();
			}
			btnOrgOfUser = new ButtonTemplate(currentNameOrg,FontAwesome.Solid.HOME.create());
			btnOrgOfUser.setEnabled(false);
		}
		
		if(SessionUtil.isPermissionChooseOrg() == true) {
			btnOrgOfUser = new ButtonTemplate("Đổi đơn vị sử dụng ("+nameCurrentOrg+")",FontAwesome.Solid.RETWEET.create());
		}

		btnOrgOfUser.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		//		btnOrgOfUser.getStyle().setMarginLeft("auto");
		//The className in main-layout.css
		btnOrgOfUser.addClassNames("btn_move-org");
		btnOrgOfUser.setTooltipText("Chuyển đơn vị sử dụng");

		if(isUiMobile) {
			btnOrgOfUser.setText("Đơn vị");
		}

		UI.getCurrent().getPage().retrieveExtendedClientDetails(e->{
			if(e.getScreenWidth() < 768) {
				btnOrgOfUser.setText("Đơn vị");
			}
		});


		btnHelp.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		//The className in main-layout.css
		btnHelp.addClassNames("btn_help");
		btnHelp.setTooltipText("Hướng dẫn sử dụng");
		btnHelp.getStyle().setMarginRight("10px");



		addToNavbar(true, toggle, viewTitle,menuBell,btnOrgOfUser,btnHelp,btnHelp.getAnchor());
	}

	private void checkUiMobile() {
		UI.getCurrent().getPage().retrieveExtendedClientDetails(e->{
			if(e.getScreenWidth() < 768) {
				isUiMobile = true;
			}
		});
	}
	
	private void initChooseDataUserOrOrg() {
		if(SessionUtil.checkDataOf() == null) {
			chooseDataUserOrOrg.setItems(DataOfEnum.values());
			chooseDataUserOrOrg.setValue(DataOfEnum.TOCANHAN);
			chooseDataUserOrOrg.setItemLabelGenerator(key->key.getTitle());
			chooseDataUserOrOrg.setLabel("Quản lý dữ liệu");
			SessionUtil.setDataOf(chooseDataUserOrOrg.getValue());
		}else {
			chooseDataUserOrOrg.setItems(DataOfEnum.values());
			chooseDataUserOrOrg.setItemLabelGenerator(key->key.getTitle());
			chooseDataUserOrOrg.setLabel("Quản lý dữ liệu");
			chooseDataUserOrOrg.setValue(SessionUtil.checkDataOf());
		}
	}
	
	public void addDrawerContent() {
		H1 appName = new H1(PropsUtil.getMainTitleForMainLayout());
		appName.getStyle().setFontSize("14px");
		appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
		appName.getStyle().setMargin("auto");

		imgLogo.setWidth("60px");
		imgLogo.setHeight("60px");
		imgLogo.getStyle().setMargin("auto");

		VerticalLayout hLayoutHeader = new VerticalLayout();
		hLayoutHeader.getStyle().set("display", "flex");
		hLayoutHeader.add(imgLogo,appName);
		hLayoutHeader.setWidthFull();

		Header header = new Header(hLayoutHeader);
		header.setWidthFull();


		Scroller scroller = new Scroller(vLayoutNav);
		scroller.setWidthFull();

		cmbDataOfYear.getStyle().set("padding-left", "5px");
		cmbDataOfYear.setWidth("95%");
		
		chooseDataUserOrOrg.getStyle().setPaddingLeft("5px");

		vContentLayout.removeAll();
		vContentLayout.setSizeFull();
		vContentLayout.add(header,cmbDataOfYear,chooseDataUserOrOrg, scroller, createFooter());
	}

	private CountMenuUtil countMenuUtil = new CountMenuUtil(SessionUtil.getOrg(),SessionUtil.getDetailOrg());

	public void createNavigation() {

		vLayoutNav.removeAll();



		//Tổng quan
		SideNav overviewNav = new SideNav();
		overviewNav.addClassName("side_nav");
		overviewNav.setLabel("Tổng quan");
		SideNavItem dashboardView = new SideNavItem("Tổng quát",DashboardView.class,createMenuIcon(FontAwesome.Solid.DASHBOARD.create()));

		onboardingView.createStep(dashboardView, "Tổng quát", "Mục tổng quát để xem nhanh các thông tin cơ bản như nhiệm vụ đã giao, được giao,"
				+ "phối hợp, theo dõi, ngoài ra bạn có thể theo dõi tổng quát các nhiệm vụ đang thực hiện như thế nào.");

		SideNavItem reportView = new SideNavItem("Báo cáo",ReportView.class,createMenuIcon(FontAwesome.Solid.FILE_EXCEL.create()));
		overviewNav.addItem(dashboardView,reportView);
		overviewNav.setCollapsible(true);

		onboardingView.createStep(reportView, "Chức năng báo cáo", "Tại chức năng báo cáo này bạn có thể tạo báo các nhiệm vụ hoặc văn bản từ đơn vị mà "
				+ "tài khoản quản lý, ngoài ra chức năng còn cho phép xuất các file đã báo cáo");

		//Văn bản
		SideNav docNav = new SideNav();
		docNav.addClassName("side_nav");
		docNav.setLabel("Văn bản giao nhiệm vụ");
		docNav.setCollapsible(true);

		SideNavItem docAway = new SideNavItem("Văn bản đi",DocView.class,createMenuIcon(FontAwesome.Solid.FILE_EXPORT.create()));
		SideNavItem docIcomingView = new SideNavItem("Văn bản đến",DocComingView.class,createMenuIcon(FontAwesome.Solid.FILE_IMPORT.create()));
		docAway.setSuffixComponent(createCounter(countMenuUtil.countDocAway()));
		docIcomingView.setSuffixComponent(createCounter(countMenuUtil.countDocIncoming()));

		docNav.addItem(docAway,docIcomingView);

		onboardingView.createStep(docNav, "Chức năng quản lý văn bản (văn bản đi, văn bản đến)", "Quản lý các văn bản của đơn vị, các văn bản có thể được tạo từ đơn vị hoặc được thêm thông qua hệ thống quản lý văn bản");

		//Theo dõi nhiệm vụ
		SideNav tdnvNav = new SideNav();
		tdnvNav.addClassName("side_nav");
		tdnvNav.setLabel("Theo dõi nhiệm vụ");
		tdnvNav.setCollapsible(true);

		SideNavItem assignTask = new SideNavItem("Giao nhiệm vụ",TaskAsignView.class,createMenuIcon(FontAwesome.Solid.MAIL_FORWARD.create()));

		onboardingView.createStep(assignTask, "Giao nhiệm vụ tự phát", "Đây là chức năng giao nhiệm vụ mà không thông qua văn bản, ngoài chức năng giao nhiệm vụ từ văn bản thì hệ thống"
				+ " cho phép bạn giao nhiệm vụ tự phát, tuy nhiên nhiệm vụ này sẽ được liệt vào danh sách nhiệm vụ tự phát");

		SideNavItem listTaskOwner = new SideNavItem("Đã giao",TaskOwnerView.class,createMenuIcon(FontAwesome.Solid.FILE_TEXT.create()));
		listTaskOwner.setSuffixComponent(createCounter(countMenuUtil.countTaskOwner()));

		onboardingView.createStep(listTaskOwner, "Quản lý nhiệm vụ đã giao", "Chức năng này để quản lý những nhiệm vụ mà đơn vị của bạn đã thực hiện giao, "
				+ "tuy nhiên các nhiệm vụ đã giao có thể hiện thị tùy theo vai trò của tài khoản trong đơn vị.");

		SideNavItem listInteadTask = new SideNavItem("Đã giao thay",TaskAssignedInsteadView.class,createMenuIcon(FontAwesome.Solid.FILE_SIGNATURE.create()));
		listInteadTask.setSuffixComponent(createCounter(9));

		SideNavItem listTaskAssignee = new SideNavItem("Được giao",TaskAssigneeView.class,createMenuIcon(FontAwesome.Solid.CLIPBOARD_QUESTION.create()));
		listTaskAssignee.setSuffixComponent(createCounter(countMenuUtil.countTaskAssignee()));

		onboardingView.createStep(listTaskAssignee, "Quản lý nhiệm vụ được giao", "Mục này quản lý các nhiệm vụ mà đơn vị của bạn được giao từ các đơn vị khác, "
				+ "và các nhiệm vụ sẽ được hiện thị các thông tin và chức năng theo vai trò mà tài khoản được sử dụng trong đơn vị");

		SideNavItem listTaskSupport = new SideNavItem("Phối hợp",TaskSupportView.class,createMenuIcon(FontAwesome.Solid.FILE_CLIPBOARD.create()));
		listTaskSupport.setSuffixComponent(createCounter(countMenuUtil.countTaskSupport()));

		onboardingView.createStep(listTaskSupport, "Quản lý nhiệm vụ phối hợp", "Mục này quản lý các nhiệm vụ mà đơn vị của bạn sẽ phối hợp với đơn vị được giao nhiệm vụ để cùng nhau "
				+ "thực hiện một nhiệm vụ nào đó");

		SideNavItem listTaskFollower = new SideNavItem("Theo dõi",TaskFollowerView.class,createMenuIcon(FontAwesome.Solid.EYE.create()));

		


		tdnvNav.addItem(assignTask,listTaskOwner,listTaskAssignee,listTaskSupport,listTaskFollower);

		listTaskFollower.setSuffixComponent(createCounter(countMenuUtil.countTaskFollow()));
		onboardingView.createStep(listTaskFollower, "Theo dõi nhiệm vụ", "Bạn sẽ được quyền theo dõi các nhiệm vụ mà đơn vị đã giao với điều kiện là người giao nhiệm vụ cho phép nhóm của bạn theo dõi nhiệm vụ đó  "
				+ "và chức năng này sẽ theo vai trò của tài khoản");

		// Schedule
		SideNav processDocAndScheduleNav = new SideNav("Tiện ích khác");
		processDocAndScheduleNav.addClassName("side_nav");
		processDocAndScheduleNav.setCollapsible(true);
		
		SideNavItem processDoc = new SideNavItem("Văn bản trình xử lý",ProcessedDocView.class,createMenuIcon(FontAwesome.Regular.FILE_WORD.create()));
		onboardingView.createStep(processDoc, "Văn bản trình xử lý", "Chức năng chưa được kích hoạt");
		
		SideNavItem scheduleWork = new SideNavItem("Lịch công tác", CalendarView.class,createMenuIcon(FontAwesome.Regular.CALENDAR_ALT.create()));
		onboardingView.createStep(scheduleWork, "Lịch công tác", "Chức năng chưa được kích hoạt");

		processDocAndScheduleNav.addItem(processDoc,scheduleWork);
		
		if(PropsUtil.isProcessDocAndSchedule() == false) {
			processDocAndScheduleNav.setVisible(false);
		}
		
		if(processDocAndScheduleNav.getItems().size() == 0) {
			processDocAndScheduleNav.setVisible(false);
		}
		
		
		SideNav utilitiesNav = new SideNav();
		utilitiesNav.addClassName("side_nav");
		utilitiesNav.setLabel("Tiện ích");
		utilitiesNav.setCollapsible(true);

		SideNavItem tag = new SideNavItem("Quản lý thẻ cá nhân",TagView.class,createMenuIcon(FontAwesome.Solid.TAG.create()));
		onboardingView.createStep(tag, "Quản lý phân loại", "Quản lý các loại thẻ, dựa vào các thẻ để phân loại các dữ liệu muốn phân loại");
		
		SideNavItem personal_record = new SideNavItem("Hồ sơ cá nhân",PersonalView.class,createMenuIcon(FontAwesome.Solid.FOLDER_CLOSED.create()));

		
		SideNavItem personal_transfer = new SideNavItem("Hồ sơ đã chuyển giao",PersonalWasTransferView.class,createMenuIcon(FontAwesome.Solid.FOLDER_OPEN.create()));
		
		
		SideNavItem personal_transferred = new SideNavItem("Hồ sơ được chuyển giao",PersonalTransferred.class,createMenuIcon(FontAwesome.Solid.FOLDER_PLUS.create()));
		
		
		utilitiesNav.addItem(tag);
		
		if(PropsUtil.isAllowPersonalRecord()) {
			utilitiesNav.addItem(personal_record,personal_transfer,personal_transferred);
			onboardingView.createStep(personal_record, "Hồ sơ cá nhân", "Quản lý những văn bản và nhiệm vụ của cá nhân");
			onboardingView.createStep(personal_transfer, "Hồ sơ đã chuyển giao", "Quản lý những hồ sơ đã chuyển giao");
			onboardingView.createStep(personal_transferred, "Hồ sơ được chuyển giao", "Là những hồ sơ được cá nhân khác chuyển giao cho mình xử lý");
		}


		vLayoutNav.add(overviewNav,docNav,tdnvNav,utilitiesNav);
		vLayoutNav.setSizeFull();
	}
	
	private String getUrlManagerOrg() {
		String url = PropsUtil.getUrlManagerOrg();
		try {
			String apiKey = AuthenToAdminService.generateApiKey().getResult();
			String shortToken = AuthenToAdminService.generateShortToken(apiKey).getResult().getCode();
			url += "?code="+shortToken;
			String idOrg = SessionUtil.getOrg().getOrganizationId();
			url += "&org="+idOrg;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return url;
	}

	private Span createCounter(int count) {
		Span inboxCounter = new Span(String.valueOf(count));
		inboxCounter.getElement().getThemeList().add("badge contrast pill");
		inboxCounter.getElement().setAttribute("aria-label", "12 unread messages");
		return inboxCounter;
	}

	private Footer createFooter() {
		Footer layout = new Footer();


		MenuBar userMenu = new MenuBar();
		userMenu.setThemeName("tertiary-inline contrast");
		userMenu.setWidthFull();

		ButtonTemplate btnUser = new ButtonTemplate(uModel.getFullName(),FontAwesome.Solid.USER.create());
		btnUser.setWidth("100%");

		MenuItem layoutMenu = userMenu.addItem("");
		layoutMenu.getStyle().setWidth("100%");
		
		ButtonTemplate btnManagerOrg = new ButtonTemplate("Quản trị đơn vị",FontAwesome.Solid.HOME_LG_ALT.create());
		btnManagerOrg.addThemeVariants(ButtonVariant.LUMO_TERTIARY);


		ButtonTemplate btnLeaderAndClassify = new ButtonTemplate("Văn bản mở rộng",FontAwesome.Solid.CLIPBOARD_LIST.create());
		btnLeaderAndClassify.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnLeaderAndClassify.getStyle().set("margin-right", "auto").setCursor("pointer");

		ButtonTemplate btnInfomation = new ButtonTemplate("Thông tin tài khoản",FontAwesome.Solid.INFO.create());
		btnInfomation.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnInfomation.getStyle().set("margin-right", "auto").setCursor("pointer");

		ButtonTemplate btnSetting = new ButtonTemplate("Cài đặt",FontAwesome.Solid.GEAR.create());
		btnSetting.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnSetting.getStyle().set("margin-right", "auto").setCursor("pointer");


		ButtonTemplate btnChangePassword = new ButtonTemplate("Đổi mật khẩu",FontAwesome.Solid.KEY.create());
		btnChangePassword.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnChangePassword.getStyle().set("margin-right", "auto").setCursor("pointer");


		ButtonTemplate btnLogout = new ButtonTemplate("Đăng xuất",FontAwesome.Solid.SIGN_OUT.create());
		btnLogout.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnLogout.getStyle().set("margin-right", "auto").setCursor("pointer");

		Div div = new Div(btnUser);
		div.getElement().getStyle().set("display", "flex");
		div.getElement().getStyle().set("align-items", "center");
		div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
		div.setWidth("214px");
		layoutMenu.add(div);
		
		
		Div divButtonOrg = new Div();
		divButtonOrg.setWidthFull();
		divButtonOrg.add(btnManagerOrg,btnManagerOrg.getAnchor());
		
		if(checkPermisstionUtil.checkPermissionUserAdministrationDepartment()) {
			layoutMenu.getSubMenu().addItem(divButtonOrg);
		}
		
		btnManagerOrg.addClickListener(e->{
			btnManagerOrg.setOpenNewPage(getUrlManagerOrg());
		});
		

		layoutMenu.getSubMenu().addItem(btnLeaderAndClassify,e->{
			openDialogControlLeaderClassify();
		});

		layoutMenu.getSubMenu().addItem(btnInfomation,e->{
			openDialogInfomation();
		});

		//		layoutMenu.getSubMenu().addItem(btnSetting,e->{
		//			openDialogSetting();
		//		});
		if(uModel.getProvider() != null && uModel.getProvider().getKey().equals("local")) {
			layoutMenu.getSubMenu().addItem(btnChangePassword,e->{
				openDialogChangePassword();
			});
		}
		
		layoutMenu.getSubMenu().addItem(btnLogout,e->{
			authenticatedUser.logout();
		});

		OnboardingStep oStep = onboardingView.createStep(layoutMenu, "Chức năng mở rộng", "Đây là tập hợp các chức năng mở rộng, hãy click chuột vào tên tài khoản để xem chi tiết");
		oStep.addBeforePopupShownListener(e->{
			//			 layoutMenu.getUI().ifPresent(ui -> {
			//			        ui.getPage().executeJs(
			//			            "document.querySelector('vaadin-menu-bar').querySelector('vaadin-menu-bar-button').click();"
			//			        );
			//			    });

			doUpGuidedOfUser();
		});



		layout.getStyle().set("margin-top", "auto");
		layout.setWidthFull();
		layout.add(userMenu);

		return layout;
	}

	private void initDataOfYear() {
		try {
			var listYear = new ArrayList<Pair<Integer, String>>();

			int yearToSelect = SessionUtil.getYear() == 0 ? LocalDate.now().getYear() : SessionUtil.getYear();
			Pair<Integer, String> modelYearSelect = null;
			for(int i = 2019 ; i <= LocalDate.now().getYear() ; i++) {
				var modelYear = Pair.of(i,"Năm "+i);
				listYear.add(modelYear);

				if(i == yearToSelect) {
					modelYearSelect = modelYear;
				}

			}
			cmbDataOfYear.setItems(listYear);
			cmbDataOfYear.setItemLabelGenerator(Pair::getRight);
			cmbDataOfYear.setValue(modelYearSelect);
			if(SessionUtil.getYear() == 0) {
				SessionUtil.setYear(LocalDate.now().getYear());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private void openDialogControlLeaderClassify() {
		DialogTemplate dialog = new DialogTemplate("Quản lý người chỉ đạo và người duyệt");

		ControlLeaderAndClassifyForm classifyForm = new ControlLeaderAndClassifyForm();

		dialog.add(classifyForm);
		dialog.getFooter().removeAll();

		dialog.setSizeFull();
		dialog.setLayoutMobile();
		dialog.open();
	}

	//	private void openDialogSetting() {
	//		DialogTemplate dialog = new DialogTemplate("Cài đặt");
	//
	//		SettingView settingView = new SettingView();
	//
	//		dialog.add(settingView);
	//
	//		dialog.setWidth("60%");
	//		dialog.setHeight("60%");
	//
	//		dialog.open();
	//	}

	private void openDialogInfomation() {
		DialogTemplate dialog = new DialogTemplate("Thông tin tài khoản");
		InfomationView infomationView = new InfomationView(()->{
			if(ui != null) {
				ui.access(()->{
					authenticatedUser.logout();
				});
			}
		});

		dialog.add(infomationView);

		dialog.getBtnSave().setIcon(FontAwesome.Solid.INFO.create());
		dialog.getBtnSave().setText("Cập nhật thông tin");
		dialog.getBtnSave().addClickListener(e->{
			infomationView.updateInfo();
		});

		dialog.setWidth("30%");
		dialog.setHeight("auto");
		dialog.setLayoutMobile();
		dialog.open();
	}

	private void openDialogChangePassword() {
		DialogTemplate dialog = new DialogTemplate("Thay đổi mật khẩu");

		ChangePasswordView changePasswordView = new ChangePasswordView();

		dialog.add(changePasswordView);

		dialog.getBtnSave().setText("Lưu");
		dialog.getBtnSave().addClickListener(e->{
			changePasswordView.changePass();
		});

		dialog.setWidth("30%");
		dialog.setLayoutMobile();
		dialog.open();
	}

	private void openDialogHowToUse() {
		DialogTemplate dialogTemplate = new DialogTemplate("Giới thiệu sơ bộ về hệ thống");

		QuestionHowToUseApp questionHowToUseApp = new QuestionHowToUseApp(()->{
			dialogTemplate.close();
			doUpGuidedOfUser();
		});

		questionHowToUseApp.addChangeListener(e->{
			dialogTemplate.close();
			if(onboardingView != null) {
				onboardingView.onStartBoarding();
			}
		});

		dialogTemplate.add(questionHowToUseApp);

		dialogTemplate.setHeight("auto");
		dialogTemplate.setWidth("40%");
		dialogTemplate.getBtnClose().setVisible(false);
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setLayoutMobile();
		dialogTemplate.open();
	}

	private void doUpGuidedOfUser() {
		ApiResultResponse<Object> update = ApiUpdateUserService.guidedUi(SessionUtil.getIdUser());
		if(update.isSuccess()) {
			System.out.println("Cap nhap guided");
		}
	}

	private Component createMenuIcon(Icon icon) {

		icon.setSize("16px");

		return icon;
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
