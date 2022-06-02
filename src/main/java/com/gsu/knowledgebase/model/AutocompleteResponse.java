package com.gsu.knowledgebase.model;

/**
 * Created by cnytync on 09/02/2018.
 */
public class AutocompleteResponse {
    private Long id;
    private String value;

    public AutocompleteResponse(Long id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
