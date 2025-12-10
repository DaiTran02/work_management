package ws.core.model.response.util;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ws.core.model.EventCalendarFile;

@Component
public class EventCalendarFileUtil {
	
	@Autowired
	private MediaUtil mediaUtil;
	
	public Document toCommon(EventCalendarFile eventCalendarFile) {
		Document document=new Document();
		document.append("id", eventCalendarFile.getId());
		document.append("createdTime", eventCalendarFile.getCreatedTime());
		document.append("updatedTime", eventCalendarFile.getUpdatedTime());
		document.append("time", eventCalendarFile.getTimeLong());
		document.append("attachments", mediaUtil.getMedias(eventCalendarFile.getAttachments()));
		document.append("creator", eventCalendarFile.getCreator());
		return document;
	}
	
	public Document toSiteResponse(EventCalendarFile eventCalendarFile) {
		return toCommon(eventCalendarFile);
	}
}
