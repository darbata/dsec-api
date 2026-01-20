package io.darbata.basecampapi.auth.internal;

import io.darbata.basecampapi.auth.AuthService;
import io.darbata.basecampapi.auth.UserDTO;
import io.darbata.basecampapi.auth.internal.request.AuthUserRequest;
import io.darbata.basecampapi.github.GithubExchangeTokenEvent;
import io.darbata.basecampapi.github.GithubRepositoryDTO;
import io.darbata.basecampapi.github.GithubService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    ResponseEntity<?> enableGithubOauth(@AuthenticationPrincipal Jwt jwt, @RequestParam String code) {
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

    @GetMapping("/github/repos")
    ResponseEntity<?> fetchUserGithubRepositories(@AuthenticationPrincipal Jwt jwt) {
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
            AuthUserRequest request = new AuthUserRequest(
                    UUID.fromString(jwt.getClaimAsString("sub")),
                    jwt.getClaimAsString("email"),
                    jwt.getClaimAsString("preferred_username"),
                    jwt.getClaimAsString("custom:discord_display_name"), // default null
                    jwt.getClaimAsString("custom:avatar_url") // default null
            );
            UserDTO dto = authService.save(request);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            System.err.println(e.getClass().getName());
            System.err.println("Error registering user: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/github/token")
    ResponseEntity<?> deleteGithubToken(@AuthenticationPrincipal Jwt jwt) {
        try {
            UUID userId = UUID.fromString(jwt.getClaimAsString("sub"));
            authService.disconnectGithub(userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            System.err.println(e.getClass().getName());
            System.err.println("Error registering user: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }


}
