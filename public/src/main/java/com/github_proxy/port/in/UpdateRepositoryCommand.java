package com.github_proxy.port.in;

public record UpdateRepositoryCommand(
        String fullName,
        String description,
        String cloneUrl,
        Integer stars
) {
}
