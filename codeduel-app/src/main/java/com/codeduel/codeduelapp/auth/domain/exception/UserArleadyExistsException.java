package com.codeduel.codeduelapp.auth.domain.exception;

public class UserArleadyExistsException extends RuntimeException {
    private final String field;
    public UserArleadyExistsException(String field) {
        super(field + " already taken");
        this.field = field;
    }
}
