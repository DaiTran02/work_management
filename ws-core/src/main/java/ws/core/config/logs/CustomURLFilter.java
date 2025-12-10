package ws.core.config.logs;

import java.io.IOException;
import java.util.Enumeration;
import java.util.UUID;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomURLFilter implements Filter {
	private static final String REQUEST_ID = "request_id";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		String requestId = UUID.randomUUID().toString();
		request.setAttribute(REQUEST_ID, requestId);
		logRequest((HttpServletRequest) request, requestId);
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {

	}

	private void logRequest(HttpServletRequest request, String requestId) {
		if (request != null) {
			StringBuilder data = new StringBuilder();
			data.append("\nLOGGING REQUEST-----------------------------------\n")
			.append("[REQUEST-ID]: ").append(requestId).append("\n")
			.append("[PATH]: ").append(request.getRequestURI()).append("\n")
			.append("[QUERIES]: ").append(request.getQueryString()).append("\n")
			.append("[HEADERS]: ").append("\n");

			Enumeration<String> headerNames = request.getHeaderNames();
			while (headerNames.hasMoreElements()) {
				String key = (String) headerNames.nextElement();
				String value = request.getHeader(key);
				data.append("---").append(key).append(" : ").append(value).append("\n");
			}
			data.append("LOGGING REQUEST-----------------------------------\n");
			log.info(data.toString());
		}
	}

}
