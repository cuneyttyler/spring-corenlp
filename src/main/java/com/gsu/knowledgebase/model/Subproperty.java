package com.gsu.knowledgebase.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by cnytync on 08/05/2017.
 */
public class Subproperty {
    private Long id;
    private Long propertyId;
    private String name;
    private String value;

    public Subproperty() {
    }

    public Subproperty(ResultSet rs) throws SQLException {
        id = rs.getLong("spr.id");
        propertyId = rs.getLong("spr.property_id");
        name = rs.getString("spr.name");
        value = rs.getString("spr.value");
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }
}
