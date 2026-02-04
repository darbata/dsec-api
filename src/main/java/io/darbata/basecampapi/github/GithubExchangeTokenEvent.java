package io.darbata.basecampapi.github;

public record GithubExchangeTokenEvent (
        String userId,
        String code
) {}
