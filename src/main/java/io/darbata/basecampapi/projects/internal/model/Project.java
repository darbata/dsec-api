package io.darbata.basecampapi.projects.internal.model;

import java.time.OffsetDateTime;
import java.util.UUID;

// TODO: make a factory function
// TODO: convert to class
public record Project (
    UUID id, // auto gen this
    boolean featured,
    String title,
    String tagline,
    String description,
    String bannerUrl,
    OffsetDateTime createdAt,
    String ownerId, // system if created by DSEC
    long repoId
) {}
