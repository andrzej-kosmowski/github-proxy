package com.github_proxy.dto;

import java.time.LocalDateTime;

public record RepositoryDto(
        String fullName,
        String description,
        String cloneUrl,
        int stars,
        LocalDateTime createdAt
) {
}
