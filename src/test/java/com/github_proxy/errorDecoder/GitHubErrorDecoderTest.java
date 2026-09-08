package com.github_proxy.errorDecoder;

import com.github_proxy.exception.RepositoryNotFoundException;
import feign.FeignException;
import feign.Request;
import feign.Response;
import feign.RetryableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GitHubErrorDecoderTest {
    GitHubErrorDecoder gitHubErrorDecoder;

    @BeforeEach
    void setUp() {
        gitHubErrorDecoder = new GitHubErrorDecoder();
    }

    @Test
    void decode_Status404_ReturnRepositoryNotFoundException() {
        // given
        Response response = createResponse(404);
        // when
        Exception result = gitHubErrorDecoder.decode("GitHubClient#getRepository", response);
        // then
        assertAll(
                () -> assertInstanceOf(RepositoryNotFoundException.class, result),
                () -> assertEquals(404, response.status()),
                () -> assertEquals("Repository microsoft/vscode not found on GitHub", result.getMessage())
        );
    }

    @Test
    void decode_Status503_ReturnRetryableException() {
        // given
        Response response = createResponse(503);
        // when
        Exception result = gitHubErrorDecoder.decode("GitHubClient#getRepository", response);
        // then
        assertAll(
                () -> assertInstanceOf(RetryableException.class, result),
                () -> assertEquals(503, response.status()),
                () -> assertEquals("API is temporarily unavailable", result.getMessage())
        );
    }

    @Test
    void decode_Status500_ReturnDefaultFeignException() {
        // given
        Response response = createResponse(500);
        // when
        Exception result = gitHubErrorDecoder.decode("GitHubClient#getRepository", response);
        // then
        assertAll(
                () -> assertInstanceOf(FeignException.class, result),
                () -> assertNotNull(result.getMessage())
        );
    }

    private Response createResponse(int status) {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "https://api.github.com/repos/microsoft/vscode",
                Map.of(),
                null,
                null,
                null
        );
        return Response.builder()
                .status(status)
                .reason("Test")
                .request(request)
                .build();
    }
}