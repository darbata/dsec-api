package io.darbata.basecampapi.discussions.internal.request;

import java.util.UUID;

public record CreateTopicDiscussionRequest(
    UUID topicId,
    UUID parentDiscussionId,
    String content
) { }
