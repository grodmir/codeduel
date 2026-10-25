package com.codeduel.codeduelapp.auth.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Данные для регистрации")
public record RegisterRequest(
        @Schema(description = "Уникальное имя пользователя", example = "alice", minLength = 3, maxLength = 100)
        @NotBlank @Size(min = 3, max = 100)
        String username,

        @Schema(description = "Уникальный email", example = "alice@example.com", maxLength = 254)
        @NotBlank @Email @Size(max = 254)
        String email,

        @Schema(description = "Пароль (минимум 8 символов)", example = "P@ssw0rd123", minLength = 8, maxLength = 72)
        @NotBlank @Size(min = 8, max = 72)
        String password
) {
}
