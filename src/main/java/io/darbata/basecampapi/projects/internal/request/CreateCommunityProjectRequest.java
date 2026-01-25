package io.darbata.basecampapi.projects.internal.request;

public record CreateCommunityProjectRequest(String title, String description, long repoId) {
    @Override
    public String toString() {
        return "CreateProjectRequest{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", repoId=" + repoId +
                '}';
    }
}
