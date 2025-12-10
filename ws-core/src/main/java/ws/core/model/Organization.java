package ws.core.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.EnumUtils;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import ws.core.enums.OrganizationLevel;
import ws.core.model.embeded.GroupOrganizationExpand;
import ws.core.model.embeded.RoleOrganizationExpand;
import ws.core.model.embeded.UserOrganizationExpand;

@Data
@Document(collection = "organization")
public class Organization {
	@Id
	@Field(value = "_id")
	private ObjectId id;
	
	@Indexed
	@Field(value = "createdTime")
	private Date createdTime;
	
	@Field(value = "updatedTime")
	private Date updatedTime;
	
	@Indexed
	@Field(value = "name")
	private String name;
	
	@Indexed
	@Field(value = "description")
	private String description;
	
	@Indexed
	@Field(value = "creatorId")
	private String creatorId;
	
	@Field(value="creatorName")
	private String creatorName;
	
	@Field(value="path")
	private String path;
	
	@Indexed
	@Field(value="parentId")
	private String parentId;
	
	@Indexed
	@Field(value="parentUnitCode")
	private String parentUnitCode;
	
	@Indexed
	@Field(value="parentIdSeconds")
	private LinkedList<String> parentIdSeconds=new LinkedList<>();
	
	@Indexed
	@Field(value="active")
	private boolean active;
	
	@Indexed
	@Field(value="archive")
	private boolean archive;
	
	@Indexed
	@Field(value="order")
	private int order;
	
	@Indexed(unique = true)
	@Field(value="unitCode")
	private String unitCode;
	
	@Indexed
	@Field(value="userOrganizationExpands")
	private LinkedList<UserOrganizationExpand> userOrganizationExpands=new LinkedList<>();
	
	@Indexed
	@Field(value="groupOrganizationExpands")
	private LinkedList<GroupOrganizationExpand> groupOrganizationExpands=new LinkedList<>();
	
	@Indexed
	@Field(value="roleOrganizationExpands")
	private LinkedList<RoleOrganizationExpand> roleOrganizationExpands=new LinkedList<>();
	
	@Indexed
	@Field(value="organizationCategoryId")
	private String organizationCategoryId;
	
	@Indexed
	@Field(value="syncExternal")
	private boolean syncExternal;
	
	@Indexed
	@Field(value="level")
	private String level;
	
	public Organization(){
		this.id=new ObjectId();
		this.createdTime=new Date();
		this.active=true;
		this.unitCode=id.toHexString();
		this.syncExternal=false;
		this.level=OrganizationLevel.room.getKey();
	}
	
	public ObjectId getObjectId() {
		return id;
	}
	public String getId() {
		return id.toHexString();
	}
	
	public long getCreatedTime() {
		if(createdTime!=null) {
			return createdTime.getTime();
		}
		return 0;
	}
	
	public long getUpdatedTime() {
		if(updatedTime!=null) {
			return updatedTime.getTime();
		}
		return 0;
	}
	
	public List<String> getAllUserIds() {
		return userOrganizationExpands.stream().map(e->e.getUserId()).collect(Collectors.toList());
	}
	
	public List<String> getAllUserNames() {
		return userOrganizationExpands.stream().map(e->e.getUserName()).collect(Collectors.toList());
	}
	
	public Optional<UserOrganizationExpand> getUserOrganizationExpand(String userId) {
		return userOrganizationExpands.stream().filter(e->e.getUserId().equals(userId)).findFirst();
	}
	
	public Optional<RoleOrganizationExpand> getRoleOrganizationExpand(String roleId){
		return roleOrganizationExpands.stream().filter(e->e.getRoleId().equals(roleId)).findFirst();
	}
	
	public Optional<GroupOrganizationExpand> getGroupOrganizationExpand(String groupId){
		return groupOrganizationExpands.stream().filter(e->e.getGroupId().equals(groupId)).findFirst();
	}
	
	public boolean hasUser(String userId) {
		return userOrganizationExpands.stream().anyMatch(e->e.getUserId().equals(userId));
	}
	
	public boolean hasRole(String roleId) {
		return roleOrganizationExpands.stream().anyMatch(e->e.getRoleId().equals(roleId));
	}
	
	public boolean hasPermissionInRoles(String permissionKey) {
		return roleOrganizationExpands.stream().filter(e->e.getPermissionKeys().contains(permissionKey)).count()>0;
	}
	
	public boolean hasPermissionUserRoles(String userId, String permissionKey) {
		return roleOrganizationExpands.stream().filter(e->e.getUserIds().contains(userId) && e.getPermissionKeys().contains(permissionKey)).count()>0;
	}
	
	public boolean hasGroup(String groupId) {
		return groupOrganizationExpands.stream().anyMatch(e->e.getGroupId().equals(groupId));
	}
	
	public List<RoleOrganizationExpand> getListRoleOrganizationExpandsOfUser(String userId){
		return roleOrganizationExpands.stream().filter(e->e.getUserIds().contains(userId)).collect(Collectors.toList());
	}
	
	public List<String> getAllPermissionOfUser(String userId){
		List<String> permissions=new ArrayList<>();
		List<RoleOrganizationExpand> roleOrganizationExpandsOfUser = getListRoleOrganizationExpandsOfUser(userId);
		for(RoleOrganizationExpand roleOrganizationExpandOfUser:roleOrganizationExpandsOfUser) {
			if(roleOrganizationExpandOfUser.getPermissionKeys().size()>0) {
				permissions.addAll(roleOrganizationExpandOfUser.getPermissionKeys());
			}
		}
		permissions=permissions.stream().distinct().collect(Collectors.toList());
		return permissions;
	}
	
	public boolean isRoot() {
		return getParentId()==null;
	}
	
	public OrganizationLevel getLevel() {
		if(level!=null && EnumUtils.isValidEnum(OrganizationLevel.class, level)) {
			return EnumUtils.getEnumIgnoreCase(OrganizationLevel.class, level);
		}
		return null;
	}
}
