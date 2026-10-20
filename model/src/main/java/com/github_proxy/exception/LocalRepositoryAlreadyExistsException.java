package com.github_proxy.exception;

public class LocalRepositoryAlreadyExistsException extends GithubProxyException {
    public LocalRepositoryAlreadyExistsException(String fullName) {
        super("Repository " + fullName + " already exists", 409);
    }
}
