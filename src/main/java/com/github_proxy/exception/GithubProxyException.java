package com.github_proxy.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class GithubProxyException extends RuntimeException {
    private final HttpStatus status;
    protected GithubProxyException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
