package com.gsu.semantic.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by cnytync on 21/08/2017.
 */
public class Link {
    public Node source;
    public Node target;
    public Double weight;

    public Link() {
    }

    public Link(Node source, Node target, Double weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }

    public Link(ResultSet rs) throws SQLException {
        source = new Node(rs, "s");
        target = new Node(rs, "d");
    }

    public Node getSource() {
        return source;
    }

    public void setSource(Node source) {
        this.source = source;
    }

    public Node getTarget() {
        return target;
    }

    public void setTarget(Node target) {
        this.target = target;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
