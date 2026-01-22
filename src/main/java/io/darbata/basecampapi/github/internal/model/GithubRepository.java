package io.darbata.basecampapi.github.internal.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubRepository (
        @JsonProperty("id") long id,
        @JsonProperty("full_name") String name,
        @JsonProperty("html_url") String url,
        @JsonProperty("language") String language
) {}
