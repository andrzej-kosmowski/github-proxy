package com.github_proxy.port.out;

import com.github_proxy.model.Repository;

import java.util.Optional;

public interface RepositoryProviderPort {
    boolean existsByFullName(String fullName);
    Repository save(Repository repository);
    void delete(Repository repository);
    Optional<Repository> findByFullName(String fullName);
}
