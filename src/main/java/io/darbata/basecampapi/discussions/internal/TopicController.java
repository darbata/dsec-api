package io.darbata.basecampapi.discussions.internal;

import io.darbata.basecampapi.common.PageDTO;
import io.darbata.basecampapi.discussions.TopicService;
import io.darbata.basecampapi.discussions.internal.dto.DiscussionDTO;
import io.darbata.basecampapi.discussions.internal.dto.UnitTopicDTO;
import io.darbata.basecampapi.discussions.internal.dto.UnitTopicDetailsDTO;
import io.darbata.basecampapi.discussions.internal.request.CreateUnitTopicRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/discussions")
class TopicController {

    private final TopicService topicService;

    TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping("/{unitCode}")
    public ResponseEntity<?> getTopicDetails(@PathVariable String unitCode) {
        // accept unit code
        try {
            UnitTopicDetailsDTO dto = topicService.getUnitTopicDetailsByUnitCode(unitCode);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> createUnitTopic(@RequestBody CreateUnitTopicRequest request) {
        try {
            UnitTopicDTO dto = topicService.createUnitTopic(
                    request.unitCode(), request.unitSiteUrl(), request.description());
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/units")
    public ResponseEntity<?> getUnitTopics(
            @RequestParam(defaultValue = "50") int pageSize, @RequestParam(defaultValue = "0") int pageNum)
    {
        try {
            PageDTO<UnitTopicDTO> dto = topicService.getUnitTopics("unit_code", pageSize, pageNum);
            return  ResponseEntity.ok(dto);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> createTopicDiscussion(
            @AuthenticationPrincipal Jwt jwt, @RequestBody CreateTopicDiscussionRequest request)
    {
        try {
            UUID userId;
            userId = UUID.fromString(jwt.getClaimAsString("sub"));
            DiscussionDTO dto = topicService.createDiscussion(
                    request.topicId(), request.parentDiscussionId(), userId, request.content());
            return  ResponseEntity.ok(dto);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

}