package com.codeduel.codeduelapp.matchmaking.domain.service;

import com.codeduel.codeduelapp.matchmaking.domain.repository.QueueStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchmakingService {
    private final QueueStore queueStore;

}
