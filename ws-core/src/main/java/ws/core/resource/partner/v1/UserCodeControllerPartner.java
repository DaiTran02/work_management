package ws.core.resource.partner.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import ws.core.model.User;
import ws.core.model.data.UserCodePublic;
import ws.core.model.request.ReqLoginByCodeCreate;
import ws.core.model.request.ReqUserCodeGenerate;
import ws.core.model.response.ResponseAPI;
import ws.core.model.response.util.UserCodePublicUtil;
import ws.core.services.LogLoginByCodeService;
import ws.core.services.UserService;
import ws.core.services.redis.UserCodePublicServiceRD;

@RestController
@RequestMapping("/api/partner/v1")
public class UserCodeControllerPartner {
	
	@Autowired
	private UserCodePublicServiceRD userCodePublicServiceRD;
	
	@Autowired
	private UserCodePublicUtil userCodePublicUtil;
	
	@Autowired
	private LogLoginByCodeService loginByCodeService;
	
	@Autowired
	private UserService userService;
	
	@PostMapping("/users-code/generate")
	public Object generate(@RequestBody @Valid ReqUserCodeGenerate reqUserCodeGenerate){
		ResponseAPI responseAPI=new ResponseAPI();
		
		User user=userService.getUserByUserName(reqUserCodeGenerate.getUsername());
		ReqLoginByCodeCreate loginByCodeCreate = new ReqLoginByCodeCreate();
		loginByCodeCreate.setFullName(user.getFullName());
		loginByCodeCreate.setUsername(user.getUsername());
		loginByCodeService.saveLog(loginByCodeCreate);
		
		UserCodePublic userCodePublic = userCodePublicServiceRD.push(user.getUsername());
		responseAPI.setStatus(HttpStatus.CREATED);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(userCodePublicUtil.toSiteResponse(userCodePublic));
		return responseAPI.build();
	}
}
