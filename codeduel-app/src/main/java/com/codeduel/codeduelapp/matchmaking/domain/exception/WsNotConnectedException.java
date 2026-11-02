package com.codeduel.codeduelapp.matchmaking.domain.exception;

/** У игрока нет активного WS-соединения. Соответствует 409 WS_NOT_CONNECTED. */
public class WsNotConnectedException extends RuntimeException {
    public WsNotConnectedException(int userId) {
        super("User " + userId + " has no active WebSocket connection");
    }
}
