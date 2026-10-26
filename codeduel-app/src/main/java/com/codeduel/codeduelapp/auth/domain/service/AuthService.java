package com.codeduel.codeduelapp.auth.domain.service;

import com.codeduel.codeduelapp.auth.domain.model.AuthenticatedUser;
import com.codeduel.codeduelapp.auth.domain.model.IssuedToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final TokenIssuer tokenIssuer;
    private final CredentialsAuthenticator authenticator;
    private final TokenRevoker tokenRevoker;

    /**
     * @throws com.codeduel.codeduelapp.auth.domain.exception.InvalidCredentialsException если логин или пароль неверны
     * */
    public IssuedToken login(String username, String password) {
        AuthenticatedUser authenticatedUser = authenticator.authenticate(username, password);
        IssuedToken token = tokenIssuer.issue(authenticatedUser);
        log.info("Login successful: userId={}", authenticatedUser.id());
        return token;
    }

    /**
     * Отзывает текущий токен.
     * */
    public void logout(String jti, Instant expiresAt, Integer userId) {
        tokenRevoker.revoke(jti, expiresAt);
        log.info("User logged out: userId={}", userId);
    }
}
