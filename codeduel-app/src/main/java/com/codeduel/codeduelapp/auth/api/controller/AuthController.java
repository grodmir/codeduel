package com.codeduel.codeduelapp.auth.api.controller;

import com.codeduel.codeduelapp.auth.api.dto.ApiError;
import com.codeduel.codeduelapp.auth.api.dto.LoginRequest;
import com.codeduel.codeduelapp.auth.api.dto.TokenResponse;
import com.codeduel.codeduelapp.auth.domain.service.TokenRevoker;
import com.codeduel.codeduelapp.auth.infrastructure.security.model.AppUserDetails;
import com.codeduel.codeduelapp.auth.infrastructure.security.dto.IssuedToken;
import com.codeduel.codeduelapp.auth.infrastructure.security.service.JwtService;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Логин и логаут")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenRevoker tokenRevoker;

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
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        req.username(),
                        req.password()
                )
        );
        if (!(authentication.getPrincipal() instanceof AppUserDetails user)) {
            throw new IllegalStateException("Unexpected user principal: " + authentication.getPrincipal().getClass());
        }
        IssuedToken issuedToken = jwtService.generate(user);
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
        tokenRevoker.revoke(jwt.getId(), jwt.getExpiresAt());
    }
}
