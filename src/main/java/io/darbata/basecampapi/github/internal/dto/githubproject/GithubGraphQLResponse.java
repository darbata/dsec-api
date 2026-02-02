import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;

import java.time.Instant;
import java.util.List;

public record GithubGraphQLResponse(GraphQLData data) {}

public record GraphQLData(GithubUser user) {}

public record GithubUser(ProjectV2 projectV2) {}

public record ProjectV2(ProjectItems items) { }

public record ProjectItems(List<ProjectItemNode> nodes) { }

public record ProjectItemNode(
        @JsonProperty("id") String id,
        @JsonProperty("__typename") String type,
        @JsonProperty("content") ProjectItemContent content,
        @JsonProperty("fieldValues") ProjectFieldValues fieldValues
) {}

public record ProjectItemContent(
        @JsonProperty ("title") String title,
        @JsonProperty ("url") String url,
        @JsonProperty ("createdAt") Instant createdAt,
        @JsonProperty ("updatedAt") Instant updatedAt,
        @JsonProperty ("labels") ProjectLabels labels
) {}

public record ProjectFieldValues(
        @JsonProperty("nodes") List<FieldValueNode> nodes
) {}

public record FieldValueNode(
        @JsonProperty("name") @Nullable String name, // Will be null for {}
        @JsonProperty("field") @Nullable ProjectField field
) {}

public record ProjectField(
        @JsonProperty("name") String name
) {}

public record ProjectLabels (
        @JsonProperty("nodes") List<LabelNode> nodes
) {}

public record LabelNode(
        @JsonProperty("name") String name,
        @JsonProperty("color") String color
) {}


