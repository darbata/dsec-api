package io.darbata.basecampapi.projects.internal.dto;

import io.darbata.basecampapi.auth.UserDTO;
import io.darbata.basecampapi.github.GithubRepository;
import io.darbata.basecampapi.projects.internal.model.Project;

import java.time.Instant;
import java.util.UUID;

public record UserProjectDTO (
    UUID id,
    String title,
    String description,
    String ownerDisplayName,
    String ownerAvatarUrl,
    long repoId,
    String repoName,
    String repoUrl,
    String repoLanguage,
    int repoOpenTickets,
    int repoStars,
    Instant repoPushedAt
) {
    public static UserProjectDTO from(Project project, UserDTO user, GithubRepository repo) {
        return new UserProjectDTO(
        project.getId(), project.getTitle(), project.getDescription(), user.displayName(), user.avatarUrl(), repo.id(),
        repo.name(), repo.url(), repo.language(), repo.openTickets(), repo.stars(), repo.pushedAt()
        );
    }
}
