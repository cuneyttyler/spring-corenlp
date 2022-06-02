package com.gsu.knowledgebase.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by cnytync on 02/03/2018.
 */
public class UserEntityCategory {
    private Long id;
    private Long userEntityId;
    private Long categoryId;
    private String categoryName;

    public UserEntityCategory() {

    }

    public UserEntityCategory(ResultSet rs) throws SQLException {
        id = rs.getLong("uec.id");
        userEntityId = rs.getLong("uec.user_entity_id");
        categoryId = rs.getLong("uec.category_id");
        categoryName = rs.getString("c.name");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserEntityId() {
        return userEntityId;
    }

    public void setUserEntityId(Long userEntityId) {
        this.userEntityId = userEntityId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
