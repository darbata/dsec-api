package io.darbata.basecampapi.projects.internal;

import io.darbata.basecampapi.projects.ProjectService;
import io.darbata.basecampapi.common.PageDTO;
import io.darbata.basecampapi.projects.internal.dto.ProjectDTO;
import io.darbata.basecampapi.projects.internal.model.FeaturedProjectDTO;
import io.darbata.basecampapi.projects.internal.request.CreateCommunityProjectRequest;
import io.darbata.basecampapi.projects.internal.request.CreateFeaturedProjectRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;

    ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }


    @GetMapping("/community")
    ResponseEntity<?> getCommunityProjects(
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "0") int pageNum
    ) {
        try {
            PageDTO<ProjectDTO> dto = projectService.getProjects(pageSize, pageNum, false);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/featured")
    ResponseEntity<?> getFeaturedProjects(
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "0") int pageNum
    ) {
        try {
            PageDTO<ProjectDTO> dto = projectService.getProjects(pageSize, pageNum, true);
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

    @PostMapping("/community")
    ResponseEntity<?> createCommunityProject(@AuthenticationPrincipal Jwt jwt, @RequestBody CreateCommunityProjectRequest request) {
        try {
            String userId = (jwt.getClaimAsString("sub"));
            ProjectDTO dto = projectService.create(userId, request.title(), request.description(), request.repoId());
            System.out.println(dto.repo());
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/featured")
    ResponseEntity<?> createFeaturedProject(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CreateFeaturedProjectRequest request
    ) {
        try {
            FeaturedProjectDTO dto = projectService.createFeaturedProject();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }



}
