package com.github_proxy.controller;

import com.github_proxy.dto.RepositoryDto;
import com.github_proxy.dto.UpdateRepositoryCommand;
import com.github_proxy.service.RepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RepositoryControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    RepositoryService repositoryService;

    RepositoryDto repositoryDto;
    @BeforeEach
    void setUp() {
        repositoryDto = new RepositoryDto(
                "microsoft/vscode",
                "Visual Studio Code",
                "https://github.com/microsoft/vscode.git",
                100,
                LocalDateTime.of(2020, 1, 1, 12, 0));
    }

    @Test
    void getLocalRepository_RepositoryExists_ReturnsRepository() throws Exception {
        // given
        when(repositoryService.getLocalRepository("microsoft", "vscode")).thenReturn(repositoryDto);
        // when & then
        mockMvc.perform(get("/local/repositories/microsoft/vscode"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("microsoft/vscode"))
                .andExpect(jsonPath("$.description").value("Visual Studio Code"))
                .andExpect(jsonPath("$.cloneUrl").value("https://github.com/microsoft/vscode.git"))
                .andExpect(jsonPath("$.stars").value(100))
                .andExpect(jsonPath("$.createdAt").value("2020-01-01T12:00:00"));
        verify(repositoryService).getLocalRepository("microsoft", "vscode");
    }

    @Test
    void getRepository_RepositoryExistsOnGitHub_ReturnsRepository() throws Exception {
        // given
        when(repositoryService.getRepository("microsoft", "vscode")).thenReturn(repositoryDto);
        // when & then
        mockMvc.perform(get("/repositories/microsoft/vscode"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("microsoft/vscode"))
                .andExpect(jsonPath("$.description").value("Visual Studio Code"))
                .andExpect(jsonPath("$.cloneUrl").value("https://github.com/microsoft/vscode.git"))
                .andExpect(jsonPath("$.stars").value(100))
                .andExpect(jsonPath("$.createdAt").value("2020-01-01T12:00:00"));
        verify(repositoryService).getRepository("microsoft", "vscode");
    }

    @Test
    void saveRepository_RepositorySaved_ReturnsCreated() throws Exception {
        // given
        when(repositoryService.saveRepository("microsoft", "vscode")).thenReturn(repositoryDto);
        // when & then
        mockMvc.perform(post("/repositories/microsoft/vscode"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("microsoft/vscode"))
                .andExpect(jsonPath("$.description").value("Visual Studio Code"))
                .andExpect(jsonPath("$.cloneUrl").value("https://github.com/microsoft/vscode.git"))
                .andExpect(jsonPath("$.stars").value(100))
                .andExpect(jsonPath("$.createdAt").value("2020-01-01T12:00:00"));
        verify(repositoryService).saveRepository("microsoft", "vscode");
    }

    @Test
    void updateRepository_RepositoryUpdated_ReturnsOk() throws Exception {
        // given
        UpdateRepositoryCommand command = new UpdateRepositoryCommand(
                "microsoft/vscode",
                "New description",
                "https://new-url.com",
                200);
        RepositoryDto updatedRepository = new RepositoryDto(
                "microsoft/vscode",
                "New description",
                "https://new-url.com",
                200,
                LocalDateTime.of(2020, 1, 1, 12, 0));
        when(repositoryService.updateLocalRepository("microsoft", "vscode", command)).thenReturn(updatedRepository);
        // when & then
        mockMvc.perform(put("/repositories/microsoft/vscode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("microsoft/vscode"))
                .andExpect(jsonPath("$.description").value("New description"))
                .andExpect(jsonPath("$.cloneUrl").value("https://new-url.com"))
                .andExpect(jsonPath("$.stars").value(200))
                .andExpect(jsonPath("$.createdAt").value("2020-01-01T12:00:00"));
        verify(repositoryService).updateLocalRepository("microsoft", "vscode", command);
    }

    @Test
    void deleteRepository_RepositoryExists_ReturnsNoContent() throws Exception {
        // when & then
        mockMvc.perform(delete("/repositories/microsoft/vscode"))
                .andExpect(status().isNoContent());
        verify(repositoryService).deleteLocalRepository("microsoft", "vscode");
    }
}