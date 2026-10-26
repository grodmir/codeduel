package com.codeduel.codeduelapp.auth.api.advice;

import com.codeduel.codeduelapp.auth.api.dto.ApiError;
import com.codeduel.codeduelapp.auth.domain.exception.*;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

/**
 * Глобальный обработчик ошибок.
 * Переводит доменные и валидационные исключения в HTTP-ответы
 * формата {@link ApiError}.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException exception
    ) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Invalid request");
        log.debug("Validation failed: {}", message);
        return buildError(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(
            ConstraintViolationException exception
    ) {
        log.debug("Constraint violation: {}", exception.getMessage());
        return buildError(HttpStatus.BAD_REQUEST, "Request parameters are invalid");
    }

    @ExceptionHandler(UserArleadyExistsException.class)
    public ResponseEntity<ApiError> handleUserAlreadyExists(
            UserArleadyExistsException exception
    ) {
        log.info("Conflict: {}", exception.getMessage());
        return buildError(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler({
            UserNotFoundById.class,
            UserNotFoundByUsername.class
    })
    public ResponseEntity<ApiError> handleUserNotFound(
            RuntimeException exception
    ) {
        log.info("Not found: {}", exception.getClass().getSimpleName());
        return buildError(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(InvalidCurrentPasswordException.class)
    public ResponseEntity<ApiError> handleInvalidCurrentPassword(
            InvalidCurrentPasswordException exception
    ) {
        return buildError(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(InvalidCredentialsException exception) {
        log.warn("Login failed: bad credentials");
        return buildError(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException e) {
        log.warn("Access denied", e);
        return buildError(HttpStatus.FORBIDDEN, "Access denied");
    }

    // Страховочная сеть: всё неожиданное
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception exception) {
        log.error("Unexpected error", exception);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }

    private ResponseEntity<ApiError> buildError(
            HttpStatus status,
            String message
    ) {
        ApiError apiError = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message
        );

        return ResponseEntity.status(status).body(apiError);
    }
}
