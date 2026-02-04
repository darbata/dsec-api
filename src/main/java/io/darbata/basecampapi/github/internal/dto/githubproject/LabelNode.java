package io.darbata.basecampapi.github.internal.dto.githubproject;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LabelNode(
        @JsonProperty("name") String name,
        @JsonProperty("color") String color
) {}
