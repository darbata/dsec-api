package io.darbata.basecampapi.github;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubProfileDTO (long githubId, String githubUsername, String githubUrl, String githubAvatarUrl) {

    @JsonCreator
    public GithubProfileDTO(
            @JsonProperty("id") long githubId,
            @JsonProperty("login") String githubUsername,
            @JsonProperty("url") String githubUrl,
            @JsonProperty("avatar_url") String githubAvatarUrl
    ) {
        this.githubId = githubId;
        this.githubUsername = githubUsername;
        this.githubUrl = githubUrl;
        this.githubAvatarUrl = githubAvatarUrl;
    }
}
