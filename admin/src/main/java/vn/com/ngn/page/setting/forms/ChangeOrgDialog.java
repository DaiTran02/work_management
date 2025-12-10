package vn.com.ngn.page.setting.forms;

import java.util.List;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.organization.ApiOrganizationModel;
import vn.com.ngn.api.organization.ApiOrganizationService;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.user.model.BelongOrganizationsModel;
import vn.com.ngn.utils.SessionUtil;
import vn.com.ngn.utils.components.DialogTemplate;

public class ChangeOrgDialog extends DialogTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private HorizontalLayout hLayout = new HorizontalLayout();

	private List<BelongOrganizationsModel> listBelongOrganizationModels;
	private boolean checkChangeOrg;
	public ChangeOrgDialog(List<BelongOrganizationsModel> belongOrganizationModels,boolean checkChangeOrg) {
		this.listBelongOrganizationModels = belongOrganizationModels;
		this.checkChangeOrg = checkChangeOrg;
		loadData();
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		//The class in file dialog.css
		addClassNames("image-gallery-view");
		addClassNames(Margin.Horizontal.AUTO, Padding.Bottom.LARGE, Padding.Horizontal.LARGE);
		this.setHeaderTitle("Chọn đơn vị quản trị");
		this.setWidth("auto");
		this.add(hLayout);
		this.setCloseOnOutsideClick(false);
	}
	
	public void configComponent() {
		this.getBtnClose().addClickListener(e->this.close());
	}
	
	public void loadData() {
		if(listBelongOrganizationModels.size() == 1) {
			SessionUtil.setOrgId(listBelongOrganizationModels.get(0).getOrganizationId());
		}else {
			for(BelongOrganizationsModel belongOrganizationsModel : listBelongOrganizationModels) {
				CardOrgForm cardOrgForm = new CardOrgForm(belongOrganizationsModel,checkChangeOrg);
				cardOrgForm.addChangeListener(e->this.close());
				hLayout.add(cardOrgForm);
			}
		}
	}
	
	@SuppressWarnings("unused")
	private ApiOrganizationModel getOrg(String id) {
		try {
			ApiResultResponse<ApiOrganizationModel> getData = ApiOrganizationService.getOneOrg(id);
			return getData.getResult();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<BelongOrganizationsModel> getListOrg(){
		return this.listBelongOrganizationModels;
	}

}
