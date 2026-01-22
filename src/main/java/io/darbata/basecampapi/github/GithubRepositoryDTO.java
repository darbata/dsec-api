package io.darbata.basecampapi.github;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubRepositoryDTO (
        long id,
        String name,
        String url,
        String language
) {
    @Override
    public String toString() {
        return "GithubRepositoryDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}

