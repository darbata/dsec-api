package io.darbata.basecampapi.github.internal.dto.githubproject;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProjectField(
        @JsonProperty("name") String name
) {}
