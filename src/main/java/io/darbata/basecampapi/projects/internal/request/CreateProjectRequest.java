package io.darbata.basecampapi.projects.internal.request;

public record CreateProjectRequest(String title, String description, long repoId) {}
