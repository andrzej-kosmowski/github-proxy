package com.github_proxy.dto;

public record UpdateRepositoryCommand(
        String fullName,
        String description,
        String cloneUrl,
        int stars
) {
}
