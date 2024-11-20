package br.com.levva.order.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
public class RedisConfig {

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        log.atTrace().log(() -> "Creating springSessionDefaultRedisSerializer");
        return new GenericJackson2JsonRedisSerializer();
    }

    @Bean
    public RedisSerializer<String> stringRedisSerializer() {
        log.atTrace().log(() -> "Creating stringRedisSerializer");
        return new StringRedisSerializer();
    }
}
