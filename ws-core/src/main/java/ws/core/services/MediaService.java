package ws.core.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import ws.core.model.Media;
import ws.core.model.filter.MediaFilter;
import ws.core.model.request.ReqMediaUpload;
import ws.core.model.request.ReqMediaUploadPartner;

public interface MediaService {
	public Media findById(String mediaId);
	
	public Media createMedia(ReqMediaUpload reqMediaUpload) throws FileNotFoundException, IOException;
	
	public Media createMedia(ReqMediaUploadPartner reqMediaUploadPartner) throws FileNotFoundException, IOException;
	
	public Media deleteById(String mediaId);
	
	public List<Media> findAll(MediaFilter mediaFilter);
	
	public Optional<Media> findOne(MediaFilter mediaFilter);
	
	public long countAll(MediaFilter mediaFilter);
}