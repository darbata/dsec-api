package io.darbata.basecampapi.github;

import io.darbata.basecampapi.github.internal.model.GithubToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class GithubService {

    private final GithubRepoRepository repository;
    private final GithubAPIService githubAPIService;
    private final GithubTokenService tokenService;

    public GithubService(GithubRepoRepository repository, GithubAPIService githubAPIService, GithubTokenService tokenService) {
        this.repository = repository;
        this.githubAPIService = githubAPIService;
        this.tokenService = tokenService;
    }

    public List<GithubRepository> fetchUserGithubRepositories(String callerId) {
        // callerId is the id of the current auth user whose token we will be using
        GithubToken token = tokenService.getUserToken(callerId);
        return githubAPIService.fetchUserRepositories(token);
    }

    public GithubRepository findById(String callerId, long id) {
        // callerId is the id of the current auth user whose token we will be using
        return repository.findById(id)
                .orElseGet(() -> fetchGithubRepository(callerId, id));
    }

    public void delete(long repoId) {
        repository.delete(repoId);
    }

    public void revokeToken(String userId) {
        tokenService.revokeUserToken(userId);
    }

    public boolean validateUserToken(String userId) {
        return tokenService.getUserToken(userId) != null;
    }

    private GithubRepository fetchGithubRepository(String callerId, long id) {
        // callerId is the id of the current auth user whose token we will be using
        GithubToken token = tokenService.getUserToken(callerId);
        GithubRepository repo = githubAPIService.fetchGithubRepositoryById(token, id);
        repository.save(repo);
        return repository.findById(id).orElseThrow();
    }


}