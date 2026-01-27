package io.darbata.basecampapi.auth;

import io.darbata.basecampapi.auth.internal.UserRepository;
import io.darbata.basecampapi.auth.internal.model.User;
import io.darbata.basecampapi.github.GithubAPIService;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final GithubAPIService githubAPIService;

    public UserService(UserRepository userRepository, GithubAPIService githubAPIService) {
        this.userRepository = userRepository;
        this.githubAPIService = githubAPIService;
    }

    public UserDTO findUserById(String id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return null;
        boolean githubConnected = githubAPIService.isGithubConnected(user.id());
        return new UserDTO(user.email(), user.displayName(), user.discordDisplayName(),
               user.avatarUrl(), githubConnected);
    }
}