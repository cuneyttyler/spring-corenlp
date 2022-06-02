package com.gsu.knowledgebase.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cnytync on 22/03/2017.
 */
public class Property {
    private Long id;
    private Long entityId;
    private String name;
    private String propertyType;
    private String source;
    private String description;
    private String lang;
    private String type;
    private String datatype;
    private String value;
    private String valueLabel;
    private String uri;
    private Integer visibility;
    private Long metaPropertyId;
    private MetaProperty metaProperty;
    private Entity customEntity;
    private Long customEntityId;
    private List<Subproperty> subproperties = new ArrayList<>();

    public Property() {
    }

    public Property(MetaProperty mp) {
        uri = mp.getUri();
        datatype = mp.getDatatype();
        description = mp.getDescription();
        name = mp.getName();
        propertyType = mp.getPropertyType();
        source = mp.getSource();
        visibility = mp.getVisibility();
        metaPropertyId = mp.getId();
    }


    public Property(ResultSet rs) throws SQLException {
        id = rs.getLong("pr.id");
        name = rs.getString("pr.name");
        datatype = rs.getString("pr.datatype");
        value = rs.getString("pr.value");
        valueLabel = rs.getString("pr.value_label");
        uri = rs.getString("pr.uri");
        entityId = rs.getLong("pr.entity_id");
        source = rs.getString("pr.source");
        description = rs.getString("pr.description");
        propertyType = rs.getString("pr.property_type");
        customEntityId = rs.getLong("pr.custom_entity_id");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Subproperty> getSubproperties() {
        return subproperties;
    }

    public void setSubproperties(List<Subproperty> subproperties) {
        this.subproperties = subproperties;
    }

    public String getValueLabel() {
        return valueLabel;
    }

    public void setValueLabel(String valueLabel) {
        this.valueLabel = valueLabel;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public Integer getVisibility() {
        return visibility;
    }

    public void setVisibility(Integer visibility) {
        this.visibility = visibility;
    }

    public Long getMetaPropertyId() {
        return metaPropertyId;
    }

    public void setMetaPropertyId(Long metaPropertyId) {
        this.metaPropertyId = metaPropertyId;
    }

    public MetaProperty getMetaProperty() {
        return metaProperty;
    }

    public void setMetaProperty(MetaProperty metaProperty) {
        this.metaProperty = metaProperty;
    }

    public Entity getCustomEntity() {
        return customEntity;
    }

    public void setCustomEntity(Entity customEntity) {
        this.customEntity = customEntity;
    }

    public Long getCustomEntityId() {
        return customEntityId;
    }

    public void setCustomEntityId(Long customEntityId) {
        this.customEntityId = customEntityId;
    }
}
