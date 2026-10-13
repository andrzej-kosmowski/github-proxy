package com.github_proxy.service;

import com.github_proxy.client.GitHubClient;
import com.github_proxy.dto.GitHubRepository;
import com.github_proxy.dto.RepositoryDto;
import com.github_proxy.dto.UpdateRepositoryCommand;
import com.github_proxy.exception.LocalRepositoryAlreadyExistsException;
import com.github_proxy.exception.LocalRepositoryNotFoundException;
import com.github_proxy.mapper.RepositoryMapper;
import com.github_proxy.model.Repository;
import com.github_proxy.repository.RepoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RepositoryServiceTest {
    RepositoryService repositoryService;
    GitHubClient gitHubClient;
    RepoRepository repoRepository;
    RepositoryMapper repositoryMapper;

    @BeforeEach
    public void setup() {
        this.gitHubClient = Mockito.mock(GitHubClient.class);
        this.repoRepository = Mockito.mock(RepoRepository.class);
        this.repositoryMapper = Mappers.getMapper(RepositoryMapper.class);
        this.repositoryService = new RepositoryService(gitHubClient, repositoryMapper, repoRepository);
    }

    @Test
    void getRepository_RepositoryExistsOnGitHub_RepositoryReturned() {
        //given
        GitHubRepository repository = new GitHubRepository(
                "microsoft/vscode",
                "Visual Studio Code",
                "https://github.com/microsoft/vscode.git",
                100,
                LocalDateTime.of(2020, 1, 1, 12, 0)
        );
        when(gitHubClient.getRepository("microsoft", "vscode")).thenReturn(repository);
        // when
        RepositoryDto result = repositoryService.getRepository("microsoft", "vscode");
        // then
        assertAll(
                () -> assertEquals("microsoft/vscode", result.fullName()),
                () -> assertEquals("Visual Studio Code", result.description()),
                () -> assertEquals("https://github.com/microsoft/vscode.git", result.cloneUrl()),
                () -> assertEquals(100, result.stars()),
                () -> assertEquals(LocalDateTime.of(2020, 1, 1, 12, 0), result.createdAt()));
        verify(gitHubClient).getRepository("microsoft", "vscode");
    }

    @Test
    void saveRepository_RepositoryDoesNotExistLocally_RepositorySaved() {
        // given
        GitHubRepository repository = new GitHubRepository(
                "microsoft/vscode",
                "Visual Studio Code",
                "https://github.com/microsoft/vscode.git",
                100,
                LocalDateTime.of(2020, 1, 1, 12, 0));
        when(repoRepository.existsByFullName("microsoft/vscode")).thenReturn(false);
        when(gitHubClient.getRepository("microsoft", "vscode")).thenReturn(repository);
        when(repoRepository.save(any(Repository.class))) .thenAnswer(i -> i.getArgument(0));
        // when
        RepositoryDto result = repositoryService.saveRepository("microsoft", "vscode");
        // then
        assertAll(
                () -> assertEquals("microsoft/vscode", result.fullName()),
                () -> assertEquals("Visual Studio Code", result.description()),
                () -> assertEquals("https://github.com/microsoft/vscode.git", result.cloneUrl()),
                () -> assertEquals(100, result.stars()),
                () -> assertEquals(LocalDateTime.of(2020, 1, 1, 12, 0), result.createdAt())
        );
        verify(repoRepository).existsByFullName("microsoft/vscode");
        verify(gitHubClient).getRepository("microsoft", "vscode");
        verify(repoRepository).save(any(Repository.class));
    }

    @Test void saveRepository_RepositoryAlreadyExists_ThrowsException() {
        // given
        String fullName = "microsoft/vscode";
        when(repoRepository.existsByFullName(fullName)).thenReturn(true);
        // when
        LocalRepositoryAlreadyExistsException exception = assertThrows(LocalRepositoryAlreadyExistsException.class,
                () -> repositoryService.saveRepository("microsoft", "vscode"));
        // then
        assertAll(
                () -> assertEquals("Repository microsoft/vscode already exists", exception.getMessage()),
                () -> assertEquals(HttpStatus.CONFLICT, exception.getStatus())
        );
        verify(repoRepository).existsByFullName(fullName);
    }

    @Test void getLocalRepository_RepositoryExists_RepositoryReturned() {
        // given
        Repository repository = new Repository(
                "microsoft/vscode",
                "Visual Studio Code",
                "https://github.com/microsoft/vscode.git",
                100,
                LocalDateTime.of(2020, 1, 1, 12, 0));
        when(repoRepository.findByFullName("microsoft/vscode")).thenReturn(Optional.of(repository));
        // when
        RepositoryDto result = repositoryService.getLocalRepository("microsoft", "vscode");
        // then
        assertAll(
                () -> assertEquals("microsoft/vscode", result.fullName()),
                () -> assertEquals("Visual Studio Code", result.description()),
                () -> assertEquals("https://github.com/microsoft/vscode.git", result.cloneUrl()),
                () -> assertEquals(100, result.stars()),
                () -> assertEquals(LocalDateTime.of(2020, 1, 1, 12, 0), result.createdAt())
        );
        verify(repoRepository).findByFullName("microsoft/vscode");
    }

    @Test void getLocalRepository_RepositoryDoesNotExist_ThrowsException() {
        // given
        when(repoRepository.findByFullName("microsoft/vscode")).thenReturn(Optional.empty());
        // when
        LocalRepositoryNotFoundException exception = assertThrows(LocalRepositoryNotFoundException.class,
                () -> repositoryService.getLocalRepository("microsoft", "vscode"));
        // then
        assertAll(
                () -> assertEquals("Repository microsoft/vscode not found in local database", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
        verify(repoRepository).findByFullName("microsoft/vscode");
    }

    @Test void updateLocalRepository_RepositoryExists_RepositoryUpdated() {
        // given
        Repository repository = new Repository(
                "microsoft/vscode",
                "Old description",
                "https://old-url.com",
                100,
                LocalDateTime.of(2020, 1, 1, 12, 0));
        UpdateRepositoryCommand command = new UpdateRepositoryCommand("microsoft/vscode",
                "New description", "https://new-url.com", 200);
        when(repoRepository.findByFullName("microsoft/vscode")).thenReturn(Optional.of(repository));
        // when
        RepositoryDto result = repositoryService.updateLocalRepository("microsoft", "vscode", command);
        // then
        assertAll(
                () -> assertEquals("microsoft/vscode", result.fullName()),
                () -> assertEquals("New description", result.description()),
                () -> assertEquals("https://new-url.com", result.cloneUrl()),
                () -> assertEquals(200, result.stars()),
                () -> assertEquals(LocalDateTime.of(2020, 1, 1, 12, 0), result.createdAt())
        );
        verify(repoRepository).findByFullName("microsoft/vscode");
    }

    @Test void updateLocalRepository_RepositoryDoesNotExist_ThrowsException() {
        // given
        UpdateRepositoryCommand command = new UpdateRepositoryCommand(
                "microsoft/vscode",
                "New description",
                "https://new-url.com",
                200);
        when(repoRepository.findByFullName("microsoft/vscode")).thenReturn(Optional.empty());
        // when
        LocalRepositoryNotFoundException exception = assertThrows(LocalRepositoryNotFoundException.class,
                () -> repositoryService.updateLocalRepository("microsoft", "vscode", command));
        // then
        assertAll(
                () -> assertEquals("Repository microsoft/vscode not found in local database", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
        verify(repoRepository).findByFullName("microsoft/vscode");
    }

    @Test void deleteLocalRepository_RepositoryExists_RepositoryDeleted() {
        // given
        Repository repository = new Repository(
                "microsoft/vscode",
                "Visual Studio Code",
                "https://github.com/microsoft/vscode.git",
                100, LocalDateTime.of(2020, 1, 1, 12, 0));
        when(repoRepository.findByFullName("microsoft/vscode")).thenReturn(Optional.of(repository));
        // when
        repositoryService.deleteLocalRepository("microsoft", "vscode");
        // then
        verify(repoRepository).findByFullName("microsoft/vscode");
        verify(repoRepository).delete(repository);
    }

    @Test void deleteLocalRepository_RepositoryDoesNotExist_ThrowsException() {
        // given
        when(repoRepository.findByFullName("microsoft/vscode")).thenReturn(Optional.empty());
        // when
        LocalRepositoryNotFoundException exception = assertThrows(LocalRepositoryNotFoundException.class,
                () -> repositoryService.deleteLocalRepository("microsoft", "vscode"));
        // then
        assertAll(
                () -> assertEquals("Repository microsoft/vscode not found in local database", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
        verify(repoRepository).findByFullName("microsoft/vscode");
    }
}
