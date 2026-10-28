package com.codeduel.codeduelapp.auth.infrastructure.persistence.mapper;

import com.codeduel.codeduelapp.auth.domain.model.Profile;
import com.codeduel.codeduelapp.auth.infrastructure.persistence.entity.ProfileJpaEntity;
import com.codeduel.codeduelapp.auth.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Преобразование между доменной моделью {@link Profile} и JPA-сущносятью {@link ProfileJpaEntity}.
 * Не содержит бизнес-логики и не обращается к репозиториям.
 * */
@Component
public class ProfileMapper {

    /**
     * Создаёт доменную модель из JPA-сущности
     *
     * @param e JPA-сущность
     * @return доменная модель
     * @throws NullPointerException если {@code e} равен {@code null}
     * */
    public Profile toModel(ProfileJpaEntity e) {
        return Profile.builder()
                .userId(e.getId())
                .rating(e.getRating())
                .wins(e.getWins())
                .losses(e.getLosses())
                .build();
    }

    /**
     * Создаёт новую JPA-сущность из доменной модели профиля пользователя.
     * Сущность не сохранена; идентификатор заполняется при персистентности.
     *
     * @param profile доменная модель профиля пользователя
     * @param user JPA-сущность user для привязки профиля к ней
     * @return новая JPA-сущность
     */
    public ProfileJpaEntity toNewEntity(Profile profile, UserJpaEntity user) {
        ProfileJpaEntity p = new ProfileJpaEntity();
        p.setUser(user);
        p.setRating(profile.getRating());
        p.setWins(profile.getWins());
        p.setLosses(profile.getLosses());
        return p;
    }

    /**
     * Обновляет изменяемые поля существующей JPA-сущности профиля пользователя.
     * Идентификатор не изменяется.
     *
     * @param p управляемая JPA-сущность пользователя
     * @param profile доменная модель с новыми значениями
     */
    public void updateEntity(ProfileJpaEntity p, Profile profile) {
        p.setRating(profile.getRating());
        p.setWins(profile.getWins());
        p.setLosses(profile.getLosses());
    }
}
