package com.codeduel.codeduelapp.auth.infrastructure.security.config;

import com.codeduel.codeduelapp.auth.domain.service.TokenRevoker;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Дополнительный валидатор JWT: отклоняет отозванные токены.
 * Подключается в {@code JwtDecoder} после стандартных проверок
 * ({@code exp}, {@code nbf}, {@code iss}).
 */
@Component
@RequiredArgsConstructor
public class RevokeTokenValidator implements OAuth2TokenValidator<Jwt> {
    private final TokenRevoker tokenRevoker;

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        if (tokenRevoker.isRevoked(jwt.getId())
                || tokenRevoker.isIssuedBeforeRevocation(jwt.getSubject(), jwt.getIssuedAt())) {
            return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error("invalid_token", "The token has been revoked", null)
            );
        }
        return OAuth2TokenValidatorResult.success();
    }
}
