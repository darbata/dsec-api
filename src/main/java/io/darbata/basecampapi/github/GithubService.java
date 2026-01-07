package io.darbata.basecampapi.github;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.time.LocalDateTime;
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

    public GithubProfileDTO fetchUserProfile(UUID userId) {
        System.out.println("fetching user profile" + userId);
        GithubToken token = getToken(userId);
        if (token == null) { return null; }
        return client.fetchProfile("Bearer " + token.accessToken());
    }

    @Async
    @EventListener
    void exchangeToken(GithubExchangeTokenEvent event) {
        GithubTokenDTO dto = client.exchangeCodeForToken(githubClientId, githubClientSecret, event.code());

        if (dto == null || dto.error() != null) {
            throw new GithubCodeTokenExchangeException("Error exchanging code for token for user " + event.userId() + " with error : " + dto == null ? "" : dto.error());
        }

        GithubToken token = GithubToken.fromDto(event.userId(), dto);
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

@Component
class TokenEncryptionService {
    private final TextEncryptor encryptor;

    public TokenEncryptionService(
            @Value("${encryption.token.password}") String tokenPassword,
            @Value("${encryption.token.salt}") String tokenSalt
    ) {
        this.encryptor = Encryptors.text(tokenPassword, tokenSalt);
    }

    GithubToken encrypt(GithubToken token) {
        return new GithubToken(
                token.userId(),
                this.encryptor.encrypt(token.accessToken()),
                token.accessTokenExpiryDate(),
                this.encryptor.encrypt(token.refreshToken()),
                token.refreshTokenExpiryDate(),
                token.scope(),
                token.tokenType()
        );
    }

    GithubToken decrypt(GithubToken token) {
        return new GithubToken(
                token.userId(),
                this.encryptor.decrypt(token.accessToken()),
                token.accessTokenExpiryDate(),
                this.encryptor.decrypt(token.refreshToken()),
                token.refreshTokenExpiryDate(),
                token.scope(),
                token.tokenType()
        );
    }
}

@HttpExchange
@Service
interface GithubApiClient {

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

    GithubToken refreshToken (
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("grant_type") String grantType,
            @RequestParam("refresh_token") String refreshToken
    );
}


record GithubToken (
        UUID userId, // maps to oauth user
        String accessToken,
        LocalDateTime accessTokenExpiryDate,
        String refreshToken,
        LocalDateTime refreshTokenExpiryDate,
        String scope,
        String tokenType
) {
    static GithubToken fromDto(UUID userId, GithubTokenDTO dto) {
        return new GithubToken(
                userId,
                dto.accessToken(),
                LocalDateTime.now().plusSeconds(dto.expiresIn()),
                dto.refreshToken(),
                LocalDateTime.now().plusSeconds(dto.refreshTokenExpiresIn()),
                dto.scope(),
                dto.tokenType()
        );
    }

    boolean hasAccessToken() {
        return accessToken != null;
    }

    boolean validAccessToken() {
        return LocalDateTime.now().isBefore(this.accessTokenExpiryDate);
    }

    boolean validRefreshToken() {
        return LocalDateTime.now().isBefore(this.refreshTokenExpiryDate);
    }
}

@Repository
class GithubTokenRepository {
    private final JdbcClient jdbcClient;

    GithubTokenRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }


    Optional<GithubToken> findById(UUID userId) {
        String sql = """
            SELECT * FROM github_tokens WHERE user_id = :userId;
        """;

        return jdbcClient.sql(sql).param("userId", userId).query(GithubToken.class).optional();
    }

    GithubToken save(GithubToken token) {
        String sql = """
            INSERT INTO github_tokens
            (user_id, access_token, access_token_expiry_date, refresh_token,
            refresh_token_expiry_date, scope, token_type)
            VALUES (:userId, :accessToken, :accessTokenExpiryDate,  :refreshToken,
            :refreshTokenExpiryDate, :scope, :tokenType);
        """;

        jdbcClient.sql(sql)
                .param("userId", token.userId())
                .param("accessToken", token.accessToken())
                .param("accessTokenExpiryDate", token.accessTokenExpiryDate())
                .param("refreshToken", token.refreshToken())
                .param("refreshTokenExpiryDate", token.refreshTokenExpiryDate())
                .param("scope", token.scope())
                .param("tokenType", token.tokenType())
                .update();

        return token;
    }



}
