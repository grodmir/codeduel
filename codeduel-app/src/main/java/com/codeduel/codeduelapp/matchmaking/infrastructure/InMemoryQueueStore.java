package com.codeduel.codeduelapp.matchmaking.infrastructure;

import com.codeduel.codeduelapp.matchmaking.domain.model.QueueEntry;
import com.codeduel.codeduelapp.matchmaking.domain.repository.QueueStore;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class InMemoryQueueStore implements QueueStore {

    Map<Integer, QueueEntry> entries = new LinkedHashMap<>();

    @Override
    public boolean add(QueueEntry entry) {
        Objects.requireNonNull(entry, "entry");
        return entries.putIfAbsent(entry.userId(), entry) == null;
    }

    @Override
    public boolean remove(int userId) {
        return entries.remove(userId) != null;
    }

    @Override
    public Optional<QueueEntry> find(int userId) {
        return Optional.ofNullable(entries.get(userId));
    }

    @Override
    public List<QueueEntry> snapshot() {
        return List.copyOf(entries.values());
    }
}
