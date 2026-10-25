package com.codeduel.codeduelapp.auth.api.controller;

import com.codeduel.codeduelapp.auth.api.dto.LoginRequest;
import com.codeduel.codeduelapp.auth.api.dto.TokenResponse;
import com.codeduel.codeduelapp.auth.domain.service.TokenRevoker;
import com.codeduel.codeduelapp.auth.infrastructure.security.model.AppUserDetails;
import com.codeduel.codeduelapp.auth.infrastructure.security.dto.IssuedToken;
import com.codeduel.codeduelapp.auth.infrastructure.security.service.JwtService;
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
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenRevoker tokenRevoker;

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

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@AuthenticationPrincipal Jwt jwt) {
        tokenRevoker.revoke(jwt.getId(), jwt.getExpiresAt());
    }
}
