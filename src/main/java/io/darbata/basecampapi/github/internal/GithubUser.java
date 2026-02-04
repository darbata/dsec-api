package io.darbata.basecampapi.github.internal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubUser(
        @JsonProperty("login") String login,
        @JsonProperty("id") long id,
        @JsonProperty("node_id") String nodeId,
        @JsonProperty("avatar_url") String avatarUrl
) { }