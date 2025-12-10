package ws.core.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ws.core.services.FileLocalService;
import ws.core.services.PropsService;

@Service
public class FileLocalServiceImpl implements FileLocalService{

	@Autowired
	private PropsService propsService;
	
	private String folderAttachments="attachments";
	private String folderExports="exports";
	private String folderExportsPublic="exports-public";
	private String folderTemplates="templates";

	@Override
	public String getPathAttachments() {
		return propsService.getStoragesFolderPath()+File.separator+folderAttachments;
	}

	@Override
	public String getPathExports() {
		return propsService.getStoragesFolderPath()+File.separator+folderExports;
	}

	@Override
	public String getPathExportsPublic() {
		return propsService.getStoragesFolderPath()+File.separator+folderExportsPublic;
	}

	@Override
	public String getPathTemplates() {
		return propsService.getStoragesFolderPath()+File.separator+folderTemplates;
	}

	@Override
	public byte[] getFile(String filePath) {
		try {
    		File storeFileAtServer = new File(getPathAttachments() + File.separator + filePath);
    		if(storeFileAtServer.exists()==false) {
    			return null;
    		}
    		
    		byte[] inFileBytes = Files.readAllBytes(Paths.get(storeFileAtServer.getPath())); 
    		//byte[] encoded = Base64.getMimeEncoder().encode(inFileBytes);
    		return inFileBytes;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void deleteFile(String filePath) {
		Thread thread=new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					File file = new File(getPathAttachments() + File.separator + filePath);
					if(file.exists()) {
						try {
				            Files.delete(Paths.get(file.getAbsolutePath()));
				            System.out.println("Đã xóa doc attachment ["+Paths.get(file.getAbsolutePath())+"]");
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
	}
}
