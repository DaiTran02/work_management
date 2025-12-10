package ws.core.resource.admin;

import java.io.IOException;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ws.core.model.User;
import ws.core.model.response.ResponseAPI;
import ws.core.services.ImportVNPTService;
import ws.core.services.UserService;

@RestController
@RequestMapping("/api/admin")
public class ImportVNPTControllerAdmin {
	@Autowired
	private ImportVNPTService importService;
	
	@Autowired
	private UserService userService;
	
	@PostMapping("/imports/vnpt/organizations-and-users")
	public Object importOrganizationsAndUser(
			@RequestParam(name = "fullPathFolderStore", required = false) String fullPathFolderStore,
			@RequestParam(name = "provider", required = false, defaultValue = "ldap") String provider,
			@RequestParam(name = "passwordDefault", required = false, defaultValue = "123") String passwordDefault) {
		String folderStoreServer="D:\\PROJECT MANAGER\\HÀ NỘI (MỞ RỘNG)\\Cây Đơn vị Thành Ủy\\Mẫu Import Tổ chức và Người dùng (VNPT)";
		if(fullPathFolderStore!=null) {
			folderStoreServer=fullPathFolderStore;
		}
		
		List<Document> result= importService.importOrganizationsAndUser(folderStoreServer, provider, passwordDefault);
		ResponseAPI responseAPI=new ResponseAPI();
		responseAPI.setStatus(HttpStatus.CREATED);
		responseAPI.setMessage("Kết quả các file scan được trong folder ["+folderStoreServer+"]");
		responseAPI.setResult(result);
		return responseAPI.build();
	}
	
	@PostMapping("/imports/vnpt/organizations")
	public Object importOrganizations(@RequestParam(name = "fileStore", required = false) String fileStore) {
		String pathFile="D:\\PROJECT MANAGER\\HÀ NỘI (MỞ RỘNG)\\Cây Đơn vị Thành Ủy\\Tạm chỉnh\\Danh sách Đơn vị.xlsx";
		if(fileStore!=null) {
			pathFile=fileStore;
		}
		
		Document result=new Document();
		try {
			result = importService.importOrganizations(pathFile);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ResponseAPI responseAPI=new ResponseAPI();
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Kết quả file ["+pathFile+"]");
		responseAPI.setResult(result);
		return responseAPI.build();
	}
	
	@PostMapping("/imports/vnpt/users")
	public Object importUsers(
			@RequestParam(name = "fileStore", required = false) String fileStore,
			@RequestParam(name = "provider", required = false, defaultValue = "ldap") String provider,
			@RequestParam(name = "passwordDefault", required = false, defaultValue = "123") String passwordDefault) {
		String pathFile="D:\\PROJECT MANAGER\\HÀ NỘI (MỞ RỘNG)\\Cây Đơn vị Thành Ủy\\Tạm chỉnh\\Danh sách Đơn vị.xlsx";
		if(fileStore!=null) {
			pathFile=fileStore;
		}
		
		User administratorUser=userService.getUserByUserName("administrator");
		Document result=new Document();
		try {
			result = importService.importUsers(pathFile, administratorUser, provider, passwordDefault);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ResponseAPI responseAPI=new ResponseAPI();
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Kết quả file ["+pathFile+"]");
		responseAPI.setResult(result);
		return responseAPI.build();
	}
}
