package com.codeduel.codeduelapp.matchmaking.infrastructure;

import com.codeduel.codeduelapp.matchmaking.config.MatchmakingProperties;
import com.codeduel.codeduelapp.matchmaking.domain.service.MatchmakingService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Запускает подбор с интервалом из конфигурации. Используется собственный однопоточный executor:
 * он не зависит от общего TaskScheduler приложения, а fixedDelay исключает наложение тиков.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MatchmakingScheduler {

    private final MatchmakingService matchmakingService;
    private final MatchmakingProperties matchmakingProperties;

    private volatile ScheduledExecutorService scheduledExecutorService;

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "matchmaking-tick");
            thread.setDaemon(true);
            return thread;
        });
        long delayMs = Math.max(1, matchmakingProperties.tickInterval().toMillis());
        scheduledExecutorService.scheduleWithFixedDelay(this::safeTick, delayMs, delayMs, TimeUnit.MILLISECONDS);
        log.info("Matchmaking scheduler started, tick interval {} ms", delayMs);
    }

    /** Исключение не должно останавливать периодическую задачу. */
    private void safeTick() {
        try {
            matchmakingService.tick();
        } catch (RuntimeException e) {
            log.error("Matchmaking tick failed", e);
        }
    }

    @PreDestroy
    public void stop() {
        ScheduledExecutorService current = scheduledExecutorService;
        if (current != null) {
            current.shutdownNow();
        }
    }


}
