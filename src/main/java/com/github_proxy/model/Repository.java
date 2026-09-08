package com.github_proxy.model;

import com.github_proxy.dto.UpdateRepositoryCommand;
import com.github_proxy.exception.InvalidRepositoryDataException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "repositories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Repository {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String description;
    private String cloneUrl;
    private Integer stars;
    private LocalDateTime createdAt;

    public Repository(String fullName, String description, String cloneUrl, int stars, LocalDateTime createdAt) {
        this.fullName = fullName;
        this.description = description;
        this.cloneUrl = cloneUrl;
        this.stars = stars;
        this.createdAt = createdAt;
    }

    public void update(UpdateRepositoryCommand command) {
        this.fullName = command.fullName();
        this.description = command.description();
        this.cloneUrl = command.cloneUrl();
        this.stars = command.stars();
        this.validate();
    }

    private void validate() {
        if (fullName == null || fullName.isBlank()) {
            throw new InvalidRepositoryDataException("Repository full name cannot be empty");
        }
        if (description == null || description.isBlank()) {
            throw new InvalidRepositoryDataException("Repository description cannot be empty");
        }
        if (cloneUrl == null || cloneUrl.isBlank()) {
            throw new InvalidRepositoryDataException("Repository clone URL cannot be empty");
        }
        if (stars < 0) {
            throw new InvalidRepositoryDataException("Stars cannot be negative");
        }
    }
}
