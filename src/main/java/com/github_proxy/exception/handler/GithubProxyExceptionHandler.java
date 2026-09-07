package com.github_proxy.exception.handler;

import com.github_proxy.dto.ErrorMessageDto;
import com.github_proxy.exception.GithubProxyException;
import feign.FeignException;
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
        HttpStatus status = exception.getStatus();
        ErrorMessageDto error = new ErrorMessageDto(LocalDateTime.now(), status.value(), status.getReasonPhrase(),
                exception.getMessage());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageDto> handleUnexpectedException(
            Exception exception
    ) {
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

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<ErrorMessageDto> handleNotFoundException(FeignException.NotFound exception) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorMessageDto error = new ErrorMessageDto(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                "Github repository not found"
        );
        return ResponseEntity.status(status).body(error);
    }
}
