package org.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;

@Configuration
@EnableWebSecurity
public class SecurityConfig<S extends Session> {

    @Autowired
    private FindByIndexNameSessionRepository<S> redisSessionRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .and()
                .sessionManagement(
                        (sessionManagement) -> sessionManagement
                                .maximumSessions(2)
                                .sessionRegistry(sessionRegistry()))
                .rememberMe((rememberMe) -> rememberMe
                        .rememberMeServices(rememberMeServices()))
                .csrf().disable();
        return http.build();
    }

    @Bean
    public SpringSessionBackedSessionRegistry<S> sessionRegistry() {
        return new SpringSessionBackedSessionRegistry<>(this.redisSessionRepository);
    }

    @Bean
    public SpringSessionRememberMeServices rememberMeServices() {
        SpringSessionRememberMeServices rememberMeServices = new SpringSessionRememberMeServices();
        rememberMeServices.setAlwaysRemember(true);
        return rememberMeServices;
    }


}
