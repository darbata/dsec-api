package io.darbata.basecampapi.auth;

import io.darbata.basecampapi.auth.internal.*;
import io.darbata.basecampapi.auth.internal.model.User;
import io.darbata.basecampapi.auth.internal.request.AuthUserRequest;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository repo;

    AuthService(UserRepository repo) {
        this.repo = repo;
    }

    public UserDTO save(AuthUserRequest request) {
        User savedUser = persistUser(request);
        return new UserDTO(savedUser.email(), savedUser.displayName(), savedUser.discordDisplayName(),
                savedUser.avatarUrl());
    }

    protected User persistUser(AuthUserRequest request) {
        User user = new User(request.id(), request.email(), request.displayName(),
                request.discordDisplayName(), request.avatarUrl());
        return repo.save(user);
    }
}
