package ws.core.model.request.embeded;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.model.embeded.Creator;
import ws.core.model.embeded.CreatorInfo;
import ws.core.validation.ValidObjectId;
import ws.core.validation.ValidStringMedium;

@Data
public class ReqCreator {
	@NotNull(message = "organizationId không được trống")
	@ValidObjectId(message = "organizationId không hợp lệ")
	private String organizationId;
	
	@NotNull(message = "organizationName không được trống")
	@ValidStringMedium(message = "organizationName không được chứa các ký tự đặc biệt")
	private String organizationName;
	
	@NotNull(message = "organizationUserId không được trống")
	@ValidObjectId(message = "organizationUserId không hợp lệ")
	private String organizationUserId;
	
	@NotNull(message = "organizationUserName không được trống")
	@ValidStringMedium(message = "organizationUserName không được chứa các ký tự đặc biệt")
	private String organizationUserName;
	
	@ValidStringMedium(message = "jobTitle không được chứa các ký tự đặc biệt")
	private String jobTitle;
	
	public Creator toCreator() {
		Creator creator=new Creator();
		creator.setOrganizationId(getOrganizationId());
		creator.setOrganizationName(getOrganizationName());
		creator.setOrganizationUserId(getOrganizationUserId());
		creator.setOrganizationUserName(getOrganizationUserName());
		creator.setJobTitle(getJobTitle());
		return creator;
	}
	
	public CreatorInfo toCreatorInfo() {
		CreatorInfo creatorInfo=new CreatorInfo();
		creatorInfo.setOrganizationId(getOrganizationId());
		creatorInfo.setOrganizationName(getOrganizationName());
		creatorInfo.setOrganizationUserId(getOrganizationUserId());
		creatorInfo.setOrganizationUserName(getOrganizationUserName());
		creatorInfo.setJobTitle(getJobTitle());
		return creatorInfo;
	}
}
