package io.darbata.basecampapi.auth;

import io.darbata.basecampapi.auth.internal.UserRepository;
import io.darbata.basecampapi.auth.internal.model.User;
import io.darbata.basecampapi.cloud.CloudService;
import io.darbata.basecampapi.github.GithubService;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final GithubService githubService;
    private final CloudService cloudService;

    public UserService(UserRepository userRepository, GithubService githubService, CloudService cloudService) {
        this.userRepository = userRepository;
        this.githubService = githubService;
        this.cloudService = cloudService;
    }

    public UserDTO findUserById(String id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return null;
        boolean githubConnected = githubService.validateUserToken(id);
        return new UserDTO(user.email(), user.displayName(), user.discordDisplayName(),
               user.avatarUrl(), githubConnected);
    }

    public String updateAvatarUrlWithUpload(String id, String type, String contentType) {
        String extension = "";

        if (type.equals("image/jpeg")) {
            extension = ".jpg";
        } else if (type.equals("image/png")) {
            extension = ".png";
        } else {
            throw new RuntimeException("Invalid object type");
        }

        String putUrl = cloudService.createUserAvaterPutUrl(id, extension, contentType);

        System.out.println(putUrl);

        return putUrl;
    }
}