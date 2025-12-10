package vn.com.ngn.api.report;

import java.util.List;

import lombok.Data;
import vn.com.ngn.api.organization.ApiKeyAndValueOrgModel;
import vn.com.ngn.utils.LocalDateUtils;

@Data
public class ApiOrganizationForReportModel {
	private String id;
    private long createdTime;
    private long updatedTime;
    private String name;
    private String description;
    private String creatorId;
    private String creatorName;
    private String path;
    private Object parentId;
    private List<String> parentIdSeconds;
    private ApiKeyAndValueOrgModel level;
    private boolean active;
    private int order;
    private String unitCode;
    private int countSubOrganization;
    private List<ApiOrganizationForReportModel> subOrganizations;
    private List<ApiUserOrganizationModel> userOrganizationExpands;
    
    public String getCountUser() {
    	return userOrganizationExpands == null ? "0" : userOrganizationExpands.size()+"";
    }
    
    public String getUnitCodeText() {
    	return unitCode == null ? "Đang cập nhật" : unitCode;
    }
    
    public String getCreateTimeText() {
    	return createdTime == 0 ? "Đang cập nhật" : LocalDateUtils.dfDateTime.format(createdTime);
    }
    
    public String getUpdateTimeText() {
    	return updatedTime == 0 ? "Chưa cập nhật" : LocalDateUtils.dfDateTime.format(updatedTime);
    }
}
