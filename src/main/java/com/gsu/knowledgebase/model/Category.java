package com.gsu.knowledgebase.model;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Category implements Serializable {
    private Long id;
    private String name;
    private Long userId;
    private Boolean hidden;
    private List<SubCategory> subCategories = new ArrayList<>();
    private List<Entity> entities = new ArrayList<>();

    public Category() {
    }

    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Category(Long id) {
        this.id = id;
    }

    public Category(ResultSet rs) throws SQLException {
        id = rs.getLong("c.id");
        name = rs.getString("c.name");
        userId = rs.getLong("c.user_id");
        hidden = rs.getBoolean("c.hidden");
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

    public List<SubCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<SubCategory> subCategories) {
        this.subCategories = subCategories;
    }

    public void addSubCategory(SubCategory subCategory) {
        subCategories.add(subCategory);
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public boolean containsSubCategory(Long id) {
        boolean contains = false;
        for(SubCategory sc: subCategories){
            if(sc.getId().equals(id)) {
                contains = true;
            }
        }

        return contains;
    }

    public int indexOfSubCategory(Long id) {
        int index = -1;
        for(int i = 0; i < subCategories.size(); i++){
            SubCategory sc = subCategories.get(i);
            if(sc.getId().equals(id)) {
                index = i;
            }
        }

        return index;
    }
}
