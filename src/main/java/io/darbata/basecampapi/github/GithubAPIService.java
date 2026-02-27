package io.darbata.basecampapi.github;

import io.darbata.basecampapi.github.internal.*;
import io.darbata.basecampapi.github.internal.dto.githubproject.GithubGraphQLResponse;
import io.darbata.basecampapi.github.internal.model.GithubToken;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

// manages token handling
@Service
class GithubAPIService {
    private final GithubApiClient client;

    public GithubUser  getAuthenticatedUser(
            GithubToken token
    ) {
        return client.getAuthenticatedUser(
                "Bearer " + token.accessToken()
        );
    }

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
                repo.stars(), repo.pushedAt(), repo.owner());
    }


    public GithubRepository fetchDsecGithubRepositoryById(InstallationAccessToken token, long id) {
        GithubRepository repo = client.getRepository(token.token(), id);
        return new GithubRepository(repo.id(), repo.name(), repo.url(), repo.language(), repo.openTickets(),
                repo.stars(), repo.pushedAt(), repo.owner());
    }

    public IssueComment commentOnIssue(GithubToken token, String projectOwner, String repoName, int issueNumber, String comment) {

        System.out.println("Posting to " + projectOwner + "/" + repoName + "/" + issueNumber);

        System.out.println("---TOKEN---");
        System.out.println(token.accessToken());

        return client.createIssueComment(
                "Bearer " + token.accessToken(),
                projectOwner,
                repoName,
                issueNumber,
                new CreateIssueCommentRequest(comment)
        );
    }

    public GithubGraphQLResponse fetchGithubProjectItems(
            InstallationAccessToken token,
            int projectNumber
    ) {
        String query = """
        query {
            organization (login: "dsec-hub") {
                projectV2(number: %d) {
                    id
                    items(first: 20) {
                        nodes {
                            id
                            __typename
                            content {
                                ... on Issue {
                                    title
                                    number
                                    body
                                    url
                                    assignees(first: 5) {
                                        nodes {
                                            login
                                            name
                                            avatarUrl
                                        }
                                    }
                                    labels(first: 10) {
                                        nodes {
                                            id
                                            name
                                            color
                                        }
                                    }
                                    createdAt
                                    updatedAt
                                }
                            }
                            fieldValues(first: 10) {
                                nodes {
                                    ... on ProjectV2ItemFieldSingleSelectValue {
                                        name
                                        field {
                                            ... on ProjectV2FieldCommon {
                                                name
                                                id
                                            }
                                            ... on ProjectV2SingleSelectField {
                                                options {
                                                    name
                                                    id
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        """.formatted(projectNumber);

        return client.queryGraphQL(
                "Bearer " + token.token(),
                new GraphQLRequest(query)
        );
    }

    public void updateItemStatus(InstallationAccessToken token, String projectId, String itemId, String fieldId, String valueId) {
        String mutation = """
            mutation {
                updateProjectV2ItemFieldValue(
                    input: {
                        projectId: "%s"
                        itemId: "%s"
                        fieldId: "%s"
                        value: {
                            singleSelectOptionId: "%s"
                        }
                    }
                ) {
                    projectV2Item {
                        id
                    }
                }
            }
        """.formatted(projectId, itemId, fieldId, valueId);

        client.queryGraphQL(
                "Bearer " + token.token(),
                new GraphQLRequest(mutation)
        );
    }


    public List<GithubRepository> fetchOrganisationRepositories(InstallationAccessToken token) {
        String org = "dsec-hub";

        return client.fetchOrganisationRepositories(
                "Bearer " + token.token(),
                org
        );

    }

    public void assignGithubIssue(InstallationAccessToken token, String name, int issueNumber, String assigneeLogin) {
        String[] nameSplit = name.split("/");
        String owner = nameSplit[0];
        String repo = nameSplit[1];

        AssigneesRequest assignees = new AssigneesRequest(new String[]{assigneeLogin});

        System.out.println("---TOKEN---");
        System.out.println(token.token());

        client.assignIssue(
                "Bearer " + token.token(),
                owner,
                repo,
                issueNumber,
                assignees
        );
    }

}


