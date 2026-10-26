package com.codeduel.codeduelapp.auth.domain.model;

import java.time.Instant;

public record IssuedToken(String value, Instant expiresAt) {}
