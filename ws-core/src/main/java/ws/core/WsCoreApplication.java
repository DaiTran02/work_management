package ws.core;

import java.util.Date;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.PostConstruct;
import ws.core.config.InitProjectService;
import ws.core.services.UpgradeDataService;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@PropertySource(value = {"classpath:application.properties"},encoding = "UTF-8")
@EnableScheduling
@EnableAsync
public class WsCoreApplication implements WebMvcConfigurer, CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(WsCoreApplication.class, args);
	}

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
	}
	
	@Autowired
	private InitProjectService initProjectService;
	
	@Autowired
	public UpgradeDataService upgradeDataService;
	
	@Override
	public void run(String... args) throws Exception {
		for (String arg : args) {
			System.out.println("=> "+arg);
		}
		System.out.println("Current JVM version - " + System.getProperty("java.version"));
		System.out.println("Timezone: "+TimeZone.getDefault().getDisplayName());
		System.out.println("System time: "+new Date());
		
		/* Init project */
		initProjectService.installDataIfNotExists();
		
		/* Upgrade data */
		upgradeDataService.realtime();
	}
} 
