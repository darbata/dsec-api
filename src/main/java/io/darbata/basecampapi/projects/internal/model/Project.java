package io.darbata.basecampapi.projects.internal.model;

import io.darbata.basecampapi.github.GithubRepositoryDTO;

import java.util.UUID;

public record Project (
    UUID id, // auto gen this
    String title, // unique
    String description,
    boolean featured,
    GithubRepositoryDTO repo,
    UUID ownerId
) {}
