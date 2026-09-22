package com.github_proxy.configuration;

import com.github_proxy.errorDecoder.GitHubErrorDecoder;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GitHubFeignConfig {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new GitHubErrorDecoder();
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default();
    }
}
