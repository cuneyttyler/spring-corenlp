package com.gsu.knowledgebase.model;

/**
 * Created by cnytync on 11/02/2018.
 */
public class CustomProperty {
    private Entity subject;
    private MetaProperty predicate;
    private Entity object;

    public CustomProperty(Entity subject, MetaProperty predicate, Entity object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    public CustomProperty() {

    }

    public Entity getSubject() {
        return subject;
    }

    public void setSubject(Entity subject) {
        this.subject = subject;
    }

    public MetaProperty getPredicate() {
        return predicate;
    }

    public void setPredicate(MetaProperty predicate) {
        this.predicate = predicate;
    }

    public Entity getObject() {
        return object;
    }

    public void setObject(Entity object) {
        this.object = object;
    }
}
