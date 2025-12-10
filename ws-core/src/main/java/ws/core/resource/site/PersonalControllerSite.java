package ws.core.resource.site;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ws.core.model.PersonalRecord;
import ws.core.model.filter.PersonalRecordFilter;
import ws.core.model.request.ReqPersonalRecordCreate;
import ws.core.model.response.ResponseAPI;
import ws.core.model.response.util.PersonalRecordUtil;
import ws.core.services.PersonalRecordService;

@RestController
@RequestMapping("/api/site")
public class PersonalControllerSite {
	@Autowired
	private PersonalRecordService personalRecordService;

	@Autowired
	private PersonalRecordUtil personalRecordUtil;
	
	@PostMapping("/personal")
	public Object createPersonal(@RequestBody ReqPersonalRecordCreate recordCreate) {
		ResponseAPI responseAPI = new ResponseAPI();
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		System.out.println("Check");
		responseAPI.setResult(personalRecordUtil.toCommon(personalRecordService.create(recordCreate)));
		return responseAPI.build();
	}

	@GetMapping("/personal")
	public Object getAllPersonal(@RequestParam(name = "userId") String userId,
			@RequestParam(name = "keySearch") String keySearch) {
		ResponseAPI responseAPI = new ResponseAPI();
		
		PersonalRecordFilter personalRecordFilter = new PersonalRecordFilter();
		personalRecordFilter.setUserId(userId);
		personalRecordFilter.setKeySearch(keySearch.isEmpty() ? null : keySearch);
		
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		List<PersonalRecord> result = personalRecordService.findAll(personalRecordFilter);
		responseAPI.setTotal(result.size());
		responseAPI.setResult(result.stream().map(personalRecordUtil::toCommon).toList());;
		return responseAPI.build();
	}
	
	@GetMapping("/personal/transfer")
	public Object getAllPersonalByOldUser(@RequestParam(name = "oldUserId") String oldUserId,
			@RequestParam(name = "keySearch") String keySearch) {
		ResponseAPI responseAPI = new ResponseAPI();
		
		PersonalRecordFilter personalRecordFilter = new PersonalRecordFilter();
		personalRecordFilter.setOldUserId(oldUserId);
		personalRecordFilter.setKeySearch(keySearch);
		
		responseAPI.setOk();
		List<PersonalRecord> result = personalRecordService.findByOldUser(personalRecordFilter);
		responseAPI.setTotal(result.size());
		responseAPI.setResult(result.stream().map(personalRecordUtil::toCommon).toList());
		
		return responseAPI.build();
	}
	
	
	@GetMapping("/personal/transferred")
	public Object getAllPersonalByTransferredUser(@RequestParam(name = "transferredUserId") String transferredUserId, @RequestParam(name = "keySearch") String keySearch) {
		ResponseAPI responseAPI = new ResponseAPI();
		
		PersonalRecordFilter personalRecordFilter = new PersonalRecordFilter();
		personalRecordFilter.setTransferredUserId(transferredUserId);
		personalRecordFilter.setKeySearch(keySearch);
		
		responseAPI.setOk();
		List<PersonalRecord> result = personalRecordService.findByTransferredUser(personalRecordFilter);
		responseAPI.setTotal(result.size());
		responseAPI.setResult(result.stream().map(personalRecordUtil::toCommon).toList());
		return responseAPI.build();
	}
	
	
	@GetMapping("/personal/{id}")
	public Object getOnePersonal(@PathVariable String id) {
		ResponseAPI responseAPI = new ResponseAPI();
		responseAPI.setOk();
		responseAPI.setResult(personalRecordUtil.toCommon(personalRecordService.getOne(id)));
		return responseAPI.build();
	}
	
	@GetMapping("/personal/detail/{id}")
	public Object getDetailPersonal(@PathVariable String id) {
		ResponseAPI responseAPI = new ResponseAPI();
		responseAPI.setOk();
		responseAPI.setResult(personalRecordUtil.toDetail(personalRecordService.getDetail(id)));
		return responseAPI.build();
	}

	@PutMapping("/personal/{id}")
	public Object updatePersonal(@PathVariable String id,@RequestBody ReqPersonalRecordCreate personalUpdate) {
		ResponseAPI responseAPI = new ResponseAPI();
		responseAPI.setOk();
		responseAPI.setResult(personalRecordUtil.toCommon(personalRecordService.update(id, personalUpdate)));
		return responseAPI.build();
	}

	@PutMapping("/personal/transfer/{id}/userId/{userId}")
	public Object transferUser(@PathVariable String id,@PathVariable String userId) {
		ResponseAPI responseAPI = new ResponseAPI();
		responseAPI.setOk();
		responseAPI.setResult(personalRecordUtil.toCommon(personalRecordService.transferUser(id, userId)));
		return responseAPI.build();
	}
	
	@PutMapping("personal/add/{id}")
	public Object addMoreData(@PathVariable String id,@RequestBody ReqPersonalRecordCreate update) {
		ResponseAPI responseAPI = new ResponseAPI();
		responseAPI.setOk();
		responseAPI.setResult(personalRecordUtil.toCommon(personalRecordService.doPutDocOrTask(id, update)));
		return responseAPI.build();
	}
	
	@DeleteMapping("/personal/{id}")
	public Object deletePersonal(@PathVariable String id) {
		ResponseAPI responseAPI = new ResponseAPI();
		personalRecordService.delete(id);
		responseAPI.setOk();
		responseAPI.setResult("Đã xóa thành công");
		return responseAPI.build();
	}
}












