package com.codeduel.codeduelapp.auth.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Выпущенный JWT и время его истечения")
public record TokenResponse(
        @Schema(description = "JWT-токен", example = "eyJhbGciOiJIUzI1NiJ9...")
        String token,

        @Schema(description = "Момент истечения (UTC)", example = "2026-10-25T12:34:56Z")
        Instant expiresAt
) {}
