package io.darbata.basecampapi.projects.internal.model;

import java.util.UUID;

public record Project (
    UUID id, // auto gen this
    String title, // unique
    String description,
    long githubRepoId,
    String githubRepoName,
    String githubRepoUrl,
    String githubRepoLanguage,
    UUID ownerId,
    String ownerUsername,
    boolean featured
) {}
