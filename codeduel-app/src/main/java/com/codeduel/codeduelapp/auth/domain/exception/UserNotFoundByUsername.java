package com.codeduel.codeduelapp.auth.domain.exception;

public class UserNotFoundByUsername extends RuntimeException {
    public UserNotFoundByUsername(String username) {
        super("User with username " + username + " not found");
    }
}
