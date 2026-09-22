package com.github_proxy.exception;

import org.springframework.http.HttpStatus;

public class LocalRepositoryNotFoundException extends GithubProxyException {
    public LocalRepositoryNotFoundException(String fullName) {
        super("Repository " + fullName + " not found in local database", HttpStatus.NOT_FOUND);
    }
}
