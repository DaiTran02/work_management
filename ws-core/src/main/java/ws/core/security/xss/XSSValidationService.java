package ws.core.security.xss;

import org.springframework.stereotype.Service;

@Service
public class XSSValidationService {
	
	public boolean sanitize(String input) {
        if (!XSSValidationUtils.isValidURL(input)) {
        	throw new XSSServletException("XSS attack error");
        }
        return true;
    }
}
