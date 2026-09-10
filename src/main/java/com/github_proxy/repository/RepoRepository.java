package com.github_proxy.repository;

import com.github_proxy.model.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepoRepository extends JpaRepository<Repository, Long> {
    Optional<Repository> findByFullName(String fullName);
    boolean existsByFullName(String fullName);
}
