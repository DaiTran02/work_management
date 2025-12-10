package ws.core.model.response.util;

import org.bson.Document;
import org.springframework.stereotype.Component;

import ws.core.model.Tag;

@Component
public class TagUtil {

	public Document toSiteResponse(Tag tag) {
		Document document=new Document();
		document.append("id", tag.getId());
		document.append("createdTime", tag.getCreatedTime());
		document.append("updatedTime", tag.getUpdatedTime());
		document.append("name", tag.getName());
		document.append("color", tag.getColor());
		document.append("creator", tag.getCreator());
		document.append("type", tag.getType());
		document.append("classIds", tag.getClassIds());
		document.append("active", tag.isActive());
		document.append("archive", tag.isArchive());
		return document;
	}
}
