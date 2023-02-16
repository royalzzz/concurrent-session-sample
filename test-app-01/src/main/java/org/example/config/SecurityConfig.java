package org.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;

@Configuration
@EnableRedisHttpSession
public class SecurityConfig<S extends Session> {

    @Autowired
    private FindByIndexNameSessionRepository<S> sessionRepository;

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
        return new SpringSessionBackedSessionRegistry<>(this.sessionRepository);
    }

    @Bean
    public SpringSessionRememberMeServices rememberMeServices() {
        SpringSessionRememberMeServices rememberMeServices = new SpringSessionRememberMeServices();
        rememberMeServices.setAlwaysRemember(true);
        return rememberMeServices;
    }

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        // I don't know why this factory can't load my configuration of redis in application.yml.
        // return new LettuceConnectionFactory();
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort()));
    }
}
