package io.darbata.basecampapi.github;

import java.util.UUID;

public record GithubExchangeTokenEvent (
        String userId,
        String code
) {}
