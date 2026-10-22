package com.github_proxy.exception;

public class RepositoryNotFoundException extends GithubProxyException {
    public RepositoryNotFoundException(String fullName) {
        super("Repository " + fullName + " not found on GitHub", 404);
    }
}
