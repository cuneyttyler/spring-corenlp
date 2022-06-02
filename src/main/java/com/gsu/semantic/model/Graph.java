package com.gsu.semantic.model;

import java.util.List;

/**
 * Created by cnytync on 21/08/2017.
 */
public class Graph {
    public List<Node> nodes;
    public List<Link> links;
    public Integer id;
    public Integer userId;
    public String name;

    public Graph() {
    }

    public Graph(List<Node> nodes, List<Link> links) {
        this.nodes = nodes;
        this.links = links;
    }

    public Graph(Integer id, List<Node> nodes, List<Link> links) {
        this.id = id;
        this.nodes = nodes;
        this.links = links;
    }

    public Graph(Integer id, String name, List<Node> nodes, List<Link> links) {
        this.id = id;
        this.name = name;
        this.nodes = nodes;
        this.links = links;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
