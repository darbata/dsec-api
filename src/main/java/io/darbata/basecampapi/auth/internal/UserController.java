package io.darbata.basecampapi.auth.internal;

import io.darbata.basecampapi.auth.AuthService;
import io.darbata.basecampapi.auth.UserDTO;
import io.darbata.basecampapi.auth.internal.request.AuthUserRequest;
import io.darbata.basecampapi.github.GithubExchangeTokenEvent;
import io.darbata.basecampapi.github.GithubRepository;
import io.darbata.basecampapi.github.GithubService;
import io.darbata.basecampapi.github.internal.model.GithubToken;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
class UserController {

    private final AuthService authService;
    private final ApplicationEventPublisher publisher;
    private final GithubService githubService;

    UserController(AuthService authService, ApplicationEventPublisher publisher, GithubService githubService) {
        this.authService = authService;
        this.publisher = publisher;
        this.githubService = githubService;
    }


    @PostMapping("/github/oauth")
    ResponseEntity<?> enableGithubOauth(@AuthenticationPrincipal Jwt jwt, @RequestParam String code) {
        try {
            String userId = jwt.getClaimAsString("sub");
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
            String userId = jwt.getClaimAsString("sub");
            List<GithubRepository> repos = githubService.fetchUserGithubRepositories(userId);
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
                    jwt.getClaimAsString("sub"),
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

    @DeleteMapping("/github/oauth")
    ResponseEntity<?> deleteGithubToken(@AuthenticationPrincipal Jwt jwt) {
        try {
            String userId = jwt.getClaimAsString("sub");
            authService.disconnectGithub(userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            System.err.println(e.getClass().getName());
            System.err.println("Error registering user: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }


}
