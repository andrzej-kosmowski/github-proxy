package com.github_proxy.in;

import com.github_proxy.RepositoryService;
import com.github_proxy.dto.RepositoryDto;
import com.github_proxy.RepositoryMapper;
import com.github_proxy.port.in.UpdateRepositoryCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class RepositoryController {
    private final RepositoryService repositoryService;
    private final RepositoryMapper mapper;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/local/repositories/{owner}/{repositoryName}")
    public RepositoryDto getLocalRepository(@PathVariable String owner, @PathVariable String repositoryName) {
        return mapper.toDto(repositoryService.getLocalRepository(owner, repositoryName));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/repositories/{owner}/{repositoryName}")
    public RepositoryDto getRepository(@PathVariable String owner, @PathVariable String repositoryName) {
        return mapper.toDto(repositoryService.getRepository(owner, repositoryName));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/repositories/{owner}/{repositoryName}")
    @ResponseStatus(HttpStatus.CREATED)
    public RepositoryDto saveRepository(@PathVariable String owner, @PathVariable String repositoryName) {
        return mapper.toDto(repositoryService.saveRepository(owner, repositoryName));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/repositories/{owner}/{repositoryName}")
    public RepositoryDto updateRepository(@PathVariable String owner, @PathVariable String repositoryName,
                                          @RequestBody UpdateRepositoryCommand command) {
        return mapper.toDto(repositoryService.updateLocalRepository(owner, repositoryName, command));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/repositories/{owner}/{repositoryName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRepository(@PathVariable String owner, @PathVariable String repositoryName) {
        repositoryService.deleteLocalRepository(owner, repositoryName);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> myInfo(
            @AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> info = Map.of(
                "userId", Objects.requireNonNull(jwt.getSubject()),
                "username", Objects.requireNonNull(jwt.getClaimAsString("preferred_username")),
                "email", Objects.requireNonNull(jwt.getClaimAsString("email")),
                "roles", Objects.requireNonNull(jwt.getClaimAsMap("realm_access"))
        );
        return ResponseEntity.ok(info);
    }

}
