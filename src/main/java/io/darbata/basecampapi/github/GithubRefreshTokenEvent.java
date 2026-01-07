package io.darbata.basecampapi.github;

import java.util.UUID;

public record GithubRefreshTokenEvent (
        UUID userId, String code
) {}
