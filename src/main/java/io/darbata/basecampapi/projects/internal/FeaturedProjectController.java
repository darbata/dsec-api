package io.darbata.basecampapi.projects.internal;

import io.darbata.basecampapi.common.PageDTO;
import io.darbata.basecampapi.projects.ProjectService;
import io.darbata.basecampapi.projects.internal.request.CreateFeaturedProjectRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/projects/featured")
class FeaturedProjectController {

    private final ProjectService projectService;

    FeaturedProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("")
    public ResponseEntity<?> getFeaturedProjects(@RequestParam int pageSize, @RequestParam int pageNum) {
        try {
            PageDTO<FeaturedProjectDTO> dto = projectService.getAllFeaturedProjects(pageSize, pageNum);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFeaturedProject(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        try {
            String callerId = jwt.getClaimAsString("sub");
            FeaturedProjectDTO dto = projectService.getFeaturedProjectById(callerId, id);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }

    }

    @PostMapping("")
    public ResponseEntity<?> createFeaturedProject(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CreateFeaturedProjectRequest request) {
        try {
            String callerId = jwt.getClaimAsString("sub");
            projectService.createFeaturedProject(
                    callerId, request.title(), request.tagline(), request.bannerUrl(), request.description(),
                    request.repoId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}