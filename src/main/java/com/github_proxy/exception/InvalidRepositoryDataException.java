package com.github_proxy.exception;

import org.springframework.http.HttpStatus;

public class InvalidRepositoryDataException extends GithubProxyException {
    public InvalidRepositoryDataException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
