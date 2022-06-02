package com.gsu.knowledgebase.model;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SubCategory implements Serializable {
    private Long id;
    private Long categoryId;
    private String name;
    private List<Entity> entities = new ArrayList<>();

    public SubCategory(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public SubCategory() {
    }

    public SubCategory(Long id) {
        this.id = id;
    }


    public SubCategory(ResultSet rs) throws SQLException {
        id = rs.getLong("sc.id");
        categoryId = rs.getLong("sc.category_id");
        name = rs.getString("sc.name");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }
}


