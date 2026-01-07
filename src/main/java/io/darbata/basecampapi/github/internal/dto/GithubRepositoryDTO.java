package io.darbata.basecampapi.github.internal.dto;

public record GithubRepositoryDTO (
        long id,
        String name,
        String fullName,
        String description,
        String url
) {}
