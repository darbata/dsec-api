package io.darbata.basecampapi.discussions.internal.dto;

import java.util.List;

public record UnitTopicDetailsDTO (
    UnitTopicDTO unitTopic,
    List<DiscussionDTO> discussions
) { }
