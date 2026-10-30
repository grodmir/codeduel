package com.codeduel.codeduelapp.auth.api.dto;

import com.codeduel.codeduelapp.auth.domain.model.Profile;
import com.codeduel.codeduelapp.auth.domain.model.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(description = "Публичный профиль пользователя")
public record UserResponse(
        @Schema(description = "ID пользователя", example = "42")
        Integer id,

        @Schema(description = "Имя пользователя", example = "alice")
        String username,

        @Schema(description = "Email", example = "alice@example.com")
        String email,

        @Schema(description = "Elo-рейтинг", example = "1200")
        int rating,

        @Schema(description = "Количество побед", example = "10")
        int wins,

        @Schema(description = "Количество поражений", example = "5")
        int losses,

        @Schema(description = "Дата регистрации (UTC)", example = "2026-01-15T10:00:00Z")
        OffsetDateTime createdAt
) {
    public static UserResponse from(User user, Profile profile) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                profile.getRating(),
                profile.getWins(),
                profile.getLosses(),
                user.getCreatedAt()
        );
    }
}
