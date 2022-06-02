package com.gsu.entitylinker.model;

import java.io.Serializable;

/**
 * Created by cnytync on 28/12/15.
 */
public class Triple implements Serializable {
    String subject;
    String relation;
    String object;
    double confidence;

    public Triple(String subject, String relation, String object, double confidence) {
        this.subject = subject;
        this.relation = relation;
        this.object = object;
        this.confidence = confidence;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
}
