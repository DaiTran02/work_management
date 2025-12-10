package ws.core.services;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import ws.core.model.LogRequest;
import ws.core.model.User;
import ws.core.model.filter.LogRequestFilter;

public interface LogRequestService {
	public void createLogRequest(HttpServletRequest request, User user);
	
	public long countLogRequestAll(LogRequestFilter logRequestFilter);
	
	public List<LogRequest> findLogRequestAll(LogRequestFilter logRequestFilter);
	
	public LogRequest findLogRequestById(String id);
}
