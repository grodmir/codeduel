package com.codeduel.codeduelapp.auth.infrastructure.security.service;

/**
 * Хеширование и проверка паролей
 * */
public interface PasswordHasher {
    /**
     * @return хеш для хранения в БД
     * */
    String hash(String rawPassword);
    /**
     * @return {@code true} если пароль соответствует хешу
     * */
    boolean matches(String rawPassword, String hashedPassword);
}
