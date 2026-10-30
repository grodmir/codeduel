package com.codeduel.codeduelapp.auth.domain.model;

import lombok.Builder;
import lombok.Getter;

/**
 * Доменная модель профиля пользователя
 * */
@Getter
@Builder(toBuilder = true)
public class Profile {
    private Integer userId;
    private int rating;
    private int wins;
    private int losses;
}
