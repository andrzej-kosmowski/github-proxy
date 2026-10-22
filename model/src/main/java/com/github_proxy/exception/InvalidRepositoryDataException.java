package com.github_proxy.exception;

public class InvalidRepositoryDataException extends GithubProxyException {
    public InvalidRepositoryDataException(String message) {
        super(message, 409);
    }
}
