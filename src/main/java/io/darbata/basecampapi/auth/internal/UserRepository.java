package io.darbata.basecampapi.auth.internal;

import io.darbata.basecampapi.auth.internal.model.User;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepository {
    private final JdbcClient jdbcClient;

    UserRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Optional<User> findById(String id) {
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
        
            INSERT INTO oauth_users (id, email, display_name, discord_display_name, avatar_url)
            VALUES (:id, :email, :displayName, :discordDisplayName, :avatarUrl)
            ON CONFLICT (id)
            DO UPDATE SET
                  email = EXCLUDED.email,
                  display_name = EXCLUDED.display_name,
                  discord_display_name = EXCLUDED.discord_display_name,
                  avatar_url = EXCLUDED.avatar_url;
        """;

        jdbcClient
                .sql(sql)
                .param("id", user.id())
                .param("email", user.email())
                .param("displayName", user.displayName())
                .param("discordDisplayName", user.discordDisplayName())
                .param("avatarUrl", user.avatarUrl())
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
}
