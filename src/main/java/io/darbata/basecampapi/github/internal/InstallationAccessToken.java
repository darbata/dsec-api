package io.darbata.basecampapi.github.internal;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

// used by app itself, to interact with projects
public record InstallationAccessToken(
        @JsonProperty("token") String token,
        @JsonProperty("expires_at") Date expiresAt
) { }
