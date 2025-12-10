package ws.core.resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ws.core.model.Organization;
import ws.core.model.response.ResponseAPI;
import ws.core.services.DevService;
import ws.core.services.RSASecurityService;

@RestController
public class DevController {
	
	@Autowired
	private DevService devService;
	
	@Autowired
	private RSASecurityService rsaSecurityService;
	
	@GetMapping("/check-api")
	public Object checkAPI() {
		return "Done";
	}
	
	@GetMapping("/api/check-api")
	public Object checkAPI2() {
		return "Done";
	}
	
	@PostMapping("/dev/import-organizations")
	public Object importOrganizations(@RequestParam(name = "fileStore", required = false) String fileStore) {
		String pathFile="D:\\PROJECT MANAGER\\HÀ NỘI (MỞ RỘNG)\\Cây Đơn vị Thành Ủy\\Tạm chỉnh\\Danh sách Đơn vị.xlsx";
		if(fileStore!=null) {
			pathFile=fileStore;
		}
		List<Organization> organizations=new ArrayList<>();
		try {
			organizations = devService.importOrganization(pathFile);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return organizations;
	}
	
	@GetMapping("/api/general-x-api-key")
	public Object generalXApiKey() {
		ResponseAPI responseAPI=new ResponseAPI();
		responseAPI.setResult(rsaSecurityService.createDataTest());
		responseAPI.setStatus(HttpStatus.OK);
		return responseAPI.build();
	}
	
	@GetMapping("/dev/api/enable-sync")
	public Object enableSync() {
		getRandomString();
//		String result=getRandomStringNormal();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		ResponseAPI responseAPI=new ResponseAPI();
		responseAPI.setResult(rsaSecurityService.createDataTest());
		responseAPI.setStatus(HttpStatus.OK);
//		try {
//			responseAPI.setResult(result.get());
//		} catch (InterruptedException | ExecutionException e) {
//			e.printStackTrace();
//		}
//		responseAPI.setResult(list.size());
		return responseAPI.build();
	}
	
	private List<String> list=new ArrayList<>();
	
	public String getRandomStringNormal(){
		try {
			long time=1000;
			System.out.println("Call getRandomString, loading "+time/1000+"s");
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String str=ObjectId.get().toHexString();
		list.add(str);
		return str;
	}
	
	@Async
	private CompletableFuture<String> getRandomString(){
		try {
			long time=10000;
			System.out.println("Call getRandomString, loading "+time/1000+"s");
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(ObjectId.get().toHexString());
	}
}
