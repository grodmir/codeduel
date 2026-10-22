package com.codeduel.codeduelapp.auth.domain;

import com.codeduel.codeduelapp.auth.domain.model.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Integer id);
    Optional<User> findByEmail(String email);
}
