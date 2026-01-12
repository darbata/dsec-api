package io.darbata.basecampapi.discussions.internal.request;

public record CreateUnitTopicRequest(
    String unitCode,
    String unitSiteUrl,
    String description
) { }
