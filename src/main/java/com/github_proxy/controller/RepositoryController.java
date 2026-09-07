package com.github_proxy.controller;

import com.github_proxy.dto.RepositoryDto;
import com.github_proxy.dto.UpdateRepositoryCommand;
import com.github_proxy.service.RepositoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RepositoryController {
    private final RepositoryService repositoryService;

    @GetMapping("/local/repositories/{owner}/{repositoryName}")
    public RepositoryDto getLocalRepository(@PathVariable String owner, @PathVariable String repositoryName) {
        return repositoryService.getLocalRepository(owner, repositoryName);
    }

    @GetMapping("/repositories/{owner}/{repositoryName}")
    public RepositoryDto getRepository(@PathVariable String owner, @PathVariable String repositoryName) {
        return repositoryService.getRepository(owner, repositoryName);
    }

    @PostMapping("/repositories/{owner}/{repositoryName}")
    @ResponseStatus(HttpStatus.CREATED)
    public RepositoryDto saveRepository(@PathVariable String owner, @PathVariable String repositoryName) {
        return repositoryService.saveRepository(owner, repositoryName);
    }

    @PutMapping("/repositories/{owner}/{repositoryName}")
    public RepositoryDto updateRepository(@PathVariable String owner, @PathVariable String repositoryName,
                                          @RequestBody UpdateRepositoryCommand command) {
        return repositoryService.updateLocalRepository(owner, repositoryName, command);
    }
}
