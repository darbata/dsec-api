package io.darbata.basecampapi.discussions;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TopicService {

    private final TopicRepository topicRepository;

    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
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

@Repository
class TopicRepository () {

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
}

