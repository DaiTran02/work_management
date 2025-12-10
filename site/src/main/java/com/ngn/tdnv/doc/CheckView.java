package com.ngn.tdnv.doc;

import com.ngn.api.ai.InputAiModel;
import com.ngn.api.ai.TestAiService;
import com.ngn.api.ai.TestModel;
import com.ngn.utils.components.ButtonTemplate;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle(value = "Công văn đi")
@Route(value = "check")
@AnonymousAllowed
public class CheckView extends VerticalLayout{
	private static final long serialVersionUID = 1L;
	
	private Span span = new Span();
	
	public CheckView() {
		TextField txt = new TextField("Hoi");
		ButtonTemplate btnHow = new ButtonTemplate("Hỏi");
		
		this.add(txt,btnHow);
		
		btnHow.addClickListener(e->{
			InputAiModel inputAiModel = new InputAiModel();
			inputAiModel.setModel("llama2");
			inputAiModel.setPrompt(txt.getValue());
			inputAiModel.setRaw(true);
			inputAiModel.setStream(false);
			how(inputAiModel);
		});
	}
	
	private void how(InputAiModel inputAiModel) {
		TestModel testModel = TestAiService.how(inputAiModel);
		span.removeAll();
		span.setText(testModel.getResponse());
		this.add(span);
	}
	
}
