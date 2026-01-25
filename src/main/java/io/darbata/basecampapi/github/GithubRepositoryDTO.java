package io.darbata.basecampapi.github;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public record GithubRepositoryDTO (
        long id,
        String name,
        String url,
        String language,
        int openTickets,
        int contributors,
        int stars,
        Date pushedAt
) {
    @Override
    public String toString() {
        return "GithubRepositoryDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", language='" + language + '\'' +
                ", openTickets=" + openTickets +
                ", contributors=" + contributors +
                ", stars=" + stars +
                ", pushedAt=" + pushedAt +
                '}';
    }
}

