package io.darbata.basecampapi.discussions.internal;

import java.util.UUID;

public record CreateTopicDiscussionRequest(
    UUID topicId,
    UUID parentDiscussionId,
    String content
) { }
