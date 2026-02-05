package io.darbata.basecampapi.projects;

import io.darbata.basecampapi.auth.UserDTO;
import io.darbata.basecampapi.auth.UserService;
import io.darbata.basecampapi.github.GithubRepository;
import io.darbata.basecampapi.common.PageDTO;
import io.darbata.basecampapi.github.GithubService;
import io.darbata.basecampapi.github.IssueStatus;
import io.darbata.basecampapi.github.internal.IssueComment;
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

    // TODO: FIX CIRCULAR DEPENDENCY
    ProjectService(ProjectRepository repo, @Lazy UserService userService, @Lazy GithubService githubService) {
        this.projectRepository = repo;
        this.userService = userService;
        this.githubService = githubService;
    }

    public UserProjectDTO getById(String callerId, UUID id) throws Exception {
        Project project = projectRepository.findById(id).orElseThrow(() -> new Exception("no project with this id"));
        GithubRepository repo = githubService.findById(callerId, project.getRepoId());
        UserDTO user = userService.findUserById(project.getOwnerId());
        return UserProjectDTO.from(project, user, repo);
    }

    public UserProjectDTO createCommunityProject(String ownerId, String title, String description, long githubRepoId) {
        GithubRepository repo = githubService.findById(ownerId, githubRepoId);
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
        GithubRepository repo = githubService.findById(callerId, project.getRepoId());
        UserDTO user = userService.findUserById(project.getOwnerId());
        return FeaturedProjectDTO.from(project, user, repo);
    }

    public FeaturedProjectDTO createFeaturedProject(
            String callerId, String title, String tagline, String bannerUrl, String description, long githubRepoId
    ) {
        GithubRepository repo = githubService.findById(callerId, githubRepoId);
        Project project = projectRepository.save(Project.createFeaturedProject(
                title, tagline, description, bannerUrl, githubRepoId, repo.owner().login()
        ));
        UserDTO user = userService.findUserById(project.getOwnerId());
        return FeaturedProjectDTO.from(project, user, repo);
    }

    public void assignProjectIssueToUser(String userId, UUID projectId, int issueNumber) throws Exception {
        Project project = projectRepository.findById(projectId).orElseThrow();
        String message = "Assigned issue to self - Basecamp";
        githubService.createIssueComment(userId, project.getRepoId(), issueNumber, message);
        githubService.assignIssueToSelf(userId, project.getRepoId(), issueNumber);
        githubService.updateItemStatus(project.getGithubProjectNum(), issueNumber, IssueStatus.IN_PROGRESS);
    }
}

