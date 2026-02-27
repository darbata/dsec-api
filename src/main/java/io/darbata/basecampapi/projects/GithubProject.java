package io.darbata.basecampapi.projects;

public record GithubProject(
        String id,
        String title,
        long number,
        String url,
        boolean closed
) { }