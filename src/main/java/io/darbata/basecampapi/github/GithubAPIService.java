package io.darbata.basecampapi.github;

import io.darbata.basecampapi.github.internal.*;
import io.darbata.basecampapi.github.internal.dto.githubproject.GithubGraphQLResponse;
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
                repo.stars(), repo.pushedAt(), repo.owner());
    }

    public IssueComment commentOnIssue(GithubToken token, String projectOwner, String repoName, int issueNumber) {

        System.out.println("Posting to " + projectOwner + "/" + repoName + "/" + issueNumber);

        return client.createIssueComment(
                "Bearer " + token.accessToken(),
                projectOwner,
                repoName,
                issueNumber,
                new CreateIssueCommentRequest("Commented by backend")
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
                    items(first: 20) {
                        nodes {
                            id
                            __typename
                            content {
                                ... on Issue {
                                    title
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



    public void assignGithubIssueTo(String githubLogin, long projectNumber, long issueNumber) {
        return;
    }

    public List<GithubRepository> fetchOrganisationRepositories(InstallationAccessToken token) {
        String org = "dsec-hub";

        return client.fetchOrganisationRepositories(
                "Bearer " + token.token(),
                org
        );

    }
}


