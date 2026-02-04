package io.darbata.basecampapi.github;

import io.darbata.basecampapi.github.internal.InstallationAccessToken;
import io.darbata.basecampapi.github.internal.IssueComment;
import io.darbata.basecampapi.github.internal.dto.githubproject.GithubGraphQLResponse;
import io.darbata.basecampapi.github.internal.dto.githubproject.ProjectV2;
import io.darbata.basecampapi.github.internal.model.GithubToken;
import io.darbata.basecampapi.projects.ProjectService;
import io.darbata.basecampapi.projects.FeaturedProjectDTO;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class GithubService {

    private final GithubRepoRepository repository;
    @Lazy private final GithubAPIService githubAPIService;
    @Lazy private final GithubTokenService tokenService;
    @Lazy private final ProjectService projectService;
    @Lazy private final InstallationAccessTokenService installationAccessTokenService;

    public GithubService(GithubRepoRepository repository, GithubAPIService githubAPIService,
                         GithubTokenService tokenService, ProjectService projectService, InstallationAccessTokenService installationAccessTokenService) {
        this.repository = repository;
        this.githubAPIService = githubAPIService;
        this.tokenService = tokenService;
        this.projectService = projectService;
        this.installationAccessTokenService = installationAccessTokenService;
    }

    public List<GithubRepository> fetchUserGithubRepositories(String callerId) {
        // callerId is the id of the current auth user whose token we will be using
        GithubToken token = tokenService.getUserToken(callerId);
        return githubAPIService.fetchUserRepositories(token);
    }

    public GithubRepository findById(String callerId, long id) {
        // callerId is the id of the current auth user whose token we will be using
        return repository.findById(id)
                .orElseGet(() -> fetchGithubRepository(callerId, id)); // else fetch from api
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
        GithubRepository repo = githubAPIService.fetchGithubRepositoryById(token, id); // call api
        repository.save(repo); // save (cache) it
        return repository.findById(id).orElseThrow();
    }

    public IssueComment createIssueComment(String callerId, long repoId, int issueNumber ) {
        GithubRepository repo = findById(callerId, repoId);

        String[] repoNameSplit = repo.name().split("/");
        String repoName = repoNameSplit[repoNameSplit.length - 1];

        GithubToken token = tokenService.getUserToken(callerId);

        return githubAPIService.commentOnIssue(token, repo.owner().login(), repoName, issueNumber);
    }


    public List<ProjectItemDTO> fetchProjectItems(UUID projectId) throws Exception {
        InstallationAccessToken token = installationAccessTokenService.getInstallationAccessToken();
        FeaturedProjectDTO project = projectService.getFeaturedProjectById(token.token(), projectId);
        System.out.println("Fetching items from project number: " + project.githubProjectNumber());
        GithubGraphQLResponse response = githubAPIService.fetchGithubProjectItems(
                token,
                project.githubProjectNumber()
        );

        System.out.println(response);

        if (response == null || response.data() == null || response.data().organization() == null ) {
            throw new RuntimeException("Github API returned no organisation data");
        }

        ProjectV2 projectV2 = response.data().organization().projectV2();
        if (projectV2 == null) {
            throw new RuntimeException("Project #" + project.githubProjectNumber() + " not found");
        }

        return projectV2.items().nodes().stream().map((projectNode) -> {
            String[] projectUrl = projectNode.content().url().split("/");
            long issueNumber = Long.parseLong(projectUrl[projectUrl.length - 1]);

            String status = projectNode.fieldValues().nodes().stream()
                    .filter(fieldValueNode -> fieldValueNode.field() != null &&
                            "Status".equals(fieldValueNode.field().name()))
                    .map(fieldValueNode -> fieldValueNode.name())
                    .findAny()
                    .orElse("No Status");


            return new ProjectItemDTO(
                    "dsec",
                    project.githubProjectNumber(),
                    issueNumber,
                    projectNode.content().title(),
                    projectNode.content().body(),
                    projectNode.content().url(),
                    status,
                    projectNode.content().assignees().nodes(),
                    projectNode.content().createdAt(),
                    projectNode.content().updatedAt(),
                    projectNode.content().labels().nodes()
            );
        }).toList();
    }

    public List<GithubRepository> fetchOrganisationRepositories() throws Exception {
        InstallationAccessToken token = installationAccessTokenService.getInstallationAccessToken();
        return githubAPIService.fetchOrganisationRepositories(token);
    }
}