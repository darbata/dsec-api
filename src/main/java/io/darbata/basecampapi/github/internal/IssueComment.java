package io.darbata.basecampapi.github.internal;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record IssueComment (
        @JsonProperty("id") long id,
        @JsonProperty("nodeId") String nodeId,
        @JsonProperty("url") String url,
        @JsonProperty("body") String body,
        @JsonProperty("user") GithubUser user,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt
) {}