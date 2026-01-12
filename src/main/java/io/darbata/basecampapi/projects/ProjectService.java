package io.darbata.basecampapi.projects;

import io.darbata.basecampapi.github.GithubProfileDTO;
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

    ProjectService(ProjectRepository repo, GithubService githubService) {
        this.projectRepository = repo;
        this.githubService = githubService;
    }

    public ProjectDTO getById(UUID id) {
        Project project = projectRepository.findById(id).orElseThrow();
        return ProjectDTO.fromEntity(project);
    }

    public ProjectDTO getByTitle(String title) {
        Project project = projectRepository.findByTitle(title).orElseThrow();
        return ProjectDTO.fromEntity(project);
    }

    public ProjectDTO create(UUID ownerId, String title, String description, long githubRepoId) {
        GithubRepositoryDTO repoDto = githubService.fetchGithubRepositoryById(ownerId, githubRepoId);
        GithubProfileDTO profileDTO = githubService.fetchUserProfile(ownerId).orElseThrow();
        Project project = new Project(
                null,
                title,
                description,
                repoDto.id(),
                repoDto.name(),
                repoDto.url(),
                repoDto.language(),
                ownerId,
                profileDTO.githubAvatarUrl()
        );

        return ProjectDTO.fromEntity(projectRepository.save(project));
    }


    public PageDTO<ProjectDTO> paginatedFetchSortedProjectsByCreated(int pageSize, int pageNum) {
        List<ProjectDTO> projects = projectRepository.paginatedFetchProjectsSortedByCreated(pageSize, pageNum)
                .stream()
                .map(ProjectDTO::fromEntity)
                .toList();

        return new PageDTO<>(
                projects,
                "created_at",
                true,
                pageSize,
                pageNum
        );
    }
}

