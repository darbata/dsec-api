package io.darbata.basecampapi.auth;

import io.darbata.basecampapi.auth.internal.*;
import io.darbata.basecampapi.auth.internal.model.User;
import io.darbata.basecampapi.auth.internal.request.AuthUserRequest;
import io.darbata.basecampapi.github.GithubService;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository repo;
    private final GithubService githubService;

    AuthService(UserRepository repo, GithubService githubService) {
        this.repo = repo;
        this.githubService = githubService;
    }

    public UserDTO save(AuthUserRequest request) {
        User savedUser = persistUser(request);
        return new UserDTO(savedUser.email(), savedUser.displayName(), savedUser.discordDisplayName(),
                savedUser.avatarUrl(), githubService.validateUserToken(request.id()));
    }

    public void disconnectGithub(String userId) {
        githubService.revokeToken(userId);
    }

    protected User persistUser(AuthUserRequest request) {
        User user = new User(request.id(), request.email(), request.displayName(),
                request.discordDisplayName(), request.avatarUrl());
        return repo.save(user);
    }
}
