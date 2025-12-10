package ws.core.model.response.util;

import org.bson.Document;
import org.springframework.stereotype.Component;

import ws.core.model.data.UserCodePublic;

@Component
public class UserCodePublicUtil {

	private Document toCommon(UserCodePublic userCodePublic) {
		Document document=new Document();
		document.put("code", userCodePublic.getToken());
		document.put("username", userCodePublic.getUsername());
		document.put("expired", userCodePublic.getExpiredLong());
		return document;
	}
	
	public Document toSiteResponse(UserCodePublic userCodePublic) {
		Document document=toCommon(userCodePublic);
		return document;
	} 
}
