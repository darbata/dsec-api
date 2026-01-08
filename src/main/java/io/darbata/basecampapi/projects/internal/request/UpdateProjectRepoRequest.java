package io.darbata.basecampapi.projects.internal.request;

import java.util.UUID;

public record UpdateProjectRepoRequest(UUID projectId, long githubRepoId) {}
