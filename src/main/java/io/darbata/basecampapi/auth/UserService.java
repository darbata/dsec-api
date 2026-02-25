package io.darbata.basecampapi.auth;

import io.darbata.basecampapi.auth.internal.UserRepository;
import io.darbata.basecampapi.auth.internal.model.User;
import io.darbata.basecampapi.cloud.CloudService;
import io.darbata.basecampapi.github.GithubService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

        String path = "user/" + id + "/pfp" + extension;

        String putUrl = cloudService.createUserAvaterPutUrl(path, contentType);
        String avatarUrl = "https://dsec-basecamp-assets.s3.ap-southeast-2.amazonaws.com/" + path;
        cloudService.updateUserAvatarUrl(id, avatarUrl);

        return putUrl;
    }

    public void updateUserDisplayName(String id, String displayName) {
        cloudService.updateUserDisplayName(id, displayName);
    }
}