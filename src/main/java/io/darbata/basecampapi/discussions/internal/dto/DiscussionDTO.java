package io.darbata.basecampapi.discussions.internal.dto;

import io.darbata.basecampapi.auth.UserDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record DiscussionDTO (
    UUID parentDiscussionId,
    UserDTO user,
    String content,
    List<DiscussionDTO> discussions,
    LocalDateTime createdAt
) { }
