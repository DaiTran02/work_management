package ws.core.security.xss;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.Part;

public class XSSRequestWrapperSanitize extends HttpServletRequestWrapper{

	public XSSRequestWrapperSanitize(HttpServletRequest request) {
		super(request);
	}

	/***
	 * XSS path URI
	 */
	@Override
	public String getRequestURI() {
		try {
			sanitizeXSS(URLDecoder.decode(super.getRequestURI(), StandardCharsets.UTF_8.name()));
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
            	values[index]=sanitizeXSS(values[index]);
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
		return sanitizeXSS(value);
	}

	/**
	 * XSS header
	 */
	@Override
	public String getHeader(String name){
		String value = super.getHeader(name);
		return sanitizeXSS(value);
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
		
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
        ServletInputStream servletInputStream = new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
        return servletInputStream;
	}
	
	@Override
	public BufferedReader getReader() throws IOException {
		return super.getReader();
	}
	
	/**
	 * XSS form-data
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
	
}
