package ws.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.Filter;
import ws.core.security.CustomUserDetailsService;
import ws.core.security.JwtAuthenticationEntryPoint;
import ws.core.security.JwtAuthenticationFilter;
import ws.core.security.apikey.ApiKeyAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig implements WebMvcConfigurer {
	
	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;
    
	@Autowired
	private JwtAuthenticationEntryPoint authenticationEntryPoint;
	
	@Autowired
	private ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;
	
	@Bean
    FilterRegistrationBean<Filter> jwtFilterRegistrationBean() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<Filter>();
        registrationBean.setFilter(jwtAuthenticationFilter);
        registrationBean.setEnabled(true);
        registrationBean.addUrlPatterns("/api/**");
        return registrationBean;
    }

    @Bean
    FilterRegistrationBean<Filter> apiKeyFilterRegistrationBean() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<Filter>();
        registrationBean.setFilter(apiKeyAuthenticationFilter);
        registrationBean.setEnabled(true);
        registrationBean.addUrlPatterns("/api/partner/**");
        return registrationBean;
    }
	
    @Bean
    UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider=new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
    
    private static final String[] AUTH_WHITELIST = {
    		"/api/admin/users/login",
    		"/api/admin/users/login-by-code",
    		"/api/site/users/login", 
    		"/api/site/users/login-by-code", 
    		"/api/admin/users/refresh-token",
    		"/api/site/users/refresh-token",
    		"/api-public/**",
    		"/check-api",
    		"/statics/**",
    		"/html/**",
    		"/dev/**",
    		"/actuator/**"
    };
    
    @Bean
    @Order(1)
    SecurityFilterChain publicApiFilterChain(HttpSecurity http) throws Exception {
    	return http
    	.csrf(AbstractHttpConfigurer::disable)
    	.securityMatcher(AUTH_WHITELIST)
    	.authorizeHttpRequests( auth -> auth
    			.requestMatchers(AUTH_WHITELIST).permitAll()
    	)
        .build();
    }
    
    @Bean
    @Order(2)
    SecurityFilterChain apiKeyFilterChain(HttpSecurity http) throws Exception {
    	return http
    	.csrf(AbstractHttpConfigurer::disable)
    	.securityMatcher("/api/partner/**")
    	.authorizeHttpRequests( auth -> auth
    			.anyRequest().authenticated()
    	)
    	.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    	.exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint))
        .addFilterBefore(apiKeyAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
    }
    
    @Bean
    @Order(3)
    SecurityFilterChain jwtSecurityFilterChain(HttpSecurity http) throws Exception {
    	return http
    	.csrf(AbstractHttpConfigurer::disable)
    	.securityMatcher("/api/**")
    	.authorizeHttpRequests(auth -> auth
    			.requestMatchers("/api/admin/**").hasAnyAuthority("administrator","quantridonvi")
    			.anyRequest().authenticated()
    	)
    	.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    	.exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint))
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
    }
    
    @Bean
    @Order(4)
    SecurityFilterChain defaultSecurityFilterChain (HttpSecurity http) throws Exception {
    	return http
    	.csrf(AbstractHttpConfigurer::disable)
    	.authorizeHttpRequests(auth -> auth
    			.anyRequest().authenticated()
    	)
    	.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    	.exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint))
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
    	registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "POST","PUT", "DELETE");
    }
    
}
