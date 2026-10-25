package com.codeduel.codeduelapp.auth.domain.service;

import java.time.Instant;

public interface TokenRevoker {
    void revoke(String jti, Instant tokenExpiresAt);
    void revokeAllForUser(Integer userId);
}
