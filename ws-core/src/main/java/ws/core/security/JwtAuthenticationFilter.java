package ws.core.security;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ws.core.advice.AccessDeniedExceptionAdvice;
import ws.core.services.CasbinAuthService;
import ws.core.services.LogRequestService;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	@Autowired
	private JwtTokenProvider tokenProvider;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private CustomUserDetailsService customUserDetailsService;
	
	@Autowired
	private LogRequestService logRequestService;
	
	@Autowired
	private CasbinAuthService casbinAuthService;
	
	@Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		System.out.println("Co kiem tra jwt");
		System.out.println("Request: "+request.getRequestURL().toString());
		try {
			String jwt = jwtService.getJwtFromRequest(request);
			if(jwt!=null) {
		        String username = tokenProvider.extractUserName(jwt);
				if(StringUtils.isNotEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
					UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
					if(tokenProvider.isTokenValid(jwt, userDetails)) {
						
						/* Nếu có kích hoạt thì mới kiểm tra qua casbin */
						if(casbinAuthService.isEnable()) {
							String api = request.getRequestURI();
							//String action = request.getMethod();
							String action = "*";
							
							if(!casbinAuthService.isAllowed(username, api, action)) {
								throw new AccessDeniedExceptionAdvice("Không có quyền truy cập bởi casbin");
							}
						}
						
						UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
						authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
						
						SecurityContext context = SecurityContextHolder.createEmptyContext();
						context.setAuthentication(authentication);
						SecurityContextHolder.setContext(context);
						
						/* Ghi log request*/
						CustomUserDetails userCreator = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
						logRequestService.createLogRequest(request, userCreator.getUser());
					}
				}
			}else {
				 throw new AccessDeniedExceptionAdvice("Không xác thực được");
			}
		} catch (Exception e) {
			e.printStackTrace();
			resolver.resolveException(request, response, null, e);
			return;
		}
		filterChain.doFilter(request, response);
	}
}
