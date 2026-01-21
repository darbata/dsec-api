package io.darbata.basecampapi.projects;

import io.darbata.basecampapi.auth.UserDTO;
import io.darbata.basecampapi.auth.UserService;
import io.darbata.basecampapi.github.GithubRepositoryDTO;
import io.darbata.basecampapi.github.GithubService;
import io.darbata.basecampapi.common.PageDTO;
import io.darbata.basecampapi.projects.internal.model.Project;
import io.darbata.basecampapi.projects.internal.dto.ProjectDTO;
import io.darbata.basecampapi.projects.internal.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final GithubService githubService;
    private final UserService userService;

    ProjectService(ProjectRepository repo, GithubService githubService, UserService userService) {
        this.projectRepository = repo;
        this.githubService = githubService;
        this.userService = userService;
    }

    public ProjectDTO getById(UUID id) {
        Project project = projectRepository.findById(id).orElseThrow();
        UserDTO user = userService.findUserById(project.ownerId());
        return fromEntity(project, user);
    }

    public ProjectDTO getByTitle(String title) {
        Project project = projectRepository.findByTitle(title).orElseThrow();
        UserDTO user = userService.findUserById(project.ownerId());
        return fromEntity(project, user);
    }

    public ProjectDTO create(String ownerId, String title, String description, long githubRepoId) {
        GithubRepositoryDTO repoDTO = githubService.fetchGithubRepositoryById(ownerId, githubRepoId);
        Project project = new Project(null, title, description, false, repoDTO.id(), repoDTO.name(),
                repoDTO.url(), repoDTO.language(), ownerId, null);
        UserDTO user = userService.findUserById(project.ownerId());
        return fromEntity(project, user);
    }


    public PageDTO<ProjectDTO> getProjects(int pageSize, int pageNum, boolean featured) {

        List<Project> projects = featured
                ? projectRepository.getFeaturedProjects(pageSize, pageNum)
                : projectRepository.getCommunityProjects(pageSize, pageNum);

        List<ProjectDTO> content = projects.stream().map((project) -> {
            UserDTO user = userService.findUserById(project.ownerId());
            return fromEntity(project, user);
        }).toList();

        return new PageDTO<>(
                content,
                "created_at",
                true,
                pageSize,
                pageNum
        );
    }

    public void toggleFeatured(String title) {
        projectRepository.toggleFeatured(title);
    }

    private ProjectDTO fromEntity(Project project, UserDTO user) {
        GithubRepositoryDTO repo = new GithubRepositoryDTO(
                project.githubRepoId(),
                project.githubRepoName(),
                project.githubRepoUrl(),
                project.githubRepoLanguage()
        );
        return new ProjectDTO(
                project.title(),
                project.description(),
                repo,
                user.displayName(),
                user.avatarUrl()
        );
    }


}

