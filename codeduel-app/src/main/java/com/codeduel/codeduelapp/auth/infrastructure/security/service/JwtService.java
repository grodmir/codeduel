package com.codeduel.codeduelapp.auth.infrastructure.security.service;

import com.codeduel.codeduelapp.auth.infrastructure.security.model.AppUserDetails;
import com.codeduel.codeduelapp.auth.infrastructure.security.dto.IssuedToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Выпускает HS256-JWT для аутентифицированного пользователя.
 *
 * <p>В токен кладутся: {@code sub = id}, {@code username}, {@code roles}
 * (с префиксом {@code ROLE_}), {@code jti} (UUID) и стандартные
 * {@code iss}/{@code iat}/{@code exp}. {@code jti} используется
 * для точечного отзыва.
 */
@Service
public class JwtService {

    private final JwtEncoder encoder;
    private final Duration ttl;

    public static final String ISSUER = "codeduel";

    public JwtService(JwtEncoder encoder, @Value("${app.jwt.ttl}") Duration ttl) {
        this.encoder = encoder;
        this.ttl = ttl;
    }

    public IssuedToken generate(AppUserDetails user) {
        Instant now = Instant.now();
        Instant exp = now.plus(ttl);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .subject(String.valueOf(user.getId()))
                .id(UUID.randomUUID().toString())
                .issuedAt(now)
                .expiresAt(exp)
                .claim("username", user.getUsername())
                .claim("roles", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).toList())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new IssuedToken(token, exp);
    }
}
