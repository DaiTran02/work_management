package com.ngn.secutity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.ngn.views.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;

@Configuration
public class SecurityConfiguration extends VaadinWebSecurity{
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @SuppressWarnings("removal")
	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests().requestMatchers(new AntPathRequestMatcher("/images/*.png")).permitAll();
        http.authorizeHttpRequests().requestMatchers(new AntPathRequestMatcher("/images/*.jpg")).permitAll();

        // Icons from the line-awesome addon
        http.authorizeHttpRequests().requestMatchers(new AntPathRequestMatcher("/line-awesome/**/*.svg")).permitAll()
        
        
        // 🔥 Những đường dẫn bắt buộc phải permit cho Vaadin hoạt động đúng
        .requestMatchers(new AntPathRequestMatcher("/VAADIN/**")).permitAll()
        .requestMatchers(new AntPathRequestMatcher("/HEARTBEAT/**")).permitAll()
        .requestMatchers(new AntPathRequestMatcher("/UIDL/**")).permitAll()
        .requestMatchers(new AntPathRequestMatcher("/push/**")).permitAll()
        .requestMatchers(new AntPathRequestMatcher("/manifest.webmanifest")).permitAll()
        .requestMatchers(new AntPathRequestMatcher("/sw.js")).permitAll()
        .requestMatchers(new AntPathRequestMatcher("/offline.html")).permitAll();
        
        super.configure(http);
        
        setLoginView(http, LoginView.class);
    }
}
