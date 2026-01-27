package io.darbata.basecampapi.projects;

import io.darbata.basecampapi.auth.UserDTO;
import io.darbata.basecampapi.auth.UserService;
import io.darbata.basecampapi.github.GithubRepository;
import io.darbata.basecampapi.github.GithubAPIService;
import io.darbata.basecampapi.common.PageDTO;
import io.darbata.basecampapi.github.GithubService;
import io.darbata.basecampapi.projects.internal.dto.UserProjectDTO;
import io.darbata.basecampapi.projects.internal.model.Project;
import io.darbata.basecampapi.projects.internal.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final GithubAPIService githubAPIService;
    private final UserService userService;
    private final GithubService githubService;

    ProjectService(ProjectRepository repo, GithubAPIService githubAPIService,
                   UserService userService, GithubService githubService
    ) {
        this.projectRepository = repo;
        this.githubAPIService = githubAPIService;
        this.userService = userService;
        this.githubService = githubService;
    }

    public UserProjectDTO getById(UUID id) {
        // TODO: use installation app token
        Project project = projectRepository.findById(id).orElseThrow();
        UserDTO user = userService.findUserById(project.getOwnerId());
        GithubRepository repo = githubService.findById(project.getOwnerId(), project.getRepoId());
        return UserProjectDTO.fromEntity(project, user, repo);
    }

    public UserProjectDTO createCommunityProject(String ownerId, String title, String description, long githubRepoId) {
        GithubRepository repo = githubAPIService.fetchGithubRepositoryById(ownerId, githubRepoId);
        Project project = projectRepository.save(Project.createCommunityProject(ownerId, title, description, githubRepoId));
        UserDTO user = userService.findUserById(project.getOwnerId());
        return UserProjectDTO.fromEntity(project, user, repo);
    }

    public PageDTO<UserProjectDTO> getAllCommunityProjects(int pageSize, int pageNum) {
        return null;
    }
}

