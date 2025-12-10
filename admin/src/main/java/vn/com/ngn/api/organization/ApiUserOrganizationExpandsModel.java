package vn.com.ngn.api.organization;

import lombok.Data;
import vn.com.ngn.api.report.ApiMoreInfoModel;
import vn.com.ngn.page.organization.models.UserOrganizationExpandsModel;

@Data
public class ApiUserOrganizationExpandsModel {
    private String userId;
    private long createdTime;
    private long updatedTime;
    private String userName;
    private String positionName;
    private String accountIOffice;
    private boolean active;
    private ApiMoreInfoModel moreInfo;
    private String status;
    
    public ApiUserOrganizationExpandsModel() {
    	
    }
    
    public ApiUserOrganizationExpandsModel(UserOrganizationExpandsModel userOrganizationExpandsModel) {
    	this.userId = userOrganizationExpandsModel.getUserId();
    	this.createdTime = userOrganizationExpandsModel.getCreatedTime();
    	this.updatedTime = userOrganizationExpandsModel.getUpdatedTime();
    	this.userName = userOrganizationExpandsModel.getUserName();
    	this.positionName = userOrganizationExpandsModel.getPositionName();
    	this.accountIOffice = userOrganizationExpandsModel.getAccountIOffice();
    	this.active = userOrganizationExpandsModel.isActive();
    }
}
