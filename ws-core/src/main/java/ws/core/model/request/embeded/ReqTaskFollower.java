package ws.core.model.request.embeded;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.model.embeded.TaskFollower;
import ws.core.validation.ValidObjectId;
import ws.core.validation.ValidStringMedium;

@Data
public class ReqTaskFollower {
	@NotNull(message = "organizationId không được trống")
	@ValidObjectId(message = "organizationId không hợp lệ")
	private String organizationId;
	
	@NotNull(message = "organizationName không được trống")
	@ValidStringMedium(message = "organizationName không được chứa các ký tự đặc biệt")
	private String organizationName;
	
	@ValidObjectId(message = "organizationGroupId không hợp lệ")
	private String organizationGroupId;
	
	@ValidStringMedium(message = "organizationGroupName không được chứa các ký tự đặc biệt")
	private String organizationGroupName;
	
	@ValidObjectId(message = "organizationUserId không hợp lệ")
	private String organizationUserId;
	
	@ValidStringMedium(message = "organizationUserName không được chứa các ký tự đặc biệt")
	private String organizationUserName;
	
	public TaskFollower toTaskFollower() {
		TaskFollower organizationGroupUser=new TaskFollower();
		organizationGroupUser.setOrganizationId(getOrganizationId());
		organizationGroupUser.setOrganizationName(getOrganizationName());
		organizationGroupUser.setOrganizationGroupId(getOrganizationGroupId());
		organizationGroupUser.setOrganizationGroupName(getOrganizationGroupName());
		organizationGroupUser.setOrganizationUserId(getOrganizationUserId());
		organizationGroupUser.setOrganizationUserName(getOrganizationUserName());
		return organizationGroupUser;
	}
}
