package com.github_proxy.service;

import com.github_proxy.client.GitHubClient;
import com.github_proxy.dto.GitHubRepository;
import com.github_proxy.dto.RepositoryDto;
import com.github_proxy.dto.UpdateRepositoryCommand;
import com.github_proxy.mapper.RepositoryMapper;
import com.github_proxy.model.Repository;
import com.github_proxy.repository.RepoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RepositoryService {
    private final GitHubClient gitHubClient;
    private final RepositoryMapper repositoryMapper;
    private final RepoRepository repoRepository;

    public RepositoryDto getRepository(String owner, String repo) {
        GitHubRepository repository = gitHubClient.getRepository(owner, repo);
        return repositoryMapper.toDto(repository);
    }

    @Transactional
    public RepositoryDto saveRepository(String owner, String repo) {
        GitHubRepository repository = gitHubClient.getRepository(owner, repo);
        Repository entity  = repositoryMapper.toEntity(repository);
        Repository saved = repoRepository.save(entity);
        return repositoryMapper.toDto(saved);
    }

    public RepositoryDto getLocalRepository(String owner, String repo) {
        String fullName = owner + "/" + repo;
        Repository entity = repoRepository.findByFullName(fullName).orElseThrow();
        return repositoryMapper.toDto(entity);
    }

    @Transactional
    public RepositoryDto updateLocalRepository(String owner, String repo, UpdateRepositoryCommand command) {
        String fullName = owner + "/" + repo;
        Repository localRepo = repoRepository.findByFullName(fullName).orElseThrow();
        localRepo.update(command);
        return repositoryMapper.toDto(localRepo);
    }
}
