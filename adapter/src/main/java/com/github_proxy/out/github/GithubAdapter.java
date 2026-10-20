package com.github_proxy.out.github;

import com.github_proxy.GitHubClient;
import com.github_proxy.RepositoryMapper;
import com.github_proxy.model.Repository;
import com.github_proxy.port.out.GithubClientPort;
import lombok.RequiredArgsConstructor;

@org.springframework.stereotype.Repository
@RequiredArgsConstructor
public class GithubAdapter implements GithubClientPort {
    private final GitHubClient client;
    private final RepositoryMapper mapper;

    @Override
    public Repository getRepository(String owner, String repo) {
        return mapper.toDomain(client.getRepository(owner, repo));
    }
}
