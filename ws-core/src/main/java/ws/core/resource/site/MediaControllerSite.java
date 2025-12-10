package ws.core.resource.site;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ws.core.model.Media;
import ws.core.model.request.ReqMediaUpload;
import ws.core.model.response.ResponseAPI;
import ws.core.model.response.util.MediaUtil;
import ws.core.services.FileLocalService;
import ws.core.services.MediaService;

@RestController
@RequestMapping("/api/site")
public class MediaControllerSite {
	
	@Autowired
	private MediaService mediaService;
	
	@Autowired
	private MediaUtil mediaUtil;
	
	@Autowired
	private FileLocalService fileLocalService;
	
	@PostMapping("/medias")
	public Object create(@ModelAttribute() @Validated ReqMediaUpload reqMediaUpload){
		ResponseAPI responseAPI=new ResponseAPI();
		
		Media docCreate = null;
		try {
			docCreate = mediaService.createMedia(reqMediaUpload);
		} catch (IOException e) {
			e.printStackTrace();
			responseAPI.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseAPI.setMessage("Lỗi hệ thống, vui lòng liên hệ quản trị viên");
			responseAPI.setError(e.getMessage());
			return responseAPI.build();
		}
		
		responseAPI.setStatus(HttpStatus.CREATED);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(mediaUtil.toResponse(docCreate));
		return responseAPI.build();
	}
	
	@GetMapping("/medias/{id}")
	public Object get(@PathVariable(name = "id", required = true) String id) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		Media media=mediaService.findById(id);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(mediaUtil.toResponse(media));
		return responseAPI.build();
	}
	
	@DeleteMapping("/medias/{id}")
	public Object delete(@PathVariable(name = "id", required = true) String id) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		mediaService.deleteById(id);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Đã xóa thành công");
		return responseAPI.build();
	}
	
	@GetMapping("/medias/{id}/get-content")
	public Object getAttachmentDoc(@PathVariable(name = "id", required = true) String id) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		Media media=mediaService.findById(id);
		
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(fileLocalService.getFile(media.getFilePath()));
		return responseAPI.build();
	}
	
}
