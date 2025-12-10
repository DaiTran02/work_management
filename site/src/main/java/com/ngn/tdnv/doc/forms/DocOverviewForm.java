package com.ngn.tdnv.doc.forms;

import com.ngn.api.doc.ApiFilterSummaryDocModel;
import com.ngn.interfaces.FormInterface;
import com.ngn.tdnv.doc.models.DocModel;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.tabs.TabSheet;

public class DocOverviewForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private TabSheet tabSheet = new TabSheet();
	
	private boolean isViewDoc = false;
	private String idDoc;
	private DocModel docModel;
	private ApiFilterSummaryDocModel apiFilterSummaryDocModel;
	public DocOverviewForm(String idDoc,DocModel docModel,ApiFilterSummaryDocModel apiFilterSummaryDocModel,boolean isViewDoc) {
		this.idDoc = idDoc;
		this.docModel = docModel;
		this.apiFilterSummaryDocModel = apiFilterSummaryDocModel;
		this.isViewDoc = isViewDoc;
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(tabSheet);
		createLayout();
	}

	@Override
	public void configComponent() {
		
	}
	
	private void createLayout() {
		tabSheet.setSizeFull();
		DocInfoForm docInfoForm = new DocInfoForm(idDoc,isViewDoc);
		docInfoForm.addChangeListener(e->{
			fireEvent(new ClickEvent(this, false));
		});
		
		tabSheet.add("Thông tin văn bản", docInfoForm);
		
		if(docModel != null || apiFilterSummaryDocModel != null) {
			DocTreeTaskForm docTreeTaskForm = new DocTreeTaskForm(idDoc);
			
			ListTasksOfDocForm listTasksOfDocForm = new ListTasksOfDocForm(docModel, apiFilterSummaryDocModel);
			
			tabSheet.addSelectedChangeListener(e->{
				if(tabSheet.getSelectedIndex() == 2) {
					docTreeTaskForm.loadData();
				}
			});
			

			tabSheet.add("Nhiệm vụ đã giao từ văn bản", listTasksOfDocForm);
			tabSheet.add("Cây văn bản", docTreeTaskForm);
		}
	}

	public void setViewDoc(boolean isViewDoc) {
		this.isViewDoc = isViewDoc;
	}
}
