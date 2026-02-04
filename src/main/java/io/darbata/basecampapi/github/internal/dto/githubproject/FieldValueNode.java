package io.darbata.basecampapi.github.internal.dto.githubproject;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;

public record FieldValueNode(
        @JsonProperty("name") @Nullable String name, // Will be null for {}
        @JsonProperty("field") @Nullable ProjectField field
) {}
