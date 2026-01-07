package io.darbata.basecampapi.github.internal.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubTokenDTO(String accessToken, Integer expiresIn, String refreshToken,
                             Integer refreshTokenExpiresIn, String scope, String tokenType,
                             String error
) {
    @JsonCreator
    public GithubTokenDTO(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("expires_in") Integer expiresIn,
            @JsonProperty("refresh_token") String refreshToken,
            @JsonProperty("refresh_token_expires_in") Integer refreshTokenExpiresIn,
            @JsonProperty("scope") String scope,
            @JsonProperty("token_type") String tokenType,
            @JsonProperty("error") String error
    ) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
        this.refreshTokenExpiresIn = refreshTokenExpiresIn;
        this.scope = scope;
        this.tokenType = tokenType;
        this.error = error;
    }
}
