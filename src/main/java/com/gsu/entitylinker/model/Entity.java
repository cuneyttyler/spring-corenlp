package com.gsu.entitylinker.model;

/**
 * Created by cnytync on 28/12/15.
 */
public class Entity {
    private String entity;
    private String type;

    public Entity(String entity, String type) {
        this.entity = entity;
        this.type = type;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
