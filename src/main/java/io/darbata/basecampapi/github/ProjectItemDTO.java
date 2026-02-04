package io.darbata.basecampapi.github;

import io.darbata.basecampapi.github.internal.dto.githubproject.AssigneeNode;
import io.darbata.basecampapi.github.internal.dto.githubproject.LabelNode;

import java.time.Instant;
import java.util.List;

public record ProjectItemDTO(
        String organisation,
        long projectNumber,
        long issueNumber, // found from url
        String title,
        String body,
        String url,
        String status, // to-do, in progress, done
        List<AssigneeNode> assignees,
        Instant createdAt,
        Instant updatedAt,
        List<LabelNode> labels
) { }