package io.darbata.basecampapi.github;

import io.darbata.basecampapi.github.internal.GithubApiClient;
import io.darbata.basecampapi.github.internal.GithubTokenRepository;
import io.darbata.basecampapi.github.internal.TokenEncryptionService;
import io.darbata.basecampapi.github.internal.dto.GithubTokenDTO;
import io.darbata.basecampapi.github.internal.exception.GithubCodeTokenExchangeException;
import io.darbata.basecampapi.github.internal.model.GithubToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class GithubTokenService {

    private final TokenEncryptionService cipher;
    private final GithubTokenRepository tokenRepository;
    private final GithubApiClient client;

    @Value("${spring.security.oauth2.client.github.client-id}")
    private String githubClientId;

    @Value("${spring.security.oauth2.client.github.client-secret}")
    private String githubClientSecret;


    public GithubTokenService(
            TokenEncryptionService cipher,
            GithubTokenRepository tokenRepository,
            GithubApiClient client
    ) {
        this.cipher = cipher;
        this.tokenRepository = tokenRepository;
        this.client = client;
    }

    public GithubToken getUserToken(String userId) {
        Optional<GithubToken> t = tokenRepository.findById(userId)
                .map(cipher::decrypt)
                .map(token -> {
                    if (!token.validAccessToken()) {
                        return refreshToken(userId, token);
                    }
                    return token; // can be null
                });
        return t.orElse(null);
    }

    public void revokeUserToken(String userId) {
        tokenRepository.deleteById(userId);
    }

    private GithubToken refreshToken(String userId, GithubToken expiredToken) {
        GithubTokenDTO dto = client.refreshToken(githubClientId, githubClientSecret, "refresh_token", expiredToken.refreshToken());

        if (dto.error() != null || dto.accessToken() == null) {
            revokeUserToken(userId);
            return null;
        }


        System.out.println("Github refresh token from client : " + dto);
        GithubToken token = GithubToken.fromDto(userId, dto);
        GithubToken savedToken = tokenRepository.update(cipher.encrypt(token));
        return cipher.decrypt(savedToken);
    }

    @EventListener
    void exchangeToken(GithubExchangeTokenEvent event) {
        System.out.println(githubClientId);
        System.out.println(githubClientSecret);
        GithubTokenDTO dto = client.exchangeCodeForToken(githubClientId, githubClientSecret, event.code());
        System.out.println("Github code token: " + dto.accessToken());

        if (dto.error() != null) {
            throw new GithubCodeTokenExchangeException("Error ... with error : " + (dto == null ? "" : dto.error()));
        }

        GithubToken token = GithubToken.fromDto(event.userId(), dto);
        System.out.println("Github token: " + token.accessToken());
        tokenRepository.save(cipher.encrypt(token));
    }
}