package io.darbata.basecampapi.github.internal.dto.githubproject;

public record AssigneeNode(
        String login,
        String name,
        String avatarUrl
) { }