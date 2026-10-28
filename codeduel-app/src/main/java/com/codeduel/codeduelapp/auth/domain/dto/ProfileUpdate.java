package com.codeduel.codeduelapp.auth.domain.dto;

/**
 * DTO для изменения данных профиля пользователя
 * */
public record ProfileUpdate(
        Integer userId,
        int rating,
        int wins,
        int losses
) {}
