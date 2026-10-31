package com.codeduel.codeduelapp.matchmaking.domain.repository;

import com.codeduel.codeduelapp.matchmaking.domain.model.QueueEntry;

import java.util.List;
import java.util.Optional;

public interface QueueStore {
    boolean add(QueueEntry entry);
    boolean remove(int userId);
    Optional<QueueEntry> find(int userId);
    List<QueueEntry> snapshot();
}
