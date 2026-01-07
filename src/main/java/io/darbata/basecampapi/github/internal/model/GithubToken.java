package io.darbata.basecampapi.github.internal.model;

import io.darbata.basecampapi.github.internal.dto.GithubTokenDTO;

import java.time.LocalDateTime;
import java.util.UUID;

public record GithubToken(
        UUID userId, // maps to oauth user
        String accessToken,
        LocalDateTime accessTokenExpiryDate,
        String refreshToken,
        LocalDateTime refreshTokenExpiryDate,
        String scope,
        String tokenType
) {
    public static GithubToken fromDto(UUID userId, GithubTokenDTO dto) {
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

    public boolean hasAccessToken() {
        return accessToken != null;
    }

    public boolean validAccessToken() {
        return LocalDateTime.now().isBefore(this.accessTokenExpiryDate);
    }

    public boolean validRefreshToken() {
        return LocalDateTime.now().isBefore(this.refreshTokenExpiryDate);
    }
}
