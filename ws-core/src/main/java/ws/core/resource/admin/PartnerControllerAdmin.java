package ws.core.resource.admin;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ws.core.model.response.ResponseAPI;
import ws.core.security.CustomUserDetails;
import ws.core.services.PartnerApiService;

@RestController
@RequestMapping("/api/admin")
public class PartnerControllerAdmin {
	
	@Autowired
	private PartnerApiService partnerApiService;
	
	@PostMapping("/mapping/org")
	public Object doMappingOrg() {
		ResponseAPI responseAPI = new ResponseAPI();
		CustomUserDetails creator = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		responseAPI.setOk();
		responseAPI.setResult(partnerApiService.doMappingOrg(creator.getUser()));
		return responseAPI.build();
	}
	
	@PutMapping("/mapping/org/{idOrg}")
	public Object doMappingOrgByIdOrg(@PathVariable(name = "idOrg")String idOrg) {
		ResponseAPI responseAPI = new ResponseAPI();
		CustomUserDetails creator = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		responseAPI.setOk();
		responseAPI.setResult(partnerApiService.doMappdingOrgByIdOrg(idOrg, creator.getUser()));
		return responseAPI.build();
	}
	
	@PostMapping("/mapping/org/v2")
    public CompletableFuture<ResponseEntity<Object>> doMap() {
        ResponseAPI responseAPI = new ResponseAPI();
        responseAPI.setOk(); // Initialize as success

        CustomUserDetails creator = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        return partnerApiService.doMappdingCustom(creator.getUser())
            .thenApply(result -> {
                responseAPI.setResult(result);
                return ResponseEntity.ok(responseAPI.build());
            })
            .exceptionally(throwable -> {
                responseAPI.setError(throwable.getMessage());
                return ResponseEntity.status(500).body(responseAPI.build());
            });
    }
	

}
