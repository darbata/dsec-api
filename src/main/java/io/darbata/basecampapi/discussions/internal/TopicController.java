package io.darbata.basecampapi.discussions.internal;

import io.darbata.basecampapi.common.PageDTO;
import io.darbata.basecampapi.discussions.DiscussionThreadDTO;
import io.darbata.basecampapi.discussions.TopicService;
import io.darbata.basecampapi.discussions.internal.dto.DiscussionDTO;
import io.darbata.basecampapi.discussions.internal.dto.UnitTopicDTO;
import io.darbata.basecampapi.discussions.internal.dto.UnitTopicDetailsDTO;
import io.darbata.basecampapi.discussions.internal.model.Discussion;
import io.darbata.basecampapi.discussions.internal.request.CreateDiscussionRequest;
import io.darbata.basecampapi.discussions.internal.request.CreateUnitTopicRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/topics")
class TopicController {

    private final TopicService topicService;

    TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping("/{unitCode}")
    public ResponseEntity<?> getTopicDetails(@PathVariable String unitCode) {
        UnitTopicDetailsDTO dto = topicService.getUnitTopicDetailsWithRootDiscussions(unitCode);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/discussions/{discussionId}")
    public ResponseEntity<?> getDiscussionThread(@PathVariable UUID discussionId) {
        DiscussionThreadDTO dto = topicService.getDiscussionThread(discussionId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/discussions/{discussionId}")
    public ResponseEntity<?> replyToDiscussion(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CreateDiscussionRequest request
    ) {
        String id = jwt.getClaimAsString("sub");
        DiscussionDTO dto = topicService.createReplyToDiscussion(id, request.parentDiscussionId(), request.content());
        return ResponseEntity.ok(dto);

    }

    @GetMapping("/units")
    public ResponseEntity<?> getUnitTopics()
    {
        List<UnitTopicDTO> dto = topicService.getUnitTopics();
        return  ResponseEntity.ok(dto);

    }

    @PostMapping("/{unit}")
    public ResponseEntity<?> createUnitTopicDiscussion(
            @AuthenticationPrincipal Jwt jwt, @PathVariable String unit, @RequestBody CreateDiscussionRequest request)
    {
        String userId = jwt.getClaimAsString("sub");
        DiscussionDTO dto = topicService.createUnitTopicDiscussion(
                unit, request.parentDiscussionId(), userId, request.content());
        return  ResponseEntity.ok(dto);
    }
}