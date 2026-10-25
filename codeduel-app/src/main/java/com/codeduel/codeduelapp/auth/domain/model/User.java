package com.codeduel.codeduelapp.auth.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

/**
 * Доменная модель пользователя
 * */
@Getter
@Builder(toBuilder = true)
public class User {
    private Integer id;
    private String username;
    private String email;
    private String passwordHash;
    private Role role;
    private int rating;
    private int wins;
    private int losses;
    private OffsetDateTime createdAt;

}
