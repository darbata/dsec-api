package io.darbata.basecampapi.auth;

import io.darbata.basecampapi.auth.internal.*;
import io.darbata.basecampapi.auth.internal.dto.UserDetailsDTO;
import io.darbata.basecampapi.auth.internal.model.User;
import io.darbata.basecampapi.auth.internal.request.AuthUserRequest;
import io.darbata.basecampapi.github.GithubProfileDTO;
import io.darbata.basecampapi.github.GithubService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
public class AuthService {

    private final GithubService githubService;
    private final UserRepository repo;

    AuthService(GithubService githubService, UserRepository repo) {
        this.githubService = githubService;
        this.repo = repo;
    }

    public UserDetailsDTO save(AuthUserRequest request) {
        User savedUser = persistUser(request);

        Optional<GithubProfileDTO> profile = githubService.fetchUserProfile(savedUser.id());

        UserDTO userDto = new UserDTO(
                savedUser.email(),
                savedUser.name()
        );

        return new UserDetailsDTO(userDto, profile);
    }

    @Transactional
    protected User persistUser(AuthUserRequest request) {
        User user = new User(request.id(), request.email(), request.name());
        return repo.save(user);
    }

}


