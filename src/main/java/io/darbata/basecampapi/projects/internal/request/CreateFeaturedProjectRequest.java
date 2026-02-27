package io.darbata.basecampapi.projects.internal.request;

public record CreateFeaturedProjectRequest(
        String title,
        String tagline,
        String description,
        String bannerImageType,
        long repoId,
        int projectNumber // dsec-hub project number
) {
    @Override
    public String toString() {
        return "CreateFeaturedProjectRequest{" +
                "title='" + title + '\'' +
                ", tagline='" + tagline + '\'' +
                ", description='" + description + '\'' +
                ", bannerImageType='" + bannerImageType + '\'' +
                ", repoId=" + repoId +
                ", projectNumber=" + projectNumber +
                '}';
    }
}
