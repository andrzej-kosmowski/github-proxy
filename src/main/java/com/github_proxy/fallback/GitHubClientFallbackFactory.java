package com.github_proxy.fallback;

import com.github_proxy.client.GitHubClient;
import com.github_proxy.exception.GitHubServiceUnavailableException;
import com.github_proxy.exception.GithubProxyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GitHubClientFallbackFactory implements FallbackFactory<GitHubClient> {
    @Override
    public GitHubClient create(Throwable cause) {
        log.error("An exception occurred when calling the GitHubClient", cause);
        return (owner, repo) -> {
            log.error("GitHub API unavailable: owner={}, repo={}", owner, repo);
            if (cause instanceof GithubProxyException exception) {
                throw exception;
            }
            throw new GitHubServiceUnavailableException();
        };
    }
}
