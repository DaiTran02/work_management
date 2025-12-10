package com.ngn.api.organization;

import java.util.List;

import lombok.Data;

@Data
public class ApiRoleOfOrgModel {
	public String roleId;
    public long createdTime;
    public long updatedTime;
    public String name;
    public String description;
    public String creatorId;
    public String creatorName;
    public List<String> permissionKeys;
    public List<String> userIds;
    public Object roleTemplateId;
    public String type;
}
