package com.codeduel.codeduelapp.auth.infrastructure.persistence.mapper;

import com.codeduel.codeduelapp.auth.domain.model.User;
import com.codeduel.codeduelapp.auth.infrastructure.persistence.entity.ProfileJpaEntity;
import com.codeduel.codeduelapp.auth.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Преобразование между доменной моделью {@link User} и JPA-сущносятью {@link UserJpaEntity}
 * (включая {@link ProfileJpaEntity}). Не содержит бизнес-логики и не обращается к репозиториям.
 * */
@Component
public class UserMapper {

    /**
     * Создаёт доменную модель из JPA-сущности
     *
     * @param e сущность с загруженным профилем (не {@code null})
     * @return доменная модель
     * @throws NullPointerException если {@code e} или {@code e.getProfile()} равны {@code null}
     * */
    public User toModel(UserJpaEntity e) {
        ProfileJpaEntity p = e.getProfile();
        return User.builder()
                .id(e.getId())
                .username(e.getUsername())
                .email(e.getEmail())
                .passwordHash(e.getPasswordHash())
                .role(e.getRole())
                .rating(p.getRating())
                .wins(p.getWins())
                .losses(p.getLosses())
                .createdAt(e.getCreatedAt())
                .build();
    }

    /**
     * Создаёт новую JPA-сущность из доменной модели.
     * Сущность не сохранена; {@code id} и {@code createdAt} заполняются при персистентности.
     *
     * @param user доменная модель (не {@code null})
     * @return несохранённая сущность с привязанным профилем
     */
    public UserJpaEntity toNewEntity(User user) {
        UserJpaEntity u = new UserJpaEntity();
        u.setUsername(user.getUsername());
        u.setEmail(user.getEmail());
        u.setPasswordHash(user.getPasswordHash());
        u.setRole(user.getRole());

        ProfileJpaEntity p = new ProfileJpaEntity();
        p.setRating(user.getRating());
        p.setWins(user.getWins());
        p.setLosses(user.getLosses());
        u.attachProfile(p);
        return u;
    }

    /**
     * Обновляет изменяемые поля существующей сущности <b>на месте</b>.
     * JPA-сущность и соответствующие ей данные в таблице обновятся автоматически.
     * {@code id} и {@code createdAt} не трогает.
     *
     * @param u    управляемая сущность с загруженным профилем (не {@code null})
     * @param user доменная модель с новыми значениями (не {@code null})
     * @throws NullPointerException если {@code u.getProfile()} равен {@code null}
     */
    public void updateEntity(UserJpaEntity u, User user) {
        u.setUsername(user.getUsername());
        u.setEmail(user.getEmail());
        u.setPasswordHash(user.getPasswordHash());
        u.setRole(user.getRole());
        u.getProfile().setRating(user.getRating());
        u.getProfile().setWins(user.getWins());
        u.getProfile().setLosses(user.getLosses());
    }
}
