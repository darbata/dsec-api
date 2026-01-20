package io.darbata.basecampapi.auth;

import io.darbata.basecampapi.auth.internal.UserRepository;
import io.darbata.basecampapi.auth.internal.model.User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO findUserById(UUID id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return null;
        return null;
    }
}