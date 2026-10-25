package com.codeduel.codeduelapp.auth.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Учётные данные для входа")
public record LoginRequest(
        @Schema(description = "Имя пользователя", example = "alice", minLength = 4, maxLength = 100)
        @NotBlank @Size(min = 4, max = 100)
        String username,

        @Schema(description = "Пароль", example = "P@ssw0rd123", minLength = 3, maxLength = 72)
        @NotBlank @Size(min = 3, max = 72)
        String password
) {
}
