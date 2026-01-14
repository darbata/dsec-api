package io.darbata.basecampapi.github.internal.model;

import io.darbata.basecampapi.github.internal.dto.GithubTokenDTO;

import java.time.OffsetDateTime;
import java.util.UUID;

public record GithubToken(
        UUID userId, // maps to oauth user
        String accessToken,
        OffsetDateTime accessTokenExpiryDate,
        String refreshToken,
        OffsetDateTime refreshTokenExpiryDate,
        String scope,
        String tokenType
) {
    public static GithubToken fromDto(UUID userId, GithubTokenDTO dto) {
        return new GithubToken(
                userId,
                dto.accessToken(),
                OffsetDateTime.now().plusSeconds(dto.expiresIn()),
                dto.refreshToken(),
                OffsetDateTime.now().plusSeconds(dto.refreshTokenExpiresIn()),
                dto.scope(),
                dto.tokenType()
        );
    }

    public boolean hasAccessToken() {
        return accessToken != null;
    }

    public boolean validAccessToken() {
        return OffsetDateTime.now().isBefore(this.accessTokenExpiryDate);
    }

    public boolean validRefreshToken() {
        return OffsetDateTime.now().isBefore(this.refreshTokenExpiryDate);
    }
}
