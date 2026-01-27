package io.darbata.basecampapi.github;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class GithubService {

    private final GithubRepoRepository repository;
    private final GithubAPIService githubAPIService;

    public GithubService(GithubRepoRepository repository, GithubAPIService githubAPIService) {
        this.repository = repository;
        this.githubAPIService = githubAPIService;
    }

    public boolean isGithubConnected(String userId) {
        return githubAPIService.isGithubConnected(userId);
    }

    public void disconnectGithub(String userId) {
        githubAPIService.disconnectGithub(userId);
    }

    public List<GithubRepository> fetchUserGithubRepositories(String userId) {
        return githubAPIService.fetchUserRepositories(userId);
    }

    public GithubRepository fetchGithubRepository(String userId, long id) {
        GithubRepository repo = githubAPIService.fetchGithubRepositoryById(userId, id);
        repository.save(repo);
        return repository.findById(id).orElseThrow();
    }

    public GithubRepository findById(String userId, long id) {
        return repository.findById(id)
                .orElseGet(() -> fetchGithubRepository(userId, id));
    }

    public List<GithubRepository> findAllCommunity(int pageNum, int pageSize) {
        return repository.findAllCommunity(pageNum, pageSize);
    }

    public List<GithubRepository> findAllFeatured(int pageNum, int pageSize) {
        return repository.findAllFeatured(pageNum, pageSize);
    }

    public void delete(long repoId) {
        repository.delete(repoId);
    }

}