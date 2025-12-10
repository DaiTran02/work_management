package com.ngn;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@Theme(value = "site")
@Push
@EnableAsync
@PWA(name = "Hệ thống theo dõi nhiệm vụ", shortName = "SiteHN",
offlinePath = "./themes/offline/offline.html")
public class Application implements AppShellConfigurator {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
	
	@Override
	public void configurePage(AppShellSettings settings) {
		settings.addFavIcon("icon", "./themes/favicon/favicon.png", "192x192");
	}
	
//	@Bean
//	public Executor customTaskExecutor() {
//		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//	    executor.setCorePoolSize(2);
//	    executor.setMaxPoolSize(2);
//	    executor.setQueueCapacity(500);
//	    executor.setThreadNamePrefix("RequestAPI");
//	    executor.initialize();
//	    return executor;
//	}
}
