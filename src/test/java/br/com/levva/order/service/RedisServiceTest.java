package br.com.levva.order.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class RedisServiceTest {

    private RedisService redisService;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        redisService = new RedisService(redisTemplate);
    }

    @Test
    void isDuplicate_shouldReturnFalse_whenOrderIsNew() {
        // Given
        String orderId = "12345";
        long expirationSeconds = 60L;
        when(valueOperations.setIfAbsent(orderId, "processed", expirationSeconds, TimeUnit.SECONDS)).thenReturn(true);

        // When
        boolean result = redisService.isDuplicate(orderId, expirationSeconds);

        // Then
        assertFalse(result);
        verify(valueOperations, times(1)).setIfAbsent(orderId, "processed", expirationSeconds, TimeUnit.SECONDS);
    }

    @Test
    void isDuplicate_shouldReturnTrue_whenOrderIsDuplicate() {
        // Given
        String orderId = "12345";
        long expirationSeconds = 60L;
        when(valueOperations.setIfAbsent(orderId, "processed", expirationSeconds, TimeUnit.SECONDS)).thenReturn(false);

        // When
        boolean result = redisService.isDuplicate(orderId, expirationSeconds);

        // Then
        assertTrue(result);
        verify(valueOperations, times(1)).setIfAbsent(orderId, "processed", expirationSeconds, TimeUnit.SECONDS);
    }

    @Test
    void isDuplicate_shouldReturnTrue_whenRedisReturnsNull() {
        // Given
        String orderId = "12345";
        long expirationSeconds = 60L;
        when(valueOperations.setIfAbsent(orderId, "processed", expirationSeconds, TimeUnit.SECONDS)).thenReturn(null);

        // When
        boolean result = redisService.isDuplicate(orderId, expirationSeconds);

        // Then
        assertTrue(result);
        verify(valueOperations, times(1)).setIfAbsent(orderId, "processed", expirationSeconds, TimeUnit.SECONDS);
    }
}
