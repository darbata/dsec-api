package io.darbata.basecampapi.github;

import io.darbata.basecampapi.github.internal.dto.githubproject.AssigneeNode;
import io.darbata.basecampapi.github.internal.dto.githubproject.LabelNode;

import java.time.Instant;
import java.util.List;

public record ProjectItemDTO(
        String organisation,
        int projectNumber,
        int issueNumber,
        String title,
        String body,
        String url,
        String status, // to-do, in progress, done
        String statusFieldId,
        List<StatusFieldOption> statusOptions,
        List<AssigneeNode> assignees,
        Instant createdAt,
        Instant updatedAt,
        List<LabelNode> labels
) { }