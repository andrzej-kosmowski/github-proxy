package com.github_proxy.exception.handler;

import com.github_proxy.dto.ErrorMessageDto;
import com.github_proxy.exception.GithubProxyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GithubProxyExceptionHandler {
    @ExceptionHandler(GithubProxyException.class)
    public ResponseEntity<ErrorMessageDto> handleGithubProxyException(GithubProxyException exception) {
        log.error("Business exception: status={}, message={}", exception.getStatus(), exception.getMessage());
        HttpStatus status = exception.getStatus();
        ErrorMessageDto error = new ErrorMessageDto(LocalDateTime.now(), status.value(), status.getReasonPhrase(),
                exception.getMessage());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageDto> handleUnexpectedException(
            Exception exception
    ) {
        log.error("Unexpected exception occurred: {}", exception.getMessage(), exception);
        ErrorMessageDto error = new ErrorMessageDto(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                exception.getMessage()
        );
        return ResponseEntity
                .internalServerError()
                .body(error);
    }
}
