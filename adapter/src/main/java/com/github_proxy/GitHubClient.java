package com.github_proxy;

import com.github_proxy.dto.GithubRepositoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "github-client", url = "${app.githubClient.url}", configuration = GitHubFeignConfig.class,
        fallbackFactory = GitHubClientFallbackFactory.class)
public interface GitHubClient {
    @GetMapping("/{owner}/{repo}")
    GithubRepositoryResponse getRepository(@PathVariable("owner") String owner, @PathVariable("repo") String repo);
}
