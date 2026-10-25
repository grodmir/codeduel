package com.codeduel.codeduelapp.auth.infrastructure.security;

import com.codeduel.codeduelapp.auth.domain.service.TokenRevoker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class TokenBlackList implements TokenRevoker {

    private static final String JTI_KEY_PREFIX  = "jwt:revoked:";
    private static final String USER_KEY_PREFIX = "jwt:user-revoked-before:";
    // Разбежка в 60 секунд из-за особенностей проверки Spring-ом поля exp
    private static final Duration LEEWAY = Duration.ofSeconds(60);

    private final StringRedisTemplate redis;
    private final Duration tokenTtl;

    public TokenBlackList(StringRedisTemplate redis, @Value("${app.jwt.ttl}") Duration tokenTtl) {
        this.redis = redis;
        this.tokenTtl = tokenTtl;
    }

    /**
     * Считать конкретный токен {@code jti} недействительным с текущего момента.
     * Хранить запись остаток времени жизни токена + 60 секунд разбежки.
     * */
    @Override
    public void revoke(String jti, Instant tokenExpiresAt) {
        Duration ttl = Duration.between(Instant.now(), tokenExpiresAt).plus(LEEWAY);
        if (ttl.isPositive()) {
            // "1" тут в качестве заглушки, так как нам важно только значение ключа
            redis.opsForValue().set(JTI_KEY_PREFIX + jti, "1", ttl);
        }
    }

    /**
     * Считать все токены выданные с текущего момента для пользователя {@code userId} недействительными.
     * Хранить запись максимальное время жизни токена,
     * т.к. нет возможности проверить даты их создания + 60 секунд разбежки
     * Возможно, что запись будет храниться дольше, чем живёт последний устаревший токен.
     * */
    @Override
    public void revokeAllForUser(Integer userId) {
        redis.opsForValue().set(
                USER_KEY_PREFIX + userId,
                String.valueOf(Instant.now().getEpochSecond()),
                tokenTtl.plus(LEEWAY)
        );
    }

    /**
     * Проверка на то, был ли токен {@code jti} отозван через /logout
     * */
    public boolean isRevoked(String jti) {
        return jti != null && Boolean.TRUE.equals(redis.hasKey(JTI_KEY_PREFIX + jti));
    }

    /**
     * Проверка, выдан ли этот токен ({@code issuedAt}) раньше, чем сбросили все токены пользователя ({@code userId})
     * */
    public boolean isIssuedBeforeRevocation(String userId, Instant issuedAt) {
        String value = redis.opsForValue().get(USER_KEY_PREFIX + userId);
        return value != null
                && issuedAt != null
                && issuedAt.getEpochSecond() < Long.parseLong(value);
    }
}
