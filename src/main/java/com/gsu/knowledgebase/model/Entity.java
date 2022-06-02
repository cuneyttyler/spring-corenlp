package com.gsu.knowledgebase.model;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Entity implements Serializable {
    private Long id;
    private Long userEntityId;
    private String name;
    private String description;
    private String shortDescription;
    private String dbpediaUri;
    private String wikidataId;
    private String wikipediaUri;
    private String entityType;
    private String webUri;
    private String image;
    private String smallImage;
    private String secondaryImage;
    private String smallSecondaryImage;
    private String note;
    private String source;
    private String webPageText;
    private List<Property> properties = new ArrayList<>();
    private Long webPageEntityId;
    private List<Entity> annotationEntities = new ArrayList<>();
    private Long userId;
    private List<Topic> topics = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    private List<SubCategory> subCategories = new ArrayList<>();

    public Entity() {
    }

    public Entity(ResultSet rs) throws SQLException {
        id = rs.getLong("e.id");
        name = rs.getString("e.name");
        description = rs.getString("e.description");
        shortDescription = rs.getString("e.short_description");
        dbpediaUri = rs.getString("e.dbpedia_uri");
        wikidataId = rs.getString("e.wikidata_id");
        entityType = rs.getString("e.entity_type");
        webPageEntityId = rs.getLong("e.web_page_entity_id") == 0 ? null : rs.getLong("e.web_page_entity_id");
        webUri = rs.getString("e.web_uri");
        wikipediaUri = rs.getString("e.wikipedia_uri");
        image = rs.getString("e.image");
        smallImage = rs.getString("e.small_image");
        secondaryImage = rs.getString("e.secondary_image");
        smallSecondaryImage = rs.getString("e.small_secondary_image");
        source = rs.getString("e.source");
        webPageText = rs.getString("e.web_page_text");

        try {
            userEntityId = rs.getLong("ue.id");
            note = rs.getString("ue.note");
        } catch (SQLException e) {

        }
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

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public String getWikidataId() {
        return wikidataId;
    }

    public void setWikidataId(String wikidataId) {
        this.wikidataId = wikidataId;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getWebPageEntityId() {
        return webPageEntityId;
    }

    public void setWebPageEntityId(Long webPageEntityId) {
        this.webPageEntityId = webPageEntityId;
    }

    public List<Entity> getAnnotationEntities() {
        return annotationEntities;
    }

    public void setAnnotationEntities(List<Entity> annotationEntities) {
        this.annotationEntities = annotationEntities;
    }

    public String getWebUri() {
        return webUri;
    }

    public void setWebUri(String webUri) {
        this.webUri = webUri;
    }


    public String getWikipediaUri() {
        return wikipediaUri;
    }

    public void setWikipediaUri(String wikipediaUri) {
        this.wikipediaUri = wikipediaUri;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }


    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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


    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSecondaryImage() {
        return secondaryImage;
    }

    public void setSecondaryImage(String secondaryImage) {
        this.secondaryImage = secondaryImage;
    }

    public String getSmallSecondaryImage() {
        return smallSecondaryImage;
    }

    public void setSmallSecondaryImage(String smallSecondaryImage) {
        this.smallSecondaryImage = smallSecondaryImage;
    }

    public String getWebPageText() {
        return webPageText;
    }

    public void setWebPageText(String webPageText) {
        this.webPageText = webPageText;
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

    public List<SubCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<SubCategory> subCategories) {
        this.subCategories = subCategories;
    }

    public void addTopic(Topic t) {
        topics.add(t);
    }

    public void addCategory(Category c) {
        categories.add(c);
    }

    public void addSubCategory(SubCategory sc) {
        subCategories.add(sc);
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }
}