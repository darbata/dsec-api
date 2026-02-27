package io.darbata.basecampapi.projects.internal.model;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public class Project {

    private UUID id;
    private String ownerId;
    private long repoId;

    private OffsetDateTime createdAt;
    private Boolean featured;
    private String title;
    private String tagline;
    private String description;
    private String bannerUrl;

    private int githubProjectNum; // ProjectV2 number


    private Project(String title, String description, String ownerId, long repoId) {
        this.id = null;
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        this.description = Objects.requireNonNull(description, "Description cannot be null");
        this.tagline = "";
        this.bannerUrl = "";
        this.featured = false;
        this.createdAt = null;
        this.ownerId = Objects.requireNonNull(ownerId, "OwnerId cannot be null");
        this.repoId = repoId;
        this.githubProjectNum = 0;
    }

    public static Project createCommunityProject(String title, String description, String ownerId, long repoId) {
        return new Project(title, description, ownerId, repoId);
    }

    public static Project createFeaturedProject(
            String title, String tagline, String description, String bannerUrl, long repoId,
            String githubOwnerName
    ) {
        Project project = new Project(title, description, "system", repoId);
        project.setFeatured(true);
        project.setTagline(tagline);
        project.setBannerUrl(bannerUrl);
        return project;
    }

    public static Project load(
            UUID id, String title, String description, String tagline, String bannerUrl, boolean featured,
            OffsetDateTime createdAt, String ownerId, long repoId, int githubProjectNum
    ) {
        Project project = new Project(title, description, ownerId, repoId);
        project.setId(id);
        project.setCreatedAt(createdAt);
        project.setFeatured(featured);
        project.setTagline(tagline);
        project.setBannerUrl(bannerUrl);
        project.setGithubProjectNum(githubProjectNum);
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
    public int getGithubProjectNum() {return githubProjectNum;}
    public void setBannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; }

    // used only by factory methods
    public void setId(UUID id) {this.id = id;}
    private void setOwnerId(String ownerId) {this.ownerId = ownerId;}
    private void setRepoId(long repoId) {this.repoId = repoId;}
    private void setFeatured(Boolean featured) { this.featured = featured; }
    private void setTagline(String tagline) { this.tagline = tagline; }
    private void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    private void setGithubProjectNum(int githubProjectNum) {this.githubProjectNum = githubProjectNum;}
}