package com.codeduel.codeduelapp.matchmaking.domain.exception;

/** Игрок уже в очереди, в процессе формирования пары или в активном матче. Соответствует 409 ALREADY_IN_QUEUE_OR_MATCH. */
public class AlreadyInQueueOrMatchException extends RuntimeException {
    public AlreadyInQueueOrMatchException(int userId) {
        super("User " + userId + " is already in queue or in a match");
    }
}
