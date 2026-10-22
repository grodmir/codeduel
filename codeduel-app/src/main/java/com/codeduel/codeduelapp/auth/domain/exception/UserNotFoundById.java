package com.codeduel.codeduelapp.auth.domain.exception;

public class UserNotFoundById extends RuntimeException {
    public UserNotFoundById(Integer userId) {
        super("User with id " + userId + " not found");
    }
}
