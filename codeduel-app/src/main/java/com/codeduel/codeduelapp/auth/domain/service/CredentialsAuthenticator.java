package com.codeduel.codeduelapp.auth.domain.service;

import com.codeduel.codeduelapp.auth.domain.model.AuthenticatedUser;

public interface CredentialsAuthenticator {
    /**
     * Проверяет логин и пароль.
     *
     * @throws com.codeduel.codeduelapp.auth.domain.exception.InvalidCredentialsException если логин или пароль неверны.
     * */
    AuthenticatedUser authenticate(String username, String password);
}
