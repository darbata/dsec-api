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

    public Optional<Project> findByTitle(String title) {
        String sql = """
            SELECT * FROM projects WHERE title = :title;
        """;

        return jdbcClient.sql(sql)
                .param("title", title)
                .query(Project.class)
                .optional();
    }

    public Project save(Project project) {
        String sql = """
            INSERT INTO projects (title, description, github_repo_id, github_repo_name,
            github_repo_url, github_repo_language, owner_id)
            VALUES (:title, :description, :githubRepoId, :githubRepoName,
            :githubRepoUrl, :githubRepoLanguage, :ownerId);
        """;

        jdbcClient.sql(sql)
            .param("title", project.title())
            .param("description", project.description())
            .param("githubRepoId", project.githubRepoId())
            .param("githubRepoName", project.githubRepoName())
            .param("githubRepoUrl", project.githubRepoUrl())
            .param("githubRepoLanguage", project.githubRepoLanguage())
            .param("ownerId", project.ownerId())
            .update();

        return project;
    }

    public List<Project> getCommunityProjects(int pageSize, int pageNum) {
        String sql = """
            SELECT * FROM projects WHERE featured = false ORDER BY created_at LIMIT :pageSize OFFSET :pageNum;
        """;

        return jdbcClient.sql(sql)
                .param("pageNum", pageNum)
                .param("pageSize", pageSize)
                .query(Project.class)
                .list();
    }

    public List<Project> getFeaturedProjects(int pageSize, int pageNum) {
        String sql = """
            SELECT * FROM projects WHERE featured = true ORDER BY created_at LIMIT :pageSize OFFSET :pageNum;
        """;

        return jdbcClient.sql(sql)
                .param("pageNum", pageNum)
                .param("pageSize", pageSize)
                .query(Project.class)
                .list();
    }

    public void toggleFeatured(String title) {
        String sql = """
        UPDATE projects SET featured = NOT featured WHERE title = :title;
        """;

        jdbcClient.sql(sql).param("title", title).update();
    }

}
