package io.darbata.basecampapi.discussions.internal.request;

import java.util.UUID;

public record CreateDiscussionRequest(
    UUID parentDiscussionId, // null if new discussion thread
    String content
) { }
