package io.darbata.basecampapi.discussions.internal.dto;

import io.darbata.basecampapi.discussions.internal.model.UnitTopic;

public record UnitTopicDTO (
    String unitCode,
    String unitSiteUrl,
    String description
) {
    public static UnitTopicDTO fromEntity(UnitTopic topic) {
        return new UnitTopicDTO(topic.unitCode(), topic.unitSiteUrl(), topic.description());
    }
}
