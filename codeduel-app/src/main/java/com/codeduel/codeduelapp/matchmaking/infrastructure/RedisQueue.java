package com.codeduel.codeduelapp.matchmaking.infrastructure;

import com.codeduel.codeduelapp.matchmaking.domain.model.QueueEntry;
import com.codeduel.codeduelapp.matchmaking.domain.repository.QueueStore;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RedisQueue implements QueueStore {
    @Override
    public boolean add(QueueEntry entry) {
        return false;
    }

    @Override
    public boolean remove(int userId) {
        return false;
    }

    @Override
    public Optional<QueueEntry> find(int userId) {
        return Optional.empty();
    }

    @Override
    public List<QueueEntry> snapshot() {
        return List.of();
    }
}
