package com.github_proxy.out.jpa;

import com.github_proxy.RepositoryEntity;
import com.github_proxy.RepositoryJpaRepository;
import com.github_proxy.RepositoryMapper;
import com.github_proxy.model.Repository;
import com.github_proxy.port.out.RepositoryProviderPort;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@org.springframework.stereotype.Repository
@RequiredArgsConstructor
public class JpaRepositoryAdapter implements RepositoryProviderPort {
    private final RepositoryJpaRepository jpaRepository;
    private final RepositoryMapper mapper;

    @Override
    public boolean existsByFullName(String fullName) {
        return jpaRepository.existsByFullName(fullName);
    }

    @Override
    public Repository save(Repository repository) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(repository)));
    }

    @Override
    public void delete(Repository repository) {
        RepositoryEntity entity = mapper.toEntity(repository);
        jpaRepository.delete(entity);
    }

    @Override
    public Optional<Repository> findByFullName(String fullName) {
        return jpaRepository.findByFullName(fullName).map(mapper::toDomain);
    }
}
