package io.darbata.basecampapi.auth.internal.dto;

import io.darbata.basecampapi.auth.UserDTO;
import io.darbata.basecampapi.github.GithubProfileDTO;

import java.util.Optional;

public record UserDetailsDTO(
        UserDTO userDTO,
        Optional<GithubProfileDTO> profileDTO
) { }
