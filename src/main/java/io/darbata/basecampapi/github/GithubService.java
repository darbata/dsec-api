package io.darbata.basecampapi.github;

import io.darbata.basecampapi.github.internal.GithubUser;
import io.darbata.basecampapi.github.internal.InstallationAccessToken;
import io.darbata.basecampapi.github.internal.IssueComment;
import io.darbata.basecampapi.github.internal.dto.githubproject.FieldValueNode;
import io.darbata.basecampapi.github.internal.dto.githubproject.GithubGraphQLResponse;
import io.darbata.basecampapi.github.internal.dto.githubproject.ProjectV2;
import io.darbata.basecampapi.github.internal.model.GithubToken;
import io.darbata.basecampapi.projects.ProjectService;
import io.darbata.basecampapi.projects.FeaturedProjectDTO;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    public GithubRepository findUserProjectById(String callerId, long id) {
        // callerId is the id of the current auth user whose token we will be using
        return repository.findById(id)
                .orElseGet(() -> fetchGithubRepository(callerId, id)); // else fetch from api
    }

    public GithubRepository findFeaturedProjectById(long id) {
        return repository.findById(id)
                .orElseGet(() -> {
                    try {
                        return fetchDsecGithubRepository(id);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public GithubRepository fetchDsecGithubRepository(long id) throws Exception {
        InstallationAccessToken token = installationAccessTokenService.getInstallationAccessToken();
        GithubRepository repo = githubAPIService.fetchDsecGithubRepositoryById(token, id); // call api
        repository.save(repo); // save (cache) it
        return repository.findById(id).orElseThrow();
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

    public IssueComment createIssueComment(String callerId, long repoId, int issueNumber, String comment) {
        GithubRepository repo = findUserProjectById(callerId, repoId);

        String[] repoNameSplit = repo.name().split("/");
        String repoName = repoNameSplit[repoNameSplit.length - 1];

        GithubToken token = tokenService.getUserToken(callerId);

        return githubAPIService.commentOnIssue(token, repo.owner().login(), repoName, issueNumber, comment);
    }

    public GithubUser getAuthenticatedUser(String callerId) {
        GithubToken token = tokenService.getUserToken(callerId);
        return githubAPIService.getAuthenticatedUser(token);
    }

    public void assignIssueToSelf(String callerId, long repoId, int issueNumber) throws Exception {
        InstallationAccessToken token = installationAccessTokenService.getInstallationAccessToken();
        GithubUser user = getAuthenticatedUser(callerId);
        GithubRepository repo = findUserProjectById(callerId, repoId);
        githubAPIService.assignGithubIssue(token, repo.name(), issueNumber, user.login());
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

            int issueNumber = projectNode.content().number();

            Optional<FieldValueNode> status = projectNode.fieldValues().nodes().stream()
                    .filter(fieldValueNode -> fieldValueNode.field() != null &&
                            "Status".equals(fieldValueNode.field().name()))
                    .findAny();

            String statusName = status.map(FieldValueNode::name).orElse(null);
            String statusFieldId = status.map(node -> node.field().id()).orElse(null);


            List<StatusFieldOption> statusOptions = new ArrayList<>();

            if (status.isPresent() && status.get().field().options() != null) {
                statusOptions = status.get().field().options().stream()
                        .map(option -> new StatusFieldOption(
                                option.name(),
                                option.id()
                        ))
                        .toList();
            }

            return new ProjectItemDTO(
                    "dsec",
                    project.githubProjectNumber(),
                    issueNumber,
                    projectNode.content().title(),
                    projectNode.content().body(),
                    projectNode.content().url(),
                    statusName,
                    statusFieldId,
                    statusOptions,
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

    public void updateItemStatus(int githubProjectNumber, int itemNumber, IssueStatus newStatus) throws Exception {
        System.out.println("Updating item status for project #" + githubProjectNumber + ": " + itemNumber);
        InstallationAccessToken token = installationAccessTokenService.getInstallationAccessToken();
        GithubGraphQLResponse response = githubAPIService.fetchGithubProjectItems(token, githubProjectNumber);

        ProjectV2 project = response.data().organization().projectV2();

        ProjectItemNode item = project.items().nodes()
                .stream()
                .filter(itemNode -> itemNode.content().number() == itemNumber)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("item not found"));

        FieldValueNode status = item.fieldValues().nodes().stream()
                .filter(fieldValueNode -> fieldValueNode != null && fieldValueNode.field() != null && "Status".equals(fieldValueNode.field().name()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("status not found"));

        String intendedStatusOptionId = status.field().options().stream()
                .filter(option -> option != null && option.name().equals(newStatus.getValue()))
                .findAny()
                .orElseThrow(() -> new RuntimeException("no status field found"))
                .id();

        githubAPIService.updateItemStatus(token, project.id(), item.id(), status.field().id(), intendedStatusOptionId);
    }
}