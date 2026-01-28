package io.darbata.basecampapi.github;

import io.darbata.basecampapi.projects.internal.dto.UserProjectDTO;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// cache GitHub Repositories from the API
@Repository
public class GithubRepoRepository {

    private final JdbcClient jdbcClient;

    public GithubRepoRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void save(GithubRepository repo) {
        String sql = """
            INSERT INTO github_repositories (id, name, url, language, open_tickets, contributors, stars, pushed_at)
            VALUES (:id, :name, :url, :language, :openTickets, :contributors, :stars, :pushedAt);
        """;


        this.jdbcClient.sql(sql)
                .params(Map.of(
                        "id", repo.id(),
                        "name", repo.name(),
                        "url", repo.url(),
                        "language", repo.language(),
                        "openTickets", repo.openTickets(),
                        "contributors", 0,
                        "stars", repo.stars(),
                        "pushedAt", Timestamp.from(repo.pushedAt())
                ))
                .update();
    }

    public Optional<GithubRepository> findById(long repoId) {
        String sql = """
        SELECT * FROM github_repositories WHERE id = :id
        """;

        return this.jdbcClient.sql(sql).param("id", repoId).query(GithubRepository.class).optional();
    }

    public void delete(long repoId) {
        String sql = """
        DELETE FROM github_repositories WHERE id = :id
        """;
        this.jdbcClient.sql(sql).params(Map.of("id", repoId)).update();

    }

}