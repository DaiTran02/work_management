package ws.core.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class PropsService {
	@Value("${ws.core.security.jwt.secret-key}")
	private String securityJwtSecretKey;
	
	@Value("${ws.core.security.jwt.expiration-seconds-time}")
	private long securityJwtExpirationSecondTime;
	
	@Value("${ws.core.security.api-partner.xapikey}")
	private String securityApiPartnerXApiKey;
	
	@Value("${ws.core.security.api-partner.rsa.public-key}")
	private String securityApiPartnerRSAPublicKey;
	
	@Value("${ws.core.security.api-partner.rsa.private-key}")
	private String securityApiPartnerRSAPrivateKey;
	
	@Value("${ws.core.security.api-partner.rsa.seconds-expiration}")
	private int securityApiPartnerRSASecondsExpiration;
	
	@Value("${ws.core.storages.folder.path}")
	private String storagesFolderPath;
	
	@Value("${ws.core.task.field.endtime.about-to-expire-days}")
	private int taskFieldEndtimeAboutToExpireDays;
	
	@Value("${ws.core.task.field.required-doc}")
	private boolean taskFieldRequiredDoc;
	
	@Value("${ws.core.security.auth-code.enable}")
	private boolean securityAuthCodeEnable;
	
	@Value("${ws.core.security.auth-code.expired-minutes}")
	private int securityAuthCodeExpiredMinutes;
	
	@Value("${ws.core.security.auth-code.useable-times}")
	private boolean securityAuthCodeUseableTimes;
	
	@Value("${ws.core.redis.enable}")
	private boolean redisEnable;
	
	@Value("${ws.core.redis.host}")
	private String redisHost;
	
	@Value("${ws.core.redis.port}")
	private int redisPort;
	
	@Value("${ws.core.casbin.enable}")
	private boolean casbinEnable;
	
	@Value("${ws.core.casbin.path.model}")
	private String casbinPathModel;
	
	@Value("${ws.core.casbin.path.policy}")
	private String casbinPathPolicy;
	
	@Value("${ws.core.otp.enable}")
	private boolean otpEnable;
	
	@Value("${ws.core.otp.url}")
	private String otpUrl;
	
	@Value("${ws.core.otp.xapikey}")
	private String otpXApiKey;
	
	@Value("${ws.core.otp.allow.add-user-org-from-partner}")
	private boolean allowAddUserOrgFromPartner;
	
	@Value("${ws.core.otp.allow.add-user-org-from-partner-ignore-otp-disable}")
	private boolean allowAddUserOrFromPartnerIgnoreOtpDisable;
	
	@Value("${ws.core.task.allow.view-organization-from-room}")
	private boolean allowViewOrganizationFromRoom;
	
	@Value("${ws.core.partner.api.url}")
	private String partnerApiUrl;
	
	@Value("${ws.core.partner.api.key}")
	private String partnerApiKey;
}
