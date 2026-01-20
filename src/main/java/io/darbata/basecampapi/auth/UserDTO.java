package io.darbata.basecampapi.auth;

public record UserDTO(
        String email,
        String displayName,
        String discordDisplayName,
        String avatarUrl,
        boolean githubConnected
) { }
