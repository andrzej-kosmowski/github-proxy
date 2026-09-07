package com.github_proxy.mapper;

import com.github_proxy.dto.GitHubRepository;
import com.github_proxy.dto.RepositoryDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RepositoryMapper {
    RepositoryDto toDto(GitHubRepository repository);
}
