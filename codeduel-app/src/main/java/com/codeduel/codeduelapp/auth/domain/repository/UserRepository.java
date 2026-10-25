package com.codeduel.codeduelapp.auth.domain.repository;

import com.codeduel.codeduelapp.auth.domain.model.User;

import java.util.Optional;

/**
 * Порт доступа к пользователям. Реализация не важна для домена.
 * */
public interface UserRepository {
    /**
     * Сохраняет пользователя: создаёт нового, если {@code id == null},
     * иначе обновляет существующего
     *
     * @param user доменная модель пользователя
     * @return сохранённая модель с подставленным {@code id} и {@code createdAt}
     * @throws com.codeduel.codeduelapp.auth.domain.exception.UserNotFoundById при обновлении несуществующего пользователя
     * */
    User save(User user);
    Optional<User> findById(Integer id);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    /**
     * Помечает пользователя удалённым (soft delete), не удаляя запись физически.
     *
     * @param userId идентификатор пользователя
     * @throws com.codeduel.codeduelapp.auth.domain.exception.UserNotFoundById если пользователь не найден
     */
    void deleteById(Integer userId);
}
