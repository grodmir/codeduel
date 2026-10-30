package com.codeduel.codeduelapp.auth.domain.repository;

import com.codeduel.codeduelapp.auth.domain.model.Profile;

import java.util.Optional;

/**
 * Порт доступа к данным профиля пользователя
 * */
public interface ProfileRepository {
    /**
     * Создаёт профиль для уже существующего пользователя
     * @throws com.codeduel.codeduelapp.auth.domain.exception.UserNotFoundById если пользователя с {@code profile.userId} нет
     * */
    Profile create(Profile profile);
    /**
     * Обновление профиля пользователя (userId не меняется
     * @throws com.codeduel.codeduelapp.auth.domain.exception.ProfileNotFoundException если профиль не найден
     * */
    Profile update(Profile profile);

    Optional<Profile> findByUserId(Integer id);
}
