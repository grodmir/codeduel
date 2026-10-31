package com.codeduel.codeduelapp.matchmaking.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;
import java.util.Objects;

/**
 * Параметры matchmaking. Префикс: app.matchmaking.
 * Переопределяются через application.yml или переменные окружения (relaxed binding).
 *
 * @param baseWindow          начальное окно поиска по рейтингу (±)
 * @param windowStepPerSecond прирост окна за каждую секунду ожидания
 * @param maxWindow           потолок окна
 * @param tickInterval        пауза между запусками подбора
 * @param listenerTimeout     таймаут вызова MatchFoundListener
 */

@ConfigurationProperties(prefix = "app.matchmaking")
public record MatchmakingProperties(
        @DefaultValue("50") int baseWindow,
        @DefaultValue("10") int windowStepPerSecond,
        @DefaultValue("400") int maxWindow,
        @DefaultValue("1s") Duration tickInterval,
        @DefaultValue("10s") Duration listenerTimeout
) {
    public MatchmakingProperties {
        Objects.requireNonNull(tickInterval, "tickInterval");
        Objects.requireNonNull(listenerTimeout, "listenerTimeout");
        if (baseWindow < 0) {
            throw new IllegalArgumentException("baseWindow must be >= 0");
        }
        if (windowStepPerSecond < 0) {
            throw new IllegalArgumentException("windowStepPerSecond must be >= 0");
        }
        if (maxWindow < baseWindow) {
            throw new IllegalArgumentException("maxWindow must be >= baseWindow");
        }
        if (tickInterval.isZero() || tickInterval.isNegative()) {
            throw new IllegalArgumentException("tickInterval must be positive");
        }
        if (listenerTimeout.isZero() || listenerTimeout.isNegative()) {
            throw new IllegalArgumentException("listenerTimeout must be positive");
        }
    }
}
