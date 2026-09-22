package com.github_proxy.service;

import com.github_proxy.client.GitHubClient;
import com.github_proxy.dto.GitHubRepository;
import com.github_proxy.dto.RepositoryDto;
import com.github_proxy.dto.UpdateRepositoryCommand;
import com.github_proxy.exception.LocalRepositoryAlreadyExistsException;
import com.github_proxy.exception.LocalRepositoryNotFoundException;
import com.github_proxy.mapper.RepositoryMapper;
import com.github_proxy.model.Repository;
import com.github_proxy.repository.RepoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepositoryService {
    private final GitHubClient gitHubClient;
    private final RepositoryMapper repositoryMapper;
    private final RepoRepository repoRepository;

    public RepositoryDto getRepository(String owner, String repo) {
        log.info("Fetching repository from GitHub: {}/{}", owner, repo);
        GitHubRepository repository = gitHubClient.getRepository(owner, repo);
        log.info("Successfully fetched repository from GitHub: {}/{}", owner, repo);
        return repositoryMapper.toDto(repository);
    }

    @Transactional
    public RepositoryDto saveRepository(String owner, String repo) {
        String fullName = buildFullName(owner, repo);
        log.info("Saving repository to local database: {}", fullName);
        if (repoRepository.existsByFullName(fullName)) {
            throw new LocalRepositoryAlreadyExistsException(fullName);
        }
        GitHubRepository repository = gitHubClient.getRepository(owner, repo);
        Repository entity  = repositoryMapper.toEntity(repository);
        Repository saved = repoRepository.save(entity);
        log.info("Repository successfully saved to local database: {}", fullName);
        return repositoryMapper.toDto(saved);
    }

    public RepositoryDto getLocalRepository(String owner, String repo) {
        log.info("Fetching repository from local database: {}/{}", owner, repo);
        Repository entity = findRepositoryOrThrow(owner, repo);
        log.info("Repository found in local database: {}", entity.getFullName());
        return repositoryMapper.toDto(entity);
    }

    @Transactional
    public RepositoryDto updateLocalRepository(String owner, String repo, UpdateRepositoryCommand command) {
        log.info("Updating repository in local database: {}/{}", owner, repo);
        Repository localRepo = findRepositoryOrThrow(owner, repo);
        localRepo.update(command);
        log.info("Repository successfully updated: {}", command.fullName());
        return repositoryMapper.toDto(localRepo);
    }

    @Transactional
    public void deleteLocalRepository(String owner, String repo) {
        log.info("Deleting repository from local database: {}/{}", owner, repo);
        Repository localRepo = findRepositoryOrThrow(owner, repo);
        repoRepository.delete(localRepo);
        log.info("Repository successfully deleted: {}", localRepo.getFullName());
    }

    private Repository findRepositoryOrThrow(String owner, String repo) {
        String fullName = buildFullName(owner, repo);
        return repoRepository.findByFullName(fullName).orElseThrow(() -> {
            log.warn("Repository not found in local database: {}", fullName);
            return new LocalRepositoryNotFoundException(fullName);
        });
    }

    private String buildFullName(String owner, String repo) {
        return owner + "/" + repo;
    }
}
