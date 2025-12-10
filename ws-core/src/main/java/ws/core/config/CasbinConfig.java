package ws.core.config;

import org.casbin.jcasbin.main.Enforcer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ws.core.services.PropsService;

@Configuration
public class CasbinConfig {
	
	//@Autowired 
	//private MongoClient mongoClient;
	
	@Autowired
	private PropsService propsService;
	
	@Bean  
    Enforcer enforcer() {
		//Adapter adapter = new MongoAdapter(mongoClient, "tdnv_hanoi_dev","policy");
		//Enforcer enforcer = new Enforcer("D:\\casbin\\model.conf", adapter);
		if(propsService.isCasbinEnable()) {

			Enforcer enforcer = new Enforcer(propsService.getCasbinPathModel(), propsService.getCasbinPathPolicy());
	        enforcer.enableAutoSave(true);
	        return enforcer;
		}
		return new Enforcer();
    }
}
