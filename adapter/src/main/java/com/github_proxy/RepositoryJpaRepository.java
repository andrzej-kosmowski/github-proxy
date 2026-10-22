package com.github_proxy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepositoryJpaRepository extends JpaRepository<RepositoryEntity, Long> {
    Optional<RepositoryEntity> findByFullName(String fullName);
    boolean existsByFullName(String fullName);
}
