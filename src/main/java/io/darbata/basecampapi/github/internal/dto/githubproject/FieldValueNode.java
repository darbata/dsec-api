package io.darbata.basecampapi.github.internal.dto.githubproject;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FieldValueNode(
        @JsonProperty("name") String name,
        @JsonProperty("field") ProjectField field
) {}
