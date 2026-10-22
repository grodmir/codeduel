package com.codeduel.codeduelapp.auth.api.dto;

import com.codeduel.codeduelapp.auth.domain.model.User;

import java.time.OffsetDateTime;

public record UserResponse(
        Integer id,
        String username,
        String email,
        int rating,
        int wins,
        int losses,
        OffsetDateTime createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRating(),
                user.getWins(),
                user.getLosses(),
                user.getCreatedAt()
        );
    }
}
