package ws.core.config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ws.core.model.User;
import ws.core.respository.UserRepository;
import ws.core.services.CasbinAuthService;
import ws.core.services.redis.LdapDataUserServiceRD;

@Service
public class InitProjectService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
    private MongoTemplate mongoTemplate;
	
	@Value("classpath:init/*")
    private Resource[] resources;
	
	@Autowired
	private CasbinAuthService casbinAuthService;
	
	@Autowired
	private LdapDataUserServiceRD ldapDataUserServiceRD;
	
	public void installDataIfNotExists() {
		try {
			System.out.println("-> Check và khởi tạo dữ liệu mặc định .....");
			
			/* Add admin account */
			User administrator=  getAdministrator();
			
			/* Init data */
			initData(administrator);
			
			/* Init casbin policy */
			initCasbinPolicy();
			
			/* Init users ldap to Redis */
			ldapDataUserServiceRD.initStoreDataUsers();
			
			System.out.println("-> Check và khởi tạo dữ liệu mặc định ..... Done!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private User getAdministrator() {
		Optional<User> checkAdmin = userRepository.findByUsername("administrator");
		User administrator = null;
		if(checkAdmin.isPresent()==false) {
			administrator=new User();
			administrator.setId(new ObjectId("6051883e2e85d71a67ca8319"));
			administrator.setActive(true);
			administrator.setUsername("administrator");
			administrator.setEmail("administrator@dev.com");
			administrator.setFullName("Administrator");
			administrator.setJobTitle("Quản trị cấp cao");
			administrator.setPassword(passwordEncoder.encode("P@ssw0rd"));
			userRepository.save(administrator);
		}else {
			administrator=checkAdmin.get();
		}
		return administrator;
	}
	
	private void initData(User administrator) {
		System.out.println();
		System.out.println("Init data");
		for (final Resource res : resources) {
            try {
            	if(res.getFilename().contains(".json")) {
            		System.out.println("-> File name: "+res.getFilename());
            		
            		String [] tmp=res.getFilename().split(".json");
            		String collectionName=tmp[0];
            		System.out.println("+ Collection name: "+collectionName);
            		
            		if(mongoTemplate.getDb().getCollection(collectionName).countDocuments()==0) {
	    				InputStream in = res.getInputStream();
	    				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	    				List<String> jsonLines=reader.lines().collect(Collectors.toList());
	    				for (String json : jsonLines) {
	    					Document document=Document.parse(json);
	    					mongoTemplate.getDb().getCollection(collectionName).insertOne(document);
	    					System.out.println("+ Inserted "+json);
	    				}
	    				reader.close();
            		}else {
            			System.out.println("+ The data is available");
            		}
            	}
			} catch (Exception e) {
				e.printStackTrace();
			}
            System.out.println("...");
        }
		System.out.println();
	}
	 
	private void initCasbinPolicy() {
		System.out.println("Init casbin policy");
		
		casbinAuthService.clearAll();
		
		/* Admin */
		casbinAuthService.addPolicy("quantridonvi", "/api/admin/.*", "*");
		
		/* Site */
		casbinAuthService.addPolicy("chidaovagiaonhiemvu", "/api/site/.*", "*");
		casbinAuthService.addPolicy("quanlynhiemvutrongto", "/api/admin/.*", "*");
		casbinAuthService.addPolicy("giaonhiemvuthaycholanhdaocuadonvi", "/api/admin/.*", "*");
		casbinAuthService.addPolicy("phannhiemvuduocgiaochocanbo", "/api/admin/.*", "*");
		casbinAuthService.addPolicy("xemnhiemvucuadonvi", "/api/admin/.*", "*");
		casbinAuthService.addPolicy("xemvanbancuadonvi", "/api/admin/.*", "*");
		casbinAuthService.addPolicy("themvanban", "/api/admin/.*", "*");
		casbinAuthService.addPolicy("ketthucnhiemvuduocgiaocuadonvi", "/api/admin/.*", "*");
		
		casbinAuthService.getAllPolicies().stream().forEach(e->System.out.println("p: "+e));
		casbinAuthService.getAllRoles().stream().forEach(e->System.out.println("g: "+e));
	}
}
