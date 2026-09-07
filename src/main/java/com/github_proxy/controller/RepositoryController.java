package com.github_proxy.controller;

import com.github_proxy.dto.RepositoryDto;
import com.github_proxy.service.RepositoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/repositories")
@RequiredArgsConstructor
public class RepositoryController {
    private final RepositoryService repositoryService;

    @GetMapping("/{owner}/{repositoryName}")
    public RepositoryDto getRepository(
            @PathVariable("owner") String owner, @PathVariable("repositoryName") String repo) {
        return repositoryService.getRepository(owner, repo);
    }
}
