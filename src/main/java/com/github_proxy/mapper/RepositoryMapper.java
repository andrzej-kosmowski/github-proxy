package com.github_proxy.mapper;

import com.github_proxy.dto.GitHubRepository;
import com.github_proxy.dto.RepositoryDto;
import com.github_proxy.model.Repository;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RepositoryMapper {
    RepositoryDto toDto(GitHubRepository repository);
    RepositoryDto toDto(Repository repository);
    Repository toEntity(GitHubRepository repository);
}
