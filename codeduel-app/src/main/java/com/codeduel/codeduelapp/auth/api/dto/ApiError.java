package com.codeduel.codeduelapp.auth.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Стандартный формат ошибки API")
public record ApiError(
        @Schema(description = "Момент возникновения", example = "2026-10-25T10:00:00Z")
        Instant timestamp,

        @Schema(description = "HTTP-статус", example = "409")
        int status,

        @Schema(description = "Текстовое имя статуса", example = "Conflict")
        String error,

        @Schema(description = "Описание ошибки", example = "EMAIL: already exists")
        String message
) {}