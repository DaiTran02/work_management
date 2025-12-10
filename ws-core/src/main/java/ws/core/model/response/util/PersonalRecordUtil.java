package ws.core.model.response.util;

import org.bson.Document;
import org.springframework.stereotype.Component;

import ws.core.model.PersonalRecord;
import ws.core.model.embeded.PersonalRecordDetail;

@Component
public class PersonalRecordUtil {
	public Document toCommon(PersonalRecord personalRecord) {
		Document document = new Document();
		document.append("id", personalRecord.getId().toHexString());
		document.append("createdTime", personalRecord.getCreatedTime() == null ? 0 : personalRecord.getCreatedTime().getTime());
		document.append("updatedTime", personalRecord.getUpdatedTime() == null ? 0 : personalRecord.getUpdatedTime().getTime());
		document.append("title", personalRecord.getTitle());
		document.append("description", personalRecord.getDescription());
		document.append("currentUser", personalRecord.getCurrentUser());
		document.append("oldUsers", personalRecord.getOldUsers());
		document.append("usersTransfered", personalRecord.getUsersTransfered());
		document.append("transferTime", personalRecord.getTransferTime() == null ? 0 : personalRecord.getTransferTime().getTime());
		document.append("docs", personalRecord.getDocs());
		document.append("tasks", personalRecord.getTasks());
		document.append("creator", personalRecord.getCreator());
		return document;
	}
	
	public Document toDetail(PersonalRecordDetail personalRecordOutput) {
		Document document = new Document();
		
		document.append("id", personalRecordOutput.getId());
		document.append("createdTime", personalRecordOutput.getCreatedTime() == null ? 0 : personalRecordOutput.getCreatedTime().getTime());
		document.append("updatedTime", personalRecordOutput.getUpdatedTime() == null ? 0 : personalRecordOutput.getUpdatedTime().getTime());
		document.append("title", personalRecordOutput.getTitle());
		document.append("description", personalRecordOutput.getDescription());
		document.append("currentUser", personalRecordOutput.getCurrentUser());
		document.append("oldUsers", personalRecordOutput.getOldUsers());
		document.append("usersTransfered", personalRecordOutput.getUsersTransfered());
		document.append("transferTime", personalRecordOutput.getTransferTime() == null ? 0 : personalRecordOutput.getTransferTime().getTime());
		document.append("docs", personalRecordOutput.getDocs());
		document.append("tasks", personalRecordOutput.getTasks());
		document.append("creator", personalRecordOutput.getCreator());
		
		return document;
	}
	
}
