package org.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 7 * 24 * 60 * 60, redisNamespace = "auth:goodscattle")
public class SessionConfig {

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        // I don't know why this factory can't load my configuration of redis in application.yml.
        // return new LettuceConnectionFactory();
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort()));
    }

    @Bean
    public RedisOperations<String, Object> sessionRedisOperations() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(this.redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }
    @Bean
    public FindByIndexNameSessionRepository redisSessionRepository(RedisOperations<String, Object> sessionRedisOperations) {
        return new RedisIndexedSessionRepository(sessionRedisOperations);
    }
}
