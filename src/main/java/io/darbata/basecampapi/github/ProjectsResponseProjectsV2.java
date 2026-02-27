package io.darbata.basecampapi.github;

import io.darbata.basecampapi.projects.GithubProject;

import java.util.List;

public record ProjectsResponseProjectsV2(
        List<GithubProject> nodes
) { }
