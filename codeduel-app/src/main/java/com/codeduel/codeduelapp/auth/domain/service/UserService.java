package com.codeduel.codeduelapp.auth.domain.service;

import com.codeduel.codeduelapp.auth.domain.exception.InvalidCurrentPasswordException;
import com.codeduel.codeduelapp.auth.domain.exception.UserArleadyExistsException;
import com.codeduel.codeduelapp.auth.domain.exception.UserNotFoundById;
import com.codeduel.codeduelapp.auth.domain.exception.UserNotFoundByUsername;
import com.codeduel.codeduelapp.auth.domain.model.Role;
import com.codeduel.codeduelapp.auth.domain.model.User;
import com.codeduel.codeduelapp.auth.domain.repository.UserRepository;
import com.codeduel.codeduelapp.auth.infrastructure.security.service.PasswordHasher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Создание и управление пользователями.
 * */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordHasher passwordEncoder;
    private final TokenRevoker tokenRevoker;

    @Value("${elo.initial}")
    private int initialRating;

    /**
     * Создание нового пользователя.
     *
     * <p>{@code username} обрезается по краям, {@code email} - обрезается и приводится
     * к нижнему регистру; уникальность проверяется по нормализованным значениям.
     * Роль всегда {@code USER}, рейтинг - {@code elo.initial}.
     *
     * @param username псевдоним пользователя
     * @param email почта пользователя
     * @param password пароль пользователя. Передаётся в raw-формате, кодируется внутри метода.
     *
     * @throws UserArleadyExistsException если username или email уже заняты
     * */
    @Transactional
    public void create(String username, String email, String password) {
        String normUsername = username.trim();
        String normEmail = email.trim().toLowerCase();

        if (userRepository.existsByUsername(normUsername)) throw new UserArleadyExistsException("USERNAME");
        if (userRepository.existsByEmail(normEmail)) throw new UserArleadyExistsException("EMAIL");

        User user = User.builder()
                .username(normUsername)
                .email(normEmail)
                .passwordHash(passwordEncoder.hash(password))
                .role(Role.USER)
                .rating(initialRating)
                .wins(0)
                .losses(0)
                .build();
        userRepository.save(user);
        log.info("User created: userId={}", user.getId());
    }

    /**
     * @throws UserNotFoundById если пользователя с таким {@code id} нету
     * */
    @Transactional(readOnly = true)
    public User getById(Integer id) {
        return userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundById(id)
        );
    }

    /**
     * @throws UserNotFoundByUsername если пользователя с таким {@code username} не существует
     * */
    @Transactional(readOnly = true)
    public User getByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundByUsername(username)
        );
    }

    /**
     * Частичное обновление: @{code null}-поля не изменяются
     *
     * <p>Email нормализуется (trim + lowercase) и проверяется на уникальность, только если отличается
     * от текущего. Смена пароля требует @{code currentPassword} и <b>отзывает
     * все активные токены</b> пользователя.
     *
     * @throws UserArleadyExistsException если новый email уже занят
     * @throws InvalidCurrentPasswordException если {@code newPassword} задан, а {@code currentPassword} не
     *  передан или не совпадает.
     * @throws UserNotFoundById если пользователя с {@code id} не существует.
     * */
    @Transactional
    public User update(Integer id, String email, String newPassword, String currentPassword) {
        User user = getById(id);
        var builder = user.toBuilder();

        if (email != null) {
            String normEmail = email.trim().toLowerCase();
            if (!normEmail.equals(user.getEmail())) {
                if (userRepository.existsByEmail(normEmail)) throw new UserArleadyExistsException("EMAIL");
                builder.email(normEmail);
                log.info("User email changed: userId={}", id);
            }
        }

        if (newPassword != null) {
            if (currentPassword == null || !passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
                log.warn("Password change rejected: invalid current password, userId={}", id);
                throw new InvalidCurrentPasswordException();
            }
            builder.passwordHash(passwordEncoder.hash(newPassword));
            tokenRevoker.revokeAllForUser(user.getId());
            log.info("User password changed, all tokens revoked: userId={}", id);
        }

        return userRepository.save(builder.build());
    }

    /**
     * Помечает пользователя удалённым (soft delete) и
     * <b>отзывает все активные токены</b>.
     *
     * @throws UserNotFoundById если пользователя нет
     */
    @Transactional
    public void deleteById(Integer id) {
        userRepository.deleteById(id);
        tokenRevoker.revokeAllForUser(id);
        log.info("User deleted (soft), all tokens revoked: userId={}", id);
    }
}
