package com.codeduel.codeduelapp.auth.infrastructure.persistence.repository;

import com.codeduel.codeduelapp.auth.infrastructure.persistence.entity.UserJpaEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Репозиторий пользователей.
 *
 * <p>Методы, возвращающие {@link UserJpaEntity}, используют
 * {@code @EntityGraph} для загрузки профиля — без этого обращение
 * к {@code getProfile()} вне транзакции приведёт к
 * {@code LazyInitializationException} (см. {@code UserMapper}).
 * */
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Integer> {

    @EntityGraph(attributePaths = "profile")
    Optional<UserJpaEntity> findById(@NonNull Integer id);

    @EntityGraph(attributePaths = "profile")
    Optional<UserJpaEntity> findByEmail(String email);

    @EntityGraph(attributePaths = "profile")
    Optional<UserJpaEntity> findByUsername(String username);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
