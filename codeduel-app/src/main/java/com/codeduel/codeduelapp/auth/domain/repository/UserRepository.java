package com.codeduel.codeduelapp.auth.domain.repository;

import com.codeduel.codeduelapp.auth.domain.model.User;

import java.util.Optional;

/**
 * Порт доступа к пользователям. Реализация не важна для домена.
 * */
public interface UserRepository {
    /**
     * Создаёт и сохраняет нового пользователя
     *
     * @param user доменная модель пользователя
     * @return сохранённая модель с подставленным {@code id} и {@code createdAt}
     * @throws IllegalArgumentException если {@code user.id != null}
     * */
    User create(User user);
    /**
     * Обновляет и сохраняет нового пользователя
     *
     * @param user доменная модель обновляемого пользователя
     * @return обновленная модель
     * @throws com.codeduel.codeduelapp.auth.domain.exception.UserNotFoundById при обновлении несуществующего пользователя
     * */
    User update(User user);
    Optional<User> findById(Integer id);
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
