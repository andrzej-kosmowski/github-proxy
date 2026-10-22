package com.github_proxy.port.out;

import com.github_proxy.model.Repository;

public interface GithubClientPort {
    Repository getRepository(String owner, String repo);

}
