package io.darbata.basecampapi.projects.internal.model;

import io.darbata.basecampapi.github.GithubRepositoryDTO;

import java.time.OffsetDateTime;
import java.util.UUID;

public record FeaturedProjectDTO(
        UUID id, // auto gen this
        String title,
        String tagline,
        String description,
        String bannerUrl,
        GithubRepositoryDTO repository,
        OffsetDateTime createdAt
) { }

