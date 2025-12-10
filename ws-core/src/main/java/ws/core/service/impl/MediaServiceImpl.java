package ws.core.service.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ws.core.advice.BadRequestExceptionAdvice;
import ws.core.advice.NotFoundElementExceptionAdvice;
import ws.core.model.Media;
import ws.core.model.Organization;
import ws.core.model.User;
import ws.core.model.embeded.Creator;
import ws.core.model.filter.MediaFilter;
import ws.core.model.request.ReqMediaUpload;
import ws.core.model.request.ReqMediaUploadPartner;
import ws.core.respository.MediaRepository;
import ws.core.respository.MediaRepositoryCustom;
import ws.core.security.xss.XSSValidationService;
import ws.core.services.FileLocalService;
import ws.core.services.MediaService;
import ws.core.services.OrganizationService;
import ws.core.services.UserService;
import ws.core.util.CommonUtil;
import ws.core.util.DateTimeUtil;

@Service
public class MediaServiceImpl implements MediaService{

	@Autowired
	private MediaRepository mediaRepository;
	
	@Autowired
	private MediaRepositoryCustom mediaRepositoryCustom;
	
	@Autowired
	private XSSValidationService xssValidationService;
	
	@Autowired
	private FileLocalService fileLocalService;

	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private UserService userService;
	
	@Override
	public Media findById(String mediaId) {
		Optional<Media> findMedia=mediaRepository.findById(new ObjectId(mediaId));
		if(findMedia.isPresent()) {
			return findMedia.get();
		}
		throw new NotFoundElementExceptionAdvice("Media không tồn tại");
	}

	@Override
	public Media createMedia(ReqMediaUpload reqMediaUpload) throws IOException {
		Media media=new Media();
		
		File uploadRootDir = new File(fileLocalService.getPathAttachments());
		/* Tạo thư mục gốc upload nếu nó không tồn tại. */
        if (!uploadRootDir.exists()) {
            uploadRootDir.mkdirs();
        }
        
        MultipartFile file = reqMediaUpload.getFile();
        if(file==null || file.isEmpty()) {
        	throw new BadRequestExceptionAdvice("file đính kèm rỗng");
        }
		String description=reqMediaUpload.getDescription();
		
		Creator creator=new Creator();
		creator.setOrganizationId(reqMediaUpload.getOrganizationId());
		creator.setOrganizationName(reqMediaUpload.getOrganizationName());
		creator.setOrganizationUserId(reqMediaUpload.getOrganizationUserId());
		creator.setOrganizationUserName(reqMediaUpload.getOrganizationUserName());
		
        /* Valid XSS */
        xssValidationService.sanitize(file.getName());
        xssValidationService.sanitize(file.getOriginalFilename());
        
        /* Phân theo folder năm/tháng/ngày */
        String date=DateTimeUtil.getDateFolder().format(new Date());
        
		/* Tên file gốc tại Client. */
        media.setFileName(file.getOriginalFilename());
        media.setFileType(file.getContentType());
        media.setFilePath(date+ "/"+media.getId()+"_"+CommonUtil.toFilename(file.getOriginalFilename()));
        media.setFileDescription(description);
        
        File folderFile = new File(uploadRootDir.getAbsolutePath() + File.separator + date);
		if (!folderFile.exists()) {
			folderFile.mkdirs();
        }
		
        /* Tạo file tại Server. */
		File storeFileAtServer = new File(uploadRootDir.getAbsolutePath() + File.separator + media.getFilePath());
		
		/* Ghi xuống file tại Server */
		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(storeFileAtServer));
		stream.write(file.getBytes());
		stream.close();
		
		media.setCreatedTime(new Date());
		media.setUpdatedTime(new Date());
		media.setFileSize(file.getSize());
		media.setCreator(creator);
		
		media=mediaRepository.save(media);
		return media;
	}

	@Override
	public Media createMedia(ReqMediaUploadPartner reqMediaUploadPartner) throws FileNotFoundException, IOException {
		Media media=new Media();
		
		File uploadRootDir = new File(fileLocalService.getPathAttachments());
		/* Tạo thư mục gốc upload nếu nó không tồn tại. */
        if (!uploadRootDir.exists()) {
            uploadRootDir.mkdirs();
        }
        
        MultipartFile file = reqMediaUploadPartner.getFile();
        if(file==null || file.isEmpty()) {
        	throw new BadRequestExceptionAdvice("file đính kèm rỗng");
        }
		String description=reqMediaUploadPartner.getDescription();
		
		Organization organization=organizationService.getOrganizationByUnitCode(reqMediaUploadPartner.getOrganizationCode());
		User user=userService.getUserByUserName(reqMediaUploadPartner.getUsername());
		
		Creator creator=new Creator();
		creator.setOrganizationId(organization.getId());
		creator.setOrganizationName(organization.getName());
		creator.setOrganizationUserId(user.getId());
		creator.setOrganizationUserName(user.getFullName());
		
        /* Valid XSS */
        xssValidationService.sanitize(file.getName());
        xssValidationService.sanitize(file.getOriginalFilename());
        
        /* Phân theo folder năm/tháng/ngày */
        String date=DateTimeUtil.getDateFolder().format(new Date());
        
		/* Tên file gốc tại Client. */
        media.setFileName(file.getOriginalFilename());
        media.setFileType(file.getContentType());
        media.setFilePath(date+ "/"+media.getId()+"_"+CommonUtil.toFilename(file.getOriginalFilename()));
        media.setFileDescription(description);
        
        File folderFile = new File(uploadRootDir.getAbsolutePath() + File.separator + date);
		if (!folderFile.exists()) {
			folderFile.mkdirs();
        }
		
        /* Tạo file tại Server. */
		File storeFileAtServer = new File(uploadRootDir.getAbsolutePath() + File.separator + media.getFilePath());
		
		/* Ghi xuống file tại Server */
		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(storeFileAtServer));
		stream.write(file.getBytes());
		stream.close();
		
		media.setCreatedTime(new Date());
		media.setUpdatedTime(new Date());
		media.setFileSize(file.getSize());
		media.setCreator(creator);
		
		media=mediaRepository.save(media);
		return media;
	}

	@Override
	public Media deleteById(String mediaId) {
		Media media = findById(mediaId);
		
		Thread thread=new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					File file = new File(fileLocalService.getPathAttachments() + File.separator + media.getFilePath());
					if(file.exists()) {
						try {
				            Files.delete(Paths.get(file.getAbsolutePath()));
				        } catch (IOException e) {
				            e.printStackTrace();
				        }
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.run();
		
		mediaRepository.delete(media);
		return media;
	}

	@Override
	public List<Media> findAll(MediaFilter mediaFilter) {
		return mediaRepositoryCustom.findAll(mediaFilter);
	}

	@Override
	public Optional<Media> findOne(MediaFilter mediaFilter) {
		return mediaRepositoryCustom.findOne(mediaFilter);
	}

	@Override
	public long countAll(MediaFilter mediaFilter) {
		return mediaRepositoryCustom.countAll(mediaFilter);
	}

}
