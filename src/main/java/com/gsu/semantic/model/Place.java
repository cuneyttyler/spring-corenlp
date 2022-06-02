package com.gsu.semantic.model;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Place implements Serializable {
    private Long id;
    private String name;
    private String image;
    private String smallImage;
    private String description;
    private String dbpediaUri;
    private String type;
    private String typeUri;
    private Double lat;
    private Double lon;
    private List<Property> properties = new ArrayList<>();

    public Place() {
    }

    public Place(ResultSet rs) throws SQLException {
        id = rs.getLong("pl.id");
        name = rs.getString("pl.name");
        image = rs.getString("pl.image");
        smallImage = rs.getString("pl.small_image");
        description = rs.getString("pl.description");
        dbpediaUri = rs.getString("pl.dbpedia_uri");
        type = rs.getString("pl.typ");
        typeUri = rs.getString("pl.typ_uri");
        lat = rs.getDouble("pl.lat");
        lon = rs.getDouble("pl.lon");
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDbpediaUri() {
        return dbpediaUri;
    }

    public void setDbpediaUri(String dbpediaUri) {
        this.dbpediaUri = dbpediaUri;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeUri() {
        return typeUri;
    }

    public void setTypeUri(String typeUri) {
        this.typeUri = typeUri;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSmallImage() {
        return smallImage;
    }

    public void setSmallImage(String smallImage) {
        this.smallImage = smallImage;
    }
}
