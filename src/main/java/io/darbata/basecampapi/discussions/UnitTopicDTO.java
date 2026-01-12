package io.darbata.basecampapi.discussions;

public record UnitTopicDTO (
    String unitCode,
    String unitSiteUrl,
    String description
) {
    public static UnitTopicDTO fromEntity(UnitTopic topic) {
        return new UnitTopicDTO(topic.unitCode(), topic.unitSiteUrl(), topic.description());
    }
}
