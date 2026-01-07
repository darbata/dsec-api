package io.darbata.basecampapi.users;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    // returns who is authenticated?
    // this will use the current token, to update register user or update their details
    // UUID from Cognito will never change, but name and email may
    @PostMapping("/auth")
    ResponseEntity<?> auth(@AuthenticationPrincipal Jwt jwt) {
        try {
            AuthUserRequest request = new AuthUserRequest (
                    UUID.fromString(jwt.getClaimAsString("sub")),
                    jwt.getClaimAsString("email"),
                    jwt.getClaimAsString("name")
            );
            UserDTO dto = userService.save(request);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            System.err.println(e.getClass().getName());
            System.err.println("Error registering user: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

}


@Service
@Transactional
class UserService {
    private final UserRepository repo;

    UserService(UserRepository repo) {
        this.repo = repo;
    }

    public UserDTO save(AuthUserRequest request) {
        User user = new User(request.id(), request.email(), request.name());

        if (repo.findById(request.id()).isPresent()) { // return existing user, updating details
            user = repo.update(user);
        } else { // return new user
            user = repo.save(user);
        }

        return new UserDTO(user.email(), user.name());
    }

}

record UserDTO(
        String email,
        String name
) {}

record User(
        UUID id,
        String email,
        String name
) {}

record AuthUserRequest (
        UUID id,
        String email,
        String name
) {}

@Repository
class UserRepository {
    private final JdbcClient jdbcClient;

    UserRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    List<User> findAll() {

        String sql = """
                SELECT * FROM oauth_users;
        """;

        return jdbcClient.sql(sql)
                .query(User.class)
                .list();

    }

    Optional<User> findById(UUID id) {
        String sql = """
            SELECT * FROM oauth_users WHERE id = :id;
        """;

        return jdbcClient.sql(sql)
                .param("id", id)
                .query(User.class)
                .optional();
    }

    User save(User user) {
        String sql = """
            INSERT INTO oauth_users (id, name, email)
            VALUES (:id, :name, :email);
        """;

        jdbcClient
                .sql(sql)
                .param("id", user.id())
                .param("name", user.name())
                .param("email", user.email())
                .update();

        return user;
    }

    void deleteById(UUID id) {
        String sql = """
            DELETE FROM oauth_users WHERE id = :id;
        """;

        jdbcClient.sql(sql)
                .param("id", id)
                .update();
    }

    User update(User user) {
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


