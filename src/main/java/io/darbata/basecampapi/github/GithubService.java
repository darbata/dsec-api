package io.darbata.basecampapi.github;

import io.darbata.basecampapi.github.internal.*;
import io.darbata.basecampapi.github.internal.dto.GithubTokenDTO;
import io.darbata.basecampapi.github.internal.exception.GithubCodeTokenExchangeException;
import io.darbata.basecampapi.github.internal.exception.NoTokenException;
import io.darbata.basecampapi.github.internal.model.GithubToken;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Service public class GithubService {

    private final ApplicationEventPublisher applicationEventPublisher;
    @Value("${spring.security.oauth2.client.github.client-id}")
    private String githubClientId;

    @Value("${spring.security.oauth2.client.github.client-secret}")
    private String githubClientSecret;

    private final TokenEncryptionService cipher;
    private final GithubApiClient client;
    private final GithubTokenRepository tokenRepository;

    public GithubRepositoryDTO fetchGithubRepositoryById(UUID userId, long githubRepositoryId) {
        GithubToken token = getToken(userId);
        GithubRepositoryDTO dto = client.getRepository(token.accessToken(), githubRepositoryId);
        System.out.println(dto);
        return dto;
    }

    public GithubService(TokenEncryptionService cipher, GithubApiClient client, GithubTokenRepository tokenRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.cipher = cipher;
        this.client = client;
        this.tokenRepository = tokenRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public List<GithubRepositoryDTO> fetchUserRepositories(UUID userId) {
        System.out.println("Fetching user repositories for " + userId);
        GithubToken token = getToken(userId);
        System.out.println("Acquired access token " + token.accessToken());
        return client.fetchUserRepositories(
                "Bearer " + token.accessToken(),
                "owner",
                100,
                0
        );
    }

    public Optional<GithubProfileDTO> fetchUserProfile(UUID userId) {
        return tokenRepository.findById(userId)
                .map(cipher::decrypt)
                .map(token -> {
                    return client.fetchProfile("Bearer " + token.accessToken());
                });
    }

    @Async
    @EventListener
    void exchangeToken(GithubExchangeTokenEvent event) {
        GithubTokenDTO dto = client.exchangeCodeForToken(githubClientId, githubClientSecret, event.code());
        System.out.println("Github code token: " + dto.accessToken());

        if (dto.error() != null) {
            throw new GithubCodeTokenExchangeException("Error exchanging code for token for user " + event.userId() + " with error : " + dto == null ? "" : dto.error());
        }

        GithubToken token = GithubToken.fromDto(event.userId(), dto);
        System.out.println("Github token: " + token.accessToken());
        tokenRepository.save(cipher.encrypt(token));
    }

    @Async
    @EventListener
    void refreshToken(GithubRefreshTokenEvent event) {
        GithubToken token = tokenRepository.findById(event.userId()).orElseThrow(() -> new NoTokenException("no github token found for: " + event.userId()));
        token = cipher.decrypt(token);
        validateToken(token);
        client.refreshToken(githubClientId, githubClientSecret, "refresh_token", token.refreshToken());
    }

    private void validateToken(GithubToken token) {
        if (!token.hasAccessToken()) throw new NoTokenException("no github token found for: " + token.userId());
        if (!token.validRefreshToken()) throw new NoTokenException("no github token found for: " + token.userId());
        if (!token.validAccessToken()) {
            applicationEventPublisher.publishEvent(new GithubRefreshTokenEvent(token.userId(), token.accessToken()));
        }
    }

    private GithubToken getToken(UUID userId) {
        System.out.println("getting token for " + userId);
        GithubToken token = tokenRepository.findById(userId).orElse(null);
        System.out.println("encrypted token " + token.accessToken());

        if (token == null) { return null; }

        token = cipher.decrypt(token);
        validateToken(token);
        return token;
    }

}


