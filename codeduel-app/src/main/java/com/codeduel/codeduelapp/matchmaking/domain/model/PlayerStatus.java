package com.codeduel.codeduelapp.matchmaking.domain.model;

/**
 * Статус игрока для GET /matchmaking/status.
 * Пока пара формируется (до MATCH_FOUND), игрок остаётся в статусе SEARCHING.
 */
public enum PlayerStatus {
    IDLE, SEARCHING, IN_MATCH
}
