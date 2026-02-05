package io.darbata.basecampapi.github.internal.dto.githubproject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ProjectField(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("options") List<FieldValueOption> options
) {}
