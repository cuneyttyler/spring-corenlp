package com.gsu.knowledgebase.model;

import com.gsu.common.util.DateUtils;
import org.joda.time.DateTime;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by cnytync on 02/03/2018.
 */
public class UserEntity {
    private Long id;
    private Long userId;
    private Long entityId;
    private String note;
    private DateTime crDate;
    private Entity entity;

    public UserEntity() {
    }

    public UserEntity(Entity entity) {
        this.entity = entity;
        this.id = entity.getUserEntityId();
        this.note = entity.getNote();
    }


    public UserEntity(ResultSet rs) throws SQLException{
        id=rs.getLong("ue.id");
        userId = rs.getLong("ue.user_id");
        entityId = rs.getLong("ue.entity_id");
        note = rs.getString("ue.note");
        crDate = DateUtils.fromResultSet(rs,"ue.cr_date");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public DateTime getCrDate() {
        return crDate;
    }

    public void setCrDate(DateTime crDate) {
        this.crDate = crDate;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}
