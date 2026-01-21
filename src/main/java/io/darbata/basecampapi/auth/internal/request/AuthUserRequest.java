package io.darbata.basecampapi.auth.internal.request;

import java.util.UUID;

public record AuthUserRequest(
        String id,
        String email,
        String displayName,
        String discordDisplayName,
        String avatarUrl
) { }
