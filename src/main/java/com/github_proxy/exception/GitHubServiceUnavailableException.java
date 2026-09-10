package com.github_proxy.exception;

import org.springframework.http.HttpStatus;

public class GitHubServiceUnavailableException extends GithubProxyException {
    public GitHubServiceUnavailableException() {
        super("GitHub API unavailable, try again later", HttpStatus.SERVICE_UNAVAILABLE);
    }
}
