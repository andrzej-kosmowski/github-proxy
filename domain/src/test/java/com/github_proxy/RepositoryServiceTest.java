package com.github_proxy;

import com.github_proxy.exception.InvalidRepositoryDataException;
import com.github_proxy.exception.LocalRepositoryAlreadyExistsException;
import com.github_proxy.exception.LocalRepositoryNotFoundException;
import com.github_proxy.model.Repository;
import com.github_proxy.port.in.UpdateRepositoryCommand;
import com.github_proxy.port.out.GithubClientPort;
import com.github_proxy.port.out.RepositoryProviderPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class RepositoryServiceTest {
    GithubClientPort githubClientPort;
    RepositoryProviderPort repositoryProviderPort;
    RepositoryService repositoryService;

    @BeforeEach
    void setUp() {
        githubClientPort = Mockito.mock(GithubClientPort.class);
        repositoryProviderPort = Mockito.mock(RepositoryProviderPort.class);
        repositoryService = new RepositoryService(repositoryProviderPort, githubClientPort);
    }

    private Repository repository() {
        return new Repository(1L, "microsoft/vscode", "Code editor",
                "https://github.com/microsoft/vscode.git", 100, LocalDateTime.of(2020, 1, 1, 12, 0));
    }

    @Test
    void getRepository_RepositoryExistsOnGitHub_ReturnsRepository() {
        // given
        when(githubClientPort.getRepository("microsoft", "vscode")).thenReturn(repository());
        // when
        Repository result = repositoryService.getRepository("microsoft", "vscode");
        // then
        assertEquals("microsoft/vscode", result.getFullName());
    }

    @Test
    void saveRepository_NewRepository_RepositorySaved() {
        // given
        Repository repository = repository();
        when(repositoryProviderPort.existsByFullName("microsoft/vscode")).thenReturn(false);
        when(githubClientPort.getRepository("microsoft", "vscode")).thenReturn(repository);
        when(repositoryProviderPort.save(repository)).thenReturn(repository);
        // when
        Repository result = repositoryService.saveRepository("microsoft", "vscode");
        // then
        assertEquals("microsoft/vscode", result.getFullName());
        verify(repositoryProviderPort).save(repository);
    }

    @Test
    void saveRepository_AlreadyExists_ThrowsException() {
        // given
        when(repositoryProviderPort.existsByFullName("microsoft/vscode")).thenReturn(true);
        // when
        LocalRepositoryAlreadyExistsException exception = assertThrows(
                LocalRepositoryAlreadyExistsException.class,
                () -> repositoryService.saveRepository("microsoft", "vscode"));
        // then
        assertEquals("Repository microsoft/vscode already exists", exception.getMessage());
        verifyNoInteractions(githubClientPort);
        verify(repositoryProviderPort, never()).save(any());
    }

    @Test
    void getLocalRepository_NotFound_ThrowsException() {
        // given
        when(repositoryProviderPort.findByFullName("microsoft/vscode")).thenReturn(Optional.empty());
        // when & then
        assertThrows(LocalRepositoryNotFoundException.class,
                () -> repositoryService.getLocalRepository("microsoft", "vscode"));
    }

    @Test
    void updateLocalRepository_ValidCommand_RepositoryUpdatedAndSaved() {
        // given
        Repository repository = repository();
        UpdateRepositoryCommand command = new UpdateRepositoryCommand(
                "microsoft/vscode", "New description", "https://new-url.com", 200);
        when(repositoryProviderPort.findByFullName("microsoft/vscode")).thenReturn(Optional.of(repository));
        when(repositoryProviderPort.save(repository)).thenReturn(repository);
        // when
        Repository result = repositoryService.updateLocalRepository("microsoft", "vscode", command);
        // then
        assertEquals("New description", result.getDescription());
        assertEquals(200, result.getStars());
        verify(repositoryProviderPort).save(repository);
    }

    @Test
    void updateLocalRepository_NegativeStars_ThrowsExceptionAndDoesNotSave() {
        // given
        UpdateRepositoryCommand command = new UpdateRepositoryCommand(
                "microsoft/vscode", "New description", "https://new-url.com", -1);
        when(repositoryProviderPort.findByFullName("microsoft/vscode")).thenReturn(Optional.of(repository()));
        // when & then
        assertThrows(InvalidRepositoryDataException.class,
                () -> repositoryService.updateLocalRepository("microsoft", "vscode", command));
        verify(repositoryProviderPort, never()).save(any());
    }

    @Test
    void deleteLocalRepository_RepositoryExists_RepositoryDeleted() {
        // given
        Repository repository = repository();
        when(repositoryProviderPort.findByFullName("microsoft/vscode")).thenReturn(Optional.of(repository));
        // when
        repositoryService.deleteLocalRepository("microsoft", "vscode");
        // then
        verify(repositoryProviderPort).delete(repository);
    }

}
