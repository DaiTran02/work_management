package ws.core.model.response.util;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ws.core.enums.OrganizationLevel;
import ws.core.model.Organization;
import ws.core.model.RefreshToken;
import ws.core.model.User;
import ws.core.model.User.BelongOrganization;
import ws.core.model.embeded.FirstReview;
import ws.core.security.JwtTokenProvider;
import ws.core.services.OrganizationService;
import ws.core.services.PropsService;
import ws.core.services.RefreshTokenService;

@Component
public class UserUtil{
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	private RefreshTokenService refreshTokenService;
	
	@Autowired
	private PropsService propsService;
	
	@Autowired
	private OrganizationService organizationService;
	
	private Document toLogin(User user) {
		Document document=new Document();
		document.put("id", user.getId());
		document.put("createdTime", user.getCreatedTimeLong());
		document.put("updatedTime", user.getUpdatedTimeLong());
		document.put("username", user.getUsername());
		document.put("email", user.getEmail());
		document.put("phone", user.getPhone());
		document.put("fullName", user.getFullName());
		document.put("jobTitle", user.getJobTitle());
		document.put("creatorId", user.getCreatorId());
		document.put("creatorName", user.getCreatorName());
		document.put("active", user.isActive());
		document.put("archive", user.isArchive());
		document.put("activeCode", user.getActiveCode());
		document.put("lastDateLogin", user.getLastDateLoginLong());
		document.put("lastChangePassword", user.getLastChangePasswordLong());
		document.put("lastIPLogin", user.getLastIPLogin());
		
		List<BelongOrganization> belongOrganizations=new ArrayList<>();
		if(user.getBelongOrganizations().size()>0) {
			belongOrganizations.addAll(user.getBelongOrganizations());
		}
		
		List<BelongOrganization> belongParentOrganizations=new ArrayList<>();
		if(user.getBelongOrganizations().size()>0) {
			/* Kiểm tra quyền có được view cấp cha không, mặc định là luôn luôn */
			if(propsService.isAllowViewOrganizationFromRoom()) {
				for(BelongOrganization belongOrganization:user.getBelongOrganizations()) {
					try {
						Organization organization=organizationService.getOrganizationById(belongOrganization.getOrganizationId());
						if(organization.getLevel()!=null && organization.getLevel().equals(OrganizationLevel.room)) {
							Organization organizationParent=organizationService.getOrganizationById(organization.getParentId());
							BelongOrganization belongOrganizationParent=new BelongOrganization();
							belongOrganizationParent.setOrganizationId(organizationParent.getId());
							belongOrganizationParent.setOrganizationName(organizationParent.getName());
							
							belongParentOrganizations.add(belongOrganizationParent);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				if(belongParentOrganizations.size()>0) {
					belongOrganizations.addAll(belongParentOrganizations);
				}
			}
		}
		
		document.put("belongOrganizations", belongOrganizations);
		document.put("belongParentOrganizations", belongParentOrganizations);
		document.put("firstReview", convertFirstReview(user.getFirstReview()));
		document.put("guideWebUI", user.isGuideWebUI());
		document.put("provider", user.getProvider());
		return document;
	}
	
	private Document toCommon(User user) {
		Document document=new Document();
		document.put("id", user.getId());
		document.put("createdTime", user.getCreatedTimeLong());
		document.put("updatedTime", user.getUpdatedTimeLong());
		document.put("username", user.getUsername());
		document.put("email", user.getEmail());
		document.put("phone", user.getPhone());
		document.put("fullName", user.getFullName());
		document.put("jobTitle", user.getJobTitle());
		document.put("creatorId", user.getCreatorId());
		document.put("creatorName", user.getCreatorName());
		document.put("active", user.isActive());
		document.put("archive", user.isArchive());
		document.put("activeCode", user.getActiveCode());
		document.put("lastDateLogin", user.getLastDateLoginLong());
		document.put("lastChangePassword", user.getLastChangePasswordLong());
		document.put("lastIPLogin", user.getLastIPLogin());
		document.put("belongOrganizations", user.getBelongOrganizations());
		document.put("firstReview", convertFirstReview(user.getFirstReview()));
		document.put("guideWebUI", user.isGuideWebUI());
		document.put("provider", user.getProvider());
		return document;
	}
	
	public Document toAdminResponse(User user) {
		Document document=toCommon(user);
		return document;
	}
	
	public Document toSiteResponse(User user) {
		Document document=toCommon(user);
		return document;
	}
	
	public Document toSiteLoginResponse(User user) {
		RefreshToken refreshToken=refreshTokenService.createRefreshToken(user.getId());
		
		Document document=toLogin(user);
		document.put("loginToken", jwtTokenProvider.generateToken(user.getUsername()));
		document.put("expiryToken", refreshToken.getExpiryTimeLong());
		document.put("refeshToken", refreshToken.getRefreshToken());
		
		return document;
	}
	
	public Document toAdminLoginResponse(User user) {
		RefreshToken refreshToken=refreshTokenService.createRefreshToken(user.getId());
		
		Document document=toCommon(user);
		document.put("loginToken", jwtTokenProvider.generateToken(user.getUsername()));
		document.put("expiryToken", refreshToken.getExpiryTimeLong());
		document.put("refeshToken", refreshToken.getRefreshToken());
		
		return document;
	}
	
	public Document convertFirstReview(FirstReview firstReview) {
		if(firstReview==null) {
			return null;
		}
		
		Document document=new Document();
		document.append("createdTime", firstReview.getCreatedTimeLong());
		document.append("choiceOrganizationId", firstReview.getChoiceOrganizationId());
		document.append("choiceOrganizationGroupId", firstReview.getChoiceOrganizationGroupId());
		document.append("choiceOrganizationRoleId", firstReview.getChoiceOrganizationRoleId());
		document.append("reviewed", firstReview.isReviewed());
		document.append("reviewedTime", firstReview.getReviewedTimeLong());
		return document;
	}
	
}
