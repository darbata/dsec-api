package io.darbata.basecampapi.discussions.internal.model;

import java.util.UUID;

public record UnitTopic (
    UUID id,
    String unitCode,
    String unitSiteUrl,
    String description
) { }
