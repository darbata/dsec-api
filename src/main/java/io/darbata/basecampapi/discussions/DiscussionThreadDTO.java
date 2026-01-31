package io.darbata.basecampapi.discussions;

import io.darbata.basecampapi.discussions.internal.dto.DiscussionDTO;

public record DiscussionThreadDTO(
        DiscussionDTO rootDiscussion
) { }
