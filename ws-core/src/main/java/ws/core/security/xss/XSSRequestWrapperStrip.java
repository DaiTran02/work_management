package ws.core.security.xss;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import org.springframework.web.util.HtmlUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.Part;

public class XSSRequestWrapperStrip extends HttpServletRequestWrapper{

	public XSSRequestWrapperStrip(HttpServletRequest request) {
		super(request);
	}

	/***
	 * XSS path URI
	 */
	@Override
	public String getRequestURI() {
		try {
			return stripXSS(URLDecoder.decode(super.getRequestURI(), StandardCharsets.UTF_8.name()));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return super.getRequestURI();
	}
	
	/**
	 * XSS parameters
	 */
	@Override
	public String[] getParameterValues(String parameter){
		String values[] = super.getParameterValues(parameter);
        if (null != values) {
            for (int index = 0; index < values.length; index++) {
            	values[index]=stripXSS(values[index]);
            }
        }
        return values;
	}

	/**
	 * XSS parameter
	 */
	@Override
	public String getParameter(String parameter){
		String value = super.getParameter(parameter);
		return stripXSS(value);
	}

	/**
	 * XSS header
	 */
	@Override
	public String getHeader(String name){
		String value = super.getHeader(name);
		return stripXSS(value);
	}
	
	/**
	 * XSS body
	 */
	@Override
	public ServletInputStream getInputStream() throws IOException {
		String body=new String(super.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
		if(!XSSValidationUtils.isValidBody(body)) {
			throw new XSSServletException("XSS attack (body) error");
		}
		return super.getInputStream();
	}
	
	/**
	 * XSS form
	 */
	@Override
	public Collection<Part> getParts() throws IOException, ServletException {
		Collection<Part> parts = super.getParts();
		for (Part part : parts) {
			sanitizeXSS(part.getName());
			if(part.getSubmittedFileName()!=null) {
				
			}else {
				sanitizeXSS(new String(part.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
			}
		}
		return parts;
	}
	
	private String sanitizeXSS(String input) {
        if (input!=null && !XSSValidationUtils.isValidURL(input)) {
        	throw new XSSServletException("XSS attack error ["+input+"]");
        }
        return input;
    }
	
	private String stripXSS(String value){
		if(value!=null)
			return HtmlUtils.htmlEscape(value, StandardCharsets.UTF_8.name());
		return value;
	}
}
