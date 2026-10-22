package com.github_proxy;

import com.github_proxy.out.jpa.JpaRepositoryAdapter;
import com.github_proxy.out.github.GithubAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GithubProxyConfiguration {

    @Bean
    RepositoryService repositoryService(JpaRepositoryAdapter jpaRepositoryAdapter, GithubAdapter githubAdapter) {
        return new RepositoryService(jpaRepositoryAdapter, githubAdapter);
    }
}
