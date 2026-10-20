package com.github_proxy.exception;

public class GitHubServiceUnavailableException extends GithubProxyException {
    public GitHubServiceUnavailableException() {
        super("GitHub API unavailable, try again later", 503);
    }
}
