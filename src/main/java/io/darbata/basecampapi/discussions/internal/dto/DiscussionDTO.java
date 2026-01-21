package io.darbata.basecampapi.discussions.internal.dto;

import io.darbata.basecampapi.auth.UserDTO;
import io.darbata.basecampapi.discussions.internal.model.Discussion;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record DiscussionDTO (
    UUID id,
    UUID parentDiscussionId,
    UserDTO user,
    String content,
    List<DiscussionDTO> discussions,
    OffsetDateTime createdAt
) {
    public static DiscussionDTO fromEntity(UserDTO user, Discussion discussion) {
        return new DiscussionDTO(discussion.id(), discussion.parentDiscussionId(), user, discussion.content(),
                new ArrayList<>(), discussion.createdAt());
    }
}
