package com.github_proxy;

import com.github_proxy.exception.LocalRepositoryAlreadyExistsException;
import com.github_proxy.exception.LocalRepositoryNotFoundException;
import com.github_proxy.model.Repository;
import com.github_proxy.port.out.GithubClientPort;
import com.github_proxy.port.out.RepositoryProviderPort;
import com.github_proxy.port.in.UpdateRepositoryCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class RepositoryService {
    private final RepositoryProviderPort repositoryProviderPort;
    private final GithubClientPort githubClientPort;

    public Repository getRepository(String owner, String repo) {
        log.info("Fetching repository from GitHub: {}/{}", owner, repo);
        Repository repository = githubClientPort.getRepository(owner, repo);
        log.info("Successfully fetched repository from GitHub: {}/{}", owner, repo);
        return repository;
    }

    public Repository saveRepository(String owner, String repo) {
        String fullName = buildFullName(owner, repo);
        log.info("Saving repository to local database: {}", fullName);
        if (repositoryProviderPort.existsByFullName(fullName)) {
            throw new LocalRepositoryAlreadyExistsException(fullName);
        }
        Repository repository = githubClientPort.getRepository(owner, repo);
        Repository saved = repositoryProviderPort.save(repository);
        log.info("Repository successfully saved to local database: {}", fullName);
        return saved;
    }

    public Repository getLocalRepository(String owner, String repo) {
        log.info("Fetching repository from local database: {}/{}", owner, repo);
        Repository entity = findRepositoryOrThrow(owner, repo);
        log.info("Repository found in local database: {}", entity.getFullName());
        return entity;
    }

    public Repository updateLocalRepository(String owner, String repo, UpdateRepositoryCommand command) {
        log.info("Updating repository in local database: {}/{}", owner, repo);
        Repository repository = findRepositoryOrThrow(owner, repo);
        repository.update(command.fullName(), command.description(), command.cloneUrl(),  command.stars());
        log.info("Repository successfully updated: {}", command.fullName());
        return repositoryProviderPort.save(repository);
    }

    public void deleteLocalRepository(String owner, String repo) {
        log.info("Deleting repository from local database: {}/{}", owner, repo);
        Repository localRepo = findRepositoryOrThrow(owner, repo);
        repositoryProviderPort.delete(localRepo);
        log.info("Repository successfully deleted: {}", localRepo.getFullName());
    }

    private Repository findRepositoryOrThrow(String owner, String repo) {
        String fullName = buildFullName(owner, repo);
        return repositoryProviderPort.findByFullName(fullName).orElseThrow(() -> {
            log.warn("Repository not found in local database: {}", fullName);
            return new LocalRepositoryNotFoundException(fullName);
        });
    }

    private String buildFullName(String owner, String repo) {
        return owner + "/" + repo;
    }
}
