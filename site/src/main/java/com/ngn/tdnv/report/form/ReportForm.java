package com.ngn.tdnv.report.form;

import org.vaadin.addons.yuri0x7c1.bslayout.BsColumn;
import org.vaadin.addons.yuri0x7c1.bslayout.BsLayout;
import org.vaadin.addons.yuri0x7c1.bslayout.BsRow;

import com.ngn.interfaces.FormInterface;
import com.ngn.tdnv.report.form.detail.ReportDocForm;
import com.ngn.tdnv.report.form.detail.ReportListTaskAssigneeForm;
import com.ngn.tdnv.report.form.detail.ReportListTaskKpiForm;
import com.ngn.tdnv.report.form.detail.ReportListTaskOwnerForm;
import com.ngn.tdnv.report.form.detail.ReportListTasksFollowerForm;
import com.ngn.tdnv.report.form.detail.ReportListTasksSupportForm;
import com.ngn.utils.components.DialogTemplate;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style.Display;
import com.vaadin.flow.dom.Style.FlexWrap;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;

public class ReportForm extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;

	private VerticalLayout vLayout = new VerticalLayout();
	
	private FlexLayout flexLayoutMobile = new FlexLayout();

//	private String urlImage = "./images/report.png";
	private String urlVBImage = "./images/VB.png";
	private String urlDaGiaoImage = "./images/DAGIAO.png";
	private String urlDuocGiaoImage = "./images/DUOCGIAO.png";
	private String urlHoTroImage = "./images/HOTRO.png";
	private String urlTheoDoiImage = "./images/THEODOI.png";
	private String urlRatingImage = "./images/RATING.jpg";

	private CardReportForm cardReportDoc = new CardReportForm("Danh sách văn bản","Lập báo cáo Văn bản của Đơn vị","",urlVBImage);

	private CardReportForm cardReportListTasksOwner = new CardReportForm("Nhiệm vụ đã giao", "Lập báo cáo nhiệm vụ đã giao", "", urlDaGiaoImage);
	private CardReportForm cardReportListTasksAssignee = new CardReportForm("Nhiệm vụ được giao", "Lập báo cáo nhiệm vụ được giao", "", urlDuocGiaoImage);
	private CardReportForm cardReportListTasksSupport = new CardReportForm("Nhiệm vụ phối hợp", "Lập báo cáo nhiệm vụ phối hợp", "", urlHoTroImage);
	private CardReportForm cardReportListTasksFollower = new CardReportForm("Nhiệm vụ theo dõi", "Lập báo cáo nhiệm vụ theo dõi", "", urlTheoDoiImage);
	private CardReportForm cardReportAchivement = new CardReportForm("Báo cáo đánh giá", "Lập báo cáo đánh giá", "", urlRatingImage);
	public ReportForm() {
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(vLayout);
		createLayout();
	}

	@Override
	public void configComponent() {
		cardReportDoc.addClickListener(e->{
			openDialogReportDoc();
		});


		cardReportListTasksOwner.addClickListener(e->{
			openDialogReportTaskOwner();
		});

		cardReportListTasksAssignee.addClickListener(e->{
			openDialogReportTaskAssignee();
		});

		cardReportListTasksSupport.addClickListener(e->{
			openDialogReportTaskSupport();
		});

		cardReportListTasksFollower.addClickListener(e->{
			openDialogReportTaskFollower();
		});
		
		cardReportAchivement.addClickListener(e->openDialogReportTaskKpi());

	}

	private void createLayout() {
		//The class in report.css
		addClassNames("image-gallery-view");
		this.getStyle().setPadding("0 20px").set("flex-flow", "nowrap");
		this.setWidth("100%");

		H1 title = new H1("Lập báo cáo");
		title.addClassNames(Margin.Bottom.NONE, Margin.Top.XLARGE, FontSize.XXXLARGE);

//		reportContainer.add(cardReportDoc);
//		reportContainer.add(cardReportListTasksOwner);
//		reportContainer.add(cardReportListTasksAssignee);
//		reportContainer.add(cardReportListTasksSupport);
//		reportContainer.add(cardReportListTasksFollower);
//		
		BsLayout bsLayout = new BsLayout();
		bsLayout.getStyle().setPadding("60px 100px");
		
		BsRow row = bsLayout.addRow();
		BsRow row2 = bsLayout.addRow();

		// Sử dụng 5 cột với tỷ lệ 20% mỗi cột để vừa với một hàng
		BsColumn bsColumn1 = row.addColumn(new BsColumn(cardReportDoc));
		bsColumn1.getStyle().set("flex", "1");

		BsColumn bsColumn2 = row.addColumn(new BsColumn(cardReportListTasksOwner));
		bsColumn2.getStyle().set("flex", "1");

		BsColumn bsColumn3 = row.addColumn(new BsColumn(cardReportListTasksAssignee));
		bsColumn3.getStyle().set("flex", "1");

		BsColumn bsColumn4 = row2.addColumn(new BsColumn(cardReportListTasksSupport));
		bsColumn4.getStyle().set("flex", "1");

		BsColumn bsColumn5 = row2.addColumn(new BsColumn(cardReportListTasksFollower));
		bsColumn5.getStyle().set("flex", "1");
		
		BsColumn bsColumn6 = row2.addColumn(new BsColumn(cardReportAchivement));
		bsColumn6.getStyle().set("flex", "1");

		// Đảm bảo row có display flex để các cột chia đều
		row.getStyle().set("display", "flex").set("gap", "15%");
		row2.getStyle().set("display", "flex").set("gap", "15%").setMarginTop("100px");



//		reportContainer.setFlexWrap(FlexWrap.WRAP);
//		reportContainer.setFlexDirection(FlexDirection.ROW);
//		reportContainer.getStyle().set("gap", "5px").setJustifyContent(JustifyContent.SPACE_BETWEEN);
//		reportContainer.setWidthFull();
		
		bsLayout.setWidthFull();

		vLayout.setWidthFull();
		vLayout.add(title,new Hr(),bsLayout);
		
		 bsLayout.getElement().getStyle().set("opacity", "0");
		    UI.getCurrent().getPage().executeJs("""
		        setTimeout(function() {
		            $0.style.opacity = '1';
		            $0.style.transition = 'opacity 0.5s ease';
		        }, 100);
		    """, bsLayout.getElement());
		    
		
		UI.getCurrent().getPage().retrieveExtendedClientDetails(e->{
			if(e.getScreenWidth() < 768) {
				vLayout.remove(bsLayout);
				flexLayoutMobile.add(cardReportDoc,cardReportListTasksOwner,cardReportListTasksAssignee,cardReportListTasksSupport,cardReportListTasksFollower);
				flexLayoutMobile.getStyle().setDisplay(Display.FLEX).setFlexWrap(FlexWrap.WRAP).set("gap", "20px");
				vLayout.add(flexLayoutMobile);
			}
		});
		
	}


	private void openDialogReportDoc() {
		DialogTemplate dialogTemplate = new DialogTemplate("BÁO CÁO VĂN BẢN");

		ReportDocForm reportDocForm = new ReportDocForm();
		dialogTemplate.add(reportDocForm);
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setSizeFull();
		dialogTemplate.open();
	}

	private void openDialogReportTaskOwner() {
		DialogTemplate dialogTemplate = new DialogTemplate("BÁO CÁO DANH SÁCH NHIỆM VỤ ĐÃ GIAO");

		ReportListTaskOwnerForm reportTaskForm = new ReportListTaskOwnerForm();
		dialogTemplate.add(reportTaskForm);
		dialogTemplate.setSizeFull();
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.open();

	}

	private void openDialogReportTaskAssignee() {
		DialogTemplate dialogTemplate = new DialogTemplate("BÁO CÁO DANH SÁCH NHIỆM VỤ ĐƯỢC GIAO");

		ReportListTaskAssigneeForm reportListTaskAssigneeForm = new ReportListTaskAssigneeForm();
		dialogTemplate.add(reportListTaskAssigneeForm);
		dialogTemplate.setSizeFull();
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.open();
	}

	private void openDialogReportTaskSupport() {
		DialogTemplate dialogTemplate = new DialogTemplate("BÁO CÁO DANH SÁCH NHIỆM VỤ HỖ TRỢ");

		ReportListTasksSupportForm reportListTaskAssigneeForm = new ReportListTasksSupportForm();
		dialogTemplate.add(reportListTaskAssigneeForm);
		dialogTemplate.setSizeFull();
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.open();
	}

	private void openDialogReportTaskFollower() {
		DialogTemplate dialogTemplate = new DialogTemplate("BÁO CÁO DANH SÁCH NHIỆM VỤ THEO DÕI");

		ReportListTasksFollowerForm reportListTaskAssigneeForm = new ReportListTasksFollowerForm();
		dialogTemplate.add(reportListTaskAssigneeForm);
		dialogTemplate.setSizeFull();
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.open();
	}
	
	private void openDialogReportTaskKpi() {
		DialogTemplate dialogTemplate = new DialogTemplate("BÁO CÁO KPI");

		ReportListTaskKpiForm reportListTaskKpiForm = new ReportListTaskKpiForm();
		dialogTemplate.add(reportListTaskKpiForm);
		
		dialogTemplate.setSizeFull();
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.open();
	}



}
