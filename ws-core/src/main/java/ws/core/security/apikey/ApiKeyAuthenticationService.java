package ws.core.security.apikey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import ws.core.services.RSASecurityService;

@Component
public class ApiKeyAuthenticationService {
    
	@Autowired
	private RSASecurityService rsaSecurityService;
	
    public Authentication getAuthentication(HttpServletRequest request) {
        String apiKey = request.getHeader("X-API-KEY");
        
		/* Kiểm tra valid với x-api-key */
        /*if (apiKey == null || !apiKey.equals(propsService.getSecurityApiPartnerXApiKey())) {
            throw new BadCredentialsException("API Key không xác thực");
        }*/
        
		/* Kiểm tra valid với rsa */
        if(!rsaSecurityService.isValid(apiKey)) {
        	throw new BadCredentialsException("X-API-KEY không xác thực");
        }
        
        return new ApiKeyAuthentication(apiKey, AuthorityUtils.NO_AUTHORITIES);
    }
}
