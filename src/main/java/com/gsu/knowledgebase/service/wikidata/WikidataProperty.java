package com.gsu.knowledgebase.service.wikidata;

/**
 * Created by cnytync on 26/04/2017.
 */
public enum WikidataProperty {
    INSTANCE_OF("P31"),
    SUBCLASS_OF("P279");

    private String value;

    private WikidataProperty(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
