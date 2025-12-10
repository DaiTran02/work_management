package ws.core.model.response.util;

import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.stereotype.Component;

import ws.core.model.embeded.TaskComment;

@Component
public class TaskCommentUtil {

	public Document toCommon(TaskComment taskComment) {
		Document document=new Document();
		document.append("id", taskComment.getId());
		document.append("createdTime", taskComment.getCreatedTimeLong());
		document.append("updatedTime", taskComment.getUpdatedTimeLong());
		document.append("message", taskComment.getMessage());
		document.append("attachments", taskComment.getAttachments());
		document.append("replies", taskComment.getReplies().stream().map(e->toCommon(e)).collect(Collectors.toList()));
		document.append("creator", taskComment.getCreator());
		return document;
	}
	
	public Document toSiteReponse(TaskComment taskComment) {
		return toCommon(taskComment);
	}
}
