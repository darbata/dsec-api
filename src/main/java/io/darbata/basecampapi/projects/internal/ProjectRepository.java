package io.darbata.basecampapi.projects.internal;

import io.darbata.basecampapi.github.GithubRepository;
import io.darbata.basecampapi.projects.internal.dto.UserProjectDTO;
import io.darbata.basecampapi.projects.internal.model.Project;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ProjectRepository {
    private final JdbcClient jdbcClient;

    public ProjectRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Optional<Project> findById(UUID projectId) {
        String sql = """
            SELECT * FROM projects WHERE id = :id;
        """;

        return jdbcClient.sql(sql)
                .param("id", projectId)
                .query(Project.class)
                .optional();
    }

    public Project save(Project project) {
        String sql = """
            INSERT INTO projects (title, description, tagline, banner_url, featured, owner_id, repo_id)
            VALUES (:title, :description, :tagline, :bannerUrl, :featured, :ownerId, :repoId);
        """;

        jdbcClient.sql(sql)
                .param("title", project.getTitle())
                .param("description", project.getDescription())
                .param("tagline", project.getTagline())
                .param("bannerUrl", project.getBannerUrl())
                .param("featured", project.isFeatured())
                .param("ownerId", project.getOwnerId())
                .param("repoId", project.getRepoId())
            .update();

        return project;
    }

    public List<UserProjectDTO> fetchCommunityProjects(int pageSize, int pageNum) {
        String sql = """
            SELECT
                p.id AS id,
                p.title AS title,
                p.description AS description,
                u.display_name AS ownerDisplayName,
                u.avatar_url AS ownerAvatarUrl,
                g.id AS repoId,
                g.name AS repoName,
                g.url AS repoUrl,
                g.language AS repoLanguage,
                g.open_tickets AS repoOpenTickets,
                g.contributors AS repoContributors,
                g.stars AS repoStars,
                g.pushed_at AS repoPushedAt
            FROM projects p
            JOIN oauth_users u ON p.owner_id = u.id
            LEFT JOIN github_repositories g ON p.repo_id = g.id
            WHERE featured = false
            ORDER BY p.id
            LIMIT :pageSize OFFSET :pageNum;
        """;

        return jdbcClient.sql(sql)
                .param("pageNum", pageNum)
                .param("pageSize", pageSize)
                .query((rs, rowNum) -> {
                    Timestamp ts = rs.getTimestamp("repoPushedAt");
                    Instant pushedAtInstant = (ts != null) ? ts.toInstant() : null;
                    return new UserProjectDTO(
                            rs.getObject("id", UUID.class),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getString("ownerDisplayName"),
                            rs.getString("ownerAvatarUrl"),
                            rs.getLong("repoId"),
                            rs.getString("repoName"),
                            rs.getString("repoUrl"),
                            rs.getString("repoLanguage"),
                            rs.getInt("repoOpenTickets"),
                            rs.getInt("repoStars"),
                            pushedAtInstant
                    );
                })

                .list();
    }

    public List<FeaturedProjectDTO> fetchFeaturedProjects(int pageSize, int pageNum) {
        String sql = """
            SELECT
                p.id AS id,
                p.title AS title,
                p.tagline AS tagline,
                p.banner_url AS bannerUrl,
                p.description AS description,
                u.display_name AS ownerDisplayName,
                u.avatar_url AS ownerAvatarUrl,
                g.id AS repoId,
                g.name AS repoName,
                g.url AS repoUrl,
                g.language AS repoLanguage,
                g.open_tickets AS repoOpenTickets,
                g.contributors AS repoContributors,
                g.stars AS repoStars,
                g.pushed_at AS repoPushedAt
            FROM projects p 
            JOIN oauth_users u ON p.owner_id = u.id
            LEFT JOIN github_repositories g ON p.repo_id = g.id
            WHERE featured = true
            ORDER BY p.id
            LIMIT :pageSize OFFSET :pageNum;
        """;

        return jdbcClient.sql(sql)
                .param("pageNum", pageNum)
                .param("pageSize", pageSize)
                .query((rs, rowNum) -> {
                    Timestamp ts = rs.getTimestamp("repoPushedAt");
                    Instant pushedAtInstant = (ts != null) ? ts.toInstant() : null;
                    return new FeaturedProjectDTO(
                            rs.getObject("id", UUID.class),
                            rs.getString("title"),
                            rs.getString("tagline"),
                            rs.getString("bannerUrl"),
                            rs.getString("description"),
                            rs.getString("ownerDisplayName"),
                            rs.getString("ownerAvatarUrl"),
                            rs.getLong("repoId"),
                            rs.getString("repoName"),
                            rs.getString("repoUrl"),
                            rs.getString("repoLanguage"),
                            rs.getInt("repoOpenTickets"),
                            rs.getInt("repoStars"),
                            pushedAtInstant
                    );
                })
                .list();
    }
}
