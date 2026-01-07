package io.darbata.basecampapi.auth;

import io.darbata.basecampapi.github.GithubExchangeTokenEvent;
import io.darbata.basecampapi.github.GithubProfileDTO;
import io.darbata.basecampapi.github.GithubRepositoryDTO;
import io.darbata.basecampapi.github.GithubService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
class UserController {

    private final AuthService authService;
    private final ApplicationEventPublisher publisher;
    private final GithubService githubService;

    UserController(AuthService userService, ApplicationEventPublisher publisher, GithubService githubService) {
        this.authService = userService;
        this.publisher = publisher;
        this.githubService = githubService;
    }



    @PostMapping("/github/oauth")
    ResponseEntity<?> enableGithubOauth(@AuthenticationPrincipal Jwt jwt, @RequestParam String code ) {
        try {
            UUID userId = UUID.fromString(jwt.getClaimAsString("sub"));
            publisher.publishEvent(new GithubExchangeTokenEvent(userId, code));
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            System.err.println(e.getClass().getName());
            System.err.println("Error registering user: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/repos")
    ResponseEntity<?> enableGithubOauth(@AuthenticationPrincipal Jwt jwt) {
        try {
            UUID userId = UUID.fromString(jwt.getClaimAsString("sub"));
            List<GithubRepositoryDTO> repos = githubService.fetchUserRepositories(userId);
            return ResponseEntity.ok(repos);
        } catch (Exception e) {
            System.err.println(e.getClass().getName());
            System.err.println("Error registering user: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // returns who is authenticated?
    // this will use the current token, to update register user or update their details
    // UUID from Cognito will never change, but name and email may
    @GetMapping("/auth")
    ResponseEntity<?> fetchUserDetails(@AuthenticationPrincipal Jwt jwt) {
        try {
            AuthUserRequest request = new AuthUserRequest (
                    UUID.fromString(jwt.getClaimAsString("sub")),
                    jwt.getClaimAsString("email"),
                    jwt.getClaimAsString("name")
            );

            UserDetailsDTO dto = authService.save(request);

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
public class AuthService {

    private final GithubService githubService;
    private final UserRepository repo;

    AuthService(GithubService githubService, UserRepository repo) {
        this.githubService = githubService;
        this.repo = repo;
    }

    public UserDetailsDTO save(AuthUserRequest request) {
        User user = new User(request.id(), request.email(), request.name());

        User savedUser = repo.save(user);

        UserDTO userDto = new UserDTO(
                savedUser.email(),
                savedUser.name()
        );

        GithubProfileDTO profile = githubService.fetchUserProfile(user.id());

        return new UserDetailsDTO(userDto, profile);
    }

}

record UserDetailsDTO (
        UserDTO userDTO,
        GithubProfileDTO profileDTO
) { }

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
        INSERT INTO oauth_users (id, email, name)
        VALUES (:id, :email, :name)
        ON CONFLICT (id)
        DO UPDATE SET
                      email = EXCLUDED.email,
                      name = EXCLUDED.name;

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


