package com.codeduel.codeduelapp.auth.domain.exception;

public class InvalidProfileDataException extends RuntimeException {
    public InvalidProfileDataException(Integer userId) {
        super("Invalid Profile Data for user with id: " + userId);
    }
}
