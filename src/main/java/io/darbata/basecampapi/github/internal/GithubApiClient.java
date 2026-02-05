package io.darbata.basecampapi.github.internal;

import io.darbata.basecampapi.github.GithubRepository;
import io.darbata.basecampapi.github.internal.dto.GithubTokenDTO;
import io.darbata.basecampapi.github.internal.dto.githubproject.GithubGraphQLResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;

@HttpExchange
@Service
public interface GithubApiClient {

    @GetExchange(value = "https://api.github.com/user", accept = "application/json")
    GithubUser getAuthenticatedUser(
            @RequestHeader("Authorization") String token
    );

    @GetExchange(value = "https://api.github.com/user/repos", accept = "application/json")
    List<GithubRepository> fetchUserRepositories(
            @RequestHeader("Authorization") String token,
            @RequestParam("affiliation") String affiliation,
            @RequestParam("per_page") int perPage,
            @RequestParam("page") int page
    );

    @PostExchange(value = "https://github.com/login/oauth/access_token", accept = "application/json")
    GithubTokenDTO exchangeCodeForToken(
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("code") String code
    );

    @PostExchange(value = "https://github.com/login/oauth/access_token", accept = "application/json")
    GithubTokenDTO refreshToken(
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("grant_type") String grantType,
            @RequestParam("refresh_token") String refreshToken
    );

    @GetExchange(value = "https://api.github.com/repositories/{id}", accept = "application/json")
    GithubRepository getRepository(
            @RequestHeader("Authorization") String token,
            @PathVariable("id") Long id
    );

    @PostExchange(value = "https://api.github.com/app/installations/{installationId}/access_tokens", contentType = "application/json", accept = "application/vnd.github+json")
    InstallationAccessToken getInstallationAccessToken(
            @RequestHeader("Authorization") String token,
            @PathVariable("installationId") int installationId
    );

    @PostExchange(value = "https://api.github.com/graphql", contentType = "application/json")
    GithubGraphQLResponse queryGraphQL(
            @RequestHeader("Authorization") String token,
            @RequestBody GraphQLRequest body
    );

    @PostExchange(value = "https://api.github.com/repos/{owner}/{repo}/issues/{issue_number}/comments")
    IssueComment createIssueComment(
            @RequestHeader("Authorization") String token,
            @PathVariable String owner,
            @PathVariable String repo,
            @PathVariable("issue_number") int issueNumber,
            @RequestBody CreateIssueCommentRequest body
    );

    @GetExchange("https://api.github.com/orgs/{org}/repos")
    List<GithubRepository> fetchOrganisationRepositories(
            @RequestHeader("Authorization") String token,
            @PathVariable("org") String org
    );

    @PostExchange(value = "https://api.github.com/repos/{owner}/{repo}/issues/{issue_number}/assignees")
    void assignIssue(
            @RequestHeader("Authorization") String token,
            @PathVariable("owner") String owner,
            @PathVariable("repo") String repo,
            @PathVariable("issue_number") int issueNumber,
            @RequestBody AssigneesRequest assignees
    );
}
