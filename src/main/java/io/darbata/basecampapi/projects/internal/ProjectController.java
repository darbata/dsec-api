package io.darbata.basecampapi.projects.internal;

import io.darbata.basecampapi.projects.ProjectService;
import io.darbata.basecampapi.projects.internal.dto.PageDTO;
import io.darbata.basecampapi.projects.internal.dto.ProjectDTO;
import io.darbata.basecampapi.projects.internal.request.CreateProjectRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
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
            PageDTO<ProjectDTO> dto = projectService.paginatedFetchSortedProjectsByCreated(pageSize, pageNum);
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
