package io.darbata.basecampapi.github;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
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
            INSERT INTO github_repositories (id, name, url, language, open_tickets, contributors, stars, pushed_at, owner_login)
            VALUES (:id, :name, :url, :language, :openTickets, :contributors, :stars, :pushedAt, :ownerLogin);
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
                        "pushedAt", Timestamp.from(repo.pushedAt()),
                        "ownerLogin", repo.owner().login()
                ))
                .update();
    }

    public Optional<GithubRepository> findById(long repoId) {
        String sql = """
        SELECT * FROM github_repositories WHERE id = :id
        """;

        return this.jdbcClient.sql(sql)
                .param("id", repoId)
                .query((rs, rowNum) -> {

                    Timestamp ts = rs.getTimestamp("pushed_at");
                    Instant pushedAtInstant = (ts != null) ? ts.toInstant() : null;

                    GithubOwner owner = new GithubOwner(rs.getString("owner_login"));

                    return new GithubRepository(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("url"),
                            rs.getString("language"),
                            rs.getInt("open_tickets"),
                            rs.getInt("stars"),
                            pushedAtInstant,
                            owner
                    );
                })
                .optional();
    }

    public void delete(long repoId) {
        String sql = """
        DELETE FROM github_repositories WHERE id = :id
        """;
        this.jdbcClient.sql(sql)
                .param("id", repoId)
                .update();

    }
}