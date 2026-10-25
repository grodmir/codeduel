package com.codeduel.codeduelapp.auth.infrastructure.security.service;

import com.codeduel.codeduelapp.auth.domain.service.TokenRevoker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

/**
 * Реализация {@link TokenRevoker} на Redis.
 *
 * <p>Используются два ключа: {@code jwt:revoked:<jti>} для точечного отзыва
 * и {@code jwt:user-revoked-before:<id>} для отзыва всех токенов пользователя.
 * TTL записей — время жизни токена + {@link #LEEWAY} (компенсация рассинхрона
 * часов и секундной точности {@code exp}).
 */
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

    @Override
    public void revoke(String jti, Instant tokenExpiresAt) {
        Duration ttl = Duration.between(Instant.now(), tokenExpiresAt).plus(LEEWAY);
        if (ttl.isPositive()) {
            // "1" тут в качестве заглушки, так как нам важно только значение ключа
            redis.opsForValue().set(JTI_KEY_PREFIX + jti, "1", ttl);
        }
    }

    @Override
    public void revokeAllForUser(Integer userId) {
        // Время хранения записи с запасом, поэтому умножаем на 2
        redis.opsForValue().set(
                USER_KEY_PREFIX + userId,
                String.valueOf(Instant.now().getEpochSecond()),
                tokenTtl.multipliedBy(2).plus(LEEWAY)
        );
    }

    @Override
    public boolean isRevoked(String jti) {
        return jti != null && Boolean.TRUE.equals(redis.hasKey(JTI_KEY_PREFIX + jti));
    }

    @Override
    public boolean isIssuedBeforeRevocation(String userId, Instant issuedAt) {
        String value = redis.opsForValue().get(USER_KEY_PREFIX + userId);
        return value != null
                && issuedAt != null
                && issuedAt.getEpochSecond() < Long.parseLong(value);
    }
}
