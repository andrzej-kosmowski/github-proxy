package com.github_proxy.errorDecoder;

import com.github_proxy.exception.RepositoryNotFoundException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GitHubErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultDecoder = new Default();
    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();
        String url = response.request().url();
        log.error("API call failed: method={}, status={}, url={}", methodKey, status, url);
        return switch (status) {
            case 404 -> new RepositoryNotFoundException(extractRepoName(url));
            case 503 -> new RetryableException(
                    response.status(),
                    "API is temporarily unavailable",
                    response.request().httpMethod(),
                    (Long) null,
                    response.request()
            );
            default -> defaultDecoder.decode(methodKey, response);
        };
    }

    private String extractRepoName(String url) {
        String[] parts = url.split("/repos/");
        return parts.length > 1 ? parts[1] : "repository";
    }
}
