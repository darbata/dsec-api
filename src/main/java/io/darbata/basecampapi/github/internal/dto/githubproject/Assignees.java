package io.darbata.basecampapi.github.internal.dto.githubproject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Assignees(@JsonProperty("nodes") List<AssigneeNode> nodes) { }
