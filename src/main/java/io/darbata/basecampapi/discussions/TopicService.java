package io.darbata.basecampapi.discussions;

import io.darbata.basecampapi.auth.UserDTO;
import io.darbata.basecampapi.auth.UserService;
import io.darbata.basecampapi.common.PageDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

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
    public ResponseEntity<?> getUnitTopics(@RequestParam int pageSize, @RequestParam int pageNum) {
        try {
            PageDTO<UnitTopicDTO> dto = topicService.getUnitTopics("unit_code", pageSize, pageNum);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

}



@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final UserService userService;

    public TopicService(TopicRepository topicRepository, UserService userService) {
        this.topicRepository = topicRepository;
        this.userService = userService;
    }

    public UnitTopicDTO createUnitTopic(String unitCode, String unitSiteUrl, String description) {
        UnitTopic topic = topicRepository.createUnitTopic(new UnitTopic(null, unitCode, unitSiteUrl, description));
        return new UnitTopicDTO(topic.unitCode(), topic.unitSiteUrl(), topic.description());
    }

    public PageDTO<UnitTopicDTO> getUnitTopics(String sortCol, int pageSize, int pageNumber) {
        List<UnitTopicDTO> topics = topicRepository.getUnitTopics(pageSize, pageNumber)
                .stream().map(UnitTopicDTO::fromEntity).toList();
        return new PageDTO<>(topics, sortCol, true, pageSize, pageNumber);
    }

    // get the unit topic full details as well as hierarchical discussions object
    public UnitTopicDetailsDTO getUnitTopicDetailsByUnitCode(String unitCode) {

        UnitTopic unitTopic = topicRepository.getUnitTopicByUnitCode(unitCode);
        List<Discussion> discussions = topicRepository.getTopicDiscussions(unitTopic.id());

        Map<UUID, List<Discussion>> map = new HashMap<>();

        // get all unique keys
        for (Discussion discussion : discussions) {
            if (!map.containsKey(discussion.id())) {
                map.put(discussion.id(), new ArrayList<>());
            }
        }

        // push all children discussions under them
        for (Discussion discussion : discussions) {
            if (discussion.parentDiscussionId() != null) {
                map.get(discussion.parentDiscussionId()).add(discussion);
            }
        }

        List<DiscussionDTO> discussionDtos = new ArrayList<>();

        for (Discussion discussion : discussions) {
            if (discussion.parentDiscussionId() != null) continue;

            UserDTO user  = userService.findUserById(discussion.userId());
            discussionDtos.add(new DiscussionDTO(
                    null,
                    user,
                    discussion.content(),
                    getHierarchicalDiscussions(map, discussion.id()),
                    discussion.createdAt()
            ));
        }

        UnitTopicDTO unitTopicDTO = new UnitTopicDTO(
                unitTopic.unitCode(),
                unitTopic.unitSiteUrl(),
                unitTopic.description()
        );

        return new UnitTopicDetailsDTO(
                unitTopicDTO,
                discussionDtos
        );
    }

    private ArrayList<DiscussionDTO> getHierarchicalDiscussions (Map<UUID, List<Discussion>> map, UUID parentDiscussionId) {
        if (map.get(parentDiscussionId).isEmpty()) return null;

        ArrayList<DiscussionDTO> dtos = new ArrayList<>();

        for (Discussion discussion : map.get(parentDiscussionId)) {
            UserDTO user = userService.findUserById(discussion.userId());

            dtos.add(new DiscussionDTO(
                    discussion.parentDiscussionId(),
                    user,
                    discussion.content(),
                    getHierarchicalDiscussions(map, discussion.id()),
                    discussion.createdAt()
            ));
        }

        return dtos;
    }

    public DiscussionDTO createDiscussion(UUID topicId, UUID parentDiscussionId, UUID userId, String content) {
        Discussion discussion = topicRepository.createDiscussion(
                new Discussion(null, topicId, parentDiscussionId, userId, content, null));

        UserDTO user = userService.findUserById(discussion.userId());
        return new DiscussionDTO(discussion.parentDiscussionId(), user, content, new ArrayList<>(), discussion.createdAt());
    }

}

record UnitTopic (
    UUID id,
    String unitCode,
    String unitSiteUrl,
    String description
) { }

record Discussion (
    UUID id,
    UUID topicId,
    UUID parentDiscussionId,
    UUID userId,
    String content,
    LocalDateTime createdAt
) { }

record DiscussionDTO (
    UUID parentDiscussionId,
    UserDTO user,
    String content,
    List<DiscussionDTO> discussions,
    LocalDateTime createdAt
) { }

record UnitTopicDetailsDTO (
    UnitTopicDTO  unitTopic,
    List<DiscussionDTO> discussions
) { }

@Repository
class TopicRepository {

    private final JdbcClient jdbcClient;

    TopicRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    UnitTopic createUnitTopic(UnitTopic topic) {
        // generate id for new unit topic
        UUID id =  this.jdbcClient
                .sql("INSERT INTO topics DEFAULT VALUES RETURNING id;")
                .query(UUID.class)
                .single();

        // save to db
        String sql = """
            INSERT INTO unit_topics (id, unit_code, unit_site_url, description)
            VALUES (:id, :unitCode, :unitSiteUrl, :description);
        """;
        jdbcClient.sql(sql)
                .param("id", id)
                .param("unitCode", topic.unitCode())
                .param("unitSiteUrl", topic.unitSiteUrl())
                .param("description", topic.description())
                .update();

        return new UnitTopic(id, topic.unitCode(), topic.unitSiteUrl(), topic.description());
    }

    List<UnitTopic> getUnitTopics(int pageSize, int pageNum) {
        String sql = """
        SELECT * FROM unit_topics LIMIT :pageSize OFFSET :pageNum;
        """;

        return jdbcClient.sql(sql)
                .param("pageNum", pageNum)
                .param("pageSize", pageSize)
                .query(UnitTopic.class)
                .list();
    }

    Discussion createDiscussion(Discussion discussion) {
        String sql = """
            INSERT INTO discussions (topic_id, parent_discussion_id, user_id, content)
            VALUES (:topicId, :parentDiscussionId, :userId, :content) RETURNING (id, topic_id, parent_discussion_id, user_id, content, created_at);
        """;

        return jdbcClient
                .sql(sql)
                .param("topicId", discussion.topicId())
                .param("parentDiscussionId", discussion.parentDiscussionId())
                .param("userId", discussion.userId())
                .param("content", discussion.content())
                .query(Discussion.class)
                .single();
    }

    UnitTopic getUnitTopic(UUID topicId) {
        String sql = """
            SELECT * FROM unit_topics WHERE id = :topicId;
        """;

        return jdbcClient.sql(sql)
                .param("topicId", topicId)
                .query(UnitTopic.class)
                .single();
    }

    UnitTopic getUnitTopicByUnitCode(String unitCode) {
        String sql = """
            SELECT * FROM unit_topics WHERE unit_code = :unitCode;
        """;

        return jdbcClient.sql(sql)
                .param("unitCode", unitCode)
                .query(UnitTopic.class)
                .single();
    }

    List<Discussion> getTopicDiscussions(UUID topicId) {
        String sql = """
            SELECT * FROM discussions WHERE topic_id = :topicId;
        """;

        return jdbcClient.sql(sql)
                .param("topicId", topicId)
                .query(Discussion.class)
                .list();
    }
}

