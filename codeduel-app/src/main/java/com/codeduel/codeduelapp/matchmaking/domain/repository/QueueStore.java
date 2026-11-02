package com.codeduel.codeduelapp.matchmaking.domain.repository;

import com.codeduel.codeduelapp.matchmaking.domain.model.QueueEntry;

import java.util.List;
import java.util.Optional;

/**
 * Хранилище очереди поиска игроков.
 * <p>
 * Реализации НЕ потокобезопасны: синхронизацию (общую блокировку) обеспечивает {@code MatchmakingService}.
 * Не добавляйте блокировку внутрь реализаций.
 * */
public interface QueueStore {

    /**
     * Добавляет игрока в конец очереди.
     *
     * @return false, если игрок с таким userId уже в очереди (существующая запись не меняется)
     * */
    boolean add(QueueEntry entry);

    /** @return true, если игрок был в очереди и удалён */
    boolean remove(int userId);

    Optional<QueueEntry> find(int userId);

    /** Неизменная копия очереди в порядке входа (кто дольше ждёт, тот и раньше) */
    List<QueueEntry> snapshot();
}
