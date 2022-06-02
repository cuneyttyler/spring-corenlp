package com.gsu.knowledgebase.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by cnytync on 29/09/2017.
 */
public class AnnotationItem {

    public Long id;
    public String surfaceForm;
    public String types;
    public int offset;
    public String uri;
    public Long entityId;
    public Long referencedEntityId;
    public Entity referencedEntity;

    public AnnotationItem() {

    }

    public AnnotationItem(ResultSet rs) throws SQLException {
        id = rs.getLong("ai.id");
        entityId = rs.getLong("ai.entity_id");
        referencedEntityId = rs.getLong("ai.referenced_entity_id");
        surfaceForm = rs.getString("ai.surface_form");
        types = rs.getString("ai.types");
        offset = rs.getInt("ai.offset");
        uri = rs.getString("ai.uri");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSurfaceForm() {
        return surfaceForm;
    }

    public void setSurfaceForm(String surfaceForm) {
        this.surfaceForm = surfaceForm;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getReferencedEntityId() {
        return referencedEntityId;
    }

    public void setReferencedEntityId(Long referencedEntityId) {
        this.referencedEntityId = referencedEntityId;
    }

    public Entity getReferencedEntity() {
        return referencedEntity;
    }

    public void setReferencedEntity(Entity referencedEntity) {
        this.referencedEntity = referencedEntity;
    }
}
