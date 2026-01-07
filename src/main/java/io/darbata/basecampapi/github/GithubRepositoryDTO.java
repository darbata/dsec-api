package io.darbata.basecampapi.github;

public record GithubRepositoryDTO (
        long id,
        String name,
        String fullName,
        String description,
        String url
) {}
