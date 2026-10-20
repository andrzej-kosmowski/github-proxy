package com.github_proxy.model;

import com.github_proxy.exception.InvalidRepositoryDataException;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Repository {
    private Long id;
    private String fullName;
    private String description;
    private String cloneUrl;
    private Integer stars;
    private LocalDateTime createdAt;

    public Repository(Long id, String fullName, String description, String cloneUrl, int stars, LocalDateTime createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.description = description;
        this.cloneUrl = cloneUrl;
        this.stars = stars;
        this.createdAt = createdAt;
    }

    public void update(String fullName, String description, String cloneUrl, Integer stars) {
        this.fullName = fullName;
        this.description = description;
        this.cloneUrl = cloneUrl;
        this.stars = stars;
        this.validate();
    }

    private void validate() {
        if (fullName == null || fullName.isBlank()) {
            throw new InvalidRepositoryDataException("com.github_proxy.model.Repository full name cannot be empty");
        }
        if (description == null || description.isBlank()) {
            throw new InvalidRepositoryDataException("com.github_proxy.model.Repository description cannot be empty");
        }
        if (cloneUrl == null || cloneUrl.isBlank()) {
            throw new InvalidRepositoryDataException("com.github_proxy.model.Repository clone URL cannot be empty");
        }
        if (stars < 0) {
            throw new InvalidRepositoryDataException("Stars cannot be negative");
        }
    }
}
