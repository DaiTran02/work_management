package ws.core.model.response.util;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ws.core.model.Media;
import ws.core.model.filter.MediaFilter;
import ws.core.services.MediaService;

@Component
public class MediaUtil {
	
	@Autowired
	private MediaService mediaService;
	
	private Document toCommon(Media media) {
		Document document=new Document();
		document.put("id", media.getId());
		document.put("createdTime", media.getCreatedTimeLong());
		document.put("updatedTime", media.getUpdatedTimeLong());
		document.put("fileName", media.getFileName());
		document.put("fileDescription", media.getFileDescription());
		document.put("fileType", media.getFileType());
		document.put("fileSize", media.getFileSize());
		document.put("filePath", media.getFilePath());
		document.put("creator", media.getCreator());
		return document;
	}
	
	public Document toResponse(Media media) {
		Document document=toCommon(media);
		return document;
	}
	
	public List<Document> getMedias(List<String> mediaIds) {
		List<Document> result=new ArrayList<Document>();
		if(mediaIds.size()>0) {
			MediaFilter mediaFilter=new MediaFilter();
			mediaFilter.setIds(mediaIds);
			List<Media> medias=mediaService.findAll(mediaFilter);
			for (Media eventResource : medias) {
				result.add(toResponse(eventResource));
			}
		}
		return result;
	}
}
