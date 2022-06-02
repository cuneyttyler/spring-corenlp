package com.gsu.semantic.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by cnytync on 22/03/2017.
 */
public class Property {
    private Long id;
    private Long placeId;
    private String name;
    private String lang;
    private String type;
    private String datatype;
    private String value;
    private String uri;

    public Property() {
    }

    public Property(ResultSet rs) throws SQLException {
        id = rs.getLong("pr.id");
        name = rs.getString("pr.name");
        lang = rs.getString("pr.lang");
        type = rs.getString("pr.typ");
        datatype = rs.getString("pr.datatype");
        value = rs.getString("pr.value");
        uri = rs.getString("pr.uri");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }
}
