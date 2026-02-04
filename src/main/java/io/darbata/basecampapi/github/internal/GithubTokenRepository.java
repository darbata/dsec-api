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


    public Optional<GithubToken> findById(String userId) {
        String sql = """
            SELECT * FROM github_tokens WHERE user_id = :userId;
        """;

        return jdbcClient.sql(sql)
                .param("userId", userId)
                .query(GithubToken.class)
                .optional();
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

    public GithubToken update(GithubToken token) {
        String sql = """
            UPDATE github_tokens SET
                access_token = :accessToken,
                access_token_expiry_date = :accessTokenExpiryDate,
                refresh_token = :refreshToken,
                refresh_token_expiry_date = :refreshTokenExpiryDate,
                scope = :scope,
                token_type = :tokenType
            WHERE user_id = :userId RETURNING *;
        """;

        jdbcClient.sql(sql)
                .param("userId", token.userId())
                .param("accessToken", token.accessToken())
                .param("accessTokenExpiryDate", token.accessTokenExpiryDate())
                .param("refreshToken", token.refreshToken())
                .param("refreshTokenExpiryDate", token.refreshTokenExpiryDate())
                .param("scope", token.scope())
                .param("tokenType", token.tokenType())
                .query(GithubToken.class)
                .single();

        return token;
    }

    public void deleteById(String userId) {
        String sql = """
            DELETE FROM github_tokens WHERE user_id = :userId;
        """;

        jdbcClient.sql(sql).param("userId", userId).update();
    }



}
