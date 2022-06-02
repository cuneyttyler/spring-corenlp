package com.gsu.semantic.model;

/**
 * Created by cnytync on 08/09/2017.
 */
public class Tag {
    private String name;
    private String url;

    public Tag() {
    }

    public Tag(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
