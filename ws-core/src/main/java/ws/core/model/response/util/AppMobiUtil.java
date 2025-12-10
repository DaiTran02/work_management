package ws.core.model.response.util;

import org.bson.Document;
import org.springframework.stereotype.Component;

import ws.core.model.AppMobi;

@Component
public class AppMobiUtil {
	
	private Document toCommon(AppMobi appMobi) {
		Document document=new Document();
		document.put("id", appMobi.getId());
		document.put("createdTime", appMobi.getCreatedTimeLong());
		document.put("updatedTime", appMobi.getUpdatedTimeLong());
		document.put("userId", appMobi.getUserId());
		document.put("username", appMobi.getUsername());
		document.put("fullName", appMobi.getFullName());
		document.put("deviceId", appMobi.getDeviceId());
		document.put("deviceName", appMobi.getDeviceName());
		document.put("longitute", appMobi.getLongitute());
		document.put("lagitute", appMobi.getLagitute());
		document.put("active", appMobi.isActive());
		return document;
	}
	
	public Document toAdminResponse(AppMobi appMobi) {
		Document document=toCommon(appMobi);
		return document;
	}
	
	public Document toSiteResponse(AppMobi appMobi) {
		Document document=toCommon(appMobi);
		return document;
	}
}
