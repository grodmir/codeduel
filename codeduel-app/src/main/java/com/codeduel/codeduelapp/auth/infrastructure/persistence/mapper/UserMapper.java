package com.codeduel.codeduelapp.auth.infrastructure.persistence.mapper;

import com.codeduel.codeduelapp.auth.domain.model.User;
import com.codeduel.codeduelapp.auth.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Преобразование между доменной моделью {@link User} и JPA-сущносятью {@link UserJpaEntity}.
 * Не содержит бизнес-логики и не обращается к репозиториям.
 * */
@Component
public class UserMapper {

    /**
     * Создаёт доменную модель из JPA-сущности
     *
     * @param e JPA-сущность
     * @return доменная модель
     * @throws NullPointerException если {@code e} равен {@code null}
     * */
    public User toModel(UserJpaEntity e) {
        return User.builder()
                .id(e.getId())
                .username(e.getUsername())
                .email(e.getEmail())
                .passwordHash(e.getPasswordHash())
                .role(e.getRole())
                .createdAt(e.getCreatedAt())
                .build();
    }

    /**
     * Создаёт новую JPA-сущность из доменной модели пользователя.
     * Сущность не сохранена; идентификатор и время создания
     * заполняются при персистентности.
     *
     * @param user доменная модель пользователя
     * @return новая JPA-сущность
     */
    public UserJpaEntity toNewEntity(User user) {
        UserJpaEntity u = new UserJpaEntity();
        u.setUsername(user.getUsername());
        u.setEmail(user.getEmail());
        u.setPasswordHash(user.getPasswordHash());
        u.setRole(user.getRole());
        return u;
    }

    /**
     * Обновляет изменяемые поля существующей JPA-сущности пользователя.
     * Идентификатор и время создания не изменяются.
     *
     * @param u управляемая JPA-сущность пользователя
     * @param user доменная модель с новыми значениями
     */
    public void updateEntity(UserJpaEntity u, User user) {
        u.setUsername(user.getUsername());
        u.setEmail(user.getEmail());
        u.setPasswordHash(user.getPasswordHash());
        u.setRole(user.getRole());
    }
}
