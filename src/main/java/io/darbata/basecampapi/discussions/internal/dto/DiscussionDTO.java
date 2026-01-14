package io.darbata.basecampapi.discussions.internal.dto;

import io.darbata.basecampapi.auth.UserDTO;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record DiscussionDTO (
    UUID parentDiscussionId,
    UserDTO user,
    String content,
    List<DiscussionDTO> discussions,
    OffsetDateTime createdAt
) { }
