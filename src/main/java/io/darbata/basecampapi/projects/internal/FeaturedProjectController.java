package io.darbata.basecampapi.projects.internal;

import io.darbata.basecampapi.common.PageDTO;
import io.darbata.basecampapi.github.GithubRepository;
import io.darbata.basecampapi.github.GithubService;
import io.darbata.basecampapi.github.ProjectItemDTO;
import io.darbata.basecampapi.projects.FeaturedProjectDTO;
import io.darbata.basecampapi.projects.GithubProject;
import io.darbata.basecampapi.projects.ProjectService;
import io.darbata.basecampapi.projects.internal.request.CreateFeaturedProjectRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects/featured")
class FeaturedProjectController {

    private final ProjectService projectService;
    private final GithubService githubService;

    FeaturedProjectController(ProjectService projectService, GithubService githubService) {
        this.projectService = projectService;
        this.githubService = githubService;
    }

    @GetMapping("")
    public ResponseEntity<?> getFeaturedProjects(@RequestParam int pageSize, @RequestParam int pageNum) {
        PageDTO<FeaturedProjectDTO> dto = projectService.getAllFeaturedProjects(pageSize, pageNum);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFeaturedProject(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        String callerId = jwt.getClaimAsString("sub");
        FeaturedProjectDTO dto = projectService.getFeaturedProjectById(callerId, id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/dsec")
    public ResponseEntity<?> fetchOrganisationRepositories() {
        List<GithubRepository> dto = githubService.fetchOrganisationRepositories();
        return ResponseEntity.ok(dto);
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createFeaturedProject(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CreateFeaturedProjectRequest request
    ) {
        jwt.getClaimAsString("sub");
        FeaturedProjectDTO dto = projectService.createFeaturedProject(request.title(), request.tagline(), request.description(), request.repoId());
        return ResponseEntity.ok(dto);

    }

    @PutMapping("/{id}/banner")
    public ResponseEntity<?> updateFeaturedProjectBanner(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id,
            @RequestParam String type
    ) {
        String uploadUrl = projectService.uploadBanner(id, type);
        return ResponseEntity.ok(uploadUrl);
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<?> getFeaturedProjectItems(@PathVariable UUID id) {
        List<ProjectItemDTO> dto = githubService.fetchProjectItems(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{projectId}/{issueNumber}")
    public ResponseEntity<?> assignProjectIssueToUser(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID projectId,
            @PathVariable int issueNumber
    ) {
        String userId = jwt.getClaimAsString("sub");
        projectService.assignProjectIssueToUser(userId, projectId, issueNumber);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/organisation/projects")
    public ResponseEntity<?> getKanbans() {
        List<GithubProject> dto = githubService.getKanbans();
        return ResponseEntity.ok(dto);
    }
}