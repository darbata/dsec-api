package io.darbata.basecampapi.github.internal.dto.githubproject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

public record ProjectItemContent(
        @JsonProperty("title") String title,
        @JsonProperty("number") int number,
        @JsonProperty("body") String body,
        @JsonProperty("url") String url,
        @JsonProperty("createdAt") Instant createdAt,
        @JsonProperty("updatedAt") Instant updatedAt,
        @JsonProperty("assignees") Assignees assignees,
        @JsonProperty("labels") ProjectLabels labels
) {}
