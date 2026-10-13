package com.github_proxy.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github_proxy.dto.GitHubRepository;
import com.github_proxy.exception.GitHubServiceUnavailableException;
import com.github_proxy.exception.RepositoryNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@EnableWireMock
public class GitHubClientTest {
    @Autowired
    GitHubClient gitHubClient;

    @InjectWireMock
    WireMockServer wireMockServer;

    @Test
    void getRepository_GitHubReturns200_ReturnsRepository() {
        // given
        wireMockServer.stubFor(get("/repos/microsoft/vscode")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("response.json")));
        // when
        GitHubRepository result = gitHubClient.getRepository("microsoft", "vscode");
        // then
        assertAll(
                () -> assertEquals("microsoft/vscode", result.fullName()),
                () -> assertEquals("Code editor", result.description()),
                () -> assertEquals("https://github.com/microsoft/vscode.git", result.cloneUrl()),
                () -> assertEquals(200, result.stars())
        );
        wireMockServer.verify(1, getRequestedFor(urlEqualTo("/repos/microsoft/vscode")));
    }

    @Test
    void getRepository_GitHubReturns404_ThrowsRepositoryNotFoundException() {
        // given
        wireMockServer.stubFor(get("/repos/microsoft/does-not-exist").willReturn(aResponse().withStatus(404)));
        // when & then
        RepositoryNotFoundException exception = assertThrows(RepositoryNotFoundException.class,
                () -> gitHubClient.getRepository("microsoft", "does-not-exist"));
        assertEquals("Repository microsoft/does-not-exist not found on GitHub", exception.getMessage());
        wireMockServer.verify(1, getRequestedFor(urlEqualTo("/repos/microsoft/does-not-exist")));
    }

    @Test
    void getRepository_GitHubReturns503Twice_RequestIsRetried() {
        // given
        wireMockServer.stubFor(get("/repos/microsoft/vscode")
                .inScenario("retry")
                .whenScenarioStateIs(STARTED)
                .willReturn(aResponse().withStatus(503))
                .willSetStateTo("second"));
        wireMockServer.stubFor(get("/repos/microsoft/vscode")
                .inScenario("retry")
                .whenScenarioStateIs("second")
                .willReturn(aResponse().withStatus(503))
                .willSetStateTo("third"));
        wireMockServer.stubFor(get("/repos/microsoft/vscode")
                .inScenario("retry")
                .whenScenarioStateIs("third")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("response.json")));
        // when
        GitHubRepository result = gitHubClient.getRepository("microsoft", "vscode");
        // then
        assertAll(
                () -> assertEquals("microsoft/vscode", result.fullName()),
                () -> assertEquals("Code editor", result.description()),
                () -> assertEquals("https://github.com/microsoft/vscode.git", result.cloneUrl()),
                () -> assertEquals(200, result.stars())
        );
        wireMockServer.verify(3, getRequestedFor(urlEqualTo("/repos/microsoft/vscode")));
    }

    @Test
    void getRepository_GitHubReturns503_UsesFallbackFactory() {
        // given
        wireMockServer.stubFor(get("/repos/microsoft/vscode")
                .willReturn(aResponse().withStatus(503)));
        // when & then
        assertThrows(GitHubServiceUnavailableException.class,
                () -> gitHubClient.getRepository("microsoft", "vscode"));
        wireMockServer.verify(4, getRequestedFor(urlEqualTo("/repos/microsoft/vscode")));
    }
}
