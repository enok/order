package br.com.levva.order.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isDuplicate(String orderId, long expirationSeconds) {
        Boolean isNew = redisTemplate.opsForValue().setIfAbsent(orderId, "processed", expirationSeconds, TimeUnit.SECONDS);
        log.atDebug().addArgument(orderId).addArgument(Boolean.FALSE.equals(isNew)).log(() -> "Is order: {} duplicated? {}");
        return isNew == null || !isNew; // Se retorna false, é duplicado
    }
}
