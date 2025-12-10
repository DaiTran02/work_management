package ws.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import ws.core.model.LogAccess;
import ws.core.model.User;
import ws.core.model.filter.LogAccessFilter;
import ws.core.respository.LogAccessRepository;
import ws.core.respository.LogAccessRepositoryCustom;
import ws.core.services.LogAccessService;

@Service
public class LogAccessServiceImpl implements LogAccessService{

	@Autowired
	private LogAccessRepository logAccessRepository;
	
	@Autowired
	private LogAccessRepositoryCustom logAccessRepositoryCustom;

	@Override
	public void createLogAccess(HttpServletRequest request, User user) {
		Thread thread=new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					LogAccess logAccess=new LogAccess();
					logAccess.setUserId(user.getId());
					logAccess.setUsername(user.getUsername());
					logAccess.setFullName(user.getFullName());
					logAccess.setUserAgent(request.getRemoteUser());
					logAccess.setIpAddress(request.getRemoteAddr());
					
		    		logAccessRepository.save(logAccess);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
    	thread.start();
	}

	@Override
	public List<String> getDistinctUsers(LogAccessFilter logAccessFilter) {
		return logAccessRepositoryCustom.getDistinctUsers(logAccessFilter);
	}

}
