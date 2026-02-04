package io.darbata.basecampapi.github.internal.dto.githubproject;

import io.darbata.basecampapi.github.ProjectItemNode;

import java.util.List;

public record ProjectItems(List<ProjectItemNode> nodes) { }
