package com.gsu.knowledgebase.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by cnytync on 06/03/2018.
 */
public class UserEntityTopic {
    private Long id;
    private Long userEntityId;
    private Long topicId;
    private String topicName;

    public UserEntityTopic() {

    }

    public UserEntityTopic(ResultSet rs) throws SQLException {
        id = rs.getLong("uet.id");
        userEntityId = rs.getLong("uet.user_entity_id");
        topicId = rs.getLong("uet.topic_id");
        topicName = rs.getString("t.name");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserEntityId() {
        return userEntityId;
    }

    public void setUserEntityId(Long userEntityId) {
        this.userEntityId = userEntityId;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
}
