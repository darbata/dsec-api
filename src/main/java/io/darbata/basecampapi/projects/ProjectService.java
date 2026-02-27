package io.darbata.basecampapi.projects;

import io.darbata.basecampapi.auth.UserDTO;
import io.darbata.basecampapi.auth.UserService;
import io.darbata.basecampapi.cloud.CloudService;
import io.darbata.basecampapi.github.GithubRepository;
import io.darbata.basecampapi.common.PageDTO;
import io.darbata.basecampapi.github.GithubService;
import io.darbata.basecampapi.github.IssueStatus;
import io.darbata.basecampapi.projects.internal.dto.UserProjectDTO;
import io.darbata.basecampapi.projects.internal.model.Project;
import io.darbata.basecampapi.projects.internal.ProjectRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    private final GithubService githubService;
    private final UserService userService;
    private CloudService cloudService;

    // TODO: FIX CIRCULAR DEPENDENCY
    ProjectService(ProjectRepository repo, @Lazy UserService userService, @Lazy GithubService githubService, @Lazy CloudService cloudService) {
        this.projectRepository = repo;
        this.userService = userService;
        this.githubService = githubService;
        this.cloudService = cloudService;
    }

    public UserProjectDTO getById(String callerId, UUID id) throws Exception {
        Project project = projectRepository.findById(id).orElseThrow(() -> new Exception("no project with this id"));
        GithubRepository repo = githubService.findUserProjectById(callerId, project.getRepoId());
        UserDTO user = userService.findUserById(project.getOwnerId());
        return UserProjectDTO.from(project, user, repo);
    }

    public UserProjectDTO createCommunityProject(String ownerId, String title, String description, long githubRepoId) {
        GithubRepository repo = githubService.findUserProjectById(ownerId, githubRepoId);
        Project project = projectRepository.save(Project.createCommunityProject(title, description, ownerId, githubRepoId));
        UserDTO user = userService.findUserById(project.getOwnerId());
        return UserProjectDTO.from(project, user, repo);
    }

    public PageDTO<UserProjectDTO> getAllCommunityProjects(int pageSize, int pageNum) {
        List<UserProjectDTO> projects = projectRepository.fetchCommunityProjects(pageSize, pageNum);
        return new PageDTO<>(projects, "pushed_at", false, pageSize, pageNum);
    }

    public PageDTO<FeaturedProjectDTO> getAllFeaturedProjects(int pageSize, int pageNum) {
        List<FeaturedProjectDTO> projects = projectRepository.fetchFeaturedProjects(pageSize, pageNum);
        return new PageDTO<>(projects, "pushed_at", false, pageSize, pageNum);
    }

    public FeaturedProjectDTO getFeaturedProjectById(String callerId, UUID id) throws Exception {
        Project project = projectRepository.findById(id).orElseThrow(() -> new Exception("no project found"));
        GithubRepository repo = githubService.findUserProjectById(callerId, project.getRepoId());
        UserDTO user = userService.findUserById(project.getOwnerId());
        return FeaturedProjectDTO.from(project, user, repo);
    }

    public FeaturedProjectDTO createFeaturedProject(
            String title, String tagline, String description, long githubRepoId
    ) {
        GithubRepository repo = githubService.findFeaturedProjectById(githubRepoId);

        Project project = projectRepository.save(Project.createFeaturedProject(
                title, tagline, description, "", githubRepoId, repo.owner().login()
        ));

        UserDTO user = userService.findUserById(project.getOwnerId());
        return FeaturedProjectDTO.from(project, user, repo);
    }

    public String uploadBanner(UUID projectId, String bannerImageType) {
        String extension = "";

        if (bannerImageType.equals("image/jpeg")) {
            extension = ".jpg";
        } else if (bannerImageType.equals("image/png")) {
            extension = ".png";
        } else {
            throw new RuntimeException("Invalid object type");
        }

        String path = "project/" + projectId + "/banner" + extension;
        String putUrl = cloudService.getPutUrl(path, bannerImageType);
        String bannerUrl = "https://dsec-basecamp-assets.s3.ap-southeast-2.amazonaws.com/" + path;
        this.updateProjectBannerImageUrl(projectId, bannerUrl);

        return putUrl;
    }

    public void assignProjectIssueToUser(String userId, UUID projectId, int issueNumber) throws Exception {
        Project project = projectRepository.findById(projectId).orElseThrow();
        String message = "Assigned issue to self - Basecamp";
        githubService.createIssueComment(userId, project.getRepoId(), issueNumber, message);
        githubService.assignIssueToSelf(userId, project.getRepoId(), issueNumber);
        githubService.updateItemStatus(project.getGithubProjectNum(), issueNumber, IssueStatus.IN_PROGRESS);
    }

    private void updateProjectBannerImageUrl(UUID projectId, String bannerUrl) {
        Project project = projectRepository.findById(projectId).orElseThrow();
        project.setBannerUrl(bannerUrl);
        projectRepository.save(project);
    }
}

