package com.codeduel.codeduelapp.auth.api.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Email
        @Size(max = 254)
        String email,

        @Size(min = 8, max = 72)
        String newPassword,

        String currentPassword
) {
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
