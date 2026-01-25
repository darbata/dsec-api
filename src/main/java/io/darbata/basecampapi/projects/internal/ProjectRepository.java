package io.darbata.basecampapi.projects.internal;

import io.darbata.basecampapi.projects.internal.model.Project;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

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
            INSERT INTO projects (title, description, tagline, banner_url, owner_id, repo_id)
            VALUES (:title, :description, :tagline, :bannerUrl, :ownerId, :repoId);
        """;

        jdbcClient.sql(sql)
                .param("title", project.title())
                .param("description", project.description())
                .param("tagline", project.tagline())
                .param("bannerUrl", project.bannerUrl())
                .param("ownerId", project.ownerId())
                .param("repoId", project.repoId())
            .update();

        return project;
    }

    public List<Project> fetchCommunity(int pageSize, int pageNum) {
        String sql = """
            SELECT * FROM projects WHERE featured = false ORDER BY created_at LIMIT :pageSize OFFSET :pageNum;
        """;

        return jdbcClient.sql(sql)
                .param("pageNum", pageNum)
                .param("pageSize", pageSize)
                .query(Project.class)
                .list();
    }

    public List<Project> fetchFeatured(int pageSize, int pageNum) {
        String sql = """
            SELECT * FROM projects WHERE featured = true ORDER BY created_at LIMIT :pageSize OFFSET :pageNum;
        """;

        return jdbcClient.sql(sql)
                .param("pageNum", pageNum)
                .param("pageSize", pageSize)
                .query(Project.class)
                .list();
    }
}
