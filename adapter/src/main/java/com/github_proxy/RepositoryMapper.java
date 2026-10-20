package com.github_proxy;

import com.github_proxy.dto.GithubRepositoryResponse;
import com.github_proxy.dto.RepositoryDto;
import com.github_proxy.model.Repository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RepositoryMapper {
    @Mapping(target = "id", ignore = true)
    Repository toDomain(GithubRepositoryResponse response);

    Repository toDomain(RepositoryEntity entity);
    RepositoryEntity toEntity(Repository repository);
    RepositoryDto toDto(Repository repository);
}
