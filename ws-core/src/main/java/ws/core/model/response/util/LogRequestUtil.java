package ws.core.model.response.util;

import org.bson.Document;
import org.springframework.stereotype.Component;

import ws.core.model.LogRequest;

@Component
public class LogRequestUtil {
	
	private Document toCommon(LogRequest logRequest) {
		Document document=new Document();
		document.append("id", logRequest.getId());
		document.append("createdTime", logRequest.getCreatedTimeLong());
		document.append("method", logRequest.getMethod());
		document.append("protocol", logRequest.getProtocol());
		document.append("query", logRequest.getQuery());
		document.append("userAgent", logRequest.getUserAgent());
		document.append("addRemote", logRequest.getAddremote());
		document.append("requestURL", logRequest.getRequestURL());
		return document;
	}
	
	public Document toAdminResponse(LogRequest logRequest) {
		Document document=toCommon(logRequest);
		return document;
	}
	
	public Document toSiteResponse(LogRequest logRequest) {
		Document document=toCommon(logRequest);
		return document;
	}
}
