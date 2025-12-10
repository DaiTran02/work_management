package vn.com.ngn;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@Theme(value = "my-app")
public class Application implements AppShellConfigurator {
    private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

	@Override
	public void configurePage(AppShellSettings settings) {
		settings.addFavIcon("icon", "./themes/favicon/favicon.png", "192x192");
	}
}
