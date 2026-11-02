package com.codeduel.codeduelapp.matchmaking.domain.port;

/**
 * Порт к battle: есть ли у игрока матч со статусом ACTIVE.
 * Источник истины - Postgres. Реализует battle.
 */
public interface ActiveMatchChecker {

    boolean hasActiveMatch(int userId);
}