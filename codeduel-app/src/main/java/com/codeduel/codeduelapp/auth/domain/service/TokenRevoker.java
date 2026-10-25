package com.codeduel.codeduelapp.auth.domain.service;

import java.time.Instant;

/**
 * Управление отзывом JWT (инвалидацией токена).
 *
 * <p>Отзыв не удаляет токен — токен остаётся криптографически валидным
 * до истечения {@code exp}. Проверка отзыва выполняется отдельно
 * (см. {@link com.codeduel.codeduelapp.auth.infrastructure.security.config.RevokeTokenValidator}).
 * */
public interface TokenRevoker {

    /**
     * Отзывает конкретный токен по {@code jti}.
     *
     * @param jti уникальный идентификатор токена
     * @param tokenExpiresAt время истечения токена — используется для TTL записи,
     *                       чтобы не хранить её дольше самого токена
     */
    void revoke(String jti, Instant tokenExpiresAt);

    /**
     * Отзывает все токены пользователя, выпущенные <b>до текущего момента</b>.
     * Токены, выпущенные после, остаются валидными.
     *
     * @param userId идентификатор пользователя
     */
    void revokeAllForUser(Integer userId);

    /**
     * Проверяет, отозван ли конкретный токен
     *
     * @param jti уникальный идентификатор токена
     * */
    boolean isRevoked(String jti);

    /**
     * @return {@code true}, если токен выпущен до момента последнего
     *         {@link #revokeAllForUser} для этого пользователя
     *
     * @param userId идентификатор пользователя
     * @param issuedAt время, когда был выдан конкретный токен, для которого происходит проверка
     */
    boolean isIssuedBeforeRevocation(String userId, Instant issuedAt);
}
