package com.github_proxy.exception;

import org.springframework.http.HttpStatus;

public class RepositoryNotFoundException extends GithubProxyException {
    public RepositoryNotFoundException(String fullName) {
        super("Repository " + fullName + " not found on GitHub", HttpStatus.NOT_FOUND);
    }
}
