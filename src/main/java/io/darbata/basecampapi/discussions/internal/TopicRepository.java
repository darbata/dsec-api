package io.darbata.basecampapi.discussions.internal;

import io.darbata.basecampapi.discussions.internal.model.Discussion;
import io.darbata.basecampapi.discussions.internal.model.UnitTopic;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class TopicRepository {

    private final JdbcClient jdbcClient;

    TopicRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public UnitTopic createUnitTopic(UnitTopic topic) {
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

    public List<UnitTopic> getUnitTopics(int pageSize, int pageNum) {
        String sql = """
        SELECT * FROM unit_topics LIMIT :pageSize OFFSET :pageNum;
        """;

        return jdbcClient.sql(sql)
                .param("pageNum", pageNum)
                .param("pageSize", pageSize)
                .query(UnitTopic.class)
                .list();
    }

    public Discussion createDiscussion(Discussion discussion) {
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

    public UnitTopic getUnitTopic(UUID topicId) {
        String sql = """
            SELECT * FROM unit_topics WHERE id = :topicId;
        """;

        return jdbcClient.sql(sql)
                .param("topicId", topicId)
                .query(UnitTopic.class)
                .single();
    }

    public UnitTopic getUnitTopicByUnitCode(String unitCode) {
        String sql = """
            SELECT * FROM unit_topics WHERE unit_code = :unitCode;
        """;

        return jdbcClient.sql(sql)
                .param("unitCode", unitCode)
                .query(UnitTopic.class)
                .single();
    }

    public List<Discussion> getTopicDiscussions(UUID topicId) {
        String sql = """
            SELECT * FROM discussions WHERE topic_id = :topicId;
        """;

        return jdbcClient.sql(sql)
                .param("topicId", topicId)
                .query(Discussion.class)
                .list();
    }
}
