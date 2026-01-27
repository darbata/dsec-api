package io.darbata.basecampapi.projects.internal;

import io.darbata.basecampapi.projects.ProjectService;
import io.darbata.basecampapi.common.PageDTO;
import io.darbata.basecampapi.projects.internal.dto.UserProjectDTO;
import io.darbata.basecampapi.projects.internal.request.CreateCommunityProjectRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
public class CommunityProjectController {
    private final ProjectService projectService;

    CommunityProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/community")
    ResponseEntity<?> getCommunityProjects(
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "0") int pageNum
    ) {
        try {
            PageDTO<UserProjectDTO> dto = projectService.getProjects(pageSize, pageNum, false);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    ResponseEntity<?> getProjectDetailsByTitle(@PathVariable UUID id) {
        try {
            UserProjectDTO dto = projectService.getById(id);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/community")
    ResponseEntity<?> createCommunityProject(@AuthenticationPrincipal Jwt jwt, @RequestBody CreateCommunityProjectRequest request) {
        try {
            String userId = (jwt.getClaimAsString("sub"));
            UserProjectDTO dto = projectService.createCommunityProject(
                    userId, request.title(), request.description(), request.repoId());
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
