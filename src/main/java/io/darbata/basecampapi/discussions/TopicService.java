package io.darbata.basecampapi.discussions;

import io.darbata.basecampapi.auth.UserDTO;
import io.darbata.basecampapi.auth.UserService;
import io.darbata.basecampapi.common.PageDTO;
import io.darbata.basecampapi.discussions.internal.*;
import io.darbata.basecampapi.discussions.internal.dto.DiscussionDTO;
import io.darbata.basecampapi.discussions.internal.dto.UnitTopicDTO;
import io.darbata.basecampapi.discussions.internal.dto.UnitTopicDetailsDTO;
import io.darbata.basecampapi.discussions.internal.model.Discussion;
import io.darbata.basecampapi.discussions.internal.model.UnitTopic;
import org.springframework.stereotype.Service;

import java.util.*;

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
                    discussion.id(),
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
                    discussion.id(),
                    discussion.parentDiscussionId(),
                    user,
                    discussion.content(),
                    getHierarchicalDiscussions(map, discussion.id()),
                    discussion.createdAt()
            ));
        }
        return dtos;
    }

    public DiscussionDTO createUnitTopicDiscussion(String unitCode, UUID parentDiscussionId, String userId, String content) {
        UnitTopic unitTopic = topicRepository.getUnitTopicByUnitCode(unitCode);

        Discussion discussion = topicRepository.createDiscussion(
                new Discussion(null, unitTopic.id(), parentDiscussionId, userId, content, null));

        UserDTO user = userService.findUserById(discussion.userId());
        return new DiscussionDTO(discussion.id(), discussion.parentDiscussionId(), user, content,
                new ArrayList<>(), discussion.createdAt());
    }

    public DiscussionDTO createDiscussion(UUID topicId, UUID parentDiscussionId, String userId, String content) {
        Discussion discussion = topicRepository.createDiscussion(
                new Discussion(null, topicId, parentDiscussionId, userId, content, null));

        UserDTO user = userService.findUserById(discussion.userId());
        return new DiscussionDTO(discussion.id(), discussion.parentDiscussionId(), user, content,
                new ArrayList<>(), discussion.createdAt());
    }

}

