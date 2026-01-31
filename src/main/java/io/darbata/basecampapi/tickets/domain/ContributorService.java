package io.darbata.basecampapi.tickets.domain;

import io.darbata.basecampapi.github.GithubService;

public class ContributorService {

    private final GithubService githubService;
    private final ContributorRepository contributorRepository;

    public ContributorService(GithubService githubService, ContributorRepository contributorRepository) {
        this.githubService = githubService;
        this.contributorRepository = contributorRepository;
    }
    public void handleAssignment(String userId) {
        return;
    }
}