package io.darbata.basecampapi.github.internal.dto.githubproject;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubOrganization(
        @JsonProperty("projectV2") ProjectV2 projectV2
) {}
