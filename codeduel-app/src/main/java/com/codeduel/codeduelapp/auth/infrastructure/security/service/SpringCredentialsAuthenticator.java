package com.codeduel.codeduelapp.auth.infrastructure.security.service;

import com.codeduel.codeduelapp.auth.domain.exception.InvalidCredentialsException;
import com.codeduel.codeduelapp.auth.domain.model.AuthenticatedUser;
import com.codeduel.codeduelapp.auth.domain.service.CredentialsAuthenticator;
import com.codeduel.codeduelapp.auth.infrastructure.security.model.AppUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpringCredentialsAuthenticator implements CredentialsAuthenticator {
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticatedUser authenticate(String username, String rawPassword) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(username, rawPassword)
            );
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException();
        }

        if (!(authentication.getPrincipal() instanceof AppUserDetails user)) {
            throw new IllegalStateException(
                    "Unexpected user principal: "
                            + authentication.getPrincipal().getClass()
            );
        }
        return new AuthenticatedUser(user.getId(), user.getUsername(), user.getRole());
    }
}
