package com.github_proxy.dto;

import java.time.LocalDateTime;

public record ErrorMessageDto(
        LocalDateTime timestamp,
        int status,
        String error,
        String message
) {
}
