package io.darbata.basecampapi.github;

import java.util.UUID;

public record GithubExchangeTokenEvent (
        UUID userId,
        String code
) {}
