package io.darbata.basecampapi.projects.internal.model;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public class Project {

    private final UUID id;
    private final String ownerId;
    private final long repoId;

    private OffsetDateTime createdAt;
    private Boolean featured;
    private String title;
    private String tagline;
    private String description;
    private String bannerUrl;

    private Project(String title, String description, String ownerId, long repoId) {
        this.id = null;
        this.createdAt = null;
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        this.description = Objects.requireNonNull(description, "Description cannot be null");
        this.ownerId = Objects.requireNonNull(ownerId, "OwnerId cannot be null");
        this.repoId = repoId;
        this.tagline = "";
        this.bannerUrl = "";
        this.featured = false;
    }

    public static Project createCommunityProject(String title, String description, String ownerId, long repoId) {
        return new Project(title, description, ownerId, repoId);
    }

    public static Project createFeaturedProject(
            String title, String tagline, String description, String bannerUrl, long repoId
    ) {
        Project project = new Project(title, description, "system", repoId);
        project.setFeatured(true);
        project.setTagline(tagline);
        project.setBannerUrl(bannerUrl);
        return project;
    }

    public static Project load(
            String title, OffsetDateTime createdAt, String description, String ownerId, long repoId, String tagline,
            String bannerUrl, boolean featured
    ) {
        Project project = new Project(title, description, ownerId, repoId);
        project.setCreatedAt(createdAt);
        project.setFeatured(featured);
        project.setTagline(tagline);
        project.setBannerUrl(bannerUrl);
        return project;
    }

    public UUID getId() { return id; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public String getOwnerId() { return ownerId; }
    public long getRepoId() { return repoId; }
    public Boolean isFeatured() { return featured; }
    public String getTitle() { return title; }
    public String getTagline() { return tagline; }
    public String getDescription() { return description; }
    public String getBannerUrl() { return bannerUrl; }

    // used only by factory methods
    private void setFeatured(Boolean featured) { this.featured = featured; }
    private void setTagline(String tagline) { this.tagline = tagline; }
    private void setBannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; }
    private void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}