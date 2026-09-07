package com.github_proxy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record GitHubRepository(
        @JsonProperty("full_name")
        String fullName,
        String description,
        @JsonProperty("clone_url")
        String cloneUrl,
        @JsonProperty("stargazers_count")
        int stars,
        @JsonProperty("created_at")
        LocalDateTime createdAt
) {
}
