package io.darbata.basecampapi.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.darbata.basecampapi.github.internal.dto.githubproject.ProjectFieldValues;
import io.darbata.basecampapi.github.internal.dto.githubproject.ProjectItemContent;

public record ProjectItemNode(
        @JsonProperty("id") String id,
        @JsonProperty("__typename") String type,
        @JsonProperty("content") ProjectItemContent content,
        @JsonProperty("fieldValues") ProjectFieldValues fieldValues
) {}
