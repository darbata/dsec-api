package io.darbata.basecampapi.github.internal;

import io.darbata.basecampapi.github.internal.model.GithubToken;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class GithubTokenRepository {
    private final JdbcClient jdbcClient;

    GithubTokenRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }


    public Optional<GithubToken> findById(UUID userId) {
        String sql = """
            SELECT * FROM github_tokens WHERE user_id = :userId;
        """;

        return jdbcClient.sql(sql).param("userId", userId).query(GithubToken.class).optional();
    }

    public GithubToken save(GithubToken token) {
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
