package ws.core.resource.admin;

import java.util.ArrayList;
import java.util.List;


import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import ws.core.enums.ConfigurationKey;
import ws.core.model.Configuration;
import ws.core.model.filter.ConfigurationFilter;
import ws.core.model.filter.OrderByFilter;
import ws.core.model.filter.OrderByFilter.Direction;
import ws.core.model.request.ReqConfigurationUpdate;
import ws.core.model.response.ResponseAPI;
import ws.core.model.response.util.ConfigurationUtil;
import ws.core.services.ConfigurationService;

@RestController
@RequestMapping("/admin")
public class ConfigurationControllerAdmin {
	@Autowired
	protected ConfigurationService configurationService;

	@Autowired
	private ConfigurationUtil configurationUtil;
	
	@GetMapping("/configurations")
	public Object getList() {
		ResponseAPI responseAPI=new ResponseAPI();
		
		ConfigurationFilter configurationFilter=new ConfigurationFilter();
		OrderByFilter orderByFilter=new OrderByFilter();
		orderByFilter.add("orderNumber", Direction.ASC);
		configurationFilter.setOrderByFilter(orderByFilter);
		
		long total=configurationService.countAll(configurationFilter);
		List<Configuration> configurations=configurationService.findAll(configurationFilter);
		List<Document> results=new ArrayList<Document>();
		for (Configuration item : configurations) {
			results.add(configurationUtil.toAdminResponse(item));
		}
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		responseAPI.setResult(results);
		return responseAPI.build();
	}
	
	@GetMapping("/configurations/{key}")
	public Object get(@PathVariable(name = "key", required = true) String key) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		Configuration configuration=configurationService.getByKey(key);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(configurationUtil.toAdminResponse(configuration));
		return responseAPI.build();
	}
	
	@PutMapping("/configurations/{name}")
	public Object update(
			@PathVariable(name = "name", required = true) String name,
			@RequestBody @Valid ReqConfigurationUpdate reqConfigurationUpdate) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		Configuration configuration=configurationService.update(name, reqConfigurationUpdate);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(configurationUtil.toAdminResponse(configuration));
		return responseAPI.build();
	}
	
	@GetMapping("/configurations/get-keys")
	public Object getKeys() {
		ResponseAPI responseAPI=new ResponseAPI();
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(ConfigurationKey.values());
		return responseAPI.build();
	}
}
