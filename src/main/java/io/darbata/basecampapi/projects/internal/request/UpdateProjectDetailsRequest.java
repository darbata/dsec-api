package io.darbata.basecampapi.projects.internal.request;

import java.util.UUID;

public record UpdateProjectDetailsRequest(UUID projectId, String title, String description) {}
