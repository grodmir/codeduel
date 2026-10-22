package com.codeduel.codeduelapp.auth.domain.repository;

import com.codeduel.codeduelapp.auth.domain.model.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Integer id);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    void deleteById(Integer id);
}
