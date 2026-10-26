package com.codeduel.codeduelapp.auth.api.controller;

import com.codeduel.codeduelapp.auth.api.dto.ApiError;
import com.codeduel.codeduelapp.auth.api.dto.LoginRequest;
import com.codeduel.codeduelapp.auth.api.dto.TokenResponse;
import com.codeduel.codeduelapp.auth.domain.model.IssuedToken;
import com.codeduel.codeduelapp.auth.domain.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Логин и логаут")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Вход по логину и паролю",
            description = "Возвращает JWT. Дальше его нужно передавать в заголовке Authorization: Bearer <token>.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешный вход"),
            @ApiResponse(responseCode = "400", description = "Невалидное тело запроса",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Неверный логин или пароль",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest req) {
        IssuedToken issuedToken = authService.login(req.username(), req.password());
        return new TokenResponse(issuedToken.value(), issuedToken.expiresAt());
    }

    @Operation(summary = "Выход",
            description = "Отзывает текущий токен по jti. Токен становится невалидным до истечения exp.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Токен отозван"),
            @ApiResponse(responseCode = "401", description = "Токен отсутствует или невалиден",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@AuthenticationPrincipal Jwt jwt) {
        authService.logout(jwt.getId(), jwt.getExpiresAt(), Integer.valueOf(jwt.getSubject()));
    }
}
