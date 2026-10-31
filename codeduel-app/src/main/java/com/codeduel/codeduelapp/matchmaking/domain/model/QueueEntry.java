package com.codeduel.codeduelapp.matchmaking.domain.model;

import java.time.Instant;

public record QueueEntry(int userId, int rating, Instant joinedAt) {}
