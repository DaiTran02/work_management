package ws.core.security.apikey;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	@Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;
	
	@Autowired
	private ApiKeyAuthenticationService apiKeyAuthenticationService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		System.out.println("Co kiem tra apikey");
		try {
			Authentication authentication = apiKeyAuthenticationService.getAuthentication((HttpServletRequest) request);
			
			SecurityContext context = SecurityContextHolder.createEmptyContext();
			context.setAuthentication(authentication);
			SecurityContextHolder.setContext(context);
		} catch (Exception e) {
            resolver.resolveException(request, response, null, e);
		}
		filterChain.doFilter(request, response);
	}
}
