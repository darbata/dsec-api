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
            github_repo_url, github_repo_language, owner_id, owner_username)
            VALUES (:title, :description, :githubRepoId, :githubRepoName, 
            :githubRepoUrl, :githubRepoLanguage, :ownerId, :ownerUsername);
        """;

        jdbcClient.sql(sql)
            .param("title", project.title())
            .param("description", project.description())
            .param("githubRepoId", project.githubRepoId())
            .param("githubRepoName", project.githubRepoName())
            .param("githubRepoUrl", project.githubRepoUrl())
            .param("githubRepoLanguage", project.githubRepoLanguage())
            .param("ownerId", project.ownerId())
            .param("ownerUsername", project.ownerUsername())
            .update();

        return project;
    }

    public List<Project> paginatedFetchProjectsSortedByCreated(int pageSize, int pageNum) {
        String sql = """
            SELECT * FROM projects ORDER BY created_at LIMIT :pageSize OFFSET :pageNum;
        """;

        return jdbcClient.sql(sql)
                .param("pageNum", pageNum)
                .param("pageSize", pageSize)
                .query(Project.class)
                .list();
    }

}
