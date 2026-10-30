package com.codeduel.codeduelapp.auth.domain.exception;

public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(Integer userId) {
        super("The user profile with id:" + userId + " was not found.");
    }
}
