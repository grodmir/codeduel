package com.codeduel.codeduelapp.matchmaking.domain.model;

public enum LeaveResult {
    /** Игрок был в очереди и удалён */
    REMOVED,
    /** Игрока в очереди не было */
    NOT_IN_QUEUE,
    /** Планировщик уже изъял игрока, пара формируется. Клиент ждёт MATCH_FOUND */
    ALREADY_MATCHED
}
