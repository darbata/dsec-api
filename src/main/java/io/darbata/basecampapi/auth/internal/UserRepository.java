package io.darbata.basecampapi.auth.internal;

import io.darbata.basecampapi.auth.internal.model.User;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepository {
    private final JdbcClient jdbcClient;

    UserRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<User> findAll() {

        String sql = """
                SELECT * FROM oauth_users;
        """;

        return jdbcClient.sql(sql)
                .query(User.class)
                .list();

    }

    public Optional<User> findById(UUID id) {
        String sql = """
            SELECT * FROM oauth_users WHERE id = :id;
        """;

        return jdbcClient.sql(sql)
                .param("id", id)
                .query(User.class)
                .optional();
    }

    public User save(User user) {
        String sql = """
        
                INSERT INTO oauth_users (id, email, name)
        VALUES (:id, :email, :name)
        ON CONFLICT (id)
        DO UPDATE SET
                      email = EXCLUDED.email,
                      name =
                EXCLUDED.name;

        """;

        jdbcClient
                .sql(sql)
                .param("id", user.id())
                .param("name", user.name())
                .param("email", user.email())
                .update();

        return user;
    }

    public void deleteById(UUID id) {
        String sql =
                """
            DELETE FROM oauth_users WHERE
                id = :id;
        """;

        jdbcClient.sql(sql)
                .param("id", id)
                .update();
    }

    public User update(User user) {
                String sql = """
            UPDATE oauth_users SET name = :name, email = :email WHERE id = :id;
        """;

        jdbcClient.sql(sql)
                .param("name", user.name())
                .param("email", user.email())
                .param("id", user.id())
                .update();

        return user;
    }
}
