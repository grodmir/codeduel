package com.codeduel.codeduelapp.auth.domain.exception;

public class InvalidCurrentPasswordException extends RuntimeException {
    public InvalidCurrentPasswordException() { super("Invalid current password"); }
}