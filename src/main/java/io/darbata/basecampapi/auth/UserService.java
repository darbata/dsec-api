package io.darbata.basecampapi.auth;

import io.darbata.basecampapi.auth.internal.UserRepository;
import io.darbata.basecampapi.auth.internal.model.User;
import io.darbata.basecampapi.github.GithubService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final GithubService githubService;

    public UserService(UserRepository userRepository, GithubService githubService) {
        this.userRepository = userRepository;
        this.githubService = githubService;
    }

    public UserDTO findUserById(UUID id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return null;
        boolean githubConnected = githubService.isGithubConnected(user.id());
        return new UserDTO(user.email(), user.displayName(), user.discordDisplayName(),
               user.avatarUrl(), githubConnected);
    }
}