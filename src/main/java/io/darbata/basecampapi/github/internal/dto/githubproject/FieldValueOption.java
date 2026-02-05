package io.darbata.basecampapi.github.internal.dto.githubproject;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FieldValueOption(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name
) { }
