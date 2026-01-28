package io.darbata.basecampapi.github;

import java.time.Instant;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// taken directly from Github repo
// map json from Github API to fields in this
@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubRepository (
        @JsonProperty("id") long id,
        @JsonProperty("full_name") String name,
        @JsonProperty("html_url") String url,
        @JsonProperty("language") String language,
        @JsonProperty("open_issues_count") int openTickets,
        @JsonProperty("stargazers_count") int stars,
        @JsonProperty("pushed_at") Instant pushedAt
) {
    @Override
    public String toString() {
        return "GithubRepository{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", language='" + language + '\'' +
                ", openTickets=" + openTickets +
                ", stars=" + stars +
                ", pushedAt=" + pushedAt +
                '}';
    }
}
