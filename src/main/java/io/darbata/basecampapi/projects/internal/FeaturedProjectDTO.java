package io.darbata.basecampapi.projects.internal;

import io.darbata.basecampapi.auth.UserDTO;
import io.darbata.basecampapi.github.GithubRepository;
import io.darbata.basecampapi.projects.internal.model.Project;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public record FeaturedProjectDTO (
        UUID id,
        String title,
        String tagline,
        String bannerUrl,
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
    public static FeaturedProjectDTO from(Project project, UserDTO user, GithubRepository repo) {
        return new FeaturedProjectDTO(
                project.getId(), project.getTitle(), project.getTagline(), project.getBannerUrl(), project.getDescription(),
                user.displayName(), user.avatarUrl(), repo.id(),
                repo.name(), repo.url(), repo.language(), repo.openTickets(), repo.stars(), repo.pushedAt()
        );
    }
}
