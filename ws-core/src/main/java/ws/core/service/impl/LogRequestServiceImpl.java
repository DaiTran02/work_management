package ws.core.service.impl;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import ws.core.advice.NotFoundElementExceptionAdvice;
import ws.core.model.LogRequest;
import ws.core.model.User;
import ws.core.model.filter.LogRequestFilter;
import ws.core.respository.LogRequestRepository;
import ws.core.respository.LogRequestRepositoryCustom;
import ws.core.services.LogRequestService;

@Service
public class LogRequestServiceImpl implements LogRequestService{

	@Autowired
	private LogRequestRepository logRequestRepository;
	
	@Autowired
	private LogRequestRepositoryCustom logRequestRepositoryCustom;

	@Override
	public void createLogRequest(HttpServletRequest request, User user) {
		Thread thread=new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					LogRequest logRequest=new LogRequest();
					logRequest.setUserId(user.getId());
					logRequest.setMethod(request.getMethod().toString());
		    		logRequest.setRequestURL(request.getRequestURL().toString());
		        	logRequest.setAddremote(new URI(request.getRequestURL().toString()).getHost());
		        	logRequest.setProtocol(request.getProtocol().toString());
		        	logRequest.setQuery(request.getQueryString());
	    			
		    		logRequestRepository.save(logRequest);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
    	thread.start();
	}

	@Override
	public long countLogRequestAll(LogRequestFilter logRequestFilter) {
		return logRequestRepositoryCustom.countAll(logRequestFilter);
	}

	@Override
	public List<LogRequest> findLogRequestAll(LogRequestFilter logRequestFilter) {
		return logRequestRepositoryCustom.findAll(logRequestFilter);
	}

	@Override
	public LogRequest findLogRequestById(String id) {
		Optional<LogRequest> logRequest = logRequestRepository.findById(new ObjectId(id));
		if(logRequest.isPresent()) {
			return logRequest.get();
		}
		throw new NotFoundElementExceptionAdvice("Không tìm thấy nội dung phù hợp");
	}

}
