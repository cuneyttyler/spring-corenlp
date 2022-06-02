package com.gsu.knowledgebase.model;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Topic implements Serializable {
    private Long id;
    private String name;
    private Long userId;
    private List<Category> categories = new ArrayList<>();
    private List<Entity> entities = new ArrayList<>();

    public Topic() {
    }

    public Topic(Long id) {
        this.id = id;
    }

    public Topic(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Topic(ResultSet rs) throws SQLException {
        id = rs.getLong("t.id");
        name = rs.getString("t.name");
        userId = rs.getLong("t.user_id");
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public void addCategory(Category category) {
        categories.add(category);
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    public boolean containsCategory(Long id) {
        boolean contains = false;
        for(Category c: categories){
            if(c.getId().equals(id)) {
                contains = true;
            }
        }

        return contains;
    }

    public int indexOfCategory(Long id) {
        int index = -1;
        for(int i = 0; i < categories.size(); i++){
            Category c = categories.get(i);
            if(c.getId().equals(id)) {
                index = i;
            }
        }

        return index;
    }
}
