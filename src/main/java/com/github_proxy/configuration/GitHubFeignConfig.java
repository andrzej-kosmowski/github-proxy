package com.github_proxy.configuration;

import com.github_proxy.errorDecoder.GitHubErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GitHubFeignConfig {
    @Bean
    ErrorDecoder errorDecoder() {
        return new GitHubErrorDecoder();
    }
}
