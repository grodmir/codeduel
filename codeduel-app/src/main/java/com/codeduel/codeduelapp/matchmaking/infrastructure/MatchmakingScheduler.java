package com.codeduel.codeduelapp.matchmaking.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Запускает подбор с интервалом из конфигурации. Используется собственный однопоточный executor:
 * он не зависит от общего TaskScheduler приложения, а fixedDelay исключает наложение тиков.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchmakingScheduler {
}
