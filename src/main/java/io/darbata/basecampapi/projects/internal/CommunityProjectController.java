package io.darbata.basecampapi.projects.internal;

import io.darbata.basecampapi.common.NotFoundException;
import io.darbata.basecampapi.projects.ProjectService;
import io.darbata.basecampapi.common.PageDTO;
import io.darbata.basecampapi.projects.internal.dto.UserProjectDTO;
import io.darbata.basecampapi.projects.internal.request.CreateCommunityProjectRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
        PageDTO<UserProjectDTO> dto = projectService.getAllCommunityProjects(pageSize, pageNum);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("community/{id}")
    ResponseEntity<?> deleteCommunityProject(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        String userId = jwt.getClaimAsString("sub");
        projectService.deleteUserProject(userId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    ResponseEntity<?> getProjectDetailsByTitle(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) throws NotFoundException {
        String callerId = jwt.getClaimAsString("sub");
        UserProjectDTO dto = projectService.getById(callerId, id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/community")
    ResponseEntity<?> createCommunityProject(@AuthenticationPrincipal Jwt jwt, @RequestBody CreateCommunityProjectRequest request) {
        String userId = (jwt.getClaimAsString("sub"));
        projectService.createCommunityProject(
                userId, request.title(), request.description(), request.repoId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/community/user")
    ResponseEntity<?> getUserProjects(@AuthenticationPrincipal Jwt jwt) {
        String userId = (jwt.getClaimAsString("sub"));
        List<UserProjectDTO> dto = projectService.getUserSharedProjects(userId);
        return ResponseEntity.ok(dto);
    }
}
