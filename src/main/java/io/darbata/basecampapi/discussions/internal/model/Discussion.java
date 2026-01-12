package io.darbata.basecampapi.discussions.internal.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record Discussion (
    UUID id,
    UUID topicId,
    UUID parentDiscussionId,
    UUID userId,
    String content,
    LocalDateTime createdAt
) { }
