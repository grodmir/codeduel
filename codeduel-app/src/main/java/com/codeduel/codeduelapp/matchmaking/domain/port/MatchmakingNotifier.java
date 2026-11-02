package com.codeduel.codeduelapp.matchmaking.domain.port;

import com.codeduel.codeduelapp.matchmaking.domain.model.MatchFailureReason;

/**
 * Порт уведомлений игроков. Реализация (адаптер поверх SessionRegistry из shared) отправляет WS-события.
 * Методы отправки не должны блокироваться надолго.
 */
public interface MatchmakingNotifier {

    /** Есть ли у игрока живая WS-сессия. Быстрая проверка в памяти. */
    boolean isOnline(int userId);

    /** Событие MATCH_FOUND. */
    void matchFound(int userId, int matchId, int opponentId);

    /** Событие MATCH_FAILED. */
    void matchFailed(int userId, MatchFailureReason reason);
}