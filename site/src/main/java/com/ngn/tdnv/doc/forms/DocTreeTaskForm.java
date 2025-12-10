package com.ngn.tdnv.doc.forms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.ngn.api.doc.ApiDocService;
import com.ngn.api.doc.ApiDocTreeTaskModel;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.interfaces.FormInterface;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class DocTreeTaskForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private VerticalLayout vLayout = new VerticalLayout();
	private Map<String, Pair<String, String>> map = new HashMap<String, Pair<String,String>>();
	private String dataString = "";
	private String idDoc;
	public DocTreeTaskForm(String idDoc) {
		this.idDoc = idDoc;
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(vLayout);
	}

	@Override
	public void configComponent() {
		
	}
	
	public void loadData() {
		dataString = "";
		map.clear();
		ApiResultResponse<List<ApiDocTreeTaskModel>> getTreeDocTask = ApiDocService.getTreeDocTask(idDoc);
		for(ApiDocTreeTaskModel apiDocTreeTaskModel :  getTreeDocTask.getResult()) {
			dataString += "["
							+ "'"+apiDocTreeTaskModel.getFromOrganization().getOrganizationId()+"',"
							+ "'"+apiDocTreeTaskModel.getToOrganization().getOrganizationId()+"'"
					+ "],";
			map.put(apiDocTreeTaskModel.getFromOrganization().getOrganizationId(), 
					Pair.of(apiDocTreeTaskModel.getFromOrganization().getOrganizationName(),apiDocTreeTaskModel.getDescription()));
			map.put(apiDocTreeTaskModel.getToOrganization().getOrganizationId(), 
					Pair.of(apiDocTreeTaskModel.getToOrganization().getOrganizationName(),apiDocTreeTaskModel.getDescription()));
			if(apiDocTreeTaskModel.getChildTasks() != null && !apiDocTreeTaskModel.getChildTasks().isEmpty()) {
				getChilds(apiDocTreeTaskModel.getChildTasks());
			}
		}
		createLayout(dataString);
	}
	
	private void getChilds(List<ApiDocTreeTaskModel> listChilds) {
		for(ApiDocTreeTaskModel model : listChilds) {
			dataString += "["
					+ "'"+model.getFromOrganization().getOrganizationId()+"',"
					+ "'"+model.getToOrganization().getOrganizationId()+"'"
			+ "],";
			map.put(model.getFromOrganization().getOrganizationId(), 
					Pair.of(model.getFromOrganization().getOrganizationName(),model.getDescription()));
			map.put(model.getToOrganization().getOrganizationId(), 
					Pair.of(model.getToOrganization().getOrganizationName(),model.getDescription()));
			if(model.getChildTasks() != null && !model.getChildTasks().isEmpty()) {
				getChilds(model.getChildTasks());
			}
		}
	}

	private void createLayout(String dataString) {
		vLayout.removeAll();
		Div div = new Div();
		
		String infoOfOrg = "";
		
		for(Map.Entry<String, Pair<String, String>> m : map.entrySet()) {
			infoOfOrg += "{"
							+ "id:'"+m.getKey()+"',"
							+ "title:'"+m.getValue().getValue()+"',"
							+ "name:'"+m.getValue().getKey()+"'"
					+ "},";
		}
		
		Html html = new Html("<script>"
				+ "Highcharts.chart('container', { "
				+ "    chart: { "
				+ "        height: 600, "
				+ "        inverted: true "
				+ "    }, "
				+ " "
				+ "    title: { "
				+ "        text: 'Biểu đồ nhiệm vụ đã giao từ văn bản' "
				+ "    }, "
				+ " "
			    + "    credits: {"
			    + "        enabled: false"
			    + "    },"
				+ "    accessibility: { "
				+ "        point: { "
				+ "            descriptionFormat: '{add index 1}. {toNode.name}' + "
				+ "                '{#if (ne toNode.name toNode.id)}, {toNode.id}{/if}, ' + "
				+ "                'reports to {fromNode.id}' "
				+ "        } "
				+ "    }, "
				+ " "
				+ "    series: [{ "
				+ "        type: 'organization', "
				+ "        name: 'Highsoft', "
				+ "        keys: ['from', 'to'], "
				+ "        data: [ "
				+				dataString
				+ "        ], "
				+ "        levels: [{ "
				+ "            level: 0, "
				+ "            color: 'silver', "
				+ "            dataLabels: { "
				+ "                color: 'black' "
				+ "            }, "
				+ "            height: 25 "
				+ "        }, { "
				+ "            level: 1, "
				+ "            color: 'silver', "
				+ "            dataLabels: { "
				+ "                color: 'black' "
				+ "            }, "
				+ "            height: 25 "
				+ "        }, { "
				+ "            level: 2, "
				+ "            color: '#ddbed2' "
				+ "        }, "
				+ "{ "
				+ "            level: 4, "
				+ "            color: '#359154' "
				+ "        },"
				+ "{ "
				+ "            level: 5, "
				+ "            color: '#359154' "
				+ "        },"
				+ "], "
				+ "        nodes: ["+infoOfOrg+"], "
				+ "        colorByPoint: false, "
				+ "        color: '#007ad0', "
				+ "        dataLabels: { "
				+ "            color: 'white' "
				+ "        }, "
				+ "        borderColor: 'white', "
				+ "        nodeWidth: 'auto' "
				+ "    }], "
				+ "    tooltip: { "
				+ "        outside: true "
				+ "    }, "
				+ "    exporting: { "
				+ "        allowHTML: true, "
				+ "        sourceWidth: 800, "
				+ "        sourceHeight: 600 "
				+ "    } "
				+ " "
				+ "});"
				+ "</script>");
		
		div.setId("container");
		div.add(html);
		div.setSizeFull();
		vLayout.add(div);
	}

}
