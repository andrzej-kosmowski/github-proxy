package com.github_proxy.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class SecurityExceptionHandler  {
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> handleAuth(AuthenticationException e) {
        return Map.of(
                "status", 401,
                "error", "Unauthorized",
                "message", "JWT token expired",
                "timestamp", Instant.now()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, Object> handleForbidden(AccessDeniedException e) {
        return Map.of(
                "status", 403,
                "error", "Forbidden",
                "message", "Access Denied",
                "timestamp", Instant.now()
        );
    }
}
