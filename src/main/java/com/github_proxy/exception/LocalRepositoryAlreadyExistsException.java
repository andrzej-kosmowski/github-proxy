package com.github_proxy.exception;

import org.springframework.http.HttpStatus;

public class LocalRepositoryAlreadyExistsException extends GithubProxyException {
    public LocalRepositoryAlreadyExistsException(String fullName) {
        super("Repository " + fullName + " already exists", HttpStatus.CONFLICT);
    }
}
