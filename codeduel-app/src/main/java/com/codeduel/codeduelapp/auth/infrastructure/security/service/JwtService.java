package com.codeduel.codeduelapp.auth.infrastructure.security.service;

import com.codeduel.codeduelapp.auth.domain.model.AuthenticatedUser;
import com.codeduel.codeduelapp.auth.domain.model.IssuedToken;
import com.codeduel.codeduelapp.auth.domain.service.TokenIssuer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Выпускает HS256-JWT для аутентифицированного пользователя.
 *
 * <p>В токен кладутся: {@code sub = id}, {@code username}, {@code roles}
 * (с префиксом {@code ROLE_}), {@code jti} (UUID) и стандартные
 * {@code iss}/{@code iat}/{@code exp}. {@code jti} используется
 * для точечного отзыва.
 */
@Slf4j
@Service
public class JwtService implements TokenIssuer {

    private final JwtEncoder encoder;
    private final Duration ttl;

    public static final String ISSUER = "codeduel";

    public JwtService(JwtEncoder encoder, @Value("${app.jwt.ttl}") Duration ttl) {
        this.encoder = encoder;
        this.ttl = ttl;
    }

    @Override
    public IssuedToken issue(AuthenticatedUser user) {
        Instant now = Instant.now();
        Instant exp = now.plus(ttl);
        String jti = UUID.randomUUID().toString();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .subject(String.valueOf(user.id()))
                .id(jti)
                .issuedAt(now)
                .expiresAt(exp)
                .claim("username", user.username())
                .claim("roles", List.of("ROLE_" + user.role().name()))
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        log.debug("Token issued: userId={} ,jti={}, issuedAt={}",
                claims.getSubject(),
                claims.getId(),
                claims.getIssuedAt()
        );
        return new IssuedToken(token, exp);
    }
}
