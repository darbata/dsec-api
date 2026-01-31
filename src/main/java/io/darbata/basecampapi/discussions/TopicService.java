package io.darbata.basecampapi.discussions;

import io.darbata.basecampapi.auth.UserDTO;
import io.darbata.basecampapi.auth.UserService;
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
        this.userService = userService; }

    public UnitTopicDTO createUnitTopic(String unitCode, String unitSiteUrl, String description) {
        UnitTopic topic = topicRepository.createUnitTopic(new UnitTopic(null, unitCode, unitSiteUrl, description));
        return new UnitTopicDTO(topic.unitCode(), topic.unitSiteUrl(), topic.description());
    }

    public List<UnitTopicDTO> getUnitTopics() {
        return topicRepository.getUnitTopics()
                .stream().map(UnitTopicDTO::fromEntity).toList();
    }


    public UnitTopicDetailsDTO getUnitTopicDetailsWithRootDiscussions(String unitCode) {
        UnitTopic unitTopic = topicRepository.getUnitTopicByUnitCode(unitCode);
        List<DiscussionDTO> discussions = topicRepository.getRootDiscussionsByUnitCode(unitCode)
            .stream()
            .map((discussion) -> {
            UserDTO user = userService.findUserById(discussion.userId());
            return DiscussionDTO.fromEntity(user, discussion);
        }).toList();
        return new UnitTopicDetailsDTO(UnitTopicDTO.fromEntity(unitTopic), discussions);
    }

    // get the unit topic full details as well as hierarchical discussions object
    public DiscussionThreadDTO getDiscussionThread(UUID discussionId) {
        Discussion rootDiscussion = topicRepository.getDiscussionById(discussionId);
        List<Discussion> comments = topicRepository.getComments(rootDiscussion.id());

        Map<UUID, DiscussionDTO> map = new HashMap<>();

        comments.forEach(comment -> {
            UserDTO user = userService.findUserById(comment.userId());
            map.put(comment.id(), DiscussionDTO.fromEntity(user, comment));
        });

        UserDTO rootUser = userService.findUserById(rootDiscussion.userId());
        DiscussionDTO rootDto = DiscussionDTO.fromEntity(rootUser, rootDiscussion);
        map.put(rootDto.id(), rootDto);

        for (Discussion comment : comments) {
            DiscussionDTO child = map.get(comment.id());
            DiscussionDTO parent = map.get(comment.parentDiscussionId());
            if (parent != null) {
                parent.discussions().add(child);
            }
        }
        return new DiscussionThreadDTO(rootDto);
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


    public DiscussionDTO createReplyToDiscussion(String userId, UUID parentDiscussionId, String content) {
        Discussion parent =  topicRepository.getDiscussionById(parentDiscussionId);
        Discussion discussion = topicRepository.createDiscussion(
                new Discussion(null, parent.topicId(), parentDiscussionId, userId, content, null)
        );
        UserDTO user = userService.findUserById(discussion.userId());
        return new DiscussionDTO(discussion.id(), discussion.parentDiscussionId(), user, content,
                new ArrayList<>(), discussion.createdAt());
    }
}

