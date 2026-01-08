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

    @GetExchange("https://api.github.com/user")
    GithubProfileDTO fetchProfile(
            @RequestHeader("Authorization") String token
    );

    @GetExchange("https://api.github.com/user/repos")
    List<GithubRepositoryDTO> fetchUserRepositories(
            @RequestHeader("Authorization") String token,
            @RequestParam("affiliation") String affiliation,
            @RequestParam("per_page") int perPage,
            @RequestParam("page") int page
    );

    @PostExchange("https://github.com/login/oauth/access_token")
    GithubTokenDTO exchangeCodeForToken(
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("code") String code
    );

    @PostExchange("https://github.com/login/oauth/access_token")
    GithubToken refreshToken(
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("grant_type") String grantType,
            @RequestParam("refresh_token") String refreshToken
    );

    @GetExchange("https://api.github.com/repositories/{id}")
    GithubRepositoryDTO getRepository(
            @RequestHeader("Authorization") String token,
            @PathVariable("id") Long id
    );
}
