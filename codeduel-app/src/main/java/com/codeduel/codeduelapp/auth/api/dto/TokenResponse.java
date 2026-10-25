package com.codeduel.codeduelapp.auth.api.dto;

import java.time.Instant;

public record TokenResponse(String token, Instant expiresAt) {
}
