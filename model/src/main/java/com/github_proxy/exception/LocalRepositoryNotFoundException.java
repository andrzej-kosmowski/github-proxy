package com.github_proxy.exception;

public class LocalRepositoryNotFoundException extends GithubProxyException {
    public LocalRepositoryNotFoundException(String fullName) {
        super("Repository " + fullName + " not found in local database", 404);
    }
}
