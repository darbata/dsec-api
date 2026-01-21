package io.darbata.basecampapi.projects.internal.dto;

import io.darbata.basecampapi.github.GithubRepositoryDTO;

public record ProjectDTO (
    String title,
    String description,
    GithubRepositoryDTO repo,
    String ownerDisplayName,
    String ownerAvatarUrl
) { }
