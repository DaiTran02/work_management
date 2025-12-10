package com.ngn.tdnv.task.forms.details;

import com.ngn.interfaces.FormInterface;
import com.ngn.tdnv.task.forms.components.SliderComponent;
import com.ngn.tdnv.task.models.TaskRateModel;
import com.ngn.utils.components.RatingModule;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.dom.Style.AlignItems;

public class TaskRatingForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private RatingModule ratingModule = new RatingModule();
	private TextArea txtDesr = new TextArea("Nhận xét");
	private SliderComponent sliderMarkA = new SliderComponent("Kết quả hoạt động của đơn vị được giao phụ trách","");
	private SliderComponent sliderMarkB = new SliderComponent("Khả năng tổ chức triển khai thực hiện nhiệm vụ","");
	private SliderComponent sliderMarkC = new SliderComponent("Năng lực tập hợp, đoàn kết thực hiện nhiệm vụ","");
	private Span spTotal = new Span("Điểm tổng: 0/100 (0%)");
	
	
	private TaskRateModel taskRateModel;
	private boolean ratingByKpi;
	public TaskRatingForm(TaskRateModel taskRateModel,boolean ratingByKpi) {
		this.ratingByKpi = ratingByKpi;
		buildLayout();
		configComponent();
		if(taskRateModel != null) {
			this.taskRateModel = taskRateModel;
			loadData();
		}
	}
	
	@Override
	public void buildLayout() {
		this.setWidthFull();
		this.txtDesr.setWidthFull();
		this.add(new H5("*Bình chọn số Sao, để đánh giá thi đua."),ratingModule);
		this.add(txtDesr);
		if(ratingByKpi) {
			this.add(layoutRatingByKpi());
		}
		
	}

	@Override
	public void configComponent() {
		ratingModule.addChangeListener(e->{
			switch(ratingModule.getStar()) {
			case 1:
				txtDesr.setValue("Khác");
				sliderMarkA.setMark(0);
				sliderMarkB.setMark(0);
				sliderMarkC.setMark(0);
				break;
			case 2:
				txtDesr.setValue("Chưa hoàn thành");
				sliderMarkA.setMark(50);
				sliderMarkB.setMark(50);
				sliderMarkC.setMark(50);
				break;
			case 3:
				txtDesr.setValue("Tương đối hoành thành");
				sliderMarkA.setMark(99);
				sliderMarkB.setMark(99);
				sliderMarkC.setMark(99);
				break;
			case 4:
				txtDesr.setValue("Hoàn thành tốt");
				sliderMarkA.setMark(100);
				sliderMarkB.setMark(100);
				sliderMarkC.setMark(100);
				break;
			case 5:
				txtDesr.setValue("Hoàn thành xuất sắc");
				sliderMarkA.setMark(100);
				sliderMarkB.setMark(100);
				sliderMarkC.setMark(100);
				break;
			}
		});
		
		sliderMarkA.addChangeListener(e->calculateMark());
		sliderMarkB.addChangeListener(e->calculateMark());
		sliderMarkC.addChangeListener(e->calculateMark());
	}
	
	private Component layoutRatingByKpi() {
		VerticalLayout vLayout = new VerticalLayout();
		
		spTotal.getStyle().setFontSize("25px").setFontWeight(600);
		vLayout.add(spTotal);
		
		
		sliderMarkA.setWidthFull();
		sliderMarkB.setWidthFull();
		sliderMarkC.setWidthFull();
		
		vLayout.add(sliderMarkA,sliderMarkB,sliderMarkC);
		vLayout.setWidthFull();
		vLayout.getStyle().setAlignItems(AlignItems.CENTER);
		
		return vLayout;
	}
	
	private void calculateMark() {
		try {
			int totalPercent = (sliderMarkA.getMark() + sliderMarkB.getMark() + sliderMarkC.getMark()) / 3;
			int totalMark = (( totalPercent * 70 ) / 100 ) + 30;
			
			spTotal.setText("Điểm tổng: "+totalMark+"/100 ("+totalPercent+"%)");
		} catch (Exception e) {
		}
		
	}
	
	private void loadData() {
		this.ratingModule.setStar(taskRateModel.getStar());
		txtDesr.setHeight("200px");
		txtDesr.setValue(taskRateModel.getExplain());
	}
	
	public int getStar() {
		return this.ratingModule.getStar();
	}
	
	public String getExplain() {
		return this.txtDesr.getValue().toString();
	}

	public TextArea getTxtDesr() {
		return txtDesr;
	}

	public SliderComponent getSliderMarkA() {
		return sliderMarkA;
	}

	public SliderComponent getSliderMarkB() {
		return sliderMarkB;
	}

	public SliderComponent getSliderMarkC() {
		return sliderMarkC;
	}
	
}
