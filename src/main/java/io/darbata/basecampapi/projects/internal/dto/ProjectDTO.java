package io.darbata.basecampapi.projects.internal.dto;

import io.darbata.basecampapi.projects.internal.model.Project;

public record ProjectDTO (
    String title,
    String description,
    long githubRepoId,
    String githubRepoName,
    String githubRepoUrl,
    String githubRepoLanguage,
    String ownerUsername
) {
    public static ProjectDTO fromEntity(Project project) {
        return new ProjectDTO(
                project.title(),
                project.description(),
                project.githubRepoId(),
                project.githubRepoName(),
                project.githubRepoUrl(),
                project.githubRepoLanguage(),
                project.ownerUsername()
        );
    }
}
