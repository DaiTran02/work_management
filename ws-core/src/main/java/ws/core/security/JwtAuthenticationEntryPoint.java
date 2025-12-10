package ws.core.security;

import java.io.IOException;
import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ws.core.model.response.ResponseAPI;

@SuppressWarnings("serial")
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

	@Autowired
	private ObjectMapper objectMapper;
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
		ResponseAPI responseAPI=new ResponseAPI();
		responseAPI.setStatus(HttpStatus.UNAUTHORIZED);
		responseAPI.setMessage("Lỗi không xác thực quyền truy cập");
		
		response.addHeader("Content-Type", "application/json; charset=UTF-8");
	    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	    objectMapper.writeValue(response.getOutputStream(), responseAPI);
	    response.flushBuffer();
	}
	
}
