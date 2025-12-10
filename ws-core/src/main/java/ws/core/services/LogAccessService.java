package ws.core.services;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import ws.core.model.User;
import ws.core.model.filter.LogAccessFilter;

public interface LogAccessService {
	public void createLogAccess(HttpServletRequest request, User user);
	public List<String> getDistinctUsers(LogAccessFilter logAccessFilter);
}
