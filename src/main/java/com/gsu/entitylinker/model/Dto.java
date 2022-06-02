package com.gsu.entitylinker.model;

import java.util.Collection;
import java.util.List;

/**
 * Created by cnytync on 28/12/15.
 */
public class Dto {
    private Collection<Entity> entities;
    private Collection<Triple> triples;

    public Dto(Collection<Entity> entities, Collection<Triple> triples) {
        this.entities = entities;
        this.triples = triples;
    }

    public Collection<Entity> getEntities() {

        return entities;
    }

    public void setEntities(Collection<Entity> entities) {
        this.entities = entities;
    }

    public Collection<Triple> getTriples() {
        return triples;
    }

    public void setTriples(Collection<Triple> triples) {
        this.triples = triples;
    }
}
