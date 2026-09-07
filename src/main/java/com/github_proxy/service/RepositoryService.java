package com.github_proxy.service;

import com.github_proxy.client.GitHubClient;
import com.github_proxy.dto.GitHubRepository;
import com.github_proxy.dto.RepositoryDto;
import com.github_proxy.mapper.RepositoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@RequiredArgsConstructor
public class RepositoryService {
    private final GitHubClient gitHubClient;
    private final RepositoryMapper repositoryMapper;

    public RepositoryDto getRepository(@PathVariable("owner") String owner, @PathVariable("repo") String repo) {
        GitHubRepository repository = gitHubClient.getRepository(owner, repo);
        return repositoryMapper.toDto(repository);
    }
}
