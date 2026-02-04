package io.darbata.basecampapi.github;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubOwner(@JsonProperty("login") String login) {}