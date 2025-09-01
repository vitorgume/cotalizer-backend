package com.gumeinteligenciacomercial.orcaja.config;

import io.lettuce.core.RedisURI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@Profile("!test")
public class RedisConfig {
    @Bean
    public LettuceConnectionFactory redisConnectionFactory(
            @Value("${spring.redis.url:}") String springUrl,
            @Value("${REDIS_URL:}") String envUrl
    ) {
        String url = !springUrl.isBlank() ? springUrl : envUrl;
        if (url == null || url.isBlank()) {
            throw new IllegalStateException("Configure REDIS_URL ou spring.redis.url");
        }

        RedisURI uri = RedisURI.create(url);

        var conf = new RedisStandaloneConfiguration(uri.getHost(), uri.getPort());
        if (uri.getUsername() != null && !uri.getUsername().isBlank()) {
            conf.setUsername(uri.getUsername());
        }
        if (uri.getPassword() != null && uri.getPassword().length > 0) {
            conf.setPassword(RedisPassword.of(new String(uri.getPassword())));
        }
        if (uri.getDatabase() > 0) {
            conf.setDatabase(uri.getDatabase());
        }

        var builder = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(5));
        if ("rediss".equalsIgnoreCase(uri.getSocket())) {
            builder.useSsl();
        }

        return new LettuceConnectionFactory(conf, builder.build());
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(LettuceConnectionFactory cf) {
        var tpl = new RedisTemplate<String, String>();
        tpl.setConnectionFactory(cf);
        tpl.setKeySerializer(new StringRedisSerializer());
        tpl.setValueSerializer(new StringRedisSerializer());
        return tpl;
    }
}
