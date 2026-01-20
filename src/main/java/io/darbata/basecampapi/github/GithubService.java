package io.darbata.basecampapi.github;

import io.darbata.basecampapi.github.internal.*;
import io.darbata.basecampapi.github.internal.dto.GithubTokenDTO;
import io.darbata.basecampapi.github.internal.exception.GithubCodeTokenExchangeException;
import io.darbata.basecampapi.github.internal.model.GithubToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
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



    public GithubService(TokenEncryptionService cipher, GithubApiClient client, GithubTokenRepository tokenRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.cipher = cipher;
        this.client = client;
        this.tokenRepository = tokenRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public List<GithubRepositoryDTO> fetchUserRepositories(UUID userId) {
        GithubToken token = getToken(userId)
                .orElseThrow(() -> new GithubCodeTokenExchangeException("Github code token expired"));

        return client.fetchUserRepositories(
                "Bearer " + token.accessToken(),
                "owner",
                100,
                0
        );
    }

    public GithubRepositoryDTO fetchGithubRepositoryById(UUID userId, long githubRepositoryId) {
        GithubToken token = getToken(userId)
                .orElseThrow(() -> new NoTokenException("No token found for user " + userId));
        return client.getRepository(token.accessToken(), githubRepositoryId);
    }

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

    GithubToken refreshToken(UUID userId, GithubToken expiredToken) {
        GithubTokenDTO dto = client.refreshToken(githubClientId, githubClientSecret, "refresh_token", expiredToken.refreshToken());

        if (dto.error() != null || dto.accessToken() == null) {
            tokenRepository.deleteById(userId);
            throw new GithubCodeTokenExchangeException("Github session expired, user must login again");
        }


        System.out.println("Github refresh token from client : " + dto);
        GithubToken token = GithubToken.fromDto(userId, dto);
        GithubToken savedToken = tokenRepository.update(cipher.encrypt(token));
        return cipher.decrypt(savedToken);
    }

    public boolean isGithubConnected(UUID userId) {
        return getToken(userId).isPresent();
    }

    public void disconnectGithub(UUID userId) {
        tokenRepository.deleteById(userId);
    }

    private Optional<GithubToken> getToken(UUID userId) {
        return tokenRepository.findById(userId)
                .map(cipher::decrypt)
                .map(token -> {
                    if (!token.validAccessToken()) {
                        return refreshToken(userId, token);
                    }
                    return token;
                });
    }

}


