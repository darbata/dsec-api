package io.darbata.basecampapi.projects.internal.model;

import io.darbata.basecampapi.github.GithubRepositoryDTO;

import java.time.OffsetDateTime;
import java.util.UUID;

public record Project (
    UUID id, // auto gen this
    String title, // unique
    String description,
    boolean featured,
    long githubRepoId,
    String githubRepoName,
    String githubRepoUrl,
    String githubRepoLanguage,
    UUID ownerId,
    OffsetDateTime createdAt
) {}
