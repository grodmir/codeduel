package com.codeduel.codeduelapp.auth.domain.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String field) {
        super(field + " already taken");
    }
}
