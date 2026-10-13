package com.github_proxy.client;

import com.github_proxy.configuration.GitHubFeignConfig;
import com.github_proxy.dto.GitHubRepository;
import com.github_proxy.fallback.GitHubClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "github-client", url = "${app.githubClient.url}", configuration = GitHubFeignConfig.class,
        fallbackFactory = GitHubClientFallbackFactory.class)
public interface GitHubClient {
    @GetMapping("/{owner}/{repo}")
    GitHubRepository getRepository(@PathVariable("owner") String owner, @PathVariable("repo") String repo);
}
