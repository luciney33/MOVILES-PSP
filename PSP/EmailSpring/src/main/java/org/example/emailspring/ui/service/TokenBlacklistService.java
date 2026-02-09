package org.example.emailspring.ui.service;

import io.jsonwebtoken.Claims;
import org.example.emailspring.common.Constantes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@ConditionalOnProperty(name = Constantes.SPRING_DATA_REDIS_ENABLED, havingValue = Constantes.TRUE, matchIfMissing = false)
public class TokenBlacklistService {
    private final Logger logger = LoggerFactory.getLogger(TokenBlacklistService.class);
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtService jwtService;

    public TokenBlacklistService(RedisTemplate<String, String> redisTemplate, JwtService jwtService) {
        this.redisTemplate = redisTemplate;
        this.jwtService = jwtService;
    }


    public void revokeToken(String token) {
        try {
            Claims claims = jwtService.extractAllClaims(token);
            Date expiration = claims.getExpiration();
            long ttl = expiration.getTime() - System.currentTimeMillis();

            if (ttl > 0) {
                String key = Constantes.REDIS_BLACKLIST_PREFIX + token;
                redisTemplate.opsForValue().set(key, Constantes.REVOKED, ttl, TimeUnit.MILLISECONDS);
            }
        } catch (Exception e) {
            logger.error(Constantes.ERROR_REVOCANDO_TOKEN, e);
        }
    }


    public boolean isTokenRevoked(String token) {
        try {
            String key = Constantes.REDIS_BLACKLIST_PREFIX + token;
            String value = redisTemplate.opsForValue().get(key);
            return Constantes.REVOKED.equals(value);
        } catch (Exception e) {
            return false;
        }
    }

}

