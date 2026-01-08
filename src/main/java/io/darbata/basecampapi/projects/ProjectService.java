package io.darbata.basecampapi.projects;

import io.darbata.basecampapi.github.GithubProfileDTO;
import io.darbata.basecampapi.github.GithubRepositoryDTO;
import io.darbata.basecampapi.github.GithubService;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
class ProjectController {
    private final ProjectService projectService;

    ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }


    @GetMapping("/")
    ResponseEntity<?> getProjects(
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "0") int pageNum
    ) {
        try {
            Page<ProjectDTO> dto = projectService.paginatedFetchSortedProjectsByCreated(pageSize, pageNum);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getClass());
            System.out.println(e.getStackTrace());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{title}")
    ResponseEntity<?> getProjectDetailsByTitle(@PathVariable String title) {
        try {
            ProjectDTO dto = projectService.getByTitle(title);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getClass());
            System.out.println(e.getStackTrace());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/")
    ResponseEntity<?> createProject(@AuthenticationPrincipal Jwt jwt, @RequestBody CreateProjectRequest request) {
        try {
            UUID userId = UUID.fromString(jwt.getClaimAsString("sub"));
            ProjectDTO dto = projectService.create(userId, request.title(), request.description(), request.repoId());
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getClass());
            System.out.println(e.getStackTrace());
            return ResponseEntity.badRequest().build();
        }
    }


}

@Service
class ProjectService {
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


    public Page<ProjectDTO> paginatedFetchSortedProjectsByCreated(int pageSize, int pageNum) {
        List<ProjectDTO> projects = projectRepository.paginatedFetchProjectsSortedByCreated(pageSize, pageNum)
                .stream()
                .map(ProjectDTO::fromEntity)
                .toList();

        return new Page<>(
                projects,
                "created_at",
                true,
                pageSize,
                pageNum
        );
    }
}

record GetProjectDetailsByIdRequest(UUID projectId) {}
record GetProjectDetailsByTitleRequest(String projectTitle) {}
record CreateProjectRequest(String title, String description, long repoId) {}
record UpdateProjectDetailsRequest(UUID projectId, String title, String description) {}
record UpdateProjectRepoRequest(UUID projectId, long githubRepoId) {}
record DeleteProjectRequest(UUID projectId) {}

record Page<T> (
        List<T> content,
        String sortCol,
        boolean ascending,
        int pageSize,
        int pageNum
) { }

record ProjectDTO (
    String title,
    String description,
    long githubRepoId,
    String githubRepoName,
    String githubRepoUrl,
    String githubRepoLanguage,
    String ownerUsername
) {
    public static ProjectDTO fromEntity(Project project) {
        return new ProjectDTO(
                project.title(),
                project.description(),
                project.githubRepoId(),
                project.githubRepoName(),
                project.githubRepoUrl(),
                project.githubRepoLanguage(),
                project.ownerUsername()
        );
    }
}

record Project (
    UUID id, // auto gen this
    String title, // unique
    String description,
    long githubRepoId,
    String githubRepoName,
    String githubRepoUrl,
    String githubRepoLanguage,
    UUID ownerId,
    String ownerUsername
) {}

@Repository
class ProjectRepository {
    private final JdbcClient jdbcClient;

    public ProjectRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Optional<Project> findById(UUID projectId) {
        String sql = """
            SELECT * FROM projects WHERE id = :id;
        """;

        return jdbcClient.sql(sql)
                .param("id", projectId)
                .query(Project.class)
                .optional();
    }

    public Optional<Project> findByTitle(String title) {
        String sql = """
            SELECT * FROM projects WHERE title = :title;
        """;

        return jdbcClient.sql(sql)
                .param("title", title)
                .query(Project.class)
                .optional();
    }

    public Project save(Project project) {
        String sql = """
            INSERT INTO projects (title, description, github_repo_id, github_repo_name, 
            github_repo_url, github_repo_language, owner_id, owner_username)
            VALUES (:title, :description, :githubRepoId, :githubRepoName, 
            :githubRepoUrl, :githubRepoLanguage, :ownerId, :ownerUsername);
        """;

        jdbcClient.sql(sql)
            .param("title", project.title())
            .param("description", project.description())
            .param("githubRepoId", project.githubRepoId())
            .param("githubRepoName", project.githubRepoName())
            .param("githubRepoUrl", project.githubRepoUrl())
            .param("githubRepoLanguage", project.githubRepoLanguage())
            .param("ownerId", project.ownerId())
            .param("ownerUsername", project.ownerUsername())
            .update();

        return project;
    }

    public List<Project> paginatedFetchProjectsSortedByCreated(int pageSize, int pageNum) {
        String sql = """
            SELECT * FROM projects ORDER BY created_at LIMIT :pageSize OFFSET :pageNum;
        """;

        return jdbcClient.sql(sql)
                .param("pageNum", pageNum)
                .param("pageSize", pageSize)
                .query(Project.class)
                .list();
    }

}