package com.github_proxy.exception;

import lombok.Getter;

@Getter
public abstract class GithubProxyException extends RuntimeException {
    private final Integer status;
    protected GithubProxyException(String message, int status) {
        super(message);
        this.status = status;
    }
}
