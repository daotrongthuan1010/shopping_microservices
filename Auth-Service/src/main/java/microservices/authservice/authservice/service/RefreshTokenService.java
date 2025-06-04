package microservices.authservice.authservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class RefreshTokenService {

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final Duration REFRESH_TOKEN_EXPIRY = Duration.ofDays(7);

    // Fallback in-memory storage when Redis is not available
    private final Map<String, Long> inMemoryTokens = new ConcurrentHashMap<>();

    public String createRefreshToken(Long userId) {
        String refreshToken = UUID.randomUUID().toString();

        if (redisTemplate != null) {
            try {
                String key = REFRESH_TOKEN_PREFIX + refreshToken;
                redisTemplate.opsForValue().set(key, userId, REFRESH_TOKEN_EXPIRY);
                log.info("Created refresh token in Redis for user: {}", userId);
            } catch (Exception e) {
                log.warn("Redis not available, using in-memory storage: {}", e.getMessage());
                inMemoryTokens.put(refreshToken, userId);
            }
        } else {
            log.info("Redis not configured, using in-memory storage for user: {}", userId);
            inMemoryTokens.put(refreshToken, userId);
        }

        return refreshToken;
    }

    public Long getUserIdFromRefreshToken(String refreshToken) {
        if (redisTemplate != null) {
            try {
                String key = REFRESH_TOKEN_PREFIX + refreshToken;
                Object userId = redisTemplate.opsForValue().get(key);
                if (userId != null) {
                    return Long.valueOf(userId.toString());
                }
            } catch (Exception e) {
                log.warn("Redis error, checking in-memory storage: {}", e.getMessage());
            }
        }

        // Fallback to in-memory
        Long userId = inMemoryTokens.get(refreshToken);
        if (userId == null) {
            log.warn("Refresh token not found or expired: {}", refreshToken);
        }
        return userId;
    }

    public boolean validateRefreshToken(String refreshToken) {
        if (redisTemplate != null) {
            try {
                String key = REFRESH_TOKEN_PREFIX + refreshToken;
                return Boolean.TRUE.equals(redisTemplate.hasKey(key));
            } catch (Exception e) {
                log.warn("Redis error, checking in-memory storage: {}", e.getMessage());
            }
        }

        // Fallback to in-memory
        return inMemoryTokens.containsKey(refreshToken);
    }

    public void deleteRefreshToken(String refreshToken) {
        if (redisTemplate != null) {
            try {
                String key = REFRESH_TOKEN_PREFIX + refreshToken;
                redisTemplate.delete(key);
                log.info("Deleted refresh token from Redis: {}", refreshToken);
            } catch (Exception e) {
                log.warn("Redis error: {}", e.getMessage());
            }
        }

        // Also remove from in-memory
        inMemoryTokens.remove(refreshToken);
        log.info("Deleted refresh token from memory: {}", refreshToken);
    }

    public void deleteAllRefreshTokensForUser(Long userId) {
        // This is a simple implementation - in production you might want to maintain a user->tokens mapping
        log.info("Request to delete all refresh tokens for user: {}", userId);
    }
}
