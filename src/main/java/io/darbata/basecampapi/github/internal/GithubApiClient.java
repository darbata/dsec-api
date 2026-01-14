package io.darbata.basecampapi.github.internal;

import io.darbata.basecampapi.github.GithubProfileDTO;
import io.darbata.basecampapi.github.GithubRepositoryDTO;
import io.darbata.basecampapi.github.internal.dto.GithubTokenDTO;
import io.darbata.basecampapi.github.internal.model.GithubToken;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
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
    GithubProfileDTO fetchProfile(
            @RequestHeader("Authorization") String token
    );

    @GetExchange(value = "https://api.github.com/user/repos", accept = "application/json")
    List<GithubRepositoryDTO> fetchUserRepositories(
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
    GithubRepositoryDTO getRepository(
            @RequestHeader("Authorization") String token,
            @PathVariable("id") Long id
    );
}
