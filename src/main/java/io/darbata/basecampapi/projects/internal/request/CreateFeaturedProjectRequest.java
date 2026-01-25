package io.darbata.basecampapi.projects.internal.request;

public record CreateFeaturedProjectRequest(
        String title,
        String tagline,
        String description,
        String bannerUrl,
        long repoId
) { }
