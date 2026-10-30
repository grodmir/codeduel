package com.codeduel.codeduelapp.auth.domain.service;

import com.codeduel.codeduelapp.auth.domain.model.AuthenticatedUser;
import com.codeduel.codeduelapp.auth.domain.model.IssuedToken;

/** Выпускает токен доступа для пользователя. */
public interface TokenIssuer {
    IssuedToken issue(AuthenticatedUser user);
}
