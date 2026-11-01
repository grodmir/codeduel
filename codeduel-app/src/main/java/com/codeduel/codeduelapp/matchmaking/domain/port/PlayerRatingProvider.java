package com.codeduel.codeduelapp.matchmaking.domain.port;

public interface PlayerRatingProvider {
    int getRatingByUserId(Integer userId);
}
