package io.darbata.basecampapi.github.internal.dto.githubproject;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProjectItemNode(
        @JsonProperty("id") String id,
        @JsonProperty("__typename") String type,
        @JsonProperty("content") ProjectItemContent content,
        @JsonProperty("fieldValues") ProjectFieldValues fieldValues
) {}
