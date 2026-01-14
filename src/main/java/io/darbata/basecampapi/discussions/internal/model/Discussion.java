package io.darbata.basecampapi.discussions.internal.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record Discussion (
    UUID id,
    UUID topicId,
    UUID parentDiscussionId,
    UUID userId,
    String content,
    OffsetDateTime createdAt
) { }
