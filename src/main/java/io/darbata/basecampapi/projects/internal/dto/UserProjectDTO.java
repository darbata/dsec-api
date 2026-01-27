package io.darbata.basecampapi.projects.internal.dto;

import io.darbata.basecampapi.auth.UserDTO;
import io.darbata.basecampapi.github.GithubRepository;
import io.darbata.basecampapi.projects.internal.model.Project;

import java.util.UUID;

public record UserProjectDTO (
    UUID id,
    String title,
    String description,
    String ownerDisplayName,
    String ownerAvatarUrl,
    GithubRepository repo
) {
    public static UserProjectDTO fromEntity(Project project, UserDTO user, GithubRepository repository) {
        return new UserProjectDTO(project.getId(), project.getTitle(), project.getDescription(), user.displayName(),
                user.avatarUrl(), repository);
    }
}
