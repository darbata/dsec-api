package io.darbata.basecampapi.github;

import io.darbata.basecampapi.github.internal.*;
import io.darbata.basecampapi.github.internal.model.GithubToken;
import org.springframework.stereotype.Service;

import java.util.List;

// manages token handling
@Service
class GithubAPIService {
    private final GithubApiClient client;

    public GithubAPIService(GithubApiClient client) {
        this.client = client;
    }

    public List<GithubRepository> fetchUserRepositories(GithubToken token) {
        return client.fetchUserRepositories(
                "Bearer " + token.accessToken(),
                "owner",
                100,
                0
        );
    }

    public GithubRepository fetchGithubRepositoryById(GithubToken token, long githubRepositoryId) {
        GithubRepository repo = client.getRepository(token.accessToken(), githubRepositoryId);
        return new GithubRepository(repo.id(), repo.name(), repo.url(), repo.language(), repo.openTickets(),
                repo.stars(), repo.pushedAt());
    }
}


