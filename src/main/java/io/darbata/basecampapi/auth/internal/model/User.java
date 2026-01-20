package io.darbata.basecampapi.auth.internal.model;

import java.util.UUID;

public record User(
        UUID id,
        String email,
        String displayName,
        String discordDisplayName,
        String avatarUrl
) { }
