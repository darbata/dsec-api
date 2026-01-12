package io.darbata.basecampapi.discussions;

public record CreateUnitTopicRequest(
    String unitCode,
    String unitSiteUrl,
    String description
) { }
