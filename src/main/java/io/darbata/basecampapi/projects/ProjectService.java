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
        Project savedProject = projectRepository.save(project);
        UserDTO user = userService.findUserById(project.ownerId());
        return fromEntity(savedProject, user);
    }


    public PageDTO<ProjectDTO> getProjects(int pageSize, int pageNum, boolean featured) {

        List<Project> projects = projectRepository.getCommunityProjects(pageSize, pageNum);
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

    public ProjectDTO createFeaturedProject(
            String userId, String title, String tagline, String description, String bannerUrl, long repoId
    ) {
        // fetch current state of github repository
        GithubRepositoryDTO repo = githubService.fetchGithubRepositoryById(userId, repoId);

        // save to database
        Project project = new Project(null, true, title, tagline, description, bannerUrl, repo.id(),
                repo.name(), repo.url(), repo.language(), repo.openTickets(), repo.contributors(), repo.stars(),
                repo.pushedAt(), "system", null);
        projectRepository.save(project);

        // create dto and return
    }

    private ProjectDTO fromEntity(Project project, UserDTO user) {
        GithubRepositoryDTO repo = new GithubRepositoryDTO(
                project.githubRepoId(),
                project.githubRepoName(),
                project.githubRepoUrl(),
                project.githubRepoLanguage(),
                project.githubRepoOpenTickets(),
                project.githubRepoContributors(),
                project.githubRepoStars(),
                project.githubRepoPushedAt()
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

