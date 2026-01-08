package io.darbata.basecampapi.github;

public record GithubRepositoryDTO (
        long id,
        String name,
        String url,
        String language
) { }
