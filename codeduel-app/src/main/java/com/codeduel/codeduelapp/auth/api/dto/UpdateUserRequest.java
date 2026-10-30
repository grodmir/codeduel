package com.codeduel.codeduelapp.auth.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Schema(description = "Частичное обновление профиля. Незаполненные (null) поля не меняются. " +
        "Если задан newPassword, обязателен currentPassword.")
public record UpdateUserRequest(
        @Schema(description = "Новый email", example = "new@example.com", nullable = true, maxLength = 254)
        @Email @Size(max = 254)
        String email,

        @Schema(description = "Новый пароль", example = "NewP@ss123", nullable = true, minLength = 8, maxLength = 72)
        @Size(min = 8, max = 72)
        String newPassword,

        @Schema(description = "Текущий пароль (требуется при смене пароля)", example = "OldP@ss123", nullable = true)
        String currentPassword
) {
    @Schema(hidden = true)
    @AssertTrue(message = "Invalid update request")
    public boolean isPatchValid() {
        boolean validEmail =
                email == null || !email.isBlank();

        boolean validPasswordChange =
                newPassword == null
                        ? currentPassword == null
                        : currentPassword != null
                        && !currentPassword.isBlank();

        return validEmail && validPasswordChange;
    }
}
