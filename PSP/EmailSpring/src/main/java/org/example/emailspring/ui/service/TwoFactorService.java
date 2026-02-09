package org.example.emailspring.ui.service;

import org.example.emailspring.common.Constantes;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@ConditionalOnProperty(name = Constantes.SPRING_DATA_REDIS_ENABLED, havingValue = Constantes.TRUE, matchIfMissing = false)
public class TwoFactorService {

    private final RedisTemplate<String, String> redisTemplate;

    public TwoFactorService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    public void setPending2FAUsername(String username) {
        String key = Constantes.REDIS_2FA_PENDING_PREFIX + username;
        redisTemplate.opsForValue().set(key, username, Constantes.REDIS_2FA_TTL_MINUTES, TimeUnit.MINUTES);
    }


    public boolean hasPending2FA(String username) {
        String key = Constantes.REDIS_2FA_PENDING_PREFIX + username;
        return redisTemplate.hasKey(key);
    }


    public void removePending2FA(String username) {
        String key = Constantes.REDIS_2FA_PENDING_PREFIX + username;
        redisTemplate.delete(key);
    }


    public void setPending2FASecret(String username, String secret) {
        String key = Constantes.REDIS_2FA_SECRET_PREFIX + username;
        redisTemplate.opsForValue().set(key, secret, Constantes.REDIS_2FA_TTL_MINUTES, TimeUnit.MINUTES);
    }


    public String getPending2FASecret(String username) {
        String key = Constantes.REDIS_2FA_SECRET_PREFIX + username;
        return redisTemplate.opsForValue().get(key);
    }


    public void removePending2FASecret(String username) {
        String key = Constantes.REDIS_2FA_SECRET_PREFIX + username;
        redisTemplate.delete(key);
    }
}

