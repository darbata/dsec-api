package io.darbata.basecampapi.github.internal;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public record InstallationAccessToken(
        @JsonProperty("token") String token,
        @JsonProperty("expires_at") Date expiresAt
) { }
