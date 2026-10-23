package com.codeduel.codeduelapp.auth.infrastructure.persistence.repository;

import com.codeduel.codeduelapp.auth.infrastructure.persistence.entity.UserJpaEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

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
